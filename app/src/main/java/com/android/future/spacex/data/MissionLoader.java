package com.android.future.spacex.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.android.future.spacex.retrofit.ApiClient;
import com.android.future.spacex.retrofit.ApiInterface;

import java.io.IOException;

import retrofit2.Call;

public class MissionLoader extends AsyncTaskLoader<Mission> {
    private Mission cachedMission;
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
    public Mission loadInBackground() {
        ApiInterface missionApiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Mission> call = missionApiInterface.getMissionDetails(missionNumber);

        Mission result = new Mission();
        try {
            result = call.execute().body();
        } catch (IOException e) {
            Log.v("Mission Loader", "Error: " + e.toString());
        }

        return result;
    }

    @Override
    public void deliverResult(Mission mission) {
        cachedMission = mission;
        super.deliverResult(mission);
    }
}
