package com.about.future.spacex.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.about.future.spacex.data.AppDatabase;

public class RocketViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final String mRocketId;

    public RocketViewModelFactory(AppDatabase database, String rocketId) {
        mDb = database;
        mRocketId = rocketId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new RocketViewModel(mDb, mRocketId);
    }
}
