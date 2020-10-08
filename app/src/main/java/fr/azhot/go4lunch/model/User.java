package fr.azhot.go4lunch.model;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String name;
    private String email;
    private String urlPicture;
    @Nullable
    private String chosenRestaurantId;
    @Nullable
    private String chosenRestaurantName;


    public User() {
        // public no-arg constructor needed for Firestore
    }

    public User(String uid, String name, String email, String urlPicture) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.urlPicture = urlPicture;
        this.chosenRestaurantId = null;
        this.chosenRestaurantName = null;
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
    public String getChosenRestaurantId() {
        return chosenRestaurantId;
    }

    public void setChosenRestaurantId(@Nullable String chosenRestaurantId) {
        this.chosenRestaurantId = chosenRestaurantId;
    }

    @Nullable
    public String getChosenRestaurantName() {
        return chosenRestaurantName;
    }

    public void setChosenRestaurantName(@Nullable String chosenRestaurantName) {
        this.chosenRestaurantName = chosenRestaurantName;
    }
}
