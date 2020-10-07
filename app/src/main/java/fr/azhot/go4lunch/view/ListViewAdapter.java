package fr.azhot.go4lunch.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

import fr.azhot.go4lunch.BuildConfig;
import fr.azhot.go4lunch.databinding.CellListViewBinding;
import fr.azhot.go4lunch.model.NearbySearch;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.RestaurantViewHolder> {

    private OnRestaurantClickListener mListener;


    // variables
    private RequestManager mGlide;
    private List<NearbySearch.Result> mRestaurants;

    // constructor
    public ListViewAdapter(RequestManager glide, ListViewAdapter.OnRestaurantClickListener listener) {
        this.mGlide = glide;
        this.mRestaurants = new ArrayList<>();
        this.mListener = listener;
    }

    // methods
    public List<NearbySearch.Result> getRestaurants() {
        return mRestaurants;
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

    public void setRestaurants(List<NearbySearch.Result> restaurants) {
        mRestaurants.clear();
        mRestaurants.addAll(restaurants);
        notifyDataSetChanged();
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

        public void onBindData(NearbySearch.Result result, RequestManager glide) {
            if (result.getName() != null)
                mBinding.cellListViewNameTextView.setText(result.getName());
            // mBinding.distanceTextView.setText(); // todo : calculate distance
            if (result.getVicinity() != null)
                mBinding.cellListViewVicinityTextView.setText(result.getVicinity());
            // todo : review opening hours logic to show as in project
            if (result.getOpeningHours() != null) {
                if (result.getOpeningHours().getOpenNow()) {
                    mBinding.cellListViewOpeningHoursTextView.setText("Open");
                } else {
                    mBinding.cellListViewOpeningHoursTextView.setText("Closed");
                }
            } else {
                mBinding.cellListViewOpeningHoursTextView.setText("Information not available");
            }
            if (result.getRating() != null) {
                mBinding.cellListViewRatingBar.setRating((int) Math.round(result.getRating() / 5 * 3));
            }

            // todo : count workmates (hide imageview if none

            // todo : add a "no-image" icon if null
            if (result.getPhotos() != null) {
                String url = "https://maps.googleapis.com/maps/api/place/photo?" +
                        "key=" + BuildConfig.GOOGLE_API_KEY +
                        "&photoreference=" + result.getPhotos().get(0).getPhotoReference() +
                        "&maxwidth=200";

                glide.load(url).into(mBinding.cellListViewPhotoImageView);
            }

        }

        @Override
        public void onClick(View v) {
            mListener.onRestaurantClick(getAdapterPosition());
        }
    }
}
