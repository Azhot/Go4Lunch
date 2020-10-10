package fr.azhot.go4lunch.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import fr.azhot.go4lunch.api.GoogleMapsApi;
import fr.azhot.go4lunch.model.NearbyRestaurantsPOJO;
import fr.azhot.go4lunch.service.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyRestaurantsRepository {


    // private static
    private static final String TAG = "NearbyRestaurantsRepo";
    private static NearbyRestaurantsRepository RESTAURANT_REPOSITORY;
    // variables
    private final GoogleMapsApi mGoogleMapsApi;
    private final MutableLiveData<NearbyRestaurantsPOJO> mNearbyRestaurants = new MutableLiveData<>();

    // constructor
    private NearbyRestaurantsRepository() {
        mGoogleMapsApi = RetrofitService.createService(GoogleMapsApi.class);
    }

    // public static
    public static NearbyRestaurantsRepository getInstance() {
        Log.d(TAG, "getInstance");

        if (RESTAURANT_REPOSITORY == null) {
            RESTAURANT_REPOSITORY = new NearbyRestaurantsRepository();
        }
        return RESTAURANT_REPOSITORY;
    }

    public void setNearbyRestaurants(String location, int radius) {
        Log.d(TAG, "setNearbyRestaurants");

        Call<NearbyRestaurantsPOJO> nearbyRestaurants = mGoogleMapsApi.getNearbyRestaurants(location, radius);
        nearbyRestaurants.enqueue(new Callback<NearbyRestaurantsPOJO>() {
            @Override
            public void onResponse(@NonNull Call<NearbyRestaurantsPOJO> call, @NonNull Response<NearbyRestaurantsPOJO> response) {
                Log.d(TAG, "setNearbyRestaurants: onResponse");

                mNearbyRestaurants.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<NearbyRestaurantsPOJO> call, @NonNull Throwable t) {
                Log.d(TAG, "setNearbyRestaurants: onFailure");

                mNearbyRestaurants.postValue(null);
            }
        });
    }

    public LiveData<NearbyRestaurantsPOJO> getNearbyRestaurants() {
        Log.d(TAG, "getNearbyRestaurants");

        return mNearbyRestaurants;
    }
}
