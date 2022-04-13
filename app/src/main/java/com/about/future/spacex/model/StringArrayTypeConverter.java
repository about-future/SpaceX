package com.about.future.spacex.model;
import android.text.TextUtils;
import androidx.room.TypeConverter;

public class StringArrayTypeConverter {
    @TypeConverter
    public static String[] stringToList(String data) {
        if (data == null) return null;
        return data.split(",");
    }

    @TypeConverter
    public static String ListToString(String[] someObjects) {
        if (someObjects != null && someObjects.length > 0) {
            return TextUtils.join(",", someObjects);
        } else {
            return null;
        }
    }
}
