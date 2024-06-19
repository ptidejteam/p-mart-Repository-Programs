package com.jrefinery.chart.entity;

import java.awt.Shape;

public class PieSectionEntity extends ChartEntity {

    protected Object category;

    public PieSectionEntity(Shape area, String toolTipText, Object category) {
        super(area, toolTipText);
        this.category = category;
    }

    public Object getCategory() {
        return this.category;
    }

    public void setCategory(Object category) {
        this.category = category;
    }

    public String toString() {
        return "Pie section: "+category.toString();
    }

}