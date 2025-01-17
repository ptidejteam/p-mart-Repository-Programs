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
 * -------------------------
 * DateTickMarkPosition.java
 * -------------------------
 * (C) Copyright 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DateTickMarkPosition.java,v 1.1 2007/10/10 19:21:57 vauchers Exp $
 *
 * Changes:
 * --------
 * 30-Apr-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.axis;

import java.io.Serializable;

/**
 * Used to indicate the required position of tick marks on a date axis relative to the
 * underlying time period.
 *
 * @author David Gilbert
 */
public final class DateTickMarkPosition implements Serializable {

    /** Start of period. */
    public static final DateTickMarkPosition START
        = new DateTickMarkPosition("DateTickMarkPosition.START");

    /** Middle of period. */
    public static final DateTickMarkPosition MIDDLE
        = new DateTickMarkPosition("DateTickMarkPosition.MIDDLE");

    /** End of period. */
    public static final DateTickMarkPosition END
        = new DateTickMarkPosition("DateTickMarkPosition.END");

    /** The name. */
    private String name;

    /**
     * Private constructor.
     *
     * @param name  the name.
     */
    private DateTickMarkPosition(String name) {
        this.name = name;
    }

    /**
     * Returns a string representing the object.
     *
     * @return The string.
     */
    public String toString() {
        return this.name;
    }

    /**
     * Returns <code>true</code> if this object is equal to the specified object, and
     * <code>false</code> otherwise.
     *
     * @param o  the other object.
     *
     * @return A boolean.
     */
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof DateTickMarkPosition)) {
            return false;
        }

        final DateTickMarkPosition position = (DateTickMarkPosition) o;
        if (!this.name.equals(position.toString())) {
            return false;
        }

        return true;

    }

}
