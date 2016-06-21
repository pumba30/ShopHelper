package com.pundroid.shophelper.ui.activity.login;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.pundroid.shophelper.R;
import com.pundroid.shophelper.ui.activity.BaseActivity;
import com.pundroid.shophelper.utils.Constants;
import com.pundroid.shophelper.utils.Utils;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by pumba30 on 04.06.2016.
 */

public class
CreateAccountActivity extends BaseActivity {
    private static final String LOG_TAG = CreateAccountActivity.class.getSimpleName();

    private ProgressDialog mAuthProgressDialog;
    private EditText mEditTextUsernameCreate, mEditTextEmailCreate;
    private SecureRandom mRandom = new SecureRandom();
    private TextView mSignInTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        initializeScreen();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void initializeScreen() {
        mEditTextUsernameCreate = (EditText) findViewById(R.id.edit_text_username_create);
        mEditTextEmailCreate = (EditText) findViewById(R.id.edit_text_email_create);
        mSignInTextView = (TextView) findViewById(R.id.tv_sign_in);
        mSignInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInPressed();
            }
        });
        LinearLayout linearLayoutCreateAccountActivity
                = (LinearLayout) findViewById(R.id.linear_layout_create_account_activity);
        initializeBackground(linearLayoutCreateAccountActivity);

        // Setup the progress dialog that is displayed later when authenticating with Firebase
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getResources().getString(R.string.progress_dialog_creating_user_with_firebase));
        mAuthProgressDialog.setCancelable(false);
    }


    //Open LoginActivity when user taps on "Sign in" textView
    public void onSignInPressed() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


     // Create new account using Firebase email/password provider
    public void onCreateAccountPressed(final View view) {
        mUserName = mEditTextUsernameCreate.getText().toString();
        mUserEmail = mEditTextEmailCreate.getText().toString().toLowerCase();
        mUserPassword = new BigInteger(130, mRandom).toString(32);
        Log.d(LOG_TAG, "User Password " + mUserPassword);

        Log.d(LOG_TAG, "createAccount:" + mUserEmail);

        if (!validateUserEmail(mUserEmail, mEditTextEmailCreate)
                || !validateUserName(mUserName, mEditTextUsernameCreate)) {
            return;
        }

        mAuthProgressDialog.show();
        // START create user with email

        mAuth.createUserWithEmailAndPassword(mUserEmail, mUserPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            resetPasswordInFirebase();
                        }
                    }
                });
    }

    private void resetPasswordInFirebase() {
        mAuth.sendPasswordResetEmail(mUserEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Utils.toast(getApplicationContext(), "Password sent to your email!");
                            createUserInFirebaseHelper();
                            Log.d(LOG_TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            Utils.toast(getApplicationContext(),
                                    getString(R.string.create_account_successful));
                            startIntentForProcessingEmail();
                        } else {
                            Log.d(LOG_TAG, "Error createUser " + task.getException());
                            String taskString = task.getException().toString();
                            String message =
                                    taskString.substring(taskString.lastIndexOf(":")).replace(":", "");
                            Utils.toast(getApplicationContext(), message);
                        }
                        dissmisProgressDialog(mAuthProgressDialog);
                    }
                });
    }


    //Getting the default password from Firebase on your mail.
    private void startIntentForProcessingEmail() {
        Intent intent = new Intent((Intent.ACTION_MAIN));
        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
        try {
            startActivity(intent);
            finish();
        } catch (ActivityNotFoundException e) {
            //if user does't have app to handle email
            Utils.toast(getApplicationContext(),
                    getString(R.string.install_app_for_processing_email));
        }
    }


    private void createUserInFirebaseHelper() {
        String unprocessedEmail = mUserEmail.toLowerCase();
        Utils.saveToSharedPreferences(Constants.KEY_EMAIL, unprocessedEmail, getApplicationContext());
        Utils.saveToSharedPreferences(Constants.KEY_NAME_OWNER_LIST, mUserName, getApplicationContext());
        Utils.saveToSharedPreferences(Constants.KEY_SIGN_UP_EMAIL, unprocessedEmail, getApplicationContext());
        final String encodedEmail = Utils.encodeEmail(unprocessedEmail);
        createUserInFireBase(encodedEmail, mUserName);
        mAuth.signInWithEmailAndPassword(mUserEmail, mUserPassword);
    }
}
