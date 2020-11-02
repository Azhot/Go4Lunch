package fr.azhot.go4lunch.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import fr.azhot.go4lunch.api.GoogleMapsApi;
import fr.azhot.go4lunch.model.AutocompletePOJO;
import fr.azhot.go4lunch.model.DetailsPOJO;
import fr.azhot.go4lunch.model.NearbySearchPOJO;
import fr.azhot.go4lunch.model.Restaurant;
import fr.azhot.go4lunch.service.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class which will make use of the {@link GoogleMapsApi} in order to return
 * models used by the Go4Lunch app.
 */
public class GooglePlaceRepository {


    // private static
    private static final String TAG = GooglePlaceRepository.class.getSimpleName();
    private static GooglePlaceRepository GOOGLE_PLACE_REPOSITORY;


    // variables
    private final GoogleMapsApi mGoogleMapsApi;
    private final MutableLiveData<List<Restaurant>> mNearbyRestaurants;
    private final MutableLiveData<Restaurant> mDetailsRestaurant;
    private final MutableLiveData<List<AutocompletePOJO.Prediction>> mAutocompletePredictions;


    // constructor
    private GooglePlaceRepository() {
        mGoogleMapsApi = RetrofitService.createService(GoogleMapsApi.class);
        mNearbyRestaurants = new MutableLiveData<>();
        mDetailsRestaurant = new MutableLiveData<>();
        mAutocompletePredictions = new MutableLiveData<>();
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

    /**
     * @return a LiveData of a List of {@link Restaurant} returned by the
     * Google Places Nearby Search API.
     */
    public LiveData<List<Restaurant>> getNearbyRestaurantsLiveData() {
        Log.d(TAG, "getNearbyRestaurantsLiveData");

        return mNearbyRestaurants;
    }

    /**
     * Makes-up a call to the Google Places Nearby Search API in order to set-up a LiveData of a
     * List of {@link Restaurant} which can then be observed.
     *
     * @see GoogleMapsApi#getNearbySearch for parameters info.
     */
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

    /**
     * @return a LiveData of a {@link Restaurant} returned by the
     * Google Places Details API.
     */
    public LiveData<Restaurant> getDetailsRestaurantLiveData() {
        Log.d(TAG, "getDetailsRestaurantLiveData");

        return mDetailsRestaurant;
    }

    /**
     * Makes-up a call to the Google Places Details API in order to set-up a LiveData of a
     * {@link Restaurant} which can then be observed.
     *
     * @see GoogleMapsApi#getDetails for parameters info.
     */
    public void setDetailsRestaurantLiveData(String placeId) {
        Log.d(TAG, "setDetailsRestaurantLiveData");

        if (placeId == null) {
            mDetailsRestaurant.setValue(null);
            return;
        }

        Call<DetailsPOJO> placeDetails = mGoogleMapsApi.getDetails(placeId);
        placeDetails.enqueue(new Callback<DetailsPOJO>() {
            @Override
            public void onResponse(@NonNull Call<DetailsPOJO> call, @NonNull Response<DetailsPOJO> response) {
                Log.d(TAG, "setDetailsRestaurantLiveData: onResponse");

                if (response.body() != null && response.body().getResult() != null) {
                    mDetailsRestaurant.setValue(new Restaurant(response.body().getResult()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<DetailsPOJO> call, @NonNull Throwable t) {
                Log.e(TAG, "setDetailsRestaurantLiveData: onFailure", t);

                mDetailsRestaurant.postValue(null);
            }
        });
    }

    /**
     * @return a LiveData of a List of {@link AutocompletePOJO.Prediction}
     * returned by the Google Places Autocomplete API.
     */
    public LiveData<List<AutocompletePOJO.Prediction>> getAutocompletePredictionsLiveData() {
        Log.d(TAG, "getAutocompletePredictionsLiveData");

        return mAutocompletePredictions;
    }

    /**
     * Makes-up a call to the Google Places Autocomplete API in order to set-up a LiveData of a List
     * of {@link AutocompletePOJO.Prediction} which can then be observed.
     *
     * @see GoogleMapsApi#getAutocomplete for parameters info.
     */
    public void setAutocompletePredictionsLiveData(String input, String types, String location, int radius, String sessionToken) {
        Log.d(TAG, "setAutocompletePredictionsLiveData");

        Call<AutocompletePOJO> placeDetails = mGoogleMapsApi.getAutocomplete(input, types, location, radius, sessionToken);
        placeDetails.enqueue(new Callback<AutocompletePOJO>() {
            @Override
            public void onResponse(@NonNull Call<AutocompletePOJO> call, @NonNull Response<AutocompletePOJO> response) {
                Log.d(TAG, "setAutocompletePredictionsLiveData: onResponse");

                if (response.body() != null) {
                    mAutocompletePredictions.setValue(response.body().getPredictions());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AutocompletePOJO> call, @NonNull Throwable t) {
                Log.e(TAG, "setAutocompletePredictionsLiveData: onFailure", t);

                mAutocompletePredictions.postValue(null);
            }
        });
    }

    /**
     * Makes-up a call to the Google Places Details API which will return a
     * {@link Restaurant} that can then be retrieved using
     * an {@link OnCompleteListener}.
     *
     * @see GoogleMapsApi#getDetails for parameters info.
     */
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

    /**
     * Listener used in order to get a {@link Restaurant} from a
     * Google Places Details API call.
     */
    public interface OnCompleteListener {
        void onSuccess(Restaurant restaurant);

        void onFailure();
    }
}
