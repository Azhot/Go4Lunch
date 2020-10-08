package fr.azhot.go4lunch.viewmodel;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import fr.azhot.go4lunch.model.NearbySearch;
import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.repository.RestaurantRepository;
import fr.azhot.go4lunch.repository.UserRepository;

public class AppViewModel extends ViewModel {


    // private static
    private static final String TAG = "AppViewModel";


    // variables
    private final RestaurantRepository mRestaurantRepository;
    private final UserRepository mUserRepository;
    private MutableLiveData<Location> deviceLocation = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLocationActivated = new MutableLiveData<>();


    // constructors
    public AppViewModel() {
        mRestaurantRepository = RestaurantRepository.getInstance();
        mUserRepository = UserRepository.getInstance();
    }


    // methods
    public void setNearbyRestaurants(String location, int radius) {
        Log.d(TAG, "setNearbyRestaurants");

        mRestaurantRepository.setNearbyRestaurants(location, radius);
    }

    public LiveData<NearbySearch> getNearbyRestaurants() {
        Log.d(TAG, "getNearbyRestaurants");

        return mRestaurantRepository.getNearbyRestaurants();
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
