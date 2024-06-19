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
 * -------------
 * TickUnit.java
 * -------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: NumberTickUnit.java,v 1.1 2007/10/10 18:57:57 vauchers Exp $
 *
 * Changes (from 19-Dec-2001)
 * --------------------------
 * 19-Dec-2001 : Added standard header (DG);
 *
 */

package com.jrefinery.chart;

import java.text.NumberFormat;

/**
 * A numerical tick unit.
 */
public class NumberTickUnit extends TickUnit {

    /** A formatter for the tick unit. */
    protected NumberFormat formatter;

    /**
     * Creates a new number tick unit.
     * @param value The size of the tick unit.
     * @param formatter A number formatter for the tick unit.
     */
    public NumberTickUnit(Number value, NumberFormat formatter) {
        super(value);
        this.formatter = formatter;
    }

    /**
     * Compares this tick unit to an arbitrary object.
     */
    public int compareTo(Object o) {

        NumberTickUnit other = (NumberTickUnit)o;
        if (this.value.doubleValue()>other.value.doubleValue()) {
            return 1;
        }
        else if (this.value.doubleValue()<other.value.doubleValue()) {
            return -1;
        }
        else return 0;

    }

}