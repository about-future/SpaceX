package com.about.future.spacex.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.about.future.spacex.data.Repository;
import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.model.mission.MissionMini;
import com.about.future.spacex.utils.ResultDisplay;

import java.util.List;

public class MissionsViewModel extends AndroidViewModel {
    private final Repository repository;

    public MissionsViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }

    public LiveData<ResultDisplay<List<Mission>>> getMissionsFromServer() { return repository.getMissionsFromServer(); }
    public LiveData<List<MissionMini>> getMissionsFromDb() {
        return repository.getMissions();
    }
    public LiveData<Mission> getMissionDetails(int id) { return repository.getMissionDetails(id); }
}
