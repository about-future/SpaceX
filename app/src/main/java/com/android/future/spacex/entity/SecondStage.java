package com.android.future.spacex.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SecondStage {
    @ColumnInfo(name = "block")
    @SerializedName("block")
    private int block;
    @SerializedName("payloads") //TODO
    @Ignore
    private List<Payload> payloads;

    public SecondStage(int block) {
        this.block = block;
    }

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
