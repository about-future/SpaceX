package com.android.future.spacex.entity;

import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FirstStage {
    @SerializedName("cores")
    @TypeConverters(CoreTypeConverter.class)
    private List<Core> cores = null;

    public FirstStage() {}

    @Ignore
    public FirstStage(List<Core> cores) {
        this.cores = cores;
    }

    public List<Core> getCores() { return cores; }
    public void setCores(List<Core> cores) { this.cores = cores; }
}
