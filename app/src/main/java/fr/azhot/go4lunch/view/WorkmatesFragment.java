package fr.azhot.go4lunch.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import fr.azhot.go4lunch.databinding.FragmentWorkmatesBinding;
import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.viewmodel.AppViewModel;

import static fr.azhot.go4lunch.util.AppConstants.CHOSEN_RESTAURANT_NAME_FIELD;

public class WorkmatesFragment extends Fragment {


    // private static
    private static final String TAG = "WorkmatesFragment";


    // variables
    private FragmentWorkmatesBinding mBinding;
    private AppViewModel mAppViewModel;
    private Context mContext;


    // public static
    public static WorkmatesFragment newInstance() {
        Log.d(TAG, "newInstance");

        WorkmatesFragment fragment = new WorkmatesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    // inherited methods
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        mBinding = FragmentWorkmatesBinding.inflate(inflater);

        mAppViewModel = new ViewModelProvider(this).get(AppViewModel.class);

        mBinding.workmatesRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mBinding.workmatesRecyclerView.setAdapter(new WorkmatesAdapter(
                generateOptionsForAdapter(mAppViewModel.getUsersQuery()
                        .orderBy(CHOSEN_RESTAURANT_NAME_FIELD, Query.Direction.DESCENDING)),
                Glide.with(this)));

        return mBinding.getRoot();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }


    // methods
    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }
}
