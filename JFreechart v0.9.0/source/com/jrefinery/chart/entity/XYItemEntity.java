package com.jrefinery.chart.entity;

/**
 * Title:        JFreeChart Development
 * Description:  JFreeChart development project (http:/sourceforge.net/projects/jfreechart).
 * Copyright:    Copyright (c) 2001
 * Company:      Simba Management Limited
 * @author
 * @version 1.0
 */

import java.awt.Shape;

public class XYItemEntity extends ChartEntity {

    protected int series;

    protected int item;

    public XYItemEntity(Shape area, String tooltipText, int series, int item) {
        super(area, tooltipText);
        this.series = series;
        this.item = item;
    }
}