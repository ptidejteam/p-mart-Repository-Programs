/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
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
 * -----------------------
 * VerticalNumberAxis.java
 * -----------------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   David Li;
 *                   Jonathan Nash;
 *
 * $Id: VerticalNumberAxis.java,v 1.1 2007/10/10 18:56:25 vauchers Exp $
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 23-Jun-2001 : Modified to work with null data source (DG);
 * 18-Sep-2001 : Updated e-mail address and fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Updated configure() method.  Replaced some hard-coded defaults. (DG);
 * 12-Dec-2001 : Minor change due to grid lines bug fix (DG);
 * 08-Jan-2002 : Added flag to allow axis to be inverted (DG);
 * 16-Jan-2002 : Added an optional crosshair, based on the implementation by Jonathan Nash (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;

import com.jrefinery.chart.event.*;

/**
 * A standard linear value axis, for values displayed vertically.
 * <P>
 * Note that bug 4273469 on the Java Developer Connection talks about why the grid lines don't
 * always line up with the tick marks precisely.
 *
 */
public class VerticalNumberAxis extends NumberAxis implements VerticalAxis {

    /** A flag indicating whether or not the axis label is drawn vertically. */
    protected boolean labelDrawnVertical;

    /**
     * Constructs a vertical number axis, using default values where necessary.
     */
    public VerticalNumberAxis() {

        this(null);

    }

    /**
     * Constructs a vertical number axis, using default values where necessary.
     * @param label The axis label (null permitted).
     */
    public VerticalNumberAxis(String label) {

	this(label,
             Axis.DEFAULT_AXIS_LABEL_FONT,
             ValueAxis.DEFAULT_MINIMUM_AXIS_VALUE,
             ValueAxis.DEFAULT_MAXIMUM_AXIS_VALUE);

        this.autoRange = true;

    }

    /**
     * Constructs a vertical number axis.
     * @param label The axis label (null permitted).
     * @param labelFont The font for displaying the axis label.
     * @param minimumAxisValue The lowest value shown on the axis.
     * @param maximumAxisValue The highest value shown on the axis.
     */
    public VerticalNumberAxis(String label, Font labelFont,
			      double minimumAxisValue, double maximumAxisValue) {

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
    public VerticalNumberAxis(String label,
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
                              double crosshairValue, Stroke crosshairStroke, Paint crosshairPaint) {

	super(label,
              labelFont, labelPaint, labelInsets,
              tickLabelsVisible,
              tickLabelFont, tickLabelPaint, tickLabelInsets,
              tickMarksVisible,
              tickMarkStroke,
	      autoRange, autoRangeIncludesZero, autoRangeMinimum,
	      minimumAxisValue, maximumAxisValue,
              inverted,
              autoTickUnitSelection, tickUnit,
              gridLinesVisible, gridStroke, gridPaint,
              crosshairValue, crosshairStroke, crosshairPaint);

	this.labelDrawnVertical = labelDrawnVertical;

    }

    /**
     * Returns a flag that indicates whether or not the axis label is drawn with a vertical
     * orientation (this saves space).
     * @return A flag that indicates whether or not the axis label is drawn with a vertical
     * orientation.
     */
    public boolean isLabelDrawnVertical() {
	return this.labelDrawnVertical;
    }

    /**
     * Sets the flag that controls whether or not the axis label is drawn with a vertical
     * orientation.
     * @param flag The flag.
     */
    public void setLabelDrawnVertical(boolean flag) {

        if (this.labelDrawnVertical!=flag) {
	    this.labelDrawnVertical = flag;
	    this.notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Configures the axis to work with the specified plot.  If the axis has auto-scaling, then sets
     * the maximum and minimum values.
     */
    public void configure() {
	if (isAutoRange()) {
	    autoAdjustRange();
	}
    }

//    /**
//     * Translates the data value to the display coordinates (Java 2D User Space) of the chart.
//     * @param dataValue The value to be plotted.
//     * @param plotArea The plot area in Java 2D User Space.
//     */
//    public double translatedValue(Number dataValue, Rectangle2D plotArea) {
//
//        return this.translateValueToJava2D(dataValue, plotArea);
//
//    }

    public double translateValueToJava2D(double value, Rectangle2D plotArea) {

	double axisMin = minimumAxisValue;
	double axisMax = maximumAxisValue;

	double maxY = plotArea.getMaxY();
	double minY = plotArea.getMinY();

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
        if (inverted) {
            return axisMin + (java2DValue-plotY)/(plotMaxY-plotY)*(axisMax-axisMin);
        }
        else {
            return axisMax - (java2DValue-plotY)/(plotMaxY-plotY)*(axisMax-axisMin);
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

                Number l = vvp.getMinimumVerticalDataValue();
                double lower = this.DEFAULT_MINIMUM_AXIS_VALUE;
                if (l!=null) {
		    lower = l.doubleValue();
                }

		double range = upper-lower;

                // ensure the autorange is at least <minRange> in size...
		double minRange = this.autoRangeMinimumSize.doubleValue();
		if (range<minRange) {
		    upper = (upper+lower+minRange)/2;
		    lower = (upper+lower-minRange)/2;
		}

		if (this.autoRangeIncludesZero()) {
		    if (upper!=0.0) upper = Math.max(0.0, upper+upperMargin*range);
		    if (lower!=0.0) lower = Math.min(0.0, lower-lowerMargin*range);
		}
		else {
		    if (upper!=0.0) upper = upper+upperMargin*range;
		    if (lower!=0.0) lower = lower-lowerMargin*range;
		}

		this.minimumAxisValue=lower;
		this.maximumAxisValue=upper;
	    }
	}

    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device;
     * @param drawArea The area within which the chart should be drawn.
     * @param plotArea The area within which the plot should be drawn (a subset of the drawArea).
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

	// draw the axis label
	if (this.label!=null) {
	    g2.setFont(labelFont);
	    g2.setPaint(labelPaint);

	    Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
	    if (labelDrawnVertical) {
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
     * Returns the width required to draw the axis in the specified draw area.
     * @param g2 The graphics device;
     * @param plot A reference to the plot;
     * @param drawArea The area within which the plot should be drawn.
     */
    public double reserveWidth(Graphics2D g2, Plot plot, Rectangle2D drawArea) {

	// calculate the width of the axis label...
	double labelWidth = 0.0;
	if (label!=null) {
	    Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
	    labelWidth = labelInsets.left+labelInsets.right;
	    if (this.labelDrawnVertical) {
		labelWidth = labelWidth + labelBounds.getHeight();  // assume width == height before rotation
	    }
	    else {
		labelWidth = labelWidth + labelBounds.getWidth();
	    }
	}

	// calculate the width required for the tick labels (if visible);
	double tickLabelWidth = tickLabelInsets.left+tickLabelInsets.right;
	if (tickLabelsVisible) {
	    this.refreshTicks(g2, drawArea, drawArea);
	    tickLabelWidth = tickLabelWidth+getMaxTickLabelWidth(g2, drawArea);
	}
	return labelWidth+tickLabelWidth;

    }

    /**
     * Returns area in which the axis will be displayed.
     * @param g2 The graphics device;
     * @param plot A reference to the plot;
     * @param drawArea The area in which the plot and axes should be drawn;
     * @param reservedHeight The height reserved for the horizontal axis;
     */
    public Rectangle2D reserveAxisArea(Graphics2D g2, Plot plot, Rectangle2D drawArea,
				       double reservedHeight) {

	// calculate the width of the axis label...
	double labelWidth = 0.0;
	if (label!=null) {
	    Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
	    labelWidth = labelInsets.left+labelInsets.right;
	    if (this.labelDrawnVertical) {
		labelWidth = labelWidth + labelBounds.getHeight();  // assume width == height before rotation
	    }
	    else {
		labelWidth = labelWidth + labelBounds.getWidth();
	    }
	}

	// calculate the width of the tick labels
	double tickLabelWidth = tickLabelInsets.left+tickLabelInsets.right;
	if (tickLabelsVisible) {
	    Rectangle2D approximatePlotArea = new Rectangle2D.Double(drawArea.getX(), drawArea.getY(),
								     drawArea.getWidth(),
								     drawArea.getHeight()-reservedHeight);
	    this.refreshTicks(g2, drawArea, approximatePlotArea);
	    tickLabelWidth = tickLabelWidth+getMaxTickLabelWidth(g2, approximatePlotArea);
	}

	return new Rectangle2D.Double(drawArea.getX(), drawArea.getY(), labelWidth+tickLabelWidth,
				      drawArea.getHeight()-reservedHeight);

    }

    /**
     * Selects an appropriate tick value for the axis.  The strategy is to display as many ticks as
     * possible (selected from an array of 'standard' tick units) without the labels overlapping.
     * @param g2 The graphics device;
     * @param drawArea The area in which the plot and axes should be drawn;
     * @param plotArea The area in which the plot should be drawn;
     */
    private void selectAutoTickUnit(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

        // calculate the tick label height...
        FontRenderContext frc = g2.getFontRenderContext();
        double tickLabelHeight = tickLabelFont.getLineMetrics("123", frc).getHeight()
                                 +this.tickLabelInsets.top+this.tickLabelInsets.bottom;

        // now find the smallest tick unit that will accommodate the labels...
	double zero = this.translateValueToJava2D(0.0, plotArea);

        // start with the current tick unit...
        NumberTickUnit candidate1
                         = (NumberTickUnit)this.standardTickUnits.getNearestTickUnit(this.tickUnit);
        double y = this.translateValueToJava2D(candidate1.getValue().doubleValue(), plotArea);
        double unitHeight = Math.abs(y-zero);

        // then extrapolate...
        double bestguess = (tickLabelHeight/unitHeight) * candidate1.value.doubleValue();
        NumberTickUnit guess = new NumberTickUnit(new Double(bestguess), null);
        NumberTickUnit candidate2
                             = (NumberTickUnit)this.standardTickUnits.getNearestTickUnit(guess);

        this.tickUnit = candidate2;

    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the results in the
     * tick label list (ready for drawing).
     * @param g2 The graphics device.
     * @param drawArea The area in which the plot and the axes should be drawn.
     * @param plotArea The area in which the plot should be drawn.
     */
    public void refreshTicks(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

	this.ticks.clear();

	g2.setFont(tickLabelFont);

	if (this.autoTickUnitSelection) {
	    selectAutoTickUnit(g2, drawArea, plotArea);
	}

	double size = this.tickUnit.getValue().doubleValue();
	int count = this.calculateVisibleTickCount();
	double lowestTickValue = this.calculateLowestVisibleTickValue();
	//tickLabelFormatter = new DecimalFormat(tickLabelFormatter.toPattern());
	for (int i=0; i<count; i++) {
	    Number currentTickValue = new Double(lowestTickValue+(i*size));
	    double yy = this.translateValueToJava2D(currentTickValue.doubleValue(), plotArea);
	    String tickLabel = this.valueToString(currentTickValue.doubleValue());
	    Rectangle2D tickLabelBounds = tickLabelFont.getStringBounds(tickLabel,
                                                                        g2.getFontRenderContext());
	    float x = (float)(plotArea.getX()
                              -tickLabelBounds.getWidth()
                              -tickLabelInsets.left-tickLabelInsets.right);
	    float y = (float)(yy+(tickLabelBounds.getHeight()/2));
	    Tick tick = new Tick(currentTickValue, tickLabel, x, y);
	    ticks.add(tick);
	}

    }

    /**
     * Returns true if the specified plot is compatible with the axis, and false otherwise.
     * <P>
     * This class (VerticalNumberAxis) requires that the plot implements the VerticalValuePlot
     * interface.
     * @param plot The plot.
     * @return True if the specified plot is compatible with the axis, and false otherwise.
     */
    protected boolean isCompatiblePlot(Plot plot) {

        if (plot instanceof VerticalValuePlot) return true;
        else return false;

    }

}
