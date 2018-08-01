package com.about.future.spacex.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.about.future.spacex.R;
import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.utils.DateUtils;
import com.about.future.spacex.view.MissionDetailsActivity;

import java.util.Date;

import static com.about.future.spacex.view.MissionsFragment.MISSION_NUMBER_KEY;
import static com.about.future.spacex.view.MissionsFragment.TOTAL_MISSIONS_KEY;

public class MissionAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Mission upcomingMission, int totalMissions) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mission_app_widget);

        //TODO: empty view

        if (upcomingMission != null) {
            // Set mission patch
            try {
                switch (upcomingMission.getRocket().getRocketName()) {
                    case "Falcon 9":
                        if (!TextUtils.isEmpty(upcomingMission.getRocket().getSecondStage().getPayloads().get(0).getPayloadType())
                                && TextUtils.equals(upcomingMission.getRocket().getSecondStage().getPayloads().get(0).getPayloadType(), "Satellite")) {
                            views.setImageViewResource(R.id.widget_mission_patch, R.drawable.default_patch_f9_small);
                        } else {
                            views.setImageViewResource(R.id.widget_mission_patch, R.drawable.default_patch_dragon_small);
                        }
                        break;
                    case "Falcon Heavy":
                        views.setImageViewResource(R.id.widget_mission_patch, R.drawable.default_patch_fh_small);
                        break;
                    case "BFR":
                        views.setImageViewResource(R.id.widget_mission_patch, R.drawable.default_patch_bfr_small);
                        break;
                    case "Big Falcon Rocket":
                        views.setImageViewResource(R.id.widget_mission_patch, R.drawable.default_patch_bfr_small);
                        break;
                    default:
                        views.setImageViewResource(R.id.widget_mission_patch, R.drawable.default_patch_f9_small);
                        break;
                }
            } catch (NullPointerException e) {
                views.setImageViewResource(R.id.widget_mission_patch, R.drawable.default_patch_dragon_small);
            }

            // Set Mission name
            views.setTextViewText(R.id.widget_mission_name, upcomingMission.getMissionName());

            // Set Mission launch date
            // Convert mission Date from seconds in milliseconds
            Date upcomingMissionDate = new Date(upcomingMission.getLaunchDateUnix() * 1000L);
            // Set formatted date
            views.setTextViewText(R.id.widget_launch_date, DateUtils.formatDate(upcomingMissionDate));

            // Set time left until launch in a textview
            views.setTextViewText(R.id.widget_time_left, DateUtils.formatTimeLeft(upcomingMission.getLaunchDateUnix()));

            // Create the intent and set extras
            Intent intent = new Intent(context, MissionDetailsActivity.class);
            intent.putExtra(MISSION_NUMBER_KEY, upcomingMission.getFlightNumber());
            intent.putExtra(TOTAL_MISSIONS_KEY, totalMissions);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_main_content, pendingIntent);

        } else {
            views.setImageViewResource(R.id.widget_mission_patch, R.drawable.spacex_landing_droneship);
            views.setTextViewText(R.id.widget_mission_name, context.getString(R.string.widget_label_no_mission_available));
            views.setViewVisibility(R.id.widget_launch_date, View.GONE);
            views.setTextViewText(R.id.widget_time_left, context.getString(R.string.widget_label_click_here_to_load));
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Start the intent service update widget action, the service will take care of updating the widgets UI
        UpdateIntentService.startActionUpdateMissionWidget(context);
    }

    public static void updateMissionWidgets(Context context, AppWidgetManager appWidgetManager,
                                            int[] appWidgetIds, Mission upcomingMission, int totalMissions) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, upcomingMission, totalMissions);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

