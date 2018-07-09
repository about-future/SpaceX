package com.android.future.spacex.entity;

import android.arch.persistence.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class Core {
    @ColumnInfo(name = "core_serial")
    @SerializedName("core_serial")
    private String coreSerial;
    @SerializedName("flight")
    private int flight;
    @SerializedName("block")
    private int block;
    @SerializedName("reused")
    private boolean reused;
    @ColumnInfo(name = "land_success")
    @SerializedName("land_success")
    private boolean landingSuccess;
    @ColumnInfo(name = "landing_type")
    @SerializedName("landing_type")
    private String landingType;
    @ColumnInfo(name = "landing_vehicle")
    @SerializedName("landing_vehicle")
    private String landingVehicle;

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

    public void setBlock(int block) { this.block = block; }
    public void setCoreSerial(String coreSerial) { this.coreSerial = coreSerial; }
    public void setFlight(int flight) { this.flight = flight; }
    public void setLandingType(String landingType) { this.landingType = landingType; }
    public void setLandingSuccess(boolean landingSuccess) { this.landingSuccess = landingSuccess; }
    public void setLandingVehicle(String landingVehicle) { this.landingVehicle = landingVehicle; }
    public void setReused(boolean reused) { this.reused = reused; }
}
