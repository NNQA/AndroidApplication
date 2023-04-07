package hcmute.edu.vn.spotifyclone.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Song implements Serializable {
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
}
