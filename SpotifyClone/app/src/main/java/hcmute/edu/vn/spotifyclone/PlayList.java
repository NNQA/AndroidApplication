package hcmute.edu.vn.spotifyclone;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_play_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerviewtimkiem);
        linearLayout = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayout);

        searchListAdapter = new searchListAdapter(getListUsers());
        recyclerView.setAdapter(searchListAdapter);

        return view;
    }

    private List<user> getListUsers() {
        List<user> list = new ArrayList<>();
        list.add(new user(R.drawable.headphone1,"hay trao cho anh","son tung"));
        list.add(new user(R.drawable.headphone2,"em cua ngay hom qua","son tung"));
        list.add(new user(R.drawable.headphone3,"anh sai roi","son tung"));
        list.add(new user(R.drawable.headphone4,"chay ngay di","son tung"));
        list.add(new user(R.drawable.headphone1,"con mua ngang qua","son tung"));
        list.add(new user(R.drawable.headphone2,"con mua ngang qua 2","son tung"));
        list.add(new user(R.drawable.headphone3,"con mua ngang qua 3","son tung"));
        list.add(new user(R.drawable.headphone4,"co chac yeu la day","son tung"));
        list.add(new user(R.drawable.headphone1,"chung ta cua hien tai","son tung"));
        return list;
    }
}