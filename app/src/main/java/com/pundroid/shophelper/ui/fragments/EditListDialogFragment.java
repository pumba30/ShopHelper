package com.pundroid.shophelper.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.pundroid.shophelper.R;
import com.pundroid.shophelper.model.ShoppingList;
import com.pundroid.shophelper.utils.Constants;

/**
 * Created by pumba30 on 30.05.2016.
 */
public abstract class EditListDialogFragment extends DialogFragment {
    protected EditText mEditTextForList;
    private int mResource;
    protected String mListId;
    protected String mItemId;


    //Helper method that creates a basic bundle of all
    // of the information needed to change values in a shopping list.
    protected static Bundle newInstanceHelper(ShoppingList shoppingList, int resource,
                                              String shoppingListId) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_LAYOUT_RESOURCE, resource);
        bundle.putSerializable(Constants.KEY_LIST_NAME, shoppingList);
        bundle.putString(Constants.KEY_LIST_ID, shoppingListId);

        return bundle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResource = getArguments().getInt(Constants.KEY_LAYOUT_RESOURCE);
        mListId = getArguments().getString(Constants.KEY_LIST_ID);
        mItemId = getArguments().getString(Constants.KEY_ITEM_LIST_ID);
    }

    // Open the keyboard automatically when the dialog fragment is opened
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    protected Dialog createDialogHelper(int stringResourceForPositiveButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(mResource, null);
        mEditTextForList = (EditText) rootView.findViewById(R.id.edit_text_list_dialog);


        //Call doListEdit() when user taps "Done" keyboard action
        mEditTextForList.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    doListEdit();
                    EditListDialogFragment.this.getDialog().cancel();
                }
                return true;
            }
        });

        builder.setView(rootView)
                .setPositiveButton(stringResourceForPositiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        doListEdit();
                        EditListDialogFragment.this.getDialog().cancel();
                    }
                })
                .setNegativeButton(R.string.negative_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditListDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    //Set the EditText text to be the inputted text
    //and put the pointer at the end of the input
    protected void helpSetDefaultValueEditText(String defaultText) {
        mEditTextForList.setText(defaultText);
        mEditTextForList.setSelection(defaultText.length());
    }

    protected abstract void doListEdit();
}
