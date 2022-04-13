package com.about.future.spacex.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.about.future.spacex.data.Repository;
import com.about.future.spacex.model.rocket.Rocket;
import com.about.future.spacex.model.rocket.RocketMini;
import com.about.future.spacex.utils.ResultDisplay;

import java.util.List;

public class RocketsViewModel extends AndroidViewModel {
    private final Repository repository;

    public RocketsViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }

    public LiveData<ResultDisplay<List<Rocket>>> getRocketsFromServer() { return repository.getRocketsFromServer(); }
    public LiveData<List<RocketMini>> getMiniRocketsFromDb() { return repository.getMiniRockets(); }
    public List<RocketMini> getMiniRocketsRaw() { return repository.getMiniRocketsRaw(); }
    public LiveData<List<Rocket>> getRocketsFromDb() { return repository.getRockets(); }

    public LiveData<Rocket> getRocketDetails(String id) {
        return repository.getRocketDetails(id);
    }
    public String getRocketType(String rocketId) { return repository.getRocketType(rocketId); }
}
