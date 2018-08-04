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
    private Paint paint;
    private boolean isInit;
    private int mQuarter, mHalf, mThreeQuarters;
    private Star[] mStars;

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

    private void initStarField() {
        mWidth = getWidth();
        mHeight = getHeight();
        paint = new Paint();
        isInit = true;

        int totalStars = Math.min(mWidth, mHeight);
        mStars = new Star[totalStars];

        for (int i = 0; i < mStars.length; i++) {
            mStars[i] = new Star(mWidth, mHeight);
        }

        mQuarter = totalStars / 4;
        mHalf = totalStars / 2;
        mThreeQuarters = 3 * mQuarter;
    }

    private void drawStars(Canvas canvas) {
        paint.reset();
        paint.setColor(getResources().getColor(R.color.starColor));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        for (int i = 0; i < mQuarter; i++) {
            mStars[i].showUpLeft(mWidth, mHeight, canvas, paint);
            mStars[i].update(0.4, mWidth, mHeight);

            mStars[i + mQuarter].showUpRight(mWidth, mHeight, canvas, paint);
            mStars[i + mQuarter].update(0.4, mWidth, mHeight);

            mStars[i + mHalf].showDownLeft(mWidth, mHeight, canvas, paint);
            mStars[i + mHalf].update(0.4, mWidth, mHeight);

            mStars[i + mThreeQuarters].showDownRight(mWidth, mHeight, canvas, paint);
            mStars[i + mThreeQuarters].update(0.4, mWidth, mHeight);
        }
    }
}
