package hcmute.edu.vn.spotifyclone;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import hcmute.edu.vn.spotifyclone.model.Song;


public class Profile extends Fragment {
    FirebaseStorage storage;
    public TextView tvUsername;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    LinearSongAdapter adapter;
    ImageView add_song_btn;
    int SELECT_PICTURE = 200;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment;
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tvUsername = view.findViewById(R.id.username);
        tvUsername.setText(getData("userName"));

        recyclerView = view.findViewById(R.id.ln_recycle_layout);
        add_song_btn = view.findViewById(R.id.add_song_btn);

        add_song_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChangeScreen();
            }
        });

        setupSharedSongRecycleView();
        return view;

    }

    public void ChangeScreen() {
        Intent intent = new Intent(getContext(),UploadMusic.class);
        startActivity(intent);
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
    public void downloadSong(Song song) {
        String fileName = song.getSongName() + ".mp3";

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(song.getSource());
        File localFile = new File(fileName);
        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
              Log.d("file","download in" + fileName);
            }
        });
    }

}