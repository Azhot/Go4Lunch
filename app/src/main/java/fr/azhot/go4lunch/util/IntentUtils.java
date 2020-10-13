package fr.azhot.go4lunch.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

import fr.azhot.go4lunch.model.Restaurant;

import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_ID_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_NAME_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_PHOTO_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_RATING_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_VICINITY_EXTRA;

public class IntentUtils {

    public static Intent loadRestaurantDataIntoIntent(Context context, Class<?> cls, Restaurant restaurant) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(RESTAURANT_ID_EXTRA, restaurant.getPlaceId());
        intent.putExtra(RESTAURANT_NAME_EXTRA, restaurant.getName());
        intent.putExtra(RESTAURANT_VICINITY_EXTRA, restaurant.getVicinity());
        if (restaurant.getPhoto() != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            restaurant.getPhoto().compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            intent.putExtra(RESTAURANT_PHOTO_EXTRA, byteArray);
        }
        intent.putExtra(RESTAURANT_RATING_EXTRA, (int) Math.round(restaurant.getRating() / 5 * 3));
        return intent;
    }
}
