package com.about.future.spacex.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.about.future.spacex.R;
import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.model.mission.Links;
import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.utils.ImageUtils;
import com.about.future.spacex.view.MissionDetailsActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.about.future.spacex.view.MissionsFragment.MISSION_NUMBER_KEY;
import static com.about.future.spacex.view.MissionsFragment.TOTAL_MISSIONS_KEY;

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
                    Mission upcomingMission = mDb.missionDao().findUpcomingMission((new Date().getTime() / 1000));
                    int totalMissions = mDb.missionDao().countMissions();
                    int missionNumber = upcomingMission.getFlightNumber();

                    if (TextUtils.equals(remoteMessage.getFrom(), "/topics/updates")) {
                        // Update the upcoming mission's webcast link
                        String webcastLink = data.get("body");
                        //String webcastVideoKey = webcastLink.substring(webcastLink.indexOf("=") + 1, webcastLink.length());
                        //String webcastImagePreviewUrl = ImageUtils.buildSdVideoThumbnailUrl(webcastVideoKey);

                        // Update the upcoming mission's webcast link
                        Links newLinks = upcomingMission.getLinks();
                        newLinks.setVideoLink(webcastLink);
                        upcomingMission.setLinks(newLinks);
                        mDb.missionDao().updateMission(upcomingMission);

                        sendNotification(getString(R.string.live_webcast), totalMissions, missionNumber);
                    } else {
                        sendNotification(data.get("body"), totalMissions, missionNumber);
                    }
                }
            });
        }
    }

    private void sendNotification(String message, int totalMissions, int missionNumber) {
        String shortMessage;

        // If the message is longer than the max number of characters we want in our
        // notification, truncate it and add the unicode character for ellipsis
        if (message.length() > NOTIFICATION_MAX_CHARACTERS) {
            shortMessage = message.substring(0, NOTIFICATION_MAX_CHARACTERS) + "\u2026";
        } else {
            shortMessage = message;
        }

        Intent intent = new Intent(this, MissionDetailsActivity.class);
        intent.putExtra(MISSION_NUMBER_KEY, missionNumber);
        intent.putExtra(TOTAL_MISSIONS_KEY, totalMissions);
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
