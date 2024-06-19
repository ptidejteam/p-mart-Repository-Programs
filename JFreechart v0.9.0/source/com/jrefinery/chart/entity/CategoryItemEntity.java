package com.jrefinery.chart.entity;

import java.awt.Shape;

public class CategoryItemEntity extends ChartEntity {

    protected int series;

    protected Object category;

    public CategoryItemEntity(Shape area, String toolTipText, int series, Object category) {
        super(area, toolTipText);
        this.series = series;
        this.category = category;
    }

    public int getSeries() {
        return this.series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public Object getCategory() {
        return this.category;
    }

    public void setCategory(Object category) {
        this.category = category;
    }

    public String toString() {
        return "Category Item: series="+series+", category="+category.toString();
    }

}