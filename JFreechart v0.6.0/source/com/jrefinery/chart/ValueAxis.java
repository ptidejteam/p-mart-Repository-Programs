/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: ValueAxis.java,v 1.1 2007/10/10 18:53:20 vauchers Exp $
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
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 23-Nov-2001 : Overhauled standard tick unit code (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.*;
import javax.swing.*;
import com.jrefinery.chart.event.*;

/**
 * The base class for axes that display value data (a "value" can be a Number or a Date).
 */
public abstract class ValueAxis extends Axis {

    /** The default grid line stroke. */
    public static final Stroke DEFAULT_GRID_LINE_STROKE = new BasicStroke(0.5f,
                                                                          BasicStroke.CAP_BUTT,
					                                  BasicStroke.JOIN_BEVEL,
                                                                          0.0f,
					                                  new float[] {2.0f, 2.0f},
                                                                          0.0f);

    /** The default grid line paint. */
    public static final Paint DEFAULT_GRID_LINE_PAINT = Color.gray;

    /** Flag that indicates whether or not the axis automatically scales to fit the chart data. */
    protected boolean autoRange;

    /** Flag that indicates whether or not the tick unit is selected automatically. */
    protected boolean autoTickUnit;

    /** An index into an array of standard tick values; */
    protected int autoTickIndex;

    /** Flag that indicates whether or not grid lines are showing for this axis. */
    protected boolean showGridLines;

    /** The stroke used to draw grid lines. */
    protected Stroke gridStroke;

    /** The paint used to draw grid lines. */
    protected Paint gridPaint;

    /**
     * Constructs a value axis.
     * @param label The axis label.
     * @param labelFont The font for displaying the axis label.
     * @param labelPaint The paint used to draw the axis label.
     * @param labelInsets Determines the amount of blank space around the label.
     * @param tickLabelsVisible Flag indicating whether or not the tick labels are visible.
     * @param tickLabelFont The font used to display tick labels.
     * @param tickLabelPaint The paint used to draw tick labels.
     * @param tickLabelInsets Determines the amount of blank space around tick labels.
     * @param tickMarksVisible Flag indicating whether or not the tick marks are visible.
     * @param tickMarkStroke The stroke used to draw tick marks (if visible).
     * @param autoRange Flag indicating whether or not the axis range is automatically adjusted to
     *                  fit the data.
     * @param autoTickUnit A flag indicating whether or not the tick unit is automatically
     *                     selected.
     * @param showGridLines Flag indicating whether or not grid lines are visible for this axis.
     * @param gridStroke The Stroke used to display grid lines (if visible).
     * @param gridPaint The Paint used to display grid lines (if visible).
     */
    public ValueAxis(String label, Font labelFont, Paint labelPaint, Insets labelInsets,
		     boolean tickLabelsVisible,
                     Font tickLabelFont, Paint tickLabelPaint, Insets tickLabelInsets,
		     boolean tickMarksVisible, Stroke tickMarkStroke,
		     boolean autoRange, boolean autoTickUnit,
		     boolean showGridLines, Stroke gridStroke, Paint gridPaint) {

	super(label, labelFont, labelPaint, labelInsets,
	      tickLabelsVisible, tickLabelFont, tickLabelPaint, tickLabelInsets,
	      tickMarksVisible, tickMarkStroke);

	this.autoRange = autoRange;
	this.autoTickUnit = autoTickUnit;
	this.showGridLines = showGridLines;
	this.gridStroke = gridStroke;
	this.gridPaint = gridPaint;

    }

    /**
     * Constructs a value axis.
     * @param label The axis label.
     */
    public ValueAxis(String label) {
	super(label);
	this.autoRange = true;
	this.autoTickUnit = true;
	this.showGridLines = true;
	this.gridStroke = ValueAxis.DEFAULT_GRID_LINE_STROKE;
        this.gridPaint = Color.gray;
    }

    /**
     * Returns true if the axis range is automatically adjusted to fit the data, and false
     * otherwise.
     */
    public boolean isAutoRange() {
	return autoRange;
    }

    /**
     * Sets a flag that determines whether or not the axis range is automatically adjusted to fit
     * the data, and notifies registered listeners that the axis has been modified.
     * @param auto Flag indicating whether or not the axis is automatically scaled to fit the data.
     */
    public void setAutoRange(boolean auto) {
	if (this.autoRange!=auto) {
	    this.autoRange=auto;
	    if (autoRange) autoAdjustRange();
	    notifyListeners(new AxisChangeEvent(this));
	}
    }

    /**
     * Returns A flag indicating whether or not the tick unit is automatically selected from a
     * range of standard tick units.
     * @return A flag indicating whether or not the tick unit is automatically selected.
     */
    public boolean isAutoTickUnit() {
	return autoTickUnit;
    }

    /**
     * Sets a flag indicating whether or not the tick unit is automatically selected from a
     * range of standard tick units.
     * <P>
     * Registered listeners are notified of a change to the axis.
     * @param flag The new value of the flag.
     */
    public void setAutoTickValue(boolean flag) {

        if (this.autoTickUnit!=flag) {
            this.autoTickUnit = flag;
	    notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns true if the grid lines are visible for this axis, and false otherwise.
     */
    public boolean isShowGridLines() {
	return showGridLines;
    }

    /**
     * Sets the visibility of the grid lines and notifies registered listeners that the axis has
     * been modified.
     * @param show The new setting.
     */
    public void setShowGridLines(boolean show) {
	showGridLines = show;
	notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the Stroke used to draw the grid lines (if visible).
     */
    public Stroke getGridStroke() {
	return gridStroke;
    }

    /**
     * Sets the Stroke used to draw the grid lines (if visible) and notifies registered listeners
     * that the axis has been modified.
     * @param stroke The new grid line stroke.
     */
    public void setGridStroke(Stroke stroke) {
	gridStroke = stroke;
	notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the Paint used to color the grid lines (if visible).
     */
    public Paint getGridPaint() {
	return gridPaint;
    }

    /**
     * Sets the Paint used to color the grid lines (if visible) and notifies registered listeners
     * that the axis has been modified.
     * @param paint The new grid paint.
     */
    public void setGridPaint(Paint paint) {
	gridPaint = paint;
	notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Automatically determines the maximum and minimum values on the axis to 'fit' the data.
     */
    public abstract void autoAdjustRange();

    /**
     * Converts a value from the data source to a Java2D user-space co-ordinate relative to the
     * specified plotArea.  The coordinate will be an x-value for horizontal axes and a y-value
     * for vertical axes (refer to the subclass).
     * <p>
     * Note that it is possible for the coordinate to fall outside the plotArea.
     */
    public abstract double translatedValue(Number dataValue, Rectangle2D plotArea);

}
