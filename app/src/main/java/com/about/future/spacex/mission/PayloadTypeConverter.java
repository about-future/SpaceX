package com.about.future.spacex.mission;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class PayloadTypeConverter {
    private static Gson gson = new Gson();
    @TypeConverter
    public static List<Payload> stringToList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Payload>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String ListToString(List<Payload> someObjects) {
        return gson.toJson(someObjects);
    }
}
