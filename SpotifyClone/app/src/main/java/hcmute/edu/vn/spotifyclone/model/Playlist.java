package hcmute.edu.vn.spotifyclone.model;

import hcmute.edu.vn.spotifyclone.user;

public class Playlist {
    public Playlist(String playlistId, String playListName, String image) {
        this.playlistId = playlistId;
        this.playListName = playListName;
        this.image = image;
    }
    public Playlist(String playlistId, String playListName, String authid, String image) {
        this.playlistId = playlistId;
        this.playListName = playListName;
        this.authId = authid;
        this.image = image;
    }

    public String playlistId;
    public String playListName;
    public String   authId;
    public String  image;

    public Playlist() {

    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlayListName() {
        return playListName;
    }

    public void setPlayListName(String playListName) {
        this.playListName = playListName;
    }

    public String getAuth() {
        return authId;
    }

    public void setAuth(String authId) {
        this.authId = authId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
