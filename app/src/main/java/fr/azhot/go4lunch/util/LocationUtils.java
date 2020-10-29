package fr.azhot.go4lunch.util;

import android.content.IntentSender;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

public class LocationUtils {

    private static final String TAG = LocationUtils.class.getSimpleName();

    public static void checkLocationSettings(final AppCompatActivity appCompatActivity, int interval, int fastestInterval, final int requestCode) {
        Log.d(TAG, "checkLocationSettings");

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest
                .setInterval(interval)
                .setFastestInterval(fastestInterval)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(appCompatActivity);
        client.checkLocationSettings(builder.build())
                .addOnFailureListener(appCompatActivity, e -> {
                    try {
                        // Location settings are not satisfied, but it can be fixed
                        if (e instanceof ResolvableApiException) {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(appCompatActivity, requestCode);
                        }
                    } catch (IntentSender.SendIntentException sendEx) {
                        Log.e(TAG, "onFailure", sendEx);
                    }
                });
    }
}
