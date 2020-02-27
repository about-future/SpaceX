package com.about.future.spacex.fcm;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.data.AppExecutors;
import com.about.future.spacex.data.MissionsDao;
import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.retrofit.ApiManager;
import com.about.future.spacex.utils.DateUtils;
import com.about.future.spacex.utils.SpaceXPreferences;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpaceXWorker extends Worker {
    private final MissionsDao missionsDao;

    public SpaceXWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        AppDatabase appDatabase = AppDatabase.getInstance(getApplicationContext());
        missionsDao = appDatabase.missionDao();
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

        //if (NetworkUtils.isConnected(getApplicationContext())) {
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

            return Result.success();
//        } else {
//            return Result.failure();
//        }
    }

    private void deleteAllMissions() {
        AppExecutors.getInstance().diskIO().execute(missionsDao::deleteAllMissions);
    }

    private void addMissions(List<Mission> missions) {
        AppExecutors.getInstance().diskIO().execute(() -> missionsDao.insertMissions(missions));
    }
}
