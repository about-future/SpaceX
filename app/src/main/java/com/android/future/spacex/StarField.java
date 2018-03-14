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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInit) {
            initStarField();
        }

        canvas.drawColor(getResources().getColor(R.color.colorTransparent));
        //drawStar(canvas);

        paint.reset();
        paint.setColor(getResources().getColor(R.color.colorStar));
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);



        for (int i = 0; i < 200; i++) {
            stars[i].showUpLeft(width, height, canvas, paint);
            stars[i].update(0.2, width, height);

            stars[i + 200].showUpRight(width, height, canvas, paint);
            stars[i + 200].update(0.2, width, height);

            stars[i + 400].showDownRight(width, height, canvas, paint);
            stars[i + 400].update(0.2, width, height);

            stars[i + 600].showDownLeft(width, height, canvas, paint);
            stars[i + 600].update(0.2, width, height);

        }

//        for (int i = 0; i < 200; i++) {
//            stars[i + 200].showUpRight(width, height, canvas, paint);
//            stars[i + 200].update(0.2, width, height);
//        }
//
//        for (int i = 0; i < 200; i++) {
//            stars[i + 400].showDownRight(width, height, canvas, paint);
//            stars[i + 400].update(0.2, width, height);
//        }
//
//        for (int i = 0; i < 200; i++) {
//            stars[i + 600].showDownLeft(width, height, canvas, paint);
//            stars[i + 600].update(0.2, width, height);
//        }

        postInvalidateDelayed(500);
        invalidate();

    }

    private void drawStar(Canvas canvas) {
        paint.reset();
        paint.setColor(getResources().getColor(android.R.color.white));
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        //canvas.drawCircle(width / 2, height / 2, 100, paint);

        //canvas.drawCircle(width / 3, height / 3, 50, paint);

        for (int i=1; i < 20; i++) {
            canvas.drawCircle(width / i, height / i, 5, paint);
        }
    }
}
