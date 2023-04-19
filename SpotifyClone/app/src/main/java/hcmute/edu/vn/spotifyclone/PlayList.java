package hcmute.edu.vn.spotifyclone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hcmute.edu.vn.spotifyclone.model.Song;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayList extends Fragment {
    private RecyclerView recyclerView;
    private searchListAdapter searchListAdapter;
    LinearLayoutManager linearLayout;
    View view;
    FragmentManager fragmentManager;
    String playlistId;
    TextView textView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlayList() {
        // Required empty public constructor
    }

    public PlayList(String s, String datSadNess, Object o, int i) {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayList.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayList newInstance(String param1, String param2) {
        PlayList fragment = new PlayList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private Context activity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_play_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerviewtimkiem);
        textView = view.findViewById(R.id.namePlayList);
        TextView name = view.findViewById(R.id.nameUser);
        linearLayout = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayout);
        playlistId = getArguments().getString("playlistId");
        String playlistName = getArguments().getString("playlistName");
        TextView button = view.findViewById(R.id.back);
        textView.setText(playlistName);
        Log.d("TAG", "Error getting playlists: " + playlistId);
        List<Song> songList = new ArrayList<>();
        Context context = this.getActivity();
        SharedPreferences sharedPreferences=this.getContext().getSharedPreferences("myRef",0);
        name.setText(sharedPreferences.getString("userName",null));
        FindListSongs(new OnSuccessListener<List<DocumentSnapshot>>() {
            @Override
            public void onSuccess(List<DocumentSnapshot> songDocuments) {
                for (DocumentSnapshot songDocument : songDocuments) {
                    Song song = songDocument.toObject(Song.class);
                    songList.add(song);
                }
                songList.forEach((e) -> {
                    Log.d("asd", "onSuccess: " + e.getSongName());
                });
                if (songList != null) {
                    searchListAdapter = new searchListAdapter(context, songList, playlistId);
                    searchListAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(searchListAdapter);
                }
                searchListAdapter.notifyDataSetChanged();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("PlaylistFragment", "Error getting song documents", e);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment listPlayList = new ListPlayList();
                fragmentTransaction.replace(R.id.playlist, listPlayList);
                fragmentTransaction.commit();
            }
        });

        ShapeableImageView play = view.findViewById(R.id.iconPlay);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MusicPlay_Activity.class);
                playlistId = getArguments().getString("playlistId");
                intent.putExtra("PlaylistIDintent", playlistId);
                context.startActivity(intent);
            }
        });


        return view;
    }



    private void FindListSongs(OnSuccessListener<List<DocumentSnapshot>> onSuccessListener, OnFailureListener onFailureListener) {
        final FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("playlist_song")
                .whereEqualTo("playlistId", playlistId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        List<String> songIds = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : snapshotList) {
                            Log.d("asd", "onSuccess: " + documentSnapshot.getString("songId"));
                            songIds.add(documentSnapshot.getString("songId"));
                        }
                        if (!songIds.isEmpty()) { // check if songIds list is not empty
                            database.collection("songs")
                                    .whereIn("songId", songIds)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            onSuccessListener.onSuccess(queryDocumentSnapshots.getDocuments());
                                            Log.d("asd", "Number of songs retrieved: " + queryDocumentSnapshots.getDocuments().size());
                                        }
                                    })
                                    .addOnFailureListener(onFailureListener);
                        } else {
                            onSuccessListener.onSuccess(Collections.emptyList()); // return empty list if songIds list is empty
                        }
                    }
                })
                .addOnFailureListener(onFailureListener);
    }
}