package fr.azhot.go4lunch.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import fr.azhot.go4lunch.api.GoogleMapsApi;
import fr.azhot.go4lunch.model.NearbySearch;
import fr.azhot.go4lunch.service.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {


    // private static
    private static final String TAG = "RestaurantRepository";
    private static RestaurantRepository RESTAURANT_REPOSITORY;


    // public static
    public static RestaurantRepository getInstance() {
        Log.d(TAG, "getInstance");

        if (RESTAURANT_REPOSITORY == null) {
            RESTAURANT_REPOSITORY = new RestaurantRepository();
        }
        return RESTAURANT_REPOSITORY;
    }


    // variables
    private final GoogleMapsApi mGoogleMapsApi;
    private final MutableLiveData<NearbySearch> mNearbyRestaurants = new MutableLiveData<>();


    // constructor
    private RestaurantRepository() {
        mGoogleMapsApi = RetrofitService.createService(GoogleMapsApi.class);
    }

    public void setNearbyRestaurants(String location, int radius) {
        Log.d(TAG, "setNearbyRestaurants");

        Call<NearbySearch> restaurants = mGoogleMapsApi.getNearbyRestaurants(location, radius);
        restaurants.enqueue(new Callback<NearbySearch>() {
            @Override
            public void onResponse(@NonNull Call<NearbySearch> call, @NonNull Response<NearbySearch> response) {
                Log.d(TAG, "setNearbyRestaurants: onResponse");

                mNearbyRestaurants.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<NearbySearch> call, @NonNull Throwable t) {
                Log.d(TAG, "setNearbyRestaurants: onFailure");

                // todo : question to Virgil : why postValue ? are we not on the main Thread ?
                mNearbyRestaurants.postValue(null);
            }
        });
    }

    public LiveData<NearbySearch> getNearbyRestaurants() {
        Log.d(TAG, "getNearbyRestaurants");

        return mNearbyRestaurants;
    }
}
