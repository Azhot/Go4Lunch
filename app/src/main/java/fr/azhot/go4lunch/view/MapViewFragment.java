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
import fr.azhot.go4lunch.model.Restaurant;
import fr.azhot.go4lunch.util.LocationUtils;
import fr.azhot.go4lunch.util.PermissionsUtils;
import fr.azhot.go4lunch.viewmodel.AppViewModel;

import static fr.azhot.go4lunch.util.AppConstants.DEFAULT_INTERVAL;
import static fr.azhot.go4lunch.util.AppConstants.DEFAULT_ZOOM;
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
    private Location mDeviceLastKnownLocation;
    private boolean mIsLocationActivated;
    private AppViewModel mAppViewModel;
    private List<Restaurant> mCurrentRestaurants;


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
        initObservers();
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
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (mDeviceLastKnownLocation != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mDeviceLastKnownLocation.getLatitude(), mDeviceLastKnownLocation.getLongitude()),
                    DEFAULT_ZOOM));
            mGoogleMap.setMyLocationEnabled(true);
            addRestaurantMarkers(mCurrentRestaurants, mGoogleMap);
        }
    }


    // methods
    private void init(LayoutInflater inflater) {
        Log.d(TAG, "init");

        mBinding = FragmentMapViewBinding.inflate(inflater);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.cell_workmates_fragment_container);
        mCurrentRestaurants = new ArrayList<>();

        mBinding.mapViewFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo : should activate or deactivate the auto animateCamera
                LocationUtils.checkLocationSettings((AppCompatActivity) mContext, DEFAULT_INTERVAL, FASTEST_INTERVAL, RC_CHECK_SETTINGS);
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

                mCurrentRestaurants.clear();
                mCurrentRestaurants.addAll(restaurants);
                addRestaurantMarkers(mCurrentRestaurants, mGoogleMap);
            }
        });

        mAppViewModel.getDeviceLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                Log.d(TAG, "getDeviceLocation: onChanged");

                mDeviceLastKnownLocation = location;
                animateCamera(mDeviceLastKnownLocation, DEFAULT_ZOOM, mGoogleMap);
            }
        });

        mAppViewModel.getLocationActivated().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Log.d(TAG, "getLocationActivated: onChanged");

                if (mGoogleMap != null) {
                    mIsLocationActivated = aBoolean;

                    if (mIsLocationActivated) {
                        // todo : should check if connection is available or else show message to user
                        mGoogleMap.setMyLocationEnabled(true);
                        addRestaurantMarkers(mCurrentRestaurants, mGoogleMap);
                    } else {
                        mGoogleMap.setMyLocationEnabled(false);
                        mGoogleMap.clear();
                        Toast.makeText(mContext, R.string.get_location_error, Toast.LENGTH_LONG).show();
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

    private void addRestaurantMarkers(List<Restaurant> restaurants, GoogleMap googleMap) {
        Log.d(TAG, "addRestaurantMarkers");

        if (restaurants != null && googleMap != null) {
            // todo : Tous les restaurants des alentours sont affichés sur la carte
            //  en utilisant une punaise personnalisée. Si au moins un collègue s’est
            //  déjà manifesté pour aller dans un restaurant donné, la punaise est
            //  affichée dans une couleur différente (verte). L’utilisateur peut appuyer
            //  sur une punaise pour afficher la fiche du restaurant.
            googleMap.clear();
            for (Restaurant restaurant : restaurants) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(restaurant.getName());
                markerOptions.position(new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng()));
                googleMap.addMarker(markerOptions);
            }
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
