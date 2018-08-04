package com.about.future.spacex.model.rocket;

import com.google.gson.annotations.SerializedName;

public class Mass {
    @SerializedName("kg")
    private int kg;
    @SerializedName("lb")
    private int lb;

    public Mass(int kg, int lb) {
        this.kg = kg;
        this.lb = lb;
    }

    public int getKg() { return kg; }
    public int getLb() { return lb; }
}
