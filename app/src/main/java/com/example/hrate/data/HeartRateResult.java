package com.example.hrate.data;

import android.text.format.DateFormat;

import java.util.Calendar;

public class HeartRateResult {
    private int id;
    private int value;
    private int userStatusId;
    private String note;
    private long timestamp;

    public HeartRateResult(int id, int value, int userStatusId, String note, long timestamp) {
        this.id = id;
        this.value = value;
        this.userStatusId = userStatusId;
        this.note = note;
        this.timestamp = timestamp;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getUserStatusId() {
        return userStatusId;
    }

    public void setUserStatusId(int userStatusId) {
        this.userStatusId = userStatusId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTime(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp * 1000);
        String date = DateFormat.format("HH:mm", cal).toString();
        return date;
    }

    private String getDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
