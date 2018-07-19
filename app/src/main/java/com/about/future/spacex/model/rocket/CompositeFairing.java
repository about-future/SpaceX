package com.about.future.spacex.model.rocket;

import com.google.gson.annotations.SerializedName;

public class CompositeFairing {
    @SerializedName("height")
    private Dimension height;
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
