package com.about.future.spacex.model.mission;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MissionMini {
    @ColumnInfo(name = "flight_number")
    private final int flightNumber;

    @ColumnInfo(name = "mission_name")
    private final String missionName;

    @ColumnInfo(name = "launch_date_unix")
    private final long launchDateUnix;

    @ColumnInfo(name = "small_patch")
    private final String smallPatch;

    @ColumnInfo(name = "rocket")
    private final String rocketName;

    public MissionMini(int flightNumber, String missionName, long launchDateUnix, String smallPatch,
                       String rocketName) {
        this.flightNumber = flightNumber;
        this.missionName = missionName;
        this.launchDateUnix = launchDateUnix;
        this.smallPatch = smallPatch;
        this.rocketName = rocketName;
    }

    public int getFlightNumber() { return flightNumber; }
    public String getMissionName() { return missionName; }
    public long getLaunchDateUnix() { return launchDateUnix; }
    public String getSmallPatch() { return smallPatch; }
    public String getRocketName() { return rocketName; }
}
