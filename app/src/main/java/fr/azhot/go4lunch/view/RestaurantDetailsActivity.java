package fr.azhot.go4lunch.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.ActivityRestaurantDetailsBinding;

public class RestaurantDetailsActivity extends AppCompatActivity {


    // variables
    private ActivityRestaurantDetailsBinding mBinding;


    // inherited methods
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        updateUIWithRestaurantDetails();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.restaurant_details_fab:
                // update user chosen restaurant status
                break;
            default:
                break;
        }
    }

    private void updateUIWithRestaurantDetails() {
        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        String details = intent.getStringExtra("details");
        String photoUrl = intent.getStringExtra("photoUrl");
        int rating = intent.getIntExtra("rating", 0);

        mBinding.restaurantDetailsNameTextView.setText(name);
        mBinding.restaurantDetailsVicinity.setText(details);
        Glide.with(this)
                .load(photoUrl)
                .into(mBinding.restaurantDetailsPhotoImageView);
        mBinding.restaurantDetailsRatingBar.setRating(rating);
    }
}
