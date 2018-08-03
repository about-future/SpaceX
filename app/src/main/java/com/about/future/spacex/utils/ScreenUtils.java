package com.about.future.spacex.utils;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.about.future.spacex.R;

public class ScreenUtils {

    /* Return the width of the screen in pixels (i.e. 720, 1280 or 1600)
     * @param context is used to create a windowManager, so we can get the screen metrics
     */
    public static int getScreenWidhtInPixels(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.widthPixels;
    }

//    /* Return the current width of the screen in DPs.
//     * @param context is used to call getScreenSize method
//     *
//     * For a phone the width in portrait mode can be 360dp and in landscape mode 640dp.
//     * For a tablet the width in portrait mode can be 800dp and in landscape mode 1280dp
//     */
//    public static int getScreenWidthInDps (Context context) {
//        // Get the screen sizes and density
//        float[] screenSize = getScreenSize(context);
//        // Divide the first item of the array(screen width) by the last one(screen density),
//        // round the resulting number and return it
//        return Math.round(screenSize[0] / screenSize[2]);
//    }
//
//    /* Return the current height of the screen in DPs.
//     * @param context is used to call getScreenSize method
//     *
//     */
//    public static int getScreenHeightInDps (Context context) {
//        // Get the screen sizes and density
//        float[] screenSize = getScreenSize(context);
//        // Divide the second item of the array(screen height) by the last one(screen density),
//        // round the resulting number and return it
//        return Math.round(screenSize[1] / screenSize[2]);
//    }

    /* Return the width, height of the screen in pixels and the screen density (i.e. {720.0, 1280.0, 2.0})
     * @param context is used to create a windowManager, so we can get the screen metrics
     */
    public static float[] getScreenSize(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        return new float[]{
                (float) displayMetrics.widthPixels,
                (float) displayMetrics.heightPixels,
                displayMetrics.density};
    }

    /* Check the screen orientation and return true if it's portrait or false if it's landscape
     * @param context is used to access resources
     */
    public static boolean isPortraitMode(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /* Display a snakbar message in the selected view
       @param mainView is used to point to the desired view
       @param message is used to carry the desired message
     */
    public static void snakBarThis(View mainView, String message) {
        Snackbar snackbar = Snackbar.make(mainView, message, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackgroundColor(view.getResources().getColor(R.color.colorPrimary, null));
        } else {
            view.setBackgroundColor(view.getResources().getColor(R.color.colorPrimary));
        }
        snackbar.show();
    }
}
