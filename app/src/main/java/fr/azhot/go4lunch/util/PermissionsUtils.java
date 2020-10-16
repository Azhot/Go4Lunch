package fr.azhot.go4lunch.util;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import fr.azhot.go4lunch.R;

import static fr.azhot.go4lunch.util.AppConstants.RC_CALL_PHONE_PERMISSION;
import static fr.azhot.go4lunch.util.AppConstants.RC_LOCATION_PERMISSIONS;

public class PermissionsUtils {

    private static final String TAG = "PermissionsUtils";

    public static void getLocationPermission(AppCompatActivity activity, int requestCode) {
        Log.d(TAG, "getLocationPermission");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

    public static void checkLocationPermission(AppCompatActivity appCompatActivity) {
        Log.d(TAG, "checkLocationPermission");

        if (!PermissionsUtils.isLocationPermissionGranted(appCompatActivity)) {
            PermissionsUtils.getLocationPermission(appCompatActivity, RC_LOCATION_PERMISSIONS);
        }
    }

    public static void forceUserChoiceOnLocationPermissions(AppCompatActivity appCompatActivity) {
        Log.d(TAG, "forceUserChoiceOnLocationPermissions");

        new AlertDialog.Builder(appCompatActivity)
                .setTitle(R.string.permissions_dialog_title)
                .setMessage(R.string.permissions_dialog_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkLocationPermission(appCompatActivity);
                    }
                })
                .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appCompatActivity.finish();
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK &&
                                event.getAction() == KeyEvent.ACTION_UP &&
                                !event.isCanceled()) {
                            dialog.cancel();
                            checkLocationPermission(appCompatActivity);
                            return true;
                        }
                        return false;
                    }
                })
                .show();
    }

    public static void getCallPhonePermission(AppCompatActivity activity, int requestCode) {
        Log.d(TAG, "getLocationPermission");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.CALL_PHONE,};
            activity.requestPermissions(permissions, requestCode);
        }
    }

    public static boolean checkCallPhonePermission(AppCompatActivity appCompatActivity) {
        Log.d(TAG, "checkLocationPermission");

        if (!(ContextCompat.checkSelfPermission(appCompatActivity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)) {
            PermissionsUtils.getCallPhonePermission(appCompatActivity, RC_CALL_PHONE_PERMISSION);
        }

        return ContextCompat.checkSelfPermission(appCompatActivity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }
}
