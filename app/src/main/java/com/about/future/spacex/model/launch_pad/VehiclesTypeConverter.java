package com.about.future.spacex.model.launch_pad;

import android.text.TextUtils;

import androidx.room.TypeConverter;

public class VehiclesTypeConverter {
    @TypeConverter
    public static String[] stringToArray(String data) {
        if (TextUtils.isEmpty(data))
            return new String[]{""};

        return TextUtils.split(data, ",");
    }

    @TypeConverter
    public static String arrayToString(String[] vehicles) {
        if (vehicles == null)
            return "";

        return TextUtils.join(",", vehicles);
    }
}
