package com.about.future.spacex.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SpaceXPreferences {
    private static final String MISSIONS = "missions_loaded";
    private static final String LAUNCH_PADS = "launch_pads_loaded";

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
}
