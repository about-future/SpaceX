package com.about.future.spacex.data;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.about.future.spacex.model.core.Core;
import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.model.pads.LandingPad;
import com.about.future.spacex.model.pads.LaunchPad;
import com.about.future.spacex.model.rocket.Capsule;
import com.about.future.spacex.model.rocket.Rocket;

@Database(entities = {Mission.class, LaunchPad.class, Rocket.class, LandingPad.class, Core.class, Capsule.class}, version = 1, exportSchema = false)
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

    public abstract MissionsDao missionsDao();
    public abstract LaunchPadsDao launchPadsDao();
    public abstract RocketsDao rocketsDao();
    public abstract LandingPadsDao landingPadsDao();
    public abstract CoresDao coresDao();
    public abstract CapsulesDao capsulesDao();
}
