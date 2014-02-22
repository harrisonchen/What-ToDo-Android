package com.studentglue.whattodotodolisttaskmanager;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.HashMap;

public class TaskActivity extends ActionBarActivity {

    Intent intent;
    Bundle extras;
    String task_id;
    String task_name;
    String task_list;
    String task_importance;

    TextView list_belonging_to;

    EditText task_edittext;
    TextView level_of_importance_textview;
    SeekBar level_of_importance_seekbar;
    Button update_task_btn;
    Button delete_task_btn;
    Button finish_task_btn;

    DBTools dbtools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        setupUI(findViewById(R.id.container));

        dbtools = new DBTools(this);

        intent = getIntent();
        extras = intent.getExtras();
        task_id = extras.getString("EXTRA_TASK_ID");
        task_name = extras.getString("EXTRA_TASK_NAME");
        task_list = extras.getString("EXTRA_TASK_LIST");
        task_importance = extras.getString("EXTRA_TASK_IMPORTANCE");

        list_belonging_to = (TextView) findViewById(R.id.list_belonging_to);
        list_belonging_to.setText(task_list);

        task_edittext = (EditText) findViewById(R.id.task_edittext);
        level_of_importance_textview = (TextView) findViewById(R.id.level_of_importance_textview);
        level_of_importance_textview.setText(task_importance);
        level_of_importance_seekbar = (SeekBar) findViewById(R.id.level_of_importance_seekbar);
        level_of_importance_seekbar.setOnSeekBarChangeListener(levelOfImportanceSeekBarListener);
        level_of_importance_seekbar.setProgress(Integer.parseInt(task_importance));
        update_task_btn = (Button) findViewById(R.id.update_task_btn);
        delete_task_btn = (Button) findViewById(R.id.delete_task_btn);
        finish_task_btn = (Button) findViewById(R.id.finish_task_btn);

        if(dbtools.getTaskStatus(task_id).equals("0")) {
            finish_task_btn.setText(R.string.finish_task_btn);
        }
        else {
            finish_task_btn.setText(R.string.unfinish_task_btn);
        }


        task_edittext.setText(task_name);


        update_task_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String taskName = task_edittext.getText().toString();

                HashMap<String, String> taskMap = new HashMap<String, String>();

                taskMap.put("task_id", task_id);
                taskMap.put("taskName", taskName);
                taskMap.put("importance", String.valueOf(level_of_importance_seekbar.getProgress()));

                dbtools.updateTask(taskMap);

                setResult(Activity.RESULT_OK);

                finish();
            }
        });

        delete_task_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                dbtools.deleteTask(task_id);

                setResult(Activity.RESULT_OK);

                finish();
            }
        });

        finish_task_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                dbtools.toggleTaskComplete(task_id, dbtools.getTaskStatus(task_id));

                setResult(Activity.RESULT_OK);

                finish();
            }
        });



        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
    }

    private SeekBar.OnSeekBarChangeListener levelOfImportanceSeekBarListener =
            new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    int importance = level_of_importance_seekbar.getProgress();
                    level_of_importance_textview.setText(String.valueOf(importance));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            };

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(TaskActivity.this);
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
        getMenuInflater().inflate(R.menu.task, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_task, container, false);
            return rootView;
        }*/
    }

}
