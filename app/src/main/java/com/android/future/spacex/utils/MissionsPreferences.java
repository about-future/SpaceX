package com.android.future.spacex.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.future.spacex.R;

public class MissionsPreferences {
    private static final String CATEGORY = "missions_loaded";

    public static Boolean getLoadingStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(CATEGORY, false);
    }

    public static void setLoadingStatus(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(CATEGORY, status);
        editor.apply();
    }
}
