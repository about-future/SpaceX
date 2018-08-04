package com.about.future.spacex.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.about.future.spacex.data.AppDatabase;

public class MissionViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mMissionNumber;

    public MissionViewModelFactory(AppDatabase database, int missionNumber) {
        mDb = database;
        mMissionNumber = missionNumber;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MissionViewModel(mDb, mMissionNumber);
    }
}
