package hcmute.edu.vn.spotifyclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import hcmute.edu.vn.spotifyclone.dataAccess.SongDAO;
import hcmute.edu.vn.spotifyclone.model.Song;

public class UploadMusic extends AppCompatActivity {
    int SELECT_PICTURE = 200;
    int SELECT_SONG = 300;
    Uri selectedSongUri;
    Uri selectedImageUri;
    SongDAO songDAO = new SongDAO();

    MaterialToolbar topAppBar;
    EditText songName;
    EditText singer;
    EditText songUri;
    ImageView uploadSongImage;
    ImageView uploadSongUriBtn;
    Button uploadSongImageBtn;

    Button mainUpload;

    Song song = new Song();
    int a = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_upload_music);

        topAppBar = findViewById(R.id.topAppBar);
        songName = findViewById(R.id.et_song_name);
        singer = findViewById(R.id.et_singer);
        songUri = findViewById(R.id.et_song_uri);
        uploadSongImage = findViewById(R.id.song_image);
        uploadSongUriBtn = findViewById(R.id.upload_song_uri_btn);
        uploadSongImageBtn = findViewById(R.id.add_image_btn);
        mainUpload = findViewById(R.id.main_upload_song_btn);


        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        uploadSongUriBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songChoose();
            }
        });
        uploadSongImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChoose();
            }
        });

        mainUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadFileToDB(selectedImageUri,"image");
                song.setSongName(songName.getText().toString());
                song.setSinger(singer.getText().toString());
                song.setUploader(getData("uid"));
                song.setSource("");
                song.setImage("");
                songDAO.addSong(song, new SongDAO.SongIdCallback() {
                    @Override
                    public void onIdLoad(String id) {
                        song.setSongId(id);
                    }

                    @Override
                    public void onSongLoadFailed(Exception e) {
                        Log.e("Error","get id fail");
                    }
                });
                UploadFileToDB(selectedImageUri,"image");
                UploadFileToDB(selectedSongUri,"songUri");

            }
        });
    }


    void songChoose() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("audio/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select song"), SELECT_SONG);
    }

    void imageChoose() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select song"), SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_SONG) {
            // Get the url of the image from data
            selectedSongUri = data.getData();
            songUri.setText(selectedSongUri.getLastPathSegment());
        }
        else if(requestCode == SELECT_PICTURE){
            selectedImageUri = data.getData();
            uploadSongImage.setImageURI(selectedImageUri);
        }
    }

    void UploadFileToDB(Uri uri,String type) {
        if (null != uri) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference fileRef;
            if(type == "image")
                fileRef = storageRef.child("/SongImageFile/" + uri.getLastPathSegment());
            else{
                fileRef = storageRef.child("/Mp3File/" + uri.getLastPathSegment());
            }
            UploadTask uploadTask;
            uploadTask = fileRef.putFile(uri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String fileUrl = uri.toString();
                            String songId = song.getSongId();
                            if(type == "image") {
                                songDAO.updateOnlyField(songId,"image",fileUrl, getApplicationContext());
                            }
                            else {
                                songDAO.updateOnlyField(songId, "source", fileUrl, getApplicationContext());
                                Toast.makeText(getApplicationContext(),"Share Success",Toast.LENGTH_LONG).show();
                                onBackPressed();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Failure");
                }
            });
                // update the preview image in the layout

        }
    }


    private String getData(String key){
        SharedPreferences sharedPreferences=getSharedPreferences("myRef",0);
        if(sharedPreferences.contains(key)){
            String data = sharedPreferences.getString(key,null);
            return data;
        }
        else{
            return null;
        }
    }
}