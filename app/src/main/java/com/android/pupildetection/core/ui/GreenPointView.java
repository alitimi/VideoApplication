package com.android.pupildetection.core.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GreenPointView extends View {

    private Paint paint;

    public GreenPointView(Context context) {
        super(context);
        init();
    }

    public GreenPointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GreenPointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerX = getWidth() - 75;
        int centerY = getHeight() / 2;
        int radius = 25;  // or any desired size
        canvas.drawCircle(centerX, centerY, radius, paint);
    }
}


