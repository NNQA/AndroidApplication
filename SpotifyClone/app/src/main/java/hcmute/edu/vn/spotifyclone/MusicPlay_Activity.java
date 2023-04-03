package hcmute.edu.vn.spotifyclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import hcmute.edu.vn.spotifyclone.dataAccess.PlaylistDAO;
import hcmute.edu.vn.spotifyclone.dataAccess.SongDAO;
import hcmute.edu.vn.spotifyclone.model.Playlist;

public class MusicPlay_Activity extends AppCompatActivity {

//    Testing CRUD
    Button btnPlay;

    SongDAO     songDAO;
    PlaylistDAO playlistDAO;



//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        songDAO = new SongDAO();
        playlistDAO = new PlaylistDAO();

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
                Playlist playlist = new Playlist("", "ads", null,0);

                playlistDAO.addPlaylist(playlist);
                Toast.makeText(getApplicationContext(),"OK OK",Toast.LENGTH_LONG).show();



            }
        });
    }
}