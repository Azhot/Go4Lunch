package fr.azhot.go4lunch.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import fr.azhot.go4lunch.BuildConfig;
import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import fr.azhot.go4lunch.model.Restaurant;
import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.notification.LunchTimeNotificationPublisher;
import fr.azhot.go4lunch.repository.GooglePlaceRepository;
import fr.azhot.go4lunch.util.PermissionsUtils;
import fr.azhot.go4lunch.viewmodel.AppViewModel;

import static fr.azhot.go4lunch.util.AppConstants.NOTIFICATIONS_PREFERENCES_NAME;
import static fr.azhot.go4lunch.util.AppConstants.RC_CALL_PHONE_PERMISSION;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_ID_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.SELECTED_RESTAURANT_ID_FIELD;
import static fr.azhot.go4lunch.util.AppConstants.SHARED_PREFERENCES_NAME;

public class RestaurantDetailsActivity extends AppCompatActivity {


    // private static
    private static final String TAG = RestaurantDetailsActivity.class.getSimpleName();


    // variables
    private ActivityRestaurantDetailsBinding mBinding;
    private AppViewModel mViewModel;
    private FirebaseAuth mAuth;
    private User mUser;
    private Restaurant mCurrentRestaurant;


    // inherited methods
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        init();
        setContentView(mBinding.getRoot());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_CALL_PHONE_PERMISSION) {
            if (PermissionsUtils.checkCallPhonePermission(this)) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + mCurrentRestaurant.getPhoneNumber()));
                startActivity(intent);
            }
        }
    }

    // methods
    private void init() {
        Log.d(TAG, "init");

        mBinding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        mBinding.restaurantDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        mAuth = FirebaseAuth.getInstance();
        String placeId = getIntent().getStringExtra(RESTAURANT_ID_EXTRA);
        mViewModel.getRestaurantDetails(placeId, new GooglePlaceRepository.OnCompleteListener() {
            @Override
            public void onSuccess(Restaurant restaurant) {
                mCurrentRestaurant = restaurant;
                updateUIWithRestaurantDetails();
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "getRestaurantDetails: onFailure");
            }
        });
    }

    private void updateUIWithRestaurantDetails() {
        Log.d(TAG, "updateUIWithRestaurantDetails");

        mBinding.restaurantDetailsNameTextView.setText(mCurrentRestaurant.getName());
        mBinding.restaurantDetailsVicinity.setText(mCurrentRestaurant.getVicinity());
        if (mCurrentRestaurant.getPhotoReference() != null) {
            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                    "key=" + BuildConfig.GOOGLE_API_KEY +
                    "&photoreference=" + mCurrentRestaurant.getPhotoReference() +
                    "&maxwidth=400";

            Glide.with(this)
                    .load("https://source.unsplash.com/random/400x400") // todo : REPLACE WITH photoUrl AT THE END OF PROJECT
                    .into(mBinding.restaurantDetailsPhotoImageView);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_no_image)
                    .into(mBinding.restaurantDetailsPhotoImageView);
        }
        if (mCurrentRestaurant.getRating() != null) {
            mBinding.restaurantDetailsRatingBar.setRating(mCurrentRestaurant.getRating());
        }

        mBinding.restaurantDetailsRecyclerView.setAdapter(new RestaurantDetailsAdapter(
                generateOptionsForAdapter(mViewModel
                        .getUsersQuery()
                        .whereEqualTo(SELECTED_RESTAURANT_ID_FIELD, mCurrentRestaurant.getPlaceId())),
                Glide.with(this)));

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mViewModel.getUser(currentUser.getUid())
                    .addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "getUser: onSuccess");
                                mUser = task.getResult().toObject(User.class);
                                if (mUser != null) {
                                    setUpFab();
                                    setUplikeButton();
                                }
                            } else {
                                Log.e(TAG, "getUser: onFailure", task.getException());
                            }
                        }
                    });
        }
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }

    private void setUpFab() {
        Log.d(TAG, "setUpFab");

        if (mCurrentRestaurant.getPlaceId().equals(mUser.getSelectedRestaurantId())) {
            mBinding.restaurantDetailsFab.setImageResource(R.drawable.ic_check_circle_cyan);
        } else {
            mBinding.restaurantDetailsFab.setImageResource(R.drawable.ic_check_circle_grey);
        }
    }

    private void setUplikeButton() {
        Log.d(TAG, "setUpFab");

        if (mUser.getLikedRestaurants().contains(mCurrentRestaurant.getPlaceId())) {
            mBinding.restaurantDetailsLikeButton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.ic_star_cyan), null, null);
            mBinding.restaurantDetailsLikeButton.setText(R.string.liked);
            mBinding.restaurantDetailsLikeButton.setTextColor(ContextCompat.getColor(this, R.color.colorCyan));
        } else {
            mBinding.restaurantDetailsLikeButton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.ic_star_orange), null, null);
            mBinding.restaurantDetailsLikeButton.setText(R.string.like);
            mBinding.restaurantDetailsLikeButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    public void onClick(View view) {
        Log.d(TAG, "onClick");

        switch (view.getId()) {
            case R.id.restaurant_details_fab:
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                boolean isNotificationsActivated = sharedPreferences.getBoolean(NOTIFICATIONS_PREFERENCES_NAME, true);
                LunchTimeNotificationPublisher lunchTimeNotificationPublisher = new LunchTimeNotificationPublisher();

                if ((mCurrentRestaurant.getPlaceId().equals(mUser.getSelectedRestaurantId()))) {
                    mBinding.restaurantDetailsFab.setImageResource(R.drawable.ic_check_circle_grey);
                    mUser.setSelectedRestaurantId(null);
                    mUser.setSelectedRestaurantName(null);
                    if (isNotificationsActivated) {
                        lunchTimeNotificationPublisher.cancelLunchTimeNotification(this);
                    }
                } else {
                    // change button to activated
                    mBinding.restaurantDetailsFab.setImageResource(R.drawable.ic_check_circle_cyan);
                    mUser.setSelectedRestaurantId(mCurrentRestaurant.getPlaceId());
                    mUser.setSelectedRestaurantName(mCurrentRestaurant.getName());
                    if (isNotificationsActivated) {
                        lunchTimeNotificationPublisher.scheduleLunchTimeNotification(this, mUser.getUid(), mCurrentRestaurant);
                    }
                }

                mViewModel.updateUserRestaurantChoice(mUser)
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "updateUserRestaurantChoice: onSuccess");
                                } else {
                                    Log.e(TAG, "updateUserRestaurantChoice: onFailure", task.getException());
                                }
                            }
                        });
                break;
            case R.id.restaurant_details_call_button:
                Log.d(TAG, "onClick");

                if (mCurrentRestaurant.getPhoneNumber() != null) {
                    if (PermissionsUtils.checkCallPhonePermission(this)) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + mCurrentRestaurant.getPhoneNumber()));
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, R.string.no_phone_number, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.restaurant_details_like_button:
                if (mUser.getLikedRestaurants().contains(mCurrentRestaurant.getPlaceId())) {
                    mUser.getLikedRestaurants().remove(mCurrentRestaurant.getPlaceId());
                    mBinding.restaurantDetailsLikeButton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.ic_star_orange), null, null);
                    mBinding.restaurantDetailsLikeButton.setText(R.string.like);
                    mBinding.restaurantDetailsLikeButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                } else {
                    mUser.getLikedRestaurants().add(mCurrentRestaurant.getPlaceId());
                    mBinding.restaurantDetailsLikeButton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.ic_star_cyan), null, null);
                    mBinding.restaurantDetailsLikeButton.setText(R.string.liked);
                    mBinding.restaurantDetailsLikeButton.setTextColor(ContextCompat.getColor(this, R.color.colorCyan));
                }
                mViewModel.updateUserLikedRestaurant(mUser)
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "updateUserLikedRestaurant: onSuccess");
                                } else {
                                    Log.e(TAG, "updateUserLikedRestaurant: onFailure", task.getException());
                                }
                            }
                        });
                break;
            case R.id.restaurant_details_website_button:
                if (mCurrentRestaurant.getWebsite() != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(mCurrentRestaurant.getWebsite()));
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.no_website, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}