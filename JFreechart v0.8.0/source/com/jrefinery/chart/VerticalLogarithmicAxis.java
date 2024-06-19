/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * ----------------------------
 * VerticalLogarithmicAxis.java
 * ----------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  Michael Duffy;
 * Contributor(s):   -;
 *
 * $Id: VerticalLogarithmicAxis.java,v 1.1 2007/10/10 18:59:09 vauchers Exp $
 *
 * Changes
 * -------
 * 14-Mar-2002 : Version 1 contributed by Michael Duffy (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.Paint;
import java.awt.Font;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

/**
 * A logartihmic value axis, for values displayed vertically.
 */
public class VerticalLogarithmicAxis extends VerticalNumberAxis  {

    /**
     * Constructs a vertical logarithmic axis, using default values where necessary.
     */
    public VerticalLogarithmicAxis() {

        this(null);

    }

    /**
     * Constructs a vertical logarithmic axis, using default values where necessary.
     * @param label The axis label (null permitted).
     */
    public VerticalLogarithmicAxis(String label) {

	// Set the default min/max axis values for a logaritmic scale.
        this(label,
             Axis.DEFAULT_AXIS_LABEL_FONT,
             1,
             10);

        this.autoRange = true;

    }

    /**
     * Constructs a vertical logarithmic axis.
     * @param label The axis label (null permitted).
     * @param labelFont The font for displaying the axis label.
     * @param minimumAxisValue The lowest value shown on the axis.
     * @param maximumAxisValue The highest value shown on the axis.
     */
    public VerticalLogarithmicAxis(String label,
                                   Font labelFont,
			           double minimumAxisValue,
                                   double maximumAxisValue) {

	this(label,
             labelFont,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             Axis.DEFAULT_AXIS_LABEL_INSETS,
             true, // vertical axis label
             true, // tick labels visible
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
	     true, // tick marks visible
             Axis.DEFAULT_TICK_STROKE,
             true, // auto range
             true, // auto range includes zero
             NumberAxis.DEFAULT_MINIMUM_AUTO_RANGE,
	     minimumAxisValue,
             maximumAxisValue,
             false, // inverted
	     true, // auto tick unit selection
             NumberAxis.DEFAULT_TICK_UNIT,
	     true, // grid lines visible
             ValueAxis.DEFAULT_GRID_LINE_STROKE,
             ValueAxis.DEFAULT_GRID_LINE_PAINT,
             ValueAxis.DEFAULT_CROSSHAIR_VISIBLE,
             0.0,
             ValueAxis.DEFAULT_CROSSHAIR_STROKE,
             ValueAxis.DEFAULT_CROSSHAIR_PAINT);
    }

    /**
     * Constructs a vertical number axis.
     * @param label The axis label.
     * @param labelFont The font for displaying the axis label.
     * @param labelPaint The paint used to draw the axis label.
     * @param labelInsets Determines the amount of blank space around the label.
     * @param labelDrawnVertical Flag indicating whether or not the label is drawn vertically.
     * @param tickLabelsVisible Flag indicating whether or not tick labels are visible.
     * @param tickLabelFont The font used to display tick labels.
     * @param tickLabelPaint The paint used to draw tick labels.
     * @param tickLabelInsets Determines the amount of blank space around tick labels.
     * @param showTickMarks Flag indicating whether or not tick marks are visible.
     * @param tickMarkStroke The stroke used to draw tick marks (if visible).
     * @param autoRange Flag indicating whether or not the axis is automatically scaled to fit the
     *                  data.
     * @param autoRangeIncludesZero A flag indicating whether or not zero *must* be displayed on
     *                              axis.
     * @param autoRangeMinimum The smallest automatic range allowed.
     * @param minimumAxisValue The lowest value shown on the axis.
     * @param maximumAxisValue The highest value shown on the axis.
     * @param inverted A flag indicating whether the axis is normal or inverted (inverted means
     *                 running from positive to negative).
     * @param autoTickUnitSelection A flag indicating whether or not the tick units are
     *                              selected automatically.
     * @param tickUnit The tick unit.
     * @param showGridLines Flag indicating whether or not grid lines are visible for this axis.
     * @param gridStroke The pen/brush used to display grid lines (if visible).
     * @param gridPaint The color used to display grid lines (if visible).
     * @param crosshairValue The value at which to draw an optional crosshair (null permitted).
     * @param crosshairStroke The pen/brush used to draw the crosshair.
     * @param crosshairPaint The color used to draw the crosshair.
     */
    public VerticalLogarithmicAxis(String label,
                                   Font labelFont, Paint labelPaint, Insets labelInsets,
			           boolean labelDrawnVertical,
			           boolean tickLabelsVisible, Font tickLabelFont, Paint tickLabelPaint,
                                   Insets tickLabelInsets,
			           boolean tickMarksVisible, Stroke tickMarkStroke,
			           boolean autoRange, boolean autoRangeIncludesZero,
                                   Number autoRangeMinimum,
			           double minimumAxisValue, double maximumAxisValue,
                                   boolean inverted,
			           boolean autoTickUnitSelection,
                                   NumberTickUnit tickUnit,
 			           boolean gridLinesVisible, Stroke gridStroke, Paint gridPaint,
                                   boolean crosshairVisible, double crosshairValue,
                                   Stroke crosshairStroke, Paint crosshairPaint) {

	super(label,
              labelFont, labelPaint, labelInsets,
              labelDrawnVertical,
              tickLabelsVisible,
              tickLabelFont, tickLabelPaint, tickLabelInsets,
              tickMarksVisible,
              tickMarkStroke,
	      autoRange, autoRangeIncludesZero, false, autoRangeMinimum,
	      minimumAxisValue, maximumAxisValue,
              inverted,
              autoTickUnitSelection, tickUnit,
              gridLinesVisible, gridStroke, gridPaint,
              crosshairVisible, crosshairValue, crosshairStroke, crosshairPaint);
    }

    public double translateValueToJava2D(double value, Rectangle2D plotArea) {

	double axisMin = minimumAxisValue;
	double axisMax = maximumAxisValue;

	double maxY = plotArea.getMaxY();
	double minY = plotArea.getMinY();

        // The Math.log() funtion is based on e not 10.
        if (value != 0.0) {

            value = Math.log(value)/Math.log(10);
        }

        if (axisMin != 0.0) {

            axisMin = Math.log(axisMin)/Math.log(10);
        }

        if (axisMax != 0.0) {

            axisMax = Math.log(axisMax)/Math.log(10);
        }

        if (inverted) {
            return minY + (((value - axisMin)/(axisMax - axisMin)) * (maxY - minY));
        }
        else {
            return maxY - (((value - axisMin)/(axisMax - axisMin)) * (maxY - minY));
        }

    }

    public double translateJava2DtoValue(float java2DValue, Rectangle2D plotArea) {

	double axisMin = minimumAxisValue;
	double axisMax = maximumAxisValue;

	double plotY = plotArea.getY();
	double plotMaxY = plotArea.getMaxY();

        // The Math.log() funtion is based on e not 10.
        if (axisMin != 0.0) {

            axisMin = Math.log(axisMin)/Math.log(10);
        }

        if (axisMax != 0.0) {

            axisMax = Math.log(axisMax)/Math.log(10);
        }

        if (inverted) {
            return axisMin + Math.pow(10, ((java2DValue-plotY)/(plotMaxY-plotY))*(axisMax - axisMin));
        }
        else {
            return axisMax - Math.pow(10, ((java2DValue-plotY)/(plotMaxY-plotY))*(axisMax - axisMin));
        }
    }

    /**
     * Rescales the axis to ensure that all data is visible.
     */
    public void autoAdjustRange() {

	if (plot!=null) {
	    if (plot instanceof VerticalValuePlot) {
		VerticalValuePlot vvp = (VerticalValuePlot)plot;

                Number u = vvp.getMaximumVerticalDataValue();
                double upper = this.DEFAULT_MAXIMUM_AXIS_VALUE;
                if (u!=null) {
		    upper = u.doubleValue();
                }

                upper = computeLogCeil(upper);

                Number l = vvp.getMinimumVerticalDataValue();
                double lower = this.DEFAULT_MINIMUM_AXIS_VALUE;
                if (l!=null) {
		    lower = l.doubleValue();
                }

                lower = computeLogFloor(lower);

		this.minimumAxisValue=lower;
		this.maximumAxisValue=upper;
	    }
	}

    }

    /**
     * Returns the smallest (closest to negative infinity) double value that is
     * not less than the argument, is equal to a mathematical integer and
     * satisfying the condition that log base 10 of the value is an integer
     * (i.e., the value returned will be a power of 10: 1, 10, 100, 1000, etc.).
     *
     * @param lower  a double value above which a ceiling will be calcualted.
     */
    private double computeLogCeil(double upper) {

        // The Math.log() funtion is based on e not 10.
        double logCeil = Math.log(upper)/Math.log(10);

        logCeil = Math.ceil(logCeil);

        logCeil = Math.pow(10, logCeil);

        return logCeil;
    }

    /**
     * Returns the largest (closest to positive infinity) double value that is
     * not greater than the argument, is equal to a mathematical integer and
     * satisfying the condition that log base 10 of the value is an integer
     * (i.e., the value returned will be a power of 10: 1, 10, 100, 1000, etc.).
     *
     * @param lower  a double value below which a floor will be calcualted.
     */
    private double computeLogFloor(double lower) {

        // The Math.log() funtion is based on e not 10.
        double logFloor = Math.log(lower)/Math.log(10);

        logFloor = Math.floor(logFloor);

        logFloor = Math.pow(10, logFloor);

        return logFloor;
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device;
     * @param drawArea The area within which the chart should be drawn.
     * @param plotArea The area within which the plot should be drawn (a subset of the drawArea).
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

        //this.setGridStroke(new BasicStroke());
        //this.setGridPaint(Color.lightGray);

	// draw the axis label
	if (this.label!=null) {
	    g2.setFont(labelFont);
	    g2.setPaint(labelPaint);

	    Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
	    if (this.verticalLabel) {
		double xx = drawArea.getX()+labelInsets.left+labelBounds.getHeight();
		double yy = plotArea.getY()+plotArea.getHeight()/2+(labelBounds.getWidth()/2);
		drawVerticalString(label, g2, (float)xx, (float)yy);
	    }
	    else {
		double xx = drawArea.getX()+labelInsets.left;
		double yy = drawArea.getY()+drawArea.getHeight()/2-labelBounds.getHeight()/2;
		g2.drawString(label, (float)xx, (float)yy);
	    }
	}

	// draw the tick labels and marks and gridlines
	this.refreshTicks(g2, drawArea, plotArea);
	double xx = plotArea.getX();
	g2.setFont(tickLabelFont);

	Iterator iterator = ticks.iterator();
	while (iterator.hasNext()) {
	    Tick tick = (Tick)iterator.next();
	    float yy = (float)this.translateValueToJava2D(tick.getNumericalValue(), plotArea);
	    if (tickLabelsVisible) {
		g2.setPaint(this.tickLabelPaint);
		g2.drawString(tick.getText(), tick.getX(), tick.getY());
	    }
	    if (tickMarksVisible) {
		g2.setStroke(this.getTickMarkStroke());
		Line2D mark = new Line2D.Double(plotArea.getX()-2, yy,
						plotArea.getX()+2, yy);
		g2.draw(mark);
	    }
	    if (gridLinesVisible) {
		g2.setStroke(gridStroke);
		g2.setPaint(gridPaint);
		Line2D gridline = new Line2D.Double(xx, yy,
						    plotArea.getMaxX(), yy);
		g2.draw(gridline);

	    }
	}

    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the results in the
     * tick label list (ready for drawing).
     * @param g2 The graphics device.
     * @param drawArea The area in which the plot and the axes should be drawn.
     * @param plotArea The area in which the plot should be drawn.
     */
    public void refreshTicks(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

        if (minimumAxisValue == 0) {

            minimumAxisValue = 1;
        }

        this.ticks.clear();

        int iBegCount = (int)Math.rint(Math.log(minimumAxisValue)/Math.log(10));

        int iEndCount = (int)Math.rint(Math.log(maximumAxisValue)/Math.log(10));

        for (int i = iBegCount; i <= iEndCount; i++) {

            int jEndCount = 10;

            if (i == iEndCount) {

                jEndCount = 1;
            }

            for (int j = 0; j < jEndCount; j++) {

                Number currentTickValue = new Double(Math.pow(10, i) + (Math.pow(10, i)*j));

                double yy = this.translateValueToJava2D(currentTickValue.doubleValue(), plotArea);

                String tickLabel = "";

                if (j == 0) {

                    tickLabel = String.valueOf(currentTickValue.intValue());
                }

                Rectangle2D tickLabelBounds = tickLabelFont.getStringBounds(tickLabel, g2.getFontRenderContext());

                float x = (float)(plotArea.getX()
                                  - tickLabelBounds.getWidth()
                                  - tickLabelInsets.left
                                  - tickLabelInsets.right);

                float y = (float)(yy + (tickLabelBounds.getHeight()/2));

                Tick tick = new Tick(currentTickValue, tickLabel, x, y);

                ticks.add(tick);
            }
        }
    }
}
