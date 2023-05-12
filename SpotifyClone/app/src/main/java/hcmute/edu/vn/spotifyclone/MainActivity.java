package hcmute.edu.vn.spotifyclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import hcmute.edu.vn.spotifyclone.model.Song;
import hcmute.edu.vn.spotifyclone.service.SongService;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
//    Fragment Initiating
    public Home home_fragment = new Home();
    public Profile profile_fragment = new Profile();
    public PlayList playlist_fragment = new PlayList();
    public MusicPlay musicPlay_fragment = new MusicPlay();
    public Search search_fragment = new Search();
    private Song mySongVariable  = new Song();
    private  add_item_playlist add_item_playlist = new add_item_playlist();
    private ListPlayList listPlayList = new ListPlayList();

    FragmentManager fragmentManager = getSupportFragmentManager();
//    Component
    private TextView spSongTitle, spSinger;
    private MaterialButton spBtnPlay;
    private ShapeableImageView spImgSong;
    private RelativeLayout smallPlayer;
//    Variable
    public static boolean status_player = false;
    public static boolean isPlaying = true;
    public boolean isServiceRunning = true;
    public static Song recentSong;
    public String mySongId = "";
    private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
//    Broadcast

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle == null) {
                return;
            }

            isPlaying = bundle.getBoolean("status_player");
            int musicAction = bundle.getInt("action_music");
            recentSong = (Song) bundle.getSerializable("object_song");

            handleMusicAction(musicAction);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
//        String[] a = new String[0];
//        a[0] = "123";
//        Broadcast
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver, new IntentFilter("send_action_to_act"));

//        Init component
        spSongTitle = findViewById(R.id.smallPlayer_title);
        spSinger = findViewById(R.id.smallPlayer_singer);
        spBtnPlay = findViewById(R.id.smallPlayer_btnPlay);
        spImgSong = findViewById(R.id.smallPlayer_imgSong);
        smallPlayer = findViewById(R.id.smallPlayer);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy gfgPolicy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(gfgPolicy);
        }
        mAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = new BottomNavigationView(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                final int navigation_home = R.id.navigation_home;
                final int navigation_search = R.id.navigation_search;
                final int navigation_library = R.id.navigation_library;
                final int navigation_profile = R.id.navigation_profile;
                switch (item.getItemId()){
                    case navigation_home:
                        fragmentManager.beginTransaction().replace(R.id.main_fragment,home_fragment).commit();
                        return true;
                    case navigation_search:
                        fragmentManager.beginTransaction().replace(R.id.main_fragment,search_fragment).commit();
                        return true;
                    case navigation_library:
                        fragmentManager.beginTransaction().replace(R.id.main_fragment,listPlayList).commit();
//                        fragmentManager.beginTransaction().replace(R.id.main_fragment,add_item_playlist).commit();
                        return true;
                    case navigation_profile:
                        fragmentManager.beginTransaction().replace(R.id.main_fragment,profile_fragment).commit();
                        return true;
                }
                return false;
            }
        });


        fragmentManager.beginTransaction().replace(R.id.main_fragment,home_fragment).commit();

//        Componet onClick Event
        spBtnPlay.setOnClickListener(new View.OnClickListener() {
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
//                    isPlaying = true;
//                    openBigPlayer(recentSong.getSongId());
                }
            }
        });

        smallPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MusicPlay_Activity.class);
                getApplicationContext().startActivity(intent);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("abc", "s "+status_player);
        if(status_player == true) {
            smallPlayer.setVisibility(View.VISIBLE);
            setInfomation(recentSong);
            setStatusButtonPlay();
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //reload();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("Register","CreateUserWithEmai:Success");
                    FirebaseUser user = mAuth.getCurrentUser();
                }else{
                    Log.w("Register", "createUserWithEmail:failure", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Register Fail","CreateUserWithEmai:Success");
            }
        });
    }
    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }


//    Handle music play
    private void handleMusicAction(int action) {
        switch (action){
            case SongService.ACTION_PAUSE:
                setStatusButtonPlay();
                break;
            case SongService.ACTION_RESUME:
                setStatusButtonPlay();
                break;
            case SongService.ACTION_CLEAR:
                spBtnPlay.setIconResource(R.drawable.button_play);
                isServiceRunning = false;
                smallPlayer.setVisibility(View.GONE);
                break;
        }
    }

    public void setStatusButtonPlay(){
        if(isPlaying){
            spBtnPlay.setIconResource(R.drawable.button_pause);
        } else {
            spBtnPlay.setIconResource(R.drawable.button_play);
        }
    }

    void openBigPlayer(String songId) {
        Intent intent = new Intent(MainActivity.this,MusicPlay_Activity.class);
        startActivity(intent);
    }

    public void sendActToService(int action){
        Intent intent = new Intent(this, SongService.class);
        intent.putExtra("action_music_service", action);

        startService(intent);
    }

    public void setInfomation(Song song){
        spBtnPlay.setIconResource(R.drawable.button_pause);
        spSongTitle.setText(song.getSongName());
        spSinger.setText(song.getSinger());
        Glide.with(getApplicationContext()).load(recentSong.getImage()).into(spImgSong);
    }
}
