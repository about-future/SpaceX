package com.android.future.spacex.data;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.android.future.spacex.R;
import com.android.future.spacex.utils.MathUtils;

import java.util.Random;

import static com.android.future.spacex.utils.MathUtils.map;

public class Star {
    private float x;
    private float y;
    private float z;

    public Star(int width, int height) {
        this.x = (float) (Math.random() * width/2);
        this.y = (float) (Math.random() * height/2);
        this.z = (float) (Math.random() * width/2);
    }

    public void update(double speed, int width, int height) {
        this.z = (float) (this.z - speed);
        if (this.z < 1) {
            this.z = (float) (Math.random() * width/2);
            this.x = (float) (Math.random() * width/2);
            this.y = (float) (Math.random() * height/2);
        }
    }

    public void showUpLeft(int width, int height, Canvas canvas, Paint paint) {
        float sx = map(this.x / this.z, 1, 0, 0, width / 2);
        float sy = map(this.y / this.z, 1, 0, 0, height / 2);
        float r = map(this.z, 0, width * 2, 6, -30);
        paint.setShadowLayer(r*2, 0, 0, Color.BLUE);
        canvas.drawCircle(sx, sy, r, paint);
    }

    public void showUpRight(int width, int height, Canvas canvas, Paint paint) {
        float sx = map(this.x / this.z, 0, 1, width / 2, width);
        float sy = map(this.y / this.z, 1, 0, height / 4, height / 2);
        float r = map(this.z, 0, width * 2, 6, -30);
        paint.setShadowLayer(r*2, 0, 0, Color.BLUE);
        canvas.drawCircle(sx, sy, r, paint);
    }

    public void showDownLeft(int width, int height, Canvas canvas, Paint paint) {
        float sx = map(this.x / this.z, 1, 0, 0, width / 2);
        float sy = map(this.y / this.z, 0, 1, height / 2, height);
        float r = map(this.z, 0, width * 2, 6, -30);
        paint.setShadowLayer(r*2, 0, 0, Color.BLUE);
        canvas.drawCircle(sx, sy, r, paint);
    }

    public void showDownRight(int width, int height, Canvas canvas, Paint paint) {
        float sx = map(this.x / this.z, 0, 1, width / 2, width);
        float sy = map(this.y / this.z, 0, 1, height / 2, height);
        float r = map(this.z, 0, width * 2, 6, -30);
        paint.setShadowLayer(r*2, 0, 0, Color.BLUE);
        canvas.drawCircle(sx, sy, r, paint);
    }
}
