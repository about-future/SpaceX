package com.about.future.spacex.model.mission;

import androidx.room.Ignore;
import androidx.room.TypeConverters;

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
