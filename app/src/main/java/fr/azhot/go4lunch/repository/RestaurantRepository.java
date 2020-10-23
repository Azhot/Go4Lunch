package fr.azhot.go4lunch.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import fr.azhot.go4lunch.model.Restaurant;

import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_COLLECTION_NAME;

public class RestaurantRepository {


    // private static
    private static final String TAG = RestaurantRepository.class.getSimpleName();
    private static RestaurantRepository RESTAURANT_REPOSITORY;


    // variables
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mCollectionReference;


    // constructor
    private RestaurantRepository() {
        this.mFirebaseFirestore = FirebaseFirestore.getInstance();
        this.mCollectionReference = mFirebaseFirestore.collection(RESTAURANT_COLLECTION_NAME);
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
    public void createOrUpdateRestaurantFromNearby(Restaurant restaurant, AppCompatActivity activity) {
        Log.d(TAG, "createOrUpdateRestaurantFromNearby");

        mCollectionReference
                .document(restaurant.getPlaceId())
                .get()
                .addOnCompleteListener(activity, new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createOrUpdateRestaurantFromNearby: onSuccess");
                            if (task.getResult().exists()) {
                                Log.d(TAG, "createOrUpdateRestaurantFromNearby: exists");
                                mCollectionReference
                                        .document(restaurant.getPlaceId())
                                        .update("name", restaurant.getName(),
                                                "vicinity", restaurant.getVicinity(),
                                                "latitude", restaurant.getLatitude(),
                                                "longitude", restaurant.getLongitude(),
                                                "open", restaurant.getOpen(),
                                                "rating", restaurant.getRating());
                            } else {
                                Log.d(TAG, "createOrUpdateRestaurantFromNearby: does not exist");
                                mCollectionReference
                                        .document(restaurant.getPlaceId())
                                        .set(restaurant);
                            }
                        } else {
                            Log.e(TAG, "createOrUpdateRestaurantFromNearby: onFailure", task.getException());
                        }
                    }
                });
    }

    public void createOrUpdateRestaurantFromDetails(Restaurant restaurant, AppCompatActivity activity) {
        Log.d(TAG, "createOrUpdateRestaurantFromDetails");

        mCollectionReference
                .document(restaurant.getPlaceId())
                .get()
                .addOnCompleteListener(activity, new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createOrUpdateRestaurantFromDetails: onSuccess");
                            if (task.getResult().exists()) {
                                Log.d(TAG, "createOrUpdateRestaurantFromDetails: exists");
                                mCollectionReference
                                        .document(restaurant.getPlaceId())
                                        .update("name", restaurant.getName(),
                                                "vicinity", restaurant.getVicinity(),
                                                "latitude", restaurant.getLatitude(),
                                                "longitude", restaurant.getLongitude(),
                                                "rating", restaurant.getRating(),
                                                "phoneNumber", restaurant.getPhoneNumber(),
                                                "website", restaurant.getWebsite());
                            } else {
                                Log.d(TAG, "createOrUpdateRestaurantFromDetails: does not exist");
                                mCollectionReference
                                        .document(restaurant.getPlaceId())
                                        .set(restaurant);
                            }
                        } else {
                            Log.e(TAG, "createOrUpdateRestaurantFromDetails: onFailure", task.getException());
                        }
                    }
                });
    }

    public Task<DocumentSnapshot> getRestaurant(String placeId) {
        Log.d(TAG, "getRestaurant");

        return mCollectionReference
                .document(placeId)
                .get();
    }
}
