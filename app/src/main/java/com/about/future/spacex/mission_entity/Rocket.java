package com.about.future.spacex.mission_entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;

import com.google.gson.annotations.SerializedName;

public class Rocket {
    @ColumnInfo(name = "rocket_id")
    @SerializedName("rocket_id")
    private String rocketId;
    @ColumnInfo(name = "rocket_name")
    @SerializedName("rocket_name")
    private String rocketName;
    @ColumnInfo(name = "rocket_type")
    @SerializedName("rocket_type")
    private String rocketType;
    //@ColumnInfo(name = "first_stage")
    @SerializedName("first_stage")
    @Embedded
    private FirstStage firstStage;
    //@ColumnInfo(name = "second_stage")
    @SerializedName("second_stage")
    @Embedded
    private SecondStage secondStage;

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

    public void setFirstStage(FirstStage firstStage) { this.firstStage = firstStage; }
    public void setSecondStage(SecondStage secondStage) { this.secondStage = secondStage; }
    public void setRocketId(String rocketId) { this.rocketId = rocketId; }
    public void setRocketName(String rocketName) { this.rocketName = rocketName; }
    public void setRocketType(String rocketType) { this.rocketType = rocketType; }
}
