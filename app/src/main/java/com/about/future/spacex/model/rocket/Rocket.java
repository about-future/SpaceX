package com.about.future.spacex.model.rocket;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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
    private Dimension height;
    @SerializedName("diameter")
    private Dimension diameter;
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

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setActive(boolean active) { this.active = active; }
    public void setStages(int stages) { this.stages = stages; }
    public void setBoosters(int boosters) { this.boosters = boosters; }
    public void setCostPerLaunch(int costPerLaunch) { this.costPerLaunch = costPerLaunch; }
    public void setSuccessRatePct(int successRatePct) { this.successRatePct = successRatePct; }
    public void setFirstFlight(String firstFlight) { this.firstFlight = firstFlight; }
    public void setCompany(String company) { this.company = company; }
    public void setCountry(String country) { this.country = country; }
    public void setHeight(Dimension height) { this.height = height; }
    public void setDiameter(Dimension diameter) { this.diameter = diameter; }
    public void setMass(Mass mass) { this.mass = mass; }
    public void setPayloadWeights(List<PayloadWeights> payloadWeights) { this.payloadWeights = payloadWeights; }
    public void setFirstStage(FirstStage firstStage) { this.firstStage = firstStage; }
    public void setSecondStage(SecondStage secondStage) { this.secondStage = secondStage; }
    public void setEngines(Engines engines) { this.engines = engines; }
    public void setLandingLegs(LandingLegs landingLegs) { this.landingLegs = landingLegs; }
    public void setDescription(String description) { this.description = description; }
}
