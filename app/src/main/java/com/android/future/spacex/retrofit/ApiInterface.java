package com.android.future.spacex.retrofit;

import com.android.future.spacex.entity.Mission;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    // Launches Endpoint
    @GET("launches/all")
    Call<List<Mission>> getMissions();

    @GET("launches/all")
    Call<Mission> getMissionDetails(@Query("flight_number") int number);

    @GET("launches/upcoming")
    Call<List<Mission>> getUpcomingMissions();

    @GET("launches/upcoming")
    Call<Mission> getUpcomingMissionDetails(@Query("flight_number") int number);

    @GET("launches/latest")
    Call<Mission> getLatestMission();

    @GET("launches/next")
    Call<Mission> getNextMission();

    // Launchpad data, can use GoogleMaps to show exact location of the Launch pad
    // Rocket Info
    // Capsule Detail
    // Detailed Capsule Data
    // Detailed Core Data
}
