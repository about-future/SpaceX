package com.android.future.spacex.data;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.android.future.spacex.launch_pad_entity.LaunchPad;
import com.android.future.spacex.retrofit.ApiClient;
import com.android.future.spacex.retrofit.ApiInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class LaunchPadsLoader extends AsyncTaskLoader<List<LaunchPad>> {
    private List<LaunchPad> cachedPads;// = new ArrayList<>();

    public LaunchPadsLoader(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (cachedPads == null)
            forceLoad();
    }

    @Nullable
    @Override
    public List<LaunchPad> loadInBackground() {
        ApiInterface launchPadsApiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<LaunchPad>> call = launchPadsApiInterface.getLaunchPads();

        List<LaunchPad> result = new ArrayList<>();
        try {
            result = call.execute().body();
        } catch (IOException e) {
            Log.v("LaunchPads Loader", "Error: " + e.toString());
        }

        return result;
    }

    @Override
    public void deliverResult(List<LaunchPad> launchPads) {
        cachedPads = launchPads;
        super.deliverResult(launchPads);
    }
}
