package com.charles.myroadsideassistant.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Profile extends SQLiteOpenHelper {

    Context context;

    public Profile(@Nullable Context context) {
        super(context, "profiledb", null, 1);
        this.context = context;
    }

    public void insertData(String name1, String mobile1, String imageuri1) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name1);
        cv.put("mobile", mobile1);
        cv.put("imageuri", imageuri1);
        db.insert("profile", null, cv);
        db.close();
    }

    public void delete() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("profile", null, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table profile(name text, mobile text, imageuri text);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}