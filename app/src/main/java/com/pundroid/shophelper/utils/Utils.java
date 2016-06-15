package com.pundroid.shophelper.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by pumba30 on 29.05.2016.
 */
public class Utils {

    /**
     * Format the date with SimpleDateFormat
     */
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public static final String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public static void saveToSharedPreferences(String keySharedPref, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(keySharedPref, value).apply();
    }

    public static String getPreferencesValue(String key, String defValue, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, defValue);
    }

    public static String getUserProviderId() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            List<String> providersIds = firebaseAuth.getCurrentUser().getProviders();
            if (providersIds != null) {
                return providersIds.get(0);
            }
        }
        return "";
    }

    public static void toast(Context context, String toast) {
        Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
    }

}
