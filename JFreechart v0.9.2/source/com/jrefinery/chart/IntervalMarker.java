package com.jrefinery.chart;

import java.awt.Paint;

public class IntervalMarker extends Marker {

    protected double startValue;
    protected double endValue;
    protected String label;

    public IntervalMarker(double startValue, double endValue, String label,
                          Paint outlinePaint, Paint paint, float alpha) {

        super((startValue+endValue)/2, outlinePaint, paint, alpha);
        this.startValue = startValue;
        this.endValue = endValue;
        this.label = label;
    }

    public double getStartValue() {
        return this.startValue;
    }

    public double getEndValue() {
        return this.endValue;
    }

    public String getLabel() {
        return this.label;
    }

}