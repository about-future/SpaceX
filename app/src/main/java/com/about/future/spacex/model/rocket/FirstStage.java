package com.about.future.spacex.model.rocket;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;

import com.google.gson.annotations.SerializedName;

public class FirstStage {
    @SerializedName("reusable")
    private final boolean reusable;
    @SerializedName("engines")
    private final int engines;
    @ColumnInfo(name = "fuel_amount_tons")
    @SerializedName("fuel_amount_tons")
    private final double fuelAmountTons;
    @ColumnInfo(name = "burn_time_sec")
    @SerializedName("burn_time_sec")
    private final int burnTimeSec;
    @Embedded(prefix = "thrust_sea_level_")
    @SerializedName("thrust_sea_level")
    private final Thrust thrustSeaLevel;
    @Embedded(prefix = "thrust_vacuum_")
    @SerializedName("thrust_vacuum")
    private final Thrust thrustVacuum;

    public FirstStage(boolean reusable, int engines, double fuelAmountTons,
                      int burnTimeSec, Thrust thrustSeaLevel, Thrust thrustVacuum) {
        this.reusable = reusable;
        this.engines = engines;
        this.fuelAmountTons = fuelAmountTons;
        this.burnTimeSec = burnTimeSec;
        this.thrustSeaLevel = thrustSeaLevel;
        this.thrustVacuum = thrustVacuum;
    }

    public boolean isReusable() { return reusable; }
    public int getEngines() { return engines; }
    public double getFuelAmountTons() { return fuelAmountTons; }
    public int getBurnTimeSec() { return burnTimeSec; }
    public Thrust getThrustSeaLevel() { return thrustSeaLevel; }
    public Thrust getThrustVacuum() { return thrustVacuum; }
}
