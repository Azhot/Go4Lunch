package fr.azhot.go4lunch.viewmodel;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.List;

import fr.azhot.go4lunch.model.NearbyRestaurantsPOJO;
import fr.azhot.go4lunch.model.Restaurant;
import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.repository.RestaurantRepository;
import fr.azhot.go4lunch.repository.UserRepository;

public class AppViewModel extends ViewModel {


    // private static
    private static final String TAG = "AppViewModel";


    // variables
    private final RestaurantRepository mRestaurantRepository;
    private final UserRepository mUserRepository;
    private MutableLiveData<Location> deviceLocation;
    private MutableLiveData<Boolean> isLocationActivated;


    // constructors
    public AppViewModel() {
        mRestaurantRepository = RestaurantRepository.getInstance();
        mUserRepository = UserRepository.getInstance();
        deviceLocation = new MutableLiveData<>();
        isLocationActivated = new MutableLiveData<>();
    }


    // methods
    public LiveData<NearbyRestaurantsPOJO> getNearbyRestaurantsPOJO() {
        Log.d(TAG, "getNearbyRestaurantsPOJO");

        return mRestaurantRepository.getNearbyRestaurantsPOJO();
    }

    public void setNearbyRestaurantsPOJO(String location, int radius) {
        Log.d(TAG, "setNearbyRestaurantsPOJO");

        mRestaurantRepository.setNearbyRestaurantsPOJO(location, radius);
    }

    public MutableLiveData<List<Restaurant>> getRestaurants() {
        return mRestaurantRepository.getRestaurants();
    }

    public void setRestaurants(NearbyRestaurantsPOJO nearbyRestaurantsPOJO) {
        mRestaurantRepository.setRestaurants(nearbyRestaurantsPOJO);
    }

    public void loadRestaurantsPhotos(List<Restaurant> restaurants, RequestManager glide) {
        mRestaurantRepository.loadRestaurantsPhotos(restaurants, glide);
    }

    public MutableLiveData<Location> getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation");

        return deviceLocation;
    }

    public void setDeviceLocation(Location location) {
        Log.d(TAG, "setDeviceLocation");

        deviceLocation.setValue(location);
    }

    public MutableLiveData<Boolean> getLocationActivated() {
        Log.d(TAG, "getLocationActivated");

        return isLocationActivated;
    }

    public void setLocationActivated(boolean b) {
        Log.d(TAG, "setLocationActivated");

        isLocationActivated.setValue(b);
    }

    public Task<Void> createOrUpdateUser(User user) {
        Log.d(TAG, "createOrUpdateUser");

        return mUserRepository.createOrUpdateUser(user);
    }

    public Task<DocumentSnapshot> getUser(String uid) {
        Log.d(TAG, "getUser");

        return mUserRepository.getUser(uid);
    }

    public Query getUsersQuery() {
        Log.d(TAG, "getUsersQuery");

        return mUserRepository.getUsersQuery();
    }

    public Query loadWorkmatesInRestaurants(String placeId) {
        Log.d(TAG, "loadWorkmatesInRestaurants");

        return mUserRepository.loadWorkmatesInRestaurants(placeId);
    }
}
