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
    private final Paint textPaint = new Paint();
    private final Paint circlePaint = new Paint();
    private final Rect bounds = new Rect();
    private final String letters;
    private final int padding;
    private final Context context;

    public LetterAvatar(Context context, int color, int colorCircle, String letters, int paddingInDp) {
        super(color);
        this.letters = letters;
        this.context = context;

        // Set up text paint
        textPaint.setAntiAlias(true);
        textPaint.setColor(0xffffffff); // White color for text
        textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        textPaint.setTextSize(70); // Set a fixed text size, you can adjust this as needed

        // Set up circle paint
        circlePaint.setColor(ContextCompat.getColor(context, colorCircle));
        circlePaint.setAntiAlias(true);

        // Convert padding from dp to pixels
        Resources resources = context.getResources();
        float density = resources.getDisplayMetrics().density;
        this.padding = Math.round(paddingInDp * density);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // Calculate the center position for the circle
        int centerX = getBounds().width() / 2;
        int centerY = getBounds().height() / 2;

        // Calculate the circle radius
        int circleRadius = Math.min(centerX, centerY) - padding;

        // Draw the circle
        canvas.drawCircle(centerX, centerY, circleRadius, circlePaint);

        // Get the bounds for the text
        textPaint.getTextBounds(letters, 0, letters.length(), bounds);

        // Calculate the position to draw the text
        float textX = centerX - bounds.exactCenterX();
        float textY = centerY - bounds.exactCenterY();

        // Draw the text
        canvas.drawText(letters, textX, textY, textPaint);
    }
}
