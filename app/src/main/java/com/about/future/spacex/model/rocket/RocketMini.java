package com.about.future.spacex.model.rocket;


import android.arch.persistence.room.ColumnInfo;

public class RocketMini {
    @ColumnInfo(name = "rocketid")
    private int rocketId;
    private String id;
    private String name;
    private String description;

    public RocketMini(int rocketId, String id, String name, String description) {
        this.rocketId = rocketId;
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getRocketId() { return rocketId; }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}
