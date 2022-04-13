package com.about.future.spacex.model.mission;
import androidx.room.TypeConverters;

import com.about.future.spacex.model.StringArrayTypeConverter;
import com.google.gson.annotations.SerializedName;

public class Flickr {
   @TypeConverters(StringArrayTypeConverter.class)
   private final String[] small;

   @SerializedName("original")
   @TypeConverters(StringArrayTypeConverter.class)
   private final String[] original;

   public Flickr(String[] small, String[] original) {
      this.small = small;
      this.original = original;
   }

   public String[] getSmall() { return small; }
   public String[] getOriginal() { return original; }
}
