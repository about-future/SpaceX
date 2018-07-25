package com.about.future.spacex.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.model.rocket.Rocket;

public class RocketViewModel extends ViewModel {

    private LiveData<Rocket> rocketLiveData;

    public LiveData<Rocket> getRocketLiveData() { return rocketLiveData; }

    public RocketViewModel(AppDatabase database, int rocketId) {
        rocketLiveData = database.rocketDao().loadRocketDetails(rocketId);
    }
}
