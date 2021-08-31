package com.example.hrate.data;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSharedPreferences {
    private static final String SHARED_PREFERENCES_NAME = "heartRateData";
    private SharedPreferences sharedPreferences;
    private static AppSharedPreferences instance = null;

    private AppSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static AppSharedPreferences getInstance(Context context){
        if (instance == null){
            synchronized (AppSharedPreferences.class){
                instance = new AppSharedPreferences(context);
            }
        }
        return instance;
    }

    public SharedPreferences getValues(){
        return sharedPreferences;
    }

    public SharedPreferences.Editor getEditor(){
        return sharedPreferences.edit();
    }
}
