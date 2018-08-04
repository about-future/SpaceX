package com.about.future.spacex.model.mission;

import android.arch.persistence.room.ColumnInfo;

public class MissionMini {
    @ColumnInfo(name = "flight_number")
    private int flightNumber;
    @ColumnInfo(name = "mission_name")
    private String missionName;
    @ColumnInfo(name = "launch_date_unix")
    private long launchDateUnix;
    @ColumnInfo(name = "mission_patch_small")
    private String missionPatch;
    @ColumnInfo(name = "rocket_name")
    private String rocketName;

    public MissionMini(int flightNumber, String missionName, long launchDateUnix, String missionPatch, String rocketName) {
        this.flightNumber = flightNumber;
        this.missionName = missionName;
        this.launchDateUnix = launchDateUnix;
        this.missionPatch = missionPatch;
        this.rocketName = rocketName;
    }

    public int getFlightNumber() { return flightNumber; }
    public String getMissionName() { return missionName; }
    public long getLaunchDateUnix() { return launchDateUnix; }
    public String getMissionPatch() { return missionPatch; }
    public String getRocketName() { return rocketName; }
}
