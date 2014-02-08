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

        String query = "CREATE TABLE tasks(" +
                "taskId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "status INTEGER DEFAULT 0)";

        String query2 = "INSERT INTO tasks (name, status) VALUES ('Buy banana', 0)";
        String query3 = "INSERT INTO tasks (name, status) VALUES ('Buy strawberry', 0)";
        String query4 = "INSERT INTO tasks (name, status) VALUES ('Buy chocolate', 0)";
        String query5 = "INSERT INTO tasks (name, status) VALUES ('Buy donuts', 0)";
        String query6 = "INSERT INTO tasks (name, status) VALUES ('Go home', 0)";

        database.execSQL(query);
        database.execSQL(query2);
        database.execSQL(query3);
        database.execSQL(query4);
        database.execSQL(query5);
        database.execSQL(query6);
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

        database.insert("tasks", null, values);

        database.close();
    }

    public int updateTask(HashMap<String, String> queryValues) {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", queryValues.get("taskName"));
        values.put("status", queryValues.get("status"));

        return database.update("tasks", values, "taskId" + " = ?",
                                new String[] { queryValues.get("taskId") } );

    }

    public void deleteTask(String id) {

        SQLiteDatabase database = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM tasks WHERE taskId='" + id + "'";

        database.execSQL(deleteQuery);

        database.close();
    }

    public ArrayList<HashMap<String, String>> getAllTasks() {

        ArrayList<HashMap<String, String>> taskArrayList;

        taskArrayList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT * FROM tasks ORDER BY taskId DESC";

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

        database.close();

        return taskArrayList;
    }

    public HashMap<String, String> getTask(String id) {

        HashMap<String, String> taskMap = new HashMap<String, String>();

        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM tasks where taskId='" + id + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            do {

                taskMap.put("taskId", cursor.getString(0));
                taskMap.put("name", cursor.getString(1));
                taskMap.put("status", cursor.getString(2));
            } while (cursor.moveToNext());
        }

        database.close();

        return taskMap;
    }

    public String getNextMaxID(String table) {

        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM SQLITE_SEQUENCE WHERE name='" + table + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        int max;

        if (cursor.moveToFirst()) {
            max = Integer.parseInt(cursor.getString(1));
            max = max + 1;
        }
        else {
            max = 0;
        }

        database.close();

        return String.valueOf(max);

    }

    public String getTaskStatus(String id) {

        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT status FROM tasks WHERE taskId='" + id + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        String taskId = "";

        if (cursor.moveToFirst()) {

            taskId = cursor.getString(0);

        }

        database.close();

        return taskId;

    }

}
