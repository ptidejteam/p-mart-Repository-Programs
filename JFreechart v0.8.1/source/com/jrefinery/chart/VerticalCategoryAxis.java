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
 * -------------------------
 * VerticalCategoryAxis.java
 * -------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: VerticalCategoryAxis.java,v 1.1 2007/10/10 19:02:36 vauchers Exp $
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 23-Jun-2001 : Modified to work with null data source (DG);
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 16-Oct-2001 : Moved data source classes to com.jrefinery.data.* (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Jan-2002 : Changed the positioning of category labels to improve centering on bars (DG);
 *               Fixed bugs causing exceptions when axis label is null (DG);
 * 06-Mar-2002 : Added accessor methods for verticalLabel attribute. Updated import statements (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.chart.event.AxisChangeEvent;

/**
 * A vertical axis that displays categories, used for horizontal bar charts.
 * <P>
 * The axis needs to rely on the plot for placement of labels, since the plot controls how the
 * categories are distributed.
 */
public class VerticalCategoryAxis extends CategoryAxis implements VerticalAxis {

    /** The default setting for vertical axis label. */
    public static final boolean DEFAULT_VERTICAL_LABEL = true;

    /** A flag that indicates whether or not the axis label should be drawn vertically. */
    protected boolean verticalLabel;

    /**
     * Constructs a new axis, using default attributes where necessary.
     *
     * @param label The axis label.
     */
    public VerticalCategoryAxis(String label) {

        this(label,
             Axis.DEFAULT_AXIS_LABEL_FONT,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             VerticalCategoryAxis.DEFAULT_VERTICAL_LABEL,
             true, // category labels visible
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             false, // tick marks visible
             Axis.DEFAULT_TICK_STROKE);

    }

    /**
     * Constructs a new axis.
     *
     * @param label The axis label.
     * @param labelFont The font for displaying the axis label.
     * @param labelPaint The paint used to draw the axis label.
     * @param labelInsets Determines the amount of blank space around the label.
     * @param verticalLabel Flag indicating whether or not the axis label is drawn vertically.
     * @param categoryLabelsVisible Flag indicating whether or not category labels are visible.
     * @param categoryLabelFont The font used to display category labels.
     * @param categoryLabelPaint The paint used to draw category labels.
     * @param tickMarksVisible Flag indicating whether or not tick marks are visible.
     * @param tickMarkStroke The stroke used to draw tick marks (if visible).
     */
    public VerticalCategoryAxis(String label,
                                Font labelFont, Paint labelPaint, Insets labelInsets,
				boolean verticalLabel,
				boolean categoryLabelsVisible,
                                Font categoryLabelFont, Paint categoryLabelPaint,
                                Insets categoryLabelInsets,
				boolean tickMarksVisible, Stroke tickMarkStroke) {

	super(label, labelFont, labelPaint, labelInsets,
	      categoryLabelsVisible, categoryLabelFont, categoryLabelPaint, categoryLabelInsets,
	      tickMarksVisible, tickMarkStroke);

	this.verticalLabel = verticalLabel;

    }

    /**
     * Returns a flag indicating whether or not the axis label is drawn vertically.
     */
    public boolean isVerticalLabel() {
        return this.verticalLabel;
    }

    /**
     * Sets a flag indicating whether or not the axis label is drawn vertically.  If the setting
     * is changed, registered listeners are notified that the axis has changed.
     */
    public void setVerticalLabel(boolean flag) {

    	if (this.verticalLabel!=flag) {
	    this.verticalLabel = flag;
	    notifyListeners(new AxisChangeEvent(this));
	}

    }

    /**
     * Draws the CategoryAxis on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device.
     * @param drawArea The area within which the axis should be drawn.
     * @param plotArea The area within which the plot is being drawn.
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

	// draw the axis label
	if (label!=null) {
            g2.setFont(labelFont);
	    g2.setPaint(labelPaint);
	    FontRenderContext frc = g2.getFontRenderContext();
	    LineMetrics metrics = labelFont.getLineMetrics(label, frc);
	    Rectangle2D labelBounds = labelFont.getStringBounds(label, frc);
	    if (verticalLabel) {
	        double xx = drawArea.getX()+labelInsets.left
                                           +metrics.getHeight()
                                           -metrics.getDescent()
		                           -metrics.getLeading();
	        double yy = plotArea.getY()+plotArea.getHeight()/2
                                           +(labelBounds.getWidth()/2);
	        drawVerticalString(label, g2, (float)xx, (float)yy);
	    }
	    else {
	        double xx = drawArea.getX()+labelInsets.left;
	        double yy = drawArea.getY()+drawArea.getHeight()/2-labelBounds.getHeight()/2;
	        g2.drawString(label, (float)xx, (float)yy);
	    }
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
     * @param g2 The graphics device (used to get font measurements).
     * @param drawArea The area where the plot and axes will be drawn.
     * @param plotArea The area inside the axes.
     */
    public void refreshTicks(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

	this.ticks.clear();
	CategoryPlot categoryPlot = (CategoryPlot)plot;
        CategoryDataset data = (CategoryDataset)plot.getChart().getDataset();
        if (data!=null) {
            Font font = this.getTickLabelFont();
	    g2.setFont(font);
	    FontRenderContext frc = g2.getFontRenderContext();
	    int categoryIndex = 0;
	    Iterator iterator = data.getCategories().iterator();
            while (iterator.hasNext()) {
	        Object category = iterator.next();
	        String label = category.toString();
	        Rectangle2D labelBounds = font.getStringBounds(label, frc);
	        LineMetrics metrics = font.getLineMetrics(label, frc);

	        float xx = (float)(plotArea.getX()-tickLabelInsets.right-labelBounds.getWidth());
	        float yy = (float)(categoryPlot.getCategoryCoordinate(categoryIndex, plotArea)
                                  -metrics.getStrikethroughOffset()+0.5f);
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
     * @param g2 The graphics device (used to obtain font information).
     * @param drawArea The area within which the axis should be drawn.
     * @param plot The plot that the axis belongs to.
     */
    public double reserveWidth(Graphics2D g2, Plot plot, Rectangle2D drawArea) {

	// calculate the width of the axis label...
        double labelWidth = 0.0;
        if (label!=null) {
	    Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
	    labelWidth = this.labelInsets.left+labelInsets.right;
	    if (this.verticalLabel) {
	        // assume width == height before rotation
                labelWidth = labelWidth + labelBounds.getHeight();
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
     * Returns the area required to draw the axis in the specified draw area.
     * @param g2 The graphics device;
     * @param drawArea The area within which the plot should be drawn;
     * @param reservedHeight The height reserved by the horizontal axis.
     */
    public Rectangle2D reserveAxisArea(Graphics2D g2, Plot plot, Rectangle2D drawArea,
				       double reservedHeight) {

        // calculate the width of the axis label...
        double labelWidth = 0.0;
        if (label!=null) {
	    Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
	    labelWidth = this.labelInsets.left+labelInsets.right;
	    if (this.verticalLabel) {
                // assume width == height before rotation
	        labelWidth = labelWidth + labelBounds.getHeight();
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

	return new Rectangle2D.Double(drawArea.getX(), drawArea.getY(), labelWidth+tickLabelWidth,
				      drawArea.getHeight()-reservedHeight);
    }

    /**
     * Returns true if the specified plot is compatible with the axis, and false otherwise.
     * @param plot The plot;
     */
    protected boolean isCompatiblePlot(Plot plot) {

        if (plot instanceof HorizontalCategoryPlot) return true;
        else return false;

    }

    /**
     * Configures the axis against the current plot.  Nothing required in this class.
     */
    public void configure() {
    }

}
