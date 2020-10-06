package fr.azhot.go4lunch.view;

import android.view.LayoutInflater;
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
    private final RequestManager mGlide;
    private List<NearbySearch.Result> mRestaurants;


    // constructor
    public ListViewAdapter(RequestManager glide, List<NearbySearch.Result> restaurants) {
        this.mGlide = glide;
        this.mRestaurants = restaurants;
    }


    // inherited methods
    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantViewHolder(CellListViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {

        private CellListViewBinding mBinding;

        public RestaurantViewHolder(CellListViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
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
    }
}
