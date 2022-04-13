package com.about.future.spacex.model.mission;


import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "missions")
public class Mission {
//      "fairings":{
//         "reused":true,
//         "recovery_attempt":true,
//         "recovered":true,
//         "ships":[
//            "60c8c7a45d4819007ea69871"
//         ]
//      },


//      "static_fire_date_utc":"2021-06-22T15:24:00.000Z",
//      "static_fire_date_unix":1624375440,
//      "net":false,
//      "window":0,


//      "failures":[],


//      "crew":[],


//      "ships":[
//         "60c8c7a45d4819007ea69871"
//      ],


//      "capsules":[
//      ],


//      "payloads":[
//         "608ac397eb3e50044e3630e7"
//      ],


//      "cores":[
//         {
//            "core":"5ef670f10059c33cee4a826c",
//            "flight":8,
//            "gridfins":true,
//            "legs":true,
//            "reused":true,
//            "landing_attempt":true,
//            "landing_success":true,
//            "landing_type":"RTLS",
//            "landpad":"5e9e3032383ecb267a34e7c7"
//         }
//      ],


//      "auto_update":true,
//      "tbd":false,
//      "launch_library_id":"5d248abe-17ef-43ce-9c04-aef33af40520",



    @PrimaryKey
    @SerializedName("flight_number")
    @ColumnInfo(name = "flight_number")
    private int flightNumber;

    @SerializedName("id")
    @ColumnInfo(name = "id")
    private String id;              // "id":"600f9b6d8f798e2a4d5f979f"

    @SerializedName("name")
    @ColumnInfo(name = "mission_name")
    private String missionName;



    @SerializedName("rocket")
    @ColumnInfo(name = "rocket")
    private String rocket;          // "rocket":"5e9d0d95eda69973a809d1ec"



    @ColumnInfo(name = "launch_date_unix")
    @SerializedName("date_unix")
    private long launchDateUnix;

    @ColumnInfo(name = "launch_date_utc")
    @SerializedName("date_utc")
    private String launchDateUtc;

    @ColumnInfo(name = "date_local")
    @SerializedName("date_local")
    private String launchDateLocal; // "date_local":"2021-06-30T15:31:00-04:00"

    @SerializedName("date_precision")
    @ColumnInfo(name = "date_precision")
    private String datePrecision;   // "date_precision":"hour"

    @SerializedName("launchpad")
    @ColumnInfo(name = "launchpad")
    private String launchPad;       // "launchpad":"5e9e4501f509094ba4566f84"

    @ColumnInfo(name = "launch_success")
    @SerializedName("success")
    private boolean launchSuccess;



    @SerializedName("links")
    @Embedded
    private Links links;

    @SerializedName("details")
    private String details;

    @SerializedName("upcoming")
    private boolean upcoming;


    public Mission(int flightNumber, String id, String missionName, String rocket,
                   long launchDateUnix, String launchDateUtc, String launchDateLocal, String datePrecision,
                   String launchPad, boolean launchSuccess, Links links, String details, boolean upcoming) {
        this.flightNumber = flightNumber;
        this.id = id;
        this.missionName = missionName;
        this.rocket = rocket;
        this.launchDateUnix = launchDateUnix;
        this.launchDateUtc = launchDateUtc;
        this.launchDateLocal = launchDateLocal;
        this.datePrecision = datePrecision;
        this.launchPad = launchPad;
        this.launchSuccess = launchSuccess;
        this.links = links;
        this.details = details;
        this.upcoming = upcoming;
    }

    @Ignore
    public Mission(String id, String missionName, String rocket,
                   long launchDateUnix, String launchDateUtc, String launchDateLocal, String datePrecision,
                   String launchPad, boolean launchSuccess, Links links, String details, boolean upcoming) {
        this.id = id;
        this.missionName = missionName;
        this.rocket = rocket;
        this.launchDateUnix = launchDateUnix;
        this.launchDateUtc = launchDateUtc;
        this.launchDateLocal = launchDateLocal;
        this.datePrecision = datePrecision;
        this.launchPad = launchPad;
        this.launchSuccess = launchSuccess;
        this.links = links;
        this.details = details;
        this.upcoming = upcoming;
    }

    @Ignore
    public Mission(){}

    public int getFlightNumber() { return flightNumber; }
    public String getMissionName() { return missionName; }
    public String getId() { return id; }

    public long getLaunchDateUnix() { return launchDateUnix; }
    public String getLaunchDateUtc() { return launchDateUtc; }
    public String getLaunchDateLocal() { return launchDateLocal; }

    public String getRocket() { return rocket; }

    public String getDatePrecision() { return datePrecision; }
    public String getLaunchPad() { return launchPad; }
    public boolean isLaunchSuccess() { return launchSuccess; }

    public Links getLinks() { return links; }
    public String getDetails() { return details; }
    public boolean isUpcoming() { return upcoming; }

    public void setLinks(Links links) { this.links = links; }
}
