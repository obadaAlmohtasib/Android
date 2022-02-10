package com.example.paintart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Painter extends android.view.View {

    private final ArrayList<Path> brushes;
    private final ArrayList<Paint> paints;

    public Painter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // initialize global var, to avoid Null Pointer Exception.
        brushes = new ArrayList<>();
        paints = new ArrayList<>();
        // set default values
        paints.add(getDefaultPaint());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float pointX = event.getX();
        float pointY = event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                Path brush = new Path();
                brush.moveTo(pointX, pointY);
                brushes.add(brush);
                break;

            case MotionEvent.ACTION_MOVE:
                brushes.get(brushes.size() - 1).lineTo(pointX, pointY);
                break;

            case MotionEvent.ACTION_UP:
                paints.add(getDefaultPaint());
                break;

            default:
                return false;
        }

        invalidate();
        return true;
    }

    private Paint getDefaultPaint() {

        Paint paint = new Paint();
        if (paints.size() == 0) {
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            paint.setAlpha(255);
        } else {
            paint.setColor(getPaint().getColor());
            paint.setStyle(getPaint().getStyle());
            paint.setStrokeWidth(getPaint().getStrokeWidth());
            paint.setAlpha(getPaint().getAlpha());
        }

        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int count = 0;
        for (Path brush : brushes) {
            canvas.drawPath(brush, paints.get(count));
            count++;
        }

        super.onDraw(canvas);
    }

    public Paint getPaint() {
        return paints.get(paints.size() - 1);
    }

    public void cleanAll() {
        brushes.clear(); // Removes all of the elements from this list.
        paints.clear();
        paints.add(getDefaultPaint());
        invalidate();
    }

    public boolean closeShape() {

        if (brushes.size() == 0) {
            return false;
        }

        brushes.get(brushes.size() - 1).close();
        invalidate();
        return true;
    }

}