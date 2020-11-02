package fr.azhot.go4lunch.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.FragmentListViewBinding;
import fr.azhot.go4lunch.model.Restaurant;
import fr.azhot.go4lunch.util.IntentUtils;
import fr.azhot.go4lunch.view.activity.RestaurantDetailsActivity;
import fr.azhot.go4lunch.view.adapter.ListViewAdapter;
import fr.azhot.go4lunch.viewmodel.AppViewModel;

public class ListViewFragment extends Fragment implements ListViewAdapter.OnRestaurantClickListener {


    // private static
    private static final String TAG = ListViewFragment.class.getSimpleName();


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
    private AppViewModel mViewModel;
    private ListViewAdapter mAdapter;
    private List<ListenerRegistration> mListenerRegistrations;
    private ListenerRegistration mAutocompleteListenerRegistration;


    // inherited methods
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");

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
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        mViewModel = ViewModelProviders.of(requireActivity()).get(AppViewModel.class);
        initObservers();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");

        mContext = null;
        for (ListenerRegistration registration : mListenerRegistrations) {
            registration.remove();
        }
        mListenerRegistrations.clear();
        if (mAutocompleteListenerRegistration != null) {
            mAutocompleteListenerRegistration.remove();
        }
    }

    @Override
    public void onRestaurantClick(String placeId) {
        Intent intent = IntentUtils.loadRestaurantDataIntoIntent(
                mContext, RestaurantDetailsActivity.class, placeId);
        startActivity(intent);
    }

    // methods
    private void init(LayoutInflater inflater) {
        Log.d(TAG, "init");

        mBinding = FragmentListViewBinding.inflate(inflater);
        mBinding.cellWorkmatesRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new ListViewAdapter(Glide.with(this), this);
        mBinding.cellWorkmatesRecycleView.setAdapter(mAdapter);
        mListenerRegistrations = new ArrayList<>();
    }

    private void initObservers() {
        Log.d(TAG, "initObservers");

        // Nearby Search observer
        mViewModel.getNearbyRestaurantsLiveData().observe(getViewLifecycleOwner(), restaurants -> {
            Log.d(TAG, "getNearbyRestaurantsLiveData: onChanged");

            if (restaurants.isEmpty()) {
                mBinding.noRestaurantLayout.setVisibility(View.VISIBLE);
            } else {
                mBinding.noRestaurantLayout.setVisibility(View.GONE);

                mAdapter.setRestaurants(restaurants);

                for (ListenerRegistration registration : mListenerRegistrations) {
                    registration.remove();
                }
                mListenerRegistrations.clear();

                for (Restaurant restaurant : restaurants) {
                    ListenerRegistration registration =
                            mViewModel.loadWorkmatesInRestaurants(restaurant.getPlaceId())
                                    .addSnapshotListener((snapshot, e) -> {
                                        if (snapshot != null && e == null) {
                                            Log.d(TAG, "added EventListener to : " + restaurant.getName());
                                            restaurant.setWorkmatesJoining(snapshot.size());
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    });
                    mListenerRegistrations.add(registration);
                }
            }
        });

        // Location setting's observer
        mViewModel.getLocationActivatedLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            Log.d(TAG, "getLocationActivatedLiveData: onChanged");

            if (aBoolean) {
                mAdapter.showRestaurants();
                if (mAdapter.getRestaurants().isEmpty()) {
                    mBinding.noRestaurantLayout.setVisibility(View.VISIBLE);

                } else {
                    mBinding.noRestaurantLayout.setVisibility(View.GONE);
                }
                mBinding.noRestaurantImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_no_restaurants));
                mBinding.noRestaurantTextView.setText(R.string.no_restaurants_found);
            } else {
                mAdapter.hideRestaurants();
                mBinding.noRestaurantLayout.setVisibility(View.VISIBLE);
                mBinding.noRestaurantImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_location_off));
                mBinding.noRestaurantTextView.setText(R.string.location_is_disabled);
            }
        });

        // Current device's location observer
        mViewModel.getDeviceLocationLiveData().observe(getViewLifecycleOwner(), location -> {
            Log.d(TAG, "getDeviceLocationLiveData: onChanged");

            mAdapter.setDeviceLocation(location);
        });

        // Restaurant details observer
        mViewModel.getDetailsRestaurantLiveData().observe(getViewLifecycleOwner(), restaurant -> {
            Log.d(TAG, "getDetailsRestaurantLiveData: onChanged");

            if (restaurant != null) {
                mAutocompleteListenerRegistration =
                        mViewModel.loadWorkmatesInRestaurants(restaurant.getPlaceId())
                                .addSnapshotListener((snapshot, e) -> {
                                    if (snapshot != null && e == null) {
                                        Log.d(TAG, "added EventListener to : " + restaurant.getName());
                                        restaurant.setWorkmatesJoining(snapshot.size());
                                        mAdapter.filterAutocompleteRestaurant(restaurant);
                                        mBinding.noRestaurantLayout.setVisibility(View.GONE);
                                    }
                                });
            } else {
                if (mAutocompleteListenerRegistration != null) {
                    mAutocompleteListenerRegistration.remove();
                }
                mAdapter.loadSavedRestaurants();
                if (mAdapter.getRestaurants().isEmpty()) {
                    mBinding.noRestaurantLayout.setVisibility(View.VISIBLE);
                } else {
                    mBinding.noRestaurantLayout.setVisibility(View.GONE);
                }
            }
        });
    }
}
