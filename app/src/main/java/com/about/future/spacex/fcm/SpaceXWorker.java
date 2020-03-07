package com.about.future.spacex.fcm;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.about.future.spacex.R;
import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.data.MissionsDao;
import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.retrofit.ApiManager;
import com.about.future.spacex.utils.DateUtils;
import com.about.future.spacex.utils.SpaceXPreferences;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.about.future.spacex.utils.Constants.CHANNEL_ID;

public class SpaceXWorker extends Worker {
    private final MissionsDao missionsDao;

    public SpaceXWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        AppDatabase appDatabase = AppDatabase.getInstance(getApplicationContext());
        missionsDao = appDatabase.missionsDao();
    }

    @NonNull
    @Override
    public Result doWork() {
        String date = SpaceXPreferences.getDownloadDate(getApplicationContext());
        String now = DateUtils.getFullDate(new Date().getTime());
        if (date.equals("")) {
            SpaceXPreferences.setDownloadDate(getApplicationContext(), now);
        } else {
            SpaceXPreferences.setDownloadDate(getApplicationContext(), date.concat("\n").concat(now));
        }

        ApiManager.getInstance().getMissions(new Callback<List<Mission>>() {
            @Override
            public void onResponse(Call<List<Mission>> call, Response<List<Mission>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<Mission> missions = response.body();

                        Log.v("WORKER MISSIONS", "ARE: " + missions.size());

                        if (missions.size() > 0) {
                            deleteAllMissions(); // Delete old missions from DB
                            addMissions(missions);
                        }
                    }
                } else {
                    // Bad API token
                    Log.v("WORKER MISSIONS", "ARE: NULL");
                }
            }

            @Override
            public void onFailure(Call<List<Mission>> call, Throwable t) {
                // Http error
                Log.v("WORKER MISSIONS", "FAILED");
            }
        });

        Log.v("SpaceX Worker", "is doing it's thing!");

        createNotification(getApplicationContext());

        Constraints constraints = new Constraints
                .Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                //.setRequiredNetworkType(NetworkType.UNMETERED)
                //.setRequiredNetworkType(NetworkType.NOT_ROAMING)
                .build();

        OneTimeWorkRequest downloadMissions = new OneTimeWorkRequest
                .Builder(SpaceXWorker.class)
                .setConstraints(constraints)
                .setInitialDelay(24, TimeUnit.HOURS)
                .build();
        WorkManager.getInstance(getApplicationContext()).enqueue(downloadMissions);

        return Result.success();
    }

    private void deleteAllMissions() {
        AppExecutors.getInstance().diskIO().execute(missionsDao::deleteAllMissions);
    }

    private void addMissions(List<Mission> missions) {
        AppExecutors.getInstance().diskIO().execute(() -> missionsDao.insertMissions(missions));
    }

    private static void createNotification(Context context) {
        String notificationTitle = "SpaceX";
        String notificationMessage = "Lista de lansari a fost actualizata.";
        int id = 1;

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_spacex_logo_xonly)  // TODO: Poate trebuie iconita sa fie cu alb!
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(id, notification);
    }
}
