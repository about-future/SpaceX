package com.about.future.spacex.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.about.future.spacex.data.Repository;
import com.about.future.spacex.model.pads.LandingPad;
import com.about.future.spacex.utils.ResultDisplay;

import java.util.List;

public class LandingPadsViewModel extends AndroidViewModel {
    private final Repository repository;

    public LandingPadsViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }

    public LiveData<ResultDisplay<List<LandingPad>>> getLandingPadsFromServer() { return repository.getLandingPadsFromServer(); }
    public LiveData<List<LandingPad>> getLandingPadsFromDb() {
        return repository.getLandingPads();
    }
    public LiveData<LandingPad> getLandingPadDetails(String id) { return repository.getLandingPadDetails(id); }
    public int getLandingPadId(String pad) { return repository.getLandingPadId(pad); }
}
