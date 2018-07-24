package com.about.future.spacex.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.about.future.spacex.R;
import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.model.mission.Links;
import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.utils.ImageUtils;
import com.about.future.spacex.utils.SpaceXPreferences;
import com.about.future.spacex.view.MissionDetailsActivity;
import com.about.future.spacex.viewmodel.MissionsViewModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.about.future.spacex.view.MissionsFragment.MISSION_NUMBER_KEY;
import static com.about.future.spacex.view.MissionsFragment.TOTAL_MISSIONS_KEY;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final int NOTIFICATION_MAX_CHARACTERS = 45;
    private static final String SPACEX_GROUP_KEY = "spacex_group";
    private static String LOG_TAG = MyFirebaseMessagingService.class.getSimpleName();

    private AppDatabase mDb;
    private List<Mission> mUpcomingMissions;
    private Mission mUpcomingMission;
    private int mMissionNumber;
    private int mTotalMissions;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        final Map<String, String> data = remoteMessage.getData();

        if (data.size() > 0) {
            mDb = AppDatabase.getInstance(getApplicationContext());

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    //TODO: small mess
                    mUpcomingMissions = mDb.missionDao().findUpcomingMission((new Date().getTime() / 1000));
                    mTotalMissions = mDb.missionDao().countMissions();

                    mMissionNumber = mUpcomingMissions.get(0).getFlightNumber();
                    mUpcomingMission = mUpcomingMissions.get(0);

                    //Log.v("TOTAL1", "IS: " + String.valueOf(mTotalMissions));
                    //Log.v("UPCOMING1", " IS: " + String.valueOf(mMissionNumber));

//                    if (mUpcomingMissions.size() > 0 && totalMissions > 0) {
//                        mUpcomingMission = upcomingMissions.get(0);
//                        mMissionNumber = upcomingMissions.get(0).getFlightNumber();
//                        mTotalMissions = totalMissions;
//                    }

                    sendNotification("SpaceX", data.get("body"), mTotalMissions, mMissionNumber);
                }
            });

            if (TextUtils.equals(remoteMessage.getFrom(), "/topics/news")) {
                // Send a notification with the new received message
                //Log.v("SEND", "NOTIFICATION");
                //sendNotification(data.get("title"), data.get("body"));
            } else {
                // Update the upcoming mission's webcast link
                String title = "SpaceX";
                final String webcastLink = data.get("body");
                String webcastVideoKey = webcastLink.substring(webcastLink.indexOf("=") + 1, webcastLink.length());
                String webcastImagePreview = ImageUtils.buildSdVideoThumbnailUrl(webcastLink);

                // Update the upcoming mission's webcast link
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        Links newLinks = new Links();
                        newLinks.setVideoLink(webcastLink);
                        mUpcomingMission.setLinks(newLinks);

                        mDb.missionDao().updateMission(mUpcomingMission);
                    }
                });

                //Log.v("SEND", "UPDATE");
                //sendNotification(title, webcastLink);
            }

            //Log.d(LOG_TAG, "Message data payload: " + data);
        }
    }

    private void sendNotification(String title, String message, int totalMissions, int missionNumber) {
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

    private void setUpdatedNotification(String title, String body) {

    }
}
