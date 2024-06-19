/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
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
 * ------------------
 * DateAxisRange.java
 * ------------------
 * (C) Copyright 2001, 2002, by Bill Kelemen.
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   -;
 *
 * $Id: DateAxisRange.java,v 1.1 2007/10/10 18:57:06 vauchers Exp $
 *
 * Changes:
 * --------
 * 06-Dec-2001 : Version 1 (BK);
 * 08-Jan-2002 : Moved to new package com.jrefinery.chart.combination (DG);
 *
 */

package com.jrefinery.chart.combination;

import java.util.*;

/**
 * AxisRange implementation for Date axes. AxisRange is an interface
 * used by CombinedPlot to represent the min/max range of an axis. This allows
 * general algorithms to operate on dates and numerical ranges.
 *
 * @author Bill Kelemen (bill@kelemen-usa.com)
 */
public class DateAxisRange extends AbstractAxisRange {

    /**
     * Creates an DateAxisRange object.
     *
     * @param min Minimum date value
     * @param max Maximum date value
     */
    public DateAxisRange(Date min, Date max) {
        super(min, max);
    }

    /**
     * Returns true if o1 is before o2.
     *
     * @param o1 Object #1 to compare.
     * @param o2 Object #2 to compare.
     */
    protected boolean before(Object o1, Object o2) {
        Date d1 = (Date)o1;
        Date d2 = (Date)o2;
        return (d1.before(d2));
    }

}