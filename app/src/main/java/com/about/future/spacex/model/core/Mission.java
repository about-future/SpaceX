package com.about.future.spacex.model.core;

import com.google.gson.annotations.SerializedName;

public class Mission {
    @SerializedName("name")
    private String name;

    @SerializedName("flight")
    private String flight;

    public Mission(String name, String flight) {
        this.name = name;
        this.flight = flight;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFlight() { return flight; }
    public void setFlight(String flight) { this.flight = flight; }
}
