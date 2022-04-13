package com.about.future.spacex.model.pads;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.about.future.spacex.model.StringArrayTypeConverter;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "launch_pads")
public class LaunchPad {
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    @ColumnInfo(name = "id")
    private String id;

    @SerializedName("name")
    @ColumnInfo(name = "name")
    private final String name;

    @ColumnInfo(name = "full_name")
    @SerializedName("full_name")
    private final String fullName;



    @SerializedName("images")
    @Embedded
    private final Images images;



    @SerializedName("latitude")
    private final double latitude;

    @SerializedName("longitude")
    private final double longitude;

    @SerializedName("locality")
    private final String locality;

    @SerializedName("region")
    private final String region;

    @SerializedName("timezone")
    private final String timezone;



    //@ColumnInfo(name = "rockets")
    @TypeConverters(StringArrayTypeConverter.class)
    @SerializedName("rockets")
    private final String[] rockets;

    @TypeConverters(StringArrayTypeConverter.class)
    @SerializedName("launches")
    private final String[] launches;

    @SerializedName("launch_attempts")
    @ColumnInfo(name = "attempted_launches")
    private int attemptedLaunches;

    @SerializedName("launch_successes")
    @ColumnInfo(name = "successful_launches")
    private int successfulLaunches;



    @SerializedName("status")
    private final String status;

    @SerializedName("details")
    private final String details;


    public LaunchPad(@NonNull String id, String name, String fullName, Images images,
                     double latitude, double longitude, String locality, String region, String timezone,
                     String[] rockets, String[] launches, int attemptedLaunches, int successfulLaunches,
                     String status, String details) {
        this.id = id;
        this.name = name;
        this.fullName = fullName;
        this.images = images;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locality = locality;
        this.region = region;
        this.timezone = timezone;
        this.rockets = rockets;
        this.launches = launches;
        this.attemptedLaunches = attemptedLaunches;
        this.successfulLaunches = successfulLaunches;
        this.status = status;
        this.details = details;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public Images getImages() {
        return images;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLocality() {
        return locality;
    }

    public String getRegion() {
        return region;
    }

    public String getTimezone() {
        return timezone;
    }

    public String[] getRockets() { return rockets; }

    public String[] getLaunches() {
        return launches;
    }

    public int getAttemptedLaunches() {
        return attemptedLaunches;
    }

    public void setAttemptedLaunches(int attemptedLaunches) {
        this.attemptedLaunches = attemptedLaunches;
    }

    public int getSuccessfulLaunches() {
        return successfulLaunches;
    }

    public void setSuccessfulLaunches(int successfulLaunches) {
        this.successfulLaunches = successfulLaunches;
    }

    public String getStatus() {
        return status;
    }

    public String getDetails() {
        return details;
    }
}
