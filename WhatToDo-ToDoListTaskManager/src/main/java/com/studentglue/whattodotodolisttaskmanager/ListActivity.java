package com.studentglue.whattodotodolisttaskmanager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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

public class ListActivity extends ActionBarActivity {

    private static final int REQUEST_CODE = 1234;

    Button what_todo_btn;

    TextView taskId;
    TextView taskName;
    TextView task_text_view;

    Button add_todo_btn;
    EditText add_todo_edit_text;

    ArrayList<HashMap<String, String>> taskList;

    DBTools dbtools = new DBTools(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
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

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        final String list_id = extras.getString("EXTRA_LIST_ID");
        String list_name = extras.getString("EXTRA_LIST_NAME");

        task_text_view = (TextView) findViewById(R.id.task_text_view);

        task_text_view.setText(list_name);

        add_todo_btn = (Button) findViewById(R.id.add_todo_btn);
        add_todo_edit_text = (EditText) findViewById(R.id.add_todo_edit_text);

        taskList = dbtools.getAllTaskFromList(list_id);

        ListView listView = (ListView) findViewById(R.id.taskListView);

        String[] from = new String[] { "task_id", "name" };
        final int[] to = { R.id.taskId, R.id.taskName };

        final SimpleAdapter adapter = new SimpleAdapter(this, taskList, R.layout.task_entry,
                from, to);
        listView.setAdapter(adapter);

        what_todo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent whatToDoIntent = new Intent(getApplication(), WhatToDoActivity.class);
                startActivity(whatToDoIntent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {

                taskId = (TextView) view.findViewById(R.id.taskId);
                taskName = (TextView) view.findViewById(R.id.taskName);

                final String taskIdValue = taskId.getText().toString();
                final String taskNameValue = taskName.getText().toString();

                dbtools.deleteTask(taskIdValue);

                view.animate().setDuration(1000).alpha(0).withEndAction(new Runnable() {

                    public void run() {

                        for (HashMap<String, String> map : taskList) {

                            if (map.get("task_id").equals(taskIdValue)) {

                                taskList.remove(map);
                                break;
                            }
                        }

                        adapter.notifyDataSetChanged();
                        view.setAlpha(1);
                    }
                });

            }
        });

        add_todo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                String taskName = add_todo_edit_text.getText().toString();

                if(!taskName.equals("")) {
                    HashMap<String, String> taskMap = new HashMap<String, String>();

                    taskMap.put("taskName", taskName);
                    taskMap.put("list_id", list_id);

                    add_todo_edit_text.setText("");

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("task_id", dbtools.getNextMaxID("task"));
                    map.put("name", taskName);
                    map.put("status", "0");
                    map.put("list_id", list_id);

                    taskList.add(0, map);

                    dbtools.addTaskWithList(taskMap);

                    adapter.notifyDataSetChanged();
                    view.setAlpha(1);

                }
            }
        });

        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
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
                    hideSoftKeyboard(ListActivity.this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
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
            View rootView = inflater.inflate(R.layout.fragment_list, container, false);
            return rootView;
        }*/
    }

}
