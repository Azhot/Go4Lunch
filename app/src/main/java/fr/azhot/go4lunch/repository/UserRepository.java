package fr.azhot.go4lunch.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import fr.azhot.go4lunch.model.Restaurant;
import fr.azhot.go4lunch.model.User;

import static fr.azhot.go4lunch.util.AppConstants.CHOSEN_RESTAURANT_ID_FIELD;
import static fr.azhot.go4lunch.util.AppConstants.CHOSEN_RESTAURANT_NAME_FIELD;
import static fr.azhot.go4lunch.util.AppConstants.USER_COLLECTION_NAME;

public class UserRepository {


    // private static
    private static final String TAG = "UserRepository";
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
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;


    // constructor
    private UserRepository() {
        this.mFirebaseFirestore = FirebaseFirestore.getInstance();
        this.mCollectionReference = mFirebaseFirestore.collection(USER_COLLECTION_NAME);
    }


    // methods
    public Task<Void> createUser(User user) {
        Log.d(TAG, "createUser");

        return mCollectionReference
                .document(user.getUid())
                .set(user);
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

    public Task<Void> updateUserChosenRestaurant(User user) {
        Log.d(TAG, "updateUserChosenRestaurant");

        return mCollectionReference
                .document(user.getUid())
                .set(user, SetOptions.mergeFields(CHOSEN_RESTAURANT_ID_FIELD, CHOSEN_RESTAURANT_NAME_FIELD));
    }

    public Query loadWorkmatesInRestaurants(Restaurant restaurant) {
        Log.d(TAG, "loadWorkmatesInRestaurants");

        return mCollectionReference
                .whereEqualTo(CHOSEN_RESTAURANT_ID_FIELD, restaurant.getPlaceId());
    }
}
