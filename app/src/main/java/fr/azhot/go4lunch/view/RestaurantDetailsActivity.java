package fr.azhot.go4lunch.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.viewmodel.UserViewModel;

public class RestaurantDetailsActivity extends AppCompatActivity {


    // variables
    private ActivityRestaurantDetailsBinding mBinding;
    private UserViewModel mUserViewModel;
    private User mCurrentUser;
    private String mRestaurantId;
    private String mRestaurantName;
    private String mRestaurantVicinity;
    private String mRestaurantPhotoUrl;
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
                // update user chosen restaurant status

                break;
            default:
                break;
        }
    }

    private void init() {
        mBinding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
    }

    private void retrieveDataFromIntent() {
        Intent intent = getIntent();

        mRestaurantId = intent.getStringExtra("restaurantId");
        mRestaurantName = intent.getStringExtra("restaurantName");
        mRestaurantVicinity = intent.getStringExtra("restaurantVicinity");
        mRestaurantPhotoUrl = intent.getStringExtra("restaurantPhotoUrl");
        mRestaurantRating = intent.getIntExtra("restaurantRating", 0);
    }

    private void updateUIWithRestaurantDetails() {
        mBinding.restaurantDetailsNameTextView.setText(mRestaurantName);
        mBinding.restaurantDetailsVicinity.setText(mRestaurantVicinity);
        Glide.with(this)
                .load(mRestaurantPhotoUrl)
                .into(mBinding.restaurantDetailsPhotoImageView);
        mBinding.restaurantDetailsRatingBar.setRating(mRestaurantRating);
    }
}
