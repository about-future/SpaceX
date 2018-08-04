package com.about.future.spacex.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.about.future.spacex.data.AppDatabase;

public class RocketViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mRocketId;

    public RocketViewModelFactory(AppDatabase database, int rocketId) {
        mDb = database;
        mRocketId = rocketId;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new RocketViewModel(mDb, mRocketId);
    }
}
