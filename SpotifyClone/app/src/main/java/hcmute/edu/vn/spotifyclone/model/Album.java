package hcmute.edu.vn.spotifyclone.model;

public class Album {
    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public int getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(int albumImage) {
        this.albumImage = albumImage;
    }

    private String albumTitle;
    private int albumImage;

    public Album() {
    }

    public Album(String albumTitle, int albumImage) {
        this.albumTitle = albumTitle;
        this.albumImage = albumImage;
    }
}
