package com.pundroid.shophelper.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pundroid.shophelper.R;
import com.pundroid.shophelper.model.ShoppingList;
import com.pundroid.shophelper.model.ShoppingListItem;
import com.pundroid.shophelper.ui.adapters.ActiveListItemAdapter;
import com.pundroid.shophelper.ui.fragments.AddListItemDialogFragment;
import com.pundroid.shophelper.ui.fragments.EditListItemNameDialogFragment;
import com.pundroid.shophelper.ui.fragments.EditListNameDialogFragment;
import com.pundroid.shophelper.ui.fragments.RemoveListDialogFragment;
import com.pundroid.shophelper.utils.Constants;
import com.pundroid.shophelper.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pumba30 on 30.05.2016.
 */
public class ActiveListDetailsActivity extends BaseActivity {
    private static final String LOG_TAG = ActiveListDetailsActivity.class.getSimpleName();

    private FloatingActionButton mAddItemToListButton;
    private ListView mListView;
    private ShoppingList mShoppingList;
    private ActiveListItemAdapter mActiveListItemAdapter;
    private String mShoppingListId;
    private boolean mIsUserOwner;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_list_details);
        Intent intent = getIntent();
        mShoppingListId = intent.getStringExtra(Constants.KEY_LIST_ID);
        mIsUserOwner = intent.getBooleanExtra(Constants.KEY_IS_USER_OWNER, false);
        if (mShoppingListId == null) {
            finish();
            return;
        }

        initializeScreen();

        final DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(Constants.FIREBASE_LOCATION_SHOPPING_LIST_ITEMS).child(mShoppingListId);
        mActiveListItemAdapter = new ActiveListItemAdapter(this, ShoppingListItem.class,
                R.layout.single_active_list_item, ref, mIsUserOwner);
        mListView.setAdapter(mActiveListItemAdapter);


        Firebase refShoppingList = new Firebase(Constants.FIREBASE_URL_ACTIVE_LISTS)
                .child(mShoppingListId);
        refShoppingList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ShoppingList list = dataSnapshot.getValue(ShoppingList.class);
                if (list == null) {
                    finish();
                    return;
                }
                mShoppingList = list;
                setTitle(list.getListName());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {/*empty*/}
        });

        invalidateOptionsMenu();

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Check that the view is not the empty footer item
                if (view.getId() != R.id.list_view_footer_empty) {
                    String itemId = mActiveListItemAdapter.getRef(position).getKey();
                    String itemName = mActiveListItemAdapter.getItem(position).getName();

                    showEditListItemNameDialog(itemName, itemId);
                }
                return true;
            }
        });

        //User purchased item
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.getId() != R.id.list_view_footer_empty) {
                    String itemId = mActiveListItemAdapter.getRef(position).getKey();
                    ShoppingListItem itemShoppingList = mActiveListItemAdapter.getItem(position);
                    String userEmail = Utils.getPreferencesValue(Constants.KEY_EMAIL, "", getApplicationContext());

                    if (itemShoppingList != null) {
                        Map<String, Object> updateBought = new HashMap<String, Object>();
                        if (!itemShoppingList.isHasBought()) {
                            updateBought.put(Constants.FIREBASE_PROPERTY_BOUGHT_BY_USER, userEmail);
                            updateBought.put(Constants.FIREBASE_PROPERTY_HAS_BOUGHT, true);

                            Firebase shoppingListRef = new Firebase
                                    (Constants.FIREBASE_URL_SHOPPINGLIST_ITEMS).child(mShoppingListId);
                            Firebase itemShoppingListRef = shoppingListRef.child(itemId);

                            itemShoppingListRef.updateChildren(updateBought, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    if (firebaseError != null) {
                                        Log.d(LOG_TAG, getString(R.string.log_error_updating_data)
                                                + firebaseError.getDetails());
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_details, menu);

        MenuItem remove = menu.findItem(R.id.action_remove_list);
        MenuItem edit = menu.findItem(R.id.action_edit_list_name);
        MenuItem share = menu.findItem(R.id.action_share_list);
        MenuItem archive = menu.findItem(R.id.action_archive);

        if (mIsUserOwner) {
            remove.setVisible(true);
            edit.setVisible(true);
            share.setVisible(true);
            archive.setVisible(true);
        } else {
            remove.setVisible(false);
            edit.setVisible(false);
            share.setVisible(false);
            archive.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Show edit list dialog when the edit action is selected
        if (id == R.id.action_edit_list_name) {
            showEditListNameDialog();
            return true;
        }

        // removeList() when the remove action is selected
        if (id == R.id.action_remove_list) {
            removeList();
            return true;
        }

        //Eventually we'll add this
        if (id == R.id.action_share_list) {
            return true;
        }

        //archiveList() when the archive action is selected
        if (id == R.id.action_archive) {
            archiveList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mActiveListItemAdapter.cleanup();
    }

    private void initializeScreen() {
        mListView = (ListView) findViewById(R.id.list_view_shopping_list_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        View footer = getLayoutInflater().inflate(R.layout.footer_empty, null);
        mListView.addFooterView(footer);
        mAddItemToListButton = (FloatingActionButton) findViewById(R.id.fab_detail_add_item);

        if (mAddItemToListButton != null) {
            if (!mIsUserOwner) {
                mAddItemToListButton.setVisibility(View.INVISIBLE);
            } else {
                mAddItemToListButton.setVisibility(View.VISIBLE);
            }
        }

        mAddItemToListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddListItemDialog();
            }
        });

    }

    public void archiveList() {/*empty*/}


    public void addMeal(View view) {/*empty*/}


    public void removeList() {
        DialogFragment dialog = RemoveListDialogFragment.newInstance(mShoppingList, mShoppingListId);
        dialog.show(getSupportFragmentManager(), "RemoveListDialogFragment");
    }


    // Show the add list item dialog when user taps "Add list item" fab
    public void showAddListItemDialog() {
        DialogFragment dialog = AddListItemDialogFragment.newInstance(mShoppingList, mShoppingListId);
        dialog.show(getSupportFragmentManager(), "AddListItemDialogFragment");
    }


    // Show edit list name dialog when user selects "Edit list name" menu item
    public void showEditListNameDialog() {
        DialogFragment dialog = EditListNameDialogFragment.newInstance(mShoppingList, mShoppingListId);
        dialog.show(getSupportFragmentManager(), "EditListNameDialogFragment");
    }


    // Show the edit list item name dialog after longClick on the particular item
    public void showEditListItemNameDialog(String itemName, String itemId) {
        Log.d(LOG_TAG, "Create Dialog EditItemListName");
        DialogFragment dialog = EditListItemNameDialogFragment.newInstance(mShoppingList, mShoppingListId, itemId, itemName);
        dialog.show(getSupportFragmentManager(), "EditListItemNameDialogFragment");
    }


    //This method is called when user taps "Start/Stop shopping" button
    public void toggleShopping(View view) {

    }


}