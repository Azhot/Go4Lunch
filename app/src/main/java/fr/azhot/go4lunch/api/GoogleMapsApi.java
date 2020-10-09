package fr.azhot.go4lunch.api;

import fr.azhot.go4lunch.BuildConfig;
import fr.azhot.go4lunch.model.NearbyRestaurantsPOJO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapsApi {

    String queryNearbyRestaurants = "maps/api/place/nearbysearch/json?keyword=restaurant&key=" + BuildConfig.GOOGLE_API_KEY;

    @GET(queryNearbyRestaurants)
    Call<NearbyRestaurantsPOJO> getNearbyRestaurants(@Query("location") String location, @Query("radius") int radius);
}
