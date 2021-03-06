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




    // Return true if launchpads list was downloaded before or false if was never downloaded
    public static boolean getLaunchPadsStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_launch_pads_status_key), true);
    }

    // Set launch pads downloading status to true the first time the app is run and every time
    // the list of launch pads is refreshed
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




    // Return true if rockets list was downloaded before or false if was never downloaded
    public static boolean getRocketsStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_rockets_status_key), true);
    }

    // Set rockets downloading status to true the first time the app is run and every time
    // the list of rockets is refreshed
    public static void setRocketsStatus(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_rockets_status_key), status);
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




    // Set true if the user was subscribed to firebase topics
    public static void setTopicSubscriptionStatus(Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_topic_subscription_status_key), value);
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
    /*public static boolean showAcronymMeaning(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_acronyms_key), false);
    }*/




    // Return true if landing pads list was downloaded before or false if was never downloaded
    public static boolean getLandingPadsStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_landing_pads_status_key), true);
    }

    // Set landing pads downloading status to true the first time the app is run and every time
    // the list of landing pads is refreshed
    public static void setLandingPadsStatus(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_landing_pads_status_key), status);
        editor.apply();
    }

    // Return the date when landing pads thumbnails was first downloaded
    public static long getLandingPadsThumbnailsSavingDate(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(context.getString(R.string.pref_landing_pads_thumbnails_date_key), 1530000000);
    }

    // Set the date when thumbnails were downloaded
    public static void setLandingPadsThumbnailsSavingDate(Context context, long saveDate) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(context.getString(R.string.pref_landing_pads_thumbnails_date_key), saveDate);
        editor.apply();
    }

    // Return false if the list of landing pads was never downloaded before
    public static boolean getLandingPadsFirstLoad(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_first_load_landing_pads_key), true);
    }

    // Set landing pads first load to true if it's the first time the list of landing pads is downloaded
    public static void setLandingPadsFirstLoad(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_first_load_landing_pads_key), status);
        editor.apply();
    }



    public static String getDownloadDate(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(context.getString(R.string.pref_download_date_key), "");
    }

    public static void setDownloadDate(Context context, String date) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(context.getString(R.string.pref_download_date_key), date);
        editor.apply();
    }

    // Settings Preferences
    public static boolean getAcronymsStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_acronyms_key), false);
    }

    public static void setAcronymsStatus(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_acronyms_key), status);
        editor.apply();
    }

    public static String getUnits(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(context.getString(R.string.pref_units_key), "");
    }

    public static void setUnits(Context context, String selectedUnit) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(context.getString(R.string.pref_units_key), selectedUnit);
        editor.apply();
    }



    // Return true if cores list was downloaded before or false if was never downloaded
    public static boolean getCoresStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_cores_status_key), true);
    }

    // Set cores downloading status to true the first time the app is run and every time
    // the list of cores is refreshed
    public static void setCoresStatus(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_cores_status_key), status);
        editor.apply();
    }

    // Return false if the list of cores was never downloaded before
    public static boolean getCoresFirstLoad(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_first_load_cores_key), true);
    }

    // Set cores first load to true if it's the first time the list of cores is downloaded
    public static void setCoresFirstLoad(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_first_load_cores_key), status);
        editor.apply();
    }



    // Return true if capsules list was downloaded before or false if was never downloaded
    public static boolean getCapsulesStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_capsules_status_key), true);
    }

    // Set capsules downloading status to true the first time the app is run and every time
    // the list of capsules is refreshed
    public static void setCapsulesStatus(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_capsules_status_key), status);
        editor.apply();
    }

    // Return false if the list of capsules was never downloaded before
    public static boolean getCapsulesFirstLoad(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_first_load_capsules_key), true);
    }

    // Set capsules first load to true if it's the first time the list of capsules is downloaded
    public static void setCapsulesFirstLoad(Context context, boolean status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_first_load_capsules_key), status);
        editor.apply();
    }
}
