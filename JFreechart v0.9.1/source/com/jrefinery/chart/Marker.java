package com.jrefinery.chart;

import java.awt.Paint;

public class Marker {

    protected double value;

    protected Paint outlinePaint;

    protected Paint paint;

    protected float alpha;

    public Marker(double value, Paint outlinePaint, Paint paint, float alpha) {
        this.value = value;
        this.outlinePaint = outlinePaint;
        this.paint = paint;
        this.alpha = alpha;
    }

    public double getValue() {
        return this.value;
    }

    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    public Paint getPaint() {
        return this.paint;
    }

    public float getAlpha() {
        return this.alpha;
    }

}