/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: HorizontalCategoryAxis.java,v 1.1 2007/10/10 18:53:20 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * (C) Copyright 2000, 2001 Simba Management Limited;
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
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 23-Jun-2001 : Modified to work with null data source (DG);
 * 18-Sep-2001 : Updated e-mail address and fixed DOS encoding problem (DG);
 * 16-Oct-2001 : Moved data source classes to com.jrefinery.data.* (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 07-Nov-2001 : Updated configure() method (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.util.*;
import com.jrefinery.data.*;

/**
 * A horizontal axis that displays categories.  Used for bar charts and line charts.
 * <P>
 * Note: the axis needs to rely on the plot for assistance with the placement of category labels,
 * since the plot controls how the categories are distributed.
 */
public class HorizontalCategoryAxis extends CategoryAxis implements HorizontalAxis {

    /** A flag that indicates whether or not the category labels should be drawn vertically; */
    protected boolean verticalCategoryLabels;

    /**
     * Full constructor: returns a new HorizontalCategoryAxis with attributes as specified by the
     * caller.
     * @param label The axis label.
     * @param labelFont The font for displaying the axis label.
     * @param labelPaint The paint used to draw the axis label.
     * @param labelInsets Determines the amount of blank space around the label.
     * @param showCategoryLabels Flag indicating whether or not category (tick) labels are visible.
     * @param verticalCategoryLabels Flag indicating whether or not the category labels are drawn
     *                               vertically.
     * @param categoryLabelFont The font used to display category (tick) labels.
     * @param categoryLabelPaint The paint used to draw category (tick) labels.
     * @param categoryLabelInsets Determines the blank space around each category (tick) label.
     * @param showTickMarks Flag indicating whether or not tick marks are visible.
     * @param tickMarkStroke The stroke used to draw tick marks (if visible).
     */
    public HorizontalCategoryAxis(String label, Font labelFont, Paint labelPaint,
                                  Insets labelInsets,
                                  boolean showCategoryLabels, boolean verticalCategoryLabels,
				  Font categoryLabelFont, Paint categoryLabelPaint,
                                  Insets categoryLabelInsets,
				  boolean showTickMarks, Stroke tickMarkStroke) {

	super(label, labelFont, labelPaint, labelInsets,
	      showCategoryLabels, categoryLabelFont, categoryLabelPaint, categoryLabelInsets,
	      showTickMarks, tickMarkStroke);

	this.verticalCategoryLabels = verticalCategoryLabels;

    }

    /**
     * Standard constructor - builds a HorizontalCategoryAxis with defaults for most attributes.
     * @param label The axis label;
     */
    public HorizontalCategoryAxis(String label) {
	super(label);
	this.verticalCategoryLabels = false;
    }

    /**
     * Returns a flag indicating whether the category labels are drawn 'vertically'.
     */
    public boolean getVerticalCategoryLabels() {
	return this.verticalCategoryLabels;
    }

    /**
     * Sets the flag that determines whether the category labels are drawn 'vertically'.
     * @param flag The new value of the flag;
     */
    public void setVerticalCategoryLabels(boolean flag) {
	this.verticalCategoryLabels = flag;
	this.notifyListeners(new com.jrefinery.chart.event.AxisChangeEvent(this));
    }

    /**
     * Draws the HorizontalCategoryAxis on a Java 2D graphics device (such as the screen or a
     * printer).
     * @param g2 The graphics device;
     * @param drawArea The area within which the axis should be drawn;
     * @param plotArea The area within which the plot is being drawn.
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

	// draw the axis label
	g2.setFont(labelFont);
	g2.setPaint(labelPaint);
	FontRenderContext frc = g2.getFontRenderContext();
	Rectangle2D labelBounds = labelFont.getStringBounds(label, frc);
	LineMetrics lm = labelFont.getLineMetrics(label, frc);
	float labelx = (float)(plotArea.getX()+plotArea.getWidth()/2-labelBounds.getWidth()/2);
	float labely = (float)(drawArea.getMaxY()-labelInsets.bottom
                               -lm.getDescent()-lm.getLeading());
	g2.drawString(label, labelx, labely);

	// draw the category labels
	if (this.tickLabelsVisible) {
	    g2.setFont(tickLabelFont);
	    g2.setPaint(tickLabelPaint);
	    this.refreshTicks(g2, drawArea, plotArea);
	    Iterator iterator = ticks.iterator();
	    while (iterator.hasNext()) {
		Tick tick = (Tick)iterator.next();
		if (this.verticalCategoryLabels) {
		    this.drawVerticalString(tick.getText(), g2, tick.getX(), tick.getY());
		}
		else {
		    g2.drawString(tick.getText(), tick.getX(), tick.getY());
		}
	    }
	}

    }

    /**
     * Creates a temporary list of ticks that can be used when drawing the axis.
     * @param g2 The graphics device (used to get font measurements);
     * @param drawArea The area where the plot and axes will be drawn;
     * @param plotArea The area inside the axes;
     */
    public void refreshTicks(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {
	this.ticks.clear();
	CategoryPlot categoryPlot = (CategoryPlot)plot;
        Dataset data = categoryPlot.getDataset();
        if (data!=null) {
	    FontRenderContext frc = g2.getFontRenderContext();
	    Font font = this.getTickLabelFont();
	    g2.setFont(font);
	    int categoryIndex = 0;
	    float xx = 0.0f;
	    float yy = 0.0f;
	    Iterator iterator = categoryPlot.getDataset().getCategories().iterator();
	    while (iterator.hasNext()) {
	        Object category = iterator.next();
	        String label = category.toString();
	        Rectangle2D labelBounds = font.getStringBounds(label, frc);
	        LineMetrics metrics = font.getLineMetrics(label, frc);
	        if (this.verticalCategoryLabels) {
		    xx = (float)(categoryPlot.getCategoryCoordinate(categoryIndex, plotArea)+
		 	     labelBounds.getHeight()/2);
		    yy = (float)(plotArea.getMaxY()+tickLabelInsets.top+labelBounds.getWidth());
	        }
	        else {
		    xx = (float)(categoryPlot.getCategoryCoordinate(categoryIndex, plotArea)-
			     labelBounds.getWidth()/2);
		    yy = (float)(plotArea.getMaxY()+tickLabelInsets.top+metrics.getHeight()
			     -metrics.getDescent());
	        }
	        Tick tick = new Tick(category, label, xx, yy);
	        ticks.add(tick);
                categoryIndex = categoryIndex+1;
            }
	}
    }

    /**
     * Estimates the height required for the axis, given a specific drawing area, without any
     * information about the width of the vertical axis.
     * <P>
     * Supports the HorizontalAxis interface.
     * @param g2 The graphics device (used to obtain font information);
     * @param plot The plot that the axis belongs to;
     * @param drawArea The area within which the axis should be drawn;
     */
    public double reserveHeight(Graphics2D g2, Plot plot, Rectangle2D drawArea) {

	// calculate the height of the axis label...
	Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
	double labelHeight = this.labelInsets.top+labelInsets.bottom+labelBounds.getHeight();

	// calculate the height required for the tick labels (if visible);
	double tickLabelHeight = 0.0;
	if (tickLabelsVisible) {
	    g2.setFont(tickLabelFont);
	    this.refreshTicks(g2, drawArea, drawArea);
	    tickLabelHeight = tickLabelInsets.top+tickLabelInsets.bottom+
		getMaxTickLabelHeight(g2, drawArea, this.verticalCategoryLabels);
	}
	return labelHeight+tickLabelHeight;
    }

    /**
     * Returns the area required to draw the axis in the specified draw area.
     * @param g2 The graphics device;
     * @param plot The plot that the axis belongs to;
     * @param drawArea The area within which the plot should be drawn;
     * @param reservedWidth The width reserved by the vertical axis;
     */
    public Rectangle2D reserveAxisArea(Graphics2D g2, Plot plot, Rectangle2D drawArea,
				       double reservedWidth) {

	// calculate the height of the axis label...
	Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
	double labelHeight = this.labelInsets.top+labelInsets.bottom+labelBounds.getHeight();

	// calculate the height required for the tick labels (if visible);
	double tickLabelHeight = 0.0;
	if (tickLabelsVisible) {
	    g2.setFont(tickLabelFont);
	    this.refreshTicks(g2, drawArea, drawArea);
	    tickLabelHeight = tickLabelInsets.top+tickLabelInsets.bottom+
		getMaxTickLabelHeight(g2, drawArea, this.verticalCategoryLabels);
	}
	return new Rectangle2D.Double(drawArea.getX(),
                                      drawArea.getMaxY()-labelHeight-tickLabelHeight,
				      drawArea.getWidth()-reservedWidth,
                                      labelHeight+tickLabelHeight);
    }

    /**
     * A utility method for determining the height of the tallest tick label.
     */
    private double getMaxTickLabelHeight(Graphics2D g2, Rectangle2D drawArea, boolean vertical) {
	Font font = getTickLabelFont();
	g2.setFont(font);
	FontRenderContext frc = g2.getFontRenderContext();
	double maxHeight = 0.0;
	if (vertical) {
	    Iterator iterator = this.ticks.iterator();
	    while (iterator.hasNext()) {
		Tick tick = (Tick)iterator.next();
		Rectangle2D labelBounds = font.getStringBounds(tick.getText(), frc);
		if (labelBounds.getWidth()>maxHeight) {
		    maxHeight = labelBounds.getWidth();
		}
	    }
	}
	else {
	    LineMetrics metrics = font.getLineMetrics("Sample", frc);
	    maxHeight = metrics.getHeight();
	}
	return maxHeight;
    }

    /**
     * Returns true if the specified plot is compatible with the axis, and false otherwise.
     * @param plot The plot;
     */
    protected boolean isCompatiblePlot(Plot plot) {
        if (plot instanceof CategoryPlot) return true;
        else return false;
    }

    /**
     * Configures the axis against the current plot.  Nothing required in this class.
     */
    public void configure() {
    }

}
