package com.about.future.spacex.model.rocket;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import com.google.gson.annotations.SerializedName;

public class SecondStage {
    @SerializedName("engines")
    private final int engines;
    @SerializedName("fuel_amount_tons")
    private final double fuelAmountTons;
    @ColumnInfo(name = "burn_time_sec")
    @SerializedName("burn_time_sec")
    private final int burnTimeSec;
    @Embedded(prefix = "thrust_")
    @SerializedName("thrust")
    private final Thrust thrust;
    @Embedded(prefix = "payloads_")
    @SerializedName("payloads")
    private final Payloads payloads;

    public SecondStage(int engines, double fuelAmountTons, int burnTimeSec, Thrust thrust, Payloads payloads) {
        this.engines = engines;
        this.fuelAmountTons = fuelAmountTons;
        this.burnTimeSec = burnTimeSec;
        this.thrust = thrust;
        this.payloads = payloads;
    }

    public int getEngines() { return engines; }
    public double getFuelAmountTons() { return fuelAmountTons; }
    public int getBurnTimeSec() { return burnTimeSec; }
    public Thrust getThrust() { return thrust; }
    public Payloads getPayloads() { return payloads; }
}
