package fr.azhot.go4lunch.view;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.FragmentMapViewBinding;
import fr.azhot.go4lunch.model.NearbySearch;
import fr.azhot.go4lunch.util.LocationUtils;
import fr.azhot.go4lunch.util.PermissionsUtils;
import fr.azhot.go4lunch.viewmodel.RestaurantViewModel;

import static fr.azhot.go4lunch.util.AppConstants.DEFAULT_INTERVAL;
import static fr.azhot.go4lunch.util.AppConstants.DEFAULT_ZOOM;
import static fr.azhot.go4lunch.util.AppConstants.DISTANCE_UNTIL_UPDATE;
import static fr.azhot.go4lunch.util.AppConstants.FASTEST_INTERVAL;
import static fr.azhot.go4lunch.util.AppConstants.NEARBY_SEARCH_RADIUS;
import static fr.azhot.go4lunch.util.AppConstants.RC_CHECK_SETTINGS;

@SuppressWarnings("MissingPermission") // ok since permissions are forced to the user @ onResume
public class MapViewFragment extends Fragment implements OnMapReadyCallback, Observer<NearbySearch> {


    // private static
    private static final String TAG = "MapViewFragment";


    // public static
    public static MapViewFragment newInstance() {
        Log.d(TAG, "newInstance");

        MapViewFragment fragment = new MapViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    // variables
    private FragmentMapViewBinding mBinding;
    private Context mContext;
    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private RestaurantViewModel mRestaurantViewModel;


    // inherited methods
    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach");

        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        init(inflater);
        LocationUtils.checkLocationSettings((AppCompatActivity) mContext, DEFAULT_INTERVAL, FASTEST_INTERVAL, RC_CHECK_SETTINGS);

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");

        super.onResume();
        checkPermissions(); // should NOT be remove without removing @SuppressWarnings("MissingPermission")
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");

        super.onDetach();
        mContext = null;
    }

    /**
     * Called when map is ready.
     * Is triggered when calling SupportMapFragment.getMapAsync(OnMapReadyCallback onMapReadyCallback)
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");

        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (MainActivity.CURRENT_LOCATION != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(MainActivity.CURRENT_LOCATION.getLatitude(), MainActivity.CURRENT_LOCATION.getLongitude()),
                    DEFAULT_ZOOM));
        }
        initLocationUpdates();
    }

    /**
     * Observer for RestaurantViewModel.getNearbyRestaurants(String location, int radius) method
     */
    @Override
    public void onChanged(NearbySearch nearbySearch) {
        Log.d(TAG, "onChanged");

        if (nearbySearch != null) {
            List<NearbySearch.Result> restaurants = nearbySearch.getResults();
            MainActivity.CURRENT_RESTAURANTS.clear();
            MainActivity.CURRENT_RESTAURANTS.addAll(restaurants);
            addRestaurantMarkers(MainActivity.CURRENT_RESTAURANTS);
        }
    }


    // methods
    private void init(LayoutInflater inflater) {
        Log.d(TAG, "init");

        mBinding = FragmentMapViewBinding.inflate(inflater);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        mRestaurantViewModel = ViewModelProviders.of(this).get(RestaurantViewModel.class);
        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationUtils.checkLocationSettings((AppCompatActivity) mContext, DEFAULT_INTERVAL, FASTEST_INTERVAL, RC_CHECK_SETTINGS);
                if (MainActivity.CURRENT_LOCATION != null) {
                    moveCamera(MainActivity.CURRENT_LOCATION, DEFAULT_ZOOM);
                } else {
                    Snackbar.make(mBinding.getRoot(), R.string.get_location_error, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void checkPermissions() {
        Log.d(TAG, "checkPermissions");

        if (!PermissionsUtils.isLocationPermissionGranted(mContext)) {
            requireActivity().finish();
        }
    }

    private void moveCamera(Location location, float zoom) {
        Log.d(TAG, "moveCamera");

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()),
                zoom));
    }

    public void initLocationUpdates() {
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
                    Log.d(TAG, "LocationCallback.onLocationResult");
                    super.onLocationResult(locationResult);

                    if (locationResult.getLastLocation() != null) {
                        if (MainActivity.CURRENT_LOCATION == null
                                || locationResult.getLastLocation().distanceTo(MainActivity.CURRENT_LOCATION) > DISTANCE_UNTIL_UPDATE) {
                            MainActivity.CURRENT_LOCATION = locationResult.getLastLocation();
                            moveCamera(locationResult.getLastLocation(), DEFAULT_ZOOM);
                            mRestaurantViewModel
                                    .getNearbyRestaurants(MainActivity.CURRENT_LOCATION.getLatitude() + "," + MainActivity.CURRENT_LOCATION.getLongitude(), NEARBY_SEARCH_RADIUS)
                                    .observe(getViewLifecycleOwner(), MapViewFragment.this);
                        }
                    }
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    Log.d(TAG, "LocationCallback.onLocationAvailability");
                    super.onLocationAvailability(locationAvailability);

                    if (locationAvailability.isLocationAvailable()) {
                        mGoogleMap.setMyLocationEnabled(true);
                        addRestaurantMarkers(MainActivity.CURRENT_RESTAURANTS);
                    } else {
                        mGoogleMap.setMyLocationEnabled(false);
                        mGoogleMap.clear();
                        Snackbar.make(mBinding.getRoot(), R.string.get_location_error, Snackbar.LENGTH_LONG).show();
                    }
                }
            };
        }

        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    private void addRestaurantMarkers(List<NearbySearch.Result> restaurants) {
        Log.d(TAG, "addRestaurantMarkers");

        // todo : markers color should be orange with
        //  dark-orange knife and fork icon inside.
        //  Color should change to light green and
        //  cutlery in white when at least one workmate
        //  confirms going to the corresponding restaurant
        for (NearbySearch.Result result : restaurants) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(result.getName());
            markerOptions.position(new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng()));
            mGoogleMap.addMarker(markerOptions);
        }
    }

    private void closeKeyboard() {
        Log.d(TAG, "closeKeyboard");

        View view = requireActivity().getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
