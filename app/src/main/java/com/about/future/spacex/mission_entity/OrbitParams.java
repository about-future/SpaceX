package com.about.future.spacex.mission_entity;

import android.arch.persistence.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class OrbitParams {
    @ColumnInfo(name = "reference_system")
    @SerializedName("reference_system")
    private String referenceSystem;
    @SerializedName("regime")
    private String regime;
    @SerializedName("longitude")
    private double longitude;
    @ColumnInfo(name = "semi_major_axis_km")
    @SerializedName("semi_major_axis_km")
    private double semiMajorAxisKm;
    @SerializedName("eccentricity")
    private double eccentricity;
    @ColumnInfo(name = "periapsis_km")
    @SerializedName("periapsis_km")
    private double periapsisKm;
    @ColumnInfo(name = "apoapsis_km")
    @SerializedName("apoapsis_km")
    private double apoapsisKm;
    @ColumnInfo(name = "inclination_deg")
    @SerializedName("inclination_deg")
    private double inclinationDeg;
    @ColumnInfo(name = "period_min")
    @SerializedName("period_min")
    private double periodMin;
    @ColumnInfo(name = "lifespan_years")
    @SerializedName("lifespan_years")
    private int lifespanYears;

    public OrbitParams(String referenceSystem, String regime, double longitude,
                       double semiMajorAxisKm, double eccentricity, double periapsisKm, double apoapsisKm,
                       double inclinationDeg, double periodMin, int lifespanYears) {
        this.apoapsisKm = apoapsisKm;
        this.eccentricity = eccentricity;
        this.inclinationDeg = inclinationDeg;
        this.lifespanYears = lifespanYears;
        this.longitude = longitude;
        this.periapsisKm = periapsisKm;
        this.periodMin = periodMin;
        this.referenceSystem = referenceSystem;
        this.regime = regime;
        this.semiMajorAxisKm = semiMajorAxisKm;
    }

    public double getApoapsisKm() { return apoapsisKm; }
    public double getEccentricity() { return eccentricity; }
    public double getInclinationDeg() { return inclinationDeg; }
    public int getLifespanYears() { return lifespanYears; }
    public double getLongitude() { return longitude; }
    public double getPeriapsisKm() { return periapsisKm; }
    public double getPeriodMin() { return periodMin; }
    public String getReferenceSystem() { return referenceSystem; }
    public String getRegime() { return regime; }
    public double getSemiMajorAxisKm() { return semiMajorAxisKm; }

    public void setApoapsisKm(double apoapsisKm) { this.apoapsisKm = apoapsisKm; }
    public void setEccentricity(double eccentricity) { this.eccentricity = eccentricity; }
    public void setInclinationDeg(double inclinationDeg) { this.inclinationDeg = inclinationDeg; }
    public void setLifespanYears(int lifespanYears) { this.lifespanYears = lifespanYears; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setPeriapsisKm(double periapsisKm) { this.periapsisKm = periapsisKm; }
    public void setPeriodMin(double periodMin) { this.periodMin = periodMin; }
    public void setReferenceSystem(String referenceSystem) { this.referenceSystem = referenceSystem; }
    public void setRegime(String regime) { this.regime = regime; }
    public void setSemiMajorAxisKm(double semiMajorAxisKm) { this.semiMajorAxisKm = semiMajorAxisKm; }
}
