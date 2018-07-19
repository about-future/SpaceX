package com.about.future.spacex.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.about.future.spacex.data.AppDatabase;
import com.about.future.spacex.model.rocket.Rocket;

import java.util.List;

public class RocketsViewModel extends AndroidViewModel {

    private LiveData<List<Rocket>> rockets;

    public RocketsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase appDatabase = AppDatabase.getInstance(this.getApplication());
        rockets = appDatabase.rocketDao().loadAllRockets();
    }

    public LiveData<List<Rocket>> getRockets() { return rockets; }
}
