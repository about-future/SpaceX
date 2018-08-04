package com.about.future.spacex.model.mission;

import android.arch.persistence.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class Payload {
    @ColumnInfo(name = "payload_id")
    @SerializedName("payload_id")
    private String payloadId;
    @SerializedName("reused")
    private boolean reused;
    @SerializedName("customers")
    private String[] customers;
    @ColumnInfo(name = "payload_type")
    @SerializedName("payload_type")
    private String payloadType;
    @ColumnInfo(name = "payload_mass_kg")
    @SerializedName("payload_mass_kg")
    private double payloadMassKg;
    @ColumnInfo(name = "payload_mass_lbs")
    @SerializedName("payload_mass_lbs")
    private double payloadMassLbs;
    @SerializedName("orbit")
    private String orbit;
    @ColumnInfo(name = "orbit_params")
    @SerializedName("orbit_params")
    private OrbitParams orbitParams;

    public Payload(String payloadId, boolean reused, String[] customers, String payloadType,
                   double payloadMassKg, double payloadMassLbs, String orbit, OrbitParams orbitParams) {
        this.payloadId = payloadId;
        this.reused = reused;
        this.customers = customers;
        this.payloadType = payloadType;
        this.payloadMassKg = payloadMassKg;
        this.payloadMassLbs = payloadMassLbs;
        this.orbit = orbit;
        this.orbitParams = orbitParams;
    }

    public String[] getCustomers() { return customers; }
    public String getOrbit() { return orbit; }
    public OrbitParams getOrbitParams() { return orbitParams; }
    public String getPayloadId() { return payloadId; }
    public double getPayloadMassKg() { return payloadMassKg; }
    public double getPayloadMassLbs() { return payloadMassLbs; }
    public String getPayloadType() { return payloadType; }
    public boolean isReused() { return reused; }
}
