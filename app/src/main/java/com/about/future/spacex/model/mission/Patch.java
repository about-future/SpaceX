package com.about.future.spacex.model.mission;
import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class Patch {
    @SerializedName("small")
    @ColumnInfo(name = "small_patch")
    private final String small;

    @SerializedName("large")
    @ColumnInfo(name = "large_patch")
    private final String large;

    public Patch(String small, String large) {
        this.small = small;
        this.large = large;
    }

    public String getSmall() { return small; }
    public String getLarge() { return large; }
}
