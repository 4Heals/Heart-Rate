package com.example.hrate.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class AppSQLiteOpenHelper extends SQLiteOpenHelper implements DBConstant {
    private static final String TAG = "db";
    AppSQLiteOpenHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SQL = String.format("CREATE table %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER,%s INTEGER ,%s TEXT, %s INTEGER)"
                , TABLE_RESULTS, RESULT_ID, RESULT_VALUE, RESULT_USER_STATUS_ID,RESULT_NOTE, RESULT_TIMESTAMP);
        Log.d(TAG,CREATE_SQL);
        db.execSQL(CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_SQL = String.format("DROP TABLE if EXIST %s",TABLE_RESULTS);
        db.execSQL(DROP_SQL);
    }
}
