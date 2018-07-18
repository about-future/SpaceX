package com.about.future.spacex.utils;

import android.content.Context;

public class ImageUtils {

    private static final String VIDEO_THUMBNAIL_BASE_URL = "https://img.youtube.com/vi/";
    private static final String VIDEO_THUMBNAIL_SIZE_SD = "/sddefault.jpg";
    private static final String VIDEO_THUMBNAIL_SIZE_HQ = "/hqdefault.jpg";

    private static final String MAP_THUMBNAIL_BASE_URL = "https://maps.googleapis.com/maps/api/staticmap?center=";
    private static final String MAP_THUMBNAIL_REST_URL = "&zoom=14&size=200x200&maptype=satellite";

    /* Build and return the Thumbnail URL for SpaceX missions
     * @param missionKey is used to build the url
     */
    public static String buildSdVideoThumbnailUrl(Context context, String missionKey) {
        return VIDEO_THUMBNAIL_BASE_URL.concat(missionKey).concat(VIDEO_THUMBNAIL_SIZE_SD);
    }

    public static String buildHqVideoThumbnailUrl(Context context, String missionKey) {
        return VIDEO_THUMBNAIL_BASE_URL.concat(missionKey).concat(VIDEO_THUMBNAIL_SIZE_HQ);
    }

    /* Build and return the Thumbnail URL for SpaceX launch pad location
     * @param latitude and longitude are used to build the url
     */
    public static String buildMapThumbnailUrl(double latitude, double longitude) {
        return MAP_THUMBNAIL_BASE_URL
                .concat(String.valueOf(latitude))
                .concat(",")
                .concat(String.valueOf(longitude))
                .concat(MAP_THUMBNAIL_REST_URL);
    }
}
