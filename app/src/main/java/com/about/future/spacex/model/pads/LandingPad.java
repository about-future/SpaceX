package com.about.future.spacex.model.pads;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "landing_pads")
public class LandingPad {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pad_id")
    private int padId;

    @SerializedName("id")
    @ColumnInfo(name = "id")
    private final String id;

    @SerializedName("full_name")
    @ColumnInfo(name = "full_name")
    private final String fullName;

    @SerializedName("status")
    @ColumnInfo(name = "status")
    private final String status;

    @Embedded
    @SerializedName("location")
    private final Location location;

    @SerializedName("landing_type")
    @ColumnInfo(name = "landing_type")
    private final String landingType;

    @SerializedName("attempted_landings")
    @ColumnInfo(name = "attempted_landings")
    private final int attemptedLandings;

    @SerializedName("successful_landings")
    @ColumnInfo(name = "successful_landings")
    private final int successfulLandings;

    @SerializedName("wikipedia")
    @ColumnInfo(name = "wikipedia")
    private final String wikipedia;

    @SerializedName("details")
    @ColumnInfo(name = "details")
    private final String details;

    public LandingPad(String id, String fullName, String status, Location location, String landingType, int attemptedLandings, int successfulLandings, String wikipedia, String details) {
        this.id = id;
        this.fullName = fullName;
        this.status = status;
        this.location = location;
        this.landingType = landingType;
        this.attemptedLandings = attemptedLandings;
        this.successfulLandings = successfulLandings;
        this.wikipedia = wikipedia;
        this.details = details;
    }

    public void setPadId(int padId) { this.padId = padId; }
    public int getPadId() { return padId; }
    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getStatus() { return status; }
    public Location getLocation() { return location; }
    public String getLandingType() { return landingType; }
    public int getAttemptedLandings() { return attemptedLandings; }
    public int getSuccessfulLandings() { return successfulLandings; }
    public String getWikipedia() { return wikipedia; }
    public String getDetails() { return details; }
}