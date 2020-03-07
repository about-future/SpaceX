package com.about.future.spacex.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.about.future.spacex.data.Repository;
import com.about.future.spacex.model.core.Core;
import com.about.future.spacex.utils.ResultDisplay;

import java.util.List;

public class CoresViewModel extends AndroidViewModel {
    private final Repository repository;

    public CoresViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
    }

    public LiveData<ResultDisplay<List<Core>>> getCoresFromServer() { return repository.getCoresFromServer(); }
    public LiveData<List<Core>> getCoresFromDb() {
        return repository.getCores();
    }
    public LiveData<Core> getCoreDetails(String id) { return repository.getCoreDetails(id); }
    //public int getCoreId(String core) { return repository.getCoreId(core); }
}
