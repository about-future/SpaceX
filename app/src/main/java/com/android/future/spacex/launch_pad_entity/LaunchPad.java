package com.android.future.spacex.launch_pad_entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "launch_pads")
public class LaunchPad {
    @NonNull
    @PrimaryKey
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

    public LaunchPad(@NonNull String id, String fullName, String status, Location location, String[] vehiclesLaunched, String details) {
        this.id = id;
        this.fullName = fullName;
        this.status = status;
        this.location = location;
        this.vehiclesLaunched = vehiclesLaunched;
        this.details = details;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public String[] getVehiclesLaunched() { return vehiclesLaunched; }
    public void setVehiclesLaunched(String[] vehiclesLaunched) { this.vehiclesLaunched = vehiclesLaunched; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
