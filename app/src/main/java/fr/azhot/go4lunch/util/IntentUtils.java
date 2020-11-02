package fr.azhot.go4lunch.util;

import android.content.Context;
import android.content.Intent;

import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_ID_EXTRA;

/**
 * Utils for intents
 */
public class IntentUtils {

    /**
     * Loads up and return an intent with a specific placeId
     *
     * @param context a {@link Context} of the application package implementing this class.
     * @param cls     the component class that is to be used for the intent.
     * @param placeId the unique identifier of the place to load to the intent.
     * @return an {@link Intent} loaded with the placeId specified as parameter.
     */
    public static Intent loadRestaurantDataIntoIntent(Context context, Class<?> cls, String placeId) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(RESTAURANT_ID_EXTRA, placeId);
        return intent;
    }
}
