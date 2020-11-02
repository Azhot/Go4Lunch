package fr.azhot.go4lunch.util;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import fr.azhot.go4lunch.R;

import static fr.azhot.go4lunch.util.AppConstants.RC_CALL_PHONE_PERMISSION;
import static fr.azhot.go4lunch.util.AppConstants.RC_LOCATION_PERMISSIONS;
import static fr.azhot.go4lunch.util.AppConstants.RC_READ_EXTERNAL_STORAGE_PERMISSION;

/**
 * Utils for permissions
 */
public class PermissionsUtils {

    private static final String TAG = PermissionsUtils.class.getSimpleName();

    /**
     * Will prompt a dialog inviting the user to provide the app with the ACCESS_FINE_LOCATION
     * and ACCESS_COARSE_LOCATION permissions.
     *
     * @param appCompatActivity the {@link AppCompatActivity} from which this method is called.
     * @param requestCode       specific request code to match with a result reported
     *                          to onRequestPermissionsResult.
     */
    public static void getLocationPermission(AppCompatActivity appCompatActivity, int requestCode) {
        Log.d(TAG, "getLocationPermission");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
            appCompatActivity.requestPermissions(permissions, requestCode);
        }
    }

    /**
     * Checks whether location permissions are provided by the device or not.
     *
     * @return a boolean which value is set to true if permissions are granted and false otherwise.
     */
    public static boolean isLocationPermissionGranted(Context context) {
        Log.d(TAG, "isLocationPermissionGranted");

        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Combines both {@link PermissionsUtils#getLocationPermission} and
     * {@link PermissionsUtils#isLocationPermissionGranted} methods.
     *
     * @param appCompatActivity the {@link AppCompatActivity} from which this method is called.
     */
    public static void checkLocationPermission(AppCompatActivity appCompatActivity) {
        Log.d(TAG, "checkLocationPermission");

        if (!PermissionsUtils.isLocationPermissionGranted(appCompatActivity)) {
            PermissionsUtils.getLocationPermission(appCompatActivity, RC_LOCATION_PERMISSIONS);
        }
    }

    /**
     * Forces the user to provide location permissions or else finishes the activity.
     *
     * @param appCompatActivity the {@link AppCompatActivity} from which this method is called.
     */
    public static void forceUserChoiceOnLocationPermissions(AppCompatActivity appCompatActivity) {
        Log.d(TAG, "forceUserChoiceOnLocationPermissions");

        new AlertDialog.Builder(appCompatActivity)
                .setTitle(R.string.permissions_dialog_title)
                .setMessage(R.string.permissions_dialog_message)
                .setPositiveButton(R.string.ok, (dialog, which) -> checkLocationPermission(appCompatActivity))
                .setNegativeButton(R.string.exit, (dialog, which) -> appCompatActivity.finish())
                .setOnKeyListener((dialog, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK &&
                            event.getAction() == KeyEvent.ACTION_UP &&
                            !event.isCanceled()) {
                        dialog.cancel();
                        checkLocationPermission(appCompatActivity);
                        return true;
                    }
                    return false;
                })
                .show();
    }

    /**
     * Will prompt a dialog inviting the user to provide the app with the CALL_PHONE permission.
     *
     * @param appCompatActivity the {@link AppCompatActivity} from which this method is called.
     * @param requestCode       specific request code to match with a result reported
     *                          to onRequestPermissionsResult.
     */
    public static void getCallPhonePermission(AppCompatActivity appCompatActivity, int requestCode) {
        Log.d(TAG, "getLocationPermission");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.CALL_PHONE,};
            appCompatActivity.requestPermissions(permissions, requestCode);
        }
    }

    /**
     * Checks whether CALL_PHONE permission is provided by the device or calls the
     * {@link PermissionsUtils#getCallPhonePermission} method.
     *
     * @param appCompatActivity the {@link AppCompatActivity} from which this method is called.
     * @return a boolean which value is set to true if permission is granted and false otherwise.
     */
    public static boolean checkCallPhonePermission(AppCompatActivity appCompatActivity) {
        Log.d(TAG, "checkLocationPermission");

        if (!(ContextCompat.checkSelfPermission(appCompatActivity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)) {
            PermissionsUtils.getCallPhonePermission(appCompatActivity, RC_CALL_PHONE_PERMISSION);
        }

        return ContextCompat.checkSelfPermission(appCompatActivity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Will prompt a dialog inviting the user to provide the app with the READ_EXTERNAL_STORAGE permission.
     *
     * @param appCompatActivity the {@link AppCompatActivity} from which this method is called.
     * @param requestCode       specific request code to match with a result reported
     *                          to onRequestPermissionsResult.
     */
    public static void getExternalStoragePermission(AppCompatActivity appCompatActivity, int requestCode) {
        Log.d(TAG, "getLocationPermission");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,};
            appCompatActivity.requestPermissions(permissions, requestCode);
        }
    }

    /**
     * Checks whether READ_EXTERNAL_STORAGE permission is provided by the device or calls the
     * {@link PermissionsUtils#getExternalStoragePermission} method.
     *
     * @param appCompatActivity the {@link AppCompatActivity} from which this method is called.
     * @return a boolean which value is set to true if permission is granted and false otherwise.
     */
    public static boolean checkExternalStoragePermission(AppCompatActivity appCompatActivity) {
        Log.d(TAG, "checkLocationPermission");

        if (!(ContextCompat.checkSelfPermission(appCompatActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            PermissionsUtils.getExternalStoragePermission(appCompatActivity, RC_READ_EXTERNAL_STORAGE_PERMISSION);
        }

        return ContextCompat.checkSelfPermission(appCompatActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}
