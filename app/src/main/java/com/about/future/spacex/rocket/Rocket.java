package com.about.future.spacex.rocket;

import com.about.future.spacex.mission.FirstStage;
import com.about.future.spacex.mission.SecondStage;
import com.google.gson.annotations.SerializedName;

public class Rocket {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("type")
    private String type;
    @SerializedName("active")
    private boolean active;
    @SerializedName("stages")
    private int stages;
    @SerializedName("boosters")
    private int boosters;
    @SerializedName("cost_per_launch")
    private int costPerLaunch;
    @SerializedName("success_rate_pct")
    private int successRatePct;
    @SerializedName("first_flight")
    private String firstFlight;
    @SerializedName("country")
    private String country;
    @SerializedName("company")
    private String company;


    @SerializedName("height")
    private Height height;
    @SerializedName("diameter")
    private Diameter diameter;
    @SerializedName("mass")
    private Mass mass;
    @SerializedName("payload_weights")
    private List<PayloadWeights> payloadWeights;
    @SerializedName("first_stage")
    private FirstStage firstStage;
    @SerializedName("second_stage")
    private SecondStage secondStage;
    @SerializedName("engines")
    private Engines engines;
    @SerializedName("landing_legs")
    private LandingLegs landingLegs;
    @SerializedName("description")
    private String description;



}
