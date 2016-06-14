package com.pundroid.shophelper.ui.activeListDetails;

import android.app.Dialog;
import android.os.Bundle;

import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.pundroid.shophelper.R;
import com.pundroid.shophelper.model.ShoppingList;
import com.pundroid.shophelper.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pumba30 on 30.05.2016.
 */
public class EditListItemNameDialogFragment extends EditListDialogFragment {
    public static final String LOG_TAG = EditListItemNameDialogFragment.class.getSimpleName();
    private String mItemName;
    private String mItemId;

    /**
     * Public static constructor that creates fragment and passes a bundle with data into it when adapter is created
     */
    public static EditListItemNameDialogFragment newInstance(ShoppingList shoppingList, String listId, String itemId, String itemName) {
        EditListItemNameDialogFragment editListItemNameDialogFragment = new EditListItemNameDialogFragment();

        Bundle bundle = EditListDialogFragment.newInstanceHelper(shoppingList, R.layout.dialog_edit_item, listId);
        bundle.putString(Constants.KEY_ITEM_LIST_ID, itemId);
        bundle.putString(Constants.KEY_ITEM_LIST_NAME, itemName);
        editListItemNameDialogFragment.setArguments(bundle);

        return editListItemNameDialogFragment;
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemId = getArguments().getString(Constants.KEY_ITEM_LIST_ID);
        mItemName = getArguments().getString(Constants.KEY_ITEM_LIST_NAME);
    }


    @Override

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /** {@link EditListDialogFragment#createDialogHelper(int)} is a
         * superclass method that creates the dialog
         */
        Dialog dialog = super.createDialogHelper(R.string.positive_button_edit_item);
        return dialog;
    }

    /**
     * Change selected list item name to the editText input if it is not empty
     */
    protected void doListEdit() {
        String enteredUserItemName = mEditTextForList.getText().toString();
        if (!enteredUserItemName.equals("") && !enteredUserItemName.equals(mItemName)) { //todo name
            Firebase ref = new Firebase(Constants.FIREBASE_URL);
            Map<String, Object> updateItemListName = new HashMap<>();
            updateItemListName.put("/" + Constants.FIREBASE_LOCATION_SHOPPING_LIST_ITEMS
                            + "/" + mListId
                            + "/" + mItemId + "/" + Constants.FIREBASE_PROPERTY_LIST_ITEM_NAME,
                    enteredUserItemName);

            HashMap<String, Object> changedTimestampMap = new HashMap<>();
            changedTimestampMap.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

            updateItemListName.put("/" + Constants.FIREBASE_LOCATION_ACTIVE_LISTS + "/" + mListId +
                    "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED, changedTimestampMap);
            ref.updateChildren(updateItemListName);
        }
    }
}
