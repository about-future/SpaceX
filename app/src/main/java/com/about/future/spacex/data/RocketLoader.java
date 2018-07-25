package com.about.future.spacex.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.about.future.spacex.model.rocket.Rocket;
import com.about.future.spacex.retrofit.ApiClient;
import com.about.future.spacex.retrofit.ApiInterface;

import java.io.IOException;

import retrofit2.Call;

public class RocketLoader  extends AsyncTaskLoader<Rocket> {
    private Rocket cachedRocket;
    private final String rocketId;

    public RocketLoader(@NonNull Context context, String rocketId) {
        super(context);
        this.rocketId = rocketId;
    }

    @Override
    protected void onStartLoading() {
        if (cachedRocket == null)
            forceLoad();
    }

    @Nullable
    @Override
    public Rocket loadInBackground() {
        ApiInterface rocketApiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Rocket> call = rocketApiInterface.getRocket(rocketId);

        Rocket result = null;
        try {
            result = call.execute().body();
        } catch (IOException e) {
            Log.v("Rocket Loader", "Error: " + e.toString());
        }

        return result;
    }

    @Override
    public void deliverResult(Rocket rocket) {
        cachedRocket = rocket;
        super.deliverResult(rocket);
    }
}
