package hcmute.edu.vn.spotifyclone;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.utils.widget.ImageFilterButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.spotifyclone.model.Playlist;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListPlayList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListPlayList extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private playlistAdapter playlistAdapter;
    LinearLayoutManager linearLayout;
    ImageFilterButton imageFilterButton;
    View           view;
    FragmentManager fragmentManager;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    SearchView searchView;


    public ListPlayList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListPlayList.
     */
    // TODO: Rename and change types and number of parameters
    public static ListPlayList newInstance(String param1, String param2) {
        ListPlayList fragment = new ListPlayList();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_list_play_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerviewtimkiem);
        linearLayout = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayout);
        final List<Playlist> playlists = new ArrayList<>();
        imageFilterButton = view.findViewById(R.id.addPlaylist);
        imageFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_item_playlist add = new add_item_playlist();
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.listPlayList, add);
                fragmentTransaction.commit();


            }
        });
        searchView = view.findViewById(R.id.SearchView);

        getListUsers(new OnSuccessListener<List<Playlist>>() {
            @Override
            public void onSuccess(List<Playlist> list) {
                playlists.addAll(list);
                playlistAdapter.notifyDataSetChanged();

            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure case
                Log.d("TAG", "Error getting playlists: " + e.getMessage());
            }
        });

        playlistAdapter = new playlistAdapter(this.getActivity(),playlists);
        recyclerView.setAdapter(playlistAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                playlistAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                playlistAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return view;
    }
    private void getListUsers(OnSuccessListener<List<Playlist>> onSuccessListener, OnFailureListener onFailureListener) {
        SharedPreferences sharedPreferences=this.getContext().getSharedPreferences("myRef",0);
        database.collection("playlist")
                .whereEqualTo("authId",sharedPreferences.getString("uid",null))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Playlist> list = new ArrayList<>();
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : snapshotList) {
                            Playlist playlists = new Playlist(documentSnapshot.getString("playlistId"),
                                    documentSnapshot.getString("playListName"),
                                    documentSnapshot.getString("image"));

                            list.add(playlists);
                        }
                        onSuccessListener.onSuccess(list);
                    }
                })
                .addOnFailureListener(onFailureListener);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("adad","onstart");
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}