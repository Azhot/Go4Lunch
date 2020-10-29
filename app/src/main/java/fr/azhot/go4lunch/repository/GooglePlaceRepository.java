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
    private final MutableLiveData<Restaurant> mDetailsRestaurantFromAutocomplete;


    // constructor
    private GooglePlaceRepository() {
        mGoogleMapsApi = RetrofitService.createService(GoogleMapsApi.class);
        mNearbyRestaurants = new MutableLiveData<>();
        mDetailsRestaurantFromAutocomplete = new MutableLiveData<>();
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
    public LiveData<List<Restaurant>> getNearbyRestaurantsLiveData() {
        Log.d(TAG, "getNearbyRestaurantsLiveData");

        return mNearbyRestaurants;
    }

    public void setNearbyRestaurantsLiveData(String keyword, String type, String location, int radius) {
        Log.d(TAG, "setNearbyRestaurantsLiveData");

        Call<NearbySearchPOJO> placeNearbySearch = mGoogleMapsApi.getNearbySearch(keyword, type, location, radius);
        placeNearbySearch.enqueue(new Callback<NearbySearchPOJO>() {
            @Override
            public void onResponse(@NonNull Call<NearbySearchPOJO> call, @NonNull Response<NearbySearchPOJO> response) {
                Log.d(TAG, "setNearbyRestaurantsLiveData: onResponse");

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
                Log.e(TAG, "setNearbyRestaurantsLiveData: onFailure", t);

                mNearbyRestaurants.postValue(null);
            }
        });
    }

    public LiveData<Restaurant> getDetailsRestaurantFromAutocompleteLiveData() {
        Log.d(TAG, "getDetailsRestaurantFromAutocompleteLiveData");

        return mDetailsRestaurantFromAutocomplete;
    }

    public void setDetailsRestaurantFromAutocompleteLiveData(String placeId) {
        Log.d(TAG, "setDetailsRestaurantFromAutocompleteLiveData");

        if (placeId == null) {
            mDetailsRestaurantFromAutocomplete.setValue(null);
            return;
        }

        Call<DetailsPOJO> placeDetails = mGoogleMapsApi.getDetails(placeId);
        placeDetails.enqueue(new Callback<DetailsPOJO>() {
            @Override
            public void onResponse(@NonNull Call<DetailsPOJO> call, @NonNull Response<DetailsPOJO> response) {
                Log.d(TAG, "setDetailsRestaurantFromAutocompleteLiveData: onResponse");

                if (response.body() != null && response.body().getResult() != null) {
                    mDetailsRestaurantFromAutocomplete.setValue(new Restaurant(response.body().getResult()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<DetailsPOJO> call, @NonNull Throwable t) {
                Log.e(TAG, "setDetailsRestaurantFromAutocompleteLiveData: onFailure", t);

                mDetailsRestaurantFromAutocomplete.postValue(null);
            }
        });
    }

    public void getDetailsRestaurant(String placeId, OnCompleteListener onCompleteListener) {
        Call<DetailsPOJO> placeDetails = mGoogleMapsApi.getDetails(placeId);
        placeDetails.enqueue(new Callback<DetailsPOJO>() {
            @Override
            public void onResponse(@NonNull Call<DetailsPOJO> call, @NonNull Response<DetailsPOJO> response) {
                Log.d(TAG, "getDetailsRestaurant: onResponse");

                if (response.body() != null) {
                    onCompleteListener.onSuccess(new Restaurant(response.body().getResult()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<DetailsPOJO> call, @NonNull Throwable t) {
                Log.e(TAG, "setDetailsPOJO: onFailure", t);

                onCompleteListener.onFailure();
            }
        });
    }

    public interface OnCompleteListener {
        void onSuccess(Restaurant restaurant);

        void onFailure();
    }
}
