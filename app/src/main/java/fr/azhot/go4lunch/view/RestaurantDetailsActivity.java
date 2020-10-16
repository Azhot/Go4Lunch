package fr.azhot.go4lunch.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import fr.azhot.go4lunch.BuildConfig;
import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import fr.azhot.go4lunch.model.DetailsPOJO;
import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.util.PermissionsUtils;
import fr.azhot.go4lunch.viewmodel.AppViewModel;

import static fr.azhot.go4lunch.util.AppConstants.RC_CALL_PHONE_PERMISSION;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_ID_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_PHOTO_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.SELECTED_RESTAURANT_ID_FIELD;

public class RestaurantDetailsActivity extends AppCompatActivity {


    // private static
    private static final String TAG = "RestaurantDetailsActivi";


    // variables
    private ActivityRestaurantDetailsBinding mBinding;
    private AppViewModel mViewModel;
    private FirebaseAuth mAuth;
    private User mUser;
    private String mRestaurantId;
    private String mRestaurantName;
    private String mRestaurantVicinity;
    private Bitmap mRestaurantPhoto;
    private Integer mRestaurantRating;
    private String mRestaurantWebsite;
    private String mRestaurantPhoneNumber;
    private DetailsPOJO.Result mResult;


    // inherited methods
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        init();
        setContentView(mBinding.getRoot());
        retrieveDataFromIntent();
        initObservers();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_CALL_PHONE_PERMISSION) {
            if (grantResults.length > 0) {
                for (int i : grantResults) {
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: permissions denied.");
                        return;
                    }
                }
            }
            PermissionsUtils.checkCallPhonePermission(this);
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + mRestaurantPhoneNumber));
            startActivity(intent);
        }
    }

    // methods
    private void init() {
        Log.d(TAG, "init");

        mBinding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        mBinding.restaurantDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        mAuth = FirebaseAuth.getInstance();
    }

    private void retrieveDataFromIntent() {
        Log.d(TAG, "retrieveDataFromIntent");

        Intent intent = getIntent();
        mRestaurantId = intent.getStringExtra(RESTAURANT_ID_EXTRA);
        mViewModel.setDetailsPOJO(mRestaurantId);
        byte[] byteArray = intent.getByteArrayExtra(RESTAURANT_PHOTO_EXTRA);
        if (byteArray != null) {
            mRestaurantPhoto = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
    }

    private void initObservers() {
        mViewModel.getDetailsPOJO().observe(this, new Observer<DetailsPOJO>() {
            @Override
            public void onChanged(DetailsPOJO detailsPOJO) {
                if (detailsPOJO != null) {
                    mResult = detailsPOJO.getResult();

                    mRestaurantName = mResult.getName();
                    mRestaurantVicinity = mResult.getVicinity();
                    if (mResult.getRating() != null) {
                        mRestaurantRating = (int) Math.round(mResult.getRating() / 5 * 3);
                    }
                    mRestaurantWebsite = mResult.getWebsite();
                    mRestaurantPhoneNumber = mResult.getInternationalPhoneNumber();

                    if (mResult.getPhotos() != null) {
                        if (mRestaurantPhoto == null) {
                            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                                    "key=" + BuildConfig.GOOGLE_API_KEY +
                                    "&photoreference=" + mResult.getPhotos().get(0).getPhotoReference() +
                                    "&maxwidth=400";
                            Glide.with(RestaurantDetailsActivity.this)
                                    .asBitmap()
                                    .load(photoUrl)
                                    .into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            mBinding.restaurantDetailsPhotoImageView.setImageBitmap(resource);
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {

                                        }
                                    });
                        }
                    } else {
                        Glide.with(RestaurantDetailsActivity.this)
                                .asBitmap()
                                .load(R.drawable.ic_no_image)
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        mBinding.restaurantDetailsPhotoImageView.setImageBitmap(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });
                    }
                    updateUIWithRestaurantDetails();
                } else {
                    // todo : could not find restaurant with this ID
                }
            }
        });

        mBinding.restaurantDetailsRecyclerView.setAdapter(new RestaurantDetailsAdapter(
                generateOptionsForAdapter(mViewModel
                        .getUsersQuery()
                        .whereEqualTo(SELECTED_RESTAURANT_ID_FIELD, mRestaurantId)),
                Glide.with(this)));

        if (mAuth.getCurrentUser() != null) {
            mViewModel.getUser(mAuth.getCurrentUser().getUid())
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

    private void updateUIWithRestaurantDetails() {
        Log.d(TAG, "updateUIWithRestaurantDetails");

        mBinding.restaurantDetailsNameTextView.setText(mRestaurantName);
        mBinding.restaurantDetailsVicinity.setText(mRestaurantVicinity);
        if (mRestaurantPhoto != null) {
            Glide.with(this)
                    .load(mRestaurantPhoto)
                    .into(mBinding.restaurantDetailsPhotoImageView);
        }
        if (mRestaurantRating != null) {
            mBinding.restaurantDetailsRatingBar.setRating(mRestaurantRating);
        }
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }

    private void setUpFab(String restaurantId, User user) {
        Log.d(TAG, "setUpFab");

        if (restaurantId.equals(user.getSelectedRestaurantId())) {
            mBinding.restaurantDetailsFab.setImageResource(R.drawable.ic_check_circle_cyan);
        } else {
            mBinding.restaurantDetailsFab.setImageResource(R.drawable.ic_check_circle_grey);
        }
    }

    public void onClick(View view) {
        Log.d(TAG, "onClick");

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
                Log.d(TAG, "onClick");

                if (mRestaurantPhoneNumber != null) {
                    PermissionsUtils.checkCallPhonePermission(this);
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + mRestaurantPhoneNumber));
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.no_phone_number, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.restaurant_details_like_button:
                // todo : question to Virgile : what is it expected for me to do here ?
                break;
            case R.id.restaurant_details_website_button:
                if (mRestaurantWebsite != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(mRestaurantWebsite));
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