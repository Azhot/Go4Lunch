package fr.azhot.go4lunch.viewmodel;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import fr.azhot.go4lunch.model.NearbyRestaurantsPOJO;
import fr.azhot.go4lunch.model.Restaurant;
import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.repository.NearbyRestaurantsRepository;
import fr.azhot.go4lunch.repository.UserRepository;

public class AppViewModel extends ViewModel {


    // private static
    private static final String TAG = "AppViewModel";


    // variables
    private final NearbyRestaurantsRepository mNearbyRestaurantsRepository;
    private final UserRepository mUserRepository;
    private List<NearbyRestaurantsPOJO.Result> mPreviousResults;
    private List<Restaurant> mRestaurants;
    private MutableLiveData<Location> deviceLocation;
    private MutableLiveData<Boolean> isLocationActivated;


    // constructors
    public AppViewModel() {
        mNearbyRestaurantsRepository = NearbyRestaurantsRepository.getInstance();
        mUserRepository = UserRepository.getInstance();
        mPreviousResults = new ArrayList<>();
        mRestaurants = new ArrayList<>();
        deviceLocation = new MutableLiveData<>();
        isLocationActivated = new MutableLiveData<>();
    }


    // methods
    public void setNearbyRestaurants(String location, int radius) {
        Log.d(TAG, "setNearbyRestaurants");

        mNearbyRestaurantsRepository.setNearbyRestaurants(location, radius);
    }

    public LiveData<NearbyRestaurantsPOJO> getNearbyRestaurants() {
        Log.d(TAG, "getNearbyRestaurants");

        return mNearbyRestaurantsRepository.getNearbyRestaurants();
    }

    public List<NearbyRestaurantsPOJO.Result> getPreviousResults() {
        Log.d(TAG, "getPreviousResults");

        return mPreviousResults;
    }

    public void setPreviousResults(List<NearbyRestaurantsPOJO.Result> previousResults) {
        Log.d(TAG, "setPreviousResults");

        mPreviousResults = previousResults;
    }

    public List<Restaurant> getRestaurants() {
        Log.d(TAG, "getRestaurants");

        return mRestaurants;
    }

    public MutableLiveData<Location> getDeviceLocation() {
        Log.d(TAG, "getLocation");

        return deviceLocation;
    }

    public void setDeviceLocation(Location location) {
        Log.d(TAG, "setLocationActivated");

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
        Log.d(TAG, "updateUser: " + user.getName());

        mUserRepository.updateUserChosenRestaurant(user);
    }
}
