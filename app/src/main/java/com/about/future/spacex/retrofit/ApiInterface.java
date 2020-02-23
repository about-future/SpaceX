package com.about.future.spacex.retrofit;

import com.about.future.spacex.model.mission.Mission;
import com.about.future.spacex.model.launch_pad.LaunchPad;
import com.about.future.spacex.model.rocket.Rocket;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {
    // Launches Endpoint
    @GET("launches")
    Call<List<Mission>> getMissions();

    @GET("launches/{id}")
    Call<List<Mission>> getMissionDetails(@Path("id") int number);

    /* Left here on purpose.
    @GET("launches/upcoming")
    Call<List<Mission>> getUpcomingMissions();

    @GET("launches/next")
    Call<Mission> getNextMission(); */


    // Launch Pads Endpoint
    @GET("launchpads")
    Call<List<LaunchPad>> getLaunchPads();

    @GET("launchpads/{id}")
    Call<LaunchPad> getLaunchPad(@Path("id") String id);


    // Rocket Data Endpoint
    @GET("rockets")
    Call<List<Rocket>> getRockets();

    @GET("rockets/{rocket_id}")
    Call<Rocket> getRocket(@Path("rocket_id") String rocketId);
}
