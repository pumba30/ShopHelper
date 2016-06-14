package com.pundroid.shophelper.ui.activeLists;


import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.Query;
import com.pundroid.shophelper.R;
import com.pundroid.shophelper.model.ShoppingList;


/**
 * Created by pumba30 on 01.06.2016.
 */
public class ActiveListAdapter extends FirebaseListAdapter<ShoppingList> {


    public ActiveListAdapter(Activity activity, Class<ShoppingList> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    @Override
    protected void populateView(View v, ShoppingList model, int position) {
        TextView listNameTextView = (TextView) v.findViewById(R.id.text_view_list_name);
        TextView createdByTextView = (TextView) v.findViewById(R.id.text_view_created_by_user);

        listNameTextView.setText(model.getListName());
        createdByTextView.setText(model.getOwner());
    }


}
