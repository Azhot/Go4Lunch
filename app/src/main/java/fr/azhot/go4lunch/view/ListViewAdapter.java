package fr.azhot.go4lunch.view;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import fr.azhot.go4lunch.BuildConfig;
import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.CellListViewBinding;
import fr.azhot.go4lunch.model.NearbyRestaurantsPOJO;
import fr.azhot.go4lunch.model.Restaurant;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.RestaurantViewHolder> {

    private static final String TAG = "ListViewAdapter";
    private OnRestaurantClickListener mListener;


    // variables
    private RequestManager mGlide;
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
    public List<Restaurant> getRestaurants() {
        Log.d(TAG, "getRestaurants");

        return mRestaurants;
    }

    public void setRestaurants(List<NearbyRestaurantsPOJO.Result> results) {
        Log.d(TAG, "setRestaurants");

        mRestaurants.clear();

        for (NearbyRestaurantsPOJO.Result result : results) {
            if (result.getPhotos() != null) {
                Restaurant restaurant = new Restaurant(result, null);
                String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                        "key=" + BuildConfig.GOOGLE_API_KEY +
                        "&photoreference=" + result.getPhotos().get(0).getPhotoReference() +
                        "&maxwidth=400";

                Log.d(TAG, "setRestaurants: " + result.getName() + ", photo : " + photoUrl);

                mGlide.asBitmap()
                        .load(photoUrl)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                restaurant.setPhoto(resource);
                                mRestaurants.add(restaurant);
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
        }
    }

    public void hideRestaurants() {
        mHiddenRestaurants.clear();
        mHiddenRestaurants.addAll(mRestaurants);
        mRestaurants.clear();
        notifyDataSetChanged();
    }

    public void showRestaurants() {
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
            NearbyRestaurantsPOJO.Result result = restaurant.getResult();
            mBinding.cellListViewNameTextView.setText(result.getName());
            // mBinding.distanceTextView.setText(); // todo : calculate distance
            mBinding.cellListViewVicinityTextView.setText(result.getVicinity());
            if (result.getOpeningHours() != null) {
                if (result.getOpeningHours().getOpenNow()) {
                    mBinding.cellListViewOpeningHoursTextView.setText(R.string.open);
                } else {
                    mBinding.cellListViewOpeningHoursTextView.setText(R.string.closed);
                }
            } else {
                mBinding.cellListViewOpeningHoursTextView.setText(R.string.info_not_available);
            }
            mBinding.cellListViewRatingBar.setRating((int) Math.round(result.getRating() / 5 * 3));

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
