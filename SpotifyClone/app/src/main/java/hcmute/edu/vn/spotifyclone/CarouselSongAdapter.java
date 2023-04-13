package hcmute.edu.vn.spotifyclone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import hcmute.edu.vn.spotifyclone.model.Song;

public class CarouselSongAdapter extends FirestoreRecyclerAdapter<Song, CarouselSongAdapter.ViewHolder> {

    private Context context;

    public CarouselSongAdapter(@NonNull FirestoreRecyclerOptions<Song> options, Context context) {
        super(options);

        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Song song) {

        InputStream inputStream = null;
        try {
            inputStream = new URL(song.getImage()).openStream();
        }catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        holder.song_image.setImageBitmap(bitmap);
        holder.song_name.setText(song.getSongName());
        holder.song_item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateSongScreen(song.getSongId());

            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView song_image;
        public TextView song_name;
        public LinearLayout song_item_view;

        public ViewHolder(View itemView) {
            super(itemView);
            song_image = itemView.findViewById(R.id.song_image);
            song_name = itemView.findViewById(R.id.song_name);
            song_item_view = itemView.findViewById(R.id.song_item_view);
        }
    }

    void navigateSongScreen(String songId) {
        Intent intent = new Intent(context,MusicPlay_Activity.class);
        intent.putExtra("sondId",songId);
        context.startActivity(intent);
    }
}
