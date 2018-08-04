package com.about.future.spacex.model.rocket;

import com.google.gson.annotations.SerializedName;

public class LandingLegs {
    @SerializedName("number")
    private int number;
    @SerializedName("material")
    private String material;

    public LandingLegs(int number, String material) {
        this.number = number;
        this.material = material;
    }

    public int getNumber() { return number; }
    public String getMaterial() { return material; }
}
