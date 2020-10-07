package fr.azhot.go4lunch.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.repository.UserRepository;

public class UserViewModel extends ViewModel {


    // private static
    private static final String TAG = "UserViewModel";


    // variables
    private UserRepository mUserRepository;


    // constructors
    public UserViewModel() {
        mUserRepository = UserRepository.getInstance();
    }


    // methods
    public void createUser(User user) {
        Log.d(TAG, "createUser: " + user.getName());

        mUserRepository.createUser(user);
    }

    public Task<DocumentSnapshot> getUser(String uid) {
        Log.d(TAG, "getUser with uid: " + uid);

        return mUserRepository.getUser(uid);
    }

    public Query getUsersQuery() {
        Log.d(TAG, "getUsersQuery");

        return mUserRepository.getUsersQuery();
    }

    public void updateUserChosenRestaurant(String uid, String restaurantId) {
        getUser(uid).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (!restaurantId.equals(task.getResult().get("chosenRestaurant"))) {
                        mUserRepository.updateUserChosenRestaurant(uid, restaurantId);
                    } else {
                        mUserRepository.updateUserChosenRestaurant(uid, null);
                    }
                }
            }
        });
    }
}
