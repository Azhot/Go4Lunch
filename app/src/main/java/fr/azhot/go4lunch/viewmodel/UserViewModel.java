package fr.azhot.go4lunch.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.List;

import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.repository.UserRepository;

public class UserViewModel extends ViewModel {


    // private static
    private static final String TAG = "RestaurantViewModel";


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

    public ListenerRegistration getAllUsersAsLiveData(MutableLiveData<List<User>> users) {
        Log.d(TAG, "getAllUsersAsLiveData");

        return mUserRepository.getAllUsersAsLiveData(users);
    }
}
