package hcmute.edu.vn.spotifyclone.dataAccess;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import hcmute.edu.vn.spotifyclone.model.Song;


public class SongDAO {

    List<Song> listSong = new ArrayList<>();

    public interface SongCallback {
        void onSongLoaded(Song song);
        void onSongLoadFailed(Exception e);
    }
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    public void addSong(Song song) {
        String songId = UUID.randomUUID().toString();
        song.setSongId(songId);

        database.collection("songs").document(songId)
                .set(song)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Success!", "Document Added successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Failure", "Error when add document", e);
                    }
                });
    }


    public void getSong(String songId, final SongCallback callback) {



        database.collection("songs").document(songId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Nếu document tồn tại, chuyển đổi dữ liệu sang đối tượng Song.
                            Song song = documentSnapshot.toObject(Song.class);
                            callback.onSongLoaded(song);
                        } else {
                            Log.d("Not found", "Document does not exist");
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("failure", "Error getting document", e);

                    }
                });
    }


    public Song getRealSong(String songId){
        final Song[] thisSong = {new Song()};
        getSong(songId, new SongDAO.SongCallback() {
            @Override
            public void onSongLoaded(Song song) {
                // Assign the song data to a variable outside the getSong() method.
                thisSong[0] = song;
            }
            @Override
            public void onSongLoadFailed(Exception e) {
                // Handle the error here.
                Log.d("Song load error", e.getMessage());
            }
        });

        return thisSong[0];
    }

    public void updateSong(Song song) {

        String documentId = song.getSongId();

        database.collection("songs").document(documentId)
                .set(song)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Success!", "Document Updated successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Failure", "Error when update document", e);
                    }
                });
    }

    public void deleteSong(String songId) {

        database.collection("songs").document(songId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("success", "Document deleted successfully");
                })
                .addOnFailureListener(e -> {
                    Log.w("failure", "Error when delete document", e);
                });
    }

}
