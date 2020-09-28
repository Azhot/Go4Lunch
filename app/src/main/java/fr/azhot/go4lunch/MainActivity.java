package fr.azhot.go4lunch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import fr.azhot.go4lunch.databinding.ActivityMainBinding;

import static fr.azhot.go4lunch.AppConstants.RC_PERMISSIONS;

public class MainActivity extends AppCompatActivity {


    // private static
    private static final String TAG = "MainActivity";


    // variables
    private ActivityMainBinding mBinding;
    private MapViewFragment mMapViewFragment;
    private ListViewFragment mListViewFragment;
    private WorkmatesFragment mWorkmatesFragment;


    // inherited methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.toolbar);
        setUpDrawerNavigation();
        setUpBottomNavigation();
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

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");

        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
            mMapViewFragment = (mMapViewFragment == null) ? MapViewFragment.newInstance() : mMapViewFragment;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(mBinding.navHostFragment.getId(), mMapViewFragment)
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

    private void setUpDrawerNavigation() {
        Log.d(TAG, "setUpDrawerNavigation");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                mBinding.drawerLayout,
                mBinding.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mBinding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_your_lunch:
                        // launch details about the restaurant selected by the user
                        break;
                    case R.id.nav_settings:
                        // launch settings, e.g. to set up notifications
                        break;
                    case R.id.nav_logout:
                        // sign out here
                        break;
                    default:
                        Log.d(TAG, "onNavigationItemSelected: could not match user click with id.");
                        break;
                }

                mBinding.drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });
    }

    private void setUpBottomNavigation() {
        Log.d(TAG, "setUpBottomNavigation");

        mBinding.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment;
                switch (item.getItemId()) {
                    case R.id.list_view:
                        Log.d(TAG, "onNavigationItemSelected: user clicked on List view.");
                        selectedFragment = (mListViewFragment == null) ? ListViewFragment.newInstance() : mListViewFragment;
                        setTitle(R.string.list_view_title);
                        break;
                    case R.id.workmates:
                        Log.d(TAG, "onNavigationItemSelected: user clicked on Workmates.");
                        selectedFragment = (mWorkmatesFragment == null) ? WorkmatesFragment.newInstance() : mWorkmatesFragment;
                        setTitle(R.string.workmates_title);
                        break;
                    case R.id.map_view:
                        Log.d(TAG, "onNavigationItemSelected: user clicked on Map view.");
                        selectedFragment = (mMapViewFragment == null) ? MapViewFragment.newInstance() : mMapViewFragment;
                        setTitle(R.string.map_view_title);
                        break;
                    default:
                        Log.d(TAG, "onNavigationItemSelected: could not match user click with id.");
                        selectedFragment = (mMapViewFragment == null) ? MapViewFragment.newInstance() : mMapViewFragment;
                        setTitle(R.string.map_view_title);
                        break;
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit, R.anim.fragment_open_enter, R.anim.fragment_close_exit)
                        .replace(mBinding.navHostFragment.getId(), selectedFragment)
                        .commit();

                return true;
            }
        });
    }
}