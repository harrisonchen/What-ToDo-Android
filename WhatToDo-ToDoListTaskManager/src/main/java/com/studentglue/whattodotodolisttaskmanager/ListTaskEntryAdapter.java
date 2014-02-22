package com.studentglue.whattodotodolisttaskmanager;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ListTaskEntryAdapter extends BaseAdapter {

    ArrayList<HashMap<String, String>> list;
    Context context;
    DBTools dbtools;

    ListTaskEntryAdapter(Context c, String list_id) {

        context = c;
        dbtools = new DBTools(context);

        list = dbtools.getAllTaskFromList(list_id);
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
        TextView listName = (TextView) row.findViewById(R.id.listName);
        ImageView importantColor = (ImageView) row.findViewById(R.id.importantColor);

        HashMap<String, String> taskMap = list.get(i);

        taskId.setText(taskMap.get("task_id"));
        taskName.setText(taskMap.get("name"));

        if (taskMap.get("list_id").equals("-1")) {
            listName.setText("None");
        }
        else {
            listName.setText(dbtools.getListCategory(taskMap.get("list_id")));
        }

        switch(taskMap.get("importance").charAt(0) - 48) {
            case 0:
                importantColor.setImageResource(R.drawable.transparent_tab);
                break;
            case 1:
                importantColor.setImageResource(R.drawable.green_tab);
                break;
            case 2:
                importantColor.setImageResource(R.drawable.green_tab);
                break;
            case 3:
                importantColor.setImageResource(R.drawable.green_tab);
                break;
            default:
                importantColor.setImageResource(R.drawable.green_tab);
                break;
        }

        if(taskMap.get("status").equals("1")){
            taskName.setPaintFlags(taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        return row;
    }
}
