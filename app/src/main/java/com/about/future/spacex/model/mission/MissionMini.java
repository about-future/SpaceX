package com.about.future.spacex.model.mission;

import androidx.room.ColumnInfo;
import androidx.room.TypeConverters;

import java.util.List;

public class MissionMini {
    @ColumnInfo(name = "flight_number")
    private final int flightNumber;
    @ColumnInfo(name = "mission_name")
    private final String missionName;
    @ColumnInfo(name = "launch_date_unix")
    private final long launchDateUnix;
    @ColumnInfo(name = "mission_patch_small")
    private final String missionPatch;
    @ColumnInfo(name = "rocket_name")
    private final String rocketName;
    private final int block;
    @TypeConverters(PayloadTypeConverter.class)
    private final List<Payload> payloads;

    public MissionMini(int flightNumber, String missionName, long launchDateUnix, String missionPatch,
                       String rocketName, int block, List<Payload> payloads) {
        this.flightNumber = flightNumber;
        this.missionName = missionName;
        this.launchDateUnix = launchDateUnix;
        this.missionPatch = missionPatch;
        this.rocketName = rocketName;
        this.block = block;
        this.payloads = payloads;
    }

    public int getFlightNumber() { return flightNumber; }
    public String getMissionName() { return missionName; }
    public long getLaunchDateUnix() { return launchDateUnix; }
    public String getMissionPatch() { return missionPatch; }
    public String getRocketName() { return rocketName; }
    public int getBlock() { return block; }
    public List<Payload> getPayloads() { return payloads; }
}
