package com.about.future.spacex.retrofit;

import com.about.future.spacex.App;
import com.about.future.spacex.model.pads.LandingPad;
import com.about.future.spacex.model.pads.LaunchPad;
import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.model.rocket.Rocket;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.about.future.spacex.utils.Constants.BASE_URL;

public class ApiManager {
    private static final String HEADER_CACHE_CONTROL = "Cache-Control";
    private static final String HEADER_PRAGMA = "Pragma";
    private static final long cacheSize = 5 * 1024 * 1024; // 5 MB

    private static ApiManager sApiManager;
    private static ApiInterface sApiInterface;
    private static ApiInterface sApiInterfaceForced;

    private ApiManager() {
        Retrofit retrofitForced = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        sApiInterfaceForced = retrofitForced.create(ApiInterface.class);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        sApiInterface = retrofit.create(ApiInterface.class);
    }

    private static OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .cache(cache())
                .addInterceptor(httpLoggingInterceptor())
                .addNetworkInterceptor(networkInterceptor())
                .build();
    }

    private static Cache cache() {
        return new Cache(new File(App.getInstance().getCacheDir(), "indentificator"), cacheSize);
    }

    private static HttpLoggingInterceptor httpLoggingInterceptor() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    private static Interceptor networkInterceptor() {
        return chain -> {
            Response response = chain.proceed(chain.request());
            CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(24, TimeUnit.HOURS)
                    .build();
            return response.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                    .build();
        };
    }

    public static ApiManager getInstance() {
        if (sApiManager == null) {
            sApiManager = new ApiManager();
        }
        return sApiManager;
    }



    public void getMissions(Callback<List<Mission>> callback) {
        Call<List<Mission>> response = sApiInterfaceForced.getMissions();
        response.enqueue(callback);
    }

    public void getMissionDetails(int number, Callback<List<Mission>> callback) {
        Call<List<Mission>> response = sApiInterfaceForced.getMissionDetails(number);
        response.enqueue(callback);
    }



    public void getRockets(Callback<List<Rocket>> callback) {
        Call<List<Rocket>> response = sApiInterfaceForced.getRockets();
        response.enqueue(callback);
    }

    public void getRocket(String rocketId, Callback<Rocket> callback) {
        Call<Rocket> response = sApiInterfaceForced.getRocket(rocketId);
        response.enqueue(callback);
    }



    public void getLaunchPads(Callback<List<LaunchPad>> callback) {
        Call<List<LaunchPad>> response = sApiInterfaceForced.getLaunchPads();
        response.enqueue(callback);
    }

    public void getLaunchPad(String padId, Callback<LaunchPad> callback) {
        Call<LaunchPad> response = sApiInterfaceForced.getLaunchPad(padId);
        response.enqueue(callback);
    }



    public void getLandingPads(Callback<List<LandingPad>> callback) {
        Call<List<LandingPad>> response = sApiInterfaceForced.getLandingPads();
        response.enqueue(callback);
    }

    public void getLandingPad(String padId, Callback<LandingPad> callback) {
        Call<LandingPad> response = sApiInterfaceForced.getLandingPad(padId);
        response.enqueue(callback);
    }



    //
}
