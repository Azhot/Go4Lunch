package fr.azhot.go4lunch.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import fr.azhot.go4lunch.databinding.CellWorkmatesFragmentBinding;
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
        return new WorkmateViewHolder(CellWorkmatesFragmentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position, @NonNull User model) {
        holder.bindWithUserDetails(model, glide);
    }


    // view holder
    public static class WorkmateViewHolder extends RecyclerView.ViewHolder {

        private CellWorkmatesFragmentBinding binding;

        public WorkmateViewHolder(CellWorkmatesFragmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindWithUserDetails(User user, RequestManager glide) {
            glide.load(user.getUrlPicture())
                    .circleCrop()
                    .into(binding.profilePicture);
            binding.text.setText(user.getName() + " is eating french (Le Zinc)");
        }
    }
}
