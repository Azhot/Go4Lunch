package fr.azhot.go4lunch.api;

import fr.azhot.go4lunch.BuildConfig;
import fr.azhot.go4lunch.model.DetailsPOJO;
import fr.azhot.go4lunch.model.NearbySearchPOJO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapsApi {

    String queryPlaceNearbySearch = "maps/api/place/nearbysearch/json?key=" + BuildConfig.GOOGLE_API_KEY;
    String queryPlaceDetails = "maps/api/place/details/json?" +
            "fields=place_id,name,vicinity,photos,rating,geometry,international_phone_number,website" +
            "&key=" + BuildConfig.GOOGLE_API_KEY;

    @GET(queryPlaceNearbySearch)
    Call<NearbySearchPOJO> getNearbySearch(@Query("keyword") String keyword, @Query("location") String location, @Query("radius") int radius);

    @GET(queryPlaceDetails)
    Call<DetailsPOJO> getDetails(@Query("place_id") String placeId);
}
