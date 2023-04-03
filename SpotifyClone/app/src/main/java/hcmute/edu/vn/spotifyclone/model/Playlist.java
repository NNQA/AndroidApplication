package hcmute.edu.vn.spotifyclone.model;

import hcmute.edu.vn.spotifyclone.user;

public class Playlist {
    public Playlist(String playlistId, String playListName, user auth, int image) {
        this.playlistId = playlistId;
        this.playListName = playListName;
        this.auth = auth;
        this.image = image;
    }

    public String playlistId;
    public String playListName;
    public user   auth;
    public int    image;

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

    public user getAuth() {
        return auth;
    }

    public void setAuth(user auth) {
        this.auth = auth;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
