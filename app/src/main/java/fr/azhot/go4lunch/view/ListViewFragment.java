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
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fr.azhot.go4lunch.databinding.FragmentListViewBinding;
import fr.azhot.go4lunch.model.Restaurant;
import fr.azhot.go4lunch.util.IntentUtils;
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
        mViewModel = ViewModelProviders.of(requireActivity()).get(AppViewModel.class);
        initObservers();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");

        super.onDetach();
        mContext = null;
        for (ListenerRegistration registration : mListenerRegistrations) {
            registration.remove();
        }
        mListenerRegistrations.clear();
    }

    // Called when user clicks on a cell of the recyclerview
    @Override
    public void onRestaurantClick(String restaurantId, Bitmap restaurantPhoto) {
        Intent intent = IntentUtils.loadRestaurantDataIntoIntent(
                mContext, RestaurantDetailsActivity.class, restaurantId, restaurantPhoto);
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
        mListenerRegistrations = new ArrayList<>();
    }

    // Initializes the AppViewModel observers
    private void initObservers() {
        Log.d(TAG, "initObservers");

        mViewModel.getNearbyRestaurants().observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                mAdapter.setRestaurants(restaurants);

                for (ListenerRegistration registration : mListenerRegistrations) {
                    registration.remove();
                }
                mListenerRegistrations.clear();

                for (Restaurant restaurant : restaurants) {
                    ListenerRegistration registration =
                            mViewModel.loadWorkmatesInRestaurants(restaurant.getPlaceId())
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                                            if (snapshot != null && e == null) {
                                                Log.d(TAG, "added EventListener to : " + restaurant.getName());
                                                restaurant.setWorkmatesJoining(snapshot.size());
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                    mListenerRegistrations.add(registration);
                }
            }
        });

        mViewModel.getLocationActivated().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Log.d(TAG, "getLocationActivated: onChanged");

                if (aBoolean) {
                    mAdapter.showRestaurants();
                } else {
                    mAdapter.hideRestaurants();
                }
            }
        });

        mViewModel.getDeviceLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                mAdapter.setDeviceLocation(location);
            }
        });


        mViewModel.getAutocompletePrediction().observe(getViewLifecycleOwner(), new Observer<AutocompletePrediction>() {
            @Override
            public void onChanged(AutocompletePrediction autocompletePrediction) {

                if (autocompletePrediction != null) {
                    for (Restaurant restaurant : mAdapter.getRestaurants()) {
                        if (autocompletePrediction.getPlaceId().equals(restaurant.getPlaceId())) {
                            mAdapter.filterAutocompleteRestaurant(restaurant);
                            break;
                        }
                    }
                } else {
                    mAdapter.setRestaurants(mAdapter.getSavedRestaurants());
                }
            }
        });
    }
}
