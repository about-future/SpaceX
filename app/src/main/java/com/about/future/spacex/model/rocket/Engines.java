package com.about.future.spacex.model.rocket;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;

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
    @ColumnInfo(name = "engine_loss_max")
    @SerializedName("engine_loss_max")
    private int engineLossMax;
    @ColumnInfo(name = "propellant_1")
    @SerializedName("propellant_1")
    private String propellant1;
    @ColumnInfo(name = "propellant_2")
    @SerializedName("propellant_2")
    private String propellant2;
    @Embedded(prefix = "thrust_sea_level_")
    @SerializedName("thrust_sea_level")
    private Thrust thrustSeaLevel;
    @Embedded(prefix = "thrust_vacuum_")
    @SerializedName("thrust_vacuum")
    private Thrust thrustVacuum;
    @ColumnInfo(name = "thrust_to_weight")
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

    public void setNumber(int number) { this.number = number; }
    public void setType(String type) { this.type = type; }
    public void setVersion(String version) { this.version = version; }
    public void setLayout(String layout) { this.layout = layout; }
    public void setEngineLossMax(int engineLossMax) { this.engineLossMax = engineLossMax; }
    public void setPropellant1(String propellant1) { this.propellant1 = propellant1; }
    public void setPropellant2(String propellant2) { this.propellant2 = propellant2; }
    public void setThrustSeaLevel(Thrust thrustSeaLevel) { this.thrustSeaLevel = thrustSeaLevel; }
    public void setThrustVacuum(Thrust thrustVacuum) { this.thrustVacuum = thrustVacuum; }
    public void setThrustToWeight(double thrustToWeight) { this.thrustToWeight = thrustToWeight; }
}
