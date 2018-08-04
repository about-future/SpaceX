package com.about.future.spacex.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.model.mission.MissionMini;

import java.util.List;

public class MissionsViewModel extends AndroidViewModel {

    private final LiveData<List<MissionMini>> missions;

    public MissionsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase appDatabase = AppDatabase.getInstance(this.getApplication());
        missions = appDatabase.missionDao().loadAllMissions();
    }

    public LiveData<List<MissionMini>> getMissions() {
        return missions;
    }
}
