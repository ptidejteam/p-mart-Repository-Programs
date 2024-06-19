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
 * -----------------
 * DateTickUnit.java
 * -----------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DateTickUnit.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
 *
 * Changes (from 03-Sep-2002)
 * --------------------------
 * 03-Sep-2002 : Incomplete code, not used yet (DG);
 *
 */

package com.jrefinery.chart;

import java.text.DateFormat;
import java.util.Date;

/**
 * Represents a tick unit for a date axis.  This code is incomplete and not used yet.
 */
public class DateTickUnit extends TickUnit {

    public static final int YEAR = 0;
    public static final int MONTH = 1;
    public static final int DAY = 2;
    public static final int HOUR = 3;
    public static final int MINUTE = 4;
    public static final int SECOND = 5;
    public static final int MILLISECOND = 6;

    protected int unit;

    protected int count;

    protected DateFormat formatter;

    /**
     * Constructor.
     *
     * @param unit  -.
     * @param count  -.
     * @param formatter  -.
     */
    public DateTickUnit(int unit, int count, DateFormat formatter) {

        // pass zero for the size, then calculate an approximation below...
        super(0.0);
        this.unit = unit;
        this.count = count;
        this.size = 0.0;  // compute the approximate size

    }

    /**
     * Formats a value.
     *
     * @param milliseconds  date in milliseconds since 01-01-1970.
     *
     * @return the formatted date.
     */
    public String valueToString(double milliseconds) {
        return formatter.format(new Date((long)milliseconds));
    }

}
