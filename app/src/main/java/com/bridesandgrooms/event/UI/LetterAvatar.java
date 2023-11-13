package com.bridesandgrooms.event.UI;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import androidx.core.content.ContextCompat;
import com.bridesandgrooms.event.R;

public class LetterAvatar extends ColorDrawable {
    final Paint               paint   = new Paint();
    final Rect bounds  = new Rect();

    final String              pLetters;
    private final int         pPadding;
    int                 pSize   = 0;
    float               pMesuredTextWidth;

    int                 pBoundsTextHeight;
    final Context             context;

    //------------------------------------------------------

    public LetterAvatar (Context context, int color, String letter, int paddingInDp) {
        super(color);
        this.pLetters = letter;
        Resources pResources = context.getResources();
        float ONE_DP = 1 * pResources.getDisplayMetrics().density;
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

        } while ((bounds.height() < (getBounds().height() - pPadding)) && (paint.measureText(pLetters) < (getBounds().width() - pPadding)));

        paint.setTextSize(pSize);
        pMesuredTextWidth = paint.measureText(pLetters);
        pBoundsTextHeight = bounds.height();

        float xOffset = ((getBounds().width() - pMesuredTextWidth) / 2);
        float yOffset = pBoundsTextHeight + (getBounds().height() - pBoundsTextHeight) / 2;
        paint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        paint.setColor(0xffffffff);
//------------------------------------------------------
        //------------------------------------------------------
        Paint circlePaint = new Paint();
        circlePaint.setColor(ContextCompat.getColor(context, R.color.rosaChillon));
        circlePaint.setAntiAlias(true);

        canvas.drawCircle(-3, 15 - (xOffset), yOffset + 5, circlePaint);
//------------------------------------------------------
        canvas.drawText(pLetters, xOffset, yOffset, paint);
    }
}