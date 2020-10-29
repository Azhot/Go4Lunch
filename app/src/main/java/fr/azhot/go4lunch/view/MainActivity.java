package fr.azhot.go4lunch.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import fr.azhot.go4lunch.BuildConfig;
import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.ActivityMainBinding;
import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.util.IntentUtils;
import fr.azhot.go4lunch.util.PermissionsUtils;
import fr.azhot.go4lunch.viewmodel.AppViewModel;

import static fr.azhot.go4lunch.util.AppConstants.DEFAULT_INTERVAL;
import static fr.azhot.go4lunch.util.AppConstants.FASTEST_INTERVAL;
import static fr.azhot.go4lunch.util.AppConstants.RC_LOCATION_PERMISSIONS;

public class MainActivity extends AppCompatActivity {


    // private static
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int NEARBY_SEARCH_RADIUS = 500;
    public static final int AUTOCOMPLETE_SEARCH_RADIUS = 5000;
    public static final float DISTANCE_UNTIL_UPDATE = 50f;
    public static final String RESTAURANT_TYPE = "restaurant";


    // variables
    private ActivityMainBinding mBinding;
    private FirebaseAuth mAuth;
    private AppViewModel mViewModel;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private Location mDeviceLocation;
    private User mUser;


    // inherited methods
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_LOCATION_PERMISSIONS) {
            if (grantResults.length > 0) {
                for (int i : grantResults) {
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: permissions denied.");
                        PermissionsUtils.forceUserChoiceOnLocationPermissions(this);
                        return;
                    } else {
                        launchFragment();
                    }
                }
            }
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
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        mAuth = FirebaseAuth.getInstance();
        mViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.mainToolbar);
        setUpDrawerNavigation();
        setUpBottomNavigation();
        PermissionsUtils.checkLocationPermission(this);
        if (PermissionsUtils.isLocationPermissionGranted(this)) {
            launchFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);

        setUpAutoCompleteSearchView(menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");

        super.onResume();
        initLocationUpdates();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mViewModel.getUser(currentUser.getUid())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "getUser: onSuccess");
                            mUser = task.getResult().toObject(User.class);
                            if (mUser != null) {
                                setUpDrawerWithUserDetails(mUser);
                            }
                        } else {
                            Log.e(TAG, "getUser: onFailure", task.getException());
                        }
                    });
        }
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

        mBinding.mainNavView.setNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_your_lunch) {
                if (mUser.getSelectedRestaurantId() != null) {
                    Intent intent = IntentUtils.loadRestaurantDataIntoIntent(
                            MainActivity.this, RestaurantDetailsActivity.class, mUser.getSelectedRestaurantId());
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, R.string.no_restaurant_selected, Toast.LENGTH_SHORT).show();
                }
            } else if (item.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            } else if (item.getItemId() == R.id.nav_logout) {
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }

            mBinding.mainDrawerLayout.closeDrawer(GravityCompat.START);

            return true;
        });
    }

    // configuring nav drawer header
    private void setUpDrawerWithUserDetails(User user) {
        Log.d(TAG, "setUpDrawerWithUserDetails");

        Glide.with(MainActivity.this)
                .load((user.getUrlPicture()))
                .circleCrop()
                .into((AppCompatImageView) mBinding.mainNavView.getHeaderView(0).findViewById(R.id.drawer_header_user_picture));

        AppCompatTextView nameTextView = mBinding.mainNavView.getHeaderView(0).findViewById(R.id.drawer_header_user_name);
        nameTextView.setText(user.getName());

        AppCompatTextView emailTextView = mBinding.mainNavView.getHeaderView(0).findViewById(R.id.drawer_header_user_email);
        emailTextView.setText(user.getEmail());
    }

    private void setUpBottomNavigation() {
        Log.d(TAG, "setUpBottomNavigation");

        mBinding.mainBottomNav.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment;

            if (item.getItemId() == R.id.list_view) {
                selectedFragment = ListViewFragment.newInstance();
                setTitle(R.string.list_view_title);
            } else if (item.getItemId() == R.id.workmates) {
                selectedFragment = WorkmatesFragment.newInstance();
                setTitle(R.string.workmates_title);
            } else if (item.getItemId() == R.id.map_view) {
                selectedFragment = MapViewFragment.newInstance();
                setTitle(R.string.map_view_title);
            } else {
                selectedFragment = MapViewFragment.newInstance();
                setTitle(R.string.map_view_title);
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit, R.anim.fragment_open_enter, R.anim.fragment_close_exit)
                    .replace(mBinding.mainNavHostFragment.getId(), selectedFragment)
                    .commit();

            return true;
        });
    }

    @SuppressWarnings("MissingPermission") // ok since we are calling isLocationPermissionGranted
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
                    mViewModel.setDeviceLocationLiveData(currentLocation);
                    if (mDeviceLocation == null || mDeviceLocation.distanceTo(currentLocation) > DISTANCE_UNTIL_UPDATE) {
                        mDeviceLocation = currentLocation;
                        // should check connection status to send a "no connection"
                        // message to user if not available.
                        // otherwise if connection was not available on first
                        // call then observer never gets nearby restaurants for current location.
                        String location = mDeviceLocation.getLatitude() + "," + mDeviceLocation.getLongitude();
                        mViewModel.setNearbyRestaurantsLiveData(RESTAURANT_TYPE, RESTAURANT_TYPE, location, NEARBY_SEARCH_RADIUS);
                    }
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    Log.d(TAG, "LocationCallback: onLocationAvailability");

                    super.onLocationAvailability(locationAvailability);
                    if (!locationAvailability.isLocationAvailable()) {
                        Toast.makeText(MainActivity.this, R.string.get_location_error, Toast.LENGTH_SHORT).show();
                    }
                    mViewModel.setLocationActivatedLiveData(locationAvailability.isLocationAvailable());
                }
            };
        }

        if (PermissionsUtils.isLocationPermissionGranted(this)) {
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    private void launchFragment() {
        Log.d(TAG, "launchFragment");

        getSupportFragmentManager()
                .beginTransaction()
                .replace(mBinding.mainNavHostFragment.getId(), MapViewFragment.newInstance())
                .commit();
        setTitle(R.string.list_view_title);
    }

    private void setUpAutoCompleteSearchView(Menu menu) {
        Log.d(TAG, "setUpAutoCompleteSearchView");

        if (!Places.isInitialized()) {
            Places.initialize(MainActivity.this, BuildConfig.GOOGLE_API_KEY);
        }
        PlacesClient placesClient = Places.createClient(MainActivity.this);

        List<AutocompletePrediction> predictions = new ArrayList<>();

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint(getString(R.string.search_bar_hint));
        AutoCompleteTextView autoCompleteTextView = searchView.findViewById(R.id.search_src_text);
        autoCompleteTextView.setThreshold(3);
        String[] from = new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1};
        int[] to = new int[]{R.id.item_label};
        CursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.search_item,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        searchView.setSuggestionsAdapter(cursorAdapter);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                closeKeyboard();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cursorAdapter.changeCursor(null);

                if (newText.length() >= 3
                        && locationManager != null
                        && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    RectangularBounds bounds = RectangularBounds.newInstance(
                            getCoordinate(
                                    mDeviceLocation.getLatitude(),
                                    mDeviceLocation.getLongitude(),
                                    -AUTOCOMPLETE_SEARCH_RADIUS),
                            getCoordinate(
                                    mDeviceLocation.getLatitude(),
                                    mDeviceLocation.getLongitude(),
                                    AUTOCOMPLETE_SEARCH_RADIUS));

                    AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

                    FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                            .setLocationRestriction(bounds)
                            .setSessionToken(token)
                            .setTypeFilter(TypeFilter.ESTABLISHMENT)
                            .setQuery(newText)
                            .build();

                    placesClient.findAutocompletePredictions(request)
                            .addOnSuccessListener(MainActivity.this, findAutocompletePredictionsResponse -> {
                                Log.d(TAG, "onSuccess");

                                predictions.clear();
                                predictions.addAll(findAutocompletePredictionsResponse.getAutocompletePredictions());

                                MatrixCursor matrixCursor = new MatrixCursor(new String[]{BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1});

                                for (int i = 0; i < predictions.size(); i++) {
                                    AutocompletePrediction prediction = predictions.get(i);
                                    Log.d(TAG, "findAutocompletePredictions : onSuccess: " + prediction.getPrimaryText(null));
                                    if (prediction.getPlaceTypes().contains(Place.Type.RESTAURANT)) {
                                        matrixCursor.addRow(new Object[]{i, prediction.getPrimaryText(null) + ", " + prediction.getSecondaryText(null)});
                                        cursorAdapter.changeCursor(matrixCursor);
                                    }
                                }
                            })
                            .addOnFailureListener(MainActivity.this, e -> Log.e(TAG, "findAutocompletePredictions : onFailure: ", e));
                } else if (newText.length() == 0) {
                    mViewModel.setDetailsRestaurantFromAutocompleteLiveData(null);
                }

                return true;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                closeKeyboard();

                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                String selection = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                searchView.setQuery(selection, false);
                mViewModel.setDetailsRestaurantFromAutocompleteLiveData(predictions.get(position).getPlaceId());

                return true;
            }
        });

        searchView.setOnCloseListener(() -> {
            mViewModel.setDetailsRestaurantFromAutocompleteLiveData(null);
            return false;
        });
    }

    private LatLng getCoordinate(double latitude, double longitude, long distance) {
        Log.d(TAG, "getCoordinate");

        double lat = latitude + (180 / Math.PI) * (distance / 6378137f);
        double lng = longitude + (180 / Math.PI) * (distance / 6378137f) / Math.cos(latitude);
        return new LatLng(lat, lng);
    }

    private void closeKeyboard() {
        Log.d(TAG, "closeKeyboard");

        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}