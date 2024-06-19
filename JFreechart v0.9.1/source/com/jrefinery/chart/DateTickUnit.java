package com.jrefinery.chart;

import java.text.DateFormat;
import java.util.Date;

public class DateTickUnit extends TickUnit {

    public static final int YEAR = 0;
    public static final int MONTH = 1;
    public static final int DAY = 2;
    public static final int HOUR = 3;
    public static final int MINUTE = 4;
    public static final int SECOND = 5;
    public static final int MILLISECOND = 6;

    protected int unit;

    protected int count;

    protected DateFormat formatter;

    public DateTickUnit(int unit, int count, DateFormat formatter) {

        super(0.0);  // pass zero for the size, then calculate an approximation below...
        this.unit = unit;
        this.count = count;
        this.size = 0.0;  // compute the approximate size

    }

    /**
     * Formats a value.
     */
    public String valueToString(double milliseconds) {
        return formatter.format(new Date((long)milliseconds));
    }

}