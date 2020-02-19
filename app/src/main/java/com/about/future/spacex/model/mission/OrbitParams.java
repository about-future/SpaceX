package com.about.future.spacex.model.mission;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class OrbitParams {
    @ColumnInfo(name = "reference_system")
    @SerializedName("reference_system")
    private final String referenceSystem;
    @SerializedName("regime")
    private final String regime;
    @SerializedName("longitude")
    private final double longitude;
    @ColumnInfo(name = "semi_major_axis_km")
    @SerializedName("semi_major_axis_km")
    private final double semiMajorAxisKm;
    @SerializedName("eccentricity")
    private final double eccentricity;
    @ColumnInfo(name = "periapsis_km")
    @SerializedName("periapsis_km")
    private final double periapsisKm;
    @ColumnInfo(name = "apoapsis_km")
    @SerializedName("apoapsis_km")
    private final double apoapsisKm;
    @ColumnInfo(name = "inclination_deg")
    @SerializedName("inclination_deg")
    private final double inclinationDeg;
    @ColumnInfo(name = "period_min")
    @SerializedName("period_min")
    private final double periodMin;
    @ColumnInfo(name = "lifespan_years")
    @SerializedName("lifespan_years")
    private final double lifespanYears;

    public OrbitParams(String referenceSystem, String regime, double longitude,
                       double semiMajorAxisKm, double eccentricity, double periapsisKm, double apoapsisKm,
                       double inclinationDeg, double periodMin, double lifespanYears) {
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
    public double getLifespanYears() { return lifespanYears; }
    public double getLongitude() { return longitude; }
    public double getPeriapsisKm() { return periapsisKm; }
    public double getPeriodMin() { return periodMin; }
    public String getReferenceSystem() { return referenceSystem; }
    public String getRegime() { return regime; }
    public double getSemiMajorAxisKm() { return semiMajorAxisKm; }
}
