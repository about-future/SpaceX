/* Credits for this class, go to Jerry Zhao
   Please see the link below for full swipe gesture tutorial:
   www.dev2qa.com/how-to-detect-swipe-gesture-direction-in-android/ */

package com.about.future.spacex.utils;

import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.about.future.spacex.view.SpaceXActivity;
import com.about.future.spacex.StarfieldActivity;

public class DetectSwipeGestureListener  extends GestureDetector.SimpleOnGestureListener {

    // Source activity that display message in text view.
    private StarfieldActivity activity = null;

    public void setActivity(StarfieldActivity activity) {
        this.activity = activity;
    }

    // This method is invoked when a swipe gesture happened
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        // Minimal x and y axis swipe distance
        int MIN_SWIPE_DISTANCE_X = 100;
        int MIN_SWIPE_DISTANCE_Y = 100;

        // Maximal x and y axis swipe distance
        int MAX_SWIPE_DISTANCE_X = 1000;
        int MAX_SWIPE_DISTANCE_Y = 1000;

        // Get swipe delta value in x axis.
        float deltaX = e1.getX() - e2.getX();

        // Get swipe delta value in y axis.
        float deltaY = e1.getY() - e2.getY();

        // Get absolute value.
        float deltaXAbs = Math.abs(deltaX);
        float deltaYAbs = Math.abs(deltaY);

        // Only when swipe distance between minimal and maximal distance value,
        // then we treat it as effective swipe gesture
        if((deltaXAbs >= MIN_SWIPE_DISTANCE_X && deltaXAbs <= MAX_SWIPE_DISTANCE_X)
                || (deltaYAbs >= MIN_SWIPE_DISTANCE_Y && deltaYAbs <= MAX_SWIPE_DISTANCE_Y)) {
            activity.startActivity(new Intent(activity, SpaceXActivity.class));
        }
        return true;
    }
}
