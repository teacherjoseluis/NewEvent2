package com.bridesandgrooms.event.ui;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

/**
 * Default formatter used for formatting values inside the chart. Uses a DecimalFormat with
 * pre-calculated number of digits (depending on max and min value).
 *
 * @author Philipp Jahoda
 */
public class CurrencyValueFormatter extends ValueFormatter
{

    /**
     * DecimalFormat for formatting
     */
    protected DecimalFormat mFormat;

    protected int mDecimalDigits;

    /**
     * Sets up the formatter with a given number of decimal digits.
     *
     * @param digits
     */

    public void setup(int digits) {

        this.mDecimalDigits = digits;

        StringBuilder b = new StringBuilder();
        for (int i = 0; i < digits; i++) {
            if (i == 0)
                b.append(".");
            b.append("0");
        }

        mFormat = new DecimalFormat("$###,###,###,##0" + b.toString());
    }

    @Override
    public String getFormattedValue(float value) {

        // put more logic here ...
        // avoid memory allocations here (for performance reasons)

        return mFormat.format(value);
    }

}
