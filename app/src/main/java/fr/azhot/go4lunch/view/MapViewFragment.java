package fr.azhot.go4lunch.view;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.FragmentMapViewBinding;
import fr.azhot.go4lunch.model.NearbySearch;
import fr.azhot.go4lunch.util.LocationUtils;
import fr.azhot.go4lunch.util.PermissionsUtils;
import fr.azhot.go4lunch.viewmodel.AppViewModel;

import static fr.azhot.go4lunch.util.AppConstants.DEFAULT_INTERVAL;
import static fr.azhot.go4lunch.util.AppConstants.DEFAULT_ZOOM;
import static fr.azhot.go4lunch.util.AppConstants.DISTANCE_UNTIL_UPDATE;
import static fr.azhot.go4lunch.util.AppConstants.FASTEST_INTERVAL;
import static fr.azhot.go4lunch.util.AppConstants.NEARBY_SEARCH_RADIUS;
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
    private Location mDeviceLastKnownLocation;
    private boolean mIsLocationActivated;
    private AppViewModel mAppViewModel;
    private List<NearbySearch.Result> mCurrentRestaurants;


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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");

        super.onActivityCreated(savedInstanceState);
        mAppViewModel = ViewModelProviders.of(requireActivity()).get(AppViewModel.class);
        initObserver();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");

        super.onResume();
        checkLocationPermissions(); // should NOT be remove without removing @SuppressWarnings("MissingPermission")
        mMapFragment.getMapAsync(this);
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
        if (mDeviceLastKnownLocation != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mDeviceLastKnownLocation.getLatitude(), mDeviceLastKnownLocation.getLongitude()),
                    DEFAULT_ZOOM));
            if (mIsLocationActivated) {
                mGoogleMap.setMyLocationEnabled(true);
                addRestaurantMarkers(mCurrentRestaurants);
            }
        }
    }


    // methods
    private void init(LayoutInflater inflater) {
        Log.d(TAG, "init");

        mBinding = FragmentMapViewBinding.inflate(inflater);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.cell_workmates_fragment_container);
        mAppViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        mBinding.mapViewFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationUtils.checkLocationSettings((AppCompatActivity) mContext, DEFAULT_INTERVAL, FASTEST_INTERVAL, RC_CHECK_SETTINGS);
                if (mDeviceLastKnownLocation != null) {
                    animateCamera(mDeviceLastKnownLocation, DEFAULT_ZOOM);
                } else {
                    Toast.makeText(mContext, R.string.get_location_error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initObserver() {
        Log.d(TAG, "initObserver");

        mAppViewModel.getNearbyRestaurants().observe(getViewLifecycleOwner(), new Observer<NearbySearch>() {
            @Override
            public void onChanged(NearbySearch nearbySearch) {
                Log.d(TAG, "getNearbyRestaurants: onChanged");

                if (mCurrentRestaurants == null) {
                    mCurrentRestaurants = new ArrayList<>();
                }
                mCurrentRestaurants.clear();
                mCurrentRestaurants.addAll(nearbySearch.getResults());
                addRestaurantMarkers(mCurrentRestaurants);
            }
        });
        mAppViewModel.getDeviceLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                Log.d(TAG, "getDeviceLocation: onChanged");

                if (mDeviceLastKnownLocation == null || mDeviceLastKnownLocation.distanceTo(location) > DISTANCE_UNTIL_UPDATE) {
                    mDeviceLastKnownLocation = location;
                    animateCamera(mDeviceLastKnownLocation, DEFAULT_ZOOM);
                    mAppViewModel.setNearbyRestaurants(mDeviceLastKnownLocation.getLatitude() + "," + mDeviceLastKnownLocation.getLongitude(), NEARBY_SEARCH_RADIUS);
                }
            }
        });
        mAppViewModel.getLocationActivated().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Log.d(TAG, "getLocationActivated: onChanged");

                mIsLocationActivated = aBoolean;

                if (aBoolean) {
                    mGoogleMap.setMyLocationEnabled(true);
                    if (mCurrentRestaurants != null) {
                        addRestaurantMarkers(mCurrentRestaurants);
                    }
                } else {
                    mGoogleMap.setMyLocationEnabled(false);
                    mGoogleMap.clear();
                    Toast.makeText(mContext, R.string.get_location_error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void checkLocationPermissions() {
        Log.d(TAG, "checkPermissions");

        if (!PermissionsUtils.isLocationPermissionGranted(mContext)) {
            requireActivity().finish();
        }
    }

    private void animateCamera(Location location, float zoom) {
        Log.d(TAG, "moveCamera");

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()),
                zoom));
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
            // todo : bugs when log in
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
