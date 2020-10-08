package fr.azhot.go4lunch.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.ActivityLoginBinding;
import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.viewmodel.UserViewModel;

import static fr.azhot.go4lunch.util.AppConstants.RC_GOOGLE_SIGN_IN;

public class LoginActivity extends AppCompatActivity {


    // private static
    private static final String TAG = "LoginActivity";


    // variables
    private ActivityLoginBinding mBinding;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser mCurrentUser;
    private CallbackManager mCallbackManager;
    private AuthCredential mUpdatedAuthCredential;
    private LoginManager mLoginManager;
    private UserViewModel mUserViewModel;


    // inherited methods
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        mBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        configureGoogleSignIn();
        configureFacebookSignIn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser != null) {
            navigateToMainActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");

        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuthWithCredential(credential);
            } catch (ApiException e) {
                Log.e(TAG, "onActivityResult: Google sign in failed.", e);
            }
        }
    }


    // methods
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_facebook_login_button:
                Log.d(TAG, "onClick: facebook login button");

                signInWithFacebook();
                break;
            case R.id.login_google_login_button:
                Log.d(TAG, "onClick: google login button");

                signInWithGoogle();
                break;
            default:
                break;
        }
    }

    private void configureGoogleSignIn() {
        Log.d(TAG, "configureGoogleSignIn");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    private void configureFacebookSignIn() {
        mCallbackManager = CallbackManager.Factory.create();
        mLoginManager = LoginManager.getInstance();
        mLoginManager.logOut();
        mLoginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook sign in: onSuccess");

                firebaseAuthWithCredential(FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken()));
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook sign in: onCancel");

                Toast.makeText(LoginActivity.this, R.string.sign_in_canceled, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "facebook sign in: onError", error);

                // todo : just a patch-up - should check doc for new implementation since deprecated
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
                    Toast.makeText(LoginActivity.this, R.string.unkown_sign_in_error, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.no_connection_error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void signInWithFacebook() {
        mLoginManager.logInWithReadPermissions(LoginActivity.this, Arrays.asList(
                "email",
                "public_profile"));
    }

    private void firebaseAuthWithCredential(AuthCredential credential) {
        Log.d(TAG, "firebaseAuthWithCredential");

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "firebaseAuthWithCredential: success.");

                            // since task is successful, we can assert mCurrentUser is not null
                            mCurrentUser = task.getResult().getUser();
                            assert mCurrentUser != null;

                            // this is called only when user tries to sign in to Facebook
                            // with an account already existing with the email address with
                            // Google sign in (see below)
                            if (mUpdatedAuthCredential != null) {
                                mCurrentUser.linkWithCredential(mUpdatedAuthCredential);
                            }
                            createUserInFirestore(mCurrentUser);
                            navigateToMainActivity();
                        } else {
                            Log.e(TAG, "firebaseAuthWithCredential: failure.", task.getException());

                            // check whether the account already exists with Google sign in
                            // (FirebaseAuthUserCollisionException), and if it does, call Google
                            // sign in to trigger linkWithCredential (see above)
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                FirebaseAuthUserCollisionException e = (FirebaseAuthUserCollisionException) task.getException();
                                if (e.getErrorCode().equals("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL")) {
                                    makeAlertDialogExistingSignIn(e.getEmail(), e.getUpdatedCredential());
                                } else {
                                    Toast.makeText(LoginActivity.this, R.string.unkown_sign_in_error, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                // todo : just a patch-up - should check doc for new implementation since deprecated
                                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
                                    Toast.makeText(LoginActivity.this, R.string.unkown_sign_in_error, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, R.string.no_connection_error, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                });
    }

    private void navigateToMainActivity() {
        Log.d(TAG, "navigateToMainActivity");

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void makeAlertDialogExistingSignIn(String email, AuthCredential credential) {
        new AlertDialog.Builder(this)
                .setTitle("Email address already linked to an existing account.")
                .setMessage(email + " is already linked to an existing account via Google sign in.\n" +
                        "Would you like to log in to your Google account to link both " +
                        "Google and Facebook sign in methods to your Go4Lunch account ?")
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUpdatedAuthCredential = credential;
                        signInWithGoogle();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .create()
                .show();
    }

    private void createUserInFirestore(FirebaseUser user) {
        String uid = user.getUid();
        String name = user.getDisplayName();
        String email = user.getEmail();
        String urlPicture = (user.getPhotoUrl() != null)
                ? user.getPhotoUrl().toString()
                : null;

        mUserViewModel.createUser(new User(uid, name, email, urlPicture));
    }
}
