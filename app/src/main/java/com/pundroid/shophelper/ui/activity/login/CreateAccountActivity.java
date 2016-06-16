package com.pundroid.shophelper.ui.activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.pundroid.shophelper.R;
import com.pundroid.shophelper.ui.activity.BaseActivity;
import com.pundroid.shophelper.utils.Constants;
import com.pundroid.shophelper.utils.Utils;

/**
 * Created by pumba30 on 04.06.2016.
 */

/**
 * Represents Sign up screen and functionality of the app
 */
public class CreateAccountActivity extends BaseActivity {
    private static final String LOG_TAG = CreateAccountActivity.class.getSimpleName();

    private ProgressDialog mAuthProgressDialog;
    private EditText mEditTextUsernameCreate, mEditTextEmailCreate, mEditTextPasswordCreate;
    private FirebaseAuth mAuth;

    private String mUserName;
    private String mUserEmail;
    private String mUserPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        /**
         * Link layout elements from XML and setup the progress dialog
         */
        initializeScreen();
        mAuth = FirebaseAuth.getInstance();
    }


    /**
     * Override onCreateOptionsMenu to inflate nothing
     *
     * @param menu The menu with which nothing will happen
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    /**
     * Link layout elements from XML and setup the progress dialog
     */
    public void initializeScreen() {
        mEditTextUsernameCreate = (EditText) findViewById(R.id.edit_text_username_create);
        mEditTextEmailCreate = (EditText) findViewById(R.id.edit_text_email_create);
        mEditTextPasswordCreate = (EditText) findViewById(R.id.edit_text_password_create);
        LinearLayout linearLayoutCreateAccountActivity
                = (LinearLayout) findViewById(R.id.linear_layout_create_account_activity);
        initializeBackground(linearLayoutCreateAccountActivity);

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getResources().getString(R.string.progress_dialog_creating_user_with_firebase));
        mAuthProgressDialog.setCancelable(false);
    }

    /**
     * Open LoginActivity when user taps on "Sign in" textView
     */
    public void onSignInPressed(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Create new account using Firebase email/password provider
     */
    public void onCreateAccountPressed(final View view) {
        mUserName = mEditTextUsernameCreate.getText().toString();
        mUserEmail = mEditTextEmailCreate.getText().toString().toLowerCase();
        mUserPassword = mEditTextPasswordCreate.getText().toString();

        Log.d(LOG_TAG, "createAccount:" + mUserEmail);
        if (!validateUserEmail(mUserEmail, mEditTextEmailCreate)
                || !validateUserPassword(mUserPassword, mEditTextPasswordCreate)
                || !validateUserName(mUserName, mEditTextUsernameCreate)) {
            return;
        }

        mAuthProgressDialog.show();

        // START create_user_with_email
        mAuth.createUserWithEmailAndPassword(mUserEmail, mUserPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            String taskString = task.getException().toString();
                            String message = taskString.substring(taskString.lastIndexOf(":")).replace(":", "");
                            Toast.makeText(CreateAccountActivity.this, message, Toast.LENGTH_LONG).show();
                        } else {
                            Log.d(LOG_TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            Toast.makeText(CreateAccountActivity.this, "Authentication successful.",
                                    Toast.LENGTH_SHORT).show();
                            createUserInFirebaseHelper();

                            startMainActivity();
                        }
                        dissmisProgressDialog(mAuthProgressDialog);
                    }
                });
    }

    private void createUserInFirebaseHelper() {
        String unprocessedEmail = mUserEmail.toLowerCase();
        Utils.saveToSharedPreferences(Constants.KEY_EMAIL, unprocessedEmail, getApplicationContext());
        Utils.saveToSharedPreferences(Constants.KEY_NAME_OWNER_LIST, mUserName, getApplicationContext());

        final String encodedEmail = Utils.encodeEmail(unprocessedEmail);
        createUserInFireBase(encodedEmail, mUserName);
        mAuth.signInWithEmailAndPassword(mUserEmail, mUserPassword);
    }
}
