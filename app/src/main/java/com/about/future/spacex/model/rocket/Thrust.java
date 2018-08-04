package com.about.future.spacex.model.rocket;

import com.google.gson.annotations.SerializedName;

public class Thrust {
    @SerializedName("kN")
    private final int kN;
    @SerializedName("lbf")
    private final int lbf;

    public Thrust(int kN, int lbf) {
        this.kN = kN;
        this.lbf = lbf;
    }

    public int getKN() { return kN; }
    public int getLbf() { return lbf; }
}
