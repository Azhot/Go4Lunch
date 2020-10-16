package fr.azhot.go4lunch.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_ID_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_PHOTO_EXTRA;

public class IntentUtils {


    public static Intent loadRestaurantDataIntoIntent(Context context, Class<?> cls, String restaurantId, Bitmap restaurantPhoto) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(RESTAURANT_ID_EXTRA, restaurantId);
        if (restaurantPhoto != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            restaurantPhoto.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            intent.putExtra(RESTAURANT_PHOTO_EXTRA, byteArray);
        }
        return intent;
    }
}
