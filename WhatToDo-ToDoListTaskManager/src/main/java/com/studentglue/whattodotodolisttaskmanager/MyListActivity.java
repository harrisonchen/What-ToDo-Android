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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyListActivity extends ActionBarActivity {

    private static final int REQUEST_CODE = 1234;
    private static final int GO_TO_LIST = 5;
    private static final int NEW_LIST = 6;

    TextView listId;
    TextView listName;

    ImageButton add_list_btn;
    EditText add_list_edit_text;

    ArrayList<HashMap<String, String>> myList;
    ListView listView;
    ListEntryAdapter customAdapter;

    DBTools dbtools = new DBTools(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);
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

        add_list_btn = (ImageButton) findViewById(R.id.add_list_btn);
        add_list_edit_text = (EditText) findViewById(R.id.add_list_edit_text);

        myList = dbtools.getAllLists();

        listView = (ListView) findViewById(R.id.myListView);
        customAdapter = new ListEntryAdapter(this);
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {

                listId = (TextView) view.findViewById(R.id.listId);
                listName = (TextView) view.findViewById(R.id.listName);

                final String listIdValue = listId.getText().toString();
                final String listNameValue = listName.getText().toString();

                Intent listIntent = new Intent(getApplication(), ListActivity.class);
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
                else {
                    Intent newTaskIntent = new Intent(getApplication(), NewListActivity.class);
                    startActivityForResult(newTaskIntent, NEW_LIST);
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

        customAdapter = new ListEntryAdapter(this);
        listView.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {

        setResult(RESULT_OK);

        super.onBackPressed();
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

                add_list_edit_text = (EditText) findViewById(R.id.add_todo_edit_text);
                add_list_edit_text.setText(textInput);
            }
        }
        else if (requestCode == GO_TO_LIST && resultCode == RESULT_OK) {

            setAdapter();
        }
        else if (requestCode == NEW_LIST && resultCode == RESULT_OK) {

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
                    hideSoftKeyboard(MyListActivity.this);
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
        getMenuInflater().inflate(R.menu.my_list, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_my_list, container, false);
            return rootView;
        }*/
    }

}
