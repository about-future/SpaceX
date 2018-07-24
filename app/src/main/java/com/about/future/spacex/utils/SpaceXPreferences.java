package com.about.future.spacex.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.about.future.spacex.R;

import java.util.Date;

public class SpaceXPreferences {

    public static boolean getLoadingStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_mission_status_key), false);
    }

    public static void setLoadingStatus(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_mission_status_key), status);
        editor.apply();
    }

    public static boolean getLaunchPadsStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_launch_pads_status_key), false);
    }

    public static void setLaunchPadsStatus(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_launch_pads_status_key), status);
        editor.apply();
    }

    public static long getLaunchPadsThumbnailsSavingDate(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(context.getString(R.string.pref_launch_pads_thumbnails_date_key), 1530000000);
    }

    public static void setLaunchPadsThumbnailsSavingDate(Context context, long saveDate) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(context.getString(R.string.pref_launch_pads_thumbnails_date_key), saveDate);
        editor.apply();
    }

    public static boolean getRocketsStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_rockets_status_key), false);
    }

    public static void setRocketsStatus(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_rockets_status_key), status);
        editor.apply();
    }

    public static void setTopicSubscriptionStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_topic_subscription_status_key), true);
        editor.apply();
    }

    public static boolean getTopicSubscriptionStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_topic_subscription_status_key), false);
    }
}
