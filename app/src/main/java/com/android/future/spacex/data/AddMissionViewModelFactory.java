package com.android.future.spacex.data;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class AddMissionViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mMissionNumber;

    public AddMissionViewModelFactory(AppDatabase database, int missionNumber) {
        mDb = database;
        mMissionNumber = missionNumber;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new AddMissionViewModel(mDb, mMissionNumber);
    }
}
