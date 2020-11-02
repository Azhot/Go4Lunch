package fr.azhot.go4lunch.view.adapter;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.azhot.go4lunch.BuildConfig;
import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.CellListViewBinding;
import fr.azhot.go4lunch.model.Restaurant;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.RestaurantViewHolder> {


    private final List<Restaurant> mRestaurants;


    // private static
    private static final String TAG = ListViewAdapter.class.getSimpleName();


    // variables
    private final RequestManager mGlide;
    private final List<Restaurant> mHiddenRestaurants;
    private final List<Restaurant> mSavedRestaurants;
    private final OnRestaurantClickListener mListener;
    private Location mDeviceLocation;

    private void sortRestaurants() {
        Log.d(TAG, "sortRestaurants");

        Collections.sort(mRestaurants, (o1, o2) -> {

            Location lo1 = new Location(LocationManager.GPS_PROVIDER);
            lo1.setLatitude(o1.getLatitude());
            lo1.setLongitude(o1.getLongitude());

            Location lo2 = new Location(LocationManager.GPS_PROVIDER);
            lo1.setLatitude(o2.getLatitude());
            lo1.setLongitude(o2.getLongitude());

            return Float.compare(lo1.distanceTo(mDeviceLocation), lo2.distanceTo(mDeviceLocation));
        });
        notifyDataSetChanged();
    }


    // constructor
    public ListViewAdapter(RequestManager glide, OnRestaurantClickListener listener) {
        Log.d(TAG, "constructor");

        this.mGlide = glide;
        this.mRestaurants = new ArrayList<>();
        this.mHiddenRestaurants = new ArrayList<>();
        this.mSavedRestaurants = new ArrayList<>();
        this.mDeviceLocation = new Location(LocationManager.GPS_PROVIDER);
        this.mListener = listener;
    }


    // inherited methods
    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantViewHolder(CellListViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        holder.onBindData(mRestaurants.get(position), mGlide, mDeviceLocation);
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }


    // methods
    public List<Restaurant> getRestaurants() {
        Log.d(TAG, "getRestaurants");

        return mRestaurants;
    }

    public void filterAutocompleteRestaurant(Restaurant restaurant) {
        Log.d(TAG, "setRestaurants");

        mSavedRestaurants.clear();
        mSavedRestaurants.addAll(mRestaurants);

        mRestaurants.clear();
        mRestaurants.add(restaurant);
        sortRestaurants();
    }

    public void loadSavedRestaurants() {
        if (!mSavedRestaurants.isEmpty()) {
            mRestaurants.clear();
            mRestaurants.addAll(mSavedRestaurants);
            sortRestaurants();
        }
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        Log.d(TAG, "setRestaurants");

        mRestaurants.clear();
        mRestaurants.addAll(restaurants);
        sortRestaurants();
    }

    public void hideRestaurants() {
        Log.d(TAG, "hideRestaurants");

        mHiddenRestaurants.addAll(mRestaurants);
        mRestaurants.clear();
        notifyDataSetChanged();
    }

    public void showRestaurants() {
        Log.d(TAG, "showRestaurants");

        mRestaurants.addAll(mHiddenRestaurants);
        mHiddenRestaurants.clear();
        notifyDataSetChanged();
    }

    public void setDeviceLocation(Location location) {
        Log.d(TAG, "setDeviceLocation");

        mDeviceLocation = location;
    }


    // interface
    public interface OnRestaurantClickListener {
        void onRestaurantClick(String placeId);
    }


    // view holder
    public static class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final CellListViewBinding mBinding;
        private final OnRestaurantClickListener mListener;
        private Restaurant mRestaurant;

        public RestaurantViewHolder(CellListViewBinding binding, ListViewAdapter.OnRestaurantClickListener listener) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.mListener = listener;
            this.mRestaurant = null;
            binding.getRoot().setOnClickListener(this);
        }

        public void onBindData(Restaurant restaurant, RequestManager glide, Location deviceLocation) {
            mRestaurant = restaurant;
            mBinding.cellListViewNameTextView.setText(restaurant.getName());
            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(mRestaurant.getLatitude());
            location.setLongitude(mRestaurant.getLongitude());
            String distance = Math.round(deviceLocation.distanceTo(location)) + "m";
            mBinding.cellListViewDistanceTextView.setText(distance);
            mBinding.cellListViewVicinityTextView.setText(restaurant.getVicinity());

            if (restaurant.getOpen() != null) {
                if (restaurant.getOpen()) {
                    mBinding.cellListViewOpeningHoursTextView.setText(R.string.open);
                } else {
                    mBinding.cellListViewOpeningHoursTextView.setText(R.string.closed);
                }
            } else {
                mBinding.cellListViewOpeningHoursTextView.setText(R.string.info_not_available);
            }

            if (restaurant.getRating() != null) {
                mBinding.cellListViewRatingBar.setRating(restaurant.getRating());
            }

            if (restaurant.getWorkmatesJoining() != 0) {
                String workmatesJoiningString = "(" + restaurant.getWorkmatesJoining() + ")";
                mBinding.cellListViewWorkmatesTextView.setText(workmatesJoiningString);
                mBinding.cellListViewWorkmatesImageView.setVisibility(View.VISIBLE);
            } else {
                mBinding.cellListViewWorkmatesTextView.setText(null);
                mBinding.cellListViewWorkmatesImageView.setVisibility(View.INVISIBLE);
            }

            if (restaurant.getPhotoReference() != null) {
                String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                        "key=" + BuildConfig.GOOGLE_API_KEY +
                        "&photoreference=" + restaurant.getPhotoReference() +
                        "&maxwidth=400";

                Log.d(TAG, "loadPhoto: " + restaurant.getName() + ", downloaded photo : " + photoUrl);
                glide.load(photoUrl)
                        .into(mBinding.cellListViewPhotoImageView);
            } else {
                glide.load(R.drawable.ic_no_image)
                        .into(mBinding.cellListViewPhotoImageView);
            }
        }

        @Override
        public void onClick(View v) {
            mListener.onRestaurantClick(mRestaurant.getPlaceId());
        }
    }
}
