package fr.azhot.go4lunch.model;

import android.graphics.Bitmap;

public class Restaurant {
    private NearbyRestaurantsPOJO.Result mResult;
    private Bitmap photo;

    public Restaurant(NearbyRestaurantsPOJO.Result result, Bitmap photo) {
        this.mResult = result;
        this.photo = photo;
    }

    public NearbyRestaurantsPOJO.Result getResult() {
        return mResult;
    }

    public void setResult(NearbyRestaurantsPOJO.Result result) {
        mResult = result;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}
