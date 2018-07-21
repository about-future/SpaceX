package com.about.future.spacex.utils;

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

}
