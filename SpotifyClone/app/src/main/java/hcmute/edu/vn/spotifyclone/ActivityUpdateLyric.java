package hcmute.edu.vn.spotifyclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;

import hcmute.edu.vn.spotifyclone.dataAccess.SongDAO;

public class ActivityUpdateLyric extends AppCompatActivity {
    EditText lyric;
    Button upload_btn;

    TextView lyric_label;

    MaterialToolbar topAppBar;

    SongDAO songDAO = new SongDAO();
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_update_lyric);

        topAppBar = findViewById(R.id.topAppBar);
        lyric = findViewById(R.id.et_lyric);
        lyric.setMovementMethod(new ScrollingMovementMethod());
        upload_btn = findViewById(R.id.main_upload_song_btn);
        lyric_label = findViewById(R.id.textView4);
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();
                String a =lyric.getText().toString().replaceAll("\n", "&n");
                String songId = bundle.getString("sondId");
                songDAO.updateOnlyField(songId,"lyric",a, getApplicationContext());
                onBackPressed();
            }
        });
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();
        String a = bundle.getString("songName") + "'s" + " Lyric";
        String song_lyric = bundle.getString("lyric");
        song_lyric = song_lyric.replaceAll("&n", "\n");
        lyric_label.setText(a);
        lyric.setText(song_lyric);
    }


}