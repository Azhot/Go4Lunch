package fr.azhot.go4lunch;

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
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import fr.azhot.go4lunch.databinding.FragmentMapViewBinding;

@SuppressWarnings("MissingPermission") // ok since permissions are forced to the user @ onResume
public class MapViewFragment extends Fragment implements OnMapReadyCallback {


    // private static
    private static final String TAG = "MapViewFragment";
    private static final float DEFAULT_ZOOM = 15f;
    private static final long DEFAULT_INTERVAL = 60 * 1000;


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
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        checkPermissions(); // should NOT be remove without removing @SuppressWarnings("MissingPermission")
        // todo : should check that gps is turn on and if not, ask user to turn it on https://developer.android.com/training/location/change-location-settings
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
        mGoogleMap = googleMap;
        getDeviceLocation();
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
    }


    // methods
    private void init(LayoutInflater inflater) {
        mBinding = FragmentMapViewBinding.inflate(inflater);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });
    }

    private void checkPermissions() {
        if (!Permissions.isLocationPermissionGranted(mContext)) {
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
                            moveCamera(task.getResult(), DEFAULT_ZOOM);
                        } else {
                            Log.d(TAG, "onComplete: current location is null ");
                            Toast.makeText(mContext, "Error: unable to get current location.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void moveCamera(Location location, float zoom) {
        Log.d(TAG, "moveCamera");
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()),
                zoom));
    }

    private void closeKeyboard() {

        View view = requireActivity().getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
