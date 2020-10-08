package fr.azhot.go4lunch.view;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import fr.azhot.go4lunch.BuildConfig;
import fr.azhot.go4lunch.databinding.FragmentListViewBinding;
import fr.azhot.go4lunch.model.NearbySearch;
import fr.azhot.go4lunch.viewmodel.AppViewModel;

import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_ID_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_NAME_EXTRA;
import static fr.azhot.go4lunch.util.AppConstants.RESTAURANT_PHOTO_URL_EXTRA;
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
    private List<NearbySearch.Result> mCurrentRestaurants;


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
        NearbySearch.Result restaurant = mAdapter.getRestaurants().get(position);

        String restaurantId = restaurant.getPlaceId();
        intent.putExtra(RESTAURANT_ID_EXTRA, restaurantId);

        String restaurantName = restaurant.getName();
        intent.putExtra(RESTAURANT_NAME_EXTRA, restaurantName);

        String restaurantVicinity = restaurant.getVicinity();
        intent.putExtra(RESTAURANT_VICINITY_EXTRA, restaurantVicinity);

        if (restaurant.getPhotos() != null) {
            String restaurantPhotoUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                    "key=" + BuildConfig.GOOGLE_API_KEY +
                    "&photoreference=" + restaurant.getPhotos().get(0).getPhotoReference() +
                    "&maxwidth=400";
            intent.putExtra(RESTAURANT_PHOTO_URL_EXTRA, restaurantPhotoUrl);
        }

        if (restaurant.getRating() != null) {
            int restaurantRating = ((int) Math.round(restaurant.getRating() / 5 * 3));
            intent.putExtra(RESTAURANT_RATING_EXTRA, restaurantRating);
        }

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

        mAppViewModel.getNearbyRestaurants().observe(getViewLifecycleOwner(), new Observer<NearbySearch>() {
            @Override
            public void onChanged(NearbySearch nearbySearch) {
                if (mCurrentRestaurants == null) {
                    mCurrentRestaurants = new ArrayList<>();
                }
                mCurrentRestaurants.clear();
                mCurrentRestaurants.addAll(nearbySearch.getResults());
                mAdapter.setRestaurants(mCurrentRestaurants);
            }
        });

        mAppViewModel.getLocationActivated().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    if (mCurrentRestaurants != null) {
                        mAdapter.setRestaurants(mCurrentRestaurants);
                    }
                } else {
                    mAdapter.setRestaurants(new ArrayList<>());
                }
            }
        });
    }
}
