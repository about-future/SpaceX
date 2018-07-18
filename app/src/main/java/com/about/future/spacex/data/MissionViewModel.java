package com.about.future.spacex.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.about.future.spacex.mission.Mission;

public class MissionViewModel extends ViewModel {

    private LiveData<Mission> missionLiveData;

    public LiveData<Mission> getMissionLiveData() { return missionLiveData; }

    public MissionViewModel(AppDatabase database, int missionNumber) {
        missionLiveData = database.missionDao().loadMissionDetails(missionNumber);
    }
}
