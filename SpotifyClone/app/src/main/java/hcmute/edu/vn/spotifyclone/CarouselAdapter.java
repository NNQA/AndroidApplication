package hcmute.edu.vn.spotifyclone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hcmute.edu.vn.spotifyclone.model.Album;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ViewHolder> {
    private List<Album> albumList;
    private Context context;

    public CarouselAdapter(List<Album> albumList, Context context) {
        this.albumList = albumList;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album album = albumList.get(position);
        holder.album_title.setText(album.getAlbumTitle());
        holder.album_image.setImageResource(album.getAlbumImage());
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView album_image;
        public TextView album_title;

        public ViewHolder(View itemView) {
            super(itemView);
            album_image = itemView.findViewById(R.id.album_image);
            album_title = itemView.findViewById(R.id.album_title);
        }
    }
}
