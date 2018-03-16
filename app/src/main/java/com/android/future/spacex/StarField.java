package com.android.future.spacex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.android.future.spacex.data.Star;


public class StarField extends View {
    private int width;
    private int height;
    private Paint paint;
    private boolean isInit;

    Star[] stars = new Star[800];

    public StarField(Context context) {
        super(context);
    }

    public StarField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //initStarField();
    }

    public StarField(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initStarField () {
        width = getWidth();
        height = getHeight();
        paint = new Paint();
        isInit = true;

        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star(width, height);
        }
//        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setColor(Color.WHITE);
//        paint.setStyle(Paint.Style.FILL);
//        paint.setShadowLayer(100, 0, 0, Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInit) {
            initStarField();
        }
        drawStars(canvas);
        invalidate();
    }

    private void drawStars(Canvas canvas) {
        paint.reset();
        paint.setColor(getResources().getColor(android.R.color.white));
        //paint.setShadowLayer(100, 0, 0, getResources().getColor(R.color.colorAccent));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        //canvas.drawColor(getResources().getColor(R.color.colorBackground));
        //canvas.drawCircle(width/2, 3*height/4, 50, paint);

        for (int i = 0; i < 100; i++) {
            stars[i].showUpLeft(width, height, canvas, paint);
            stars[i].update(0.3, width, height);

            stars[i + 100].showUpRight(width, height, canvas, paint);
            stars[i + 100].update(0.3, width, height);

            stars[i + 200].showDownLeft(width, height, canvas, paint);
            stars[i + 200].update(0.3, width, height);

            stars[i + 300].showDownRight(width, height, canvas, paint);
            stars[i + 300].update(0.3, width, height);
        }
    }
}
