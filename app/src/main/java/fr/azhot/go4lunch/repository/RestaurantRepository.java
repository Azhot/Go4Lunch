package fr.azhot.go4lunch.repository;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import fr.azhot.go4lunch.model.NearbyRestaurantsPOJO;
import fr.azhot.go4lunch.model.Restaurant;
import fr.azhot.go4lunch.service.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {


    // private static
    private static final String TAG = "NearbyRestaurantsRepo";
    private static RestaurantRepository RESTAURANT_REPOSITORY;


    // variables
    private final GoogleMapsApi mGoogleMapsApi;
    private final MutableLiveData<NearbyRestaurantsPOJO> mNearbyRestaurantsPOJO;
    private final MutableLiveData<Restaurant> mRestaurant;
    private List<Restaurant> mExistingRestaurants;


    // constructor
    private RestaurantRepository() {
        mGoogleMapsApi = RetrofitService.createService(GoogleMapsApi.class);
        mNearbyRestaurantsPOJO = new MutableLiveData<>();
        mRestaurant = new MutableLiveData<>();
        mExistingRestaurants = new ArrayList<>();
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

    public LiveData<Restaurant> getRestaurant() {
        Log.d(TAG, "getRestaurant");

        return mRestaurant;
    }

    public void setRestaurants(NearbyRestaurantsPOJO nearbyRestaurantsPOJO, RequestManager glide) {
        Log.d(TAG, "setRestaurants");

        boolean isExisting;

        for (NearbyRestaurantsPOJO.Result result : nearbyRestaurantsPOJO.getResults()) {
            isExisting = false;
            for (Restaurant restaurant : mExistingRestaurants) {
                if (restaurant.getPlaceId().equals(result.getPlaceId())) {
                    isExisting = true;
                    break;
                }
            }

            if (!isExisting) {
                if (result.getPhotos() != null) {
                    String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                            "key=" + BuildConfig.GOOGLE_API_KEY +
                            "&photoreference=" + result.getPhotos().get(0).getPhotoReference() +
                            "&maxwidth=400";

                    Log.d(TAG, "setRestaurants: " + result.getName() + ", downloaded photo : " + photoUrl);

                    glide.asBitmap()
                            .load(photoUrl)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    Restaurant restaurant = new Restaurant(result, resource);
                                    mExistingRestaurants.add(restaurant);
                                    mRestaurant.setValue(restaurant);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });
                } else {
                    Log.d(TAG, "setRestaurants: " + result.getName() + ", no photo");

                    glide.asBitmap()
                            .load(R.drawable.ic_no_image)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    Restaurant restaurant = new Restaurant(result, resource);
                                    mExistingRestaurants.add(restaurant);
                                    mRestaurant.setValue(restaurant);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });
                }
            }
        }
    }

    public List<Restaurant> getExistingRestaurants() {
        return mExistingRestaurants;
    }
}
