package hcmute.edu.vn.spotifyclone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.spotifyclone.model.Song;

public class searchListAdapter extends RecyclerView.Adapter<searchListAdapter.ViewHolder> implements Filterable {

    private List<Song> mlsong;
    private List<Song> mlsongOld;
    private Context        context;


    public searchListAdapter(Context context,List<Song> mluser) {
        this.mlsong = mluser;
        this.mlsongOld = mluser;
        this.context = context;
    }
    public searchListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public searchListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_search, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull searchListAdapter.ViewHolder holder, int position) {
        Song song = mlsong.get(position);
        if(song == null) return;
        Glide.with(context).load(song.getImage()).into(holder.imageView);

        holder.txtName.setText(song.getSongName());
        holder.txtad.setText(song.getSinger());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateSongScreen(song.getSongId());
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mlsong != null) return mlsong.size();
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String stringSch = charSequence.toString();
                if(stringSch.isEmpty()) {
                    mlsong = mlsongOld;
                }
                else {
                    List<Song> list = new ArrayList<>();
                    for (Song song: mlsongOld) {
                        if(song.getSongName().toLowerCase().contains(stringSch.toLowerCase())) {
                            list.add(song);
                        }
                    }
                    mlsong = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mlsong;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mlsong = (List<Song>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView  txtName, txtad;
        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.itemShow);
            imageView = itemView.findViewById(R.id.imageSearch);
            txtName = itemView.findViewById(R.id.titleName);
            txtad = itemView.findViewById(R.id.desciption);
        }
    }
    void navigateSongScreen(String songId) {
        Intent intent = new Intent(context,MusicPlay_Activity.class);
        intent.putExtra("sondId",songId);
        context.startActivity(intent);
    }
}
