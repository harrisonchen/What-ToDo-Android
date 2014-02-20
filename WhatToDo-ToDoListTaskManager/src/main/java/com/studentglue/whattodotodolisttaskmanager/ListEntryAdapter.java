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

/**
 * Created by harrison on 2/19/14.
 */
public class ListEntryAdapter extends BaseAdapter {

    ArrayList<HashMap<String, String>> list;
    Context context;
    DBTools dbtools;

    ListEntryAdapter(Context c) {

        context = c;
        dbtools = new DBTools(context);

        list = dbtools.getAllLists();
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
        View row = inflater.inflate(R.layout.list_entry, viewGroup, false);

        TextView listId = (TextView) row.findViewById(R.id.listId);
        TextView listName = (TextView) row.findViewById(R.id.listName);

        HashMap<String, String> listMap = list.get(i);

        listId.setText(listMap.get("list_id"));
        listName.setText(listMap.get("category") +
                " (" + dbtools.getTaskCountInList(listMap.get("list_id")) + ")" );

        return row;
    }
}
