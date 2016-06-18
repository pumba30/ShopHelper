package com.pundroid.shophelper.ui.activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.pundroid.shophelper.R;
import com.pundroid.shophelper.ui.activity.BaseActivity;
import com.pundroid.shophelper.utils.Constants;
import com.pundroid.shophelper.utils.Utils;

/**
 * Created by pumba30 on 04.06.2016.
 */

/**
 * Represents Sign in screen and functionality of the app
 */
public class LoginActivity extends BaseActivity {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    /* A dialog that is presented until the Firebase authentication finished. */
    private ProgressDialog mAuthProgressDialog;
    private EditText mEditTextEmailInput, mEditTextPasswordInput;
    /* Request code used to invoke sign in user interactions for Google+ */
    public static final int RC_GOOGLE_SIGN_IN = 1;
    /* A Google account object that is populated if the user signs in with Google */
    private GoogleSignInAccount mGoogleAccount;
    private AuthCredential mCredentialGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        initializeScreen();

        /**
         * Call signInPassword() when user taps "Done" keyboard action
         */
        mEditTextPasswordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    signInPassword();
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    // Open CreateAccountActivity when user taps on "Sign up" TextView
    public void onSignUpPressed(View view) {
        Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
        startActivity(intent);
    }

    public void initializeScreen() {
        mEditTextEmailInput = (EditText) findViewById(R.id.edit_text_email);
        mEditTextPasswordInput = (EditText) findViewById(R.id.edit_text_password);
        LinearLayout linearLayoutLoginActivity
                = (LinearLayout) findViewById(R.id.linear_layout_login_activity);
        initializeBackground(linearLayoutLoginActivity);

        setTextEmailInTextView();
        TextView signInTextView = (TextView) findViewById(R.id.login_with_password);

        if (signInTextView != null) {
            signInTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signInPassword();
                }
            });
        }
        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getString(R.string.progress_dialog_authenticating_with_firebase));
        mAuthProgressDialog.setCancelable(false);

        setupGoogleSignIn();
    }

    private void setTextEmailInTextView() {
        if (Utils.getUserProviderId().equals(Constants.PASSWORD_PROVIDER_ID)) {
            String email = Utils.getPreferencesValue(Constants.KEY_SIGN_UP_EMAIL, "", getApplicationContext());
            mEditTextEmailInput.setText(email);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTextEmailInTextView();
    }

    /**
     * Sign in with Password provider (used when user taps "Done" action on keyboard)
     */
    public void signInPassword() {
        final String userEmail = mEditTextEmailInput.getText().toString();
        String userPassword = mEditTextPasswordInput.getText().toString();

        if (!validateUserEmail(userEmail, mEditTextEmailInput)) {
            return;
        }

        mAuthProgressDialog.show();
        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            String taskString = task.getException().toString();
                            String message = taskString.substring(taskString.lastIndexOf(":")).replace(":", "");
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();

                        } else {
                            Log.d(LOG_TAG, "signInWhithEmail:onComplete:" + task.isSuccessful());
                            Toast.makeText(LoginActivity.this, R.string.auth_successful,
                                    Toast.LENGTH_SHORT).show();
                            String unprocessedEmail = userEmail.toLowerCase();
                            Utils.saveToSharedPreferences(Constants.KEY_EMAIL,
                                    unprocessedEmail, getApplicationContext());
                            startMainActivity();
                        }
                        dissmisProgressDialog(mAuthProgressDialog);
                    }
                });
    }


    private void setupGoogleSignIn() {
        SignInButton signInButton = (SignInButton) findViewById(R.id.login_with_google);
        if (signInButton != null) {
            signInButton.setSize(SignInButton.SIZE_WIDE);

            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSignInGooglePressed(v);
                }
            });
        }
    }

    // Sign in with Google plus when user clicks "Sign in with Google" textView (button)
    public void onSignInGooglePressed(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
        mAuthProgressDialog.show();
    }

    //This callback is triggered when any startActivityForResult finishes. The requestCode maps to
    //the value passed into startActivityForResult.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...); */
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(LOG_TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            /* Signed in successfully, get the OAuth token */
            mGoogleAccount = result.getSignInAccount();
            firebaseAuthWithGoogle(mGoogleAccount);
        } else {
            if (result.getStatus().getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                Utils.toast(getApplicationContext(), getString(R.string.sign_in_cancelled));
            } else {
                Utils.toast(getApplicationContext(), getString(R.string.error_handling_sign_in)
                        + result.getStatus().getStatusMessage());
            }
            mAuthProgressDialog.dismiss();
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        Log.d(LOG_TAG, "firebaseAuthWithGoogle:" + account.getId());
        mCredentialGoogle = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(mCredentialGoogle)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d(LOG_TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(LOG_TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                            String unprocessedEmail = account.getEmail().toLowerCase();
                            Utils.saveToSharedPreferences(Constants.KEY_EMAIL,
                                    unprocessedEmail, getApplicationContext());
                            Utils.saveToSharedPreferences(Constants.KEY_NAME_OWNER_LIST,
                                    account.getDisplayName(), getApplicationContext());

                            //Encode user email replacing "." with "," to be able to use it
                            //as a Firebase db key
                            String encodedEmail = Utils.encodeEmail(unprocessedEmail);
                            String userName = account.getDisplayName();
                            createUserInFireBase(encodedEmail, userName);
                            startMainActivity();
                        }
                        mAuthProgressDialog.dismiss();
                    }
                });
    }
}
