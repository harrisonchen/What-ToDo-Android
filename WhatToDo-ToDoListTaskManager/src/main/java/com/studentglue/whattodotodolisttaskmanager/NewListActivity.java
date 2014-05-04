package com.studentglue.whattodotodolisttaskmanager;

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
import android.widget.EditText;

import java.util.HashMap;

public class NewListActivity extends ActionBarActivity {

    EditText add_list_edit_text;
    Button add_list_btn;

    DBTools dbtools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        dbtools = new DBTools(this);

        add_list_edit_text = (EditText) findViewById(R.id.add_list_edit_text);
        add_list_btn = (Button) findViewById(R.id.add_list_btn);

        add_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String list_name = add_list_edit_text.getText().toString();

                if(!list_name.equals("")) {
                    HashMap<String, String> taskMap = new HashMap<String, String>();

                    taskMap.put("category", list_name);

                    dbtools.addList(taskMap);

                    setResult(RESULT_OK);

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
        getMenuInflater().inflate(R.menu.new_list, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_new_list, container, false);
            return rootView;
        }*/
    }

}
