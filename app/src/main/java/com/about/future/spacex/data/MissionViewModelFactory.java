package com.about.future.spacex.data;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

public class MissionViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mMissionNumber;

    public MissionViewModelFactory(AppDatabase database, int missionNumber) {
        mDb = database;
        mMissionNumber = missionNumber;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new MissionViewModel(mDb, mMissionNumber);
    }
}
