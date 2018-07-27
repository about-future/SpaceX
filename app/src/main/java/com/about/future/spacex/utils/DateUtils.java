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

    public static String shortDateFormat(String stringDate) {
        // Date format
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = simpleDateFormat.parse(stringDate, pos);
        SimpleDateFormat simpleDateReformat = new SimpleDateFormat("d MMMM yyyy", Locale.US);

        return simpleDateReformat.format(date);
    }
}
