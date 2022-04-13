package com.about.future.spacex.model.pads;
import androidx.room.TypeConverters;

import com.about.future.spacex.model.StringArrayTypeConverter;
import com.google.gson.annotations.SerializedName;

public class Images {
   @SerializedName("large")
   @TypeConverters(StringArrayTypeConverter.class)
   private final String[] largeImages;

   public Images(String[] largeImages) {
      this.largeImages = largeImages;
   }

   public String[] getLargeImages() {
      return largeImages;
   }
}
