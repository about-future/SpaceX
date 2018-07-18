package com.android.future.spacex.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.android.future.spacex.launch_pad_entity.LaunchPad;

import java.util.List;

public class LaunchPadsViewModel extends AndroidViewModel{

    private LiveData<List<LaunchPad>> launchPads;

    public LaunchPadsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase appDatabase = AppDatabase.getInstance(this.getApplication());
        launchPads = appDatabase.launchPadDao().loadAllLaunchPads();
    }

    public LiveData<List<LaunchPad>> getLaunchPads() { return launchPads; }
}