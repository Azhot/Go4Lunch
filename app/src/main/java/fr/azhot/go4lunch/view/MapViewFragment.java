package fr.azhot.go4lunch.view;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import fr.azhot.go4lunch.POJO.NearbySearch;
import fr.azhot.go4lunch.POJO.Result;
import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.FragmentMapViewBinding;
import fr.azhot.go4lunch.util.AppConstants;
import fr.azhot.go4lunch.util.LocationUtils;
import fr.azhot.go4lunch.util.PermissionsUtils;
import fr.azhot.go4lunch.viewmodel.RestaurantViewModel;

import static fr.azhot.go4lunch.util.AppConstants.DEFAULT_INTERVAL;
import static fr.azhot.go4lunch.util.AppConstants.FASTEST_INTERVAL;
import static fr.azhot.go4lunch.util.AppConstants.RC_CHECK_SETTINGS;

@SuppressWarnings("MissingPermission") // ok since permissions are forced to the user @ onResume
public class MapViewFragment extends Fragment implements OnMapReadyCallback {


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
    private RestaurantViewModel mRestaurantViewModel;
    private List<Result> mRestaurants = new ArrayList<>();


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
        loadMap();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");

        super.onPause();
        // todo : turn of callback when implementation of location updates
    }


    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");

        super.onDetach();
        mContext = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");

        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
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
                getDeviceLocation();
            }

        });
    }

    private void checkPermissions() {
        Log.d(TAG, "checkPermissions");

        if (!PermissionsUtils.isLocationPermissionGranted(mContext)) {
            requireActivity().finish();
        }
    }

    private void loadMap() {
        Log.d(TAG, "loadMap");

        checkPermissions();
        mMapFragment.getMapAsync(this);
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation");

        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            moveCamera(task.getResult(), AppConstants.DEFAULT_ZOOM);

                            double latitude = task.getResult().getLatitude();
                            double longitude = task.getResult().getLongitude();

                            mRestaurantViewModel.getNearbyRestaurants(latitude + "," + longitude, 1000).observe(getViewLifecycleOwner(), new Observer<NearbySearch>() {
                                @Override
                                public void onChanged(NearbySearch nearbySearch) {
                                    Log.d(TAG, "onChanged");
                                    List<Result> restaurants = nearbySearch.getResults();
                                    mRestaurants.clear();
                                    mRestaurants.addAll(restaurants);
                                    addRestaurantMarkers(mRestaurants);
                                }
                            });


                        } else {
                            Log.d(TAG, "onComplete: current location is null ");
                            LocationUtils.checkLocationSettings((AppCompatActivity) mContext, DEFAULT_INTERVAL, FASTEST_INTERVAL, RC_CHECK_SETTINGS);
                            Snackbar.make(mBinding.getRoot(), R.string.get_location_error, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void moveCamera(Location location, float zoom) {
        Log.d(TAG, "moveCamera");

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()),
                zoom));
    }

    private void closeKeyboard() {
        Log.d(TAG, "closeKeyboard");

        View view = requireActivity().getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addRestaurantMarkers(List<Result> restaurants) {
        for (Result result : restaurants) {
            Log.d(TAG, "addRestaurantMarkers: add marker for: " + result.getName());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(result.getName());
            Log.d(TAG, "addRestaurantMarkers:" + result.getName() + "'s location = " + result.getGeometry().getLocation());
            markerOptions.position(new LatLng(result.getGeometry().getLocation().getLatitude(), result.getGeometry().getLocation().getLongitude()));
            Log.d(TAG, "addRestaurantMarkers: " + result.getName() + " is located: " + result.getGeometry().getLocation().getLatitude() + "," + result.getGeometry().getLocation().getLongitude());
            mGoogleMap.addMarker(markerOptions);
            mGoogleMap.clear();
        }
    }
}
