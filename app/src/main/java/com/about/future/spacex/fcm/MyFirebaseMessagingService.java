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
import android.util.Log;

import com.about.future.spacex.R;
import com.about.future.spacex.view.MissionDetailsActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static com.about.future.spacex.view.MissionsFragment.MISSION_NUMBER_KEY;
import static com.about.future.spacex.view.MissionsFragment.TOTAL_MISSIONS_KEY;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final int NOTIFICATION_MAX_CHARACTERS = 45;
    private static final String SPACEX_GROUP_KEY = "spacex_group";
    private static String LOG_TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(LOG_TAG, "From: " + remoteMessage.getFrom());
        Map<String, String> data = remoteMessage.getData();

        if (data.size() > 0) {
            if (TextUtils.equals(remoteMessage.getFrom(), "/topics/news")) {
                // Send a notification with the new received message
                sendNotification(data);
            } else {
                // Update the upcoming mission's webcast link
                Log.v("UPDATE", "DATABASE NOW");
            }

            Log.d(LOG_TAG, "Message data payload: " + data);
        }
    }

    private void sendNotification(Map<String, String> data) {
        String title = data.get("title");
        String message = data.get("body");
        String shortMessage;

        // If the message is longer than the max number of characters we want in our
        // notification, truncate it and add the unicode character for ellipsis
        if (message.length() > NOTIFICATION_MAX_CHARACTERS) {
            shortMessage = message.substring(0, NOTIFICATION_MAX_CHARACTERS) + "\u2026";
        } else {
            shortMessage = message;
        }

        Intent intent = new Intent(this, MissionDetailsActivity.class);
        //intent.putExtra(MISSION_NUMBER_KEY, missionNumber);
        //intent.putExtra(TOTAL_MISSIONS_KEY, mTotalMissions);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(title)
                        .setContentText(shortMessage)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        // TODO: Test this: .setGroup(SPACEX_GROUP_KEY)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
