package hcmute.edu.vn.spotifyclone.dataAccess;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Playlist_songDAO {
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    public void addSongToPlaylist(String songId, String playlistId){
        Map<String, Object> doc = new HashMap<>();
        doc.put("playlistId", playlistId);
        doc.put("songId", songId);

//        database.collection("playlist_song")
//                .whereEqualTo("songId", songId)
//                .whereEqualTo("playlistId", playlistId)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()) {
//                            if(task.getResult().isEmpty()){
//                                //Not exist, add
//
//
//
//                            } else {
//                                // Tài liệu đã tồn tại, không thêm mới
//                                Log.e("Firestore", "Document already exists!");
//                            }
//                        } else {
//                            Log.e("Firestore", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });

        database.collection("playlist_song")
                .add(doc)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Log.e("Success", "Add song succeed");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Failure", "Add song failed");
                    }
                });

    }

    public void removeSongFromPlaylist(String songId, String playlistId){

        Query query = database.collection("playlist_song")
                .whereEqualTo("songId", songId)
                .whereEqualTo("playlistId", playlistId);


        query.get().addOnSuccessListener(querySnapshot -> {
            for (QueryDocumentSnapshot document : querySnapshot) {
                // Xoá từng tài liệu tương ứng
                database.collection("playlist_song").document(document.getId()).delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.e("Success", "Remove song succeed");
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Failure", "Remove song failed");
                        });
            }
        }).addOnFailureListener(e -> {
            Log.e("Error", "An error occur when get document");
        });
    }
}
