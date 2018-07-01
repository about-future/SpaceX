package com.android.future.spacex.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.android.future.spacex.retrofit.ApiClient;
import com.android.future.spacex.retrofit.ApiInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class MissionsLoader extends AsyncTaskLoader<List<Mission>> {
    private List<Mission> cachedMissions;// = new ArrayList<>();

    public MissionsLoader(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (cachedMissions == null)
            forceLoad();
    }

    @Nullable
    @Override
    public List<Mission> loadInBackground() {
        ApiInterface missionApiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Mission>> call = missionApiInterface.getMissions();

        List<Mission> result = new ArrayList<>();
        try {
            result = call.execute().body();
        } catch (IOException e) {
            Log.v("Missions Loader", "Error: " + e.toString());
        }

        return result;
    }

    @Override
    public void deliverResult(List<Mission> missions) {
        cachedMissions = missions;
        super.deliverResult(missions);
    }
}

