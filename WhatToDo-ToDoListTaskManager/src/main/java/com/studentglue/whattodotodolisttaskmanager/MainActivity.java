package com.studentglue.whattodotodolisttaskmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    Intent intent;
    TextView taskId;
    TextView taskName;

    EditText add_todo_edit_text;
    Button add_todo_btn;
    Button my_list_btn;

    ArrayList<HashMap<String, String>> taskList;

    DBTools dbtools = new DBTools(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI(findViewById(R.id.container));

        add_todo_btn = (Button) findViewById(R.id.add_todo_btn);
        //add_todo_btn.setTag(1);
        my_list_btn = (Button) findViewById(R.id.my_lists_btn);
        //my_list_btn.setTag(2);

        taskList = dbtools.getAllTasks();

        my_list_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent myListIntent = new Intent(getApplication(), MyListActivity.class);;

                startActivity(myListIntent);
            }
        });

        if(taskList.size() != 0) {

            ListView listView = (ListView) findViewById(R.id.taskListView);

            String[] from = new String[] { "taskId", "name" };
            int[] to = { R.id.taskId, R.id.taskName };

            final SimpleAdapter adapter = new SimpleAdapter(this, taskList, R.layout.task_entry,
                    from, to);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    taskId = (TextView) findViewById(R.id.taskId);
                    taskName = (TextView) findViewById(R.id.taskName);

                    final String taskIdValue = taskId.getText().toString();
                    final String taskNameValue = taskName.getText().toString();

                    dbtools.deleteTask(taskIdValue);

                    for (HashMap<String, String> map : taskList) {

                        if (map.get("taskId").equals(taskIdValue)) {

                            taskList.remove(map);
                            break;
                        }
                    }

                    /*HashMap<String, String> taskMap = new HashMap<String, String>();

                    taskMap.put("taskId", taskIdValue);
                    taskMap.put("name", taskNameValue);
                    String taskStatus = dbtools.getTaskStatus(taskIdValue);
                    if (taskStatus.equals("0")) {
                        taskMap.put("status", "1");
                        //taskName.setPaintFlags(taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    else {
                        taskMap.put("status", "0");
                        //taskName.setPaintFlags( taskName.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    }

                    dbtools.updateTask(taskMap);*/

                    adapter.notifyDataSetChanged();
                    view.setAlpha(1);

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

                        dbtools.addTask(taskMap);

                        add_todo_edit_text.setText("");

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("taskId", dbtools.getNextMaxID("tasks"));
                        map.put("name", taskName);
                        map.put("status", "0");

                        taskList.add(0, map);

                        adapter.notifyDataSetChanged();
                        view.setAlpha(1);

                    }
                }
            });

        }
        else {

            add_todo_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    add_todo_edit_text = (EditText) findViewById(R.id.add_todo_edit_text);

                    String taskName = add_todo_edit_text.getText().toString();

                    if(!taskName.equals("")) {
                        HashMap<String, String> taskMap = new HashMap<String, String>();

                        taskMap.put("taskName", taskName);

                        dbtools.addTask(taskMap);

                        add_todo_edit_text.setText("");

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("taskId", dbtools.getNextMaxID("tasks"));
                        map.put("name", taskName);
                        map.put("status", "0");

                        taskList.add(0, map);

                    }
                }
            });

        }

        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }
    }

    public void addTodo(View view) {

        EditText add_todo_edit_text = (EditText) findViewById(R.id.add_todo_edit_text);

        String taskName = add_todo_edit_text.getText().toString();

        if(!taskName.equals("")) {
            HashMap<String, String> taskMap = new HashMap<String, String>();

            taskMap.put("taskName", taskName);

            dbtools.addTask(taskMap);

            add_todo_edit_text.setText("");

            this.callMainActivity(view);

        }

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
