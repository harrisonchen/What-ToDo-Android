package com.studentglue.whattodotodolisttaskmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class DBTools extends SQLiteOpenHelper {

    public DBTools(Context applicationContext) {

        super(applicationContext, "todo.db", null, 1);
    }

    public void onCreate(SQLiteDatabase database) {

        String listCreateQuery = "CREATE TABLE list(list_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "category TEXT NOT NULL, status INTEGER DEFAULT 0)";

        String taskCreateQuery = "CREATE TABLE task(task_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "list_id INTEGER DEFAULT -1, name TEXT, status INTEGER DEFAULT 0, " +
                "importance INTEGER DEFAULT 0, " +
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
        values.put("importance", queryValues.get("importance"));

        return database.update("task", values, "task_id" + " = ?",
                new String[] { queryValues.get("task_id") } );

    }

    public int completeTask(String id) {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("status", "1");

        return database.update("task", values, "task_id" + " = ?",
                new String[] { id } );
    }

    public int toggleTaskComplete(String id, String status) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        if(status.equals("1")) {
            values.put("status", "0");
        }
        else {
            values.put("status", "1");
        }

        return database.update("task", values, "task_id" + " = ?",
                new String[] { id } );
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

        String selectQuery = "SELECT * FROM task ORDER BY status ASC, task_id DESC, importance DESC";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            do {

                HashMap<String, String> taskMap = new HashMap<String, String>();

                taskMap.put("task_id", cursor.getString(0));
                taskMap.put("list_id", cursor.getString(1));
                taskMap.put("name", cursor.getString(2));
                taskMap.put("status", cursor.getString(3));
                taskMap.put("importance", cursor.getString(4));

                taskArrayList.add(taskMap);
            } while (cursor.moveToNext());
        }

        database.close();

        return taskArrayList;
    }

    public ArrayList<HashMap<String, String>> getAllImportantTasks() {

        ArrayList<HashMap<String, String>> taskArrayList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT * FROM task WHERE importance > 0 ORDER BY status ASC, importance DESC, task_id DESC";

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            do {

                HashMap<String, String> taskMap = new HashMap<String, String>();

                taskMap.put("task_id", cursor.getString(0));
                taskMap.put("list_id", cursor.getString(1));
                taskMap.put("name", cursor.getString(2));
                taskMap.put("status", cursor.getString(3));
                taskMap.put("importance", cursor.getString(4));

                taskArrayList.add(taskMap);
            } while (cursor.moveToNext());
        }

        database.close();

        return taskArrayList;
    }

    public HashMap<String, String> getTask(String id) {

        HashMap<String, String> taskMap = new HashMap<String, String>();

        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM task WHERE task_id='" + id + "'";

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

    public String getTaskImportance(String id) {

        String importance = "0";

        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT importance FROM task WHERE task_id=" + id;

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            importance = cursor.getString(0);
        }

        database.close();

        return importance;
    }

    public HashMap<String, String> getRandomTask(String prevTask) {

        HashMap<String, String> taskMap = new HashMap<String, String>();

        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM task where status=0";

        Cursor cursor = database.rawQuery(selectQuery, null);

        Random rand = new Random();

        int randomTaskPosition;

        if (getIncompleteTaskCount() > 1) {

            do {
                randomTaskPosition = rand.nextInt(cursor.getCount());

                cursor.moveToPosition(randomTaskPosition);

                taskMap.put("task_id", cursor.getString(0));
                taskMap.put("name", cursor.getString(2));
                taskMap.put("status", cursor.getString(3));
            } while(cursor.getString(2).equals(prevTask));
        }
        else {

            cursor.moveToFirst();

            taskMap.put("task_id", cursor.getString(0));
            taskMap.put("name", cursor.getString(2));
            taskMap.put("status", cursor.getString(3));
        }

        database.close();

        return taskMap;
    }

    public int getIncompleteTaskCount() {

        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM task where status=0";

        Cursor cursor = database.rawQuery(selectQuery, null);

        int count = cursor.getCount();

        cursor.close();
        //database.close();

        return count;

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

        String taskStatus = "";

        if (cursor.moveToFirst()) {

            taskStatus = cursor.getString(0);

        }

        database.close();

        return taskStatus;

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

    public String getListCategory(String id) {

        SQLiteDatabase database = this.getWritableDatabase();

        String category = "";

        String selectQuery = "SELECT category FROM list WHERE list_id=" + id;

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            category = cursor.getString(0);
        }

        database.close();

        return category;

    }


    public String getTaskList(String id) {

        SQLiteDatabase database = this.getWritableDatabase();

        String list_id = "";
        String category = "";

        String selectQuery = "SELECT list_id FROM task WHERE task_id=" + id;

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            list_id = cursor.getString(0);
        }

        selectQuery = "SELECT category FROM list WHERE list_id=" + list_id;

        cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            category = cursor.getString(0);
        }

        database.close();

        return category;
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

    public ArrayList<HashMap<String, String>> getAllTaskFromList(String list_id) {

        ArrayList<HashMap<String, String>> taskArrayList;

        taskArrayList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT * FROM task WHERE task.list_id='" +
                list_id + "' ORDER BY status ASC, importance DESC, task_id DESC";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            do {

                HashMap<String, String> taskMap = new HashMap<String, String>();

                taskMap.put("task_id", cursor.getString(0));
                taskMap.put("list_id", cursor.getString(1));
                taskMap.put("name", cursor.getString(2));
                taskMap.put("status", cursor.getString(3));
                taskMap.put("importance", cursor.getString(4));

                taskArrayList.add(taskMap);
            } while (cursor.moveToNext());
        }

        database.close();

        return taskArrayList;
    }

    public int getTaskCountInList(String list_id) {

        SQLiteDatabase database = this.getWritableDatabase();

        int count = 0;

        String selectQuery = "SELECT * FROM task WHERE task.list_id=" + list_id +" AND status=0";

        Cursor cursor = database.rawQuery(selectQuery, null);

        count = cursor.getCount();

        database.close();

        return count;
    }

}