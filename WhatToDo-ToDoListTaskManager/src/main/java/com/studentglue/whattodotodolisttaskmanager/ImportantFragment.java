package com.studentglue.whattodotodolisttaskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ImportantFragment extends Fragment {

    private static final int UPDATE_LISTVIEW = 2;

    Button what_todo_btn;

    TextView taskId;
    TextView taskName;
    TextView task_text_view;

    ArrayList<HashMap<String, String>> taskList;
    ListView listView;
    ImportantTaskEntryAdapter customAdapter;

    DBTools dbtools;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        dbtools = new DBTools(getActivity());

        View importantFragmentView = inflater.inflate(R.layout.fragment_important_list, container, false);

        what_todo_btn = (Button) importantFragmentView.findViewById(R.id.what_todo_btn);

        taskList = dbtools.getAllImportantTasks();

        listView = (ListView) importantFragmentView.findViewById(R.id.importantTasksList);

        customAdapter = new ImportantTaskEntryAdapter(getActivity());
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

        return importantFragmentView;
    }

    public void setAdapter() {

        customAdapter = new ImportantTaskEntryAdapter(getActivity());
        listView.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == UPDATE_LISTVIEW && resultCode == getActivity().RESULT_OK) {

            setAdapter();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}
