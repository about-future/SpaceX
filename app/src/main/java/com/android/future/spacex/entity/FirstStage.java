package com.android.future.spacex.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FirstStage {
    @SerializedName("cores")
    @Ignore
    private List<Core> cores; //TODO

    public FirstStage() {}

    @Ignore
    public FirstStage(List<Core> cores) {
        this.cores = cores;
    }

    public List<Core> getCores() { return cores; }
    public void setCores(List<Core> cores) { this.cores = cores; }
}
