package com.about.future.spacex.model.rocket;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "rockets")
public class Rocket {
    @PrimaryKey
    @SerializedName("rocketid")
    @ColumnInfo(name = "rocketid")
    private int rocketId;
    @SerializedName("id")
    private final String id;
    @SerializedName("name")
    private final String name;
    @SerializedName("type")
    private final String type;
    @SerializedName("active")
    private final boolean active;
    @SerializedName("stages")
    private final int stages;
    @SerializedName("boosters")
    private final int boosters;
    @ColumnInfo(name = "cost_per_launch")
    @SerializedName("cost_per_launch")
    private final int costPerLaunch;
    @ColumnInfo(name = "success_rate_pct")
    @SerializedName("success_rate_pct")
    private final int successRatePct;
    @ColumnInfo(name = "first_flight")
    @SerializedName("first_flight")
    private final String firstFlight;
    @SerializedName("country")
    private final String country;
    @SerializedName("company")
    private final String company;
    @Embedded(prefix = "height_")
    @SerializedName("height")
    private final Dimension height;
    @Embedded(prefix = "diameter_")
    @SerializedName("diameter")
    private final Dimension diameter;
    @Embedded
    @SerializedName("mass")
    private final Mass mass;
    @TypeConverters(PayloadWeightsTypeConverter.class)
    @SerializedName("payload_weights")
    private final List<PayloadWeights> payloadWeights;
    @Embedded(prefix = "first_stage_")
    @SerializedName("first_stage")
    private final FirstStage firstStage;
    @Embedded(prefix = "second_stage_")
    @SerializedName("second_stage")
    private final SecondStage secondStage;
    @Embedded(prefix = "engines_")
    @SerializedName("engines")
    private final Engines engines;
    @Embedded
    @SerializedName("landing_legs")
    private final LandingLegs landingLegs;
    @SerializedName("description")
    private final String description;

    public Rocket(int rocketId, String id, String name, String type, boolean active, int stages, int boosters,
                  int costPerLaunch, int successRatePct, String firstFlight, String country,
                  String company, Dimension height, Dimension diameter, Mass mass,
                  List<PayloadWeights> payloadWeights, FirstStage firstStage, SecondStage secondStage,
                  Engines engines, LandingLegs landingLegs, String description) {
        this.rocketId = rocketId;
        this.id = id;
        this.name = name;
        this.type = type;
        this.active = active;
        this.stages = stages;
        this.boosters = boosters;
        this.costPerLaunch = costPerLaunch;
        this.successRatePct = successRatePct;
        this.firstFlight = firstFlight;
        this.country = country;
        this.company = company;
        this.height = height;
        this.diameter = diameter;
        this.mass = mass;
        this.payloadWeights = payloadWeights;
        this.firstStage = firstStage;
        this.secondStage = secondStage;
        this.engines = engines;
        this.landingLegs = landingLegs;
        this.description = description;
    }

    @Ignore
    public Rocket(String id, String name, String type, boolean active, int stages, int boosters,
                  int costPerLaunch, int successRatePct, String firstFlight, String country,
                  String company, Dimension height, Dimension diameter, Mass mass,
                  List<PayloadWeights> payloadWeights, FirstStage firstStage, SecondStage secondStage,
                  Engines engines, LandingLegs landingLegs, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.active = active;
        this.stages = stages;
        this.boosters = boosters;
        this.costPerLaunch = costPerLaunch;
        this.successRatePct = successRatePct;
        this.firstFlight = firstFlight;
        this.country = country;
        this.company = company;
        this.height = height;
        this.diameter = diameter;
        this.mass = mass;
        this.payloadWeights = payloadWeights;
        this.firstStage = firstStage;
        this.secondStage = secondStage;
        this.engines = engines;
        this.landingLegs = landingLegs;
        this.description = description;
    }

    public int getRocketId() { return rocketId; }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public boolean isActive() { return active; }
    public int getStages() { return stages; }
    public int getBoosters() { return boosters; }
    public int getCostPerLaunch() { return costPerLaunch; }
    public int getSuccessRatePct() { return successRatePct; }
    public String getFirstFlight() { return firstFlight; }
    public String getCountry() { return country; }
    public String getCompany() { return company; }
    public Dimension getHeight() { return height; }
    public Dimension getDiameter() { return diameter; }
    public Mass getMass() { return mass; }
    public List<PayloadWeights> getPayloadWeights() { return payloadWeights; }
    public FirstStage getFirstStage() { return firstStage; }
    public SecondStage getSecondStage() { return secondStage; }
    public Engines getEngines() { return engines; }
    public LandingLegs getLandingLegs() { return landingLegs; }
    public String getDescription() { return description; }
}
