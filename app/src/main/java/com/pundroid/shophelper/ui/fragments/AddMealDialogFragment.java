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

/**
 * Created by pumba30 on 29.05.2016.
 */
public class AddMealDialogFragment extends DialogFragment {
    EditText editTextMealName;

    public static AddMealDialogFragment newInstance() {
        AddMealDialogFragment addMealDialogFragment = new AddMealDialogFragment();
        Bundle bundle = new Bundle();
        addMealDialogFragment.setArguments(bundle);
        return addMealDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Open the keyboard automatically when the dialog fragment is opened
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_add_meal, null);
        editTextMealName = (EditText) rootView.findViewById(R.id.edit_text_meal_name);

        //Call addMeal() when user taps "Done" keyboard action
        editTextMealName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    addMeal();
                }
                return true;
            }
        });

        builder.setView(rootView)
                .setPositiveButton(R.string.positive_button_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addMeal();
                    }
                });

        return builder.create();
    }

    private void addMeal() {/*empty*/}
}