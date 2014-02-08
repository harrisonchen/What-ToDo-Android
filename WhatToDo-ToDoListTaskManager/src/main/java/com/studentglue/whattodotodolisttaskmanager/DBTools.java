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

        String taskCreateQuery = "CREATE TABLE list(list_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "category TEXT NOT NULL, status INTEGER DEFAULT 0)";

        String listCreateQuery = "CREATE TABLE task(task_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "list_id INTEGER DEFAULT -1, name TEXT, status INTEGER DEFAULT 0," +
                "FOREIGN KEY (list_id) REFERENCES list (list_id))";

        String query7 = "INSERT INTO list(category) VALUES ('Groceries')";
        String query8 = "INSERT INTO list(category) VALUES ('Chores')";
        String query9 = "INSERT INTO list(category) VALUES ('Bucket List')";

        String query2 = "INSERT INTO task(list_id, name) VALUES (1, 'buy chocolate')";
        String query3 = "INSERT INTO task(list_id, name) VALUES (1, 'buy chocolate')";
        String query4 = "INSERT INTO task(list_id, name) VALUES (1, 'buy chocolate')";
        String query5 = "INSERT INTO task(list_id, name) VALUES (1, 'buy chocolate')";
        String query6 = "INSERT INTO task(list_id, name) VALUES (1, 'buy chocolate')";

        database.execSQL(taskCreateQuery);
        database.execSQL(listCreateQuery);
        database.execSQL(query7);
        database.execSQL(query8);
        database.execSQL(query9);
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

        database.insert("task", null, values);

        database.close();
    }

    public void addTaskWithList(HashMap<String, String> queryValues) {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", queryValues.get("taskName"));
        values.put("list_id", queryValues.get("list_id"));

        database.insert("task", null, values);

        database.close();
    }

    public int updateTask(HashMap<String, String> queryValues) {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", queryValues.get("taskName"));
        values.put("status", queryValues.get("status"));

        return database.update("task", values, "task_id" + " = ?",
                new String[] { queryValues.get("task_id") } );

    }

    public void deleteTask(String id) {

        SQLiteDatabase database = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM task WHERE task_id='" + id + "'";

        database.execSQL(deleteQuery);

        database.close();
    }

    public ArrayList<HashMap<String, String>> getAllTasks() {

        ArrayList<HashMap<String, String>> taskArrayList;

        taskArrayList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT * FROM task ORDER BY task_id DESC";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            do {

                HashMap<String, String> taskMap = new HashMap<String, String>();

                taskMap.put("task_id", cursor.getString(0));
                taskMap.put("name", cursor.getString(2));
                taskMap.put("status", cursor.getString(3));

                taskArrayList.add(taskMap);
            } while (cursor.moveToNext());
        }

        database.close();

        return taskArrayList;
    }

    public HashMap<String, String> getTask(String id) {

        HashMap<String, String> taskMap = new HashMap<String, String>();

        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM task where task_id='" + id + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            do {

                taskMap.put("task_id", cursor.getString(0));
                taskMap.put("name", cursor.getString(2));
                taskMap.put("status", cursor.getString(3));
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
            max = 1;
        }

        database.close();

        return String.valueOf(max);

    }

    public String getTaskStatus(String id) {

        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT status FROM task WHERE task_id='" + id + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        String taskId = "";

        if (cursor.moveToFirst()) {

            taskId = cursor.getString(0);

        }

        database.close();

        return taskId;

    }

    /* LIST TABLE */

    public void addList(HashMap<String, String> queryValues) {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("category", queryValues.get("category"));

        database.insert("list", null, values);

        database.close();
    }

    public void deleteList(String id) {

        SQLiteDatabase database = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM list WHERE list.list_id='" + id + "'";
        String deleteTaskQuery = "DELETE FROM task WHERE task.list_id='" + id + "'";

        database.execSQL(deleteTaskQuery);
        database.execSQL(deleteQuery);

        database.close();
    }

    public ArrayList<HashMap<String, String>> getAllLists() {

        ArrayList<HashMap<String, String>> listArrayList;

        listArrayList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT * FROM list ORDER BY list_id DESC";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            do {

                HashMap<String, String> listMap = new HashMap<String, String>();

                listMap.put("list_id", cursor.getString(0));
                listMap.put("category", cursor.getString(1));

                listArrayList.add(listMap);
            } while (cursor.moveToNext());
        }

        database.close();

        return listArrayList;
    }

}