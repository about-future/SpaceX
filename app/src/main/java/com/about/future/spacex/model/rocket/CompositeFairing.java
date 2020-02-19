package com.about.future.spacex.model.rocket;

import androidx.room.Embedded;

import com.google.gson.annotations.SerializedName;

public class CompositeFairing {
    @Embedded(prefix = "height_")
    @SerializedName("height")
    private final Dimension height;
    @Embedded(prefix = "diameter_")
    @SerializedName("diameter")
    private final Dimension diameter;

    public CompositeFairing(Dimension height, Dimension diameter) {
        this.height = height;
        this.diameter = diameter;
    }

    public Dimension getHeight() { return height; }
    public Dimension getDiameter() { return diameter; }
}
