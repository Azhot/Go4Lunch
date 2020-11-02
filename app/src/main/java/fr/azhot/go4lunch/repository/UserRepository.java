package fr.azhot.go4lunch.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import fr.azhot.go4lunch.model.User;

import static fr.azhot.go4lunch.util.AppConstants.LIKED_RESTAURANTS_ID_FIELD;
import static fr.azhot.go4lunch.util.AppConstants.NAME_ID_FIELD;
import static fr.azhot.go4lunch.util.AppConstants.SELECTED_RESTAURANT_ID_FIELD;
import static fr.azhot.go4lunch.util.AppConstants.URL_PICTURE_ID_FIELD;

/**
 * Class which will make use of the {@link FirebaseFirestore} Go4Lunch project instance in order
 * to make CRUD operations with {@link User} objects.
 */
public class UserRepository {


    // private static
    private static final String TAG = UserRepository.class.getSimpleName();
    public static final String USER_COLLECTION_NAME = "users";
    public static final String SELECTED_RESTAURANT_NAME_FIELD = "selectedRestaurantName";
    private static UserRepository USER_REPOSITORY;


    // public static
    public static UserRepository getInstance() {
        Log.d(TAG, "getInstance");

        if (USER_REPOSITORY == null) {
            USER_REPOSITORY = new UserRepository();
        }
        return USER_REPOSITORY;
    }

    // variables
    private final CollectionReference mCollectionReference;


    // constructor
    private UserRepository() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        this.mCollectionReference = firebaseFirestore.collection(USER_COLLECTION_NAME);
    }


    // methods

    /**
     * Creates a new {@link User} in the project's Firebase Firestore.
     *
     * @param user the {@link User} instance to be created.
     * @return a {@link Task} to which can be passed Listeners for completion results.
     */
    public Task<Void> createOrUpdateUser(User user) {
        Log.d(TAG, "createOrUpdateUser");

        return mCollectionReference
                .document(user.getUid())
                .set(user);
    }

    /**
     * Updates a {@link User}'s selectedRestaurantId and selectedRestaurantName fields in the
     * project's Firebase Firestore.
     *
     * @param user the {@link User} instance to be updated.
     * @return a {@link Task} to which can be passed Listeners for completion results.
     */
    public Task<Void> updateUserRestaurantChoice(User user) {
        Log.d(TAG, "updateUserRestaurantChoice");

        return mCollectionReference
                .document(user.getUid())
                .update(SELECTED_RESTAURANT_ID_FIELD, user.getSelectedRestaurantId(),
                        SELECTED_RESTAURANT_NAME_FIELD, user.getSelectedRestaurantName());
    }

    /**
     * Updates a {@link User}'s likedRestaurants field in the project's Firebase Firestore.
     *
     * @param user the {@link User} instance to be updated.
     * @return a {@link Task} to which can be passed Listeners for completion results.
     */
    public Task<Void> updateUserLikedRestaurant(User user) {
        Log.d(TAG, "updateUserLikedRestaurants");

        return mCollectionReference
                .document(user.getUid())
                .update(LIKED_RESTAURANTS_ID_FIELD, user.getLikedRestaurants());
    }

    /**
     * Updates a {@link User}'s name field in the project's Firebase Firestore.
     *
     * @param uid  the unique identifier for the {@link User} to be updated.
     * @param name the name to apply.
     * @return a {@link Task} to which can be passed Listeners for completion results.
     */
    public Task<Void> updateUserName(String uid, String name) {
        Log.d(TAG, "updateUserName");

        return mCollectionReference
                .document(uid)
                .update(NAME_ID_FIELD, name);
    }

    /**
     * Updates a {@link User}'s urlPicture field in the project's Firebase Firestore.
     *
     * @param uid        the unique identifier for the {@link User} to be updated.
     * @param urlPicture the picture url to apply.
     * @return a {@link Task} to which can be passed Listeners for completion results.
     */
    public Task<Void> updateUserPicture(String uid, String urlPicture) {
        Log.d(TAG, "updateUserPicture");

        return mCollectionReference
                .document(uid)
                .update(URL_PICTURE_ID_FIELD, urlPicture);
    }

    /**
     * Retrieve a {@link User} instance from the project's Firebase Firestore.
     *
     * @param uid the unique identifier of the {@link User} to get.
     * @return a {@link Task} to which can be passed Listeners for completion results.
     */
    public Task<DocumentSnapshot> getUser(String uid) {
        Log.d(TAG, "getUser");

        return mCollectionReference
                .document(uid)
                .get();
    }

    /**
     * @return a {@link Query} of the Users collection in the project's Firebase Firestore.
     */
    public Query getUsersQuery() {
        Log.d(TAG, "getUsersQuery");

        return mCollectionReference;
    }

    /**
     * @param placeId a {@link fr.azhot.go4lunch.model.Restaurant} placeId.
     * @return a {@link Query} of the Users in the Users collection in the project's Firebase
     * Firestore that have selected the restaurant which id's is passed as parameter.
     */
    public Query loadWorkmatesInRestaurants(String placeId) {
        Log.d(TAG, "loadWorkmatesInRestaurants");

        return mCollectionReference
                .whereEqualTo(SELECTED_RESTAURANT_ID_FIELD, placeId);
    }
}
