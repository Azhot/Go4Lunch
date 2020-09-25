package fr.azhot.go4lunch;

import android.content.IntentSender;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;

public class LocationUtils {

    private static final String TAG = "LocationUtils";

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
                .addOnFailureListener(appCompatActivity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        try {
                            // Location settings are not satisfied, but it can be fixed
                            if (e instanceof ResolvableApiException) {
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(appCompatActivity, requestCode);
                            }
                        } catch (IntentSender.SendIntentException sendEx) {
                            Log.e(TAG, "onFailure", sendEx);
                        }
                    }
                });
    }
}
