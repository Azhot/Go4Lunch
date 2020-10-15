package fr.azhot.go4lunch.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import fr.azhot.go4lunch.model.User;

import static fr.azhot.go4lunch.util.AppConstants.SELECTED_RESTAURANT_ID_FIELD;
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
    public Task<Void> createOrUpdateUser(User user) {
        Log.d(TAG, "createOrUpdateUser");

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

    public Query loadWorkmatesInRestaurants(String placeId) {
        Log.d(TAG, "loadWorkmatesInRestaurants");

        return mCollectionReference
                .whereEqualTo(SELECTED_RESTAURANT_ID_FIELD, placeId);
    }
}
