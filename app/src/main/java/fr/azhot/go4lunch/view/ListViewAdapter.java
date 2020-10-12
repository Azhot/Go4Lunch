package fr.azhot.go4lunch.view;

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
import java.util.Comparator;
import java.util.List;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.CellListViewBinding;
import fr.azhot.go4lunch.model.Restaurant;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.RestaurantViewHolder> {

    // private static
    private static final String TAG = "ListViewAdapter";

    // variables
    private RequestManager mGlide;
    private List<Restaurant> mRestaurants;
    private List<Restaurant> mHiddenRestaurants;
    private Location mDeviceLocation;
    private OnRestaurantClickListener mListener;


    // constructor
    public ListViewAdapter(RequestManager glide, ListViewAdapter.OnRestaurantClickListener listener) {
        Log.d(TAG, "constructor");

        this.mGlide = glide;
        this.mRestaurants = new ArrayList<>();
        this.mHiddenRestaurants = new ArrayList<>();
        this.mDeviceLocation = new Location(LocationManager.GPS_PROVIDER);
        this.mListener = listener;
    }

    // methods
    public void setRestaurants(List<Restaurant> restaurants) {
        mRestaurants.clear();
        mRestaurants.addAll(restaurants);
        sortRestaurants();
    }

    public List<Restaurant> getRestaurants() {
        Log.d(TAG, "getRestaurants");

        return mRestaurants;
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
        mDeviceLocation = location;
        sortRestaurants();
    }

    private void sortRestaurants() {
        Collections.sort(mRestaurants, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant o1, Restaurant o2) {
                return Float.compare(o1.getLocation().distanceTo(mDeviceLocation), o2.getLocation().distanceTo(mDeviceLocation));
            }
        });
        notifyDataSetChanged();
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


    // interface
    public interface OnRestaurantClickListener {
        void onRestaurantClick(int position);
    }


    // view holder
    public static class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CellListViewBinding mBinding;
        private ListViewAdapter.OnRestaurantClickListener mListener;

        public RestaurantViewHolder(CellListViewBinding binding, ListViewAdapter.OnRestaurantClickListener listener) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.mListener = listener;
            binding.getRoot().setOnClickListener(this);
        }

        public void onBindData(Restaurant restaurant, RequestManager glide, Location deviceLocation) {
            mBinding.cellListViewNameTextView.setText(restaurant.getName());
            String distance = Math.round(deviceLocation.distanceTo(restaurant.getLocation())) + "m";
            mBinding.cellListViewDistanceTextView.setText(distance);
            mBinding.cellListViewVicinityTextView.setText(restaurant.getVicinity());
            if (restaurant.getOpeningHours() != null) {
                if (restaurant.getOpeningHours().getOpenNow()) {
                    mBinding.cellListViewOpeningHoursTextView.setText(R.string.open);
                } else {
                    mBinding.cellListViewOpeningHoursTextView.setText(R.string.closed);
                }
            } else {
                mBinding.cellListViewOpeningHoursTextView.setText(R.string.info_not_available);
            }
            if (restaurant.getRating() != null) {
                mBinding.cellListViewRatingBar.setRating((int) Math.round(restaurant.getRating() / 5 * 3));
            }

            // todo : count workmates (hide imageview if none)

            glide.load(restaurant.getPhoto())
                    .into(mBinding.cellListViewPhotoImageView);
        }

        @Override
        public void onClick(View v) {
            mListener.onRestaurantClick(getAdapterPosition());
        }
    }
}
