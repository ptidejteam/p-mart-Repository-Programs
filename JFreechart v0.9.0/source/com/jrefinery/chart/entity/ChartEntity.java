package com.jrefinery.chart.entity;

import java.awt.Shape;

/**
 * A class that captures information about some component of a chart (a bar, line etc).
 */
public class ChartEntity {

    protected Shape area;

    protected String toolTipText;

    public ChartEntity(Shape area, String toolTipText) {
        this.area = area;
        this.toolTipText = toolTipText;
    }

    public Shape getArea() {
        return this.area;
    }

    public void setArea(Shape area) {
        this.area = area;
    }

    public String getToolTipText() {
        return this.toolTipText;
    }

    public void setToolTipText(String text) {
        this.toolTipText = toolTipText;
    }

}