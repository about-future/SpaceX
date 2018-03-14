package com.android.future.spacex.utils;

public class MathUtils {

    public static float map(float number, float oldMin, float oldMax, float newMin, float newMax)
    {
        return newMin + (number - oldMin) * (newMax - newMin) / (oldMax - oldMin);
    }
}
