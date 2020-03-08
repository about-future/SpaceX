package com.about.future.spacex.model.core;

import com.google.gson.annotations.SerializedName;

public class Mission {
    @SerializedName("name")
    private String name;

    @SerializedName("flight")
    private int flight;

    public Mission(String name, int flight) {
        this.name = name;
        this.flight = flight;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getFlight() { return flight; }
    public void setFlight(int flight) { this.flight = flight; }
}
