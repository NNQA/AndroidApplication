package hcmute.edu.vn.spotifyclone;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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

        setupAllSongRecycleView();
        return view;
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