package fr.azhot.go4lunch.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.FragmentWorkmatesBinding;
import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.util.IntentUtils;
import fr.azhot.go4lunch.viewmodel.AppViewModel;

import static fr.azhot.go4lunch.util.AppConstants.SELECTED_RESTAURANT_ID_FIELD;

public class WorkmatesFragment extends Fragment implements WorkmatesAdapter.OnWorkmateClickListener {


    // private static
    private static final String TAG = WorkmatesFragment.class.getSimpleName();


    // variables
    private FragmentWorkmatesBinding mBinding;
    private Context mContext;
    private AppViewModel mViewModel;


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
        Log.d(TAG, "onAttach");

        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        mBinding = FragmentWorkmatesBinding.inflate(inflater);
        mBinding.workmatesRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        mViewModel = ViewModelProviders.of(requireActivity()).get(AppViewModel.class);
        initObservers();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");

        mContext = null;
    }

    @Override
    public void OnWorkmateClick(String placeId, String userName) {
        Log.d(TAG, "OnWorkmateClick");

        if (placeId != null) {
            Intent intent = IntentUtils.loadRestaurantDataIntoIntent(
                    mContext, RestaurantDetailsActivity.class, placeId);
            startActivity(intent);
        } else {
            String firstName = userName.split(" ")[0];
            Toast.makeText(mContext, getString(R.string.has_not_decided, firstName), Toast.LENGTH_SHORT).show();
        }
    }


    // methods
    private void initObservers() {
        Log.d(TAG, "initObservers");

        mBinding.workmatesRecyclerView.setAdapter(new WorkmatesAdapter(
                generateOptionsForAdapter(mViewModel.getUsersQuery()
                        .orderBy(SELECTED_RESTAURANT_ID_FIELD, Query.Direction.DESCENDING)),
                Glide.with(this),
                this));
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query) {
        Log.d(TAG, "generateOptionsForAdapter");

        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }
}
