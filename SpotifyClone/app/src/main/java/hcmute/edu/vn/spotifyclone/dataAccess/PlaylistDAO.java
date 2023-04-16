package hcmute.edu.vn.spotifyclone.dataAccess;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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


    public void addPlaylist(Playlist playlist, @Nullable Runnable onComplete, @Nullable Runnable onFailure) {
        String id = UUID.randomUUID().toString();
        database.collection("playlist").document(id)
                .set(playlist)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Success!", "Document Added successfully");
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Failure", "Error when add document", e);
                        if(onFailure != null) {
                            onFailure.run();
                        }
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
    public interface OnDeletePlaylistListener {
        void onDeleteSuccess();
        void onDeleteFailure(String errorMessage);

        void onDeletePlaylistSuccess();

        void onDeletePlaylistFailure();
    }
    public void deletePlaylist(String playlistId, Context context) {
        database.collection("playlist")
                .whereEqualTo("playlistId", playlistId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        database.collection("playlist").document(documentSnapshot.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("success", "Document deleted successfully");
                                    Toast.makeText(context, "Playlist deleted", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("failure", "Error when delete document", e);
                                    Toast.makeText(context, "Failed to delete playlist", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(context, "Playlist not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("failure", "Error when get document", e);
                    Toast.makeText(context, "Failed to get playlist", Toast.LENGTH_SHORT).show();
                });
    }
}
