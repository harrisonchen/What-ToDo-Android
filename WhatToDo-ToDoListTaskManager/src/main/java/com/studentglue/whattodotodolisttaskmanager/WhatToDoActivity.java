package com.studentglue.whattodotodolisttaskmanager;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class WhatToDoActivity extends ActionBarActivity {

    String prevTask = "";

    Button maybe_later_btn;

    Intent intent;
    Bundle extras;
    String importance, list_id;

    TextView todo_text_view;

    DBTools dbtools = new DBTools(this);
    int incompleteTaskCount;
    int incompleteTaskFromListCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_what_to_do);

        intent = getIntent();
        extras = intent.getExtras();

        importance = extras.getString("IMPORTANCE");
        list_id = extras.getString("LIST_ID");

        incompleteTaskCount = dbtools.getIncompleteTaskCount();
        incompleteTaskFromListCount = dbtools.getIncompleteTaskFromListCount(list_id);

        maybe_later_btn = (Button) findViewById(R.id.maybe_later_btn);

        todo_text_view = (TextView) findViewById(R.id.todo_text_view);

        HashMap<String, String> random_task = new HashMap<String, String>();

        if (importance.equals("1") && incompleteTaskCount > 0) {
            random_task = dbtools.getRandomImportantTask(prevTask);
            todo_text_view.setText(random_task.get("name"));
            prevTask = random_task.get("task_id");
        }
        else if (!list_id.equals("-1") && incompleteTaskFromListCount > 0) {
            random_task = dbtools.getRandomImportantTaskFromList(prevTask, list_id);
            todo_text_view.setText(random_task.get("name"));
            prevTask = random_task.get("task_id");
        }
        else if (list_id.equals("-1") && incompleteTaskCount > 0) {
            random_task = dbtools.getRandomTask(prevTask);
            todo_text_view.setText(random_task.get("name"));
            prevTask = random_task.get("task_id");
        }
        else {
            todo_text_view.setText("do something...");
        }

        maybe_later_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                HashMap<String, String> random_task = new HashMap<String, String>();

                if (importance.equals("1") && incompleteTaskCount > 1) {
                    random_task = dbtools.getRandomImportantTask(prevTask);
                    todo_text_view.setText(random_task.get("name"));
                    prevTask = random_task.get("task_id");
                }
                else if (!list_id.equals("-1") && incompleteTaskFromListCount > 1) {
                    random_task = dbtools.getRandomImportantTaskFromList(prevTask, list_id);
                    todo_text_view.setText(random_task.get("name"));
                    prevTask = random_task.get("task_id");
                }
                else if (list_id.equals("-1") && incompleteTaskCount > 1) {
                    random_task = dbtools.getRandomTask(prevTask);
                    todo_text_view.setText(random_task.get("name"));
                    prevTask = random_task.get("task_id");
                }
                else if (incompleteTaskCount <= 1 || incompleteTaskFromListCount <= 1) {
                    finish();
                }
            }
        });



        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.what_to_do, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_what_to_do, container, false);
            return rootView;
        }*/
    }

}
