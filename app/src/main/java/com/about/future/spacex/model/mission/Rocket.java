package com.about.future.spacex.model.mission;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import com.google.gson.annotations.SerializedName;

public class Rocket {
    @SerializedName("id")
    @ColumnInfo(name = "rocket_id")
    private final String rocketId;

    @SerializedName("name")
    @ColumnInfo(name = "name")
    private final String rocketName;

    @SerializedName("type")
    @ColumnInfo(name = "type")
    private final String rocketType;







    @SerializedName("active")
    @ColumnInfo(name = "active")
    private final boolean active;

    @SerializedName("stages")
    @ColumnInfo(name = "stages")
    private final int numberOfStages;

    @SerializedName("boosters")
    @ColumnInfo(name = "boosters")
    private final int numberOfBoosters;

    @SerializedName("cost_per_launch")
    @ColumnInfo(name = "cost_per_launch")
    private final int costPerLaunch;

    @SerializedName("success_rate_pct")
    @ColumnInfo(name = "success_rate_pct")
    private final int successRatePct;

    @SerializedName("first_flight")
    @ColumnInfo(name = "first_flight")
    private final String dateOfFirstFlight;  //"2018-02-06"

    @SerializedName("country")
    @ColumnInfo(name = "country")
    private final String country;

    @SerializedName("company")
    @ColumnInfo(name = "company")
    private final String company;

    @SerializedName("wikipedia")
    @ColumnInfo(name = "wikipedia")
    private final String wikipedia;

    @SerializedName("description")
    @ColumnInfo(name = "description")
    private final String description;

















    //@ColumnInfo(name = "first_stage")
    @SerializedName("first_stage")
    @Embedded
    private final FirstStage firstStage;

    //@ColumnInfo(name = "second_stage")
    @SerializedName("second_stage")

    @Embedded
    private final SecondStage secondStage;

//    public Rocket(String rocketId, String rocketName, String rocketType, FirstStage firstStage, SecondStage secondStage) {
//        this.rocketId = rocketId;
//        this.rocketName = rocketName;
//        this.rocketType = rocketType;
//        this.firstStage = firstStage;
//        this.secondStage = secondStage;
//    }

    public Rocket(String rocketId, String rocketName, String rocketType, boolean active, int numberOfStages,
                  int numberOfBoosters, int costPerLaunch, int successRatePct, String dateOfFirstFlight,
                  String country, String company, String wikipedia, String description, FirstStage firstStage, SecondStage secondStage) {
        this.rocketId = rocketId;
        this.rocketName = rocketName;
        this.rocketType = rocketType;
        this.active = active;
        this.numberOfStages = numberOfStages;
        this.numberOfBoosters = numberOfBoosters;
        this.costPerLaunch = costPerLaunch;
        this.successRatePct = successRatePct;
        this.dateOfFirstFlight = dateOfFirstFlight;
        this.country = country;
        this.company = company;
        this.wikipedia = wikipedia;
        this.description = description;
        this.firstStage = firstStage;
        this.secondStage = secondStage;
    }

    public FirstStage getFirstStage() { return firstStage; }
    public SecondStage getSecondStage() { return secondStage; }
    public String getRocketId() { return rocketId; }
    public String getRocketName() { return rocketName; }
    public String getRocketType() { return rocketType; }

    public boolean isActive() {
        return active;
    }

    public int getNumberOfStages() {
        return numberOfStages;
    }

    public int getNumberOfBoosters() {
        return numberOfBoosters;
    }

    public int getCostPerLaunch() {
        return costPerLaunch;
    }

    public int getSuccessRatePct() {
        return successRatePct;
    }

    public String getDateOfFirstFlight() {
        return dateOfFirstFlight;
    }

    public String getCountry() {
        return country;
    }

    public String getCompany() {
        return company;
    }

    public String getWikipedia() {
        return wikipedia;
    }

    public String getDescription() {
        return description;
    }
}
