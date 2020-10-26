package fr.azhot.go4lunch.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import fr.azhot.go4lunch.api.GoogleMapsApi;
import fr.azhot.go4lunch.model.DetailsPOJO;
import fr.azhot.go4lunch.model.NearbySearchPOJO;
import fr.azhot.go4lunch.model.Restaurant;
import fr.azhot.go4lunch.service.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GooglePlaceRepository {


    // private static
    private static final String TAG = GooglePlaceRepository.class.getSimpleName();
    private static GooglePlaceRepository GOOGLE_PLACE_REPOSITORY;


    // variables
    private final GoogleMapsApi mGoogleMapsApi;
    private final MutableLiveData<List<Restaurant>> mNearbyRestaurants;
    private final MutableLiveData<Restaurant> mDetailsRestaurant;


    // constructor
    private GooglePlaceRepository() {
        mGoogleMapsApi = RetrofitService.createService(GoogleMapsApi.class);
        mNearbyRestaurants = new MutableLiveData<>();
        mDetailsRestaurant = new MutableLiveData<>();
    }


    // public static
    public static GooglePlaceRepository getInstance() {
        Log.d(TAG, "getInstance");

        if (GOOGLE_PLACE_REPOSITORY == null) {
            GOOGLE_PLACE_REPOSITORY = new GooglePlaceRepository();
        }
        return GOOGLE_PLACE_REPOSITORY;
    }


    // methods
    public LiveData<List<Restaurant>> getNearbyRestaurants() {
        Log.d(TAG, "getNearbyRestaurants");

        return mNearbyRestaurants;
    }

    public void setNearbyRestaurants(String keyword, String type, String location, int radius) {
        Log.d(TAG, "setNearbyRestaurants");

        Call<NearbySearchPOJO> placeNearbySearch = mGoogleMapsApi.getNearbySearch(keyword, type, location, radius);
        placeNearbySearch.enqueue(new Callback<NearbySearchPOJO>() {
            @Override
            public void onResponse(@NonNull Call<NearbySearchPOJO> call, @NonNull Response<NearbySearchPOJO> response) {
                Log.d(TAG, "setNearbyRestaurants: onResponse");

                List<Restaurant> restaurants = new ArrayList<>();

                if (response.body() != null) {
                    for (NearbySearchPOJO.Result result : response.body().getResults()) {
                        Restaurant restaurant = new Restaurant(result);
                        restaurants.add(restaurant);
                    }
                }

                mNearbyRestaurants.setValue(restaurants);
            }

            @Override
            public void onFailure(@NonNull Call<NearbySearchPOJO> call, @NonNull Throwable t) {
                Log.e(TAG, "setNearbyRestaurants: onFailure", t);

                mNearbyRestaurants.postValue(null);
            }
        });
    }

    public LiveData<Restaurant> getDetailsRestaurant() {
        Log.d(TAG, "getDetailsRestaurant");

        return mDetailsRestaurant;
    }

    public void setDetailsRestaurant(String placeId) {
        Log.d(TAG, "setDetailsRestaurant");

        Call<DetailsPOJO> placeDetails = mGoogleMapsApi.getDetails(placeId);
        placeDetails.enqueue(new Callback<DetailsPOJO>() {
            @Override
            public void onResponse(@NonNull Call<DetailsPOJO> call, @NonNull Response<DetailsPOJO> response) {
                Log.d(TAG, "setDetailsRestaurant: onResponse");

                if (response.body() != null) {
                    mDetailsRestaurant.setValue(new Restaurant(response.body().getResult()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<DetailsPOJO> call, @NonNull Throwable t) {
                Log.e(TAG, "setDetailsPOJO: onFailure", t);

                mDetailsRestaurant.postValue(null);
            }
        });
    }
}
