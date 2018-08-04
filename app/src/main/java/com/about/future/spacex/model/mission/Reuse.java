package com.about.future.spacex.model.mission;

import android.arch.persistence.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class Reuse {
    @SerializedName("core")
    private boolean core;
    @ColumnInfo(name = "side_core1")
    @SerializedName("side_core1")
    private boolean sideCore1;
    @ColumnInfo(name = "side_core2")
    @SerializedName("side_core2")
    private boolean sideCore2;
    @SerializedName("fairings")
    private boolean fairings;
    @SerializedName("capsule")
    private boolean capsule;

    public Reuse (boolean core, boolean sideCore1, boolean sideCore2, boolean fairings, boolean capsule) {
        this.core = core;
        this.sideCore1 = sideCore1;
        this.sideCore2 = sideCore2;
        this.fairings = fairings;
        this.capsule = capsule;
    }

    public boolean isCapsule() { return capsule; }
    public boolean isCore() { return core; }
    public boolean isFairings() { return fairings; }
    public boolean isSideCore1() { return sideCore1; }
    public boolean isSideCore2() { return sideCore2; }
}
