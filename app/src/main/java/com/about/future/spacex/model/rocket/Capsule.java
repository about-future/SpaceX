package com.about.future.spacex.model.rocket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.about.future.spacex.model.core.Mission;
import com.about.future.spacex.model.core.MissionsTypeConverter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "capsules")
public class Capsule {
    @PrimaryKey
    @ColumnInfo(name = "capsule_serial")
    @SerializedName("capsule_serial")
    private final String capsuleSerial;

    @ColumnInfo(name = "capsule_id")
    @SerializedName("capsule_id")
    private final String capsuleId;

    @SerializedName("status")
    private final String status;

    @Nullable
    @SerializedName("original_launch")
    @ColumnInfo(name = "original_launch")
    private final String originalLaunch;

    @Nullable
    @SerializedName("original_launch_unix")
    @ColumnInfo(name = "original_launch_unix")
    private final String originalLaunchUnix;

    @Nullable
    @TypeConverters(MissionsTypeConverter.class)
    @SerializedName("missions")
    private final List<Mission> missions;

    @SerializedName("landings")
    private final int landings;

    @SerializedName("type")
    private final String type;

    @Nullable
    @SerializedName("details")
    private final String details;

    @SerializedName("reuse_count")
    @ColumnInfo(name = "reuse_count")
    private final int reuseCount;

    public Capsule(@NonNull String capsuleSerial, String capsuleId, String status, @Nullable String originalLaunch,
                   @Nullable String originalLaunchUnix, @Nullable List<Mission> missions,
                   int landings, String type, @Nullable String details, int reuseCount) {
        this.capsuleSerial = capsuleSerial;
        this.capsuleId = capsuleId;
        this.status = status;
        this.originalLaunch = originalLaunch;
        this.originalLaunchUnix = originalLaunchUnix;
        this.missions = missions;
        this.landings = landings;
        this.type = type;
        this.details = details;
        this.reuseCount = reuseCount;
    }

    public String getCapsuleSerial() { return capsuleSerial; }
    public String getCapsuleId() { return capsuleId; }
    public String getStatus() { return status; }
    public String getOriginalLaunch() { return originalLaunch; }
    public String getOriginalLaunchUnix() { return originalLaunchUnix; }
    public List<Mission> getMissions() { return missions; }
    public int getLandings() { return landings; }
    public String getType() { return type; }
    public String getDetails() { return details; }
    public int getReuseCount() { return reuseCount; }
}
