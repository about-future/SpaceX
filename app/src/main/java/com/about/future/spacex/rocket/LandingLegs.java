package com.about.future.spacex.rocket;

import com.google.gson.annotations.SerializedName;

public class LandingLegs {
    @SerializedName("number")
    private int number;
    @SerializedName("number")
    private String material;

    public LandingLegs(int number, String material) {
        this.number = number;
        this.material = material;
    }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
}
