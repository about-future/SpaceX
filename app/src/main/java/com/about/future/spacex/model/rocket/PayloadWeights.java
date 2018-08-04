package com.about.future.spacex.model.rocket;

import com.google.gson.annotations.SerializedName;

public class PayloadWeights {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("kg")
    private int kg;
    @SerializedName("lb")
    private int lb;

    public PayloadWeights(String id, String name, int kg, int lb) {
        this.id = id;
        this.name = name;
        this.kg = kg;
        this.lb = lb;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getKg() { return kg; }
    public int getLb() { return lb; }
}
