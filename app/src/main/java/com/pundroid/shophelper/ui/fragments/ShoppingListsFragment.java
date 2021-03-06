package com.pundroid.shophelper.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pundroid.shophelper.R;
import com.pundroid.shophelper.model.ShoppingList;
import com.pundroid.shophelper.ui.adapters.ActiveListAdapter;
import com.pundroid.shophelper.ui.activity.ActiveListDetailsActivity;
import com.pundroid.shophelper.utils.Constants;

/**
 * Created by pumba30 on 29.05.2016.
 */
public class ShoppingListsFragment extends Fragment {

    public static final String LOG_TAG = ShoppingListsFragment.class.getSimpleName();
    private ListView mListView;
    private ActiveListAdapter mListAdapter;
    private boolean mIsUserOwner;

    public ShoppingListsFragment() {
    }

    public static ShoppingListsFragment newInstance() {
        ShoppingListsFragment fragment = new ShoppingListsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shopping_lists, container, false);
        initializeScreen(rootView);

        final DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(Constants.FIREBASE_LOCATION_ACTIVE_LISTS);
        Log.d(LOG_TAG, "Link to data firebase " + ref.toString());

        mListAdapter = new ActiveListAdapter(getActivity(),
                ShoppingList.class, R.layout.single_active_list, ref);
        mListView.setAdapter(mListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "Open the list details");
                ShoppingList list = mListAdapter.getItem(position);
                String ownerList = list.getOwner();
                SharedPreferences preferences =
                        PreferenceManager.getDefaultSharedPreferences(getActivity());
                String currentUser = preferences.getString(Constants.KEY_EMAIL, "");
                mIsUserOwner = ownerList.equals(currentUser);

                Intent intent = new Intent(getActivity(), ActiveListDetailsActivity.class);
                String listId = mListAdapter.getRef(position).getKey();
                intent.putExtra(Constants.KEY_LIST_ID, listId);
                intent.putExtra(Constants.KEY_IS_USER_OWNER, mIsUserOwner);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListAdapter.cleanup();
    }

    private void initializeScreen(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list_view_active_lists);
    }
}
