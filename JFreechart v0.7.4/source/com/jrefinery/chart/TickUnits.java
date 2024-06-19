/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart
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
 * --------------
 * TickUnits.java
 * --------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TickUnits.java,v 1.1 2007/10/10 18:57:57 vauchers Exp $
 *
 * Changes
 * -------
 * 23-Nov-2001 : Version 1 (DG);
 * 18-Feb-2002 : Fixed bug in getNearestTickUnit (thanks to Mario Inchiosa for reporting this,
 *               SourceForge bug id 518073) (DG);
 * 25-Feb-2002 : Moved createStandardTickUnits() method from NumberAxis, and added
 *               createIntegerTickUnits() method (DG);
 *
 */

package com.jrefinery.chart;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.text.DecimalFormat;

/**
 * A collection of tick units.
 */
public class TickUnits {

    /** Storage for the tick units. */
    protected List units;

    /**
     * Constructs a new collection of tick units.
     */
    public TickUnits() {
        this.units = new ArrayList();
    }

    /**
     * Adds a tick unit to the collection.
     * <P>
     * The tick units are maintained in ascending order.
     */
    public void add(TickUnit unit) {

        units.add(unit);
        Collections.sort(units);

    }

    /**
     * Returns the tick unit in the collection that is closest in size to the specified unit.
     * @param unit The unit.
     * @returns The unit in the collection that is closest in size to the specified unit.
     */
    public TickUnit getNearestTickUnit(TickUnit unit) {

        int index = Collections.binarySearch(units, unit);
        if (index>=0) {
            return (TickUnit)units.get(index);
        }
        else {
            index = -(index + 1);
            return (TickUnit)units.get(Math.min(index, units.size()-1));
        }

    }

    /**
     * Finds the tick unit that is closest to the specified value.
     */
    public TickUnit getNearestTickUnit(Number value) {

        return this.getNearestTickUnit(new NumberTickUnit(value, null));

    }

    /**
     * Creates the standard tick units.
     * <P>
     * If you don't like these defaults, create your own instance of TickUnits and then pass it to
     * the setStandardTickUnits(...) method.
     */
    public static TickUnits createStandardTickUnits() {

        TickUnits units = new TickUnits();

        units.add(new NumberTickUnit(new Double(0.0000001),  new DecimalFormat("0.0000000")));
        units.add(new NumberTickUnit(new Double(0.000001),   new DecimalFormat("0.000000")));
        units.add(new NumberTickUnit(new Double(0.00001),    new DecimalFormat("0.00000")));
        units.add(new NumberTickUnit(new Double(0.0001),     new DecimalFormat("0.0000")));
        units.add(new NumberTickUnit(new Double(0.001),      new DecimalFormat("0.000")));
        units.add(new NumberTickUnit(new Double(0.01),       new DecimalFormat("0.00")));
        units.add(new NumberTickUnit(new Double(0.1),        new DecimalFormat("0.0")));
        units.add(new NumberTickUnit(new Long(1L),           new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(10L),          new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(100L),         new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(1000L),        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(10000L),       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(100000L),      new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(1000000L),     new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(new Long(10000000L),    new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(new Long(100000000L),   new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(new Long(1000000000L),  new DecimalFormat("#,###,###,##0")));

        units.add(new NumberTickUnit(new Double(0.00000025), new DecimalFormat("0.00000000")));
        units.add(new NumberTickUnit(new Double(0.0000025),  new DecimalFormat("0.0000000")));
        units.add(new NumberTickUnit(new Double(0.000025),   new DecimalFormat("0.000000")));
        units.add(new NumberTickUnit(new Double(0.00025),    new DecimalFormat("0.00000")));
        units.add(new NumberTickUnit(new Double(0.0025),     new DecimalFormat("0.0000")));
        units.add(new NumberTickUnit(new Double(0.025),      new DecimalFormat("0.000")));
        units.add(new NumberTickUnit(new Double(0.25),       new DecimalFormat("0.00")));
        units.add(new NumberTickUnit(new Double(2.5),        new DecimalFormat("0.0")));
        units.add(new NumberTickUnit(new Long(25L),          new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(250L),         new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(2500L),        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(25000L),       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(250000L),      new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(2500000L),     new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(new Long(25000000L),    new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(new Long(250000000L),   new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(new Long(2500000000L),  new DecimalFormat("#,###,###,##0")));

        units.add(new NumberTickUnit(new Double(0.0000005),  new DecimalFormat("0.0000000")));
        units.add(new NumberTickUnit(new Double(0.000005),   new DecimalFormat("0.000000")));
        units.add(new NumberTickUnit(new Double(0.00005),    new DecimalFormat("0.00000")));
        units.add(new NumberTickUnit(new Double(0.0005),     new DecimalFormat("0.0000")));
        units.add(new NumberTickUnit(new Double(0.005),      new DecimalFormat("0.000")));
        units.add(new NumberTickUnit(new Double(0.05),       new DecimalFormat("0.00")));
        units.add(new NumberTickUnit(new Double(0.5),        new DecimalFormat("0.0")));
        units.add(new NumberTickUnit(new Long(5L),           new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(50L),          new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(500L),         new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(5000L),        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(50000L),       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(500000L),      new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(5000000L),     new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(new Long(50000000L),    new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(new Long(500000000L),   new DecimalFormat("#,###,##0")));
        units.add(new NumberTickUnit(new Long(5000000000L),  new DecimalFormat("#,###,###,##0")));

        return units;

    }

    /**
     * Returns a collection of tick units for integer values.
     */
    public static TickUnits createIntegerTickUnits() {

        TickUnits units = new TickUnits();

        units.add(new NumberTickUnit(new Long(1L),  new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(2L),  new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(5L),  new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(10L),  new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(20L),  new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(50L),  new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(100L),  new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(200L),  new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(500L),  new DecimalFormat("0")));
        units.add(new NumberTickUnit(new Long(1000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(2000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(5000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(10000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(20000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(50000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(100000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(200000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(500000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(1000000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(2000000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(5000000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(10000000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(20000000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(50000000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(100000000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(200000000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(500000000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(1000000000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(2000000000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(5000000000L),  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(new Long(10000000000L),  new DecimalFormat("#,##0")));

        return units;

    }

}