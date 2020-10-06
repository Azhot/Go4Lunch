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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;

import fr.azhot.go4lunch.databinding.FragmentListViewBinding;

public class ListViewFragment extends Fragment {


    // private static
    private static final String TAG = "ListViewFragment";


    // public static
    public static ListViewFragment newInstance() {
        Log.d(TAG, "newInstance");

        ListViewFragment fragment = new ListViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    // variables
    private FragmentListViewBinding mBinding;
    private Context mContext;


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

        mBinding = FragmentListViewBinding.inflate(inflater);

        mBinding.recycleView.setLayoutManager(new LinearLayoutManager(mContext));
        // todo : if user asks for ListViewFragment before MapViewFragment could get nearby restaurants,
        //  the adapter will be initialized with an empty list
        mBinding.recycleView.setAdapter(new ListViewAdapter(Glide.with(this), MainActivity.CURRENT_RESTAURANTS));
        return mBinding.getRoot();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

}
