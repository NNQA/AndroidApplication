package hcmute.edu.vn.spotifyclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.spotifyclone.model.Song;
import hcmute.edu.vn.spotifyclone.service.SongService;

public class MusicPlay_Activity extends AppCompatActivity {

    //  Component
    MaterialButton btnPlay, btnMore, btnNext, btnPrev, btnMinimize;
    TextView songTitle, songDescription;
    ShapeableImageView songImg;
    Slider slider;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    //    Object and Attribute
    public String mySongId = "abc";
    public static Song recentSong;
    public List<Song> recentPlaylist = new ArrayList<>();
    public int indexSong =0;
    public boolean isPlaying = true;
    public boolean isServiceRunning = true;
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

//        ArrayList<Song> songList = (ArrayList<Song>) getIntent().getSerializableExtra("songList");
//        if(songList != null) {
//            songList.forEach((e) -> {
//                Log.d("asd", "onSuccess: " + e.getSongName());
//            });
//        } else {
//            Log.d("asdasd", "onCreate: asdasd");
//        }

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
                // Handle menu item click event here
                return true;
            }
        });

        slider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                String myLabel = convertToTime(currentProgress)+" / "+convertToTime(totalDuration);
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
                    startPlayMusic(mySongId);
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


    public void startPlayMusic(String mSongId) {
//        db.collection("songs").document(mSongId)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        Song song = documentSnapshot.toObject(Song.class);
//                        recentSong = song;
//                        setInformation(song);
//                        startService(song);
//                        isServiceRunning = true;
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("failed", "error ");
//                    }
//                });

        db.collection("songs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Song song = document.toObject(Song.class);
                                recentPlaylist.add(song);
                            }
                            recentSong = recentPlaylist.get(indexSong);
                            setInformation(recentSong);
                            startService(recentSong);
                            isServiceRunning = true;
                        }
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

    public void startService(Song msong) {
        Intent intent = new Intent(this, SongService.class);
        Bundle bundle = new Bundle();
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
                Log.e("abc", "next");
                btnNextClick();
                break;
            case SongService.ACTION_PREVIOUS:
                Log.e("abc", "prev");
                btnPrevClick();
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
        int b = myTime/1000;
        int x =b/60;
        int y = b%60;
        return (x+":"+y);
    }

    public void btnNextClick(){
        if (indexSong+1 < recentPlaylist.size() ){
            indexSong++;
            recentSong = recentPlaylist.get(indexSong);
            setInformation(recentSong);
            startService(recentSong);
        }
    }

    public void btnPrevClick(){
        if (indexSong-1 >= 0 ){
            indexSong--;
            recentSong = recentPlaylist.get(indexSong);
            setInformation(recentSong);
            startService(recentSong);
        }
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
//        mySongId = "1q4TGECGjQliuz1q8K4f";
        mySongId = getIntent().getStringExtra("sondId");

        if (mySongId != null) {
            startPlayMusic(mySongId);
        } else {
//            mySongId = getIntent().getStringExtra("sondIdFromService");
//            mySongId = getIntent().getStringExtra("PlaylistIDintent");
//            startWhenMusicIsPlaying(mySongId);
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