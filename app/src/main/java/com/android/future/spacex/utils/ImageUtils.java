package com.android.future.spacex.utils;

import android.content.Context;

public class ImageUtils {

    private static final String VIDEO_THUMBNAIL_BASE_URL = "https://img.youtube.com/vi/";
    private static final String VIDEO_THUMBNAIL_SIZE_SD = "/sddefault.jpg";
    private static final String VIDEO_THUMBNAIL_SIZE_HQ = "/hqdefault.jpg";

    /* Build and return the Thumbnail URL for movie trailers and teasers
     * @param context is used to access utility method getPreferredImageQuality
     * @param movieKey is used to build the url
     */
    public static String buildSdVideoThumbnailUrl(Context context, String movieKey) {
        return VIDEO_THUMBNAIL_BASE_URL.concat(movieKey).concat(VIDEO_THUMBNAIL_SIZE_SD);
    }

    public static String buildHqVideoThumbnailUrl(Context context, String movieKey) {
        return VIDEO_THUMBNAIL_BASE_URL.concat(movieKey).concat(VIDEO_THUMBNAIL_SIZE_HQ);
    }

    //https://img.youtube.com/vi/ycMagB1s8XM/sddefault.jpg
}
