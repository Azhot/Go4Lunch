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

    private static final String TAG = "RestaurantRepository";
    private static RestaurantRepository mRestaurantRepository;
    private GoogleMapsApi mGoogleMapsApi;

    private RestaurantRepository() {
        mGoogleMapsApi = RetrofitService.createService(GoogleMapsApi.class);
    }

    public static RestaurantRepository getInstance() {
        Log.d(TAG, "getInstance");

        if (mRestaurantRepository == null) {
            mRestaurantRepository = new RestaurantRepository();
        }
        return mRestaurantRepository;
    }

    public LiveData<NearbySearch> getNearbyRestaurants(String location, int radius) {
        Log.d(TAG, "getNearbyRestaurants");

        final MutableLiveData<NearbySearch> restaurants = new MutableLiveData<>();
        mGoogleMapsApi.getNearbyRestaurants(location, radius).enqueue(new Callback<NearbySearch>() {
            @Override
            public void onResponse(@NonNull Call<NearbySearch> call, @NonNull Response<NearbySearch> response) {
                if (response.isSuccessful()) {
                    restaurants.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<NearbySearch> call, @NonNull Throwable t) {
                restaurants.setValue(null);
            }
        });
        return restaurants;
    }
}
