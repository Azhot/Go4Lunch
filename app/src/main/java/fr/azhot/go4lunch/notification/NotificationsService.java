// kept for the purpose of future possible interest

/*
package fr.azhot.go4lunch.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.model.Restaurant;
import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.repository.GooglePlaceRepository;
import fr.azhot.go4lunch.repository.UserRepository;
import fr.azhot.go4lunch.util.IntentUtils;
import fr.azhot.go4lunch.view.RestaurantDetailsActivity;

import static fr.azhot.go4lunch.util.AppConstants.SELECTED_RESTAURANT_ID_FIELD;

public class NotificationsService extends FirebaseMessagingService {


    // private static
    private static final String NOTIFICATION_TAG = "Go4Lunch";
    private static final int NOTIFICATION_ID = 123456789;
    private static final String DEFAULT_NOTIFICATION_CHANNEL_ID = "default_fcm_channel_id";
    private static final String DEFAULT_NOTIFICATION_CHANNEL_NAME = "default_fcm_channel_name";


    // variables
    private User mUser;
    private Restaurant mRestaurant;
    private List<User> mJoiningWorkmates;
    private UserRepository mUserRepository;
    private ListenerRegistration mListenerRegistration;


    // inherited methods
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            if (remoteMessage.getNotification().getTitle() != null &&
                    remoteMessage.getNotification().getTitle().equals(getString(R.string.lunch_time_notification_title))) {
                getUserFromFirestore();
            }
        }
    }


    // methods
    private void getUserFromFirestore() {
        mUserRepository = UserRepository.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            mUserRepository.getUser(currentUser.getUid())
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                mUser = task.getResult().toObject(User.class);
                                if (mUser != null && mUser.getSelectedRestaurantId() != null) {
                                    getRestaurantFromPlaceDetails();
                                }
                            }
                        }
                    });
        }
    }

    private void getRestaurantFromPlaceDetails() {
        GooglePlaceRepository googlePlaceRepository = GooglePlaceRepository.getInstance();
        googlePlaceRepository.getDetailsRestaurant(mUser.getSelectedRestaurantId(), new GooglePlaceRepository.OnCompleteListener() {
            @Override
            public void onSuccess(Restaurant restaurant) {
                mRestaurant = restaurant;
                mListenerRegistration = getJoiningWorkmatesFromFirestore();
            }

            @Override
            public void onFailure() {
                mRestaurant = null;
            }
        });
    }

    private ListenerRegistration getJoiningWorkmatesFromFirestore() {
        return mUserRepository.getUsersQuery()
                .whereEqualTo(SELECTED_RESTAURANT_ID_FIELD, mRestaurant.getPlaceId())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (snapshot != null) {
                            mJoiningWorkmates = new ArrayList<>(snapshot.toObjects(User.class));
                            displayNotificationOnDevice();
                        }
                    }
                });
    }

    private void displayNotificationOnDevice() {
        mListenerRegistration.remove();

        Intent intent = IntentUtils.loadRestaurantDataIntoIntent(
                this,
                RestaurantDetailsActivity.class,
                mUser.getSelectedRestaurantId());
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.lunch_time_notification_title));
        inboxStyle
                .addLine(getString(R.string.lunch_at) + " " + mRestaurant.getName())
                .addLine(getString(R.string.address) + " " + mRestaurant.getVicinity());
        if (mJoiningWorkmates.size() > 1) {
            inboxStyle.addLine(getString(R.string.with));
            for (User user : mJoiningWorkmates) {
                if (!user.getUid().equals(mUser.getUid())) {
                    inboxStyle.addLine(user.getName());
                }
            }
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, DEFAULT_NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_soupe_bowl)
                        .setContentTitle(getString(R.string.lunch_time_notification_title))
                        .setContentText(getString(R.string.lunch_at) + mRestaurant.getName())
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
            NotificationChannel channel = new NotificationChannel(
                    DEFAULT_NOTIFICATION_CHANNEL_ID,
                    DEFAULT_NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
        }
    }
}
 */
