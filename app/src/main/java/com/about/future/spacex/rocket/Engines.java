package com.about.future.spacex.rocket;

import com.google.gson.annotations.SerializedName;

public class Engines {
    @SerializedName("number")
    private int number;
    @SerializedName("type")
    private String type;
    @SerializedName("version")
    private String version;
    @SerializedName("layout")
    private String layout;
    @SerializedName("engine_loss_max")
    private int engineLossMax;
    @SerializedName("propellant_1")
    private String propellant1;
    @SerializedName("propellant_2")
    private String propellant2;
    @SerializedName("thrust_sea_level")
    private Thrust thrustSeaLevel;
    @SerializedName("thrust_vacuum")
    private Thrust thrustVacuum;
    @SerializedName("thrust_to_weight")
    private double thrustToWeight;

    public Engines(int number, String type, String version, String layout, int engineLossMax, String propellant1,
                   String propellant2, Thrust thrustSeaLevel, Thrust thrustVacuum, double thrustToWeight) {
        this.number = number;
        this.type = type;
        this.version = version;
        this.layout = layout;
        this.engineLossMax = engineLossMax;
        this.propellant1 = propellant1;
        this.propellant2 = propellant2;
        this.thrustSeaLevel = thrustSeaLevel;
        this.thrustVacuum = thrustVacuum;
        this.thrustToWeight = thrustToWeight;
    }


}
