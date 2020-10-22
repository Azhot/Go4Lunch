package fr.azhot.go4lunch.util;

import android.content.Context;
import android.content.Intent;

import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_ID_EXTRA;

public class IntentUtils {


    public static Intent loadRestaurantDataIntoIntent(Context context, Class<?> cls, String placeId) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(RESTAURANT_ID_EXTRA, placeId);
        return intent;
    }
}
