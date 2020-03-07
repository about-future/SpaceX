package com.about.future.spacex.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.model.mission.Mission;

import java.util.Date;

public class UpdateIntentService extends IntentService {
    public static final String ACTION_UPDATE_MISSION_WIDGET = "com.about.future.spacex.action.update_mission_widget";
    private AppDatabase mDb;

    public UpdateIntentService() {
        super("UpdateIntentService");
    }

    // Start this service to perform UpdateMissionWidget action with the given parameter
    public static void startActionUpdateMissionWidget(Context context) {
        Intent intent = new Intent(context, UpdateIntentService.class);
        intent.setAction(ACTION_UPDATE_MISSION_WIDGET);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_MISSION_WIDGET.equals(action)) {
                handleActionUpdateMissionWidget();
            }
        }
    }

    private void handleActionUpdateMissionWidget() {
        mDb = AppDatabase.getInstance(getApplicationContext());

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Mission upcomingMission = mDb.missionsDao().findUpcomingMission((new Date().getTime() / 1000));

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), MissionAppWidget.class));
                // Update all widgets
                MissionAppWidget.updateMissionWidgets(getApplicationContext(), appWidgetManager, appWidgetIds, upcomingMission);
            }
        });
    }
}
