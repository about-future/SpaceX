package com.about.future.spacex.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.model.launch_pad.LaunchPad;

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
