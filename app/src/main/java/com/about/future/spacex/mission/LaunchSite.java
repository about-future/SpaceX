package com.about.future.spacex.mission;

import android.arch.persistence.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class LaunchSite {
    @ColumnInfo(name = "site_id")
    @SerializedName("site_id")
    private String siteId;
    @ColumnInfo(name = "site_name")
    @SerializedName("site_name")
    private String siteName;
    @ColumnInfo(name = "site_name_long")
    @SerializedName("site_name_long")
    private String siteNameLong;

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

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public void setSiteNameLong(String siteNameLong) {
        this.siteNameLong = siteNameLong;
    }
}
