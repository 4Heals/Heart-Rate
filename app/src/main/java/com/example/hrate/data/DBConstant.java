package com.example.hrate.data;

public interface DBConstant {
    // SQL DATABASE
    String DB_NAME = "cf.bautroixa.heartratemonitor.db";
    int DB_VERION = 1;

    // SQL table: RESULTS
    String TABLE_RESULTS = "results";
    String RESULT_ID = "id";
    String RESULT_VALUE = "value";
    String RESULT_USER_STATUS_ID = "userStatusId";
    String RESULT_NOTE = "note";
    String RESULT_TIMESTAMP = "timestamp";


    // SHARED PREFERENCES:
    String KEY_LAST_RESULT_ID = "KEY_LAST_RESULT_ID";
    String KEY_TUTORIAL_HOME_FRAG = "KEY_TUTORIAL_HOME_FRAG";
    String KEY_TUTORIAL_TREND_FRAG = "KEY_TUTORIAL_TREND_FRAG";
    String KEY_TUTORIAL_RESULT_ACTV = "KEY_TUTORIAL_RESULT_ACTV";

}
