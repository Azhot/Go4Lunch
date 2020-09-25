package fr.azhot.go4lunch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import fr.azhot.go4lunch.databinding.ActivityMainBinding;

import static fr.azhot.go4lunch.AppConstants.RC_PERMISSIONS;

public class MainActivity extends AppCompatActivity {


    // private static
    private static final String TAG = "MainActivity";


    // variables
    private ActivityMainBinding mBinding;


    // inherited methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        init();
        setContentView(mBinding.getRoot());
        launchFragment();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSIONS) {
            if (grantResults.length > 0) {
                for (int i : grantResults) {
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: permissions denied.");
                        forceUserChoiceOnPermissions(this);
                        return;
                    }
                }
            }
            //todo: might not be kept here
            launchFragment();
        }
    }

    private void init() {
        Log.d(TAG, "init");
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
    }

    private void checkPermissions() {
        Log.d(TAG, "checkPermissions");
        if (!PermissionsUtils.isLocationPermissionGranted(this)) {
            PermissionsUtils.getLocationPermission(this, RC_PERMISSIONS);
        }
    }

    private void launchFragment() {
        Log.d(TAG, "launchFragment");
        if (PermissionsUtils.isLocationPermissionGranted(this)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(mBinding.navHostFragment.getId(), MapViewFragment.newInstance())
                    .commit();
        } else {
            checkPermissions();
        }
    }

    private void forceUserChoiceOnPermissions(final AppCompatActivity appCompatActivity) {
        Log.d(TAG, "forceUserChoiceOnPermissions");
        new AlertDialog.Builder(appCompatActivity)
                .setTitle(R.string.permissions_dialog_title)
                .setMessage(R.string.permissions_dialog_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkPermissions();
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
                            checkPermissions();
                            return true;
                        }
                        return false;
                    }
                })
                .show();
    }
}