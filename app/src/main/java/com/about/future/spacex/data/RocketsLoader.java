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
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class RocketsLoader extends AsyncTaskLoader<List<Rocket>> {
    private List<Rocket> cachedRockets;

    public RocketsLoader(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (cachedRockets == null)
            forceLoad();
    }

    @Nullable
    @Override
    public List<Rocket> loadInBackground() {
        ApiInterface rocketsApiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Rocket>> call = rocketsApiInterface.getRockets();

        List<Rocket> result = new ArrayList<>();
        try {
            result = call.execute().body();
        } catch (IOException e) {
            Log.v("Rockets Loader", "Error: " + e.toString());
        }

        return result;
    }

    @Override
    public void deliverResult(List<Rocket> rockets) {
        cachedRockets = rockets;
        super.deliverResult(rockets);
    }
}
