package com.android.future.spacex.retrofit;

import com.android.future.spacex.data.Mission;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    // Launches Endpoint
    @GET("launches/all")
    Call<ArrayList<Mission>> getMissions();

    @GET("launches")
    Call<Mission> getMissionDetails(@Query("flight_number") int number);

    @GET("launches/upcoming")
    Call<ArrayList<Mission>> getUpcommingMissions();

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
