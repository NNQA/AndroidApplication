package hcmute.edu.vn.spotifyclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.spotifyclone.dataAccess.Playlist_songDAO;
import hcmute.edu.vn.spotifyclone.model.Playlist;
import hcmute.edu.vn.spotifyclone.model.Song;
import hcmute.edu.vn.spotifyclone.service.SongService;

public class MusicPlay_Activity extends AppCompatActivity {

    //  Component
    MaterialButton btnPlay, btnMore, btnNext, btnPrev, btnMinimize;
    TextView songTitle, songDescription;
    ShapeableImageView songImg;
    Slider slider;
    //    dialog component
    MaterialButton cancelBtnDialog, okBtnDialog;
    TextInputLayout textInputLayout;
    AutoCompleteTextView autoCompleteTextView;
    Dialog dialog;

    //    Firebase component
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    //    Object and Attribute
    public String mySongId = "abc";
    public static Song recentSong;
    public String myPlayListId = "abc";
    public List<Song> recentPlaylist = new ArrayList<>();
    public boolean isPlaying = true;
    public boolean isServiceRunning = true;
    public boolean isPlaySingle = false;
    public int totalDuration = 1;
    public int currentProgress = 1;
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
        songTitle = findViewById(R.id.songTitle);
        songDescription = findViewById(R.id.songDescription);
        songImg = findViewById(R.id.songImage);
        slider = findViewById(R.id.songVolume);


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
                                if (task.isSuccessful()){
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot.isEmpty()) {
                                        dao.addSongToPlaylist(mySongId, PLID[0]);
                                        dialog.dismiss();
                                        openNoticeDialog("Add song successfully!!!");
                                    } else {
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
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                        if (task1.isSuccessful()) {
                                            List<DocumentSnapshot> tmpDocuments = task1.getResult().getDocuments();
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
        btnPlay.setIconResource(R.drawable.button_pause);
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

        startService(intent);
    }

    public void setProgressSong(int currentProgress, int totalDuration) {
        float a = (float) currentProgress;
        float b = (float) totalDuration;
        float percent = (a / b) * 100;
        slider.setValue(percent);
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
            Log.e("msg", "bundle is null");
            return;
        }
        myPlayListId = bundle.getString("PlaylistIDintent");
        mySongId = bundle.getString("sondId");

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
        } else {
            isPlaySingle = true;
            startPlayMusic(myPlayListId, mySongId);
        }

    }

    @Override
    public void onBackPressed() {
        MainActivity.status_player = true;
        MainActivity.recentSong = recentSong;
        finish();
        super.onBackPressed();
    }
}