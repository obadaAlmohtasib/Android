package com.example.mycontentprovider.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mycontentprovider.bean.ToDo;

import java.util.ArrayList;
import java.util.List;

public class ToDoListDBAdapter {

    private static final String TAG = "ContentProviderDemo";
    public static final String DB_NAME ="todolist.db";
    public static final int DB_VERSION =2;
    public static final String TABLE_TODO = "table_todo";
    public static final String COLUMN_TODO_ID = "task_id";
    public static final String COLUMN_TODO = "todo";
    public static final String COLUMN_PLACE = "place";
    public static String CREATE_TABLE_TODO = "CREATE TABLE " + TABLE_TODO + "("+COLUMN_TODO_ID+" INTEGER PRIMARY KEY, "+
            COLUMN_TODO+" TEXT NOT NULL, "+COLUMN_PLACE+" TEXT)";

    private Context context;
    private SQLiteDatabase sqLiteDatabase;
    private static ToDoListDBAdapter toDoListDBAdapterInstance;

    private ToDoListDBAdapter (Context context) {
        this.context = context;
        this.sqLiteDatabase = new ToDoListDBHelper(context, DB_NAME, null, DB_VERSION).getWritableDatabase();
    }

    public static ToDoListDBAdapter getToDoListDBAdapterInstance(Context context)
    {
        if (toDoListDBAdapterInstance == null) {
            toDoListDBAdapterInstance = new ToDoListDBAdapter(context);
        }

        return toDoListDBAdapterInstance;
    }

    // Will be used in the content provider
    public Cursor getCursorForAllToDos() {
        return sqLiteDatabase.query(TABLE_TODO, new String[]{COLUMN_TODO_ID, COLUMN_TODO, COLUMN_PLACE}, null,
                null, null, null, null, null);
    }

    public Cursor getCursorForSpecificPlace(String place) {

        return sqLiteDatabase.query(TABLE_TODO, new String[]{COLUMN_TODO_ID, COLUMN_TODO, COLUMN_PLACE},
                COLUMN_PLACE + " = ?", new String[]{place}, null, null, null, null);
    }

    public Cursor getCount() {
        return sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM "+ TABLE_TODO, null);
    }


    // TODO:
    //  insert, delete, modify, query methods;
    public boolean insert(String toDoItem, String place){
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_TODO, toDoItem);
        contentValues.put(COLUMN_PLACE, place);

        return sqLiteDatabase.insert(TABLE_TODO, null, contentValues)>0;
    }

    // Will be used by the provider
    public long insert(ContentValues contentValues) {
        return sqLiteDatabase.insert(TABLE_TODO, null, contentValues);
    }

    public boolean delete(long taskId) {
        return sqLiteDatabase.delete(TABLE_TODO, COLUMN_TODO_ID + " = " + taskId, null)>0;
    }

    // Will be used by the provider
    public int delete(String whereClause, String[] whereArgs) {
        return sqLiteDatabase.delete(TABLE_TODO, whereClause, whereArgs);
    }

    public boolean modify(int taskId, String newToDoItem) {
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_TODO,newToDoItem);

        return sqLiteDatabase.update(TABLE_TODO,contentValues, COLUMN_TODO_ID+" = "+taskId,null)>0;
    }

    // Will be used in the content provider
    public int update(ContentValues contentValues, String s, String [] strings) {
        return sqLiteDatabase.update(TABLE_TODO,contentValues, s,strings);
    }

    public List<ToDo> getAllToDos() {
        List<ToDo> toDoList = new ArrayList<>();

        Cursor cursor = getCursorForAllToDos();
        if (cursor != null && cursor.moveToFirst())
        {
            do {
                ToDo toDo = new ToDo(cursor.getLong(0), cursor.getString(1), cursor.getString(2));
                toDoList.add(toDo);
            } while (cursor.moveToNext());
        }
        return toDoList;
    }

    private static class ToDoListDBHelper extends SQLiteOpenHelper {


        public ToDoListDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

//        @Override
//        public void onOpen(SQLiteDatabase db) {
//            super.onOpen(db);
//        }
//
//        @Override
//        public void onConfigure(SQLiteDatabase db) {
//            super.onConfigure(db);
//            db.setForeignKeyConstraintsEnabled(true);
//        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_TODO);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
