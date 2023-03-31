package hcmute.edu.vn.spotifyclone.model;

import java.util.HashSet;
import java.util.Set;

public class Song {
    public String songId;
    public String songName;
    public String singer;
    public int image;
    public int source;
    public String uploader;

    public Song(){

    }
    public Song(String songId, String songName, String singer) {
        this.songId = songId;
        this.songName = songName;
        this.singer = singer;
    }
    public Song(String songName, String singer, int image, int source, String uploader) {
        this.songName = songName;
        this.singer = singer;
        this.image = image;
        this.source = source;
        this.uploader = uploader;
    }

    public Song(String songId, String songName, String singer, int image, int source, String uploader) {
        this.songName = songName;
        this.singer = singer;
        this.image = image;
        this.source = source;
        this.uploader = uploader;
    }

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

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }
}
