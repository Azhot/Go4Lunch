package fr.azhot.go4lunch.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.viewmodel.AppViewModel;

import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_ID_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_NAME_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_PHOTO_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_RATING_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_VICINITY_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.SELECTED_RESTAURANT_ID_FIELD;

public class RestaurantDetailsActivity extends AppCompatActivity {


    // private static
    private static final String TAG = "RestaurantDetailsActivi";


    // variables
    private ActivityRestaurantDetailsBinding mBinding;
    private AppViewModel mViewModel;
    private User mUser;
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
        setUpRecyclerView();
    }


    // methods
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.restaurant_details_fab:

                if ((mRestaurantId.equals(mUser.getSelectedRestaurantId()))) {
                    mBinding.restaurantDetailsFab.setImageResource(R.drawable.ic_check_circle_grey);
                    mUser.setSelectedRestaurantId(null);
                    mUser.setSelectedRestaurantName(null);
                } else {
                    // change button to activated
                    mBinding.restaurantDetailsFab.setImageResource(R.drawable.ic_check_circle_cyan);
                    mUser.setSelectedRestaurantId(mRestaurantId);
                    mUser.setSelectedRestaurantName(mRestaurantName);
                }

                mViewModel.createOrUpdateUser(mUser)
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "createOrUpdateUser: onSuccess");
                                } else {
                                    Log.d(TAG, "createOrUpdateUser: onFailure");
                                }
                            }
                        });
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
        mViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            mViewModel.getUser(auth.getCurrentUser().getUid())
                    .addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "getUser: onSuccess");
                                mUser = task.getResult().toObject(User.class);
                                if (mUser != null) {
                                    setUpFab(mRestaurantId, mUser);
                                }
                            } else {
                                Log.d(TAG, "getUser: onFailure");
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
        if (mRestaurantPhoto != null) {
            Glide.with(this)
                    .load(mRestaurantPhoto)
                    .into(mBinding.restaurantDetailsPhotoImageView);
            mBinding.restaurantDetailsRatingBar.setRating(mRestaurantRating);
        }
    }

    private void setUpFab(String restaurantId, User user) {
        Log.d(TAG, "setUpFab");

        if (restaurantId.equals(user.getSelectedRestaurantId())) {
            mBinding.restaurantDetailsFab.setImageResource(R.drawable.ic_check_circle_cyan);
        } else {
            mBinding.restaurantDetailsFab.setImageResource(R.drawable.ic_check_circle_grey);
        }
    }

    private void setUpRecyclerView() {
        mBinding.restaurantDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.restaurantDetailsRecyclerView.setAdapter(new RestaurantDetailsAdapter(
                generateOptionsForAdapter(mViewModel
                        .getUsersQuery()
                        .whereEqualTo(SELECTED_RESTAURANT_ID_FIELD, mRestaurantId)),
                Glide.with(this)));
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }
}
