package fr.azhot.go4lunch.viewmodel;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.List;

import fr.azhot.go4lunch.model.DetailsPOJO;
import fr.azhot.go4lunch.model.NearbySearchPOJO;
import fr.azhot.go4lunch.model.Restaurant;
import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.repository.GooglePlaceRepository;
import fr.azhot.go4lunch.repository.UserRepository;

public class AppViewModel extends ViewModel {


    // private static
    private static final String TAG = AppViewModel.class.getSimpleName();


    // variables
    private final GooglePlaceRepository mGooglePlaceRepository;
    private final UserRepository mUserRepository;
    private MutableLiveData<Location> mDeviceLocation;
    private MutableLiveData<Boolean> mIsLocationActivated;
    private MutableLiveData<AutocompletePrediction> mAutocompletePrediction;


    // constructors
    public AppViewModel() {
        mGooglePlaceRepository = GooglePlaceRepository.getInstance();
        mUserRepository = UserRepository.getInstance();
        mDeviceLocation = new MutableLiveData<>();
        mIsLocationActivated = new MutableLiveData<>();
        mAutocompletePrediction = new MutableLiveData<>();
    }


    // methods
    public LiveData<NearbySearchPOJO> getNearbySearchPOJO() {
        Log.d(TAG, "getNearbySearchPOJO");

        return mGooglePlaceRepository.getNearbySearchPOJO();
    }

    public void setNearbySearchPOJO(String keyword, String location, int radius) {
        Log.d(TAG, "setNearbySearchPOJO");

        mGooglePlaceRepository.setNearbySearchPOJO(keyword, location, radius);
    }

    public LiveData<List<Restaurant>> getNearbyRestaurants() {
        Log.d(TAG, "getNearbyRestaurants");

        return mGooglePlaceRepository.getNearbyRestaurants();
    }

    public void setNearbyRestaurants(NearbySearchPOJO nearbySearchPOJO) {
        Log.d(TAG, "setNearbyRestaurants");

        mGooglePlaceRepository.setNearbyRestaurants(nearbySearchPOJO);
    }

    public void loadRestaurantsPhotos(List<Restaurant> restaurants, RequestManager glide) {
        Log.d(TAG, "loadRestaurantsPhotos");

        mGooglePlaceRepository.loadRestaurantsPhotos(restaurants, glide);
    }

    public LiveData<DetailsPOJO> getDetailsPOJO() {
        Log.d(TAG, "getDetailsPOJO");

        return mGooglePlaceRepository.getDetailsPOJO();
    }

    public void setDetailsPOJO(String placeId) {
        Log.d(TAG, "setDetailsPOJO");

        mGooglePlaceRepository.setDetailsPOJO(placeId);
    }

    public LiveData<AutocompletePrediction> getAutocompletePrediction() {
        return mAutocompletePrediction;
    }

    public void setAutocompletePrediction(AutocompletePrediction autocompletePrediction) {
        mAutocompletePrediction.setValue(autocompletePrediction);
    }

    public LiveData<Location> getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation");

        return mDeviceLocation;
    }

    public void setDeviceLocation(Location location) {
        Log.d(TAG, "setDeviceLocation");

        mDeviceLocation.setValue(location);
    }

    public LiveData<Boolean> getLocationActivated() {
        Log.d(TAG, "getLocationActivated");

        return mIsLocationActivated;
    }

    public void setLocationActivated(boolean b) {
        Log.d(TAG, "setLocationActivated");

        mIsLocationActivated.setValue(b);
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
