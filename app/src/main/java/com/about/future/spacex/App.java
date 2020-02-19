package com.about.future.spacex;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import static com.about.future.spacex.utils.Constants.CHANNEL_ID;

public class App extends Application {
    private static App appInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
        if (appInstance == null) appInstance = this;
    }

    public static App getInstance() {
        return appInstance;
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel launchNotificationsChannel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            launchNotificationsChannel.setDescription(getString(R.string.notification_channel_description));

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(launchNotificationsChannel);
            }
        }
    }
}
