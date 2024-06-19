/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: VerticalCategoryAxis.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * (C) Copyright 2000, 2001, Simba Management Limited;
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
 * Change History: (from 23-Jun-2001)
 * ----------------------------------
 * 23-Jun-2001 : Modified to work with null data source (DG);
 * 18-Sep-2001 : Updated e-mail address and fixed DOS encoding problem (DG);
 * 16-Oct-2001 : Moved data source classes to com.jrefinery.data.* (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.util.*;
import com.jrefinery.data.*;

/**
 * An axis that displays categories.  Used for bar charts and line charts.
 * <P>
 * The axis needs to rely on the plot for placement of labels, since the plot controls how the
 * categories are distributed.
 */
public class VerticalCategoryAxis extends CategoryAxis implements VerticalAxis {

    /** A flag that indicates whether or not the axis label should be drawn vertically. */
    protected boolean verticalLabel;

    /**
     * Full constructor: returns a new VerticalCategoryAxis with attributes as specified by the
     * caller.
     * @param label The axis label;
     * @param labelFont The font for displaying the axis label;
     * @param labelPaint The paint used to draw the axis label;
     * @param labelInsets Determines the amount of blank space around the label;
     * @param verticalLabel Flag indicating whether or not the axis label is drawn vertically;
     * @param showCategoryLabels Flag indicating whether or not category (tick) labels are visible;
     * @param categoryLabelFont The font used to display category (tick) labels;
     * @param categoryLabelPaint The paint used to draw category (tick) labels;
     * @param showTickMarks Flag indicating whether or not tick marks are visible;
     * @param tickMarkStroke The stroke used to draw tick marks (if visible).
     */
    public VerticalCategoryAxis(String label, Font labelFont,
				Paint labelPaint, Insets labelInsets,
				boolean verticalLabel,
				boolean showTickLabels, Font tickLabelFont, Paint tickLabelPaint, Insets tickLabelInsets,
				boolean showTickMarks, Stroke tickMarkStroke) {

	super(label, labelFont, labelPaint, labelInsets,
	      showTickLabels, tickLabelFont, tickLabelPaint, tickLabelInsets,
	      showTickMarks, tickMarkStroke);

	this.verticalLabel = verticalLabel;

    }

    /**
     * Standard constructor - builds a VerticalCategoryAxis with mostly default attributes.
     * @param label The axis label;
     */
    public VerticalCategoryAxis(String label) {
	super(label);
	this.verticalLabel = true;
    }

    /**
     * Draws the CategoryAxis on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device;
     * @param drawArea The area within which the axis should be drawn;
     * @param plotArea The area within which the plot is being drawn.
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

	// draw the axis label
	g2.setFont(labelFont);
	g2.setPaint(labelPaint);
	FontRenderContext frc = g2.getFontRenderContext();
	LineMetrics metrics = labelFont.getLineMetrics(label, frc);
	Rectangle2D labelBounds = labelFont.getStringBounds(label, frc);
	if (verticalLabel) {
	    double xx = drawArea.getX()+labelInsets.left+metrics.getHeight()-metrics.getDescent()
		-metrics.getLeading();
	    double yy = plotArea.getY()+plotArea.getHeight()/2+(labelBounds.getWidth()/2);
	    drawVerticalString(label, g2, (float)xx, (float)yy);
	}
	else {
	    double xx = drawArea.getX()+labelInsets.left;
	    double yy = drawArea.getY()+drawArea.getHeight()/2-labelBounds.getHeight()/2;
	    g2.drawString(label, (float)xx, (float)yy);
	}

	// draw the category labels
	if (this.tickLabelsVisible) {
	    g2.setFont(tickLabelFont);
	    g2.setPaint(tickLabelPaint);
	    this.refreshTicks(g2, drawArea, plotArea);
	    Iterator iterator = ticks.iterator();
	    while (iterator.hasNext()) {
		Tick tick = (Tick)iterator.next();
		g2.drawString(tick.getText(), tick.getX(), tick.getY());
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
            Font font = this.getTickLabelFont();
	    g2.setFont(font);
	    FontRenderContext frc = g2.getFontRenderContext();
	    int categoryIndex = 0;
	    Iterator iterator = categoryPlot.getDataset().getCategories().iterator();
            while (iterator.hasNext()) {
	        Object category = iterator.next();
	        String label = category.toString();
	        Rectangle2D labelBounds = font.getStringBounds(label, frc);
	        LineMetrics metrics = font.getLineMetrics(label, frc);
	        float xx = (float)(plotArea.getX()-tickLabelInsets.right-labelBounds.getWidth());
	        float yy = (float)(categoryPlot.getCategoryCoordinate(categoryIndex, plotArea)+
			       labelBounds.getHeight()/2);
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
     * Supports the HorizontalAxisLead interface.
     * @param g2 The graphics device (used to obtain font information);
     * @param drawArea The area within which the axis should be drawn;
     * @param plot The plot that the axis belongs to.
     */
    public double reserveWidth(Graphics2D g2, Plot plot, Rectangle2D drawArea) {

	// calculate the width of the axis label...
	Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
	double labelWidth = this.labelInsets.left+labelInsets.right;
	if (this.verticalLabel) {
	    labelWidth = labelWidth + labelBounds.getHeight();  // assume width == height before rotation
	}
	else {
	    labelWidth = labelWidth + labelBounds.getWidth();
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
     * Returns the area required to draw the axis in the specified draw area.
     * @param g2 The graphics device;
     * @param drawArea The area within which the plot should be drawn;
     * @param reservedHeight The height reserved by the horizontal axis.
     */
    public Rectangle2D reserveAxisArea(Graphics2D g2, Plot plot, Rectangle2D drawArea,
				       double reservedHeight) {
	// calculate the width of the axis label...
	Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
	double labelWidth = this.labelInsets.left+labelInsets.right;
	if (this.verticalLabel) {
	    labelWidth = labelWidth + labelBounds.getHeight();  // assume width == height before rotation
	}
	else {
	    labelWidth = labelWidth + labelBounds.getWidth();
	}

	// calculate the width required for the tick labels (if visible);
	double tickLabelWidth = tickLabelInsets.left+tickLabelInsets.right;
	if (tickLabelsVisible) {
	    this.refreshTicks(g2, drawArea, drawArea);
	    tickLabelWidth = tickLabelWidth+getMaxTickLabelWidth(g2, drawArea);
	}

	return new Rectangle2D.Double(drawArea.getX(), drawArea.getY(), labelWidth+tickLabelWidth,
				      drawArea.getHeight()-reservedHeight);
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
