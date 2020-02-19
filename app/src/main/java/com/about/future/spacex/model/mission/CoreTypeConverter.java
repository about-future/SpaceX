package com.about.future.spacex.model.mission;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class CoreTypeConverter {
    private static final Gson gson = new Gson();
    @TypeConverter
    public static List<Core> stringToList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Core>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String ListToString(List<Core> someObjects) {
        return gson.toJson(someObjects);
    }
}
