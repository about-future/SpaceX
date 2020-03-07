package com.about.future.spacex.model.core;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class MissionsTypeConverter {
    private static final Gson gson = new Gson();
    @TypeConverter
    public static List<Mission> stringToList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Mission>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String ListToString(List<Mission> someObjects) {
        return gson.toJson(someObjects);
    }
}
