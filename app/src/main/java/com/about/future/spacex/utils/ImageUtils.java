package com.about.future.spacex.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.about.future.spacex.BuildConfig;
import com.about.future.spacex.R;

import java.util.Date;

public class ImageUtils {
    private static final String VIDEO_THUMBNAIL_BASE_URL = "https://img.youtube.com/vi";
    private static final String VIDEO_THUMBNAIL_SIZE_SD = "sddefault.jpg";
    private static final String VIDEO_THUMBNAIL_SIZE_HQ = "hqdefault.jpg";

    private static final String MAP_BASE_URL = "https://maps.googleapis.com/maps/api/staticmap";
    private static final String CENTER_PARAMETER = "center";
    private static final String ZOOM_PARAMETER = "zoom";
    private static final String SIZE_PARAMETER = "size";
    private static final String MAP_TYPE_PARAMETER = "maptype";
    private static final String SCALE_PARAMETER = "scale";
    private static final String API_KEY = "key";

    /* Build and return the Thumbnail URL for SpaceX missions
     * @param missionKey is used to build the url
     */
    public static String buildSdVideoThumbnailUrl(String missionKey) {
        return Uri.parse(VIDEO_THUMBNAIL_BASE_URL).buildUpon()
                .appendPath(missionKey)
                .appendPath(VIDEO_THUMBNAIL_SIZE_SD)
                .build().toString();
    }

    public static String buildHqVideoThumbnailUrl(String missionKey) {
        return Uri.parse(VIDEO_THUMBNAIL_BASE_URL).buildUpon()
                .appendPath(missionKey)
                .appendPath(VIDEO_THUMBNAIL_SIZE_HQ)
                .build().toString();
    }

    /* Build and return the Thumbnail URL for SpaceX launch pad location
     * @param latitude and longitude are used to build the url
     */
    public static String buildMapThumbnailUrl(double latitude, double longitude, int zoom, String mapType, Context context) {
        return Uri.parse(MAP_BASE_URL).buildUpon()
                .appendQueryParameter(CENTER_PARAMETER, String.format(
                        context.getString(R.string.location_coordinates),
                        String.valueOf(latitude),
                        String.valueOf(longitude)))
                .appendQueryParameter(ZOOM_PARAMETER, String.valueOf(zoom))
                .appendQueryParameter(SIZE_PARAMETER, "200x200")
                .appendQueryParameter(MAP_TYPE_PARAMETER, mapType)
                .appendQueryParameter(SCALE_PARAMETER, "2")
                .appendQueryParameter(API_KEY, BuildConfig.GoolgeAPIKey)
                .build().toString();
    }

    public static String buildSatelliteBackdropUrl(double latitude, double longitude, int zoom, Context context) {
        return Uri.parse(MAP_BASE_URL).buildUpon()
                .appendQueryParameter(CENTER_PARAMETER, String.format(
                        context.getString(R.string.location_coordinates),
                        String.valueOf(latitude),
                        String.valueOf(longitude)))
                .appendQueryParameter(ZOOM_PARAMETER, String.valueOf(zoom))
                .appendQueryParameter(SIZE_PARAMETER, "600x350")
                .appendQueryParameter(MAP_TYPE_PARAMETER, "satellite")
                .appendQueryParameter(SCALE_PARAMETER, "2")
                .appendQueryParameter(API_KEY, BuildConfig.GoolgeAPIKey)
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

    public static void setDefaultImage(ImageView imageView, String rocketName, String payloadType, int block) {
        switch (rocketName) {
            case "Falcon 9":
                if (!TextUtils.isEmpty(payloadType)) {
                    switch (payloadType) {
                        case "Satellite":
                            imageView.setImageResource(R.drawable.default_patch_f9_small);
                            break;
                        case "Dragon 1.1":
                            if (block == 5) {
                                imageView.setImageResource(R.drawable.default_patch_dragon1_b5_small);
                            } else {
                                imageView.setImageResource(R.drawable.default_patch_dragon1_b4_small);
                            }
                            break;
                        default:
                            // case "Crew Dragon"
                            imageView.setImageResource(R.drawable.default_patch_dragon_small);
                            break;
                    }
                }
                break;
            case "Falcon Heavy":
                imageView.setImageResource(R.drawable.default_patch_fh_small);
                break;
            case "BFR":
                imageView.setImageResource(R.drawable.default_patch_bfr_small);
                break;
            case "Big Falcon Rocket":
                imageView.setImageResource(R.drawable.default_patch_bfr_small);
                break;
            default:
                imageView.setImageResource(R.drawable.default_patch_f9_small);
                break;
        }
    }

    public static String getThumbnailPath(Context context, int block, String coreSerial) {
        switch (block) {
            case 1:
                switch (coreSerial) {
                    case "B1005": return context.getString(R.string.core_b1005);
                    case "B1006": return context.getString(R.string.core_b1006);
                    case "B1007": return context.getString(R.string.core_b1007);
                    case "B1008": return context.getString(R.string.core_b1008);
                    case "B1010": return context.getString(R.string.core_b1010);
                    case "B1011": return context.getString(R.string.core_b1011);
                    case "B1012": return context.getString(R.string.core_b1012);
                    case "B1013": return context.getString(R.string.core_b1013);
                    case "B1014": return context.getString(R.string.core_b1014);
                    case "B1015": return context.getString(R.string.core_b1015);
                    case "B1016": return context.getString(R.string.core_b1016);
                    case "B1017": return context.getString(R.string.core_b1017);
                    case "B1018": return context.getString(R.string.core_b1018);
                    case "B1019": return context.getString(R.string.core_b1019);
                    case "B1020": return context.getString(R.string.core_b1020);
                    default: return context.getString(R.string.core_block1);
                }
            case 2:
                switch (coreSerial) {
                    case "B1021": return context.getString(R.string.core_b1021);
                    case "B1022": return context.getString(R.string.core_b1022);
                    case "B1023": return context.getString(R.string.core_b1023);
                    case "B1024": return context.getString(R.string.core_b1024);
                    case "B1025": return context.getString(R.string.core_b1025);
                    case "B1026": return context.getString(R.string.core_b1026);
                }
                break;
            case 3:
                switch (coreSerial) {
                    case "B1028": return context.getString(R.string.core_b1028);
                    case "B1029": return context.getString(R.string.core_b1029);
                    case "B1030": return context.getString(R.string.core_b1030);
                    case "B1031": return context.getString(R.string.core_b1031);
                    case "B1032": return context.getString(R.string.core_b1032);
                    case "B1033": return context.getString(R.string.core_b1033);
                    case "B1034": return context.getString(R.string.core_b1034);
                    case "B1035": return context.getString(R.string.core_b1035);
                    case "B1036": return context.getString(R.string.core_b1036);
                    case "B1037": return context.getString(R.string.core_b1037);
                    case "B1038": return context.getString(R.string.core_b1038);
                }
                break;
            case 4:
                switch (coreSerial) {
                    case "B1039": return context.getString(R.string.core_b1039);
                    case "B1040": return context.getString(R.string.core_b1040);
                    case "B1041": return context.getString(R.string.core_b1041);
                    case "B1042": return context.getString(R.string.core_b1042);
                    case "B1043": return context.getString(R.string.core_b1043);
                    case "B1044": return context.getString(R.string.core_b1044);
                    case "B1045": return context.getString(R.string.core_b1045);
                }
                break;
            case 5:
                switch (coreSerial) {
                    case "B1046": return context.getString(R.string.core_b1046);
                    case "B1047": return context.getString(R.string.core_b1047);
                    case "B1048": return context.getString(R.string.core_b1048);
                    case "B1049": return context.getString(R.string.core_b1049);
                    case "B1050": return context.getString(R.string.core_b1050);
                    case "B1051": return context.getString(R.string.core_b1051);
                    case "B1052": return context.getString(R.string.core_b1052);
                    case "B1053": return context.getString(R.string.core_b1053);
                    case "B1054": return context.getString(R.string.core_b1054);
                    case "B1055": return context.getString(R.string.core_b1055);
                    case "B1056": return context.getString(R.string.core_b1056);
                    case "B1057": return context.getString(R.string.core_b1057);
                    case "B1059": return context.getString(R.string.core_b1059);
                    default: return context.getString(R.string.core_block5);
                }
            default: //New type of core
                return "";
        }

        return "";
    }

    public static String getMediumPath(Context context, int block, String coreSerial) {
        switch (block) {
            case 1:
                switch (coreSerial) {
                    case "B1005": return context.getString(R.string.medium_core_b1005);
                    case "B1006": return context.getString(R.string.medium_core_b1006);
                    case "B1007": return context.getString(R.string.medium_core_b1007);
                    case "B1008": return context.getString(R.string.medium_core_b1008);
                    case "B1010": return context.getString(R.string.medium_core_b1010);
                    case "B1011": return context.getString(R.string.medium_core_b1011);
                    case "B1012": return context.getString(R.string.medium_core_b1012);
                    case "B1013": return context.getString(R.string.medium_core_b1013);
                    case "B1014": return context.getString(R.string.medium_core_b1014);
                    case "B1015": return context.getString(R.string.medium_core_b1015);
                    case "B1016": return context.getString(R.string.medium_core_b1016);
                    case "B1017": return context.getString(R.string.medium_core_b1017);
                    case "B1018": return context.getString(R.string.medium_core_b1018);
                    case "B1019": return context.getString(R.string.medium_core_b1019);
                    case "B1020": return context.getString(R.string.medium_core_b1020);
                    default: return context.getString(R.string.medium_core_block1);
                }
            case 2:
                switch (coreSerial) {
                    case "B1021": return context.getString(R.string.medium_core_b1021);
                    case "B1022": return context.getString(R.string.medium_core_b1022);
                    case "B1023": return context.getString(R.string.medium_core_b1023);
                    case "B1024": return context.getString(R.string.medium_core_b1024);
                    case "B1025": return context.getString(R.string.medium_core_b1025);
                    case "B1026": return context.getString(R.string.medium_core_b1026);
                }
                break;
            case 3:
                switch (coreSerial) {
                    case "B1028": return context.getString(R.string.medium_core_b1028);
                    case "B1029": return context.getString(R.string.medium_core_b1029);
                    case "B1030": return context.getString(R.string.medium_core_b1030);
                    case "B1031": return context.getString(R.string.medium_core_b1031);
                    case "B1032": return context.getString(R.string.medium_core_b1032);
                    case "B1033": return context.getString(R.string.medium_core_b1033);
                    case "B1034": return context.getString(R.string.medium_core_b1034);
                    case "B1035": return context.getString(R.string.medium_core_b1035);
                    case "B1036": return context.getString(R.string.medium_core_b1036);
                    case "B1037": return context.getString(R.string.medium_core_b1037);
                    case "B1038": return context.getString(R.string.medium_core_b1038);
                }
                break;
            case 4:
                switch (coreSerial) {
                    case "B1039": return context.getString(R.string.medium_core_b1039);
                    case "B1040": return context.getString(R.string.medium_core_b1040);
                    case "B1041": return context.getString(R.string.medium_core_b1041);
                    case "B1042": return context.getString(R.string.medium_core_b1042);
                    case "B1043": return context.getString(R.string.medium_core_b1043);
                    case "B1044": return context.getString(R.string.medium_core_b1044);
                    case "B1045": return context.getString(R.string.medium_core_b1045);
                }
                break;
            case 5:
                switch (coreSerial) {
                    case "B1046": return context.getString(R.string.medium_core_b1046);
                    case "B1047": return context.getString(R.string.medium_core_b1047);
                    case "B1048": return context.getString(R.string.medium_core_b1048);
                    case "B1049": return context.getString(R.string.medium_core_b1049);
                    case "B1050": return context.getString(R.string.medium_core_b1050);
                    case "B1051": return context.getString(R.string.medium_core_b1051);
                    case "B1052": return context.getString(R.string.medium_core_b1052);
                    case "B1053": return context.getString(R.string.medium_core_b1053);
                    case "B1054": return context.getString(R.string.medium_core_b1054);
                    case "B1055": return context.getString(R.string.medium_core_b1055);
                    case "B1056": return context.getString(R.string.medium_core_b1056);
                    case "B1057": return context.getString(R.string.medium_core_b1057);
                    case "B1059": return context.getString(R.string.medium_core_b1059);
                    default: return context.getString(R.string.medium_core_block5);
                }
            default: //New type of core
                return "";
        }

        return "";
    }

    public static String getBackdropPath(Context context, int block, String coreSerial) {
        switch (block) {
            case 1:
                switch (coreSerial) {
                    case "B1005": return context.getString(R.string.big_core_b1005);
                    case "B1006": return context.getString(R.string.big_core_b1006);
                    case "B1007": return context.getString(R.string.big_core_b1007);
                    case "B1008": return context.getString(R.string.big_core_b1008);
                    case "B1010": return context.getString(R.string.big_core_b1010);
                    case "B1011": return context.getString(R.string.big_core_b1011);
                    case "B1012": return context.getString(R.string.big_core_b1012);
                    case "B1013": return context.getString(R.string.big_core_b1013);
                    case "B1014": return context.getString(R.string.big_core_b1014);
                    case "B1015": return context.getString(R.string.big_core_b1015);
                    case "B1016": return context.getString(R.string.big_core_b1016);
                    case "B1017": return context.getString(R.string.big_core_b1017);
                    case "B1018": return context.getString(R.string.big_core_b1018);
                    case "B1019": return context.getString(R.string.big_core_b1019);
                    case "B1020": return context.getString(R.string.big_core_b1020);
                    default: return context.getString(R.string.big_core_block1);
                }
            case 2:
                switch (coreSerial) {
                    case "B1021": return context.getString(R.string.big_core_b1021);
                    case "B1022": return context.getString(R.string.big_core_b1022);
                    case "B1023": return context.getString(R.string.big_core_b1023);
                    case "B1024": return context.getString(R.string.big_core_b1024);
                    case "B1025": return context.getString(R.string.big_core_b1025);
                    case "B1026": return context.getString(R.string.big_core_b1026);
                }
                break;
            case 3:
                switch (coreSerial) {
                    case "B1028": return context.getString(R.string.big_core_b1028);
                    case "B1029": return context.getString(R.string.big_core_b1029);
                    case "B1030": return context.getString(R.string.big_core_b1030);
                    case "B1031": return context.getString(R.string.big_core_b1031);
                    case "B1032": return context.getString(R.string.big_core_b1032);
                    case "B1033": return context.getString(R.string.big_core_b1033);
                    case "B1034": return context.getString(R.string.big_core_b1034);
                    case "B1035": return context.getString(R.string.big_core_b1035);
                    case "B1036": return context.getString(R.string.big_core_b1036);
                    case "B1037": return context.getString(R.string.big_core_b1037);
                    case "B1038": return context.getString(R.string.big_core_b1038);
                }
                break;
            case 4:
                switch (coreSerial) {
                    case "B1039": return context.getString(R.string.big_core_b1039);
                    case "B1040": return context.getString(R.string.big_core_b1040);
                    case "B1041": return context.getString(R.string.big_core_b1041);
                    case "B1042": return context.getString(R.string.big_core_b1042);
                    case "B1043": return context.getString(R.string.big_core_b1043);
                    case "B1044": return context.getString(R.string.big_core_b1044);
                    case "B1045": return context.getString(R.string.big_core_b1045);
                }
                break;
            case 5:
                switch (coreSerial) {
                    case "B1046": return context.getString(R.string.big_core_b1046);
                    case "B1047": return context.getString(R.string.big_core_b1047);
                    case "B1048": return context.getString(R.string.big_core_b1048);
                    case "B1049": return context.getString(R.string.big_core_b1049);
                    case "B1050": return context.getString(R.string.big_core_b1050);
                    case "B1051": return context.getString(R.string.big_core_b1051);
                    case "B1052": return context.getString(R.string.big_core_b1052);
                    case "B1053": return context.getString(R.string.big_core_b1053);
                    case "B1054": return context.getString(R.string.big_core_b1054);
                    case "B1055": return context.getString(R.string.big_core_b1055);
                    case "B1056": return context.getString(R.string.big_core_b1056);
                    case "B1057": return context.getString(R.string.big_core_b1057);
                    case "B1059": return context.getString(R.string.big_core_b1059);
                    default: return context.getString(R.string.big_core_block5);
                }
            default: //New type of core
                return "";
        }

        return "";
    }
}
