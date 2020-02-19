package com.about.future.spacex.model.rocket;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import com.google.gson.annotations.SerializedName;

public class Payloads {
    @ColumnInfo(name = "option_1")
    @SerializedName("option_1")
    private final String option1;
    @ColumnInfo(name = "option_2")
    @SerializedName("option_2")
    private final String option2;
    @Embedded(prefix = "composite_fairing_")
    @SerializedName("composite_fairing")
    private final CompositeFairing compositeFairing;

    public Payloads(String option1, String option2, CompositeFairing compositeFairing) {
        this.option1 = option1;
        this.option2 = option2;
        this.compositeFairing = compositeFairing;
    }

    public String getOption1() { return option1; }
    public String getOption2() { return option2; }
    public CompositeFairing getCompositeFairing() { return compositeFairing; }
}