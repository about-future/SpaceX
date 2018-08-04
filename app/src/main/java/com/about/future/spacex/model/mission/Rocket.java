package com.about.future.spacex.model.mission;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;

import com.google.gson.annotations.SerializedName;

public class Rocket {
    @ColumnInfo(name = "rocket_id")
    @SerializedName("rocket_id")
    private final String rocketId;
    @ColumnInfo(name = "rocket_name")
    @SerializedName("rocket_name")
    private final String rocketName;
    @ColumnInfo(name = "rocket_type")
    @SerializedName("rocket_type")
    private final String rocketType;
    //@ColumnInfo(name = "first_stage")
    @SerializedName("first_stage")
    @Embedded
    private final FirstStage firstStage;
    //@ColumnInfo(name = "second_stage")
    @SerializedName("second_stage")
    @Embedded
    private final SecondStage secondStage;

    public Rocket(String rocketId, String rocketName, String rocketType, FirstStage firstStage, SecondStage secondStage) {
        this.rocketId = rocketId;
        this.rocketName = rocketName;
        this.rocketType = rocketType;
        this.firstStage = firstStage;
        this.secondStage = secondStage;
    }

    public FirstStage getFirstStage() { return firstStage; }
    public SecondStage getSecondStage() { return secondStage; }
    public String getRocketId() { return rocketId; }
    public String getRocketName() { return rocketName; }
    public String getRocketType() { return rocketType; }
}
