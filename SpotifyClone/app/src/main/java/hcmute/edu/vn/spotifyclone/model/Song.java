package hcmute.edu.vn.spotifyclone.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Song implements Serializable, Parcelable {
    public String songId;
    public String songName;
    public String singer;
    public String image;
    public String source;
    public String uploader;


    public Song() {

    }

    public Song(String songId, String songName, String singer, String image, String source, String uploader) {
        this.songId = songId;
        this.songName = songName;
        this.singer = singer;
        this.image = image;
        this.source = source;
        this.uploader = uploader;
    }

    protected Song(Parcel in) {
        songId = in.readString();
        songName = in.readString();
        singer = in.readString();
        image = in.readString();
        source = in.readString();
        uploader = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(songId);
        parcel.writeString(songName);
        parcel.writeString(singer);
        parcel.writeString(image);
        parcel.writeString(source);
        parcel.writeString(uploader);
    }
}
