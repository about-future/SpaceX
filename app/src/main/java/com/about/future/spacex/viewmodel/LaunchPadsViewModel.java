package com.about.future.spacex.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.about.future.spacex.data.Repository;
import com.about.future.spacex.model.pads.LaunchPad;
import com.about.future.spacex.utils.ResultDisplay;

import java.util.List;

public class LaunchPadsViewModel extends AndroidViewModel {
    private final Repository repository;

    public LaunchPadsViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }

    public LiveData<ResultDisplay<List<LaunchPad>>> getLaunchPadsFromServer() { return repository.getLaunchPadsFromServer(); }
    public LiveData<List<LaunchPad>> getLaunchPadsFromDb() {
        return repository.getLaunchPads();
    }
    public LiveData<LaunchPad> getLaunchPadDetails(int id) { return repository.getLaunchPadDetails(id); }
    public String getLaunchPadName(String padId) { return repository.getLaunchPadName(padId); }
}
