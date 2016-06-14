package com.pundroid.shophelper.ui.activeListDetails;

import android.app.Dialog;
import android.os.Bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.pundroid.shophelper.R;
import com.pundroid.shophelper.model.ShoppingList;
import com.pundroid.shophelper.model.ShoppingListItem;
import com.pundroid.shophelper.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pumba30 on 30.05.2016.
 */
public class AddListItemDialogFragment extends EditListDialogFragment {

    /**
     * Public static constructor that creates fragment and passes a bundle with data into it when adapter is created
     */
    public static AddListItemDialogFragment newInstance(ShoppingList shoppingList, String listId) {
        AddListItemDialogFragment addListItemDialogFragment = new AddListItemDialogFragment();

        Bundle bundle = newInstanceHelper(shoppingList, R.layout.dialog_add_item, listId);
        addListItemDialogFragment.setArguments(bundle);

        return addListItemDialogFragment;
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        String userEnteredNameItem = mEditTextForList.getText().toString();
        if (!userEnteredNameItem.equals("")) {
            Firebase refFirebase = new Firebase(Constants.FIREBASE_URL);
            Firebase refList = new Firebase(Constants.FIREBASE_URL_SHOPPINGLIST_ITEMS).child(mListId);

            Map<String, Object> updatedListItemMap = new HashMap<>();

            Firebase newRef = refList.push();
            String key = newRef.getKey();

            ShoppingListItem item = new ShoppingListItem(userEnteredNameItem);
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

            this.getDialog().cancel();
        }
    }
}
