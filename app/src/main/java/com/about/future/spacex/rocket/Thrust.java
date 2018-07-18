package com.about.future.spacex.rocket;

import com.google.gson.annotations.SerializedName;

public class Thrust {
    @SerializedName("kN")
    private int kN;
    @SerializedName("lbf")
    private int lbf;

    public Thrust(int kN, int lbf) {
        this.kN = kN;
        this.lbf = lbf;
    }

    public int getKN() { return kN; }
    public void setKN(int kN) { this.kN = kN; }

    public int getLbf() { return lbf; }
    public void setLbf(int lbf) { this.lbf = lbf; }
}
