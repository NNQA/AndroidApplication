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

import hcmute.edu.vn.spotifyclone.model.Album;


public class Home extends Fragment {
    private List<Album> albumList;

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
        albumList = new ArrayList<>();
        albumList.add(new Album("Album 1", R.drawable.ic_android_blue));
        albumList.add(new Album("Album 2", R.drawable.ic_android_blue));
        albumList.add(new Album("Album 3", R.drawable.ic_android_blue));
        albumList.add(new Album("Album 4", R.drawable.ic_android_blue));
        albumList.add(new Album("Album 5", R.drawable.ic_android_blue));
        albumList.add(new Album("Album 6", R.drawable.ic_android_blue));

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        CarouselAdapter adapter = new CarouselAdapter(albumList, getContext());
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }
}