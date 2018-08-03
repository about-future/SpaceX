package com.about.future.spacex.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.about.future.spacex.R;
import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.model.mission.Links;
import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.view.MissionDetailsActivity;
import com.about.future.spacex.view.SpaceXActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;
import java.util.Map;

import static com.about.future.spacex.view.MissionsFragment.MISSION_NUMBER_KEY;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final int NOTIFICATION_MAX_CHARACTERS = 45;
    private static final String SPACEX_GROUP_KEY = "spacex_group";

    private AppDatabase mDb;

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        final Map<String, String> data = remoteMessage.getData();

        if (data.size() > 0) {
            mDb = AppDatabase.getInstance(getApplicationContext());

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Mission upcomingMission = mDb.missionDao().findUpcomingMission((new Date().getTime() / 1000));
                        int missionNumber = upcomingMission.getFlightNumber();

                        if (TextUtils.equals(remoteMessage.getFrom(), "/topics/updates")) {
                            // Update the upcoming mission's webcast link
                            Links newLinks = upcomingMission.getLinks();
                            newLinks.setVideoLink(data.get("body"));
                            upcomingMission.setLinks(newLinks);
                            mDb.missionDao().updateMission(upcomingMission);

                            // Send a notification containing a special message and the necessary
                            // data so the app can open the upcoming mission
                            sendNotification(getString(R.string.live_webcast), missionNumber);
                        } else {
                            // Otherwise, it's "/topics/news" case.
                            // Send a notification with the received message, add the necessary data
                            // so the app can open the upcoming mission
                            sendNotification(data.get("body"), missionNumber);
                        }
                    } catch (NullPointerException e) {
                        sendNotification(data.get("body"), 0);
                    }
                }
            });
        }
    }

    private void sendNotification(String message, int missionNumber) {
        String shortMessage;

        // If the message is longer than the max number of characters we want in our
        // notification, truncate it and add the unicode character for ellipsis
        if (message.length() > NOTIFICATION_MAX_CHARACTERS) {
            shortMessage = message.substring(0, NOTIFICATION_MAX_CHARACTERS) + "\u2026";
        } else {
            shortMessage = message;
        }

        Intent intent;
        // If both have value "0", it means we don't have data yet in our database, so any notification
        // that we get, should point to SpaceXActivity
        if (missionNumber == 0) {
            intent = new Intent(this, SpaceXActivity.class);
        } else {
            // Otherwise, we do have data in our DB and our intent should point to the correct mission
            intent = new Intent(this, MissionDetailsActivity.class);
            intent.putExtra(MISSION_NUMBER_KEY, missionNumber);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("SpaceX")
                        .setLights(0xFF005288, 2000, 5000)
                        .setContentText(shortMessage)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setGroup(SPACEX_GROUP_KEY)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }
}
