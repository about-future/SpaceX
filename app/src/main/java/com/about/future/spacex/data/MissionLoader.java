package com.about.future.spacex.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.retrofit.ApiClient;
import com.about.future.spacex.retrofit.ApiInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class MissionLoader extends AsyncTaskLoader<List<Mission>> {
    private List<Mission> cachedMission;
    private final int missionNumber;

    public MissionLoader(@NonNull Context context, int missionNumber) {
        super(context);
        this.missionNumber = missionNumber;
    }

    @Override
    protected void onStartLoading() {
        if (cachedMission == null)
            forceLoad();
    }

    @Nullable
    @Override
    public List<Mission> loadInBackground() {
        ApiInterface missionApiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Mission>> call = missionApiInterface.getMissionDetails(missionNumber);

        List<Mission> result = new ArrayList<>();
        try {
            result = call.execute().body();
        } catch (IOException e) {
            Log.v("Mission Loader", "Error: " + e.toString());
        }

        return result;
    }

    @Override
    public void deliverResult(List<Mission> missions) {
        cachedMission = missions;
        super.deliverResult(missions);
    }
}
