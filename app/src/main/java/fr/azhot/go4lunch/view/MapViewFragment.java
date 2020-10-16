package fr.azhot.go4lunch.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.os.Build;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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
    private Location mDeviceLocation;
    private AppViewModel mViewModel;
    private Map<Restaurant, Marker> mRestaurants;
    private List<ListenerRegistration> mListenerRegistrations;


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
        mViewModel = ViewModelProviders.of(requireActivity()).get(AppViewModel.class);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");

        super.onResume();
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");

        super.onDetach();
        mContext = null;
        for (ListenerRegistration registration : mListenerRegistrations) {
            registration.remove();
        }
        mListenerRegistrations.clear();
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
        if (mDeviceLocation != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mDeviceLocation.getLatitude(), mDeviceLocation.getLongitude()),
                    DEFAULT_ZOOM));
            checkLocationPermission(mContext);
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClick");
        for (Map.Entry<Restaurant, Marker> entry : mRestaurants.entrySet()) {
            if (marker.getTag() == entry.getValue().getTag()) {
                Restaurant restaurant = entry.getKey();
                Intent intent = IntentUtils.loadRestaurantDataIntoIntent(
                        mContext, RestaurantDetailsActivity.class, restaurant.getPlaceId(), restaurant.getPhoto());
                startActivity(intent);
                return true;
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
        mListenerRegistrations = new ArrayList<>();
        mBinding.mapViewFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationUtils.checkLocationSettings(
                        (AppCompatActivity) mContext, DEFAULT_INTERVAL, FASTEST_INTERVAL, RC_CHECK_SETTINGS);
                if (mDeviceLocation != null) {
                    animateCamera(mDeviceLocation, DEFAULT_ZOOM, mGoogleMap);
                } else {
                    Toast.makeText(mContext, R.string.get_location_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initObservers() {
        Log.d(TAG, "initObservers");

        mViewModel.getRestaurants().observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                Log.d(TAG, "getRestaurants: onChanged");

                for (ListenerRegistration registration : mListenerRegistrations) {
                    registration.remove();
                }
                mListenerRegistrations.clear();
                mGoogleMap.clear();
                mRestaurants.clear();
                for (Restaurant restaurant : restaurants) {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(restaurant.getLocation().getLatitude(),
                                    restaurant.getLocation().getLongitude()));
                    Marker marker = mGoogleMap.addMarker(markerOptions);
                    marker.setTag(restaurant.getPlaceId());
                    marker.setVisible(false);

                    ListenerRegistration registration =
                            mViewModel.loadWorkmatesInRestaurants(restaurant.getPlaceId())
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                                            if (snapshot != null && e == null) {
                                                Log.d(TAG, "getRestaurants: added EventListener to : " + restaurant.getName());
                                                if (snapshot.size() != 0) {
                                                    marker.setIcon(getBitmapDescriptor(mContext, R.drawable.ic_restaurant_marker_cyan));
                                                } else {
                                                    marker.setIcon(getBitmapDescriptor(mContext, R.drawable.ic_restaurant_marker_orange));
                                                }
                                                marker.setVisible(true);
                                            }
                                        }
                                    });
                    mListenerRegistrations.add(registration);
                    mRestaurants.put(restaurant, marker);
                }
            }
        });

        mViewModel.getDeviceLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                Log.d(TAG, "getDeviceLocation: onChanged");

                if (mDeviceLocation == null) {
                    animateCamera(location, DEFAULT_ZOOM, mGoogleMap);
                }
                mDeviceLocation = location;
            }
        });

        mViewModel.getLocationActivated().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Log.d(TAG, "getLocationActivated: onChanged");

                if (mGoogleMap != null) {
                    checkLocationPermission(mContext);
                    mGoogleMap.setMyLocationEnabled(aBoolean);
                    for (Map.Entry<Restaurant, Marker> entry : mRestaurants.entrySet()) {
                        entry.getValue().setVisible(aBoolean);
                    }
                }
            }
        });
    }

    private void checkLocationPermission(Context context) {
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

    private BitmapDescriptor getBitmapDescriptor(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable = (VectorDrawable) ContextCompat.getDrawable(context, id);
            if (vectorDrawable != null) {
                int h = vectorDrawable.getIntrinsicHeight();
                int w = vectorDrawable.getIntrinsicWidth();

                vectorDrawable.setBounds(0, 0, w, h);

                Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bm);
                vectorDrawable.draw(canvas);

                return BitmapDescriptorFactory.fromBitmap(bm);
            } else {
                return null;
            }
        } else {
            return BitmapDescriptorFactory.fromResource(id);
        }
    }

    private void closeKeyboard() {
        Log.d(TAG, "closeKeyboard");

        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) requireActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
