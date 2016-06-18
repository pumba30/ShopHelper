package com.pundroid.shophelper.utils;

import com.pundroid.shophelper.BuildConfig;

/**
 * Created by pumba30 on 29.05.2016.
 */
public class Constants {


    //Constants related to locations in Firebase, such as the name of the node
    //where active lists are stored (ie "activeLists")

    public static final String FIREBASE_LOCATION_ACTIVE_LISTS = "activeLists";
    public static final String FIREBASE_LOCATION_SHOPPING_LIST_ITEMS = "shoppingListItems";
    public static final String FIREBASE_LOCATION_USERS = "users";


    // Constants for Firebase URL

    public static final String FIREBASE_URL = BuildConfig.UNIQUE_FIREBASE_ROOT_URL;
    public static final String FIREBASE_URL_ACTIVE_LISTS =
            FIREBASE_URL + "/" + FIREBASE_LOCATION_ACTIVE_LISTS;
    public static final String FIREBASE_URL_SHOPPINGLIST_ITEMS
            = FIREBASE_URL + "/" + FIREBASE_LOCATION_SHOPPING_LIST_ITEMS;
    public static final String FIREBASE_URL_USERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USERS;


    // Constants for Firebase object properties


    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED = "dateLastChanged";
    public static final String FIREBASE_PROPERTY_LIST_NAME = "listName";
    public static final String FIREBASE_PROPERTY_LIST_ITEM_NAME = "name";
    public static final String FIREBASE_PROPERTY_EMAIL = "email";
    public static final String FIREBASE_PROPERTY_USER_IS_LOGIN_WITH_PASSWORD = "isLoginWithPassword";


    //Constants for bundles, extras and shared preferences keys

    public static final String KEY_LAYOUT_RESOURCE = "LAYOUT_RESOURCE";
    public static final String KEY_LIST_NAME = "keyListName";
    public static final String KEY_LIST_ID = "LIST_ID";
    public static final String KEY_ITEM_LIST_ID = "keyItemListId";
    public static final String KEY_ITEM_LIST_NAME = "keyItemListName";
    public static final String KEY_EMAIL = "key_email";
    public static final String KEY_SIGN_UP_EMAIL = "key_sign_up_email";
    public static final String KEY_NAME_OWNER_LIST = "key_owner_list";
    public static final String KEY_IS_USER_OWNER = "is_user_owner";

    public static final String GOOGLE_PROVIDER_ID = "google.com";
    public static final String PASSWORD_PROVIDER_ID = "password";
}
