package com.about.future.spacex.utils;

import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.about.future.spacex.ui.SpaceXActivity;
import com.about.future.spacex.StarfieldActivity;

public class DetectSwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
    // Source activity
    private StarfieldActivity activity;

    public void setActivity(StarfieldActivity activity) {
        this.activity = activity;
    }

    // This method is invoked when a swipe gesture happened
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // Minimal x and y axis swipe distance
        int minSwipeDistanceX = 100;
        int minSwipeDistanceY = 100;

        // Maximal x and y axis swipe distance
        int maxSwipeDistanceX = 1000;
        int maxSwipeDistanceY = 1000;

        // Get swipe delta value in x axis.
        float deltaX = e1.getX() - e2.getX();

        // Get swipe delta value in y axis.
        float deltaY = e1.getY() - e2.getY();

        // Get absolute value.
        float deltaXAbs = Math.abs(deltaX);
        float deltaYAbs = Math.abs(deltaY);

        // Only when the value of swipe distance is between minimal and maximal distance,
        // then we treat it as an effective swipe gesture
        if((deltaXAbs >= minSwipeDistanceX && deltaXAbs <= maxSwipeDistanceX)
                || (deltaYAbs >= minSwipeDistanceY && deltaYAbs <= maxSwipeDistanceY)) {
                activity.startActivity(new Intent(activity, SpaceXActivity.class));
        }
        return true;
    }
}
