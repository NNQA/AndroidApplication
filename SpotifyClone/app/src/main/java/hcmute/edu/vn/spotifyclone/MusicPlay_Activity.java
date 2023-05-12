package hcmute.edu.vn.spotifyclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.mlkit.nl.languageid.IdentifiedLanguage;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hcmute.edu.vn.spotifyclone.dataAccess.Playlist_songDAO;
import hcmute.edu.vn.spotifyclone.model.Playlist;
import hcmute.edu.vn.spotifyclone.model.Song;
import hcmute.edu.vn.spotifyclone.service.SongService;

public class MusicPlay_Activity extends AppCompatActivity implements GestureDetector.OnGestureListener{

    //  Component
    MaterialButton btnPlay, btnMore, btnNext, btnPrev, btnMinimize;
    TextView songTitle, songDescription, songTime, recentLanguage, tvLyric;
    ShapeableImageView songImg;
    Slider slider;
    //    dialog component
    MaterialButton cancelBtnDialog, okBtnDialog;
    TextInputLayout textInputLayout;
    AutoCompleteTextView autoCompleteTextView;
    Dialog dialog;

    TextView lyric;
    LanguageIdentifier languageIdentifier;

    //    Firebase component
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    //    Object and Attribute
    public String mySongId = "abc";
    public static Song recentSong;
    public String myPlayListId = "abc";
    public List<Song> recentPlaylist = new ArrayList<>();
    public static boolean isPlaying = true;
    public static boolean isServiceRunning = true;
    public static boolean isPlaySingle = false;
    private int tempProgress = 1;
    public int totalDuration = 1;
    public int currentProgress = 1;
    private int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 23;
    private GestureDetector gestureDetector;
    private int originalLyricHeight, originalHeight;


    //
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }

            isPlaying = bundle.getBoolean("status_player");
            int musicAction = bundle.getInt("action_music");
            recentSong = (Song) bundle.getSerializable("object_song");

            handleMusicAction(musicAction);
        }
    };

    private BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }

            totalDuration = bundle.getInt("total_duration");
            currentProgress = bundle.getInt("current_progress");

            setProgressSong(currentProgress, totalDuration);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_music_play);


        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver, new IntentFilter("send_action_to_act"));
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver2, new IntentFilter("send_in4_to_act"));

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver2, new IntentFilter("send_in4_to_act"));

        btnMore = findViewById(R.id.btnMore);
        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrevious);
        btnMinimize = findViewById(R.id.btnMinimize);
        recentLanguage = findViewById(R.id.recentLanguage);
        songTitle = findViewById(R.id.songTitle);
        songDescription = findViewById(R.id.songDescription);
        songTime = findViewById(R.id.songTime);
        songImg = findViewById(R.id.songImage);
        slider = findViewById(R.id.songVolume);

        languageIdentifier = LanguageIdentification.getClient();;

        gestureDetector = new GestureDetector(getApplicationContext(),this);
        lyric = findViewById(R.id.lyric);

        lyric.setMovementMethod(new ScrollingMovementMethod());


        originalLyricHeight = lyric.getLayoutParams().height;
        originalHeight = songImg.getLayoutParams().height;
        PopupMenu popupMenu = new PopupMenu(this, btnMore);
        popupMenu.getMenuInflater().inflate(R.menu.music_play_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.addToPlaylistOption:
                        openChoosePLDialog();
                        return true;
                    case R.id.removeFromPlaylistOption:
                        openRemovePLDialog();
                        return true;
                    case R.id.downloadSongOption:
                        downloadSong(recentSong);
                        return true;
                    default:
                        return false;
                }

            }
        });

        slider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                String myLabel = convertToTime(currentProgress) + " / " + convertToTime(totalDuration);
                return myLabel;
            }
        });

        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                tempProgress = (int) value;
            }
        });
        slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                slider.setValue(tempProgress);
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                sendActToService(SongService.ACTION_SEND_INFO);
            }
        });

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isServiceRunning) {
                    if (isPlaying) {
                        sendActToService(SongService.ACTION_PAUSE);
                    } else {
                        sendActToService(SongService.ACTION_RESUME);
                    }
                } else {
                    isPlaying = true;
                    startPlayMusic(myPlayListId, mySongId);
                }

            }
        });

        btnMinimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnNextClick();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPrevClick();
            }
        });


    }

    private void openChoosePLDialog() {
        List<String> myIdList = new ArrayList<>();
        List<String> myNamelist = new ArrayList<>();

        SharedPreferences sharedPreferences = this.getApplicationContext().getSharedPreferences("myRef", 0);

        db.collection("playlist")
                .whereEqualTo("authId", sharedPreferences.getString("uid", null))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String IdList = documentSnapshot.getString("playlistId");
                            String nameList = documentSnapshot.getString("playListName");
                            myNamelist.add(nameList);
                            myIdList.add(IdList);
                        }
                        createPLDialog(myIdList, myNamelist);
                    }
                });
    }

    private void openRemovePLDialog() {
        if (isPlaySingle == true) {
            openNoticeDialog("This song is not played from a playlist!");
        } else {
            Playlist_songDAO dao = new Playlist_songDAO();
            dao.removeSongFromPlaylist(mySongId, myPlayListId);
            openNoticeDialog("Remove completed!");
        }
    }

    public void openNoticeDialog(String msg) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setCancelable(false)
                .setTitle("Notice")
                .setIcon(R.drawable.icon_in4)
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });

        dialog = builder.create();
        dialog.show();
    }

    public void createPLDialog(List<String> myIdList, List<String> myList) {

        final String[] PLID = new String[1];

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_choose_playlist, null);
        textInputLayout = dialogView.findViewById(R.id.menuChoose);
        autoCompleteTextView = dialogView.findViewById(R.id.drop_item);
        cancelBtnDialog = dialogView.findViewById(R.id.cancelBtnDialog);
        okBtnDialog = dialogView.findViewById(R.id.okBtnDialog);

        okBtnDialog.setBackgroundColor(Color.parseColor("#C0C0C0"));
        cancelBtnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        okBtnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Playlist_songDAO dao = new Playlist_songDAO();

                db.collection("playlist_song").whereEqualTo("playlistId", PLID[0])
                        .whereEqualTo("songId", mySongId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot.isEmpty()) {
                                        dao.addSongToPlaylist(mySongId, PLID[0]);
                                        dialog.dismiss();
                                        openNoticeDialog("Add song successfully!!!");
                                    } else {
                                        dialog.dismiss();
                                        openNoticeDialog("This song has been already exist in this playlist!");
                                    }
                                }
                            }
                        });
            }
        });

        ArrayAdapter<String> itemAdapter =
                new ArrayAdapter<>(MusicPlay_Activity.this, R.layout.item_list, myList);

        autoCompleteTextView.setAdapter(itemAdapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PLID[0] = myIdList.get(i);
                okBtnDialog.setEnabled(true);
                okBtnDialog.setBackgroundColor(Color.parseColor("#33FF33"));
            }
        });

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setCancelable(false)
                .setTitle("Choose Playlist")
                .setView(dialogView);

        dialog = builder.create();
        dialog.show();
    }

    public void openProgressDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.processing_dialog_layout, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setCancelable(false)
                .setTitle("Processing...")
                .setMessage("This may take a while...")
                .setView(dialogView);

        dialog = builder.create();
        dialog.show();
    }

    ;


    public void startPlayMusic(String mPlayListId, String mSongId) {

        db.collection("playlist_song").whereEqualTo("playlistId", mPlayListId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        List<String> tempSongId = new ArrayList<>();

                        QuerySnapshot myQuery = task.getResult();
                        if (myQuery.isEmpty()) {
                            tempSongId.add(mSongId);
                        } else {
                            for (QueryDocumentSnapshot document : myQuery) {
                                String tempId = document.getString("songId");
                                tempSongId.add(tempId);
                            }
                        }

                        db.collection("songs").whereIn("songId", tempSongId).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot querySnapshot) {
                                        List<DocumentSnapshot> tmpDocuments = querySnapshot.getDocuments();
                                        for (DocumentSnapshot abc : tmpDocuments) {
                                            Song sx = abc.toObject(Song.class);
                                            recentPlaylist.add(sx);
                                        }
                                        if (mSongId != null) {
                                            Log.e("er", "null");
                                            for (Song song : recentPlaylist) {
                                                if (song.getSongId().equals(mSongId)) {
                                                    recentSong = song;
                                                    break;
                                                }
                                            }
                                            setInformation(recentSong);
                                            startMyService(recentPlaylist, recentSong);
                                            isServiceRunning = true;

                                        } else {
                                            recentSong = recentPlaylist.get(0);
                                            setInformation(recentSong);
                                            startMyService(recentPlaylist, recentSong);
                                            isServiceRunning = true;
                                        }
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void startWhenMusicIsPlaying(String mSongId) {
        db.collection("songs").document(mSongId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Song song = documentSnapshot.toObject(Song.class);
                        setInformation(song);
                        isServiceRunning = true;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("failed", "error ");
                    }
                });
    }

    public void startMyService(List<Song> mlistsong, Song msong) {
        Intent intent = new Intent(this, SongService.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("object_list_song", new ArrayList<>(mlistsong));
        bundle.putSerializable("object_song", msong);
        intent.putExtras(bundle);

        startService(intent);
    }


    public void setInformation(Song song) {
        songTitle.setText(song.getSongName());
        songDescription.setText(song.getSinger());
        String imgUrl = song.getImage();
        Glide.with(getApplicationContext()).load(imgUrl).into(songImg);
        setStatusButtonPlay();
        setThisLanguage();
    }

    public void setStatusButtonPlay() {
        if (isPlaying) {
            btnPlay.setIconResource(R.drawable.button_pause);
        } else {
            btnPlay.setIconResource(R.drawable.button_play);
        }
    }

    private void handleMusicAction(int action) {
        switch (action) {
            case SongService.ACTION_PAUSE:
                setStatusButtonPlay();
                break;
            case SongService.ACTION_RESUME:
                setStatusButtonPlay();
                break;
            case SongService.ACTION_CLEAR:
                btnPlay.setIconResource(R.drawable.button_play);
                isServiceRunning = false;
                break;
            case SongService.ACTION_NEXT:
                setInformation(recentSong);
                break;
            case SongService.ACTION_PREVIOUS:
                setInformation(recentSong);
                break;
        }
    }

    public void sendActToService(int action) {
        Intent intent = new Intent(this, SongService.class);
        intent.putExtra("action_music_service", action);

        if (action == SongService.ACTION_SEND_INFO) {
            int changeProgress = (totalDuration * tempProgress) / 100;
            intent.putExtra("change_progress", changeProgress);
        }

        startService(intent);
    }

    public void setProgressSong(int currentProgress, int totalDuration) {
        float a = (float) currentProgress;
        float b = (float) totalDuration;
        float percent = (a / b) * 100;
        slider.setValue(percent);
        songTime.setText(convertToTime(currentProgress) + " / " + convertToTime(totalDuration));
    }

    public String convertToTime(int myTime) {
        int b = myTime / 1000;
        int x = b / 60;
        int y = b % 60;
        return (x + ":" + y);
    }

    public void btnNextClick() {
        sendActToService(SongService.ACTION_NEXT);
    }

    public void btnPrevClick() {
        sendActToService(SongService.ACTION_PREVIOUS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver2);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            isPlaying = MainActivity.isPlaying;
//            Log.e("msg", "bundle is null " + isPlaying);
            setStatusButtonPlay();
            setInformation(recentSong);
            return;
        }
        myPlayListId = bundle.getString("PlaylistIDintent");
        mySongId = bundle.getString("sondId");
        isPlaying = true;

//        if (mySongId != null) {
//            startPlayMusic(myPlayListId, mySongId);
//        } else {
//            mySongId = getIntent().getStringExtra("sondIdFromService");
//            mySongId = getIntent().getStringExtra("PlaylistIDintent");
//            startWhenMusicIsPlaying(mySongId);
//        }

        if (myPlayListId != null) {
            startPlayMusic(myPlayListId, mySongId);
            isPlaySingle = false;
        } else if (myPlayListId == null && mySongId != null) {
            isPlaySingle = true;
            startPlayMusic(myPlayListId, mySongId);
        } else {

        }

    }

    @Override
    public void onBackPressed() {
        MainActivity.status_player = true;
        MainActivity.recentSong = recentSong;
        MainActivity.isPlaying = isPlaying;
        finish();
        super.onBackPressed();
    }

    public void downloadSong(Song song) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if it has not been granted yet
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        } else {
            String externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName = externalStoragePath + File.separator + song.getSongName() + ".mp3";
            File localFile = new File(fileName);
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(song.getSource());

            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d("file", "download file " + fileName);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("file", "download fail");
                }
            });
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Pass the touch event to the GestureDetector to detect swipe gestures
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        float distanceY = e1.getY() - e2.getY();

        // If the distance is greater than a threshold, update the layout parameters of the ImageView
        if (Math.abs(distanceY) > 200) {
            ViewGroup.LayoutParams params = lyric.getLayoutParams();
            AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
            animation.setDuration(200);
            System.out.println(params.height);
            if (distanceY > 0) {
                // Swipe up - change the height of the ImageView to 0

                params.height = originalLyricHeight + 1000;
                // Set a listener to hide the ImageView after the animation finishes
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        songImg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });

                // Start the animation on the ImageView

                songImg.startAnimation(animation);

            } else {
                params.height = originalLyricHeight;
                // Swipe down - reset the height of the ImageView to its original value
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        songImg.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });

                // Start the animation on the ImageView

                songImg.startAnimation(animation);

            }

            lyric.setLayoutParams(params);
        }
        return true;
    }

    public void setThisLanguage(){
        String txt = recentSong.getLyric();
        txt = txt.replaceAll("&n", "\n");
        lyric.setText(txt);
        txt = txt.replaceAll("\n", " ");
        languageIdentifier.identifyLanguage(txt)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (s.equals("und")){
                            recentLanguage.setText("Language: Undefined");
                        } else {
                            Locale locale = new Locale(s);
                            String languageName = locale.getDisplayLanguage(locale);

                            recentLanguage.setText("Language: " + languageName);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        recentLanguage.setText("Language: Error!");
                    }
                });
    }
}