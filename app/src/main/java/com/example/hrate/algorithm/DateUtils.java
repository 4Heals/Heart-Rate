package com.example.hrate.algorithm;

import android.os.Build;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

public class DateUtils {
    public static long DAY_MILIS = 24 * 60 * 60 * 1000;
    public static long WEEK_MILIS = 7 * DAY_MILIS;

    public static long getDayStartTimestamp(int minusDay) {
        long minusSec= minusDay * 24 * 60 * 60;
        ZoneId zone = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            zone = ZoneId.systemDefault();
            long startTime = ZonedDateTime.now(zone).toLocalDate().atStartOfDay(zone).toEpochSecond();
            return startTime - minusSec;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis() - minusSec*1000);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            return cal.getTimeInMillis()/1000;
        }
    }

    public static String getDateFromTimestamp(long timestamp){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM", java.util.Locale.getDefault());
        return simpleDateFormat.format(cal.getTime());
    }

    public static int getCurrentWeekDay(){
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_WEEK);
    }
}
