package com.about.future.spacex.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.about.future.spacex.R;

public class SpaceXPreferences {

    // Return true if missions list was downloaded before or false if was never downloaded
    public static boolean getMissionsStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_mission_status_key), true);
    }

    // Set missions downloading status to true the first time the app is run and every time
    // the list of missions is refreshed
    public static void setMissionsStatus(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_mission_status_key), status);
        editor.apply();
    }

    // Return true if launchpads list was downloaded before or false if was never downloaded
    public static boolean getLaunchPadsStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_launch_pads_status_key), true);
    }

    // Set launch pads downloading status to true the first time the app is run and every time
    // the list of missions is refreshed
    public static void setLaunchPadsStatus(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_launch_pads_status_key), status);
        editor.apply();
    }

    // Return the date when launch pads thumbnails was first downloaded
    public static long getLaunchPadsThumbnailsSavingDate(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(context.getString(R.string.pref_launch_pads_thumbnails_date_key), 1530000000);
    }

    // Set the date when thumbnails were downloaded
    public static void setLaunchPadsThumbnailsSavingDate(Context context, long saveDate) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(context.getString(R.string.pref_launch_pads_thumbnails_date_key), saveDate);
        editor.apply();
    }

    // Return true if rockets list was downloaded before or false if was never downloaded
    public static boolean getRocketsStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_rockets_status_key), true);
    }

    // Set rockets downloading status to true the first time the app is run and every time
    // the list of missions is refreshed
    public static void setRocketsStatus(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_rockets_status_key), status);
        editor.apply();
    }

    // Set true if the user was subscribed to firebase topics
    public static void setTopicSubscriptionStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_topic_subscription_status_key), true);
        editor.apply();
    }

    // Return true if user was subsribed to firebase topics
    public static boolean getTopicSubscriptionStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_topic_subscription_status_key), false);
    }

    // Return true is Metric preference is selected or false if Imperial system is selected
    public static boolean isMetric(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String preferredUnits = sp.getString(
                context.getString(R.string.pref_units_key),
                context.getString(R.string.pref_units_metric));

        return preferredUnits.equals(context.getString(R.string.pref_units_metric));
    }

    // Return true if show acronym meaning preference is enabled or false if it's disabled
    public static boolean showAcronymMeaning(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_acronyms_key), false);
    }



    // Return false if the list of launches was never downloaded before
    public static boolean getLaunchesFirstLoad(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_first_load_launches_key), true);
    }

    // Set launches first load to true if it's the first time the list of launches is downloaded
    public static void setLaunchesFirstLoad(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_first_load_launches_key), status);
        editor.apply();
    }

    // Return false if the list of rockets was never downloaded before
    public static boolean getRocketsFirstLoad(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_first_load_rockets_key), true);
    }

    // Set rockets first load to true if it's the first time the list of rockets is downloaded
    public static void setRocketsFirstLoad(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_first_load_rockets_key), status);
        editor.apply();
    }

    // Return false if the list of launch pads was never downloaded before
    public static boolean getLaunchPadsFirstLoad(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_first_load_pads_key), true);
    }

    // Set launch pads first load to true if it's the first time the list of launch pads is downloaded
    public static void setLaunchPadsFirstLoad(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_first_load_pads_key), status);
        editor.apply();
    }
}
