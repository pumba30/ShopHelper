package com.pundroid.shophelper.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREFERENCES_NAME = "ShopHelper";
    private static final String PREF_MAIL_KEY = "mail_key_pref";

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static String getMailKey(Context context) {
        return getPreferences(context).getString(PREF_MAIL_KEY, null);
    }

    public static void setMailKey(Context context, String key) {
        getPreferences(context).edit().putString(PREF_MAIL_KEY, key).apply();
    }


}
