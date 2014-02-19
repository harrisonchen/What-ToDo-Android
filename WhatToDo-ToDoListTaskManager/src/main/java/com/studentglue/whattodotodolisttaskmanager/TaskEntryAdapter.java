package com.studentglue.whattodotodolisttaskmanager;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskEntryAdapter extends BaseAdapter {

    ArrayList<HashMap<String, String>> list;
    Context context;

    TaskEntryAdapter(Context c) {

        context = c;
        DBTools dbtools = new DBTools(context);

        list = dbtools.getAllTasks();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.task_entry, viewGroup, false);

        TextView taskId = (TextView) row.findViewById(R.id.taskId);
        TextView taskName = (TextView) row.findViewById(R.id.taskName);

        HashMap<String, String> taskMap = list.get(i);

        taskId.setText(taskMap.get("task_id"));
        taskName.setText(taskMap.get("name"));

        if(taskMap.get("status").equals("1")){
            taskName.setPaintFlags(taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        return row;
    }
}
