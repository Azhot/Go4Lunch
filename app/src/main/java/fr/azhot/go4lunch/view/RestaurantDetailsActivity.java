package fr.azhot.go4lunch.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.viewmodel.UserViewModel;

public class RestaurantDetailsActivity extends AppCompatActivity {


    // variables
    private ActivityRestaurantDetailsBinding mBinding;
    private UserViewModel mUserViewModel;
    private User mCurrentUser;
    private String mRestaurantName;
    private String mRestaurantVicinity;
    private String mRestaurantPhotoUrl;
    private int mRestaurantRating;
    private String mRestaurantId;


    // inherited methods
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        retrieveRestaurantDetailsFromIntent();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // todo : question to Virgil : should I do a "assert user != null" here ? what is the best practice ?
        mUserViewModel.getUser(user.getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                mCurrentUser = task.getResult().toObject(User.class);
                setContentView(mBinding.getRoot());
                updateUIWithRestaurantDetails();
            }
        });
    }


    // methods
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.restaurant_details_fab:
                // update user chosen restaurant status
                // todo : question to Virgil : should I do a "assert user != null" here ? what is the best practice ?
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                mUserViewModel.updateUserChosenRestaurant(uid, mRestaurantId);
                if (!mCurrentUser.getChosenRestaurant().equals(mRestaurantId)) {
                    mBinding.restaurantDetailsFab.setBackgroundColor(getResources().getColor(R.color.colorGrey));
                } else {
                    mBinding.restaurantDetailsFab.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
                break;
            default:
                break;
        }
    }

    private void retrieveRestaurantDetailsFromIntent() {
        Intent intent = getIntent();

        mRestaurantName = intent.getStringExtra("name");
        mRestaurantVicinity = intent.getStringExtra("vicinity");
        mRestaurantPhotoUrl = intent.getStringExtra("photoUrl");
        mRestaurantRating = intent.getIntExtra("rating", 0);
        mRestaurantId = intent.getStringExtra("restaurantId");
    }

    private void updateUIWithRestaurantDetails() {
        mBinding.restaurantDetailsNameTextView.setText(mRestaurantName);
        mBinding.restaurantDetailsVicinity.setText(mRestaurantVicinity);
        Glide.with(this)
                .load(mRestaurantPhotoUrl)
                .into(mBinding.restaurantDetailsPhotoImageView);
        mBinding.restaurantDetailsRatingBar.setRating(mRestaurantRating);

        if (!mCurrentUser.getChosenRestaurant().equals(mRestaurantId)) {
            mBinding.restaurantDetailsFab.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        }
    }
}
