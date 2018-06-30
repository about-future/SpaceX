package com.android.future.spacex.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "mission")
public class Mission {
    @PrimaryKey(autoGenerate = true)
    private int id;
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
//    @Embedded
//    private Rocket rocket;
    //private String telemetry;
//    @Embedded
// @ColumnInfo(name = "launch_site")
//    private LaunchSite launchSite;
    @ColumnInfo(name = "launch_success")
    @SerializedName("launch_success")
    private boolean launchSuccess;

//    @SerializedName("links")
//    @Embedded
//    private Links links;
    @SerializedName("details")
    private String details;

    public Mission(int id, int flightNumber, String missionName, String launchYear, long launchDateUnix,
                   String launchDateUtc, boolean launchSuccess, String details) { //Links links, String details) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.missionName = missionName;
        this.launchYear = launchYear;
        this.launchDateUnix = launchDateUnix;
        this.launchDateUtc = launchDateUtc;
        this.launchSuccess = launchSuccess;
        //this.links = links;
        this.details = details;
    }

    @Ignore
    public Mission(int flightNumber, String missionName, String launchYear, long launchDateUnix,
                   String launchDateUtc, boolean launchSuccess, String details) { //}, Links links, String details) {
        this.flightNumber = flightNumber;
        this.missionName = missionName;
        this.launchYear = launchYear;
        this.launchDateUnix = launchDateUnix;
        this.launchDateUtc = launchDateUtc;
        this.launchSuccess = launchSuccess;
        //this.links = links;
        this.details = details;
    }

    @Ignore
    public Mission(){}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFlightNumber() { return flightNumber; }
    public void setFlightNumber(int flightNumber) { this.flightNumber = flightNumber; }

    public String getMissionName() { return missionName; }
    public void setMissionName(String missionName) { this.missionName = missionName; }

    public String getLaunchYear() { return launchYear; }
    public void setLaunchYear(String launchYear) { this.launchYear = launchYear; }

    public long getLaunchDateUnix() { return launchDateUnix; }
    public void setLaunchDateUnix(long launchDateUnix) { this.launchDateUnix = launchDateUnix; }

    public String getLaunchDateUtc() { return launchDateUtc; }
    public void setLaunchDateUtc(String launchDateUtc) { this.launchDateUtc = launchDateUtc; }

    public boolean isLaunchSuccess() { return launchSuccess; }
    public void setLaunchSuccess(boolean launchSuccess) { this.launchSuccess = launchSuccess; }

//    public Links getLinks() { return links; }
//    public void setLinks(Links links) { this.links = links; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
