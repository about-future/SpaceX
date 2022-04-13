package com.about.future.spacex.model.mission;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class Reddit {
   @ColumnInfo(name = "campaign")
   @SerializedName("campaign")
   private final String campaign;

   @ColumnInfo(name = "launch")
   @SerializedName("launch")
   private final String launch;

   @ColumnInfo(name = "recovery")
   @SerializedName("recovery")
   private final String recovery;

   @ColumnInfo(name = "media")
   @SerializedName("media")
   private final String media;

   public Reddit(String campaign, String launch, String recovery, String media) {
      this.campaign = campaign;
      this.launch = launch;
      this.recovery = recovery;
      this.media = media;
   }

   public String getCampaign() { return campaign; }
   public String getLaunch() { return launch; }
   public String getRecovery() { return recovery; }
   public String getMedia() { return media; }
}
