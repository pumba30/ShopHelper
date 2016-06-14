package com.pundroid.shophelper;

import android.app.Application;
import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.Logger;

/**
 * Created by pumba30 on 29.05.2016.
 */
public class ShopHelperApplication extends Application {

    public static final String TAG = ShopHelperApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setLogLevel(Logger.Level.DEBUG);
        Log.d(TAG, "Initialization of Firebase related code");
    }
}
