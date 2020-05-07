package com.example.instanote;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;

class DbManager {
    private DbHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

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