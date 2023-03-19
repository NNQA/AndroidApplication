package hcmute.edu.vn.spotifyclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFragmentSearch();
        setFragmentPlayList();

    }

    private void setFragmentPlayList() {
        BottomNavigationView menu = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationItemView item = menu.findViewById(R.id.item_3);

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.frameLayout, new PlayList(), null)
                        .commit();
            }
        });
    }

    private void setFragmentSearch() {
        BottomNavigationView menu = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationItemView item = menu.findViewById(R.id.item_2);

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.frameLayout, new Search(), null)
                        .commit();
            }
        });
    }
}