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
 * ---------------
 * NumberAxis.java
 * ---------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Laurence Vanhelsuwe;
 *
 * $Id: NumberAxis.java,v 1.1 2007/10/10 19:01:18 vauchers Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 22-Sep-2001 : Changed setMinimumAxisValue(...) and setMaximumAxisValue(...) so that they
 *               clear the autoRange flag (DG);
 * 27-Nov-2001 : Removed old, redundant code (DG);
 * 30-Nov-2001 : Added accessor methods for the standard tick units (DG);
 * 08-Jan-2002 : Added setAxisRange(...) method (DG);
 * 16-Jan-2002 : Added setTickUnit(...) method.  Extended ValueAxis to support an optional
 *               cross-hair (DG);
 * 08-Feb-2002 : Fixes bug to ensure the autorange is recalculated if the
 *               setAutoRangeIncludesZero flag is changed (DG);
 * 25-Feb-2002 : Added a new flag autoRangeStickyZero to provide further control over margins in
 *               the auto-range mechanism.  Updated constructors.  Updated import statements.
 *               Moved the createStandardTickUnits() method to the TickUnits class (DG);
 * 19-Apr-2002 : Updated Javadoc comments (DG);
 * 01-May-2002 : Updated for changes to TickUnit class, removed valueToString(...) method (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Insets;
import java.awt.Stroke;
import java.text.DecimalFormat;
import com.jrefinery.data.Range;
import com.jrefinery.chart.event.AxisChangeEvent;

/**
 * The base class for axes that display numerical data.
 * <P>
 * If the axis is set up to automatically determine its range to fit the data, you can ensure that
 * the range includes zero (statisticians usually prefer this) by setting the autoRangeIncludesZero
 * flag to true.
 * <P>
 * The NumberAxis class has a mechanism for automatically selecting a tick unit that is appropriate
 * for the current axis range.  This mechanism is an adaptation of code suggested by Laurence
 * Vanhelsuwe.
 *
 * @see HorizontalNumberAxis
 * @see VerticalNumberAxis
 *
 */
public abstract class NumberAxis extends ValueAxis {

    /** The default value for the autoRangeIncludesZero flag. */
    public static final boolean DEFAULT_AUTO_RANGE_INCLUDES_ZERO = true;

    /** The default value for the autoRangeStickyZero flag. */
    public static final boolean DEFAULT_AUTO_RANGE_STICKY_ZERO = true;

    /** The default value for the upper margin (0.05 = 5%). */
    public static final double DEFAULT_UPPER_MARGIN = 0.05;

    /** The default value for the lower margin (0.05 = 5%). */
    public static final double DEFAULT_LOWER_MARGIN = 0.05;

    /** The default minimum auto range. */
    public static final Number DEFAULT_MINIMUM_AUTO_RANGE = new Double(0.0000001);

    /** The default tick unit. */
    public static final NumberTickUnit DEFAULT_TICK_UNIT
                                                  = new NumberTickUnit(1.0, new DecimalFormat("0"));

    /** A flag that affects the orientation of the values on the axis. */
    protected boolean inverted;

    /**
     * A flag that affects the axis range when the range is determined automatically.  If the auto
     * range does NOT include zero and this flag is TRUE, then the range is changed to include zero.
     */
    protected boolean autoRangeIncludesZero;

    /**
     * A flag that affects the size of the margins added to the axis range when the range is
     * determined automatically.  If the value 0 falls within the margin and this flag is TRUE,
     * then the margin is truncated at zero.
     */
    protected boolean autoRangeStickyZero;

    /** The minimum size of a range that is determined automatically. */
    protected Number autoRangeMinimumSize;

    /**
     * The upper margin.  This is a percentage that indicates the amount by which the maximum
     * axis value exceeds the maximum data value when the axis range is determined automatically.
     */
    protected double upperMargin;

    /**
     * The lower margin.  This is a percentage that indicates the amount by which the minimum
     * axis value is "less than" the minimum data value when the axis range is determined
     * automatically.
     */
    protected double lowerMargin;

    /** The tick unit for the axis. */
    protected NumberTickUnit tickUnit;

    /** The standard tick units for the axis. */
    protected TickUnits standardTickUnits;

    /**
     * Constructs a number axis, using default values where necessary.
     *
     * @param label The axis label.
     */
    protected NumberAxis(String label) {

        this(label,
             Axis.DEFAULT_AXIS_LABEL_FONT,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             Axis.DEFAULT_AXIS_LABEL_INSETS,
             true, // tick labels visible
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             true, // tick marks visible
             Axis.DEFAULT_TICK_STROKE,
             ValueAxis.DEFAULT_AUTO_RANGE,
             NumberAxis.DEFAULT_AUTO_RANGE_INCLUDES_ZERO,
             NumberAxis.DEFAULT_AUTO_RANGE_STICKY_ZERO,
             NumberAxis.DEFAULT_MINIMUM_AUTO_RANGE,
             ValueAxis.DEFAULT_MINIMUM_AXIS_VALUE,
             ValueAxis.DEFAULT_MAXIMUM_AXIS_VALUE,
             false, // inverted
             true, // auto tick unit
             NumberAxis.DEFAULT_TICK_UNIT,
             true, // grid lines visible
             ValueAxis.DEFAULT_GRID_LINE_STROKE,
             ValueAxis.DEFAULT_GRID_LINE_PAINT,
             ValueAxis.DEFAULT_CROSSHAIR_VISIBLE,
             0.0,  // crosshair value
             ValueAxis.DEFAULT_CROSSHAIR_STROKE,
             ValueAxis.DEFAULT_CROSSHAIR_PAINT);

    }

    /**
     * Constructs a number axis.
     *
     * @param label The axis label.
     * @param labelFont The font for displaying the axis label.
     * @param labelPaint The paint used to display the axis label.
     * @param labelInsets The amount of blank space around the axis label.
     * @param tickLabelsVisible Flag indicating whether or not the tick labels are visible.
     * @param tickLabelFont The font used to display the tick labels.
     * @param tickLabelPaint The paint used to draw the tick labels.
     * @param tickLabelInsets The amount of blank space around the tick labels.
     * @param tickMarksVisible Flag indicating whether or not tick marks are visible;
     * @param tickMarkStroke The stroke used to draw the tick marks (if visible);
     * @param autoRange Flag indicating whether or not the axis range is automatically determined.
     * @param autoRangeIncludesZero A flag indicating whether the auto range must include zero.
     * @param autoRangeStickyZero A flag controlling the axis margins around zero.
     * @param autoRangeMinimumSize The minimum size for the auto range.
     * @param minimumAxisValue The lowest value shown on the axis.
     * @param maximumAxisValue The highest value shown on the axis.
     * @param inverted A flag indicating whether the axis is normal or inverted (inverted means
     *                 running from positive to negative).
     * @param autoTickUnitSelection A flag indicating whether or not the tick value is automatically
     *                              selected.
     * @param tickUnit The tick unit for the axis.
     * @param gridLinesVisible Flag indicating whether or not grid lines are visible.
     * @param gridStroke The pen/brush used to display grid lines (if visible).
     * @param gridPaint The color used to display grid lines (if visible).
     * @param crosshairValue The value at which to draw the crosshair line (null permitted).
     * @param crosshairStroke The pen/brush used to draw the data line.
     * @param crosshairPaint The color used to draw the data line.
     */
    protected NumberAxis(String label, Font labelFont, Paint labelPaint, Insets labelInsets,
                         boolean tickLabelsVisible, Font tickLabelFont, Paint tickLabelPaint,
                         Insets tickLabelInsets,
                         boolean tickMarksVisible, Stroke tickMarkStroke,
                         boolean autoRange,
                         boolean autoRangeIncludesZero, boolean autoRangeStickyZero,
                         Number autoRangeMinimumSize,
                         double minimumAxisValue, double maximumAxisValue,
                         boolean inverted,
                         boolean autoTickUnitSelection, NumberTickUnit tickUnit,
                         boolean gridLinesVisible, Stroke gridStroke, Paint gridPaint,
                         boolean crosshairVisible, double crosshairValue,
                         Stroke crosshairStroke, Paint crosshairPaint) {

        super(label,
              labelFont, labelPaint, labelInsets,
              tickLabelsVisible,
              tickLabelFont, tickLabelPaint, tickLabelInsets,
              tickMarksVisible, tickMarkStroke,
              autoRange, autoTickUnitSelection,
              gridLinesVisible, gridStroke, gridPaint,
              crosshairVisible, crosshairValue,
              crosshairStroke, crosshairPaint);

        // check arguments...
        if (minimumAxisValue>=maximumAxisValue) {
            throw new IllegalArgumentException("NumberAxis(...): minimum axis value must be less "
                                               +"than maximum axis value.");
        }

        if (autoRangeMinimumSize==null) {
            throw new IllegalArgumentException("NumberAxis(...): autoRangeMinimum cannot be null.");
        }

        // do the initialisation...
        this.autoRangeIncludesZero = autoRangeIncludesZero;
        this.autoRangeStickyZero = autoRangeStickyZero;
        this.autoRangeMinimumSize = autoRangeMinimumSize;

        this.range = new Range(minimumAxisValue, maximumAxisValue);
        this.anchorValue = 0.0;

        this.inverted = inverted;

        this.tickUnit = tickUnit;

        this.upperMargin = DEFAULT_UPPER_MARGIN;
        this.lowerMargin = DEFAULT_LOWER_MARGIN;

        this.standardTickUnits = TickUnits.createStandardTickUnits();

    }

    /**
     * Returns a flag that controls the direction of values on the axis.
     * <P>
     * For a regular axis, values increase from left to right (for a horizontal axis) and bottom
     * to top (for a vertical axis).  When the axis is 'inverted', the values increase in the
     * opposite direction.
     *
     * @return The flag.
     */
    public boolean isInverted() {
        return this.inverted;
    }

    /**
     * Sets a flag that controls the direction of values on the axis, and notifies registered
     * listeners that the axis has changed.
     *
     * @param flag The flag.
     */
    public void setInverted(boolean flag) {

        if (this.inverted!=flag) {
            this.inverted = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the flag that indicates whether or not the automatic axis range (if indeed it is
     * determined automatically) is forced to include zero.
     *
     * @return The flag.
     */
    public boolean autoRangeIncludesZero() {
        return this.autoRangeIncludesZero;
    }

    /**
     * Sets the flag that indicates whether or not the automatic axis range is forced to include
     * zero.
     *
     * @param flag The new value of the flag.
     */
    public void setAutoRangeIncludesZero(boolean flag) {

        if (autoRangeIncludesZero!=flag) {

            this.autoRangeIncludesZero = flag;
            if (this.autoRange) autoAdjustRange();
            notifyListeners(new AxisChangeEvent(this));

        }

    }

    /**
     * Returns a flag that affects the auto-range when zero falls outside the data range but
     * inside the margins defined for the axis.
     *
     * @return The flag.
     */
    public boolean autoRangeStickyZero() {
        return this.autoRangeStickyZero;
    }

    /**
     * Sets a flag that affects the auto-range when zero falls outside the data range but
     * inside the margins defined for the axis.
     *
     * @param flag The new flag.
     */
    public void setAutoRangeStickyZero(boolean flag) {

        if (autoRangeStickyZero!=flag) {

            this.autoRangeStickyZero = flag;
            if (this.autoRange) autoAdjustRange();
            notifyListeners(new AxisChangeEvent(this));

        }

    }

    /**
     * Returns the minimum size of the automatic axis range (if indeed it is determined
     * automatically).
     *
     * @return The minimum range.
     */
    public Number getAutoRangeMinimumSize() {
        return this.autoRangeMinimumSize;
    }

    /**
     * Sets the minimum size of the automatic axis range.
     *
     * @param minimum The new minimum.
     */
    public void setAutoRangeMinimumSize(Number size) {

        // check argument...
        if (size==null) {
            throw new IllegalArgumentException("NumberAxis.setAutoRangeMinimumSize(Number): "
                                               +"null not permitted.");
        }

        // make the change...
        if (autoRangeMinimumSize.doubleValue()!=size.doubleValue()) {
            this.autoRangeMinimumSize = size;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the margin (as a percentage of the range) by which the maximum axis value exceeds
     * the maximum data value.
     *
     * @return The upper margin.
     */
    public double getUpperMargin() {
        return this.upperMargin;
    }

    /**
     * Sets the upper margin.
     *
     * @param margin The new margin.
     */
    public void setUpperMargin(double margin) {
        this.upperMargin = margin;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the margin (as a percentage of the range) by which the minimum axis value is less
     * than the minimum data value.
     *
     * @return The lower margin.
     */
    public double getLowerMargin() {
        return this.lowerMargin;
    }

    /**
     * Sets the lower margin.
     *
     * @param margin The new margin.
     */
    public void setLowerMargin(double margin) {
        this.lowerMargin = margin;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the standard tick units for the axis.
     * <P>
     * If autoTickUnitSelection is on, the tick unit for the axis will be automatically selected
     * from this collection.
     *
     * @return The standard tick units.
     */
    public TickUnits getStandardTickUnits() {
        return this.standardTickUnits;
    }

    /**
     * Sets the collection of tick units for the axis, and notifies registered listeners that the
     * axis has changed.
     * <P>
     * If the autoTickUnitSelection flag is true, a tick unit will be selected from this collection
     * automatically (to ensure that labels do not overlap).
     *
     * @param collection The tick unit collection.
     */
    public void setStandardTickUnits(TickUnits collection) {

        this.standardTickUnits = collection;
        notifyListeners(new AxisChangeEvent(this));

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
     * Sets a fixed tick unit for the axis, and notifies registered listeners that the axis has
     * been changed.
     * <P>
     * This method also sets the autoTickUnitSelection flag to false.
     *
     * @param unit The new tick unit.
     */
    public void setTickUnit(NumberTickUnit unit) {

        this.autoTickUnitSelection = false;
        this.tickUnit = unit;
        notifyListeners(new AxisChangeEvent(this));

    }

    /**
     * Calculates the value of the lowest visible tick on the axis.
     *
     * @return The value of the lowest visible tick on the axis.
     */
    public double calculateLowestVisibleTickValue() {

        double unit = getTickUnit().getSize();
        double index = Math.ceil(range.getLowerBound()/unit);
        return index*unit;

    }

    /**
     * Calculates the value of the highest visible tick on the axis.
     *
     * @return The value of the highest visible tick on the axis.
     */
    public double calculateHighestVisibleTickValue() {

        double unit = getTickUnit().getSize();
        double index = Math.floor(range.getUpperBound()/unit);
        return index*unit;

    }

    /**
     * Calculates the number of visible ticks.
     *
     * @return The number of visible ticks on the axis.
     */
    public int calculateVisibleTickCount() {

        double unit = getTickUnit().getSize();
        return (int)(Math.floor(range.getUpperBound()/unit)-Math.ceil(range.getLowerBound()/unit)+1);

    }

}
