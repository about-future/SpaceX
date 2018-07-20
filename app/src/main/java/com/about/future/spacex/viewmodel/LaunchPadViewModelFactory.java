package com.about.future.spacex.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.about.future.spacex.data.AppDatabase;

public class LaunchPadViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mLaunchPadId;

    public LaunchPadViewModelFactory(AppDatabase database, int launchPadId) {
        mDb = database;
        mLaunchPadId = launchPadId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new LaunchPadViewModel(mDb, mLaunchPadId);
    }
}
