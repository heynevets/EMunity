package com.tingyuyeh.coen268a4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String COL1 = "ID";
    private static final String COL2 = "name";
    private static final String COL3 = "email";
    private static final String COL4 = "favourite";
    private static final String TABLE_NAME = "people_table";
    String TAG = "databasehelper";
    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder createTable = new StringBuilder()
            .append("CREATE TABLE " + TABLE_NAME)
            .append("(ID INTEGER PRIMARY KEY AUTOINCREMENT, ")
            .append(COL2 + " TEXT, ")
            .append(COL3 + " TEXT, ")
            .append(COL4 + " TEXT);");
        Log.d(TAG, createTable.toString());
        db.execSQL(createTable.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public boolean addData(Person p){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, p._name);
        contentValues.put(COL3, p._email);
        contentValues.put(COL4, p._favourite);
        Log.d(TAG, "addData");

        long result = db.insert(TABLE_NAME, null, contentValues);
        return (result == -1) ? false : true;
    }
    public boolean updateData(int id, Person p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, p._name);
        contentValues.put(COL3, p._email);
        contentValues.put(COL4, p._favourite);

        long result = db.update(TABLE_NAME, contentValues, "id="+id, null);
        return (result == -1) ? false : true;
    }
    public boolean deleteData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "id="+id, null);
        return (result == -1) ? false : true;
    }
    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);

        return data;
    }



}
