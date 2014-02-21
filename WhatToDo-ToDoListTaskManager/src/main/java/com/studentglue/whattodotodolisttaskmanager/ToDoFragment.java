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

public class ToDoFragment extends Fragment {

    private static final int REQUEST_CODE = 1234;
    private static final int UPDATE_LISTVIEW = 2;
    private static final int GO_TO_MY_LIST = 3;
    private static final int GO_TO_IMPORTANT_LIST = 4;

    View todoFragmentView;

    Button what_todo_btn;
    Button add_todo_btn;

    TextView taskId;
    TextView taskName;

    EditText add_todo_edit_text;

    ArrayList<HashMap<String, String>> taskList;
    ListView listView;
    TaskEntryAdapter customAdapter;

    DBTools dbtools;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        todoFragmentView = inflater.inflate(R.layout.fragment_todo, container, false);

        setupUI(todoFragmentView);

        dbtools = new DBTools(getActivity());

        what_todo_btn = (Button) todoFragmentView.findViewById(R.id.what_todo_btn);
        add_todo_btn = (Button) todoFragmentView.findViewById(R.id.add_todo_btn);

        add_todo_edit_text = (EditText) todoFragmentView.findViewById(R.id.add_todo_edit_text);

        what_todo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent whatToDoIntent = new Intent(getActivity(), WhatToDoActivity.class);
                startActivity(whatToDoIntent);
            }
        });

        listView = (ListView) todoFragmentView.findViewById(R.id.taskListView);

        customAdapter = new TaskEntryAdapter(getActivity());
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {

                taskId = (TextView) view.findViewById(R.id.taskId);
                taskName = (TextView) view.findViewById(R.id.taskName);

                final String taskIdValue = taskId.getText().toString();
                final String taskNameValue = taskName.getText().toString();

                Intent taskIntent = new Intent(getActivity(), TaskActivity.class);
                Bundle extras = new Bundle();
                extras.putString("EXTRA_TASK_ID", taskIdValue);
                extras.putString("EXTRA_TASK_NAME", taskNameValue);
                extras.putString("EXTRA_TASK_IMPORTANCE", dbtools.getTaskImportance(taskIdValue));
                taskIntent.putExtras(extras);
                startActivityForResult(taskIntent, UPDATE_LISTVIEW);
            }
        });

        add_todo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                String taskName = add_todo_edit_text.getText().toString();

                if(!taskName.equals("")) {
                    HashMap<String, String> taskMap = new HashMap<String, String>();

                    taskMap.put("taskName", taskName);
                    taskMap.put("list_id", "-1");

                    add_todo_edit_text.setText("");

                    dbtools.addTask(taskMap);

                    taskList = dbtools.getAllTasks();

                    setAdapter();

                }
            }
        });

        return todoFragmentView;
    }

    public void setAdapter() {

        customAdapter = new TaskEntryAdapter(getActivity());
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

                add_todo_edit_text = (EditText) todoFragmentView.findViewById(R.id.add_todo_edit_text);
                add_todo_edit_text.setText(textInput);
            }
        }
        else if (requestCode == UPDATE_LISTVIEW && resultCode == getActivity().RESULT_OK) {

            setAdapter();
        }
        else if (requestCode == GO_TO_MY_LIST && resultCode == getActivity().RESULT_OK) {

            setAdapter();
        }
        else if (requestCode == GO_TO_IMPORTANT_LIST && resultCode == getActivity().RESULT_OK) {

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
