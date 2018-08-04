package com.about.future.spacex.model.launch_pad;

import com.google.gson.annotations.SerializedName;

public class Location {
    @SerializedName("name")
    private String name;
    @SerializedName("region")
    private String region;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;

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
