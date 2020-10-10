package fr.azhot.go4lunch.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;

import fr.azhot.go4lunch.BuildConfig;
import fr.azhot.go4lunch.databinding.FragmentListViewBinding;
import fr.azhot.go4lunch.model.NearbyRestaurantsPOJO;
import fr.azhot.go4lunch.model.Restaurant;
import fr.azhot.go4lunch.viewmodel.AppViewModel;

import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_ID_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_NAME_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_PHOTO_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_RATING_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_VICINITY_EXTRA;

public class ListViewFragment extends Fragment implements ListViewAdapter.OnRestaurantClickListener {


    // private static
    private static final String TAG = "ListViewFragment";


    // public static
    public static ListViewFragment newInstance() {
        Log.d(TAG, "newInstance");

        ListViewFragment fragment = new ListViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    // variables
    private FragmentListViewBinding mBinding;
    private Context mContext;
    private AppViewModel mAppViewModel;
    private ListViewAdapter mAdapter;


    // inherited methods
    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach");

        super.onAttach(context);
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        init(inflater);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");

        super.onActivityCreated(savedInstanceState);
        mAppViewModel = ViewModelProviders.of(requireActivity()).get(AppViewModel.class);
        initObservers();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");

        super.onDetach();
        mContext = null;
    }

    // Called when user clicks on a cell of the recyclerview
    @Override
    public void onRestaurantClick(int position) {

        Intent intent = new Intent(mContext, RestaurantDetailsActivity.class);
        Restaurant restaurant = mAdapter.getRestaurants().get(position);

        intent.putExtra(RESTAURANT_ID_EXTRA, restaurant.getPlaceId());
        intent.putExtra(RESTAURANT_NAME_EXTRA, restaurant.getName());
        intent.putExtra(RESTAURANT_VICINITY_EXTRA, restaurant.getVicinity());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        restaurant.getPhoto().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        intent.putExtra(RESTAURANT_PHOTO_EXTRA, byteArray);

        int restaurantRating = ((int) Math.round(restaurant.getRating() / 5 * 3));
        intent.putExtra(RESTAURANT_RATING_EXTRA, restaurantRating);

        startActivity(intent);
    }

    // methods
    // Initializes UI related variables
    private void init(LayoutInflater inflater) {
        Log.d(TAG, "init");

        mBinding = FragmentListViewBinding.inflate(inflater);
        mBinding.cellWorkmatesRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new ListViewAdapter(Glide.with(this), this);
        mBinding.cellWorkmatesRecycleView.setAdapter(mAdapter);
    }

    // Initializes the AppViewModel observers
    private void initObservers() {
        Log.d(TAG, "initObservers");

        mAppViewModel.getNearbyRestaurants().observe(getViewLifecycleOwner(), new Observer<NearbyRestaurantsPOJO>() {
            @Override
            public void onChanged(NearbyRestaurantsPOJO nearbyRestaurantsPOJO) {
                Log.d(TAG, "getNearbyRestaurants: onChanged");
                if (nearbyRestaurantsPOJO == null) {
                    // todo : check if connection is available or else show message to user that no nearby restaurants
                } else {
                    // todo : bugs if connection was not available on first call then it never gets nearby restaurants
                    if (mAppViewModel.getPreviousResults().equals(nearbyRestaurantsPOJO.getResults())) {
                        mAdapter.setRestaurants(mAppViewModel.getRestaurants());
                    } else {
                        mAppViewModel.setPreviousResults(nearbyRestaurantsPOJO.getResults());
                        mAppViewModel.getRestaurants().clear();
                        createRestaurantsListAndLoadToAdapter(nearbyRestaurantsPOJO);
                    }
                }
            }
        });

        mAppViewModel.getLocationActivated().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Log.d(TAG, "getLocationActivated: onChanged");

                if (aBoolean) {
                    Log.d(TAG, "showRestaurants");
                    mAdapter.showRestaurants();
                } else {
                    Log.d(TAG, "hideRestaurants");
                    mAdapter.hideRestaurants();
                }
            }
        });
    }

    private void createRestaurantsListAndLoadToAdapter(NearbyRestaurantsPOJO nearbyRestaurantsPOJO) {
        Log.d(TAG, "downloadRestaurantsPhotos");

        // todo : compare lists in order to only download what is necessary ?

        for (NearbyRestaurantsPOJO.Result result : nearbyRestaurantsPOJO.getResults()) {
            if (result.getPhotos() != null) {
                String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                        "key=" + BuildConfig.GOOGLE_API_KEY +
                        "&photoreference=" + result.getPhotos().get(0).getPhotoReference() +
                        "&maxwidth=400";

                Log.d(TAG, "setRestaurants: " + result.getName() + ", photo : " + photoUrl);

                Glide.with(mContext).asBitmap()
                        .load(photoUrl)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                Restaurant restaurant = new Restaurant(result, resource);
                                mAppViewModel.getRestaurants().add(restaurant);
                                mAdapter.getRestaurants().add(restaurant);
                                mAdapter.notifyItemChanged(mAdapter.getItemCount());
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
        }
    }
}
