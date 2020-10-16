package fr.azhot.go4lunch.repository;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import fr.azhot.go4lunch.BuildConfig;
import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.api.GoogleMapsApi;
import fr.azhot.go4lunch.model.DetailsPOJO;
import fr.azhot.go4lunch.model.NearbyRestaurantsPOJO;
import fr.azhot.go4lunch.model.Restaurant;
import fr.azhot.go4lunch.service.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GooglePlaceRepository {


    // private static
    private static final String TAG = "GooglePlaceRepository";
    private static GooglePlaceRepository GOOGLE_PLACE_REPOSITORY;


    // variables
    private final GoogleMapsApi mGoogleMapsApi;
    private final MutableLiveData<NearbyRestaurantsPOJO> mNearbyRestaurantsPOJO;
    private final MutableLiveData<List<Restaurant>> mRestaurants;
    private final MutableLiveData<DetailsPOJO> mDetailsPOJO;


    // constructor
    private GooglePlaceRepository() {
        mGoogleMapsApi = RetrofitService.createService(GoogleMapsApi.class);
        mNearbyRestaurantsPOJO = new MutableLiveData<>();
        mRestaurants = new MutableLiveData<>();
        mDetailsPOJO = new MutableLiveData<>();
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
    public LiveData<NearbyRestaurantsPOJO> getNearbyRestaurantsPOJO() {
        Log.d(TAG, "getNearbyRestaurantsPOJO");

        return mNearbyRestaurantsPOJO;
    }

    public void setNearbyRestaurantsPOJO(String location, int radius) {
        Log.d(TAG, "setNearbyRestaurantsPOJO");

        Call<NearbyRestaurantsPOJO> nearbyRestaurants = mGoogleMapsApi.getNearbyRestaurants(location, radius);
        nearbyRestaurants.enqueue(new Callback<NearbyRestaurantsPOJO>() {
            @Override
            public void onResponse(@NonNull Call<NearbyRestaurantsPOJO> call, @NonNull Response<NearbyRestaurantsPOJO> response) {
                Log.d(TAG, "setNearbyRestaurantsPOJO: onResponse");

                mNearbyRestaurantsPOJO.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<NearbyRestaurantsPOJO> call, @NonNull Throwable t) {
                Log.d(TAG, "setNearbyRestaurantsPOJO: onFailure");

                mNearbyRestaurantsPOJO.postValue(null);
            }
        });
    }

    public LiveData<List<Restaurant>> getRestaurants() {
        return mRestaurants;
    }

    public void setRestaurants(NearbyRestaurantsPOJO nearbyRestaurantsPOJO) {
        List<Restaurant> restaurants = new ArrayList<>();

        for (NearbyRestaurantsPOJO.Result result : nearbyRestaurantsPOJO.getResults()) {
            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(result.getGeometry().getLocation().getLat());
            location.setLongitude(result.getGeometry().getLocation().getLng());

            Restaurant restaurant = new Restaurant(result, location);

            restaurants.add(restaurant);
        }

        mRestaurants.setValue(restaurants);
    }

    public void loadRestaurantsPhotos(List<Restaurant> restaurants, RequestManager glide) {
        int i = 1; // todo : DELETE AT THE END OF PROJECT
        for (Restaurant restaurant : restaurants) {
            if (restaurant.getPhotos() != null) {
                String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                        "key=" + BuildConfig.GOOGLE_API_KEY +
                        "&photoreference=" + restaurant.getPhotos().get(0).getPhotoReference() +
                        "&maxwidth=400";

                Log.d(TAG, "setRestaurants: " + restaurant.getName() + ", downloaded photo : " + photoUrl);

                glide.asBitmap()
                        .load("https://source.unsplash.com/random/400x400?sig=" + i++) // todo : REPLACE AT THE END OF PROJECT
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                restaurant.setPhoto(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            } else {
                glide.asBitmap()
                        .load(R.drawable.ic_no_image)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                restaurant.setPhoto(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
        }
    }

    public LiveData<DetailsPOJO> getDetailsPOJO() {
        Log.d(TAG, "getDetailsPOJO");

        return mDetailsPOJO;
    }

    public void setDetailsPOJO(String placeId) {
        Log.d(TAG, "setDetailsPOJO");

        Call<DetailsPOJO> placeDetails = mGoogleMapsApi.getPlaceDetails(placeId);
        placeDetails.enqueue(new Callback<DetailsPOJO>() {
            @Override
            public void onResponse(@NonNull Call<DetailsPOJO> call, @NonNull Response<DetailsPOJO> response) {
                Log.d(TAG, "setDetailsPOJO: onResponse");

                mDetailsPOJO.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<DetailsPOJO> call, @NonNull Throwable t) {
                Log.d(TAG, "setDetailsPOJO: onFailure");

                mDetailsPOJO.postValue(null);
            }
        });
    }
}
