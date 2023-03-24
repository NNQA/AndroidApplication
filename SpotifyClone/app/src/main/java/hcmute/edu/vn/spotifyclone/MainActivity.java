package hcmute.edu.vn.spotifyclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;


import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

//    Fragment Initiating
    public Home home_fragment = new Home();
    public Profile profile_fragment = new Profile();
    public PlayList playlist_fragment = new PlayList();
    public MusicPlay musicPlay_fragment = new MusicPlay();
    public Search search_fragment = new Search();

    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

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
                        fragmentManager.beginTransaction().replace(R.id.main_fragment,playlist_fragment).commit();
                        return true;
                    case navigation_profile:
                        fragmentManager.beginTransaction().replace(R.id.main_fragment,profile_fragment).commit();
                        return true;
                }
                return false;
            }
        });

        fragmentManager.beginTransaction().replace(R.id.main_fragment,home_fragment).commit();
    }
}