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
    public Task<Void> createOrUpdateUser(User user) {
        Log.d(TAG, "createOrUpdateUser");

        return mCollectionReference
                .document(user.getUid())
                .set(user);
    }

    public Task<Void> updateUserRestaurantChoice(User user) {
        Log.d(TAG, "updateUserRestaurantChoice");

        return mCollectionReference
                .document(user.getUid())
                .update(SELECTED_RESTAURANT_ID_FIELD, user.getSelectedRestaurantId(),
                        SELECTED_RESTAURANT_NAME_FIELD, user.getSelectedRestaurantName());
    }

    public Task<Void> updateUserLikedRestaurant(User user) {
        Log.d(TAG, "updateUserLikedRestaurants");

        return mCollectionReference
                .document(user.getUid())
                .update(LIKED_RESTAURANTS_ID_FIELD, user.getLikedRestaurants());
    }

    public Task<Void> updateUserName(String uid, String name) {
        Log.d(TAG, "updateUserName");

        return mCollectionReference
                .document(uid)
                .update(NAME_ID_FIELD, name);
    }

    public Task<Void> updateUserPicture(String uid, String urlPicture) {
        Log.d(TAG, "updateUserPicture");

        return mCollectionReference
                .document(uid)
                .update(URL_PICTURE_ID_FIELD, urlPicture);
    }

    public Task<DocumentSnapshot> getUser(String uid) {
        Log.d(TAG, "getUser");

        return mCollectionReference
                .document(uid)
                .get();
    }

    public Query getUsersQuery() {
        Log.d(TAG, "getUsersQuery");

        return mCollectionReference;
    }

    public Query loadWorkmatesInRestaurants(String placeId) {
        Log.d(TAG, "loadWorkmatesInRestaurants");

        return mCollectionReference
                .whereEqualTo(SELECTED_RESTAURANT_ID_FIELD, placeId);
    }
}
