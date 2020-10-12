package fr.azhot.go4lunch.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.CellRestaurantDetailsBinding;
import fr.azhot.go4lunch.model.User;

public class RestaurantDetailsAdapter extends FirestoreRecyclerAdapter<User, RestaurantDetailsAdapter.RestaurantDetailsViewHolder> {


    // variables
    private final RequestManager glide;


    // constructor
    public RestaurantDetailsAdapter(@NonNull FirestoreRecyclerOptions<User> options, RequestManager glide) {
        super(options);
        this.glide = glide;
    }


    // inherited methods
    @NonNull
    @Override
    public RestaurantDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantDetailsViewHolder(CellRestaurantDetailsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull RestaurantDetailsViewHolder holder, int position, @NonNull User model) {
        holder.bindWithUserDetails(model, glide);
    }


    // view holder
    public static class RestaurantDetailsViewHolder extends RecyclerView.ViewHolder {

        private CellRestaurantDetailsBinding mBinding;

        public RestaurantDetailsViewHolder(CellRestaurantDetailsBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        public void bindWithUserDetails(User user, RequestManager glide) {
            glide.load(user.getUrlPicture())
                    .circleCrop()
                    .into(mBinding.cellRestaurantDetailsProfilePicture);

            String firstName = user.getName().split(" ")[0];
            String isJoining = mBinding
                    .getRoot()
                    .getContext()
                    .getResources()
                    .getString(R.string.is_joining, firstName);

            mBinding.cellRestaurantDetailsTextView.setText(isJoining);
        }
    }
}
