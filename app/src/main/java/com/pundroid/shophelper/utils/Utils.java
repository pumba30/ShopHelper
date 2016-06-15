package com.pundroid.shophelper.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;

/**
 * Created by pumba30 on 29.05.2016.
 */
public class Utils {

    /**
     * Format the date with SimpleDateFormat
     */
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Context mContext;


    /**
     * Public constructor that takes mContext for later use
     */
    public Utils(Context context) {
        mContext = context;
    }


    public static final String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public static void saveToSharedPreferences(String keySharedPref, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(keySharedPref, value).apply();
    }

}
