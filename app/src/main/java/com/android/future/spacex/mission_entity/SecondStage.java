package com.android.future.spacex.mission_entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SecondStage {
    @ColumnInfo(name = "block")
    @SerializedName("block")
    private int block;
    @SerializedName("payloads")
    @TypeConverters(PayloadTypeConverter.class)
    private List<Payload> payloads = null;

    public SecondStage() {}

    @Ignore
    public SecondStage(int block, List<Payload> payloads) {
        this.block = block;
        this.payloads = payloads;
    }

    public int getBlock() { return block; }
    public List<Payload> getPayloads() { return payloads; }

    public void setBlock(int block) { this.block = block; }
    public void setPayloads(List<Payload> payloads) { this.payloads = payloads; }
}
