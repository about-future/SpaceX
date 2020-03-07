package com.about.future.spacex.model.core;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "cores")
public class Core {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "core_serial")
    @SerializedName("core_serial")
    private final String coreSerial;

    @SerializedName("block")
    private final int block;

    @SerializedName("status")
    private final String status;

    @SerializedName("original_launch")
    @ColumnInfo(name = "original_launch")
    private final String originalLaunch;

    @SerializedName("original_launch_unix")
    @ColumnInfo(name = "original_launch_unix")
    private final String originalLaunchUnix;

    @TypeConverters(MissionsTypeConverter.class)
    @SerializedName("missions")
    private final List<Mission> missions;

    @SerializedName("reuse_count")
    @ColumnInfo(name = "reuse_count")
    private final int reuseCount;

    @SerializedName("rtls_attempts")
    @ColumnInfo(name = "rtls_attempts")
    private final int rtlsAttempts;

    @SerializedName("rtls_landings")
    @ColumnInfo(name = "rtls_landings")
    private final int rtlsLandings;

    @SerializedName("asds_attempts")
    @ColumnInfo(name = "asds_attempts")
    private final int asdsAttempts;

    @SerializedName("asds_landings")
    @ColumnInfo(name = "asds_landings")
    private final int asdsLandings;

    @SerializedName("water_landing")
    @ColumnInfo(name = "water_landing")
    private final boolean waterLanding;

    @SerializedName("details")
    private final String details;

    public Core(@NonNull String coreSerial, int block, String status, String originalLaunch,
                String originalLaunchUnix, List<Mission> missions, int reuseCount, int rtlsAttempts,
                int rtlsLandings, int asdsAttempts, int asdsLandings, boolean waterLanding, String details) {
        this.coreSerial = coreSerial;
        this.block = block;
        this.status = status;
        this.originalLaunch = originalLaunch;
        this.originalLaunchUnix = originalLaunchUnix;
        this.missions = missions;
        this.reuseCount = reuseCount;
        this.rtlsAttempts = rtlsAttempts;
        this.rtlsLandings = rtlsLandings;
        this.asdsAttempts = asdsAttempts;
        this.asdsLandings = asdsLandings;
        this.waterLanding = waterLanding;
        this.details = details;
    }

    public String getCoreSerial() { return coreSerial; }
    public int getBlock() { return block; }
    public String getStatus() { return status; }
    public String getOriginalLaunch() { return originalLaunch; }
    public String getOriginalLaunchUnix() { return originalLaunchUnix; }
    public List<Mission> getMissions() { return missions; }
    public int getReuseCount() { return reuseCount; }
    public int getRtlsAttempts() { return rtlsAttempts; }
    public int getRtlsLandings() { return rtlsLandings; }
    public int getAsdsAttempts() { return asdsAttempts; }
    public int getAsdsLandings() { return asdsLandings; }
    public boolean isWaterLanding() { return waterLanding; }
    public String getDetails() { return details; }
}
