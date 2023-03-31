package hcmute.edu.vn.spotifyclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.UUID;

import hcmute.edu.vn.spotifyclone.dataAccess.SongDAO;
import hcmute.edu.vn.spotifyclone.model.Song;

public class MusicPlay_Activity extends AppCompatActivity {

//    Testing CRUD
    Button btnPlay;

    SongDAO songDAO;

//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        songDAO = new SongDAO();

        Button btnMore = findViewById(R.id.btnMore);
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

//        Testing sites
        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Song song = new Song("Trac Loi", "son tung", 123, 123, "ty");
                songDAO.addSong(song);
                Toast.makeText(getApplicationContext(),"OK OK",Toast.LENGTH_LONG).show();

//                Song song = new Song();
//                song = songDAO.getSong("42c6c913-3ae0-4fb9-8c05-a8b5edea9de6");
//                song.setSongName("fck uoi");
//                songDAO.updateSong(song);

            }
        });
    }
}