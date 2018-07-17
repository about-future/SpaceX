package com.android.future.spacex.launch_pad_entity;

import com.google.gson.annotations.SerializedName;

public class PadLocation {
    @SerializedName("name")
    private String name;
    @SerializedName("region")
    private String region;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;

    public PadLocation(String name, String region, double latitude, double longitude) {
        this.name = name;
        this.region = region;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}
