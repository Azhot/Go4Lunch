package fr.azhot.go4lunch.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import fr.azhot.go4lunch.POJO.NearbySearch;
import fr.azhot.go4lunch.repository.RestaurantRepository;

public class RestaurantViewModel extends ViewModel {

    private static final String TAG = "RestaurantViewModel";

    private RestaurantRepository mRestaurantRepository;

    public RestaurantViewModel() {
        mRestaurantRepository = RestaurantRepository.getInstance();
    }

    public LiveData<NearbySearch> getNearbyRestaurants(String location, int radius) {
        Log.d(TAG, "getNearbyRestaurants");

        return mRestaurantRepository.getNearbyRestaurants(location, radius);
    }
}
