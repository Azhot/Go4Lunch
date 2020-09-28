package fr.azhot.go4lunch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fr.azhot.go4lunch.databinding.FragmentWorkmatesBinding;

public class WorkmatesFragment extends Fragment {


    // private static
    private static final String TAG = "WorkmatesFragment";
    // variables
    private FragmentWorkmatesBinding mBinding;

    // public static
    public static WorkmatesFragment newInstance() {
        Log.d(TAG, "newInstance");

        WorkmatesFragment fragment = new WorkmatesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    // inherited methods
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        mBinding = FragmentWorkmatesBinding.inflate(inflater);
        return mBinding.getRoot();
    }
}
