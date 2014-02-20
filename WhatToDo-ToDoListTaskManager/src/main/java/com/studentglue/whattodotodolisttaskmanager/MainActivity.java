package com.studentglue.whattodotodolisttaskmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Paint;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private static final int REQUEST_CODE = 1234;
    private static final int UPDATE_LISTVIEW = 2;
    private static final int GO_TO_MY_LIST = 3;
    private static final int GO_TO_IMPORTANT_LIST = 4;

    Button what_todo_btn;

    TextView taskId;
    TextView taskName;

    EditText add_todo_edit_text;
    Button add_todo_btn;
    Button my_list_btn;

    Button important_tasks_btn;

    ArrayList<HashMap<String, String>> taskList;
    ListView listView;
    TaskEntryAdapter customAdapter;

    DBTools dbtools = new DBTools(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI(findViewById(R.id.container));

        ImageButton speakButton = (ImageButton) findViewById(R.id.speak_btn);

        // Disable button if no recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            speakButton.setEnabled(false);
            //speakButton.setText("Recognizer not present");
        }

        what_todo_btn = (Button) findViewById(R.id.what_todo_btn);

        important_tasks_btn= (Button) findViewById(R.id.important_tasks_btn);

        add_todo_btn = (Button) findViewById(R.id.add_todo_btn);
        my_list_btn = (Button) findViewById(R.id.my_lists_btn);

        taskList = dbtools.getAllTasks();

        what_todo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent whatToDoIntent = new Intent(getApplication(), WhatToDoActivity.class);
                startActivity(whatToDoIntent);
            }
        });

        important_tasks_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent importantIntent = new Intent(getApplication(), ImportantListActivity.class);
                startActivityForResult(importantIntent, GO_TO_IMPORTANT_LIST);
            }
        });

        my_list_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent myListIntent = new Intent(getApplication(), MyListActivity.class);;

                startActivityForResult(myListIntent, GO_TO_MY_LIST);
            }
        });

        listView = (ListView) findViewById(R.id.taskListView);

        customAdapter = new TaskEntryAdapter(this);
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {

                taskId = (TextView) view.findViewById(R.id.taskId);
                taskName = (TextView) view.findViewById(R.id.taskName);

                final String taskIdValue = taskId.getText().toString();
                final String taskNameValue = taskName.getText().toString();

                Intent taskIntent = new Intent(getApplication(), TaskActivity.class);
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

                add_todo_edit_text = (EditText) findViewById(R.id.add_todo_edit_text);

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

        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
    }

    public void setAdapter() {

        customAdapter = new TaskEntryAdapter(this);
        listView.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
    }

    /**
     * Handle the action of the button being clicked
     */
    public void speakButtonClicked(View v)
    {
        startVoiceRecognitionActivity();
    }

    /**
     * Fire an intent to start the voice recognition activity.
     */
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now");
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            if (data != null) {
                // Populate the wordsList with the String values the recognition engine thought it heard
                ArrayList<String> matches = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);

                String textInput;
                textInput = matches.get(0);

                add_todo_edit_text = (EditText) findViewById(R.id.add_todo_edit_text);
                add_todo_edit_text.setText(textInput);
            }
        }
        else if (requestCode == UPDATE_LISTVIEW && resultCode == RESULT_OK) {

            setAdapter();
        }
        else if (requestCode == GO_TO_MY_LIST && resultCode == RESULT_OK) {

            setAdapter();
        }
        else if (requestCode == GO_TO_IMPORTANT_LIST && resultCode == RESULT_OK) {

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
                    hideSoftKeyboard(MainActivity.this);
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

    public void callMainActivity(View view) {

        Intent theIntent = new Intent(getApplication(), MainActivity.class);
        startActivity(theIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up what_todo_button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        /*@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }*/
    }

}