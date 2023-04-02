package hcmute.edu.vn.spotifyclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;


import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import hcmute.edu.vn.spotifyclone.dataAccess.SongDAO;
import hcmute.edu.vn.spotifyclone.model.Song;

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

    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        SongDAO songDAO = new SongDAO();


        songDAO.getSong("3c3305e8-ba2c-49f2-bc63-3b9ad452f997", new SongDAO.SongCallback() {
            @Override
            public void onSongLoaded(Song song) {
                // Assign the song data to a variable outside the getSong() method.
                 mySongVariable  = song;
            }
            @Override
            public void onSongLoadFailed(Exception e) {
                // Handle the error here.
                Log.d("Song load error", e.getMessage());
            }
        });

       db.collection("usersA").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if(task.isSuccessful()){
                   for(QueryDocumentSnapshot document : task.getResult()){
                       Log.d("tag", document.getId() + " => " + document.getData());
                       Log.d("tag", document.getId() + " => " + document.getData());
                   }
               }else{
                   Log.w("TAG", "Error getting documents.", task.getException());
               }
           }
       });

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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //reload();
        }
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
}
