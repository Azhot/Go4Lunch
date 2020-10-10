package fr.azhot.go4lunch.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.CellListViewBinding;
import fr.azhot.go4lunch.model.Restaurant;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.RestaurantViewHolder> {

    // private static
    private static final String TAG = "ListViewAdapter";

    // variables
    private RequestManager mGlide;
    private OnRestaurantClickListener mListener;
    private List<Restaurant> mRestaurants;
    private List<Restaurant> mHiddenRestaurants;


    // constructor
    public ListViewAdapter(RequestManager glide, ListViewAdapter.OnRestaurantClickListener listener) {
        Log.d(TAG, "constructor");

        this.mGlide = glide;
        this.mRestaurants = new ArrayList<>();
        this.mHiddenRestaurants = new ArrayList<>();
        this.mListener = listener;
    }

    // methods
    public void addRestaurant(Restaurant restaurant) {
        if (!mRestaurants.contains(restaurant)) {
            mRestaurants.add(restaurant);
            notifyItemChanged(mRestaurants.size());
        }
    }

    public List<Restaurant> getRestaurants() {
        Log.d(TAG, "getRestaurants");

        return mRestaurants;
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        Log.d(TAG, "setRestaurants");

        mRestaurants.clear();
        mRestaurants.addAll(restaurants);
        notifyDataSetChanged();
    }

    public void hideRestaurants() {
        Log.d(TAG, "hideRestaurants");

        mHiddenRestaurants.clear();
        mHiddenRestaurants.addAll(mRestaurants);
        mRestaurants.clear();
        notifyDataSetChanged();
    }

    public void showRestaurants() {
        Log.d(TAG, "showRestaurants");

        mRestaurants.addAll(mHiddenRestaurants);
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
        holder.onBindData(mRestaurants.get(position), mGlide);
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

        public void onBindData(Restaurant restaurant, RequestManager glide) {
            mBinding.cellListViewNameTextView.setText(restaurant.getName());
            // mBinding.distanceTextView.setText(); // todo : calculate distance
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

            // todo : add a "no-image" icon if null
            glide.load(restaurant.getPhoto())
                    .into(mBinding.cellListViewPhotoImageView);
        }

        @Override
        public void onClick(View v) {
            mListener.onRestaurantClick(getAdapterPosition());
        }
    }
}
