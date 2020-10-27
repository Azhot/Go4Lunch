package fr.azhot.go4lunch.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.annotation.Nullable;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.ActivitySettingsBinding;
import fr.azhot.go4lunch.viewmodel.AppViewModel;

import static fr.azhot.go4lunch.util.AppConstants.NOTIFICATIONS_PREFERENCES_NAME;
import static fr.azhot.go4lunch.util.AppConstants.SHARED_PREFERENCES_NAME;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private ActivitySettingsBinding mBinding;
    private SharedPreferences mSharedPreferences;
    private boolean mIsNotificationsActivated;
    private AppViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        getNotificationsActivatedFromSharedPreferences();
        setUpNotificationCheckBox();
        setUpUserInformationEditTexts();
        setUpConfirmButton();
        setContentView(mBinding.getRoot());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getNotificationsActivatedFromSharedPreferences() {
        mSharedPreferences = this.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        mIsNotificationsActivated = mSharedPreferences.getBoolean(NOTIFICATIONS_PREFERENCES_NAME, true);
    }

    private void setUpNotificationCheckBox() {
        mBinding.notificationCheckBox.setChecked(mIsNotificationsActivated);
        mBinding.notificationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsNotificationsActivated = isChecked;
                mSharedPreferences
                        .edit()
                        .putBoolean(NOTIFICATIONS_PREFERENCES_NAME, mIsNotificationsActivated)
                        .apply();
            }
        });
    }

    private void setUpUserInformationEditTexts() {
        mBinding.userSettingsNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mBinding.userSettingsNameEditText.getText() != null &&
                        mBinding.userSettingsPictureEditText.getText() != null) {
                    boolean isReady = mBinding.userSettingsNameEditText.getText().length() > 3
                            || mBinding.userSettingsPictureEditText.getText().length() > 5;
                    mBinding.confirmButton.setEnabled(isReady);
                    mBinding.confirmButton.setTextColor(isReady
                            ? ContextCompat.getColor(SettingsActivity.this, R.color.colorDrawer)
                            : ContextCompat.getColor(SettingsActivity.this, R.color.colorLightGrey));
                }
            }
        });

        mBinding.userSettingsPictureEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mBinding.userSettingsNameEditText.getText() != null &&
                        mBinding.userSettingsPictureEditText.getText() != null) {
                    boolean isReady = mBinding.userSettingsNameEditText.getText().length() > 3
                            || mBinding.userSettingsPictureEditText.getText().length() > 5;
                    mBinding.confirmButton.setEnabled(isReady);
                    mBinding.confirmButton.setTextColor(isReady
                            ? ContextCompat.getColor(SettingsActivity.this, R.color.colorDrawer)
                            : ContextCompat.getColor(SettingsActivity.this, R.color.colorLightGrey));
                }
            }
        });
    }

    private void setUpConfirmButton() {
        String[] name = new String[1];
        String[] photoUrl = new String[1];

        mBinding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.userSettingsNameEditText.getText() != null) {
                    name[0] = mBinding.userSettingsNameEditText.getText().length() > 3
                            ? mBinding.userSettingsNameEditText.getText().toString().trim()
                            : "";
                }
                if (mBinding.userSettingsPictureEditText.getText() != null) {
                    photoUrl[0] = mBinding.userSettingsPictureEditText.getText().length() > 5
                            ? mBinding.userSettingsPictureEditText.getText().toString().trim()
                            : "";
                }
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    mViewModel.updateUserInformation(
                            currentUser.getUid(),
                            name[0],
                            photoUrl[0])
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SettingsActivity.this, R.string.information_updated, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SettingsActivity.this, R.string.error_information_not_updated, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }
}
