package fr.azhot.go4lunch.view;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.ActivityMainBinding;
import fr.azhot.go4lunch.util.PermissionsUtils;

import static fr.azhot.go4lunch.util.AppConstants.RC_PERMISSIONS;

public class MainActivity extends AppCompatActivity {


    // private static
    private static final String TAG = "MainActivity";


    // variables
    private ActivityMainBinding mBinding;
    private MapViewFragment mMapViewFragment;
    private ListViewFragment mListViewFragment;
    private WorkmatesFragment mWorkmatesFragment;
    private FirebaseAuth mAuth;


    // inherited methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        mAuth = FirebaseAuth.getInstance();
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.mainToolbar);
        setUpDrawerNavigation();
        setUpDrawerWithUserDetails();
        setUpBottomNavigation();
        launchFragment();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSIONS) {
            if (grantResults.length > 0) {
                for (int i : grantResults) {
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: permissions denied.");
                        forceUserChoiceOnPermissions(this);
                        return;
                    }
                }
            }
            //todo: might not be kept here
            launchFragment();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");

        if (mBinding.mainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.mainDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        String[] test = new String[]{"abc", "def", "ghi"};

        SearchView.SearchAutoComplete textArea = searchView.findViewById(R.id.search_src_text);
        // todo : question to Virgil : should I do a "assert user != null" here ? what is the best practice ?
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, test);
        textArea.setAdapter(arrayAdapter);

        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(getString(R.string.search_bar_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // searching here
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    // methods
    private void checkLocationPermissions() {
        Log.d(TAG, "checkPermissions");

        if (!PermissionsUtils.isLocationPermissionGranted(this)) {
            PermissionsUtils.getLocationPermission(this, RC_PERMISSIONS);
        }
    }

    private void launchFragment() {
        Log.d(TAG, "launchFragment");

        if (PermissionsUtils.isLocationPermissionGranted(this)) {
            mMapViewFragment = (mMapViewFragment == null) ? MapViewFragment.newInstance() : mMapViewFragment;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(mBinding.mainNavHostFragment.getId(), mMapViewFragment)
                    .commit();
            setTitle(R.string.list_view_title);
        } else {
            checkLocationPermissions();
        }
    }

    private void forceUserChoiceOnPermissions(final AppCompatActivity appCompatActivity) {
        Log.d(TAG, "forceUserChoiceOnPermissions");

        new AlertDialog.Builder(appCompatActivity)
                .setTitle(R.string.permissions_dialog_title)
                .setMessage(R.string.permissions_dialog_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkLocationPermissions();
                    }
                })
                .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appCompatActivity.finish();
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK &&
                                event.getAction() == KeyEvent.ACTION_UP &&
                                !event.isCanceled()) {
                            dialog.cancel();
                            checkLocationPermissions();
                            return true;
                        }
                        return false;
                    }
                })
                .show();
    }

    private void setUpDrawerNavigation() {
        Log.d(TAG, "setUpDrawerNavigation");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                mBinding.mainDrawerLayout,
                mBinding.mainToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mBinding.mainDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mBinding.mainNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_your_lunch:
                        // launch details about the restaurant selected by the user
                        break;
                    case R.id.nav_settings:
                        // launch settings, e.g. to set up notifications
                        break;
                    case R.id.nav_logout:
                        mAuth.signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        break;
                    default:
                        Log.d(TAG, "onNavigationItemSelected: could not match user click with id.");
                        break;
                }

                mBinding.mainDrawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });
    }

    // configuring nav drawer header
    private void setUpDrawerWithUserDetails() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        AppCompatImageView userPicture = mBinding.mainNavView
                .getHeaderView(0)
                .findViewById(R.id.drawer_header_user_picture);
        Glide.with(this)
                // todo : add a "no image" picture here instead of null
                .load((currentUser != null)
                        ? currentUser.getPhotoUrl()
                        : null)
                .circleCrop()
                .into(userPicture);

        AppCompatTextView userName = mBinding.mainNavView
                .getHeaderView(0)
                .findViewById(R.id.drawer_header_user_name);
        userName.setText(currentUser.getDisplayName());

        AppCompatTextView userEmail = mBinding.mainNavView
                .getHeaderView(0)
                .findViewById(R.id.drawer_header_user_email);
        userEmail.setText(currentUser.getEmail());
    }

    private void setUpBottomNavigation() {
        Log.d(TAG, "setUpBottomNavigation");

        mBinding.mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment;
                switch (item.getItemId()) {
                    case R.id.list_view:
                        Log.d(TAG, "onNavigationItemSelected: list view fragment");
                        selectedFragment = (mListViewFragment == null) ? ListViewFragment.newInstance() : mListViewFragment;
                        setTitle(R.string.list_view_title);
                        break;
                    case R.id.workmates:
                        Log.d(TAG, "onNavigationItemSelected: workmates fragment");
                        selectedFragment = (mWorkmatesFragment == null) ? WorkmatesFragment.newInstance() : mWorkmatesFragment;
                        setTitle(R.string.workmates_title);
                        break;
                    case R.id.map_view:
                        Log.d(TAG, "onNavigationItemSelected: map view fragment");
                        selectedFragment = (mMapViewFragment == null) ? MapViewFragment.newInstance() : mMapViewFragment;
                        setTitle(R.string.map_view_title);
                        break;
                    default:
                        Log.d(TAG, "onNavigationItemSelected: could not match user click with id.");
                        selectedFragment = (mMapViewFragment == null) ? MapViewFragment.newInstance() : mMapViewFragment;
                        setTitle(R.string.map_view_title);
                        break;
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit, R.anim.fragment_open_enter, R.anim.fragment_close_exit)
                        .replace(mBinding.mainNavHostFragment.getId(), selectedFragment)
                        .commit();

                return true;
            }
        });
    }
}