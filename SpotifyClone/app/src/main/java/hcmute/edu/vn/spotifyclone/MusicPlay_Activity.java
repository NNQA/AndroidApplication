package hcmute.edu.vn.spotifyclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    public boolean isPlaying = true;
    public boolean isServiceRunning = true;
    public int totalDuration;
//
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle == null) {
                return;
            }

            isPlaying = bundle.getBoolean("status_player");
            int musicAction = bundle.getInt("action_music");

            handleMusicAction(musicAction);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver, new IntentFilter("send_action_to_act"));

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

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isServiceRunning){
                    Log.e("Mess", "is "+isPlaying);
                    if (isPlaying){
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

    }


    public void startPlayMusic(String mSongId){

        db.collection("songs").document(mSongId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Song song = documentSnapshot.toObject(Song.class);
                        setInformation(song);
                        startService(song);
                        isServiceRunning = true;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("failed", "error ");
                    }
                });
    }

    public void startService(Song msong){
        Intent intent = new Intent(this, SongService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song", msong);
        intent.putExtras(bundle);

        startService(intent);
//        onBackPressed();
    }


    public void setInformation(Song song) {
        songTitle.setText(song.getSongName());
        songDescription.setText(song.getSinger());
        String imgUrl = song.getImage();
        Glide.with(getApplicationContext()).load(imgUrl).into(songImg);
        btnPlay.setIconResource(R.drawable.button_pause);
    }

    public void setStatusButtonPlay(){
        if(isPlaying){
            btnPlay.setIconResource(R.drawable.button_pause);
        } else {
            btnPlay.setIconResource(R.drawable.button_play);
        }
    }
    private void handleMusicAction(int action) {
        switch (action){
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
        }
    }

    public void sendActToService(int action){
        Intent intent = new Intent(this, SongService.class);
        intent.putExtra("action_music_service", action);

        startService(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Get intent Start play music here, assign mySongId = songId get from Intent
//        1q4TGECGjQliuz1q8K4f
        mySongId = getIntent().getStringExtra("sondId");
//        mySongId = "1q4TGECGjQliuz1q8K4f";
        startPlayMusic(mySongId);
        btnPrev.setEnabled(false);
        btnNext.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}