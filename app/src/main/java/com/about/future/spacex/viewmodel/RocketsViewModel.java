package com.about.future.spacex.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.model.rocket.RocketMini;

import java.util.List;

public class RocketsViewModel extends AndroidViewModel {

    private LiveData<List<RocketMini>> rockets;

    public RocketsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase appDatabase = AppDatabase.getInstance(this.getApplication());
        rockets = appDatabase.rocketDao().loadAllRockets();
    }

    public LiveData<List<RocketMini>> getRockets() { return rockets; }
}
