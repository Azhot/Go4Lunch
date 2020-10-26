package fr.azhot.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String uid;
    private String name;
    private String email;
    private String urlPicture;
    @Nullable
    private String selectedRestaurantId;
    @Nullable
    private String selectedRestaurantName;
    private List<String> likedRestaurants;


    public User() {
        // public no-arg constructor needed for Firestore
    }

    public User(String uid, String name, String email, String urlPicture) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.urlPicture = urlPicture;
        this.selectedRestaurantId = null;
        this.selectedRestaurantName = null;
        this.likedRestaurants = new ArrayList<>();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    @Nullable
    public String getSelectedRestaurantId() {
        return selectedRestaurantId;
    }

    public void setSelectedRestaurantId(@Nullable String selectedRestaurantId) {
        this.selectedRestaurantId = selectedRestaurantId;
    }

    @Nullable
    public String getSelectedRestaurantName() {
        return selectedRestaurantName;
    }

    public void setSelectedRestaurantName(@Nullable String selectedRestaurantName) {
        this.selectedRestaurantName = selectedRestaurantName;
    }

    public List<String> getLikedRestaurants() {
        return likedRestaurants;
    }

    public void setLikedRestaurants(List<String> likedRestaurants) {
        this.likedRestaurants = likedRestaurants;
    }
}
