package com.about.future.spacex.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.model.mission.MissionMini;
import com.about.future.spacex.retrofit.ApiManager;
import com.about.future.spacex.utils.ResultDisplay;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private static volatile Repository sInstance;
    private final MissionDao missionDao;
    private final RocketDao rocketDao;
    private final LaunchPadDao launchPadDao;

    private MutableLiveData<ResultDisplay<List<Mission>>> mMissionsObservable;

    private Repository(final Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        missionDao = appDatabase.missionDao();
        rocketDao = appDatabase.rocketDao();
        launchPadDao = appDatabase.launchPadDao();
    }

    public static Repository getInstance(final Application application) {
        if (sInstance == null) {
            synchronized (Repository.class) {
                if (sInstance == null) {
                    sInstance = new Repository(application);
                }
            }
        }
        return sInstance;
    }



    // Missions
    public LiveData<ResultDisplay<List<Mission>>> getMissionsFromServer() {
        mMissionsObservable = new MutableLiveData<>();
        mMissionsObservable.setValue(ResultDisplay.loading(Collections.emptyList()));

        ApiManager.getInstance().getMissions(new Callback<List<Mission>>() {
            @Override
            public void onResponse(Call<List<Mission>> call, Response<List<Mission>> response) {
//                if (response.raw().networkResponse() != null) {
//                    Log.d("CAR: getCarPlatesFr", "Received from Network");
//                } else if (response.raw().cacheResponse() != null && response.raw().networkResponse() == null) {
//                    Log.d("CAR: getCarPlatesFr", "Received from Cache");
//                }

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<Mission> missions = response.body();

                        if (missions.size() > 0) {
                            // Delete old missions from DB
                            deleteAllMissions();

                            addMissions(missions);
                        }

                        mMissionsObservable.setValue(ResultDisplay.success(missions));
                    }
                } else {
                    // Bad API token
                    mMissionsObservable.setValue(ResultDisplay.error(String.valueOf(response.code()), Collections.emptyList()));
                }
            }

            @Override
            public void onFailure(Call<List<Mission>> call, Throwable t) {
                // Http error
                mMissionsObservable.setValue(ResultDisplay.error(t.getMessage(), Collections.emptyList()));
            }
        });

        return mMissionsObservable;
    }

    private void deleteAllMissions() {
        AppExecutors.getInstance().diskIO().execute(() -> missionDao.deleteAllMissions());
    }

    private void addMissions(List<Mission> missions) {
        AppExecutors.getInstance().diskIO().execute(() -> missionDao.insertMissions(missions));
    }

    public LiveData<Mission> getMissionDetails(int id) { return missionDao.loadMissionDetails(id); }
    public LiveData<List<MissionMini>> getMissions() { return missionDao.loadAllMissions(); }
    public Mission getUpcommingMission(long now) { return missionDao.findUpcomingMission(now); }
}
