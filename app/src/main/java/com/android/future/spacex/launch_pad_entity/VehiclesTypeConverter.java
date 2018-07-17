package com.android.future.spacex.launch_pad_entity;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;

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
