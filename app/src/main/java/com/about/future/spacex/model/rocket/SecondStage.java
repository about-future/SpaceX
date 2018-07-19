package com.about.future.spacex.model.rocket;

import com.google.gson.annotations.SerializedName;

public class SecondStage {
    @SerializedName("engines")
    private int engines;
    @SerializedName("burn_time_sec")
    private int burnTimeSec;
    @SerializedName("thrust")
    private Thrust thrust;
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
