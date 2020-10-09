package fr.azhot.go4lunch.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.viewmodel.AppViewModel;

import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_ID_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_NAME_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_PHOTO_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_RATING_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_VICINITY_EXTRA;

public class RestaurantDetailsActivity extends AppCompatActivity {


    // private static
    private static final String TAG = "RestaurantDetailsActivi";


    // variables
    private ActivityRestaurantDetailsBinding mBinding;
    private AppViewModel mAppViewModel;
    private FirebaseAuth mAuth;
    private User mCurrentUser;
    private String mRestaurantId;
    private String mRestaurantName;
    private String mRestaurantVicinity;
    private Bitmap mRestaurantPhoto;
    private int mRestaurantRating;


    // inherited methods
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        setContentView(mBinding.getRoot());
        retrieveDataFromIntent();
        updateUIWithRestaurantDetails();
    }


    // methods
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.restaurant_details_fab:

                if ((mRestaurantId.equals(mCurrentUser.getChosenRestaurantId()))) {
                    mBinding.restaurantDetailsFab.setImageResource(R.drawable.ic_check_circle_grey);
                    mCurrentUser.setChosenRestaurantId(null);
                    mCurrentUser.setChosenRestaurantName(null);
                } else {
                    // change button to activated
                    mBinding.restaurantDetailsFab.setImageResource(R.drawable.ic_check_circle_cyan);
                    mCurrentUser.setChosenRestaurantId(mRestaurantId);
                    mCurrentUser.setChosenRestaurantName(mRestaurantName);
                }

                mAppViewModel.updateUserChosenRestaurant(mCurrentUser);

                break;
            case R.id.restaurant_details_call_button:
                // intent to phone with restaurant number
                break;
            case R.id.restaurant_details_like_button:
                // todo : question to Virgile : what is it to do here ?
                break;
            case R.id.restaurant_details_website_button:
                String url = "http://www.example.com";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void init() {
        Log.d(TAG, "init");

        mBinding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        mAppViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            mAppViewModel.getUser(mAuth.getCurrentUser().getUid()).addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.d(TAG, "onSuccess");
                    mCurrentUser = documentSnapshot.toObject(User.class);
                    if (mCurrentUser != null) {
                        setUpFab(mRestaurantId, mCurrentUser);
                    }
                }
            });
        }
    }

    private void retrieveDataFromIntent() {
        Log.d(TAG, "retrieveDataFromIntent");

        Intent intent = getIntent();
        mRestaurantId = intent.getStringExtra(RESTAURANT_ID_EXTRA);
        mRestaurantName = intent.getStringExtra(RESTAURANT_NAME_EXTRA);
        mRestaurantVicinity = intent.getStringExtra(RESTAURANT_VICINITY_EXTRA);
        byte[] byteArray = getIntent().getByteArrayExtra(RESTAURANT_PHOTO_EXTRA);
        if (byteArray != null) {
            mRestaurantPhoto = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
        mRestaurantRating = intent.getIntExtra(RESTAURANT_RATING_EXTRA, 0);
    }

    private void updateUIWithRestaurantDetails() {
        Log.d(TAG, "updateUIWithRestaurantDetails");

        mBinding.restaurantDetailsNameTextView.setText(mRestaurantName);
        mBinding.restaurantDetailsVicinity.setText(mRestaurantVicinity);
        Glide.with(this)
                .load(mRestaurantPhoto)
                .into(mBinding.restaurantDetailsPhotoImageView);
        mBinding.restaurantDetailsRatingBar.setRating(mRestaurantRating);
    }

    private void setUpFab(String restaurantId, User user) {
        Log.d(TAG, "setUpFab");

        Log.d(TAG, "setUpFab: restaurantId = " + restaurantId);
        Log.d(TAG, "setUpFab: getChosenRestaurantId = " + user.getChosenRestaurantId());

        if (restaurantId.equals(user.getChosenRestaurantId())) {
            mBinding.restaurantDetailsFab.setImageResource(R.drawable.ic_check_circle_cyan);
        } else {
            mBinding.restaurantDetailsFab.setImageResource(R.drawable.ic_check_circle_grey);
        }
    }
}
