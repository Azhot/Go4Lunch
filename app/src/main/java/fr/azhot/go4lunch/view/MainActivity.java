package fr.azhot.go4lunch.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.ActivityMainBinding;
import fr.azhot.go4lunch.model.NearbyRestaurantsPOJO;
import fr.azhot.go4lunch.model.Restaurant;
import fr.azhot.go4lunch.util.PermissionsUtils;
import fr.azhot.go4lunch.viewmodel.AppViewModel;

import static fr.azhot.go4lunch.util.AppConstants.DEFAULT_INTERVAL;
import static fr.azhot.go4lunch.util.AppConstants.DISTANCE_UNTIL_UPDATE;
import static fr.azhot.go4lunch.util.AppConstants.FASTEST_INTERVAL;
import static fr.azhot.go4lunch.util.AppConstants.NEARBY_SEARCH_RADIUS;
import static fr.azhot.go4lunch.util.AppConstants.RC_PERMISSIONS;

public class MainActivity extends AppCompatActivity {


    // private static
    private static final String TAG = "MainActivity";


    // variables
    private ActivityMainBinding mBinding;
    private FirebaseAuth mAuth;
    private AppViewModel mAppViewModel;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private Location mDeviceLastKnownLocation;


    // inherited methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        mAuth = FirebaseAuth.getInstance();
        mAppViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.mainToolbar);
        setUpDrawerNavigation();
        setUpDrawerWithUserDetails();
        setUpBottomNavigation();
        PermissionsUtils.checkLocationPermissions(this);
        if (PermissionsUtils.isLocationPermissionGranted(this)) {
            launchFragment();
            initObservers();
        }
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
                        PermissionsUtils.forceUserChoiceOnPermissions(this);
                        return;
                    }
                }
            }
            launchFragment();
            initObservers();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");

        if (mBinding.mainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.mainDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        String[] test = new String[]{"abc", "def", "ghi"};

        SearchView.SearchAutoComplete textArea = searchView.findViewById(R.id.search_src_text);
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, test);
        textArea.setAdapter(arrayAdapter);

        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(getString(R.string.search_bar_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // searching here
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLocationUpdates();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");

        super.onPause();
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }


    // methods
    private void setUpDrawerNavigation() {
        Log.d(TAG, "setUpDrawerNavigation");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                mBinding.mainDrawerLayout,
                mBinding.mainToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mBinding.mainDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mBinding.mainNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
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
                        mAuth.signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        break;
                    default:
                        Log.d(TAG, "onNavigationItemSelected: could not match user click with id.");
                        break;
                }

                mBinding.mainDrawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });
    }

    // configuring nav drawer header
    private void setUpDrawerWithUserDetails() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            AppCompatImageView userPicture = mBinding.mainNavView
                    .getHeaderView(0)
                    .findViewById(R.id.drawer_header_user_picture);
            Glide.with(this)
                    // todo : add a "no image" picture here instead of null
                    .load((currentUser.getPhotoUrl()))
                    .circleCrop()
                    .into(userPicture);

            AppCompatTextView userName = mBinding.mainNavView
                    .getHeaderView(0)
                    .findViewById(R.id.drawer_header_user_name);
            userName.setText(currentUser.getDisplayName());

            AppCompatTextView userEmail = mBinding.mainNavView
                    .getHeaderView(0)
                    .findViewById(R.id.drawer_header_user_email);
            userEmail.setText(currentUser.getEmail());
        }
    }

    private void setUpBottomNavigation() {
        Log.d(TAG, "setUpBottomNavigation");

        mBinding.mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment;
                switch (item.getItemId()) {
                    case R.id.list_view:
                        Log.d(TAG, "onNavigationItemSelected: list view fragment");
                        selectedFragment = ListViewFragment.newInstance();
                        setTitle(R.string.list_view_title);
                        break;
                    case R.id.workmates:
                        Log.d(TAG, "onNavigationItemSelected: workmates fragment");
                        selectedFragment = WorkmatesFragment.newInstance();
                        setTitle(R.string.workmates_title);
                        break;
                    case R.id.map_view:
                        Log.d(TAG, "onNavigationItemSelected: map view fragment");
                        selectedFragment = MapViewFragment.newInstance();
                        setTitle(R.string.map_view_title);
                        break;
                    default:
                        Log.d(TAG, "onNavigationItemSelected: could not match user click with id.");
                        selectedFragment = MapViewFragment.newInstance();
                        setTitle(R.string.map_view_title);
                        break;
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit, R.anim.fragment_open_enter, R.anim.fragment_close_exit)
                        .replace(mBinding.mainNavHostFragment.getId(), selectedFragment)
                        .commit();

                return true;
            }
        });
    }

    // ok since permissions are forced onto the user @ onRequestPermissionsResult and
    // checkLocationPermissions is called @ onCreate
    @SuppressWarnings("MissingPermission")
    private void initLocationUpdates() {
        Log.d(TAG, "initLocationUpdates");

        LocationRequest locationRequest = new LocationRequest();
        locationRequest
                .setInterval(DEFAULT_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (mLocationCallback == null) {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Log.d(TAG, "LocationCallback: onLocationResult");

                    super.onLocationResult(locationResult);
                    Location currentLocation = locationResult.getLastLocation();
                    mAppViewModel.setDeviceLocation(currentLocation);
                    if (mDeviceLastKnownLocation == null || mDeviceLastKnownLocation.distanceTo(currentLocation) > DISTANCE_UNTIL_UPDATE) {
                        mDeviceLastKnownLocation = currentLocation;
                        mAppViewModel.setNearbyRestaurantsPOJO(mDeviceLastKnownLocation.getLatitude() + "," + mDeviceLastKnownLocation.getLongitude(), NEARBY_SEARCH_RADIUS);
                    }
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    Log.d(TAG, "LocationCallback: onLocationAvailability");

                    super.onLocationAvailability(locationAvailability);
                    mAppViewModel.setLocationActivated(locationAvailability.isLocationAvailable());
                }
            };
        }

        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    private void launchFragment() {
        Log.d(TAG, "launchFragment");

        if (PermissionsUtils.isLocationPermissionGranted(this)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(mBinding.mainNavHostFragment.getId(), MapViewFragment.newInstance())
                    .commit();
            setTitle(R.string.list_view_title);
        } else {
            PermissionsUtils.checkLocationPermissions(this);
        }
    }

    private void initObservers() {
        Log.d(TAG, "initObservers");

        mAppViewModel.getNearbyRestaurantsPOJO().observe(this, new Observer<NearbyRestaurantsPOJO>() {
            @Override
            public void onChanged(NearbyRestaurantsPOJO nearbyRestaurantsPOJO) {
                Log.d(TAG, "getNearbyRestaurantsPOJO: onChanged");

                if (nearbyRestaurantsPOJO == null) {
                    // todo : check if connection is available or else show message to user that no nearby restaurants
                } else {
                    // todo : bugs if connection was not available on first call then it never gets nearby restaurants
                    mAppViewModel.setRestaurants(nearbyRestaurantsPOJO);
                }
            }
        });

        mAppViewModel.getRestaurants().observe(this, new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                mAppViewModel.loadRestaurantsPhotos(restaurants, Glide.with(MainActivity.this));
            }
        });
    }
}