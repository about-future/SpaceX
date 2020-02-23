package com.about.future.spacex.model.launch_pad;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "launch_pads")
public class LaunchPad {
    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "site_name")
    @SerializedName("name")
    private final String name;

    @SerializedName("status")
    private final String status;

    @Embedded
    @SerializedName("location")
    private final Location location;

    @ColumnInfo(name = "vehicles_launched")
    @TypeConverters(VehiclesTypeConverter.class)
    @SerializedName("vehicles_launched")
    private final String[] vehiclesLaunched;

    @SerializedName("attempted_launches")
    @ColumnInfo(name = "attempted_launches")
    private int attemptedLaunches;

    @SerializedName("successful_launches")
    @ColumnInfo(name = "successful_launches")
    private int successfulLaunches;

    @SerializedName("wikipedia")
    private String wikipedia;

    @SerializedName("details")
    private final String details;

    @ColumnInfo(name = "site_id")
    @SerializedName("site_id")
    private final String siteId;

    @ColumnInfo(name = "site_name_long")
    @SerializedName("site_name_long")
    private final String fullName;

    public LaunchPad(int id, String name, String status, Location location,
                     String[] vehiclesLaunched, int attemptedLaunches, int successfulLaunches,
                     String wikipedia, String details, String siteId, String fullName) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.location = location;
        this.vehiclesLaunched = vehiclesLaunched;
        this.attemptedLaunches = attemptedLaunches;
        this.successfulLaunches = successfulLaunches;
        this.wikipedia = wikipedia;
        this.details = details;
        this.siteId = siteId;
        this.fullName = fullName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public Location getLocation() { return location; }
    public String[] getVehiclesLaunched() { return vehiclesLaunched; }
    public int getAttemptedLaunches() { return attemptedLaunches; }
    public int getSuccessfulLaunches() { return successfulLaunches; }
    public String getWikipedia() { return wikipedia; }
    public String getDetails() { return details; }
    public String getSiteId() { return siteId; }
    public String getFullName() { return fullName; }
}
