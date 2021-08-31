package com.example.hrate.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class AppDatabase implements DBConstant{
    private AppSQLiteOpenHelper appSQLiteOpenHelper;

    private AppDatabase(Context context) {
        appSQLiteOpenHelper = new AppSQLiteOpenHelper(context);
    }

    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = new AppDatabase(context);
                }
            }
        }
        return instance;
    }

    public long insertNewResult(HeartRateResult item) {
        SQLiteDatabase sqLiteDatabase = instance.appSQLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RESULT_VALUE, item.getValue());
        contentValues.put(RESULT_USER_STATUS_ID, item.getUserStatusId());
        contentValues.put(RESULT_NOTE,item.getNote());
        contentValues.put(RESULT_TIMESTAMP,item.getTimestamp());
        return sqLiteDatabase.insert(TABLE_RESULTS, null, contentValues);
    }

    public ArrayList<HeartRateResult> getResultInTimeRange(long startTime, long endTime) {
        ArrayList<HeartRateResult> resList = new ArrayList<>();
        SQLiteDatabase db = instance.appSQLiteOpenHelper.getReadableDatabase();
        String[] column = {RESULT_ID, RESULT_VALUE,RESULT_USER_STATUS_ID, RESULT_NOTE, RESULT_TIMESTAMP};
        String selection = RESULT_TIMESTAMP + " >= ? AND " + RESULT_TIMESTAMP + " < ?";
        String[] selectionArgs = new String[]{String.valueOf(startTime), String.valueOf(endTime)};
        Cursor cursor = db.query(TABLE_RESULTS, column, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            resList.add(getFromCursor(cursor));
        }
        return resList;
    }

    public HeartRateResult getResultById(int id) {
        HeartRateResult res = null;
        SQLiteDatabase db = instance.appSQLiteOpenHelper.getReadableDatabase();
        String[] column = {RESULT_ID, RESULT_VALUE,RESULT_USER_STATUS_ID, RESULT_NOTE, RESULT_TIMESTAMP};
        String selection = RESULT_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        Cursor cursor = db.query(TABLE_RESULTS, column, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            res = getFromCursor(cursor);
        }
        return res;
    }

    private HeartRateResult getFromCursor(Cursor cursor){
        int id = cursor.getInt(0);
        int value = cursor.getInt(1);
        int userStatusId = cursor.getInt(2);
        String note = cursor.getString(3);
        long timeStamp = cursor.getLong(4);
        return new HeartRateResult(id,value,userStatusId,note,timeStamp);
    }
}
