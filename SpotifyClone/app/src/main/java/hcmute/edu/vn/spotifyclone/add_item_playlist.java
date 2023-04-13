package hcmute.edu.vn.spotifyclone;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import hcmute.edu.vn.spotifyclone.dataAccess.PlaylistDAO;
import hcmute.edu.vn.spotifyclone.model.Playlist;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link add_item_playlist#newInstance} factory method to
 * create an instance of this fragment.
 */
public class add_item_playlist extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View              view;
    TextView          textView;
    EditText editText;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public add_item_playlist() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment add_item_playlist.
     */
    // TODO: Rename and change types and number of parameters
    public static add_item_playlist newInstance(String param1, String param2) {
        add_item_playlist fragment = new add_item_playlist();
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

    public String RamdonStringImage() {
        List<String> list = new ArrayList<>();
        Random generator = new Random();
        list.add("https://firebasestorage.googleapis.com/v0/b/mobileappmusicplay.appspot.com/o/playlistImage%2F310102279_3349843245286522_5522434819865074773_n.jpg?alt=media&token=5f99dbae-7f2b-47a3-bd19-508468a191b4");
        list.add("https://firebasestorage.googleapis.com/v0/b/mobileappmusicplay.appspot.com/o/playlistImage%2F315945895_794078351692871_5767577240750830975_n.jpg?alt=media&token=6f406cc4-6075-4fe1-9fc8-beb65cd5101b");
        list.add("https://firebasestorage.googleapis.com/v0/b/mobileappmusicplay.appspot.com/o/playlistImage%2F5299129.jpg?alt=media&token=8bc2c441-a6f3-4a56-b080-bfb2e874bae3");
        list.add("https://firebasestorage.googleapis.com/v0/b/mobileappmusicplay.appspot.com/o/playlistImage%2FmusicFavorite.jpg?alt=media&token=48cc0701-09e7-4662-b085-3d23160950c2");
        list.add("https://firebasestorage.googleapis.com/v0/b/mobileappmusicplay.appspot.com/o/playlistImage%2FcmMusic.jpg?alt=media&token=f4a5dcf9-331c-4208-9b89-e76fec520ad6");
        int ramdonindex = (generator.nextInt(list.size()));
     return list.get(ramdonindex) ;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_add_item_playlist, container, false);
        textView = view.findViewById(R.id.addPlaylist);
        editText = view.findViewById(R.id.input);
        SharedPreferences sharedPreferences=this.getContext().getSharedPreferences("myRef",0);
        PlaylistDAO playlistDAO = new PlaylistDAO();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("asd", "onClick: " + sharedPreferences.getString("uid",null) +  " " + editText.getText().toString());
                String id = UUID.randomUUID().toString();
                Playlist playlist = new Playlist(id, editText.getText().toString(), sharedPreferences.getString("uid",null),RamdonStringImage() );
                playlistDAO.addPlaylist(playlist, () -> {
                    Toast.makeText(add_item_playlist.this.getContext(), "Added successfully", Toast.LENGTH_SHORT).show();
                    ListPlayList list = new ListPlayList();
                    FragmentManager fragmentManager = getChildFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.itemAddList, list);
                    fragmentTransaction.commit();
                }, () -> {
                    Toast.makeText(add_item_playlist.this.getContext(), "Added failure", Toast.LENGTH_SHORT).show();
                });

            }
        });
        return view;
    }
}