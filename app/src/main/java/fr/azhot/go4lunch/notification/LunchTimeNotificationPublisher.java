package fr.azhot.go4lunch.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.model.Restaurant;
import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.repository.UserRepository;
import fr.azhot.go4lunch.util.IntentUtils;
import fr.azhot.go4lunch.view.RestaurantDetailsActivity;

import static fr.azhot.go4lunch.util.AppConstants.SELECTED_RESTAURANT_ID_FIELD;

public class LunchTimeNotificationPublisher extends BroadcastReceiver {


    // private static
    private static final String DEFAULT_NOTIFICATION_CHANNEL_ID = "default-channel";
    private static final String NOTIFICATION_CHANNEL_ID = "10001";
    private static final String NOTIFICATION_CHANNEL = "notification-channel";
    private static final String NOTIFICATION_ID = "notification-id";
    private static final String NOTIFICATION = "notification";
    private static final int RC_NOTIFICATION_PENDING_INTENT = 132435;
    private static final int RC_BROADCAST_PENDING_INTENT = 243546;
    private static final int HOUR_OF_DAY_TO_NOTIFY = 12;


    // variables
    private List<User> mJoiningWorkmates;
    private ListenerRegistration mListenerRegistration;


    // constructor
    public LunchTimeNotificationPublisher() {

    }


    // inherited methods
    @Override
    public void onReceive(Context context, Intent intent) {

        if (!intent.getExtras().getBoolean("alarmCanceled")) {

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notification = intent.getParcelableExtra(NOTIFICATION);
            int id = intent.getIntExtra(NOTIFICATION_ID, 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        NOTIFICATION_CHANNEL_ID,
                        NOTIFICATION_CHANNEL,
                        NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            if (notificationManager != null) {
                notificationManager.notify(id, notification);
            }
        }
    }

    public void scheduleLunchTimeNotification(Context context, String uid, Restaurant restaurant) {
        mListenerRegistration = getJoiningWorkmatesFromFirestore(context, uid, restaurant);
    }

    private ListenerRegistration getJoiningWorkmatesFromFirestore(Context context, String uid, Restaurant restaurant) {
        UserRepository userRepository = UserRepository.getInstance();
        return userRepository.getUsersQuery()
                .whereEqualTo(SELECTED_RESTAURANT_ID_FIELD, restaurant.getPlaceId())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (snapshot != null) {
                            mJoiningWorkmates = new ArrayList<>(snapshot.toObjects(User.class));
                            setUpAndScheduleLunchTimeNotification(context, uid, restaurant);
                        }
                    }
                });
    }

    // methods
    private void setUpAndScheduleLunchTimeNotification(Context context, String uid, Restaurant restaurant) {
        mListenerRegistration.remove();

        // set-up notification
        Intent intent = IntentUtils.loadRestaurantDataIntoIntent(
                context,
                RestaurantDetailsActivity.class,
                restaurant.getPlaceId());
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(
                context,
                RC_NOTIFICATION_PENDING_INTENT,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(context.getString(R.string.lunch_time_notification_title));
        inboxStyle
                .addLine(context.getString(R.string.lunch_at) + " " + restaurant.getName())
                .addLine(context.getString(R.string.address) + " " + restaurant.getVicinity());
        if (!mJoiningWorkmates.isEmpty()) {
            inboxStyle.addLine(context.getString(R.string.with));
            for (User user : mJoiningWorkmates) {
                if (!user.getUid().equals(uid)) {
                    inboxStyle.addLine(user.getName());
                }
            }
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, DEFAULT_NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_soupe_bowl)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(context.getString(R.string.lunch_time_notification_title))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(notificationPendingIntent)
                        .setChannelId(NOTIFICATION_CHANNEL_ID)
                        .setStyle(inboxStyle);

        // Schedule Notification
        Intent broadcastIntent = new Intent(context, LunchTimeNotificationPublisher.class);
        broadcastIntent.putExtra(NOTIFICATION_ID, 1);
        broadcastIntent.putExtra(NOTIFICATION, builder.build());

        PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast(
                context,
                RC_BROADCAST_PENDING_INTENT,
                broadcastIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.HOUR_OF_DAY) >= HOUR_OF_DAY_TO_NOTIFY) {
            //calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY_TO_NOTIFY);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                broadcastPendingIntent);

    }

    public void cancelLunchTimeNotification(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent broadcastIntent = new Intent(context, LunchTimeNotificationPublisher.class);

        PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast(
                context,
                RC_BROADCAST_PENDING_INTENT,
                broadcastIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.cancel(broadcastPendingIntent);
        broadcastPendingIntent.cancel();
    }
}