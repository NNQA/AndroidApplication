package hcmute.edu.vn.spotifyclone;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.widget.Openable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import hcmute.edu.vn.spotifyclone.model.Song;


public class Profile extends Fragment {

    public TextView tvUsername;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    LinearSongAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment;
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tvUsername = view.findViewById(R.id.username);
        tvUsername.setText(getData("userName"));

        recyclerView = view.findViewById(R.id.ln_recycle_layout);
        setupSharedSongRecycleView();
        return view;

    }

    void setupSharedSongRecycleView(){
        String uid = getData("uid");
        Query query = database.collection("songs").whereEqualTo("uploader","7A6Opl4eUPZe79w5gTP9rI2UXys1");
        FirestoreRecyclerOptions<Song> options = new FirestoreRecyclerOptions.Builder<Song>()
                .setQuery(query, Song.class).build();
        adapter = new LinearSongAdapter(options,getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private String getData(String key){
        SharedPreferences sharedPreferences= getContext().getSharedPreferences("myRef", 0);
        if(sharedPreferences.contains(key)){
            String data = sharedPreferences.getString(key,null);
            return data;
        }
        else{
            return null;
        }
    }

}