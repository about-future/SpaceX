package com.about.future.spacex.utils;

import android.content.Context;
import android.content.res.Resources;

import com.about.future.spacex.R;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    // Format and return the launch date and time, using the preferred system (metric or imperial)
    public static String formatDate(Context context, Date missionDate) {
        boolean isMetric = SpaceXPreferences.isMetric(context);

        // Date format
        SimpleDateFormat simpleDateFormat;
        if (isMetric) {
            simpleDateFormat = new SimpleDateFormat("HH:mm, d MMMM yyyy", Locale.US);
        } else {
            simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy 'at' hh:mm a", Locale.US);
        }
        // Set the timezone reference for formatting
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().getID()));

        return simpleDateFormat.format(missionDate);
    }

    // Format and return the time left until lunch, using days, hours and minutes
    public static String formatTimeLeft(Context context, long missionDate) {
        Resources res = context.getResources();

        // Get the second left since now until launch time
        long seconds = missionDate - (new Date().getTime() / 1000);
        // Extract days, hours and minutes
        int minutes = (int) ((seconds % 3600) / 60);
        int hours = (int) ((seconds / 3600) % 24);
        int days = (int) (seconds / 24) / 3600;

        // Return final format
        if (days == 0 && hours == 0) {
            return String.format(
                    Locale.US,
                    context.getString(R.string.time_left_format_short),
                    res.getQuantityString(R.plurals.number_of_minutes, minutes, minutes));
        } else if (days == 0) {
            return String.format(
                    Locale.US,
                    context.getString(R.string.time_left_format_medium),
                    res.getQuantityString(R.plurals.number_of_hours, hours, hours),
                    res.getQuantityString(R.plurals.number_of_minutes, minutes, minutes));
        } else {
            return String.format(
                    Locale.US,
                    context.getString(R.string.time_left_format_long),
                    res.getQuantityString(R.plurals.number_of_days, days, days),
                    res.getQuantityString(R.plurals.number_of_hours, hours, hours),
                    res.getQuantityString(R.plurals.number_of_minutes, minutes, minutes));
        }
    }

    public static String shortDateFormat(Context context, String stringDate) {
        boolean isMetric = SpaceXPreferences.isMetric(context);
        // Date format
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = simpleDateFormat.parse(stringDate, pos);
        SimpleDateFormat simpleDateReformat;
        if (isMetric) {
            simpleDateReformat = new SimpleDateFormat("d MMMM yyyy", Locale.US);
        } else {
            simpleDateReformat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
        }

        return simpleDateReformat.format(date);
    }

    // Long format and return the date
    public static String getFullDate(long dateLong) {
        Date date = new Date(dateLong);
        // Date format
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.US);

        return simpleDateFormat.format(date);
    }
}
