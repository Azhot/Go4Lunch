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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;

import fr.azhot.go4lunch.BuildConfig;
import fr.azhot.go4lunch.databinding.FragmentListViewBinding;
import fr.azhot.go4lunch.model.NearbySearch;

public class ListViewFragment extends Fragment implements ListViewAdapter.Listener {


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


    // inherited methods
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        mBinding = FragmentListViewBinding.inflate(inflater);

        mBinding.cellWorkmatesRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
        // todo : if user asks for ListViewFragment before MapViewFragment could get nearby restaurants,
        //  the adapter will be initialized with an empty list
        mBinding.cellWorkmatesRecycleView.setAdapter(new ListViewAdapter(Glide.with(this), MainActivity.CURRENT_RESTAURANTS, this));
        return mBinding.getRoot();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onRestaurantClick(int position) {
        Intent intent = new Intent(mContext, RestaurantDetailsActivity.class);
        NearbySearch.Result restaurant = MainActivity.CURRENT_RESTAURANTS.get(position);
        String name = restaurant.getName();
        intent.putExtra("name", name);
        String details = restaurant.getVicinity();
        intent.putExtra("details", details);
        if (restaurant.getPhotos() != null) {
            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                    "key=" + BuildConfig.GOOGLE_API_KEY +
                    "&photoreference=" + restaurant.getPhotos().get(0).getPhotoReference() +
                    "&maxwidth=400";
            intent.putExtra("photoUrl", photoUrl);
        }
        if (restaurant.getRating() != null) {
            intent.putExtra("rating", ((int) Math.round(restaurant.getRating() / 5 * 3)));
        }
        startActivity(intent);
    }
}
