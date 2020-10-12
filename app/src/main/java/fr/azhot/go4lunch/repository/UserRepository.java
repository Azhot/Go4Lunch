package fr.azhot.go4lunch.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import fr.azhot.go4lunch.model.User;

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
    public void createUser(User user) {
        mCollectionReference
                .document(user.getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "createUser: onSuccess");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "createUser: onFailure" + e.getMessage());
                    }
                });
    }

    public Query getUsersQuery() {
        return mCollectionReference;
    }

    public Task<DocumentSnapshot> getUser(String uid) {
        return mCollectionReference
                .document(uid)
                .get();
    }

    public void updateUserChosenRestaurant(User user) {
        mCollectionReference
                .document(user.getUid())
                .set(user, SetOptions.mergeFields("chosenRestaurantId", "chosenRestaurantName"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "updateUser: onSuccess");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "updateUser: onFailure");
                    }
                });
    }
}
