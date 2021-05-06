package com.charles.myroadsideassistant.Credentials;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class UserStatus extends SQLiteOpenHelper {
        public UserStatus(@Nullable Context context) {
            super(context, "userstatus", null, 1);
        }

        public void insertData(String user_status) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("userStatus", user_status);
            db.insert("user", null, cv);
            db.close();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "create table user(userStatus text);";
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }

}