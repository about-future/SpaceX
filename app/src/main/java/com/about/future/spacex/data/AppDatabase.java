package com.about.future.spacex.data;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.model.launch_pad.LaunchPad;
import com.about.future.spacex.model.rocket.Rocket;

@Database(entities = {Mission.class, LaunchPad.class, Rocket.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "spacex";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract MissionsDao missionDao();
    public abstract LaunchPadsDao launchPadDao();
    public abstract RocketsDao rocketDao();
}
