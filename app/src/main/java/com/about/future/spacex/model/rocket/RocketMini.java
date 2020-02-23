package com.about.future.spacex.model.rocket;

import androidx.room.ColumnInfo;

public class RocketMini {
    @ColumnInfo(name = "rocket_id")
    private final String rocketId;

    private final int id;

    @ColumnInfo(name = "rocket_name")
    private final String name;

    private final String description;

    public RocketMini(String rocketId, int id, String name, String description) {
        this.rocketId = rocketId;
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getRocketId() { return rocketId; }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}
