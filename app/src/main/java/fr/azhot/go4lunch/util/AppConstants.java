package fr.azhot.go4lunch.util;

public abstract class AppConstants {
    public static final int RC_LOCATION_PERMISSIONS = 1234;
    public static final int RC_CALL_PHONE_PERMISSION = 2345;
    public static final int RC_CHECK_LOCATION_SETTINGS = 3456;
    public static final int RC_READ_EXTERNAL_STORAGE_PERMISSION = 7890;
    public static final int DEFAULT_INTERVAL = 10000;
    public static final int FASTEST_INTERVAL = 5000;
    public static final String RESTAURANT_ID_EXTRA = "restaurantId";
    public static final String SELECTED_RESTAURANT_ID_FIELD = "selectedRestaurantId";
    public static final String LIKED_RESTAURANTS_ID_FIELD = "likedRestaurants";
    public static final String NAME_ID_FIELD = "name";
    public static final String URL_PICTURE_ID_FIELD = "urlPicture";
    public static final String SHARED_PREFERENCES_NAME = "fr.azhot.go4lunch";
    public static final String NOTIFICATIONS_PREFERENCES_NAME = "fr.azhot.go4lunch.notifications";
}
