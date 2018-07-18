package com.about.future.spacex.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Date;

public class SpaceXPreferences {
    private static final String MISSIONS = "missions_loaded";
    private static final String LAUNCH_PADS = "launch_pads_loaded";
    private static final String ROCKETS = "rockets_loaded";
    private static final String LAUNCH_PADS_THUMBNAILS_DATE = "launch_pads_thumbnails_date";

    public static Boolean getLoadingStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(MISSIONS, false);
    }

    public static void setLoadingStatus(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(MISSIONS, status);
        editor.apply();
    }

    public static Boolean getLaunchPadsStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(LAUNCH_PADS, false);
    }

    public static void setLaunchPadsStatus(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(LAUNCH_PADS, status);
        editor.apply();
    }

    public static long getLaunchPadsThumbnailsSavingDate(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(LAUNCH_PADS_THUMBNAILS_DATE, 1530000000);
    }

    public static void setLaunchPadsThumbnailsSavingDate(Context context, long saveDate) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(LAUNCH_PADS_THUMBNAILS_DATE, saveDate);
        editor.apply();
    }
}
