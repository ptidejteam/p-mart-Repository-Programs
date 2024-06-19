/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: TickUnits.java,v 1.1 2007/10/10 18:53:20 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * (C) Copyright 2001, Simba Management Limited;
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
 * Changes
 * -------
 * 23-Nov-2001 : Version 1 (DG);
 *
 */

package com.jrefinery.chart;

import java.util.*;

/**
 * A collection of tick units.
 */
public class TickUnits {

    protected List units;

    public TickUnits() {
        this.units = new ArrayList();
    }

    public void add(TickUnit unit) {
        units.add(unit);
        Collections.sort(units);
    }

    /**
     * Returns the tick unit in the collection that is closest in size to the specified unit.
     */
    public TickUnit getNearestTickUnit(TickUnit unit) {
        int index = Collections.binarySearch(units, unit);
        if (index>=0) {
            return (TickUnit)units.get(index);
        }
        else {
            index = -(index + 1);
            return (TickUnit)units.get(Math.min(index, units.size()));
        }
    }

    /**
     * Finds the tick unit that is closest to the specified value.
     */
    public TickUnit getNearestTickUnit(Number value) {

        return this.getNearestTickUnit(new NumberTickUnit(value, null));

    }

}