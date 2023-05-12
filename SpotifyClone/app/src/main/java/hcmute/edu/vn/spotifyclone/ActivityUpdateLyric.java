package hcmute.edu.vn.spotifyclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ActivityUpdateLyric extends AppCompatActivity {
    EditText lyric;
    Button upload_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_lyric);

        lyric = findViewById(R.id.et_lyric);
        upload_btn = findViewById(R.id.main_upload_song_btn);

        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a =lyric.getText().toString().replaceAll("\n", "&n");
                System.out.println(a);
            }
        });
    }
}