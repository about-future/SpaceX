package com.about.future.spacex.model.pads;

import com.google.gson.annotations.SerializedName;

public class Location {
    @SerializedName("name")
    private final String name;
    @SerializedName("region")
    private final String region;
    @SerializedName("latitude")
    private final double latitude;
    @SerializedName("longitude")
    private final double longitude;

    public Location(String name, String region, double latitude, double longitude) {
        this.name = name;
        this.region = region;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() { return name; }
    public String getRegion() { return region; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
