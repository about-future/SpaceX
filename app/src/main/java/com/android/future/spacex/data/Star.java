package com.android.future.spacex.data;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.android.future.spacex.utils.MathUtils;

import static com.android.future.spacex.utils.MathUtils.map;

public class Star {
    private float x;
    private float y;
    private float z;

    public Star(int width, int height) {
        this.x = (float) (Math.random() * width);
        this.y = (float) (Math.random() * height);
        this.z = (float) (Math.random() * (width / 2));
    }

    public void update(double speed, int width, int height) {
        z = (float) (z - speed);
        if (z < 1) {
            z = width / 2;
            x = (float) (Math.random() * width);
            y = (float) (Math.random() * height);
        }
    }

    public void showUpLeft(int width, int height, Canvas canvas, Paint paint) {
        float sx = map(x / z, 0, 1, width/2, width/4);
        float sy = map(y / z, 0, 1, height/2, height/4);
        // Use the z value to increase the star size between a range from 0 to 16.
        float r = map(z, 0, width/2, 8, 0);
        // Draw star
        canvas.drawCircle(sx, sy, r, paint);
    }

    public void showUpRight(int width, int height, Canvas canvas, Paint paint) {
        float sx = map(x / z, 0, 1, width/2, width);
        float sy = map(y / z, 1, 0, height/4, height/2);
        // Use the z value to increase the star size between a range from 0 to 16.
        float r = map(z, 0, width/2, 8, 0);
        // Draw star
        canvas.drawCircle(sx, sy, r, paint);
    }

    public void showDownRight(int width, int height, Canvas canvas, Paint paint) {
        float sx = map(x / z, 0, 1, width/2, width);
        float sy = map(y / z, 0, 1, height/2, height);
        // Use the z value to increase the star size between a range from 0 to 16.
        float r = map(z, 0, width/2, 8, 0);
        // Draw star
        canvas.drawCircle(sx, sy, r, paint);
    }

    public void showDownLeft(int width, int height, Canvas canvas, Paint paint) {
        float sx = map(x / z, 1, 0, 0, width/2);
        float sy = map(y / z, 0, 1, height/2, height);
        // Use the z value to increase the star size between a range from 0 to 16.
        float r = map(z, 0, width/2, 8, 0);
        // Draw star
        canvas.drawCircle(sx, sy, r, paint);
    }
}
