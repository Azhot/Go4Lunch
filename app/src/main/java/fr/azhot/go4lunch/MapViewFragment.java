package fr.azhot.go4lunch;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fr.azhot.go4lunch.databinding.FragmentMapViewBinding;

import static android.content.Context.LOCATION_SERVICE;

public class MapViewFragment extends Fragment implements LocationListener {

    // public static

    /**
     * @return a new instance of MapViewFragment
     */
    public static MapViewFragment newInstance() {
        Log.d(TAG, "newInstance");
        MapViewFragment fragment = new MapViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    // private static
    private static final String TAG = "MapViewFragment";

    // variables
    private FragmentMapViewBinding mBinding;
    private Context mContext;
    private LocationManager mLocationManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mBinding = FragmentMapViewBinding.inflate(inflater);
        return mBinding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        checkPermissions();
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Log.d(TAG, "Location : " + latitude + "/" + longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled");
    }

    private void checkPermissions() {
        if (!Permissions.isLocationPermissionGranted(mContext)) {
            requireActivity().finish();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void loadMap() {
        Log.d(TAG, "loadMap");
        checkPermissions();
        // obtaining location depending on providers
        mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }
        if (mLocationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }
        if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }
    }
}
