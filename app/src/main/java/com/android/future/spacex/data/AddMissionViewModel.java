package com.android.future.spacex.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.android.future.spacex.entity.Mission;

public class AddMissionViewModel extends ViewModel {

    private LiveData<Mission> missionLiveData;

    public LiveData<Mission> getMissionLiveData() { return missionLiveData; }

    public AddMissionViewModel(AppDatabase database, int missionNumber) {
        missionLiveData = database.missionDao().loadMissionDetails(missionNumber);
    }
}
