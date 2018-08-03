package com.about.future.spacex.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.about.future.spacex.model.launch_pad.LaunchPad;
import com.about.future.spacex.retrofit.ApiClient;
import com.about.future.spacex.retrofit.ApiInterface;

import java.io.IOException;

import retrofit2.Call;

public class LaunchPadLoader extends AsyncTaskLoader<LaunchPad> {
    private LaunchPad cachedLaunchPad;
    private final String launchPadId;

    public LaunchPadLoader(@NonNull Context context, String launchPadId) {
        super(context);
        this.launchPadId = launchPadId;
    }

    @Override
    protected void onStartLoading() {
        if (cachedLaunchPad == null)
            forceLoad();
    }

    @Nullable
    @Override
    public LaunchPad loadInBackground() {
        ApiInterface launchPadApiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<LaunchPad> call = launchPadApiInterface.getLaunchPad(launchPadId);

        LaunchPad result = null;
        try {
            result = call.execute().body();
        } catch (IOException e) {
            Log.v("Launch Pad Loader", "Error: " + e.toString());
        }

        return result;
    }

    @Override
    public void deliverResult(LaunchPad launchPad) {
        cachedLaunchPad = launchPad;
        super.deliverResult(launchPad);
    }
}
