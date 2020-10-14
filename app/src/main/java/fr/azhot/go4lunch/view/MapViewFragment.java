package fr.azhot.go4lunch.view;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.FragmentMapViewBinding;
import fr.azhot.go4lunch.model.Restaurant;
import fr.azhot.go4lunch.util.IntentUtils;
import fr.azhot.go4lunch.util.LocationUtils;
import fr.azhot.go4lunch.util.PermissionsUtils;
import fr.azhot.go4lunch.viewmodel.AppViewModel;

import static fr.azhot.go4lunch.util.AppConstants.CENTER_FRANCE;
import static fr.azhot.go4lunch.util.AppConstants.DEFAULT_INTERVAL;
import static fr.azhot.go4lunch.util.AppConstants.DEFAULT_ZOOM;
import static fr.azhot.go4lunch.util.AppConstants.FASTEST_INTERVAL;
import static fr.azhot.go4lunch.util.AppConstants.INIT_ZOOM;
import static fr.azhot.go4lunch.util.AppConstants.RC_CHECK_SETTINGS;

@SuppressWarnings("MissingPermission") // ok since permissions are forced to the user @ onResume
public class MapViewFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


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
    private AppViewModel mAppViewModel;
    private Map<MarkerOptions, Restaurant> mRestaurants;


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
        LocationUtils.checkLocationSettings(
                (AppCompatActivity) mContext, DEFAULT_INTERVAL, FASTEST_INTERVAL, RC_CHECK_SETTINGS);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");

        super.onActivityCreated(savedInstanceState);
        mAppViewModel = ViewModelProviders.of(requireActivity()).get(AppViewModel.class);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");

        super.onResume();
        checkLocationPermissions(mContext); // should NOT be remove without removing @SuppressWarnings("MissingPermission")
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
        initObservers();
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                CENTER_FRANCE,
                INIT_ZOOM));
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (mDeviceLastKnownLocation != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mDeviceLastKnownLocation.getLatitude(), mDeviceLastKnownLocation.getLongitude()),
                    DEFAULT_ZOOM));
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClick");
        for (Map.Entry<MarkerOptions, Restaurant> entry : mRestaurants.entrySet()) {
            if (marker.getTag() == entry.getValue().getPlaceId()) {
                Intent intent = IntentUtils.loadRestaurantDataIntoIntent(
                        mContext, RestaurantDetailsActivity.class, entry.getValue());
                startActivity(intent);
            }
        }
        return true;
    }


    // methods
    private void init(LayoutInflater inflater) {
        Log.d(TAG, "init");

        mBinding = FragmentMapViewBinding.inflate(inflater);
        mMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.cell_workmates_fragment_container);
        mRestaurants = new HashMap<>();

        mBinding.mapViewFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationUtils.checkLocationSettings(
                        (AppCompatActivity) mContext, DEFAULT_INTERVAL, FASTEST_INTERVAL, RC_CHECK_SETTINGS);
                if (mDeviceLastKnownLocation != null) {
                    animateCamera(mDeviceLastKnownLocation, DEFAULT_ZOOM, mGoogleMap);
                } else {
                    Toast.makeText(mContext, R.string.get_location_error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initObservers() {
        Log.d(TAG, "initObservers");

        mAppViewModel.getRestaurants().observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                Log.d(TAG, "getRestaurants: onChanged");
                // todo : should check if connection is available or else show message to user

                mRestaurants.clear();
                for (Restaurant restaurant : restaurants) {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(restaurant.getLocation().getLatitude(),
                                    restaurant.getLocation().getLongitude()));
                    Marker marker = mGoogleMap.addMarker(markerOptions);
                    marker.setTag(restaurant.getPlaceId());
                    mRestaurants.put(markerOptions, restaurant);
                }
            }
        });

        mAppViewModel.getDeviceLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                Log.d(TAG, "getDeviceLocation: onChanged");

                if (mDeviceLastKnownLocation == null) {
                    animateCamera(location, DEFAULT_ZOOM, mGoogleMap);
                }
                mDeviceLastKnownLocation = location;
            }
        });

        mAppViewModel.getLocationActivated().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Log.d(TAG, "getLocationActivated: onChanged");

                if (mGoogleMap != null) {
                    mGoogleMap.setMyLocationEnabled(aBoolean);
                    mGoogleMap.clear();
                    for (Map.Entry<MarkerOptions, Restaurant> entry : mRestaurants.entrySet()) {
                        entry.getKey().visible(aBoolean);
                        Marker marker = mGoogleMap.addMarker(entry.getKey());
                        marker.setTag(entry.getValue().getPlaceId());
                    }
                }
            }
        });
    }

    private void checkLocationPermissions(Context context) {
        Log.d(TAG, "checkPermissions");

        if (!PermissionsUtils.isLocationPermissionGranted(context)) {
            requireActivity().finish();
        }
    }

    private void animateCamera(Location location, float zoom, GoogleMap googleMap) {
        Log.d(TAG, "moveCamera");

        if (googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()),
                    zoom));
        }
    }

    private void closeKeyboard() {
        Log.d(TAG, "closeKeyboard");

        View view = requireActivity().getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
