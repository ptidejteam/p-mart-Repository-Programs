/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
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
 * ---------------
 * NumberAxis.java
 * ---------------
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Laurence Vanhelsuwe;
 *
 * $Id: NumberAxis.java,v 1.1 2007/10/10 19:05:11 vauchers Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 22-Sep-2001 : Changed setMinimumAxisValue(...) and setMaximumAxisValue(...) so that they
 *               clear the autoRange flag (DG);
 * 27-Nov-2001 : Removed old, redundant code (DG);
 * 30-Nov-2001 : Added accessor methods for the standard tick units (DG);
 * 08-Jan-2002 : Added setAxisRange(...) method (since renamed setRange(...)) (DG);
 * 16-Jan-2002 : Added setTickUnit(...) method.  Extended ValueAxis to support an optional
 *               cross-hair (DG);
 * 08-Feb-2002 : Fixes bug to ensure the autorange is recalculated if the
 *               setAutoRangeIncludesZero flag is changed (DG);
 * 25-Feb-2002 : Added a new flag autoRangeStickyZero to provide further control over margins in
 *               the auto-range mechanism.  Updated constructors.  Updated import statements.
 *               Moved the createStandardTickUnits() method to the TickUnits class (DG);
 * 19-Apr-2002 : Updated Javadoc comments (DG);
 * 01-May-2002 : Updated for changes to TickUnit class, removed valueToString(...) method (DG);
 * 25-Jul-2002 : Moved the lower and upper margin attributes, and the auto-range minimum size, up
 *               one level to the ValueAxis class (DG);
 * 05-Sep-2002 : Updated constructor to match changes in Axis class (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 04-Oct-2002 : Moved standardTickUnits from NumberAxis --> ValueAxis (DG);
 * 24-Oct-2002 : Added a number format override (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 19-Nov-2002 : Removed grid settings (now controlled by the plot) (DG);
 * 14-Jan-2003 : Changed autoRangeMinimumSize from Number --> double, and moved crosshair settings
 *               to the plot classes (DG);
 * 20-Jan-2003 : Removed the monolithic constructor (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 16-Jul-2003 : Reworked to allow for multiple secondary axes (DG);
 *
 */

package org.jfree.chart.axis;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;

import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;

/**
 * An axis for displaying numerical data.
 * <P>
 * If the axis is set up to automatically determine its range to fit the data,
 * you can ensure that the range includes zero (statisticians usually prefer
 * this) by setting the <code>autoRangeIncludesZero</code> flag to <code>true</code>.
 * <P>
 * The <code>NumberAxis</code> class has a mechanism for automatically selecting a tick unit
 * that is appropriate for the current axis range.  This mechanism is an
 * adaptation of code suggested by Laurence Vanhelsuwe.
 *
 * @author David Gilbert
 */
public class NumberAxis extends ValueAxis implements Serializable {

    /** The default value for the autoRangeIncludesZero flag. */
    public static final boolean DEFAULT_AUTO_RANGE_INCLUDES_ZERO = true;

    /** The default value for the autoRangeStickyZero flag. */
    public static final boolean DEFAULT_AUTO_RANGE_STICKY_ZERO = true;

    /** The default tick unit. */
    public static final NumberTickUnit
        DEFAULT_TICK_UNIT = new NumberTickUnit(1.0, new DecimalFormat("0"));

    /** The default setting for the vertical tick labels flag. */
    public static final boolean DEFAULT_VERTICAL_TICK_LABELS = false;

    /**
     * A flag that affects the axis range when the range is determined
     * automatically.  If the auto range does NOT include zero and this flag
     * is TRUE, then the range is changed to include zero.
     */
    private boolean autoRangeIncludesZero;

    /**
     * A flag that affects the size of the margins added to the axis range when
     * the range is determined automatically.  If the value 0 falls within the
     * margin and this flag is TRUE, then the margin is truncated at zero.
     */
    private boolean autoRangeStickyZero;

    /** The tick unit for the axis. */
    private NumberTickUnit tickUnit;

    /** The override number format. */
    private NumberFormat numberFormatOverride;

    /** An optional band for marking regions on the axis. */
    private MarkerAxisBand markerBand;

    /**
     * Default constructor.
     */
    public NumberAxis() {
        this(null);    
    }
    
    /**
     * Constructs a number axis, using default values where necessary.
     *
     * @param label  the axis label (<code>null</code> permitted).
     */
    public NumberAxis(String label) {

        super(label, NumberAxis.createStandardTickUnits());

        this.autoRangeIncludesZero = DEFAULT_AUTO_RANGE_INCLUDES_ZERO;
        this.autoRangeStickyZero = DEFAULT_AUTO_RANGE_STICKY_ZERO;
        this.tickUnit = DEFAULT_TICK_UNIT;
        this.numberFormatOverride = null;

        this.markerBand = null;

    }
    
    /**
     * Returns the flag that indicates whether or not the automatic axis range
     * (if indeed it is determined automatically) is forced to include zero.
     *
     * @return The flag.
     */
    public boolean autoRangeIncludesZero() {
        return this.autoRangeIncludesZero;
    }

    /**
     * Sets the flag that indicates whether or not the axis range, if automatically calculated, is
     * forced to include zero.
     * <p>
     * If the flag is changed to <code>true</code>, the axis range is recalculated.
     * <p>
     * Any change to the flag will trigger an {@link AxisChangeEvent}.
     *
     * @param flag  the new value of the flag.
     */
    public void setAutoRangeIncludesZero(boolean flag) {

        if (autoRangeIncludesZero != flag) {

            this.autoRangeIncludesZero = flag;
            if (isAutoRange()) {
                autoAdjustRange();
            }
            notifyListeners(new AxisChangeEvent(this));

        }

    }

    /**
     * Returns a flag that affects the auto-range when zero falls outside the
     * data range but inside the margins defined for the axis.
     *
     * @return The flag.
     */
    public boolean autoRangeStickyZero() {
        return this.autoRangeStickyZero;
    }

    /**
     * Sets a flag that affects the auto-range when zero falls outside the data
     * range but inside the margins defined for the axis.
     *
     * @param flag  the new flag.
     */
    public void setAutoRangeStickyZero(boolean flag) {

        if (autoRangeStickyZero != flag) {

            this.autoRangeStickyZero = flag;
            if (isAutoRange()) {
                autoAdjustRange();
            }
            notifyListeners(new AxisChangeEvent(this));

        }

    }

    /**
     * Returns the tick unit for the axis.
     *
     * @return The tick unit for the axis.
     */
    public NumberTickUnit getTickUnit() {
        return this.tickUnit;
    }

    /**
     * Sets the tick unit for the axis.  This will turn off the auto tick unit selection
     * mechanism (if it is on) and send an {@link AxisChangeEvent} to all registered
     * listeners.
     *
     * @param unit  the new tick unit.
     */
    public void setTickUnit(NumberTickUnit unit) {
        setTickUnit(unit, true, true);
    }

    /**
     * Sets the tick unit for the axis.  This will turn off the auto tick unit selection
     * mechanism (if it is on) and, if requested, send an {@link AxisChangeEvent} to all
     * registered listeners.
     *
     * @param unit  the new tick unit.
     * @param notify  notify listeners?
     * @param turnOffAutoSelect  turn off the auto-tick selection?
     */
    public void setTickUnit(NumberTickUnit unit, boolean notify, boolean turnOffAutoSelect) {

        this.tickUnit = unit;
        if (turnOffAutoSelect) {
            setAutoTickUnitSelection(false, false);
        }
        if (notify) {
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the number format override.  If this is non-null, then it will be used to format
     * the numbers on the axis.
     *
     * @return The number format override.
     */
    public NumberFormat getNumberFormatOverride() {
        return this.numberFormatOverride;
    }

    /**
     * Sets the number format override.  If this is non-null, then it will be used to format
     * the numbers on the axis.
     *
     * @param formatter  the number formatter (<code>null</code> permitted).
     */
    public void setNumberFormatOverride(NumberFormat formatter) {
        this.numberFormatOverride = formatter;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the (optional) marker band for the axis.
     *
     * @return The marker band (possibly <code>null</code>).
     */
    public MarkerAxisBand getMarkerBand() {
        return this.markerBand;
    }

    /**
     * Sets the marker band for the axis.
     * <P>
     * The marker band is optional, leave it set to <code>null</code> if you don't require it.
     *
     * @param band the new band (<code>null<code> permitted).
     */
    public void setMarkerBand(MarkerAxisBand band) {
        this.markerBand = band;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns true if the specified plot is compatible with the axis.
     *
     * @param plot  the plot.
     *
     * @return <code>true</code> if the specified plot is compatible with the axis.
     */
    protected boolean isCompatiblePlot(Plot plot) {
        return (plot instanceof ValueAxisPlot);
    }

    /**
     * Configures the axis to work with the specified plot.  If the axis has
     * auto-scaling, then sets the maximum and minimum values.
     */
    public void configure() {
        if (isAutoRange()) {
            autoAdjustRange();
        }
    }

    /**
     * Rescales the axis to ensure that all data is visible.
     */
    protected void autoAdjustRange() {

        Plot plot = getPlot();
        if (plot == null) {
            return;  // no plot, no data
        }

        if (plot instanceof ValueAxisPlot) {
            ValueAxisPlot vap = (ValueAxisPlot) plot;

            Range r = vap.getDataRange(this);
            if (r == null) {
                r = new Range(DEFAULT_LOWER_BOUND, DEFAULT_UPPER_BOUND);
            }
            double upper = r.getUpperBound();
            double lower = r.getLowerBound();
            double range = upper - lower;

            // if fixed auto range, then derive lower bound...
            double fixedAutoRange = getFixedAutoRange();
            if (fixedAutoRange > 0.0) {
                lower = upper - fixedAutoRange;
            }
            else {
                // ensure the autorange is at least <minRange> in size...
                double minRange = getAutoRangeMinimumSize();
                if (range < minRange) {
                    double expand = (minRange - range) / 2;
                    upper = upper + expand;
                    lower = lower - expand;
                }


                if (autoRangeIncludesZero()) {
                    if (autoRangeStickyZero()) {
                        if (upper <= 0.0) {
                            upper = 0.0;
                        }
                        else {
                            upper = upper + getUpperMargin() * range;
                        }
                        if (lower >= 0.0) {
                            lower = 0.0;
                        }
                        else {
                            lower = lower - getLowerMargin() * range;
                        }
                    }
                    else {
                        upper = Math.max(0.0, upper + getUpperMargin() * range);
                        lower = Math.min(0.0, lower - getLowerMargin() * range);
                    }
                }
                else {
                    if (autoRangeStickyZero()) {
                        if (upper <= 0.0) {
                            upper = Math.min(0.0, upper + getUpperMargin() * range);
                        }
                        else {
                            upper = upper + getUpperMargin() * range;
                        }
                        if (lower >= 0.0) {
                            lower = Math.max(0.0, lower - getLowerMargin() * range);
                        }
                        else {
                            lower = lower - getLowerMargin() * range;
                        }
                    }
                    else {
                        upper = upper + getUpperMargin() * range;
                        lower = lower - getLowerMargin() * range;
                    }
                }
            }

            setRange(new Range(lower, upper), false, false);
        }

    }

    /**
     * Converts a data value to a coordinate in Java2D space, assuming that the
     * axis runs along one edge of the specified dataArea.
     * <p>
     * Note that it is possible for the coordinate to fall outside the plotArea.
     *
     * @param value  the data value.
     * @param dataArea  the area for plotting the data.
     * @param edge  the axis location.
     *
     * @return The Java2D coordinate.
     */
    public double translateValueToJava2D(double value,
                                         Rectangle2D dataArea, RectangleEdge edge) {

        Range range = getRange();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();

        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = dataArea.getX();
            max = dataArea.getMaxX();
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            max = dataArea.getMinY();
            min = dataArea.getMaxY();
        }
        if (isInverted()) {
            return max - ((value - axisMin) / (axisMax - axisMin)) * (max - min);
        }
        else {
            return min + ((value - axisMin) / (axisMax - axisMin)) * (max - min);
        }

    }

    /**
     * Converts a coordinate in Java2D space to the corresponding data value,
     * assuming that the axis runs along one edge of the specified dataArea.
     *
     * @param java2DValue  the coordinate in Java2D space.
     * @param dataArea  the area in which the data is plotted.
     * @param edge  the location.
     *
     * @return The data value.
     */
    public double translateJava2DtoValue(float java2DValue, Rectangle2D dataArea,
                                         RectangleEdge edge) {

        Range range = getRange();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();

        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = dataArea.getX();
            max = dataArea.getMaxX();
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            min = dataArea.getMaxY();
            max = dataArea.getY();
        }
        if (isInverted()) {
            return axisMax - (java2DValue - min) / (max - min) * (axisMax - axisMin);
        }
        else {
            return axisMin + (java2DValue - min) / (max - min) * (axisMax - axisMin);
        }

    }

    /**
     * Calculates the value of the lowest visible tick on the axis.
     *
     * @return the value of the lowest visible tick on the axis.
     */
    public double calculateLowestVisibleTickValue() {

        double unit = getTickUnit().getSize();
        double index = Math.ceil(getRange().getLowerBound() / unit);
        return index * unit;

    }

    /**
     * Calculates the value of the highest visible tick on the axis.
     *
     * @return the value of the highest visible tick on the axis.
     */
    public double calculateHighestVisibleTickValue() {

        double unit = getTickUnit().getSize();
        double index = Math.floor(getRange().getUpperBound() / unit);
        return index * unit;

    }

    /**
     * Calculates the number of visible ticks.
     *
     * @return the number of visible ticks on the axis.
     */
    public int calculateVisibleTickCount() {

        double unit = getTickUnit().getSize();
        Range range = getRange();
        return (int) (Math.floor(range.getUpperBound() / unit)
                      - Math.ceil(range.getLowerBound() / unit) + 1);


    }

    /**
     * Returns the space required to draw the axis.
     *
     * @param g2  the graphics device.
     * @param plot  a reference to the plot;
     * @param plotArea  the area within which the plot should be drawn.
     * @param edge  the axis location.
     * @param space  the space already reserved.
     *
     * @return  Space required to draw the axis.
     */
    public AxisSpace reserveSpace(Graphics2D g2, Plot plot, Rectangle2D plotArea, 
                                  RectangleEdge edge, AxisSpace space) {

        // create a new space object if one wasn't supplied...
        if (space == null) {
            space = new AxisSpace();
        }
        this.reservedForAxisLabel = 0.0;
        this.reservedForTickLabels = 0.0;
        
        // if the axis is not visible, no additional space is required...
        if (!isVisible()) {
            return space;
        }

        // if the axis has a fixed dimension, return it...
        double dimension = getFixedDimension();
        if (dimension > 0.0) {
            space.ensureAtLeast(dimension, edge);
        }

        // calculate the max size of the tick labels (if visible)...
        double tickLabelHeight = 0.0;
        double tickLabelWidth = 0.0;
        if (isTickLabelsVisible()) {
            g2.setFont(getTickLabelFont());
            refreshTicks(g2, 0.0, plotArea, plotArea, edge);
            Insets tickLabelInsets = getTickLabelInsets();
            if (RectangleEdge.isTopOrBottom(edge)) {
                tickLabelHeight = tickLabelInsets.top + tickLabelInsets.bottom;
                tickLabelHeight += getMaxTickLabelHeight(g2, plotArea, isVerticalTickLabels());
                this.reservedForTickLabels = tickLabelHeight;
            }
            else if (RectangleEdge.isLeftOrRight(edge)) {
                tickLabelWidth = tickLabelInsets.left + tickLabelInsets.right;
                tickLabelWidth += getMaxTickLabelWidth(g2, plotArea);
                this.reservedForTickLabels = tickLabelWidth;
            }
        }

        // calculate the size of the marker band (if there is one)...
        double markerBandHeight = 0.0;
        if (this.markerBand != null) {
            markerBandHeight = this.markerBand.getHeight(g2);
            //this.reserved += markerBandHeight;
        }

        // get the axis label size and update the space object...
        Rectangle2D labelEnclosure = getLabelEnclosure(g2, edge);
        double labelHeight = 0.0;
        double labelWidth = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            labelHeight = labelEnclosure.getHeight();
            this.reservedForAxisLabel = labelHeight;
            space.add(labelHeight + tickLabelHeight + markerBandHeight, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            labelWidth = labelEnclosure.getWidth();
            this.reservedForAxisLabel = labelWidth;
            space.add(labelWidth + tickLabelWidth, edge);
        }

        return space;

    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param cursor  the cursor location.
     * @param plotArea  the area within which the axes and data should be drawn.
     * @param dataArea  the area within which the data should be drawn.
     * @param edge  the location of the axis.
     * 
     * @return The width or height used by the axis.
     */
    public double draw(Graphics2D g2, double cursor,
                       Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge) {

        // if the axis is not visible, don't draw it...
        if (!isVisible()) {
            return 0.0;
        }

        // draw the tick marks and labels...
        double used1 = drawTickMarksAndLabels(g2, cursor, plotArea, dataArea, edge);
        if (edge == RectangleEdge.TOP || edge == RectangleEdge.LEFT) {
            cursor = cursor - used1;
        }
        else if (edge == RectangleEdge.BOTTOM || edge == RectangleEdge.RIGHT) {
            cursor = cursor + used1;
        }

//        // draw the marker band (if there is one)...
//        if (getMarkerBand() != null) {
//            if (edge == RectangleEdge.BOTTOM) {
//                cursor = cursor - getMarkerBand().getHeight(g2);
//            }
//            getMarkerBand().draw(g2, plotArea, dataArea, 0, cursor);
//        }
        
        // draw the axis label...
        double used2 = drawLabel(getLabel(), g2, cursor, plotArea, dataArea, edge);

        return used1 + used2;
        
    }
 
    /**
     * Draws the axis line, tick marks and tick mark labels.
     * 
     * @param g2  the graphics device.
     * @param cursor  the cursor.
     * @param plotArea  the plot area.
     * @param dataArea  the data area.
     * @param edge  the edge that the axis is aligned with.
     * 
     * @return The width or height used to draw the axis.
     */
    protected double drawTickMarksAndLabels(Graphics2D g2, double cursor,
                                            Rectangle2D plotArea,
                                            Rectangle2D dataArea, RectangleEdge edge) {
                                              
        if (isAxisLineVisible()) {
            drawAxisLine(g2, cursor, dataArea, edge);
        }
        double ol = getTickMarkOutsideLength();
        double il = getTickMarkInsideLength();
        
        refreshTicks(g2, cursor, plotArea, dataArea, edge);
        g2.setFont(getTickLabelFont());
        Iterator iterator = getTicks().iterator();
        while (iterator.hasNext()) {
            Tick tick = (Tick) iterator.next();
            float xx = (float) translateValueToJava2D(tick.getNumericalValue(), dataArea, edge);
            if (isTickLabelsVisible()) {
                g2.setPaint(getTickLabelPaint());
                if (isVerticalTickLabels()) {
                    RefineryUtilities.drawRotatedString(tick.getText(), g2,
                                                        tick.getX(), tick.getY(), -Math.PI / 2);
                }
                else {
                    g2.drawString(tick.getText(), tick.getX(), tick.getY());
                }
            }

            if (isTickMarksVisible()) {
                Line2D mark = null;
                g2.setStroke(getTickMarkStroke());
                g2.setPaint(getTickMarkPaint());
                if (edge == RectangleEdge.LEFT) {
                    mark = new Line2D.Double(cursor - ol, xx, cursor + il, xx);
                }
                else if (edge == RectangleEdge.RIGHT) {
                    mark = new Line2D.Double(cursor + ol, xx, cursor - il, xx);
                }
                else if (edge == RectangleEdge.TOP) {
                    mark = new Line2D.Double(xx, cursor - ol, xx, cursor + il);
                }
                else if (edge == RectangleEdge.BOTTOM) {
                    mark = new Line2D.Double(xx, cursor + ol, xx, cursor - il);
                }
                g2.draw(mark);
            }
        }
        return this.reservedForTickLabels;
        
    }

    /**
     * Creates the standard tick units.
     * <P>
     * If you don't like these defaults, create your own instance of TickUnits
     * and then pass it to the setStandardTickUnits(...) method in the
     * NumberAxis class.
     *
     * @return the standard tick units.
     */
    public static TickUnits createStandardTickUnits() {

        TickUnits units = new TickUnits();

        // we can add the units in any order, the TickUnits collection will sort them...
        units.add(new NumberTickUnit(0.0000001,    new DecimalFormat("0.0000000")));
        units.add(new NumberTickUnit(0.000001,     new DecimalFormat("0.000000")));
        units.add(new NumberTickUnit(0.00001,      new DecimalFormat("0.00000")));
        units.add(new NumberTickUnit(0.0001,       new DecimalFormat("0.0000")));
        units.add(new NumberTickUnit(0.001,        new DecimalFormat("0.000")));
        units.add(new NumberTickUnit(0.01,         new DecimalFormat("0.00")));
        units.add(new NumberTickUnit(0.1,          new DecimalFormat("0.0")));
        units.add(new NumberTickUnit(1,            new DecimalFormat("0")));
        units.add(new NumberTickUnit(10,           new DecimalFormat("0")));
        units.add(new NumberTickUnit(100,          new DecimalFormat("0")));
        units.add(new NumberTickUnit(1000,         new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(10000,        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(100000,       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(1000000,      new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(10000000,     new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(100000000,    new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(1000000000,   new DecimalFormat("#,###,###,##0")));
        units.add(new NumberTickUnit(10000000000.0,   new DecimalFormat("#,###,###,##0")));
        units.add(new NumberTickUnit(100000000000.0,   new DecimalFormat("#,###,###,##0")));

        units.add(new NumberTickUnit(0.00000025,   new DecimalFormat("0.00000000")));
        units.add(new NumberTickUnit(0.0000025,    new DecimalFormat("0.0000000")));
        units.add(new NumberTickUnit(0.000025,     new DecimalFormat("0.000000")));
        units.add(new NumberTickUnit(0.00025,      new DecimalFormat("0.00000")));
        units.add(new NumberTickUnit(0.0025,       new DecimalFormat("0.0000")));
        units.add(new NumberTickUnit(0.025,        new DecimalFormat("0.000")));
        units.add(new NumberTickUnit(0.25,         new DecimalFormat("0.00")));
        units.add(new NumberTickUnit(2.5,          new DecimalFormat("0.0")));
        units.add(new NumberTickUnit(25,           new DecimalFormat("0")));
        units.add(new NumberTickUnit(250,          new DecimalFormat("0")));
        units.add(new NumberTickUnit(2500,         new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(25000,        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(250000,       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(2500000,      new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(25000000,     new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(250000000,    new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(2500000000.0,   new DecimalFormat("#,###,###,##0")));
        units.add(new NumberTickUnit(25000000000.0,   new DecimalFormat("#,###,###,##0")));
        units.add(new NumberTickUnit(250000000000.0,   new DecimalFormat("#,###,###,##0")));

        units.add(new NumberTickUnit(0.0000005,    new DecimalFormat("0.0000000")));
        units.add(new NumberTickUnit(0.000005,     new DecimalFormat("0.000000")));
        units.add(new NumberTickUnit(0.00005,      new DecimalFormat("0.00000")));
        units.add(new NumberTickUnit(0.0005,       new DecimalFormat("0.0000")));
        units.add(new NumberTickUnit(0.005,        new DecimalFormat("0.000")));
        units.add(new NumberTickUnit(0.05,         new DecimalFormat("0.00")));
        units.add(new NumberTickUnit(0.5,          new DecimalFormat("0.0")));
        units.add(new NumberTickUnit(5L,           new DecimalFormat("0")));
        units.add(new NumberTickUnit(50L,          new DecimalFormat("0")));
        units.add(new NumberTickUnit(500L,         new DecimalFormat("0")));
        units.add(new NumberTickUnit(5000L,        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(50000L,       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(500000L,      new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(5000000L,     new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(50000000L,    new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(500000000L,   new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(5000000000L,  new DecimalFormat("#,###,###,##0")));
        units.add(new NumberTickUnit(50000000000L,  new DecimalFormat("#,###,###,##0")));
        units.add(new NumberTickUnit(500000000000L,  new DecimalFormat("#,###,###,##0")));

        return units;

    }

    /**
     * Returns a collection of tick units for integer values.
     *
     * @return a collection of tick units for integer values.
     */
    public static TickUnits createIntegerTickUnits() {

        TickUnits units = new TickUnits();

        units.add(new NumberTickUnit(1,              new DecimalFormat("0")));
        units.add(new NumberTickUnit(2,              new DecimalFormat("0")));
        units.add(new NumberTickUnit(5,              new DecimalFormat("0")));
        units.add(new NumberTickUnit(10,             new DecimalFormat("0")));
        units.add(new NumberTickUnit(20,             new DecimalFormat("0")));
        units.add(new NumberTickUnit(50,             new DecimalFormat("0")));
        units.add(new NumberTickUnit(100,            new DecimalFormat("0")));
        units.add(new NumberTickUnit(200,            new DecimalFormat("0")));
        units.add(new NumberTickUnit(500,            new DecimalFormat("0")));
        units.add(new NumberTickUnit(1000,           new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(2000,           new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(5000,           new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(10000,          new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(20000,          new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(50000,          new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(100000,         new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(200000,         new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(500000,         new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(1000000,        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(2000000,        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(5000000,        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(10000000,       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(20000000,       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(50000000,       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(100000000,      new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(200000000,      new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(500000000,      new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(1000000000,     new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(2000000000,     new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(5000000000.0,   new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(10000000000.0,  new DecimalFormat("#,##0")));

        return units;

    }

    /**
     * Creates a collection of standard tick units.  The supplied locale is used to create the
     * number formatter (a localised instance of <code>NumberFormat</code>).
     * <P>
     * If you don't like these defaults, create your own instance of {@link TickUnits}
     * and then pass it to the <code>setStandardTickUnits(...)</code> method.
     *
     * @param locale  the locale.
     *
     * @return a tick unit collection.
     */
    public static TickUnits createStandardTickUnits(Locale locale) {

        TickUnits units = new TickUnits();

        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);

        // we can add the units in any order, the TickUnits collection will sort them...
        units.add(new NumberTickUnit(0.0000001,    numberFormat));
        units.add(new NumberTickUnit(0.000001,     numberFormat));
        units.add(new NumberTickUnit(0.00001,      numberFormat));
        units.add(new NumberTickUnit(0.0001,       numberFormat));
        units.add(new NumberTickUnit(0.001,        numberFormat));
        units.add(new NumberTickUnit(0.01,         numberFormat));
        units.add(new NumberTickUnit(0.1,          numberFormat));
        units.add(new NumberTickUnit(1,            numberFormat));
        units.add(new NumberTickUnit(10,           numberFormat));
        units.add(new NumberTickUnit(100,          numberFormat));
        units.add(new NumberTickUnit(1000,         numberFormat));
        units.add(new NumberTickUnit(10000,        numberFormat));
        units.add(new NumberTickUnit(100000,       numberFormat));
        units.add(new NumberTickUnit(1000000,      numberFormat));
        units.add(new NumberTickUnit(10000000,     numberFormat));
        units.add(new NumberTickUnit(100000000,    numberFormat));
        units.add(new NumberTickUnit(1000000000,   numberFormat));
        units.add(new NumberTickUnit(10000000000.0,   numberFormat));

        units.add(new NumberTickUnit(0.00000025,   numberFormat));
        units.add(new NumberTickUnit(0.0000025,    numberFormat));
        units.add(new NumberTickUnit(0.000025,     numberFormat));
        units.add(new NumberTickUnit(0.00025,      numberFormat));
        units.add(new NumberTickUnit(0.0025,       numberFormat));
        units.add(new NumberTickUnit(0.025,        numberFormat));
        units.add(new NumberTickUnit(0.25,         numberFormat));
        units.add(new NumberTickUnit(2.5,          numberFormat));
        units.add(new NumberTickUnit(25,           numberFormat));
        units.add(new NumberTickUnit(250,          numberFormat));
        units.add(new NumberTickUnit(2500,         numberFormat));
        units.add(new NumberTickUnit(25000,        numberFormat));
        units.add(new NumberTickUnit(250000,       numberFormat));
        units.add(new NumberTickUnit(2500000,      numberFormat));
        units.add(new NumberTickUnit(25000000,     numberFormat));
        units.add(new NumberTickUnit(250000000,    numberFormat));
        units.add(new NumberTickUnit(2500000000.0,   numberFormat));
        units.add(new NumberTickUnit(25000000000.0,   numberFormat));

        units.add(new NumberTickUnit(0.0000005,    numberFormat));
        units.add(new NumberTickUnit(0.000005,     numberFormat));
        units.add(new NumberTickUnit(0.00005,      numberFormat));
        units.add(new NumberTickUnit(0.0005,       numberFormat));
        units.add(new NumberTickUnit(0.005,        numberFormat));
        units.add(new NumberTickUnit(0.05,         numberFormat));
        units.add(new NumberTickUnit(0.5,          numberFormat));
        units.add(new NumberTickUnit(5L,           numberFormat));
        units.add(new NumberTickUnit(50L,          numberFormat));
        units.add(new NumberTickUnit(500L,         numberFormat));
        units.add(new NumberTickUnit(5000L,        numberFormat));
        units.add(new NumberTickUnit(50000L,       numberFormat));
        units.add(new NumberTickUnit(500000L,      numberFormat));
        units.add(new NumberTickUnit(5000000L,     numberFormat));
        units.add(new NumberTickUnit(50000000L,    numberFormat));
        units.add(new NumberTickUnit(500000000L,   numberFormat));
        units.add(new NumberTickUnit(5000000000L,  numberFormat));
        units.add(new NumberTickUnit(50000000000L,  numberFormat));

        return units;

    }

    /**
     * Returns a collection of tick units for integer values.
     * Uses a given Locale to create the DecimalFormats.
     *
     * @param locale the locale to use to represent Numbers.
     *
     * @return a collection of tick units for integer values.
     */
    public static TickUnits createIntegerTickUnits(Locale locale) {

        TickUnits units = new TickUnits();

        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);

        units.add(new NumberTickUnit(1,              numberFormat));
        units.add(new NumberTickUnit(2,              numberFormat));
        units.add(new NumberTickUnit(5,              numberFormat));
        units.add(new NumberTickUnit(10,             numberFormat));
        units.add(new NumberTickUnit(20,             numberFormat));
        units.add(new NumberTickUnit(50,             numberFormat));
        units.add(new NumberTickUnit(100,            numberFormat));
        units.add(new NumberTickUnit(200,            numberFormat));
        units.add(new NumberTickUnit(500,            numberFormat));
        units.add(new NumberTickUnit(1000,           numberFormat));
        units.add(new NumberTickUnit(2000,           numberFormat));
        units.add(new NumberTickUnit(5000,           numberFormat));
        units.add(new NumberTickUnit(10000,          numberFormat));
        units.add(new NumberTickUnit(20000,          numberFormat));
        units.add(new NumberTickUnit(50000,          numberFormat));
        units.add(new NumberTickUnit(100000,         numberFormat));
        units.add(new NumberTickUnit(200000,         numberFormat));
        units.add(new NumberTickUnit(500000,         numberFormat));
        units.add(new NumberTickUnit(1000000,        numberFormat));
        units.add(new NumberTickUnit(2000000,        numberFormat));
        units.add(new NumberTickUnit(5000000,        numberFormat));
        units.add(new NumberTickUnit(10000000,       numberFormat));
        units.add(new NumberTickUnit(20000000,       numberFormat));
        units.add(new NumberTickUnit(50000000,       numberFormat));
        units.add(new NumberTickUnit(100000000,      numberFormat));
        units.add(new NumberTickUnit(200000000,      numberFormat));
        units.add(new NumberTickUnit(500000000,      numberFormat));
        units.add(new NumberTickUnit(1000000000,     numberFormat));
        units.add(new NumberTickUnit(2000000000,     numberFormat));
        units.add(new NumberTickUnit(5000000000.0,   numberFormat));
        units.add(new NumberTickUnit(10000000000.0,  numberFormat));

        return units;

    }

    /**
     * A utility method for determining the height of the tallest tick label.
     *
     * @param g2  the graphics device.
     * @param drawArea  the area within which the plot and axes should be drawn.
     * @param vertical  a flag that indicates whether or not the tick labels are 'vertical'.
     *
     * @return the height of the tallest tick label.
     */
    protected double getMaxTickLabelHeight(Graphics2D g2, Rectangle2D drawArea, boolean vertical) {
        Font font = getTickLabelFont();
        g2.setFont(font);
        FontRenderContext frc = g2.getFontRenderContext();
        double maxHeight = 0.0;
        if (vertical) {
            Iterator iterator = getTicks().iterator();
            while (iterator.hasNext()) {
                Tick tick = (Tick) iterator.next();
                Rectangle2D labelBounds = font.getStringBounds(tick.getText(), frc);
                if (labelBounds.getWidth() > maxHeight) {
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
     * Estimates the maximum tick label height.
     * 
     * @param g2  the graphics device.
     * 
     * @return The maximum height.
     */
    protected double estimateMaximumTickLabelHeight(Graphics2D g2) {

        Insets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.top + tickLabelInsets.bottom;
        
        Font tickLabelFont = getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();
        result += tickLabelFont.getLineMetrics("123", frc).getHeight();
        return result;
        
    }

    /**
     * Estimates the maximum width of the tick labels, assuming the specified tick unit is used.
     * <P>
     * Rather than computing the string bounds of every tick on the axis, we just look at two
     * values: the lower bound and the upper bound for the axis.  These two values will usually
     * be representative.
     *
     * @param g2  the graphics device.
     * @param tickUnit  the tick unit to use for calculation.
     *
     * @return the estimated maximum width of the tick labels.
     */
    protected double estimateMaximumTickLabelWidth(Graphics2D g2, TickUnit tickUnit) {

        Insets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.left + tickLabelInsets.right;

        Font tickLabelFont = getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();
        if (isVerticalTickLabels()) {
            // all tick labels have the same width (equal to the height of the font)...
            result += tickLabelFont.getStringBounds("0", frc).getHeight();
        }
        else {
            // look at lower and upper bounds...
            Range range = getRange();
            double lower = range.getLowerBound();
            double upper = range.getUpperBound();
            String lowerStr = tickUnit.valueToString(lower);
            String upperStr = tickUnit.valueToString(upper);
            double w1 = tickLabelFont.getStringBounds(lowerStr, frc).getWidth();
            double w2 = tickLabelFont.getStringBounds(upperStr, frc).getWidth();
            result += Math.max(w1, w2);
        }

        return result;

    }
    
    /**
     * Selects an appropriate tick value for the axis.  The strategy is to
     * display as many ticks as possible (selected from an array of 'standard'
     * tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param drawArea  the area in which the plot and axes should be drawn.
     * @param dataArea  the area defined by the axes.
     * @param edge  the axis location.
     */
    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D drawArea, Rectangle2D dataArea,
                                      RectangleEdge edge) {

        if (RectangleEdge.isTopOrBottom(edge)) {
            selectHorizontalAutoTickUnit(g2, drawArea, dataArea, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            selectVerticalAutoTickUnit(g2, drawArea, dataArea, edge);
        }

    }

    /**
     * Selects an appropriate tick value for the axis.  The strategy is to
     * display as many ticks as possible (selected from an array of 'standard'
     * tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param drawArea  the area in which the plot and axes should be drawn.
     * @param dataArea  the area defined by the axes.
     * @param edge  the axis location.
     */
   protected void selectHorizontalAutoTickUnit(Graphics2D g2,
                                               Rectangle2D drawArea, Rectangle2D dataArea,
                                               RectangleEdge edge) {

        double zero = translateValueToJava2D(0.0, dataArea, edge);
        double tickLabelWidth = estimateMaximumTickLabelWidth(g2, getTickUnit());

        // start with the current tick unit...
        TickUnits tickUnits = getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(getTickUnit());
        double x1 = translateValueToJava2D(unit1.getSize(), dataArea, edge);
        double unit1Width = Math.abs(x1 - zero);

        // then extrapolate...
        double guess = (tickLabelWidth / unit1Width) * unit1.getSize();

        NumberTickUnit unit2 = (NumberTickUnit) tickUnits.getCeilingTickUnit(guess);
        double x2 = translateValueToJava2D(unit2.getSize(), dataArea, edge);
        double unit2Width = Math.abs(x2 - zero);

        tickLabelWidth = estimateMaximumTickLabelWidth(g2, unit2);
        if (tickLabelWidth > unit2Width) {
            unit2 = (NumberTickUnit) tickUnits.getLargerTickUnit(unit2);
        }

        setTickUnit(unit2, false, false);

    }

    /**
     * Selects an appropriate tick value for the axis.  The strategy is to
     * display as many ticks as possible (selected from an array of 'standard'
     * tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param plotArea  the area in which the plot and axes should be drawn.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the axis location.
     */
    protected void selectVerticalAutoTickUnit(Graphics2D g2, Rectangle2D plotArea,
                                              Rectangle2D dataArea, RectangleEdge edge) {

        double zero = translateValueToJava2D(0.0, dataArea, edge);
        double tickLabelHeight = estimateMaximumTickLabelHeight(g2);

        // start with the current tick unit...
        TickUnits tickUnits = getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(getTickUnit());
        double y = translateValueToJava2D(unit1.getSize(), dataArea, edge);
        double unitHeight = Math.abs(y - zero);

        // then extrapolate...
        double guess = (tickLabelHeight / unitHeight) * unit1.getSize();
        
        NumberTickUnit unit2 = (NumberTickUnit) tickUnits.getCeilingTickUnit(guess);
        double y2 = translateValueToJava2D(unit2.getSize(), dataArea, edge);
        double unit2Height = Math.abs(y2 - zero);

        tickLabelHeight = estimateMaximumTickLabelHeight(g2);
        if (tickLabelHeight > unit2Height) {
            unit2 = (NumberTickUnit) tickUnits.getLargerTickUnit(unit2);
        }

        setTickUnit(unit2, false, false);

    }
    
    /**
     * Calculates the positions of the tick labels for the axis, storing the results in the
     * tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param cursor  the cursor.
     * @param plotArea  the area in which the plot and the axes should be drawn.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the location of the axis.
     *
     */
    public void refreshTicks(Graphics2D g2, double cursor,
                             Rectangle2D plotArea, Rectangle2D dataArea,
                             RectangleEdge edge) {

        if (RectangleEdge.isTopOrBottom(edge)) {
            refreshHorizontalTicks(g2, cursor, plotArea, dataArea, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            refreshVerticalTicks(g2, cursor, plotArea, dataArea, edge);
        }

    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the results in the
     * tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param cursor  the cursor.
     * @param plotArea  the area in which the plot (inlcuding axes) should be drawn.
     * @param dataArea  the area in which the data should be drawn.
     * @param edge  the location of the axis.
     */
    protected void refreshHorizontalTicks(Graphics2D g2, double cursor,
                                          Rectangle2D plotArea, Rectangle2D dataArea,
                                          RectangleEdge edge) {

        getTicks().clear();

        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);
        FontRenderContext frc = g2.getFontRenderContext();

        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, plotArea, dataArea, edge);
        }

        double size = getTickUnit().getSize();
        int count = calculateVisibleTickCount();
        double lowestTickValue = calculateLowestVisibleTickValue();

        if (count <= ValueAxis.MAXIMUM_TICK_COUNT) {
            for (int i = 0; i < count; i++) {
                double currentTickValue = lowestTickValue + (i * size);
                double xx = translateValueToJava2D(currentTickValue, dataArea, edge);
                String tickLabel;
                NumberFormat formatter = getNumberFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(currentTickValue);
                }
                else {
                    tickLabel = getTickUnit().valueToString(currentTickValue);
                }
                Rectangle2D tickLabelBounds = tickLabelFont.getStringBounds(tickLabel, frc);
                LineMetrics metrics = tickLabelFont.getLineMetrics(tickLabel, frc);
                float x = 0.0f;
                float y = 0.0f;
                Insets tickLabelInsets = getTickLabelInsets();
                if (isVerticalTickLabels()) {
                    x = (float) (xx + tickLabelBounds.getHeight() / 2);
                    if (edge == RectangleEdge.TOP) {
                        y = (float) (cursor - tickLabelInsets.bottom
                                            - tickLabelBounds.getWidth());
                    }
                    else {
                        y = (float) (cursor + tickLabelInsets.top
                                            + tickLabelBounds.getWidth());
                    }
                }
                else {
                    x = (float) (xx - tickLabelBounds.getWidth() / 2);
                    if (edge == RectangleEdge.TOP) {
                        y = (float) (cursor - tickLabelInsets.bottom
                                            - metrics.getLeading()
                                            - metrics.getDescent());
                    }
                    else {
                        y = (float) (cursor + tickLabelInsets.top
                                            + tickLabelBounds.getHeight());
                    }
                }
                Tick tick = new Tick(new Double(currentTickValue), tickLabel, x, y);
                getTicks().add(tick);
            }
        }

    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the results in the
     * tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param cursor  the cursor location.
     * @param plotArea  the area in which the plot and the axes should be drawn.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the location of the axis.
     *
     */
    protected void refreshVerticalTicks(Graphics2D g2, double cursor, 
                                        Rectangle2D plotArea, Rectangle2D dataArea,
                                        RectangleEdge edge) {

        getTicks().clear();

        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);

        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, plotArea, dataArea, edge);
        }

        double size = getTickUnit().getSize();
        int count = calculateVisibleTickCount();
        double lowestTickValue = calculateLowestVisibleTickValue();

        if (count <= ValueAxis.MAXIMUM_TICK_COUNT) {
            for (int i = 0; i < count; i++) {
                double currentTickValue = lowestTickValue + (i * size);
                double yy = translateValueToJava2D(currentTickValue, dataArea, edge);
                String tickLabel;
                NumberFormat formatter = getNumberFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(currentTickValue);
                }
                else {
                    tickLabel = getTickUnit().valueToString(currentTickValue);
                }
                FontRenderContext frc = g2.getFontRenderContext();
                Rectangle2D tickLabelBounds = tickLabelFont.getStringBounds(tickLabel, frc);
                LineMetrics lm = tickLabelFont.getLineMetrics(tickLabel, frc);
                float x;
                if (edge == RectangleEdge.LEFT) {
                    x = (float) (cursor
                                 - tickLabelBounds.getWidth() - getTickLabelInsets().right);
                }
                else {
                    x = (float) (cursor + getTickLabelInsets().left);
                }
                float y = (float) (yy + (lm.getAscent() / 2));
                Tick tick = new Tick(new Double(currentTickValue), tickLabel, x, y);
                getTicks().add(tick);
            }
        }

    }

}
