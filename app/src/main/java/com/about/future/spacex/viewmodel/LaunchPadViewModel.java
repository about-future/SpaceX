package com.about.future.spacex.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.model.launch_pad.LaunchPad;

public class LaunchPadViewModel extends ViewModel {

    private LiveData<LaunchPad> launchPadLiveData;

    public LiveData<LaunchPad> getLaunchPadLiveData() { return launchPadLiveData; }

    public LaunchPadViewModel(AppDatabase database, int launchPadId) {
        launchPadLiveData = database.launchPadDao().loadLaunchPadDetails(launchPadId);
    }
}
