package com.about.future.spacex.model.rocket;

import android.text.TextUtils;

import androidx.room.TypeConverter;

public class FlickerTypeConverter {
    @TypeConverter
    public static String[] stringToArray(String data) {
        if (TextUtils.isEmpty(data))
            return new String[]{""};

        return TextUtils.split(data, ",");
    }

    @TypeConverter
    public static String arrayToString(String[] flickerImages) {
        if (flickerImages == null)
            return "";

        return TextUtils.join(",", flickerImages);
    }
}
