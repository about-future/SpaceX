package com.about.future.spacex.model.stars;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.about.future.spacex.R;


public class StarField extends View {
    private int mWidth;
    private int mHeight;
    private Paint paint, shadowPaint;
    private boolean isInit;
    private int mTotalStars;
    private int mQuarter, mHalf, mThreeQuarters;
    Star[] stars;

    public StarField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInit) {
            initStarField();
        }
        drawStars(canvas);
        invalidate();
    }

    public void initStarField() {
        mWidth = getWidth();
        mHeight = getHeight();
        paint = new Paint();
        shadowPaint = new Paint();
        isInit = true;

        mTotalStars = Math.min(mWidth, mHeight);
        stars = new Star[mTotalStars];

        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star(mWidth, mHeight);
        }

        mQuarter = mTotalStars / 4;
        mHalf = mTotalStars / 2;
        mThreeQuarters = 3 * mQuarter;
    }

    private void drawStars(Canvas canvas) {
        paint.reset();
        paint.setColor(getResources().getColor(R.color.starColor));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        for (int i = 0; i < mQuarter; i++) {
            stars[i].showUpLeft(mWidth, mHeight, canvas, paint);
            stars[i].update(0.4, mWidth, mHeight);

            stars[i + mQuarter].showUpRight(mWidth, mHeight, canvas, paint);
            stars[i + mQuarter].update(0.4, mWidth, mHeight);

            stars[i + mHalf].showDownLeft(mWidth, mHeight, canvas, paint);
            stars[i + mHalf].update(0.4, mWidth, mHeight);

            stars[i + mThreeQuarters].showDownRight(mWidth, mHeight, canvas, paint);
            stars[i + mThreeQuarters].update(0.4, mWidth, mHeight);
        }
    }
}
