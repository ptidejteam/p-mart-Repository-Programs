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
 * ---------------------
 * SimpleTimePeriod.java
 * ---------------------
 * (C) Copyright 2002, 2003 by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: SimpleTimePeriod.java,v 1.1 2007/10/10 19:54:14 vauchers Exp $
 *
 * Changes
 * -------
 * 07-Oct-2002 : Added Javadocs (DG);
 * 10-Jan-2003 : Renamed TimeAllocation --> SimpleTimePeriod (DG);
 *
 */

package com.jrefinery.data;

import java.util.Date;

/**
 * An arbitrary period of time, measured to millisecond precision using <code>java.util.Date</code>.
 *
 * @author David Gilbert
 * 
 */
public class SimpleTimePeriod implements TimePeriod {

    /** The start date/time. */
    private Date start;

    /** The end date/time. */
    private Date end;

    /**
     * Creates a new time allocation.
     *
     * @param start  the start date/time.
     * @param end  the end date/time.
     */
    public SimpleTimePeriod(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the start date/time.
     *
     * @return the start date/time.
     */
    public Date getStart() {
        return this.start;
    }

    /**
     * Returns the end date/time.
     *
     * @return the end date/time.
     */
    public Date getEnd() {
        return this.end;
    }

}
