package com.about.future.spacex.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.about.future.spacex.model.core.Core;
import com.about.future.spacex.model.pads.LandingPad;
import com.about.future.spacex.model.pads.LaunchPad;
import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.model.mission.MissionMini;
import com.about.future.spacex.model.rocket.Capsule;
import com.about.future.spacex.model.rocket.Rocket;
import com.about.future.spacex.model.rocket.RocketMini;
import com.about.future.spacex.retrofit.ApiManager;
import com.about.future.spacex.utils.ResultDisplay;

import java.util.ArrayList;
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
    private final LandingPadsDao landingPadsDao;
    private final CoresDao coresDao;
    private final CapsulesDao capsulesDao;

    private MutableLiveData<ResultDisplay<List<Mission>>> mMissionsObservable;
    private MutableLiveData<ResultDisplay<List<Rocket>>> mRocketsObservable;
    private MutableLiveData<ResultDisplay<List<LaunchPad>>> mLaunchPadsObservable;
    private MutableLiveData<ResultDisplay<List<LandingPad>>> mLandingPadsObservable;
    private MutableLiveData<ResultDisplay<List<Core>>> mCoresObservable;
    private MutableLiveData<ResultDisplay<List<Capsule>>> mCapsulesObservable;

    private Repository(final Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        missionsDao = appDatabase.missionsDao();
        rocketsDao = appDatabase.rocketsDao();
        launchPadsDao = appDatabase.launchPadsDao();
        landingPadsDao = appDatabase.landingPadsDao();
        coresDao = appDatabase.coresDao();
        capsulesDao = appDatabase.capsulesDao();
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
    public LiveData<List<MissionMini>> getMiniMissions(int[] flights) {
        return missionsDao.loadMiniMissions(flights);
    }




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

    public LiveData<Rocket> getRocketDetails(String id) { return rocketsDao.loadRocketDetails(id); }
    public LiveData<List<RocketMini>> getMiniRockets() { return rocketsDao.loadMiniRockets(); }
    public List<RocketMini> getMiniRocketsRaw() { return rocketsDao.loadMiniRocketsRaw(); }

    public LiveData<List<Rocket>> getRockets() { return rocketsDao.loadRockets(); }
    public String getRocketType(String rocketId) { return rocketsDao.getRocketType(rocketId); }




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
    public String getLaunchPadName(String padId) { return launchPadsDao.getLaunchPadName(padId); }




    // Landing Pads
    public LiveData<ResultDisplay<List<LandingPad>>> getLandingPadsFromServer() {
        mLandingPadsObservable = new MutableLiveData<>();
        mLandingPadsObservable.setValue(ResultDisplay.loading(Collections.emptyList()));

        ApiManager.getInstance().getLandingPads(new Callback<List<LandingPad>>() {
            @Override
            public void onResponse(Call<List<LandingPad>> call, Response<List<LandingPad>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<LandingPad> landingPads = response.body();

                        Log.v("LANDING PADS RESPONSE", "SUCCESSFUL");
                        Log.v("LANDING PADS", "ARE: " + landingPads.size());
                        for (LandingPad landingPad : landingPads) {
                            Log.v("PAD NAME", "IS: " + landingPad.getFullName());
                        }

                        if (landingPads.size() > 0) {
                            deleteAllLandingPads();
                            addLandingPads(landingPads);
                        }

                        mLandingPadsObservable.setValue(ResultDisplay.success(landingPads));
                    } else {
                        Log.v("LANDING PADS RESPONSE", "SUCCESSFUL, BUT EMPTY");
                    }
                } else {
                    // Bad API token
                    mLandingPadsObservable.setValue(ResultDisplay.error(String.valueOf(response.code()), Collections.emptyList()));
                    Log.v("LANDING PADS RESPONSE", "SUCCESSFUL");
                }
            }

            @Override
            public void onFailure(Call<List<LandingPad>> call, Throwable t) {
                // Http error
                mLaunchPadsObservable.setValue(ResultDisplay.error(t.getMessage(), Collections.emptyList()));
                Log.v("LANDING PADS RESPONSE", "FAILED");
            }
        });

        return mLandingPadsObservable;
    }

    private void deleteAllLandingPads() {
        AppExecutors.getInstance().diskIO().execute(landingPadsDao::deleteAllPads);
    }

    private void addLandingPads(List<LandingPad> landingPads) {
        AppExecutors.getInstance().diskIO().execute(() -> landingPadsDao.insertLandingPads(landingPads));
    }

    public LiveData<LandingPad> getLandingPadDetails(String id) { return landingPadsDao.loadLandingPadDetails(id); }
    public LiveData<List<LandingPad>> getLandingPads() { return landingPadsDao.loadAllLandingPads(); }
    public int getLandingPadId(String pad) { return landingPadsDao.getLandingPadId(pad); }




    // Cores
    public LiveData<ResultDisplay<List<Core>>> getCoresFromServer() {
        mCoresObservable = new MutableLiveData<>();
        mCoresObservable.setValue(ResultDisplay.loading(Collections.emptyList()));

        ApiManager.getInstance().getCores(new Callback<List<Core>>() {
            @Override
            public void onResponse(Call<List<Core>> call, Response<List<Core>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<Core> cores = response.body();

                        Log.v("CORES RESPONSE", "SUCCESSFUL");
                        Log.v("CORES", "ARE: " + cores.size());
                        for (Core core : cores) {
                            Log.v("CORE SERIAL", "IS: " + core.getCoreSerial());
                        }

                        if (cores.size() > 0) {
                            deleteAllCores();
                            addCores(cores);
                        }

                        mCoresObservable.setValue(ResultDisplay.success(cores));
                    } else {
                        Log.v("CORES RESPONSE", "SUCCESSFUL, BUT EMPTY");
                    }
                } else {
                    // Bad API token
                    mCoresObservable.setValue(ResultDisplay.error(String.valueOf(response.code()), Collections.emptyList()));
                    Log.v("CORES RESPONSE", "SUCCESSFUL");
                }
            }

            @Override
            public void onFailure(Call<List<Core>> call, Throwable t) {
                // Http error
                mLaunchPadsObservable.setValue(ResultDisplay.error(t.getMessage(), Collections.emptyList()));
                Log.v("CORES RESPONSE", "FAILED");
            }
        });

        return mCoresObservable;
    }

    private void deleteAllCores() {
        AppExecutors.getInstance().diskIO().execute(coresDao::deleteAllCors);
    }

    private void addCores(List<Core> cores) {
        AppExecutors.getInstance().diskIO().execute(() -> coresDao.insertCore(cores));
    }

    public LiveData<Core> getCoreDetails(String id) { return coresDao.loadCoreDetails(id); }
    public LiveData<List<Core>> getCores() { return coresDao.loadAllCores(); }




    // Capsules
    public LiveData<ResultDisplay<List<Capsule>>> getCapsulesFromServer() {
        mCapsulesObservable = new MutableLiveData<>();
        mCapsulesObservable.setValue(ResultDisplay.loading(Collections.emptyList()));

        ApiManager.getInstance().getCapsules(new Callback<List<Capsule>>() {
            @Override
            public void onResponse(Call<List<Capsule>> call, Response<List<Capsule>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<Capsule> capsules = response.body();

                        Log.v("CAPSULES RESPONSE", "SUCCESSFUL");
                        Log.v("CAPSULES", "ARE: " + capsules.size());
                        for (Capsule capsule : capsules) {
                            Log.v("CORE SERIAL", "IS: " + capsule.getCapsuleSerial());
                        }

                        if (capsules.size() > 0) {
                            deleteAllCapsules();
                            addCapsules(capsules);
                        }

                        mCapsulesObservable.setValue(ResultDisplay.success(capsules));
                    } else {
                        Log.v("CAPSULES RESPONSE", "SUCCESSFUL, BUT EMPTY");
                    }
                } else {
                    // Bad API token
                    mCapsulesObservable.setValue(ResultDisplay.error(String.valueOf(response.code()), Collections.emptyList()));
                    Log.v("CAPSULES RESPONSE", "SUCCESSFUL");
                }
            }

            @Override
            public void onFailure(Call<List<Capsule>> call, Throwable t) {
                // Http error
                mLaunchPadsObservable.setValue(ResultDisplay.error(t.getMessage(), Collections.emptyList()));
                Log.v("CAPSULES RESPONSE", "FAILED");
            }
        });

        return mCapsulesObservable;
    }

    private void deleteAllCapsules() {
        AppExecutors.getInstance().diskIO().execute(capsulesDao::deleteAllCapsules);
    }

    private void addCapsules(List<Capsule> capsules) {
        AppExecutors.getInstance().diskIO().execute(() -> capsulesDao.insertCapsule(capsules));
    }

    public LiveData<Capsule> getCapsuleDetails(String id) { return capsulesDao.loadCapsuleDetails(id); }
    public LiveData<List<Capsule>> getCapsules() { return capsulesDao.loadAllCapsules(); }
}
