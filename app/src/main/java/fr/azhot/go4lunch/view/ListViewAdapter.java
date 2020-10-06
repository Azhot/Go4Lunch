package fr.azhot.go4lunch.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.util.List;

import fr.azhot.go4lunch.BuildConfig;
import fr.azhot.go4lunch.databinding.CellListViewBinding;
import fr.azhot.go4lunch.model.NearbySearch;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.RestaurantViewHolder> {

    // variables
    private RequestManager mGlide;
    private Listener mListener;
    private List<NearbySearch.Result> mRestaurants;

    // constructor
    public ListViewAdapter(RequestManager glide, List<NearbySearch.Result> restaurants, ListViewAdapter.Listener listener) {
        this.mGlide = glide;
        this.mRestaurants = restaurants;
        this.mListener = listener;
    }

    // inherited methods
    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantViewHolder(CellListViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), mListener);
    }


    public interface Listener {
        void onRestaurantClick(int position);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        holder.onBindData(mRestaurants.get(position), mGlide);
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }


    // methods
    public void setRestaurants(List<NearbySearch.Result> restaurants) {
        this.mRestaurants = restaurants;
        notifyDataSetChanged();
    }

    // view holder
    public static class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CellListViewBinding mBinding;
        private ListViewAdapter.Listener mListener;

        public RestaurantViewHolder(CellListViewBinding binding, ListViewAdapter.Listener listener) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.mListener = listener;
            binding.getRoot().setOnClickListener(this);
        }

        public void onBindData(NearbySearch.Result result, RequestManager glide) {
            if (result.getName() != null) mBinding.nameTextView.setText(result.getName());
            // mBinding.distanceTextView.setText(); // todo : calculate distance ?
            if (result.getVicinity() != null)
                mBinding.vicinityTextView.setText(result.getVicinity());
            if (result.getOpeningHours() != null) {
                if (result.getOpeningHours().getOpenNow()) {
                    mBinding.openingHoursTextView.setText("Open");
                } else {
                    mBinding.openingHoursTextView.setText("Closed");
                }
            } else {
                mBinding.openingHoursTextView.setText("Information not available");
            }
            if (result.getRating() != null) {
                double d = result.getRating();
                int i = (int) (d / 5 * 3);
                mBinding.ratingBar.setRating(i);
            }

            // todo : count workmates (hide imageview if none

            // todo : add a "no-image" icon if null
            if (result.getPhotos() != null) {
                String url = "https://maps.googleapis.com/maps/api/place/photo?" +
                        "key=" + BuildConfig.GOOGLE_API_KEY +
                        "&photoreference=" + result.getPhotos().get(0).getPhotoReference() +
                        "&maxwidth=200";

                glide.load(url).into(mBinding.photoImageView);
            }

        }

        @Override
        public void onClick(View v) {
            mListener.onRestaurantClick(getAdapterPosition());
        }
    }
}
