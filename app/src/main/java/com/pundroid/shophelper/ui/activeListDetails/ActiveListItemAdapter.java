package com.pundroid.shophelper.ui.activeListDetails;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.pundroid.shophelper.R;
import com.pundroid.shophelper.model.ShoppingListItem;
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

    public ActiveListItemAdapter(Activity activity, Class modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, modelLayout, ref);
        mActivity = activity;
        mRef = ref;
    }

    @Override
    protected void populateView(View v, ShoppingListItem model, final int position) {

        TextView boughtByUser = (TextView) v.findViewById(R.id.text_view_bought_by_user);
        TextView itemName = (TextView) v.findViewById(R.id.text_view_active_list_item_name);
        ImageButton buttonRemoveItem = (ImageButton) v.findViewById(R.id.button_remove_item);
        //if(m)

        final String itemId = this.getRef(position).getKey();

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


        boughtByUser.setText(model.getOwner());
        itemName.setText(model.getName());
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
