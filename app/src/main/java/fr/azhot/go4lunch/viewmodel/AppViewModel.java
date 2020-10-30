package fr.azhot.go4lunch.viewmodel;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.List;

import fr.azhot.go4lunch.model.AutocompletePOJO;
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
    private final MutableLiveData<Location> mDeviceLocation;
    private final MutableLiveData<Boolean> mIsLocationActivated;


    // constructors
    public AppViewModel() {
        mGooglePlaceRepository = GooglePlaceRepository.getInstance();
        mUserRepository = UserRepository.getInstance();
        mDeviceLocation = new MutableLiveData<>();
        mIsLocationActivated = new MutableLiveData<>();
    }


    // methods
    public LiveData<List<Restaurant>> getNearbyRestaurantsLiveData() {
        return mGooglePlaceRepository.getNearbyRestaurantsLiveData();
    }

    public void setNearbyRestaurantsLiveData(String keyword, String type, String location, int radius) {
        mGooglePlaceRepository.setNearbyRestaurantsLiveData(keyword, type, location, radius);
    }

    public LiveData<Restaurant> getDetailsRestaurantLiveData() {
        return mGooglePlaceRepository.getDetailsRestaurantLiveData();
    }

    public void setDetailsRestaurantLiveData(String placeId) {
        mGooglePlaceRepository.setDetailsRestaurantLiveData(placeId);
    }

    public LiveData<List<AutocompletePOJO.Prediction>> getAutocompletePredictionsLiveData() {
        return mGooglePlaceRepository.getAutocompletePredictionsLiveData();
    }

    public void setAutocompletePredictionsLiveData(String input, String types, String location, int radius, String sessionToken) {
        mGooglePlaceRepository.setAutocompletePredictionsLiveData(input, types, location, radius, sessionToken);
    }

    public void getRestaurantDetails(String placeId, GooglePlaceRepository.OnCompleteListener onCompleteListener) {
        mGooglePlaceRepository.getDetailsRestaurant(placeId, onCompleteListener);
    }

    public LiveData<Location> getDeviceLocationLiveData() {
        return mDeviceLocation;
    }

    public void setDeviceLocationLiveData(Location location) {
        mDeviceLocation.setValue(location);
    }

    public LiveData<Boolean> getLocationActivatedLiveData() {
        return mIsLocationActivated;
    }

    public void setLocationActivatedLiveData(boolean b) {
        mIsLocationActivated.setValue(b);
    }

    public Task<Void> createOrUpdateUser(User user) {
        return mUserRepository.createOrUpdateUser(user);
    }

    public Task<Void> updateUserRestaurantChoice(User user) {
        return mUserRepository.updateUserRestaurantChoice(user);
    }

    public Task<Void> updateUserName(String uid, String name) {
        return mUserRepository.updateUserName(uid, name);
    }

    public Task<Void> updateUserPicture(String uid, String urlPicture) {
        return mUserRepository.updateUserPicture(uid, urlPicture);
    }

    public Task<Void> updateUserLikedRestaurant(User user) {
        return mUserRepository.updateUserLikedRestaurant(user);
    }

    public Task<DocumentSnapshot> getUser(String uid) {
        return mUserRepository.getUser(uid);
    }

    public Query getUsersQuery() {
        return mUserRepository.getUsersQuery();
    }

    public Query loadWorkmatesInRestaurants(String placeId) {
        return mUserRepository.loadWorkmatesInRestaurants(placeId);
    }
}
