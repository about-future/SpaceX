package com.about.future.spacex.model.rocket;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import com.google.gson.annotations.SerializedName;

public class Engines {
    @SerializedName("number")
    private final int number;
    @SerializedName("type")
    private final String type;
    @SerializedName("version")
    private final String version;
    @SerializedName("layout")
    private final String layout;
    @ColumnInfo(name = "engine_loss_max")
    @SerializedName("engine_loss_max")
    private final int engineLossMax;
    @ColumnInfo(name = "propellant_1")
    @SerializedName("propellant_1")
    private final String propellant1;
    @ColumnInfo(name = "propellant_2")
    @SerializedName("propellant_2")
    private final String propellant2;
    @Embedded(prefix = "thrust_sea_level_")
    @SerializedName("thrust_sea_level")
    private final Thrust thrustSeaLevel;
    @Embedded(prefix = "thrust_vacuum_")
    @SerializedName("thrust_vacuum")
    private final Thrust thrustVacuum;
    @ColumnInfo(name = "thrust_to_weight")
    @SerializedName("thrust_to_weight")
    private final double thrustToWeight;

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

    public int getNumber() { return number; }
    public String getType() { return type; }
    public String getVersion() { return version; }
    public String getLayout() { return layout; }
    public int getEngineLossMax() { return engineLossMax; }
    public String getPropellant1() { return propellant1; }
    public String getPropellant2() { return propellant2; }
    public Thrust getThrustSeaLevel() { return thrustSeaLevel; }
    public Thrust getThrustVacuum() { return thrustVacuum; }
    public double getThrustToWeight() { return thrustToWeight; }
}
