package com.android.future.spacex.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.android.future.spacex.entity.Mission;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<Mission>> missions;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase appDatabase = AppDatabase.getInstance(this.getApplication());
        missions = appDatabase.missionDao().loadAllMissions();
    }

    public LiveData<List<Mission>> getMissions() {
        return missions;
    }
}
