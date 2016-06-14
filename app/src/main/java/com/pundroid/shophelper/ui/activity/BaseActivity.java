package com.pundroid.shophelper.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.pundroid.shophelper.R;
import com.pundroid.shophelper.model.User;
import com.pundroid.shophelper.ui.login.CreateAccountActivity;
import com.pundroid.shophelper.ui.login.LoginActivity;
import com.pundroid.shophelper.utils.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by pumba30 on 29.05.2016.
 */
public class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String LOG_TAG = BaseActivity.class.getSimpleName();
    public static final int NUMBER_CHARACTERS = 6;
    protected GoogleApiClient mGoogleApiClient;
    protected FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Setup the Google API object to allow Google logins */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        /**
         * Build a GoogleApiClient with access to the Google Sign-In API and the
         * options specified by gso.
         */
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Log.d(LOG_TAG, "User email: " + user.getEmail());
                    startMainActivity();
                } else {

                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.menu_base, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initializeBackground(LinearLayout linearLayout) {

        /**
         * Set different background image for landscape and portrait layouts
         */
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            linearLayout.setBackgroundResource(R.drawable.background_loginscreen_land);
        } else {

            linearLayout.setBackgroundResource(R.drawable.background_loginscreen);
        }
    }

    protected boolean validateUserEmail(String userEmail, EditText editText) {
        boolean valid = true;
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if (!pattern.matcher(userEmail).matches()) {
            editText.setError(getString(R.string.enter_valid_email));
            valid = false;
        } else if (TextUtils.isEmpty(userEmail)) {
            editText.setError(getString(R.string.set_error_required));
            valid = false;
        } else {
            editText.setError(null);
        }
        return valid;
    }

    protected boolean validateUserPassword(String userPassword, EditText editText) {
        boolean valid = true;
        if (TextUtils.isEmpty(userPassword)) {
            editText.setError(getString(R.string.set_error_required));
            valid = false;
        } else {
            editText.setError(null);
        }

        if (userPassword.length() < NUMBER_CHARACTERS) {
            editText.setError(getString(R.string.the_password_must_have_at_least_6_characters));
            valid = false;
        } else {
            editText.setError(null);
        }
        return valid;
    }

    protected boolean validateUserName(String userName, EditText editText) {
        boolean valid = true;
        if (TextUtils.isEmpty(userName)) {
            editText.setError(getString(R.string.set_error_required));
            valid = false;
        } else {
            editText.setError(null);
        }
        return valid;
    }

    protected void dissmisProgressDialog(ProgressDialog dialog) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!((this instanceof LoginActivity) || (this instanceof CreateAccountActivity))) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Creates a new user in Firebase from the Java POJO
     */
    protected void createUserInFireBase(final String encodedEmail, final String userName) {
        final Firebase usersRef = new Firebase(Constants.FIREBASE_URL_USERS).child(encodedEmail);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Map<String, Object> timestamp = new HashMap<>();
                    timestamp.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                    User newUser = new User(encodedEmail, userName, timestamp);
                    usersRef.setValue(newUser);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(LOG_TAG, getString(R.string.log_error_occurred) + firebaseError.getMessage());
            }
        });
    }


    protected void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
