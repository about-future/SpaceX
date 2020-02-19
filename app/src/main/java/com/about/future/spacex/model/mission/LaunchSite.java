package com.about.future.spacex.model.mission;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class LaunchSite {
    @ColumnInfo(name = "site_id")
    @SerializedName("site_id")
    private final String siteId;
    @ColumnInfo(name = "site_name")
    @SerializedName("site_name")
    private final String siteName;
    @ColumnInfo(name = "site_name_long")
    @SerializedName("site_name_long")
    private final String siteNameLong;

    public LaunchSite(String siteId, String siteName, String siteNameLong) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.siteNameLong = siteNameLong;
    }

    public String getSiteId() {
        return siteId;
    }
    public String getSiteName() {
        return siteName;
    }
    public String getSiteNameLong() {
        return siteNameLong;
    }
}
