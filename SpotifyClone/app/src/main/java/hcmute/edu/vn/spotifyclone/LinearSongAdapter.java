package hcmute.edu.vn.spotifyclone;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import hcmute.edu.vn.spotifyclone.model.Song;

public class LinearSongAdapter extends FirestoreRecyclerAdapter<Song, LinearSongAdapter.ViewHolder> {

    private Context context;
    private int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 23;
    Dialog dialog;


    public LinearSongAdapter(@NonNull FirestoreRecyclerOptions<Song> options, Context context) {
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
//        holder.song_item_view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                navigateSongScreen(song.getSongId());
//
//            }
//        });
        holder.song_singer.setText(song.getSinger());
        holder.download_song_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                downloadSong(song);

            }
        });

        holder.play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navigateSongScreen(song.getSongId());

            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.linear_song_item, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView song_image;
        public TextView song_name;
        public TextView song_singer;
        public LinearLayout song_item_view;
        public ImageView download_song_btn;
        public ImageView play_btn;

        public ViewHolder(View itemView) {
            super(itemView);
            song_image = itemView.findViewById(R.id.ln_song_image);
            song_name = itemView.findViewById(R.id.ln_song_name);
            song_singer = itemView.findViewById(R.id.ln_song_singer);
            download_song_btn = itemView.findViewById(R.id.download_song_btn);
            play_btn = itemView.findViewById(R.id.play_btn);
        }
    }

    public void downloadSong(Song song) {
        if (ContextCompat.checkSelfPermission((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if it has not been granted yet
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        } else {
            String externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName = externalStoragePath + File.separator + song.getSongName() + ".mp3";
            File localFile = new File(fileName);
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(song.getSource());

            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(context, "Download success!!!",
                            Toast.LENGTH_LONG).show();
                    openNoticeDialog("Download OK");
                    Log.d("file","download file " + fileName);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("file","download fail");
                }
            });
        }
        }

    void navigateSongScreen(String songId) {
        Intent intent = new Intent(context,MusicPlay_Activity.class);
        intent.putExtra("sondId",songId);
        context.startActivity(intent);
    }
    public void openNoticeDialog(String msg) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setCancelable(false)
                .setTitle("Notice")
                .setIcon(R.drawable.icon_in4)
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });

        dialog = builder.create();
        dialog.show();
    }
}
