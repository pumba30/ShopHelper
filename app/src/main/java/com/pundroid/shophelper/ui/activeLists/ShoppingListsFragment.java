package com.pundroid.shophelper.ui.activeLists;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pundroid.shophelper.R;
import com.pundroid.shophelper.model.ShoppingList;
import com.pundroid.shophelper.ui.activeListDetails.ActiveListDetailsActivity;
import com.pundroid.shophelper.utils.Constants;
import com.pundroid.shophelper.utils.Utils;

import java.util.Date;

/**
 * Created by pumba30 on 29.05.2016.
 */
public class ShoppingListsFragment extends Fragment {

    public static final String TAG = ShoppingListsFragment.class.getSimpleName();
    private ListView mListView;
    private ActiveListAdapter mListAdapter;
    private TextView mListNameTextView;
    private TextView mTextViewOwner;
    private TextView mTextViewTimeStamp;
    private boolean mIsUserOwner = false;

    public ShoppingListsFragment() {
        /* Required empty public constructor */
    }

    /**
     * Create fragment and pass bundle with data as it's arguments
     * Right now there are not arguments...but eventually there will be.
     */
    public static ShoppingListsFragment newInstance() {
        ShoppingListsFragment fragment = new ShoppingListsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /**
         * Initalize UI elements
         */
        View rootView = inflater.inflate(R.layout.fragment_shopping_lists, container, false);
        initializeScreen(rootView);

        final DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(Constants.FIREBASE_LOCATION_ACTIVE_LISTS);
        Log.d(TAG, "LINK TO FIREDATABASE " + ref.toString());

        mListAdapter = new ActiveListAdapter(getActivity(),
                ShoppingList.class, R.layout.single_active_list, ref);
        mListView.setAdapter(mListAdapter);

        /**
         * Set interactive bits, such as click events and adapters
         */
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Open the list details", Toast.LENGTH_SHORT).show();
                ShoppingList list = mListAdapter.getItem(position);
                String ownerList = list.getOwner();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String currentUser = preferences.getString(Constants.KEY_EMAIL, "");
                if (ownerList.equals(currentUser)) {
                    mIsUserOwner = true;
                }
                if (list != null) {
                    Intent intent = new Intent(getActivity(), ActiveListDetailsActivity.class);

                    String listId = mListAdapter.getRef(position).getKey();
                    intent.putExtra(Constants.KEY_LIST_ID, listId);
                    // TODO: 14.06.2016 get value mIsUserOwner in ActivlistDetailActivity and use it

                    intent.putExtra(Constants.KEY_IS_USER_OWNER, mIsUserOwner);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListAdapter.cleanup();
    }

    /**
     * Link layout elements from XML
     */
    private void initializeScreen(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list_view_active_lists);
    }

    private String getTextTimeStamp(ShoppingList shoppingList) {
        if (shoppingList.getDateLastChanged() != null) {
            Log.d(TAG, "getDateLastChanged != null");
            return Utils.SIMPLE_DATE_FORMAT.format(new Date(shoppingList.getDateLastChangedLong()));
        } else {
            Log.d(TAG, "HASH MAP NULL");
            return "";
        }
    }

}
