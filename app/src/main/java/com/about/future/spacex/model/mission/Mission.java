package com.about.future.spacex.model.mission;


import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "missions")
public class Mission {
    @PrimaryKey
    @ColumnInfo(name = "flight_number")
    @SerializedName("flight_number")
    private int flightNumber;
    @ColumnInfo(name = "mission_name")
    @SerializedName("mission_name")
    private String missionName;
    @ColumnInfo(name = "launch_year")
    @SerializedName("launch_year")
    private String launchYear;
    @ColumnInfo(name = "launch_date_unix")
    @SerializedName("launch_date_unix")
    private long launchDateUnix;
    @ColumnInfo(name = "launch_date_utc")
    @SerializedName("launch_date_utc")
    private String launchDateUtc;
    @SerializedName("is_tentative")
    @ColumnInfo(name = "is_tentative")
    private boolean isTentative;
    @SerializedName("tentative_max_precision")
    @ColumnInfo(name = "tentative_max_precision")
    private String tentativeMaxPrecision;
    @SerializedName("rocket")
    @Embedded
    private Rocket rocket;
    @SerializedName("reuse")
    @Embedded
    private Reuse reuse;
    @SerializedName("launch_site")
    @Embedded
    private LaunchSite launchSite;
    @ColumnInfo(name = "launch_success")
    @SerializedName("launch_success")
    private boolean launchSuccess;
    @SerializedName("links")
    @Embedded
    private Links links;
    @SerializedName("details")
    private String details;
    @SerializedName("upcoming")
    private boolean upcoming;


    public Mission(int flightNumber, String missionName, String launchYear, long launchDateUnix,
                   String launchDateUtc, boolean isTentative, String tentativeMaxPrecision,
                   Rocket rocket, Reuse reuse, LaunchSite launchSite, boolean launchSuccess,
                   Links links, String details, boolean upcoming) {
        this.flightNumber = flightNumber;
        this.missionName = missionName;
        this.launchYear = launchYear;
        this.launchDateUnix = launchDateUnix;
        this.launchDateUtc = launchDateUtc;
        this.isTentative = isTentative;
        this.tentativeMaxPrecision = tentativeMaxPrecision;
        this.rocket = rocket;
        this.reuse = reuse;
        this.launchSite = launchSite;
        this.launchSuccess = launchSuccess;
        this.links = links;
        this.details = details;
        this.upcoming = upcoming;
    }

    @Ignore
    public Mission(String missionName, String launchYear, long launchDateUnix, String launchDateUtc,
                   Rocket rocket, boolean isTentative, String tentativeMaxPrecision, Reuse reuse,
                   LaunchSite launchSite, boolean launchSuccess, Links links, String details, boolean upcoming) {
        this.missionName = missionName;
        this.launchYear = launchYear;
        this.launchDateUnix = launchDateUnix;
        this.launchDateUtc = launchDateUtc;
        this.isTentative = isTentative;
        this.tentativeMaxPrecision = tentativeMaxPrecision;
        this.rocket = rocket;
        this.reuse = reuse;
        this.launchSite = launchSite;
        this.launchSuccess = launchSuccess;
        this.links = links;
        this.details = details;
    }

    @Ignore
    public Mission(){}

    public int getFlightNumber() { return flightNumber; }
    public String getMissionName() { return missionName; }
    public String getLaunchYear() { return launchYear; }
    public long getLaunchDateUnix() { return launchDateUnix; }
    public String getLaunchDateUtc() { return launchDateUtc; }
    public boolean isTentative() { return isTentative; }
    public String getTentativeMaxPrecision() { return tentativeMaxPrecision; }
    public Rocket getRocket() { return rocket; }
    public Reuse getReuse() { return reuse; }
    public LaunchSite getLaunchSite() { return launchSite; }
    public boolean isLaunchSuccess() { return launchSuccess; }
    public Links getLinks() { return links; }
    public String getDetails() { return details; }
    public boolean isUpcoming() { return upcoming; }

    public void setRocket(Rocket rocket) { this.rocket = rocket; }
    public void setLinks(Links links) { this.links = links; }
}
