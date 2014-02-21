package com.studentglue.whattodotodolisttaskmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MyListFragment extends Fragment {

    private static final int REQUEST_CODE = 1234;
    private static final int GO_TO_LIST = 5;

    View mylistFragmentView;

    Button what_todo_btn;

    TextView listId;
    TextView listName;

    Button delete_list_btn;

    Button add_list_btn;
    EditText add_list_edit_text;

    ArrayList<HashMap<String, String>> myList;
    ListView listView;
    ListEntryAdapter customAdapter;

    DBTools dbtools;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        dbtools = new DBTools(getActivity());

        mylistFragmentView = inflater.inflate(R.layout.fragment_my_list, container, false);

        setupUI(mylistFragmentView);

        what_todo_btn = (Button) mylistFragmentView.findViewById(R.id.what_todo_btn);

        add_list_btn = (Button) mylistFragmentView.findViewById(R.id.add_list_btn);
        add_list_edit_text = (EditText) mylistFragmentView.findViewById(R.id.add_list_edit_text);

        myList = dbtools.getAllLists();

        listView = (ListView) mylistFragmentView.findViewById(R.id.myListView);
        customAdapter = new ListEntryAdapter(getActivity());
        listView.setAdapter(customAdapter);

        what_todo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent whatToDoIntent = new Intent(getActivity(), WhatToDoActivity.class);
                startActivity(whatToDoIntent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {

                listId = (TextView) view.findViewById(R.id.listId);
                listName = (TextView) view.findViewById(R.id.listName);

                final String listIdValue = listId.getText().toString();
                final String listNameValue = listName.getText().toString();

                Intent listIntent = new Intent(getActivity(), ListActivity.class);
                Bundle extras = new Bundle();
                extras.putString("EXTRA_LIST_ID", listIdValue);
                extras.putString("EXTRA_LIST_NAME", listNameValue);
                listIntent.putExtras(extras);
                startActivityForResult(listIntent, GO_TO_LIST);

            }
        });

        add_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                String listName = add_list_edit_text.getText().toString();

                if(!listName.equals("")) {
                    HashMap<String, String> listMap = new HashMap<String, String>();

                    listMap.put("category", listName);

                    add_list_edit_text.setText("");

                    dbtools.addList(listMap);

                    myList = dbtools.getAllLists();

                    setAdapter();

                }
            }
        });

        return mylistFragmentView;
    }

    public void setAdapter() {

        customAdapter = new ListEntryAdapter(getActivity());
        listView.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == getActivity().RESULT_OK)
        {
            if (data != null) {
                // Populate the wordsList with the String values the recognition engine thought it heard
                ArrayList<String> matches = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);

                String textInput;
                textInput = matches.get(0);

                add_list_edit_text = (EditText) mylistFragmentView.findViewById(R.id.add_todo_edit_text);
                add_list_edit_text.setText(textInput);
            }
        }
        else if (requestCode == GO_TO_LIST && resultCode == getActivity().RESULT_OK) {

            setAdapter();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(getActivity());
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }
}
