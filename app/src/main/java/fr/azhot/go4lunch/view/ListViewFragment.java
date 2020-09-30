package fr.azhot.go4lunch.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fr.azhot.go4lunch.databinding.FragmentListViewBinding;

public class ListViewFragment extends Fragment {


    // private static
    private static final String TAG = "ListViewFragment";
    // variables
    private FragmentListViewBinding mBinding;

    // public static
    public static ListViewFragment newInstance() {
        Log.d(TAG, "newInstance");

        ListViewFragment fragment = new ListViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    // inherited methods
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        mBinding = FragmentListViewBinding.inflate(inflater);
        return mBinding.getRoot();
    }
}
