package com.about.future.spacex.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    public static String formatDate(Date missionDate) {
        // Date format
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm, d MMMM yyyy", Locale.US);
        // Set the timezone reference for formatting
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().getID()));

        return simpleDateFormat.format(missionDate);
    }

    public static String formatTimeLeft(long missionDate) {
        long seconds = missionDate - (new Date().getTime() / 1000);
        int minutes = (int) ((seconds % 3600) / 60);
        int hours = (int) ((seconds / 3600) % 24);
        int days = (int) (seconds / 24) / 3600;

        String timeFormat = "%d days, %d hours and %d minutes";

        // Format days
        if (days == 0) {
            timeFormat = "%d hours and %d minutes";
        } else if (days == 1) {
            timeFormat = timeFormat.replace("days", "day");
        }

        // Format hours
        if (hours == 0 && days == 0) {
            timeFormat = "%d minutes";
        } else if (hours == 1) {
            timeFormat = timeFormat.replace("hours", "hour");
        }

        // Format minutes
        if (minutes == 1) {
            timeFormat = timeFormat.replace("minutes", "minute");
        }

        // Return final format
        if (days == 0 && hours == 0) {
            return String.format(Locale.US, timeFormat, minutes);
        } else if (days == 0) {
            return String.format(Locale.US, timeFormat, hours, minutes);
        } else {
            return String.format(Locale.US, timeFormat, days, hours, minutes);
        }
    }

    public static String shortDateFormat(String stringDate) {
        // Date format
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = simpleDateFormat.parse(stringDate, pos);
        SimpleDateFormat simpleDateReformat = new SimpleDateFormat("d MMMM yyyy", Locale.US);

        return simpleDateReformat.format(date);
    }


}
