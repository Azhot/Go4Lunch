package fr.azhot.go4lunch.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.Arrays;

import fr.azhot.go4lunch.R;
import fr.azhot.go4lunch.databinding.ActivityLoginBinding;
import fr.azhot.go4lunch.databinding.AltertDialogLoginBinding;
import fr.azhot.go4lunch.model.User;
import fr.azhot.go4lunch.viewmodel.AppViewModel;

public class LoginActivity extends AppCompatActivity {


    // private static
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final int RC_GOOGLE_SIGN_IN = 4567;
    public static final int RC_EMAIL_SIGN_IN = 5678;


    // variables
    private ActivityLoginBinding mBinding;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser mCurrentUser;
    private CallbackManager mCallbackManager;
    private AuthCredential mUpdatedAuthCredential;
    private LoginManager mLoginManager;
    private AppViewModel mAppViewModel;


    // inherited methods
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        mBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        mAppViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        configureGoogleSignIn();
        configureFacebookSignIn();
        configureTwitterSignIn();
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

        mBinding.loginTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }


    // methods
    public void onClick(View view) {
        Log.d(TAG, "onClick");

        if (view.getId() == R.id.login_facebook_login_button) {
            Log.d(TAG, "onClick: facebook login button");

            signInWithFacebook();
        } else if (view.getId() == R.id.login_google_login_button) {
            Log.d(TAG, "onClick: google login button");

            signInWithGoogle();
        } else if (view.getId() == R.id.login_twitter_login_interface_button) {
            Log.d(TAG, "onClick: twitter login button");

            signInWithTwitter();
        } else if (view.getId() == R.id.login_email_login_text_view) {
            Log.d(TAG, "onClick: email login text view");

            signInWithEmailAndPassword();
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
        Log.d(TAG, "signInWithGoogle");

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    private void configureFacebookSignIn() {
        Log.d(TAG, "configureFacebookSignIn");

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

                Toast.makeText(LoginActivity.this, R.string.sign_in_canceled, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "facebook sign in: onError", error);

                // should check connection status to send a "no connection"
                // message to user if not available
                Toast.makeText(LoginActivity.this, R.string.unknown_sign_in_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithFacebook() {
        Log.d(TAG, "signInWithFacebook");

        mLoginManager.logInWithReadPermissions(LoginActivity.this, Arrays.asList(
                "email",
                "public_profile"));
    }

    private void configureTwitterSignIn() {
        Log.d(TAG, "configureTwitterSignIn");

        TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));
        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(twitterAuthConfig)
                .debug(true)
                .build();
        Twitter.initialize(twitterConfig);
    }

    private void signInWithTwitter() {
        Log.d(TAG, "signInWithFacebook");

        mBinding.loginTwitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(TAG, "twitter sign in : success");

                TwitterSession session = result.data;
                AuthCredential authCredential = TwitterAuthProvider.getCredential(session.getAuthToken().token,
                        result.data.getAuthToken().secret);
                firebaseAuthWithCredential(authCredential);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.e(TAG, "twitter sign in : failure", exception);
            }
        });
        mBinding.loginTwitterLoginButton.performClick();
    }

    private void firebaseAuthWithCredential(AuthCredential credential) {
        Log.d(TAG, "firebaseAuthWithCredential");

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "firebaseAuthWithCredential: success.");

                            mCurrentUser = task.getResult().getUser();
                            if (mCurrentUser != null) {
                                if (mUpdatedAuthCredential != null) {
                                    mCurrentUser.linkWithCredential(mUpdatedAuthCredential);
                                }
                                createUserInFirestore(mCurrentUser);
                                navigateToMainActivity();
                            }
                        } else {
                            Log.e(TAG, "firebaseAuthWithCredential: failure.", task.getException());

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                FirebaseAuthUserCollisionException e = (FirebaseAuthUserCollisionException) task.getException();
                                if (e.getErrorCode().equals("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL")) {
                                    makeAlertDialogExistingSignIn(e.getEmail(), e.getUpdatedCredential());
                                } else {
                                    Toast.makeText(LoginActivity.this, R.string.unknown_sign_in_error, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, R.string.unknown_sign_in_error, Toast.LENGTH_SHORT).show();
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
        Log.d(TAG, "makeAlertDialogExistingSignIn");

        String providerName;
        switch (credential.getProvider()) {
            case TwitterAuthProvider.PROVIDER_ID:
                providerName = "Twitter";
                break;
            case FacebookAuthProvider.PROVIDER_ID:
                providerName = "Facebook";
                break;
            default:
                providerName = "this";
                break;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.email_already_linked_title)
                .setMessage(email + getString(R.string.email_already_linked_message, providerName))
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
        Log.d(TAG, "createUserInFirestore");

        String uid = user.getUid();
        String name = user.getDisplayName();
        String email = user.getEmail();
        String urlPicture = user.getPhotoUrl() != null
                ? user.getPhotoUrl().toString()
                : "https://cdn.pixabay.com/photo/2016/08/08/09/17/avatar-1577909_1280.png";

        mAppViewModel.createOrUpdateUser(new User(uid, name, email, urlPicture))
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createOrUpdateUser: onSuccess");
                        } else {
                            Log.e(TAG, "createOrUpdateUser: onFailure", task.getException());
                        }
                    }
                });
    }

    private void signInWithEmailAndPassword() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        AltertDialogLoginBinding altertDialogLoginBinding = AltertDialogLoginBinding.inflate(getLayoutInflater());
        alert.setView(altertDialogLoginBinding.getRoot());
        alert.setCancelable(false);
        AlertDialog dialog = alert.create();

        final String[] name = new String[1];
        final String[] email = new String[1];
        final String[] password = new String[1];

        altertDialogLoginBinding.loginAlertDialogLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name[0] = altertDialogLoginBinding.loginAlertDialogNameEditText.getText().toString();
                email[0] = altertDialogLoginBinding.loginAlertDialogEmailEditText.getText().toString();
                password[0] = altertDialogLoginBinding.loginAlertDialogPasswordEditText.getText().toString();

                if (email[0].isEmpty() || password[0].isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please provide valid e-mail and password.", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                }
            }
        });

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        altertDialogLoginBinding.loginAlertDialogCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (email[0] != null) {
                    mAuth.createUserWithEmailAndPassword(email[0], password[0])
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "createUserWithEmail: success");

                                        mCurrentUser = mAuth.getCurrentUser();

                                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name[0].isEmpty()
                                                        ? email[0].split("@")[0]
                                                        : name[0])
                                                .build();

                                        mCurrentUser.updateProfile(userProfileChangeRequest)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            createUserInFirestore(mCurrentUser);
                                                            navigateToMainActivity();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Log.w(TAG, "createUserWithEmail: failure", task.getException());

                                        Exception exception = task.getException();
                                        mAuth.signInWithEmailAndPassword(email[0], password[0])
                                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "signInWithEmail: success");
                                                            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                                                            intent.putExtra("email", email[0]);
                                                            startActivityForResult(intent, RC_EMAIL_SIGN_IN);
                                                        } else {
                                                            Log.w(TAG, "signInWithEmail: failure", task.getException());
                                                            if (task.getException().getMessage().equals("The password is invalid or the user does not have a password.")) {
                                                                Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(LoginActivity.this, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                }
            }
        });
    }
}
