package fr.azhot.go4lunch.model;

import android.graphics.Bitmap;

public class Restaurant extends NearbyRestaurantsPOJO.Result {

    private Bitmap photo;

    public Restaurant(NearbyRestaurantsPOJO.Result result, Bitmap photo) {
        super(result.getGeometry(), result.getIcon(), result.getName(), result.getPhotos(),
                result.getPlaceId(), result.getReference(), result.getScope(), result.getTypes(),
                result.getVicinity(), result.getBusinessStatus(), result.getOpeningHours(),
                result.getPlusCode(), result.getRating(), result.getUserRatingsTotal());
        this.photo = photo;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}
