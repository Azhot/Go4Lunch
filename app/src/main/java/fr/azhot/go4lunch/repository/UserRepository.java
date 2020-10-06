package fr.azhot.go4lunch.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import fr.azhot.go4lunch.model.User;

public class UserRepository {


    // private static
    private static final String TAG = "UserRepository";
    private static UserRepository USER_REPOSITORY;
    // varibales
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;

    // constructor
    public UserRepository() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.collectionReference = firebaseFirestore.collection("users");
    }

    // public static
    public static UserRepository getInstance() {
        Log.d(TAG, "getInstance");

        if (USER_REPOSITORY == null) {
            USER_REPOSITORY = new UserRepository();
        }
        return USER_REPOSITORY;
    }

    // methods
    public void createUser(User user) {
        collectionReference
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
                        Log.d(TAG, "createUser: onFailure");
                    }
                });
    }

    public Task<DocumentSnapshot> getUser(String uid) {
        return collectionReference.document(uid).get();
    }

    public Query getUsersQuery() {
        return collectionReference;
    }

    public ListenerRegistration getAllUsersAsLiveData(final MutableLiveData<List<User>> users) {
        return collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "onEvent: Listen failed.", error);
                    return;
                }
                if (value != null && !value.isEmpty()) {
                    users.postValue(value.toObjects(User.class));
                }
            }
        });
    }
}
