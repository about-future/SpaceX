package com.about.future.spacex.model.rocket;

import com.google.gson.annotations.SerializedName;

public class FirstStage {
    @SerializedName("reusable")
    private boolean reusable;
    @SerializedName("engines")
    private int engines;
    @SerializedName("fuel_amount_tons")
    private double fuelAmountTons;
    @SerializedName("burn_time_sec")
    private int burnTimeSec;
    @SerializedName("thrust_sea_level")
    private Thrust thrustSeaLevel;
    @SerializedName("thrust_vacuum")
    private Thrust thrustVacuum;

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

    public void setReusable(boolean reusable) { this.reusable = reusable; }
    public void setEngines(int engines) { this.engines = engines; }
    public void setFuelAmountTons(double fuelAmountTons) { this.fuelAmountTons = fuelAmountTons; }
    public void setBurnTimeSec(int burnTimeSec) { this.burnTimeSec = burnTimeSec; }
    public void setThrustSeaLevel(Thrust thrustSeaLevel) { this.thrustSeaLevel = thrustSeaLevel; }
    public void setThrustVacuum(Thrust thrustVacuum) { this.thrustVacuum = thrustVacuum; }
}
