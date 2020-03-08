package com.about.future.spacex.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.about.future.spacex.data.Repository;
import com.about.future.spacex.model.rocket.Capsule;
import com.about.future.spacex.utils.ResultDisplay;

import java.util.List;

public class CapsulesViewModel extends AndroidViewModel {
    private final Repository repository;

    public CapsulesViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }

    public LiveData<ResultDisplay<List<Capsule>>> getCapsulesFromServer() { return repository.getCapsulesFromServer(); }
    public LiveData<List<Capsule>> getCapsulesFromDb() {
        return repository.getCapsules();
    }
    public LiveData<Capsule> getCapsuleDetails(String id) { return repository.getCapsuleDetails(id); }
    //public int getCapsuleId(String capsule) { return repository.getCoreId(capsule); }
}
