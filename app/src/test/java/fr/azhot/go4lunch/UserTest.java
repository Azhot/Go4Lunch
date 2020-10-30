package fr.azhot.go4lunch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.azhot.go4lunch.model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit test on User model
 */
@RunWith(JUnit4.class)
public class UserTest {

    private static final String UID = "1234567890";
    private static final String NAME = "testName";
    private static final String EMAIL = "test@test.test";
    private static final String URL_PICTURE = "testUrlPicture";
    private static final String SELECTED_RESTAURANT_ID = "testSelectedRestaurantId";
    private static final String SELECTED_RESTAURANT_NAME = "testSelectedRestaurantName";
    private static final List<String> LIKED_RESTAURANTS = new ArrayList<>(Collections.singleton("0987654321"));

    @Test
    public void createUserWithSuccess() {
        User user = new User(UID, NAME, EMAIL, URL_PICTURE);
        assertNotNull(user);
    }

    @Test
    public void getUserDetails() {
        User user = new User(UID, NAME, EMAIL, URL_PICTURE);
        user.setSelectedRestaurantId(SELECTED_RESTAURANT_ID);
        user.setSelectedRestaurantName(SELECTED_RESTAURANT_NAME);
        user.setLikedRestaurants(LIKED_RESTAURANTS);
        assertEquals(UID, user.getUid());
        assertEquals(NAME, user.getName());
        assertEquals(EMAIL, user.getEmail());
        assertEquals(URL_PICTURE, user.getUrlPicture());
        assertEquals(SELECTED_RESTAURANT_ID, user.getSelectedRestaurantId());
        assertEquals(SELECTED_RESTAURANT_NAME, user.getSelectedRestaurantName());
        assertEquals(LIKED_RESTAURANTS, user.getLikedRestaurants());
    }
}
