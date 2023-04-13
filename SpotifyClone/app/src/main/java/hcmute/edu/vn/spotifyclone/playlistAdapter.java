package hcmute.edu.vn.spotifyclone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.spotifyclone.model.Playlist;
import hcmute.edu.vn.spotifyclone.model.Song;

public class playlistAdapter extends RecyclerView.Adapter<playlistAdapter.ViewHolder> implements Filterable {
    private List<Playlist> mlplaylist;
    private List<Playlist>   mlplaylistOld;
    private  Context    context;

    public playlistAdapter(Context context,List<Playlist> mluser) {
        this.mlplaylist = mluser;
        this.mlplaylistOld = mluser;
        this.context = context;
    }
    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String stringSch = charSequence.toString();
                if(stringSch.isEmpty()) {
                    mlplaylist = mlplaylistOld;
                }
                else {
                    List<Playlist> list = new ArrayList<>();
                    for (Playlist playlist: mlplaylistOld) {
                        if(playlist.getPlayListName().toLowerCase().contains(stringSch.toLowerCase())) {
                            list.add(playlist);
                        }
                    }
                    mlplaylist = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mlplaylist;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mlplaylist = (List<Playlist>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public playlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent,false);

        return new playlistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull playlistAdapter.ViewHolder holder, int position) {

        Playlist playlist = mlplaylist.get(position);
        Log.d("Asdsad", "onBindViewHolder: " +playlist.toString());
        if(playlist == null) return;
        Glide.with(context).load(playlist.getImage()).into(holder.imageView);
        holder.txtName.setText(playlist.getPlayListName());
        holder.txtad.setText("a");
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayList playListFr = new PlayList();
                Bundle args = new Bundle();
                args.putString("playlistId", playlist.getPlaylistId());
                args.putString("playlistName", playlist.getPlayListName());
                playListFr.setArguments(args);
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.listPlayList, playListFr);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mlplaylist != null) return mlplaylist.size();
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtName, txtad;
        RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.itemShow);
            imageView = itemView.findViewById(R.id.imageSearch);
            txtName = itemView.findViewById(R.id.titleName);
            txtad = itemView.findViewById(R.id.desciption);
        }
    }
    void navigateSongScreen(String playlistId) {
        Intent intent = new Intent(context,Playlist.class);
        intent.putExtra("playlistId",playlistId);
        context.startActivity(intent);
    }
}
