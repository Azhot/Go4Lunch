package fr.azhot.go4lunch.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import fr.azhot.go4lunch.databinding.ActivityRestaurantDetailBinding;

public class RestaurantDetailActivity extends AppCompatActivity {


    // variables
    private ActivityRestaurantDetailBinding mBinding;


    // inherited methods
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityRestaurantDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        String details = intent.getStringExtra("details");
        String photoUrl = intent.getStringExtra("photoUrl");

        mBinding.restaurantNameTextView.setText(name);
        mBinding.restaurantDetails.setText(details);
        Glide.with(this)
                .load(photoUrl)
                .into(mBinding.restaurantPhotoImageView);
    }
}
