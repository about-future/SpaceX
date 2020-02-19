package com.about.future.spacex.model.launch_pad;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "launch_pads")
public class LaunchPad {
    @PrimaryKey
    @SerializedName("padid")
    @ColumnInfo(name = "padid")
    private int padId;
    @SerializedName("id")
    private final String id;
    @ColumnInfo(name = "full_name")
    @SerializedName("full_name")
    private final String fullName;
    @SerializedName("status")
    private final String status;
    @Embedded
    @SerializedName("location")
    private final Location location;
    @ColumnInfo(name = "vehicles_launched")
    @TypeConverters(VehiclesTypeConverter.class)
    @SerializedName("vehicles_launched")
    private final String[] vehiclesLaunched;
    @SerializedName("details")
    private final String details;

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
}
