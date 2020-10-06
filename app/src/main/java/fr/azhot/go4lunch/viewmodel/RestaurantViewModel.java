package fr.azhot.go4lunch.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import fr.azhot.go4lunch.model.NearbySearch;
import fr.azhot.go4lunch.repository.RestaurantRepository;

public class RestaurantViewModel extends ViewModel {


    // private static
    private static final String TAG = "RestaurantViewModel";


    // variables
    private RestaurantRepository mRestaurantRepository;


    // constructors
    public RestaurantViewModel() {
        mRestaurantRepository = RestaurantRepository.getInstance();
    }


    // methods
    public LiveData<NearbySearch> getNearbyRestaurants(String location, int radius) {
        Log.d(TAG, "getNearbyRestaurants");

        return mRestaurantRepository.getNearbyRestaurants(location, radius);
    }
}
