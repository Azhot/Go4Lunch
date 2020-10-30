package fr.azhot.go4lunch;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Collections;

import fr.azhot.go4lunch.model.DetailsPOJO;
import fr.azhot.go4lunch.model.NearbySearchPOJO;
import fr.azhot.go4lunch.model.Restaurant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit test on Restaurant model
 */
@RunWith(JUnit4.class)
public class RestaurantTest {

    private static final String NAME = "testName";
    private static final String ID = "testId";
    private static final String VICINITY = "testVicinity";
    private static final Double LATITUDE = 12.345d;
    private static final Double LONGITUDE = 67.890d;
    private static final boolean OPEN_NOW = true;
    private static final Double RATING = 4.2d;
    private static final String PHOTO_REF = "testPhotoRef";
    private static final String PHONE_NUMBER = "1234567890";
    private static final String WEBSITE = "www.testWebsite.com";


    private DetailsPOJO.Result resultDetails;
    private NearbySearchPOJO.Result resultNearby;

    @Before
    public void setup() {
        resultDetails = new DetailsPOJO.Result();
        resultDetails.setName(NAME);
        resultDetails.setPlaceId(ID);
        resultDetails.setVicinity(VICINITY);
        DetailsPOJO.Geometry geometryDetails = new DetailsPOJO.Geometry();
        DetailsPOJO.Location locationDetails = new DetailsPOJO.Location();
        locationDetails.setLat(LATITUDE);
        locationDetails.setLng(LONGITUDE);
        geometryDetails.setLocation(locationDetails);
        resultDetails.setGeometry(geometryDetails);
        resultDetails.setRating(RATING);
        DetailsPOJO.Photo photoDetails = new DetailsPOJO.Photo();
        photoDetails.setPhotoReference(PHOTO_REF);
        resultDetails.setPhotos(new ArrayList<>(Collections.singleton(photoDetails)));
        resultDetails.setInternationalPhoneNumber(PHONE_NUMBER);
        resultDetails.setWebsite(WEBSITE);

        resultNearby = new NearbySearchPOJO.Result();
        resultNearby.setName(NAME);
        resultNearby.setPlaceId(ID);
        resultNearby.setVicinity(VICINITY);
        NearbySearchPOJO.Geometry geometryNearby = new NearbySearchPOJO.Geometry();
        NearbySearchPOJO.Location locationNearby = new NearbySearchPOJO.Location();
        locationNearby.setLat(LATITUDE);
        locationNearby.setLng(LONGITUDE);
        geometryNearby.setLocation(locationNearby);
        NearbySearchPOJO.OpeningHours openingHours = new NearbySearchPOJO.OpeningHours();
        openingHours.setOpenNow(OPEN_NOW);
        resultNearby.setOpeningHours(openingHours);
        resultNearby.setGeometry(geometryNearby);
        resultNearby.setRating(RATING);
        NearbySearchPOJO.Photo photoNearby = new NearbySearchPOJO.Photo();
        photoNearby.setPhotoReference(PHOTO_REF);
        resultNearby.setPhotos(new ArrayList<>(Collections.singleton(photoNearby)));
    }

    @Test
    public void createRestaurantWithSuccess() {
        Restaurant restaurantFromDetails = new Restaurant(resultDetails);
        Restaurant restaurantFromNearby = new Restaurant(resultNearby);
        assertNotNull(restaurantFromDetails);
        assertNotNull(restaurantFromNearby);
    }

    @Test
    public void getRestaurantDetailsFromDetails() {
        Restaurant restaurantFromDetails = new Restaurant(resultDetails);
        assertEquals(NAME, restaurantFromDetails.getName());
        assertEquals(ID, restaurantFromDetails.getPlaceId());
        assertEquals(VICINITY, restaurantFromDetails.getVicinity());
        assertEquals(LATITUDE, restaurantFromDetails.getLatitude(), 0.0);
        assertEquals(LONGITUDE, restaurantFromDetails.getLongitude(), 0.0);
        assertEquals(Math.round(RATING / 5 * 3), restaurantFromDetails.getRating(), 0);
        assertEquals(PHOTO_REF, restaurantFromDetails.getPhotoReference());
        assertEquals(PHONE_NUMBER, restaurantFromDetails.getPhoneNumber());
        assertEquals(WEBSITE, restaurantFromDetails.getWebsite());
    }

    @Test
    public void getRestaurantDetailsFromNearby() {
        Restaurant restaurantFromNearby = new Restaurant(resultNearby);
        assertEquals(NAME, restaurantFromNearby.getName());
        assertEquals(ID, restaurantFromNearby.getPlaceId());
        assertEquals(VICINITY, restaurantFromNearby.getVicinity());
        assertEquals(LATITUDE, restaurantFromNearby.getLatitude(), 0.0);
        assertEquals(LONGITUDE, restaurantFromNearby.getLongitude(), 0.0);
        assertEquals(OPEN_NOW, restaurantFromNearby.getOpen());
        assertEquals(Math.round(RATING / 5 * 3), restaurantFromNearby.getRating(), 0);
        assertEquals(PHOTO_REF, restaurantFromNearby.getPhotoReference());
    }
}
