package fr.azhot.go4lunch.model;

import android.graphics.Bitmap;
import android.location.Location;

public class Restaurant extends NearbyRestaurantsPOJO.Result {

    private Bitmap mPhoto;
    private Location mLocation;

    public Restaurant(NearbyRestaurantsPOJO.Result result, Bitmap photo, Location location) {
        super(result.getGeometry(), result.getIcon(), result.getName(), result.getPhotos(),
                result.getPlaceId(), result.getReference(), result.getScope(), result.getTypes(),
                result.getVicinity(), result.getBusinessStatus(), result.getOpeningHours(),
                result.getPlusCode(), result.getRating(), result.getUserRatingsTotal());
        this.mPhoto = photo;
        this.mLocation = location;
    }

    public Bitmap getPhoto() {
        return mPhoto;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }
}
