package fr.azhot.go4lunch.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.FragmentWorkmatesBinding;
import fr.azhot.go4lunch.model.Restaurant;
import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.util.IntentUtils;
import fr.azhot.go4lunch.viewmodel.AppViewModel;

import static fr.azhot.go4lunch.util.AppConstants.SELECTED_RESTAURANT_ID_FIELD;

public class WorkmatesFragment extends Fragment implements WorkmatesAdapter.OnWorkmateClickListener {


    // private static
    private static final String TAG = "WorkmatesFragment";


    // variables
    private FragmentWorkmatesBinding mBinding;
    private Context mContext;
    private AppViewModel mViewModel;
    private List<Restaurant> mRestaurants;


    // public static
    public static WorkmatesFragment newInstance() {
        Log.d(TAG, "newInstance");

        WorkmatesFragment fragment = new WorkmatesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


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

        mBinding = FragmentWorkmatesBinding.inflate(inflater);
        mBinding.workmatesRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
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
    }

    @Override
    public void OnWorkmateClick(String restaurantId, String userName) {
        Log.d(TAG, "OnWorkmateClick");

        if (restaurantId != null) {
            Bitmap restaurantPhoto = null;
            for (Restaurant restaurant : mRestaurants) {
                if (restaurant.getPlaceId().equals(restaurantId)) {
                    restaurantPhoto = restaurant.getPhoto();
                    break;
                }
            }
            Intent intent = IntentUtils.loadRestaurantDataIntoIntent(
                    mContext, RestaurantDetailsActivity.class, restaurantId, restaurantPhoto);
            startActivity(intent);
        } else {
            String firstName = userName.split(" ")[0];
            Toast.makeText(mContext, getString(R.string.has_not_decided, firstName), Toast.LENGTH_SHORT).show();
        }
    }


    // methods
    private void initObservers() {
        Log.d(TAG, "initObservers");

        mBinding.workmatesRecyclerView.setAdapter(new WorkmatesAdapter(
                generateOptionsForAdapter(mViewModel.getUsersQuery()
                        .orderBy(SELECTED_RESTAURANT_ID_FIELD, Query.Direction.DESCENDING)),
                Glide.with(this),
                this));

        mViewModel.getRestaurants().observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                Log.d(TAG, "getRestaurants");

                if (mRestaurants == null) {
                    mRestaurants = new ArrayList<>();
                }

                mRestaurants.clear();
                mRestaurants.addAll(restaurants);
            }
        });
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query) {
        Log.d(TAG, "generateOptionsForAdapter");

        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }
}
