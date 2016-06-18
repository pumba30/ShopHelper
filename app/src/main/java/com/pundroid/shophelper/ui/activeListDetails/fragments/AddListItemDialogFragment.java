package com.pundroid.shophelper.ui.activeListDetails.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pundroid.shophelper.R;
import com.pundroid.shophelper.model.ShoppingList;
import com.pundroid.shophelper.model.ShoppingListItem;
import com.pundroid.shophelper.model.User;
import com.pundroid.shophelper.utils.Constants;
import com.pundroid.shophelper.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pumba30 on 30.05.2016.
 */
public class AddListItemDialogFragment extends EditListDialogFragment {

    private static final String LOG_TAG = AddListDialogFragment.class.getSimpleName();
    private String mUserEnteredNameItem;

    public static AddListItemDialogFragment newInstance(ShoppingList shoppingList, String listId) {
        AddListItemDialogFragment addListItemDialogFragment = new AddListItemDialogFragment();

        Bundle bundle = newInstanceHelper(shoppingList, R.layout.dialog_add_item, listId);
        addListItemDialogFragment.setArguments(bundle);

        return addListItemDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /** {@link EditListDialogFragment#createDialogHelper(int)} is a
         * superclass method that creates the dialog
         **/
        return super.createDialogHelper(R.string.positive_button_add_list_item);
    }

    /**
     * Adds new item to the current shopping list
     */
    @Override
    protected void doListEdit() {

        mUserEnteredNameItem = mEditTextForList.getText().toString();
        if (mUserEnteredNameItem.equals("")) {
            this.getDialog().cancel();
        } else  {
            String providerId = Utils.getUserProviderId();
            if (providerId.equals(Constants.GOOGLE_PROVIDER_ID)) {
                FirebaseUser firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseAuth != null) {
                    String userOwner = firebaseAuth.getDisplayName();
                    createListItem(userOwner);
                }
            } else if (providerId.equals(Constants.PASSWORD_PROVIDER_ID)) {
                String encodedEmail = Utils.encodeEmail(
                        Utils.getPreferencesValue(Constants.KEY_EMAIL, "", getActivity()));
                Firebase userRef = new Firebase(Constants.FIREBASE_URL_USERS + "/" + encodedEmail);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            User user = dataSnapshot.getValue(User.class);
                            String userOwner = user.getName();
                            createListItem(userOwner);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Log.d(LOG_TAG, firebaseError.getDetails());
                    }
                });
            }
        }
    }

    private void createListItem(String userOwner) {
        Firebase refFirebase = new Firebase(Constants.FIREBASE_URL);
        Firebase refList = new Firebase(Constants.FIREBASE_URL_SHOPPINGLIST_ITEMS).child(mListId);

        Map<String, Object> updatedListItemMap = new HashMap<>();

        Firebase newRef = refList.push();
        String key = newRef.getKey();

        ShoppingListItem item = new ShoppingListItem(mUserEnteredNameItem, userOwner);
        Map<String, Object> itemToMap =
                (HashMap<String, Object>) new ObjectMapper().convertValue(item, Map.class);
        updatedListItemMap.put("/" + Constants.FIREBASE_LOCATION_SHOPPING_LIST_ITEMS
                + "/" + mListId
                + "/" + key, itemToMap);

        Map<String, Object> updatedTimeStamp = new HashMap<>();
        updatedTimeStamp.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        updatedListItemMap.put("/" + Constants.FIREBASE_LOCATION_ACTIVE_LISTS
                + "/" + mListId
                + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED, updatedTimeStamp);

        refFirebase.updateChildren(updatedListItemMap);
    }
}
