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
    // variables
    private GoogleMapsApi mGoogleMapsApi;


    // constructor
    private RestaurantRepository() {
        mGoogleMapsApi = RetrofitService.createService(GoogleMapsApi.class);
    }

    // public static
    public static RestaurantRepository getInstance() {
        Log.d(TAG, "getInstance");

        if (RESTAURANT_REPOSITORY == null) {
            RESTAURANT_REPOSITORY = new RestaurantRepository();
        }
        return RESTAURANT_REPOSITORY;
    }

    // methods
    public LiveData<NearbySearch> getNearbyRestaurants(String location, int radius) {
        Log.d(TAG, "getNearbyRestaurants");

        MutableLiveData<NearbySearch> mutableLiveData = new MutableLiveData<>();
        mGoogleMapsApi.getNearbyRestaurants(location, radius).enqueue(new Callback<NearbySearch>() {
            @Override
            public void onResponse(@NonNull Call<NearbySearch> call, @NonNull Response<NearbySearch> response) {
                Log.d(TAG, "getNearbyRestaurants: onResponse");

                if (response.isSuccessful()) {
                    mutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<NearbySearch> call, @NonNull Throwable t) {
                Log.d(TAG, "getNearbyRestaurants: onFailure");

                mutableLiveData.setValue(null);
            }
        });
        return mutableLiveData;
    }
}
