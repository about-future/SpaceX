package com.about.future.spacex.model.launch_pad;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "launch_pads")
public class LaunchPad {
    @PrimaryKey
    @SerializedName("padid")
    @ColumnInfo(name = "padid")
    private int padId;
    @SerializedName("id")
    private String id;
    @ColumnInfo(name = "full_name")
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("status")
    private String status;
    @Embedded
    @SerializedName("location")
    private Location location;
    @ColumnInfo(name = "vehicles_launched")
    @TypeConverters(VehiclesTypeConverter.class)
    @SerializedName("vehicles_launched")
    private String[] vehiclesLaunched;
    @SerializedName("details")
    private String details;

    public LaunchPad(int padId, String id, String fullName, String status,
                     Location location, String[] vehiclesLaunched, String details) {
        this.padId = padId;
        this.id = id;
        this.fullName = fullName;
        this.status = status;
        this.location = location;
        this.vehiclesLaunched = vehiclesLaunched;
        this.details = details;
    }

    @Ignore
    public LaunchPad(String id, String fullName, String status,
                     Location location, String[] vehiclesLaunched, String details) {
        this.id = id;
        this.fullName = fullName;
        this.status = status;
        this.location = location;
        this.vehiclesLaunched = vehiclesLaunched;
        this.details = details;
    }

    public int getPadId() { return padId; }
    public String getId() { return id; }
    public String getDetails() { return details; }
    public String getFullName() { return fullName; }
    public Location getLocation() { return location; }
    public String[] getVehiclesLaunched() { return vehiclesLaunched; }
    public String getStatus() { return status; }

    public void setPadId(int padId) { this.padId = padId; }
    public void setId(String id) { this.id = id; }
    public void setDetails(String details) { this.details = details; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setLocation(Location location) { this.location = location; }
    public void setVehiclesLaunched(String[] vehiclesLaunched) { this.vehiclesLaunched = vehiclesLaunched; }
    public void setStatus(String status) { this.status = status; }
}
