package com.example.instanote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;

class DbManager {
    private DbHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public static final String ID = "ID";
    public static final String TITLE = "TITLE";
    public static final String TEXT = "CONTENT";
    public static final String LINK = "LINK";

    DbManager(Context c) {
        context = c;
    }

    DbManager open() throws SQLException {
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    void close() {
        dbHelper.close();
    }

    void insertData(String title,String link,String content) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE,title);
        contentValues.put(LINK,link);
        contentValues.put(TEXT,content);
        database.insert("PinnedNotes",null ,contentValues);
    }

    void updateData(String id,String title,String link,String content) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID,id);
        contentValues.put(TITLE,title);
        contentValues.put(LINK,link);
        contentValues.put(TEXT,content);
        database.update("PinnedNotes", contentValues, "ID = ?",new String[] { id });
    }

    ArrayList<String> getAllPinnedNotes(String column) {
        ArrayList<String> arrayList = new ArrayList<>();

        Cursor res = database.rawQuery("select * from PinnedNotes", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            arrayList.add(res.getString(res.getColumnIndex(column)));
            res.moveToNext();
        }
        res.close();
        return arrayList;
    }

    ArrayList<Integer> getAllIds() {
        ArrayList<Integer> arrayList = new ArrayList<>();

        Cursor res = database.rawQuery("select * from PinnedNotes", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            arrayList.add(res.getInt(res.getColumnIndex("id")));
            res.moveToNext();
        }
        res.close();
        return arrayList;
    }

    void deleteSelectedNote(int id) throws SQLException {
        database.delete("PinnedNotes", "id=" + id, null);
    }
}