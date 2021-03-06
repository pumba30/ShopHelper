package com.pundroid.shophelper.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pundroid.shophelper.R;
import com.pundroid.shophelper.ui.fragments.AddListDialogFragment;
import com.pundroid.shophelper.ui.fragments.ShoppingListsFragment;
import com.pundroid.shophelper.ui.activity.login.LoginActivity;
import com.pundroid.shophelper.ui.fragments.AddMealDialogFragment;
import com.pundroid.shophelper.ui.fragments.MealsFragment;
import com.pundroid.shophelper.utils.Constants;
import com.pundroid.shophelper.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "onCreate MainActivity");
        //set title - owner's list name
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String nameUser = user.getDisplayName();
                    if (nameUser != null) {
                        String title = nameUser.split("\\s")[0] + getString(R.string.add_s_lists_to_owner);
                        setTitle(title);
                    } else {
                        String userEmail = user.getEmail();
                        if (userEmail != null) {
                            String title = user.getEmail().split("@")[0];
                            setTitle(title + getString(R.string.add_s_lists_to_owner));
                        } else {
                            setTitle(getString(R.string.app_name));
                        }
                    }
                }
            }
        });
        initializeScreen();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            clearUserData();
            //logout from Firebase
            // FIXME: 17.06.2016 incorrect output from account
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (Utils.getUserProviderId().equals(Constants.PASSWORD_PROVIDER_ID)) {
            String userEmail = Utils.getPreferencesValue
                    (Constants.KEY_EMAIL, "", getApplicationContext());
            Utils.saveToSharedPreferences
                    (Constants.KEY_SIGN_UP_EMAIL, userEmail, getApplicationContext());
        }
    }


    private void clearUserData() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().apply();
    }

    private void initializeScreen() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        assert viewPager != null;
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);

        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager);
    }

    public void showAddListDialog(View view) {
        DialogFragment dialog = AddListDialogFragment.newInstance();
        dialog.show(MainActivity.this.getSupportFragmentManager(), "AddListDialogFragment");
    }

    public void showAddMealDialog(View view) {
        DialogFragment dialog = AddMealDialogFragment.newInstance();
        dialog.show(MainActivity.this.getSupportFragmentManager(), "AddMealDialogFragment");
    }

    public class SectionPagerAdapter extends FragmentStatePagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = ShoppingListsFragment.newInstance();
                    break;
                case 1:
                    fragment = MealsFragment.newInstance();
                    break;
                default:
                    fragment = ShoppingListsFragment.newInstance();
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.pager_title_shopping_lists);
                case 1:
                default:
                    return getString(R.string.pager_title_meals);
            }
        }
    }
}
