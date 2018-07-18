package com.about.future.spacex.utils;

import android.content.Context;
import android.net.Uri;

import java.util.Date;

public class ImageUtils {

    private static final String VIDEO_THUMBNAIL_BASE_URL = "https://img.youtube.com/vi/";
    private static final String VIDEO_THUMBNAIL_SIZE_SD = "/sddefault.jpg";
    private static final String VIDEO_THUMBNAIL_SIZE_HQ = "/hqdefault.jpg";

    private static final String MAP_BASE_URL = "https://maps.googleapis.com/maps/api/staticmap";
    private static final String CENTER_PARAMETER = "center";
    private static final String ZOOM_PARAMETER = "zoom";
    private static final String SIZE_PARAMETER = "size";
    private static final String MAP_TYPE_PARAMETER = "maptype";
    private static final String SCALE_PARAMETER = "scale";
    private static final String KEY_PARAMETER = "key";

    /* Build and return the Thumbnail URL for SpaceX missions
     * @param missionKey is used to build the url
     */
    public static String buildSdVideoThumbnailUrl(String missionKey) {
        return VIDEO_THUMBNAIL_BASE_URL.concat(missionKey).concat(VIDEO_THUMBNAIL_SIZE_SD);
    }

    public static String buildHqVideoThumbnailUrl(String missionKey) {
        return VIDEO_THUMBNAIL_BASE_URL.concat(missionKey).concat(VIDEO_THUMBNAIL_SIZE_HQ);
    }

    /* Build and return the Thumbnail URL for SpaceX launch pad location
     * @param latitude and longitude are used to build the url
     */
    public static String buildMapThumbnailUrl(double latitude, double longitude) {
        return Uri.parse(MAP_BASE_URL).buildUpon()
                .appendQueryParameter(CENTER_PARAMETER, String.valueOf(latitude) + "," + String.valueOf(longitude))
                .appendQueryParameter(ZOOM_PARAMETER, "14")
                .appendQueryParameter(SIZE_PARAMETER, "200x200")
                .appendQueryParameter(MAP_TYPE_PARAMETER, "satellite")
                .appendQueryParameter(SCALE_PARAMETER, "2")
                .build().toString();
    }

    public static String buildMapBackdropUrl(double latitude, double longitude) {
        return Uri.parse(MAP_BASE_URL).buildUpon()
                .appendQueryParameter(CENTER_PARAMETER, String.valueOf(latitude) + "," + String.valueOf(longitude))
                .appendQueryParameter(ZOOM_PARAMETER, "15")
                .appendQueryParameter(SIZE_PARAMETER, "600x350")
                .appendQueryParameter(MAP_TYPE_PARAMETER, "satellite")
                .appendQueryParameter(SCALE_PARAMETER, "2")
                .build().toString();
    }

    /* Return true if the difference between Now and savedDate is equal or grater than 30 days
     * @param context is used to access preferences method
     */
    public static boolean doWeNeedToFetchImagesOnline(Context context) {
        long now = new Date().getTime() / 1000;
        long savedDate = SpaceXPreferences.getLaunchPadsThumbnailsSavingDate(context) / 1000;

        return (now - savedDate) >= (30 * 24 * 60 * 60); // 30 days * 24h * 60min * 60sec
    }
}
