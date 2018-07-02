package com.android.future.spacex.entity;

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

    public void setCapsule(boolean capsule) { this.capsule = capsule; }
    public boolean isCapsule() { return capsule; }

    public void setCore(boolean core) { this.core = core; }
    public boolean isCore() { return core; }

    public void setFairings(boolean fairings) { this.fairings = fairings; }
    public boolean isFairings() { return fairings; }

    public void setSideCore1(boolean sideCore1) { this.sideCore1 = sideCore1; }
    public boolean isSideCore1() { return sideCore1; }

    public void setSideCore2(boolean sideCore2) { this.sideCore2 = sideCore2; }
    public boolean isSideCore2() { return sideCore2; }
}
