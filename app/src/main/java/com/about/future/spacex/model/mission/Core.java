package com.about.future.spacex.model.mission;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class Core {
    @ColumnInfo(name = "core_serial")
    @SerializedName("core_serial")
    private final String coreSerial;
    @SerializedName("flight")
    private final int flight;
    @SerializedName("block")
    private final int block;
    @SerializedName("reused")
    private final boolean reused;
    @ColumnInfo(name = "land_success")
    @SerializedName("land_success")
    private final boolean landingSuccess;
    @ColumnInfo(name = "landing_type")
    @SerializedName("landing_type")
    private final String landingType;
    @ColumnInfo(name = "landing_vehicle")
    @SerializedName("landing_vehicle")
    private final String landingVehicle;

    public Core(String coreSerial, int flight, int block, boolean reused,
                boolean landingSuccess, String landingType, String landingVehicle) {
        this.block = block;
        this.coreSerial = coreSerial;
        this.flight = flight;
        this.landingSuccess = landingSuccess;
        this.landingType = landingType;
        this.landingVehicle = landingVehicle;
        this.reused = reused;
    }

    public int getBlock() { return block; }
    public String getCoreSerial() { return coreSerial; }
    public int getFlight() { return flight; }
    public String getLandingType() { return landingType; }
    public String getLandingVehicle() { return landingVehicle; }
    public boolean isLandingSuccess() { return landingSuccess; }
    public boolean isReused() { return reused; }
}
