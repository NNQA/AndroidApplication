package hcmute.edu.vn.spotifyclone;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.spotifyclone.model.Playlist;
import hcmute.edu.vn.spotifyclone.model.Song;


public class Home extends Fragment {
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    CarouselSongAdapter adapter;

    TextView listTitle;


    public Home() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        listTitle = view.findViewById(R.id.list_title);
        recyclerView = view.findViewById(R.id.recycler_view);
//        setupAllSongRecycleView();
        new LoadDataAsyncTask().execute();
        return view;
    }

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, FirestoreRecyclerOptions<Song>> {

        @Override
        protected FirestoreRecyclerOptions<Song> doInBackground(Void... voids) {
            // Heavy work goes here, e.g. loading data from Firebase or network
            Query query = database.collection("songs").limit(20);
            FirestoreRecyclerOptions<Song> option = new FirestoreRecyclerOptions.Builder<Song>()
                    .setQuery(query,Song.class).build();
            return option;
        }

        @Override
        protected void onPostExecute(FirestoreRecyclerOptions<Song> option) {
            super.onPostExecute(option);
            // Update the UI on the main thread
            adapter = new CarouselSongAdapter(option,getContext());
            recyclerView.setAdapter(adapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            adapter.startListening();
        }
    }

    void setupAllSongRecycleView() {
         Query query = database.collection("songs").limit(20);

        FirestoreRecyclerOptions<Song> option = new FirestoreRecyclerOptions.Builder<Song>()
                .setQuery(query,Song.class).build();

        adapter = new CarouselSongAdapter(option,getContext());
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

    }



    @Override
    public void onStart() {
        super.onStart();
        if(adapter != null)
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter != null)
        adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter != null)
        adapter.notifyDataSetChanged();
    }
}

