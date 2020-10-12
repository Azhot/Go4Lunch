package fr.azhot.go4lunch.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
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

import java.io.ByteArrayOutputStream;

import fr.azhot.go4lunch.databinding.FragmentListViewBinding;
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

        if (restaurant.getPhoto() != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            restaurant.getPhoto().compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            intent.putExtra(RESTAURANT_PHOTO_EXTRA, byteArray);
        }

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

        mAppViewModel.getRestaurant().observe(getViewLifecycleOwner(), new Observer<Restaurant>() {
            @Override
            public void onChanged(Restaurant restaurant) {
                Log.d(TAG, "getRestaurant: onChanged: ");

                mAdapter.setRestaurants(mAppViewModel.getExistingRestaurants());
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

        mAppViewModel.getDeviceLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                mAdapter.setDeviceLocation(location);
            }
        });
    }
}
