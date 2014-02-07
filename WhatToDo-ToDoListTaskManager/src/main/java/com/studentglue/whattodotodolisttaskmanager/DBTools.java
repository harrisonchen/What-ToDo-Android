package com.studentglue.whattodotodolisttaskmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DBTools extends SQLiteOpenHelper {

    public DBTools(Context applicationContext) {

        super(applicationContext, "todo.db", null, 1);
    }

    public void onCreate(SQLiteDatabase database) {

        String query = "CREATE TABLE task(" +
                "taskId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT)," +
                "status INTEGER DEFAULT 0";

        database.execSQL(query);
    }

    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {

        String query = "DROP TABLE IF EXISTS todo";

        database.execSQL(query);

        onCreate(database);
    }

    public void addTask(HashMap<String, String> queryValues) {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", queryValues.get("taskName"));

        database.insert("todo", null, values);

        database.close();
    }

    public int updateTask(HashMap<String, String> queryValues) {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", queryValues.get("taskName"));

        return database.update("todo", values, "taskId" + " = ?",
                                new String[] { queryValues.get("taskId") } );

    }

    public void deleteTask(String id) {

        SQLiteDatabase database = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM todo WHERE taskId='" + id + "'";

        database.execSQL(deleteQuery);

        database.close();
    }

    public ArrayList<HashMap<String, String>> getAllTasks() {

        ArrayList<HashMap<String, String>> taskArrayList;

        taskArrayList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT * FROM tasks";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            do {

                HashMap<String, String> taskMap = new HashMap<String, String>();

                taskMap.put("taskId", cursor.getString(0));
                taskMap.put("name", cursor.getString(1));
                taskMap.put("status", cursor.getString(2));

                taskArrayList.add(taskMap);
            } while (cursor.moveToNext());
        }

        return taskArrayList;
    }

    public HashMap<String, String> getTask(String id) {

        HashMap<String, String> taskMap = new HashMap<String, String>();

        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM contacts where taskId='" + id + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            do {

                taskMap.put("taskId", cursor.getString(0));
                taskMap.put("name", cursor.getString(1));
                taskMap.put("status", cursor.getString(2));
            } while (cursor.moveToNext());
        }

        return taskMap;
    }

}
