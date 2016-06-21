package com.pundroid.shophelper.ui.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.pundroid.shophelper.R;
import com.pundroid.shophelper.model.ShoppingListItem;
import com.pundroid.shophelper.model.User;
import com.pundroid.shophelper.utils.Constants;
import com.pundroid.shophelper.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pumba30 on 02.06.2016.
 */
public class ActiveListItemAdapter extends FirebaseListAdapter<ShoppingListItem> {
    private static final String LOG_TAG = ActiveListItemAdapter.class.getSimpleName();
    private DatabaseReference mRef;
    private boolean mIsOwner;

    public ActiveListItemAdapter(Activity activity, Class modelClass, int modelLayout,
                                 DatabaseReference ref, boolean isOwner) {
        super(activity, modelClass, modelLayout, ref);
        mActivity = activity;
        mRef = ref;
        mIsOwner = isOwner;
    }

    @Override
    protected void populateView(View v, ShoppingListItem shoppingListItem, final int position) {

        TextView boughtByUser = (TextView) v.findViewById(R.id.text_view_bought_by_user);
        TextView itemName = (TextView) v.findViewById(R.id.text_view_active_list_item_name);
        TextView boughtBy = (TextView) v.findViewById(R.id.text_view_bought_by);
        final ImageButton buttonRemoveItem = (ImageButton) v.findViewById(R.id.button_remove_item);
        buttonRemoveItem.setVisibility(View.VISIBLE);
        if (!mIsOwner) {
            buttonRemoveItem.setVisibility(View.INVISIBLE);
        }
        final String itemId = this.getRef(position).getKey();
        setStatusBoughtUser(shoppingListItem, boughtBy, boughtByUser, buttonRemoveItem, itemName);

        buttonRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.CustomTheme_Dialog)
                        .setTitle(R.string.remove_item_option)
                        .setMessage(R.string.dialog_message_are_you_sure_remove_item)

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeItem(itemId);
                                Utils.toast(mActivity, "Remove Item");
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void setStatusBoughtUser(final ShoppingListItem shoppingListItem, final TextView boughtBy,
                                     final TextView boughtByUser, final ImageButton buttonRemoveItem,
                                     final TextView itemName) {

        if (shoppingListItem.isHasBought() && shoppingListItem.getBoughtBy() != null) {
            itemName.setText(shoppingListItem.getName());
            itemName.setPaintFlags(itemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            buttonRemoveItem.setVisibility(View.INVISIBLE);
            boughtBy.setVisibility(View.VISIBLE);

            String ownerItemListKey = Utils.encodeEmail(shoppingListItem.getBoughtBy());

            Firebase userRef = new Firebase(Constants.FIREBASE_URL_USERS)
                    .child(ownerItemListKey);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    //If you are the owner of the list  and you have bought this item,
                    // set the label "Bought by You"
                    if (shoppingListItem.getBoughtBy()
                            .equals(Utils.getPreferencesValue(Constants.KEY_EMAIL, "", mActivity))) {
                        boughtByUser.setText(mActivity.getString(R.string.text_you));
                    } else {
                        //Otherwise, we set the label that is purchased by another user
                        boughtByUser.setText(user.getName());
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.d(LOG_TAG, firebaseError.getDetails());
                }
            });
        } else {
            boughtByUser.setText(shoppingListItem.getOwner());
            itemName.setText(shoppingListItem.getName());
            buttonRemoveItem.setVisibility(View.VISIBLE);
            // TODO: 21.06.2016 make Check Off shoppingItemList
        }
    }


    private void removeItem(String itemId) {
        Firebase ref = new Firebase(Constants.FIREBASE_URL);

        HashMap<String, Object> updatedRemoveItemMap = new HashMap<>();
        updatedRemoveItemMap.put("/" + Constants.FIREBASE_LOCATION_SHOPPING_LIST_ITEMS
                + "/" + mRef.getKey() + "/" + itemId, null);

        Map<String, Object> updateTimeStamp = new HashMap<>();
        updateTimeStamp.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        updatedRemoveItemMap.put("/" + Constants.FIREBASE_LOCATION_ACTIVE_LISTS
                + "/" + mRef.getKey()
                + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED, updateTimeStamp);

        ref.updateChildren(updatedRemoveItemMap);
    }
}
