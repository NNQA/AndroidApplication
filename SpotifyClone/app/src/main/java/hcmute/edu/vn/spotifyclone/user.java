package hcmute.edu.vn.spotifyclone;

public class user {

    private int image;
    private String name;
    private String address;

    public user(int image, String name, String address) {
        this.image = image;
        this.name = name;
        this.address = address;
    }


    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}