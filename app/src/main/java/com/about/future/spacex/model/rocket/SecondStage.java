package com.about.future.spacex.model.rocket;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;

import com.google.gson.annotations.SerializedName;

public class SecondStage {
    @SerializedName("engines")
    private int engines;
    @ColumnInfo(name = "burn_time_sec")
    @SerializedName("burn_time_sec")
    private int burnTimeSec;
    @Embedded(prefix = "thrust_")
    @SerializedName("thrust")
    private Thrust thrust;
    @Embedded(prefix = "payloads_")
    @SerializedName("payloads")
    private Payloads payloads;

    public SecondStage(int engines, int burnTimeSec, Thrust thrust, Payloads payloads) {
        this.engines = engines;
        this.burnTimeSec = burnTimeSec;
        this.thrust = thrust;
        this.payloads = payloads;
    }

    public int getEngines() { return engines; }
    public int getBurnTimeSec() { return burnTimeSec; }
    public Thrust getThrust() { return thrust; }
    public Payloads getPayloads() { return payloads; }

    public void setEngines(int engines) { this.engines = engines; }
    public void setBurnTimeSec(int burnTimeSec) { this.burnTimeSec = burnTimeSec; }
    public void setThrust(Thrust thrust) { this.thrust = thrust; }
    public void setPayloads(Payloads payloads) { this.payloads = payloads; }
}
