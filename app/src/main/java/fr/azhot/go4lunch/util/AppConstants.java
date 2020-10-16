package fr.azhot.go4lunch.util;

import com.google.android.gms.maps.model.LatLng;

public abstract class AppConstants {
    public static final int RC_LOCATION_PERMISSIONS = 1234;
    public static final int RC_CALL_PHONE_PERMISSION = 2345;
    public static final int RC_CHECK_SETTINGS = 3456;
    public static final int RC_GOOGLE_SIGN_IN = 4567;
    public static final int DEFAULT_INTERVAL = 10000;
    public static final int FASTEST_INTERVAL = 5000;
    public static final float DEFAULT_ZOOM = 14.5f;
    public static final LatLng CENTER_FRANCE = new LatLng(46.3432097, 2.5733245);
    public static final float INIT_ZOOM = 5f;
    public static final int NEARBY_SEARCH_RADIUS = 500;
    public static final float DISTANCE_UNTIL_UPDATE = 50f;
    public static final String USER_COLLECTION_NAME = "users";
    public static final String RESTAURANT_ID_EXTRA = "restaurantId";
    public static final String RESTAURANT_NAME_EXTRA = "restaurantName";
    public static final String RESTAURANT_VICINITY_EXTRA = "restaurantVicinity";
    public static final String RESTAURANT_PHOTO_EXTRA = "restaurantPhoto";
    public static final String RESTAURANT_RATING_EXTRA = "restaurantRating";
    public static final String SELECTED_RESTAURANT_ID_FIELD = "selectedRestaurantId";
}
