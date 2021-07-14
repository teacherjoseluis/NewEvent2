package com.example.newevent2.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.newevent2.R;

public class LetterAvatar extends ColorDrawable {
    Paint               paint   = new Paint();
    Rect bounds  = new Rect();

    String              pLetters;
    private float       ONE_DP  = 0.0f;
    private Resources pResources;
    private int         pPadding;
    int                 pSize   = 0;
    float               pMesuredTextWidth;

    int                 pBoundsTextwidth;
    int                 pBoundsTextHeight;
    Context             context;

    //------------------------------------------------------
    private Paint circlePaint;
    //------------------------------------------------------

    public LetterAvatar (Context context, int color, String letter, int paddingInDp) {
        super(color);
        this.pLetters = letter;
        this.pResources = context.getResources();
        ONE_DP = 1 * pResources.getDisplayMetrics().density;
        this.pPadding = Math.round(paddingInDp * ONE_DP);
        this.context = context;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        paint.setAntiAlias(true);

        do {
            paint.setTextSize(++pSize);
            paint.getTextBounds(pLetters, 0, pLetters.length(), bounds);

        } while ((bounds.height() < (canvas.getHeight() - pPadding)) && (paint.measureText(pLetters) < (canvas.getWidth() - pPadding)));

        paint.setTextSize(pSize);
        pMesuredTextWidth = paint.measureText(pLetters);
        pBoundsTextHeight = bounds.height();

        float xOffset = ((canvas.getWidth() - pMesuredTextWidth) / 2);
        float yOffset = (int) (pBoundsTextHeight + (canvas.getHeight() - pBoundsTextHeight) / 2);
        paint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        paint.setColor(0xffffffff);
//------------------------------------------------------
        circlePaint = new Paint();
        circlePaint.setColor(ContextCompat.getColor(context, R.color.rosaChillon));
        circlePaint.setAntiAlias(true);

        canvas.drawCircle(-3, 15 - (xOffset), yOffset + 5, circlePaint);
//------------------------------------------------------
        canvas.drawText(pLetters, xOffset, yOffset, paint);
    }
}