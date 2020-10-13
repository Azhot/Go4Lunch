package fr.azhot.go4lunch.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.CellWorkmatesBinding;
import fr.azhot.go4lunch.model.User;

public class WorkmatesAdapter extends FirestoreRecyclerAdapter<User, WorkmatesAdapter.WorkmateViewHolder> {


    // variables
    private final RequestManager glide;


    // constructor
    public WorkmatesAdapter(@NonNull FirestoreRecyclerOptions<User> options, RequestManager glide) {
        super(options);
        this.glide = glide;
    }


    // inherited methods
    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkmateViewHolder(CellWorkmatesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position, @NonNull User model) {
        holder.bindWithUserDetails(model, glide);
    }


    // view holder
    public static class WorkmateViewHolder extends RecyclerView.ViewHolder {

        private CellWorkmatesBinding mBinding;

        public WorkmateViewHolder(CellWorkmatesBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        public void bindWithUserDetails(User user, RequestManager glide) {
            glide.load(user.getUrlPicture())
                    .circleCrop()
                    .into(mBinding.cellWorkmatesProfilePicture);

            String firstName = user.getName().split(" ")[0];
            String restaurantName = user.getSelectedRestaurantName();
            String stringHasDecided = mBinding
                    .getRoot()
                    .getContext()
                    .getResources()
                    .getString(R.string.has_decided, firstName, restaurantName);
            String stringHasNotDecided = mBinding
                    .getRoot()
                    .getContext()
                    .getResources()
                    .getString(R.string.has_not_decided, firstName);

            if (user.getSelectedRestaurantId() != null) {
                mBinding.cellWorkmatesTextView.setText(stringHasDecided);
                mBinding.cellWorkmatesTextView.setTextAppearance(
                        mBinding.getRoot().getContext(),
                        R.style.TextHasDecided);
            } else {
                mBinding.cellWorkmatesTextView.setText(stringHasNotDecided);
                mBinding.cellWorkmatesTextView.setTextAppearance(
                        mBinding.getRoot().getContext(),
                        R.style.TextHasNotDecided);
            }
        }
    }
}
