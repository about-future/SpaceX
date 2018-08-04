package com.about.future.spacex.model.rocket;

import com.google.gson.annotations.SerializedName;

public class Dimension {
    @SerializedName("meters")
    private final double meters;
    @SerializedName("feet")
    private final double feet;

    public Dimension(double meters, double feet) {
        this.meters = meters;
        this.feet = feet;
    }

    public double getMeters() { return meters; }
    public double getFeet() { return feet; }
}
