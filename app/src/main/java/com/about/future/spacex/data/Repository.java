package com.about.future.spacex.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.about.future.spacex.model.launch_pad.LaunchPad;
import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.model.mission.MissionMini;
import com.about.future.spacex.model.rocket.Rocket;
import com.about.future.spacex.model.rocket.RocketMini;
import com.about.future.spacex.retrofit.ApiManager;
import com.about.future.spacex.utils.ResultDisplay;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private static volatile Repository sInstance;
    private final MissionsDao missionsDao;
    private final RocketsDao rocketsDao;
    private final LaunchPadsDao launchPadsDao;

    private MutableLiveData<ResultDisplay<List<Mission>>> mMissionsObservable;
    private MutableLiveData<ResultDisplay<List<Rocket>>> mRocketsObservable;
    private MutableLiveData<ResultDisplay<List<LaunchPad>>> mLaunchPadsObservable;

    private Repository(final Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        missionsDao = appDatabase.missionDao();
        rocketsDao = appDatabase.rocketDao();
        launchPadsDao = appDatabase.launchPadDao();
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
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<Mission> missions = response.body();

                        Log.v("MISSIONS", "ARE: " + missions.size());

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
        AppExecutors.getInstance().diskIO().execute(missionsDao::deleteAllMissions);
    }

    private void addMissions(List<Mission> missions) {
        AppExecutors.getInstance().diskIO().execute(() -> missionsDao.insertMissions(missions));
    }

    public LiveData<Mission> getMissionDetails(int id) { return missionsDao.loadMissionDetails(id); }
    public LiveData<List<Mission>> getMissions() { return missionsDao.loadMissions(); }
    public LiveData<List<MissionMini>> getMiniMissions() { return missionsDao.loadMiniMissions(); }
    public LiveData<List<MissionMini>> getUpcomingMissions(long now) { return missionsDao.loadUpcomingMiniMissions(now); }
    public LiveData<List<MissionMini>> getPastMissions(long now) { return missionsDao.loadPastMiniMissions(now); }


    // Rockets
    public LiveData<ResultDisplay<List<Rocket>>> getRocketsFromServer() {
        mRocketsObservable = new MutableLiveData<>();
        mRocketsObservable.setValue(ResultDisplay.loading(Collections.emptyList()));

        ApiManager.getInstance().getRockets(new Callback<List<Rocket>>() {
            @Override
            public void onResponse(Call<List<Rocket>> call, Response<List<Rocket>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<Rocket> rockets = response.body();

                        Log.v("ROCKETS", "ARE: " + rockets.size());
                        for (Rocket rocket : rockets) {
                            Log.v("ROCKET NAME", "IS: " + rocket.getRocketName());
                        }

                        if (rockets.size() > 0) {
                            deleteAllRockets();
                            addRockets(rockets);
                        }

                        mRocketsObservable.setValue(ResultDisplay.success(rockets));
                    }
                } else {
                    // Bad API token
                    mRocketsObservable.setValue(ResultDisplay.error(String.valueOf(response.code()), Collections.emptyList()));
                }
            }

            @Override
            public void onFailure(Call<List<Rocket>> call, Throwable t) {
                // Http error
                mRocketsObservable.setValue(ResultDisplay.error(t.getMessage(), Collections.emptyList()));
            }
        });

        return mRocketsObservable;
    }

    private void deleteAllRockets() {
        AppExecutors.getInstance().diskIO().execute(rocketsDao::deleteAllRockets);
    }

    private void addRockets(List<Rocket> rockets) {
        AppExecutors.getInstance().diskIO().execute(() -> rocketsDao.insertRockets(rockets));
    }

    public LiveData<Rocket> getRocketDetails(int id) { return rocketsDao.loadRocketDetails(id); }
    public LiveData<List<RocketMini>> getMiniRockets() { return rocketsDao.loadMiniRockets(); }
    public LiveData<List<Rocket>> getRockets() { return rocketsDao.loadRockets(); }
    public String getRocketId(String rocket) { return rocketsDao.getRocketId(rocket); }





    // Launch Pads
    public LiveData<ResultDisplay<List<LaunchPad>>> getLaunchPadsFromServer() {
        mLaunchPadsObservable = new MutableLiveData<>();
        mLaunchPadsObservable.setValue(ResultDisplay.loading(Collections.emptyList()));

        ApiManager.getInstance().getLaunchPads(new Callback<List<LaunchPad>>() {
            @Override
            public void onResponse(Call<List<LaunchPad>> call, Response<List<LaunchPad>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<LaunchPad> launchPads = response.body();

                        Log.v("LAUNCH PADS RESPONSE", "SUCCESSFUL");
                        Log.v("LAUNCH PADS", "ARE: " + launchPads.size());
                        for (LaunchPad launchPad : launchPads) {
                            Log.v("PAD NAME", "IS: " + launchPad.getFullName());
                        }

                        if (launchPads.size() > 0) {
                            deleteAllLaunchPads();
                            addLaunchPads(launchPads);
                        }

                        mLaunchPadsObservable.setValue(ResultDisplay.success(launchPads));
                    } else {
                        Log.v("LAUNCH PADS RESPONSE", "SUCCESSFUL, BUT EMPTY");
                    }
                } else {
                    // Bad API token
                    mLaunchPadsObservable.setValue(ResultDisplay.error(String.valueOf(response.code()), Collections.emptyList()));
                    Log.v("LAUNCH PADS RESPONSE", "SUCCESSFUL");
                }
            }

            @Override
            public void onFailure(Call<List<LaunchPad>> call, Throwable t) {
                // Http error
                mLaunchPadsObservable.setValue(ResultDisplay.error(t.getMessage(), Collections.emptyList()));
                Log.v("LAUNCH PADS RESPONSE", "FAILED");
            }
        });

        return mLaunchPadsObservable;
    }

    private void deleteAllLaunchPads() {
        AppExecutors.getInstance().diskIO().execute(launchPadsDao::deleteAllPads);
    }

    private void addLaunchPads(List<LaunchPad> launchPads) {
        AppExecutors.getInstance().diskIO().execute(() -> launchPadsDao.insertLaunchPads(launchPads));
    }

    public LiveData<LaunchPad> getLaunchPadDetails(int id) { return launchPadsDao.loadLaunchPadDetails(id); }
    public LiveData<List<LaunchPad>> getLaunchPads() { return launchPadsDao.loadAllLaunchPads(); }
    public int getLaunchPadId(String pad) { return launchPadsDao.getLaunchPadId(pad); }
}
