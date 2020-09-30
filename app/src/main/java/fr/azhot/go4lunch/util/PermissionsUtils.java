package fr.azhot.go4lunch.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class PermissionsUtils {

    private static final String TAG = "PermissionsUtils";

    public static void getLocationPermission(AppCompatActivity activity, int requestCode) {
        Log.d(TAG, "getLocationPermission");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            String[] permissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
            activity.requestPermissions(permissions, requestCode);
        }
    }

    public static boolean isLocationPermissionGranted(Context context) {
        Log.d(TAG, "isLocationPermissionGranted");
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
