package com.about.future.spacex.model.rocket;

import android.arch.persistence.room.Embedded;

import com.google.gson.annotations.SerializedName;

public class CompositeFairing {
    @Embedded(prefix = "height")
    @SerializedName("height")
    private Dimension height;
    @Embedded(prefix = "diameter")
    @SerializedName("diameter")
    private Dimension diameter;

    public CompositeFairing(Dimension height, Dimension diameter) {
        this.height = height;
        this.diameter = diameter;
    }

    public Dimension getHeight() { return height; }
    public Dimension getDiameter() { return diameter; }

    public void setHeight(Dimension height) { this.height = height; }
    public void setDiameter(Dimension diameter) { this.diameter = diameter; }
}
