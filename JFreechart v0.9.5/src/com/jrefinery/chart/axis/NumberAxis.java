/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Laurence Vanhelsuwe;
 *
 * $Id: NumberAxis.java,v 1.1 2007/10/10 19:54:25 vauchers Exp $
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
 *
 */

package com.jrefinery.chart.axis;

import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.Locale;
import com.jrefinery.chart.event.AxisChangeEvent;
import com.jrefinery.data.Range;

/**
 * The base class for axes that display numerical data.
 * <P>
 * If the axis is set up to automatically determine its range to fit the data,
 * you can ensure that the range includes zero (statisticians usually prefer
 * this) by setting the <code>autoRangeIncludesZero</code> flag to <code>true</code>.
 * <P>
 * The <code>NumberAxis</code> class has a mechanism for automatically selecting a tick unit
 * that is appropriate for the current axis range.  This mechanism is an
 * adaptation of code suggested by Laurence Vanhelsuwe.
 *
 *
 * @author David Gilbert
 */
public abstract class NumberAxis extends ValueAxis {

    /** The default value for the autoRangeIncludesZero flag. */
    public static final boolean DEFAULT_AUTO_RANGE_INCLUDES_ZERO = true;

    /** The default value for the autoRangeStickyZero flag. */
    public static final boolean DEFAULT_AUTO_RANGE_STICKY_ZERO = true;

    /** The default tick unit. */
    public static final NumberTickUnit
        DEFAULT_TICK_UNIT = new NumberTickUnit(1.0, new DecimalFormat("0"));

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

    /**
     * Constructs a number axis, using default values where necessary.
     *
     * @param label  the axis label.
     */
    protected NumberAxis(String label) {
        
        super(label, NumberAxis.createStandardTickUnits());

        this.autoRangeIncludesZero = DEFAULT_AUTO_RANGE_INCLUDES_ZERO;
        this.autoRangeStickyZero = DEFAULT_AUTO_RANGE_STICKY_ZERO;
        this.tickUnit = DEFAULT_TICK_UNIT;
        this.numberFormatOverride = null;        
        
    }

    /**
     * Returns the flag that indicates whether or not the automatic axis range
     * (if indeed it is determined automatically) is forced to include zero.
     *
     * @return the flag.
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
     * @return the flag.
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
     * @return the tick unit for the axis.
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
     * @return the number format override.
     */
    public NumberFormat getNumberFormatOverride() {
        return this.numberFormatOverride;
    }

    /**
     * Sets the number format override.  If this is non-null, then it will be used to format
     * the numbers on the axis.
     *
     * @param formatter  the number formatter (null permitted).
     */
    public void setNumberFormatOverride(NumberFormat formatter) {
        this.numberFormatOverride = formatter;
        notifyListeners(new AxisChangeEvent(this));
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

}
