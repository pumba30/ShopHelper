package com.pundroid.shophelper.ui.activeListDetails.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.pundroid.shophelper.R;
import com.pundroid.shophelper.model.ShoppingList;
import com.pundroid.shophelper.ui.activity.ActiveListDetailsActivity;
import com.pundroid.shophelper.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pumba30 on 30.05.2016.
 */
public class EditListNameDialogFragment extends EditListDialogFragment {
    private static final String LOG_TAG = ActiveListDetailsActivity.class.getSimpleName();
    private String mShoppingListName;
    private String mShoppingListId;

    /**
     * Public static constructor that creates fragment and passes a bundle with data into it when adapter is created
     */
    public static EditListNameDialogFragment newInstance(ShoppingList shoppingList, String shoppingListId) {
        EditListNameDialogFragment editListNameDialogFragment = new EditListNameDialogFragment();
        Bundle bundle = EditListDialogFragment.newInstanceHelper(shoppingList, R.layout.dialog_edit_list, shoppingListId);
        editListNameDialogFragment.setArguments(bundle);
        return editListNameDialogFragment;
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        ShoppingList list = (ShoppingList) bundle.getSerializable(Constants.KEY_LIST_NAME);
        if (list != null) {
            mShoppingListName = list.getListName();
        }
        String listId = bundle.getString(Constants.KEY_LIST_ID);
        if (listId != null) {
            mShoppingListId = listId;
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        /** {@link EditListDialogFragment#createDialogHelper(int)} is a
         * superclass method that creates the dialog
         **/

        final Dialog dialog = super.createDialogHelper(R.string.positive_button_edit_item);
        helpSetDefaultValueEditText(mShoppingListName);

        return dialog;
    }

    /**
     * Changes the list name in all copies of the current list
     */
    protected void doListEdit() {
        final String nameListLast = mEditTextForList.getText().toString();
        if (!nameListLast.equals("")) {
            if (mShoppingListName != null) {
                if (!nameListLast.equals(mShoppingListName)) {
                    Toast.makeText(getActivity(), "NOT EQuals", Toast.LENGTH_SHORT).show();
                    writeNameListInFirebase(nameListLast);
                }
            }
        }
    }

    /**
     * title update in shopping list
     * update at the same time time stamp
     */
    private void writeNameListInFirebase(String nameListChanged) {
        Firebase firebaseRef = new Firebase(Constants.FIREBASE_URL_ACTIVE_LISTS).child(mShoppingListId);

        //title update  in shopping list
        Map<String, Object> updatePropertiesFirebase = new HashMap<>();
        updatePropertiesFirebase.put(Constants.FIREBASE_PROPERTY_LIST_NAME, nameListChanged);

        //update timestamp
        Map<String, Object> changedTimeStamp = new HashMap<>();
        changedTimeStamp.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        updatePropertiesFirebase.put(Constants.FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED, changedTimeStamp);
        // plus example add ComplitionListener to updateChildren
        firebaseRef.updateChildren(updatePropertiesFirebase, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    System.out.println("Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.i(LOG_TAG, "Data saved successfully.");
                }
            }
        });
    }
}
