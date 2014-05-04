package com.studentglue.whattodotodolisttaskmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewTaskActivity extends ActionBarActivity {

    EditText task_name_edit_text;
    EditText new_category_edit_text;
    Spinner category_spinner;
    Button add_todo_btn;

    boolean is_new_category;
    String list_id;

    ArrayList<HashMap<String, String>> list_entries;

    List<String> entries;

    DBTools dbtools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        setupUI(findViewById(R.id.container));

        dbtools = new DBTools(this);

        task_name_edit_text = (EditText) findViewById(R.id.task_name_edit_text);
        new_category_edit_text = (EditText) findViewById(R.id.new_category_edit_text);
        category_spinner = (Spinner) findViewById(R.id.category_spinner);
        add_todo_btn = (Button) findViewById(R.id.add_todo_btn);

        is_new_category = false;
        list_id = "-1";

        task_name_edit_text.requestFocus();

        list_entries = dbtools.getAllLists();

        entries = new ArrayList<String>();

        entries.add("Choose a Category");
        entries.add("No Category");
        entries.add("New Category");

        for(int i = 0; i < list_entries.size(); ++i) {
            entries.add(list_entries.get(i).get("category"));
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, entries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setAdapter(adapter);

        add_todo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                String taskName = task_name_edit_text.getText().toString();
                String list_category;

                if(is_new_category) {
                    list_category = new_category_edit_text.getText().toString();
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("category", list_category);
                    dbtools.addList(map);

                    if(!taskName.equals("")) {
                        HashMap<String, String> taskMap = new HashMap<String, String>();

                        taskMap.put("taskName", taskName);
                        taskMap.put("list_id", dbtools.getListId(list_category));

                        dbtools.addTaskWithList(taskMap);

                        setResult(RESULT_OK);

                        finish();
                    }
                }
                else {
                    if(!taskName.equals("")) {
                        HashMap<String, String> taskMap = new HashMap<String, String>();

                        taskMap.put("taskName", taskName);
                        taskMap.put("list_id", list_id);

                        dbtools.addTaskWithList(taskMap);

                        setResult(RESULT_OK);

                        finish();
                    }
                }
            }
        });

        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(category_spinner.getSelectedItem().toString().equals("New Category")) {
                    new_category_edit_text.setVisibility(View.VISIBLE);
                    new_category_edit_text.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(new_category_edit_text, InputMethodManager.SHOW_IMPLICIT);

                    is_new_category = true;
                }
                else {
                    String choice = category_spinner.getSelectedItem().toString();
                    if(!choice.equals("No Category") && !choice.equals("Choose a Category")) {
                        list_id = dbtools.getListId(choice);
                    }
                    else {
                        list_id = "-1";
                    }

                    is_new_category = false;
                    new_category_edit_text.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
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
                    hideSoftKeyboard(NewTaskActivity.this);
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
        getMenuInflater().inflate(R.menu.new_task, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_new_task, container, false);
            return rootView;
        }*/
    }

}
