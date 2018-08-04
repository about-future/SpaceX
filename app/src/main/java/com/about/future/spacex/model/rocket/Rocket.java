package com.about.future.spacex.model.rocket;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "rockets")
public class Rocket {
    @PrimaryKey
    @SerializedName("rocketid")
    @ColumnInfo(name = "rocketid")
    private int rocketId;
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
    @ColumnInfo(name = "cost_per_launch")
    @SerializedName("cost_per_launch")
    private int costPerLaunch;
    @ColumnInfo(name = "success_rate_pct")
    @SerializedName("success_rate_pct")
    private int successRatePct;
    @ColumnInfo(name = "first_flight")
    @SerializedName("first_flight")
    private String firstFlight;
    @SerializedName("country")
    private String country;
    @SerializedName("company")
    private String company;
    @Embedded(prefix = "height_")
    @SerializedName("height")
    private Dimension height;
    @Embedded(prefix = "diameter_")
    @SerializedName("diameter")
    private Dimension diameter;
    @Embedded
    @SerializedName("mass")
    private Mass mass;
    @TypeConverters(PayloadWeightsTypeConverter.class)
    @SerializedName("payload_weights")
    private List<PayloadWeights> payloadWeights;
    @Embedded(prefix = "first_stage_")
    @SerializedName("first_stage")
    private FirstStage firstStage;
    @Embedded(prefix = "second_stage_")
    @SerializedName("second_stage")
    private SecondStage secondStage;
    @Embedded(prefix = "engines_")
    @SerializedName("engines")
    private Engines engines;
    @Embedded
    @SerializedName("landing_legs")
    private LandingLegs landingLegs;
    @SerializedName("description")
    private String description;

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
