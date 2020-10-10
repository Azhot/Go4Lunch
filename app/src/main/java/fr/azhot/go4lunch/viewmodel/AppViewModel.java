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

    public List<NearbyRestaurantsPOJO.Result> getPreviousResults() {
        Log.d(TAG, "getPreviousResults");

        return mRestaurantRepository.getPreviousResults();
    }

    public void setPreviousResults(List<NearbyRestaurantsPOJO.Result> previousResults) {
        Log.d(TAG, "setPreviousResults");

        mRestaurantRepository.setPreviousResults(previousResults);
    }

    public LiveData<Restaurant> getRestaurant() {
        Log.d(TAG, "getRestaurant");

        return mRestaurantRepository.getRestaurant();
    }

    public List<Restaurant> getRestaurants() {
        Log.d(TAG, "getRestaurants");

        return mRestaurantRepository.getRestaurants();
    }

    public void setRestaurants(NearbyRestaurantsPOJO nearbyRestaurantsPOJO, RequestManager glide) {
        Log.d(TAG, "setRestaurants");

        mRestaurantRepository.setRestaurants(nearbyRestaurantsPOJO, glide);
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

    public void createUser(User user) {
        Log.d(TAG, "createUser: " + user.getName());

        mUserRepository.createUser(user);
    }

    public Task<DocumentSnapshot> getUser(String uid) {
        Log.d(TAG, "getUser with uid: " + uid);

        return mUserRepository.getUser(uid);
    }

    public Query getUsersQuery() {
        Log.d(TAG, "getUsersQuery");

        return mUserRepository.getUsersQuery();
    }

    public void updateUserChosenRestaurant(User user) {
        Log.d(TAG, "updateUserChosenRestaurant: " + user.getName());

        mUserRepository.updateUserChosenRestaurant(user);
    }
}
