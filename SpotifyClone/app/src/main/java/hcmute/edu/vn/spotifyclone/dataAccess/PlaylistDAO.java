package hcmute.edu.vn.spotifyclone.dataAccess;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

import hcmute.edu.vn.spotifyclone.model.Playlist;

public class PlaylistDAO {
    public interface PlaylistCallback {
        void onPlaylistLoaded(Playlist playlist);
        void onSongLoadFailed(Exception e);
    }
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    public void addPlaylist(Playlist playlist) {
        String id = UUID.randomUUID().toString();
        playlist.setPlaylistId(id);
        Log.d("aa", "addPlaylist: " );

        database.collection("playlist").document(id)
                .set(playlist)
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
    public void getPlaylist(String idPlaylist, final PlaylistDAO.PlaylistCallback callback) {
        database.collection("playlist").document(idPlaylist).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Nếu document tồn tại, chuyển đổi dữ liệu sang đối tượng Song.
                            Playlist playlist = documentSnapshot.toObject(Playlist.class);
                            callback.onPlaylistLoaded(playlist);
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

    public void updatePlaylist(Playlist playlist) {
        database.collection("playlist").document(playlist.getPlaylistId())
                .set(playlist)
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
    public void deletePlaylist(String idPlaylist) {
        database.collection("playlist").document(idPlaylist).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("success", "Document deleted successfully");
                })
                .addOnFailureListener(e -> {
                    Log.w("failure", "Error when delete document", e);
                });
    }

}
