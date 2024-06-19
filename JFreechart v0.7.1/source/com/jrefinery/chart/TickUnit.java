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
 * -------------
 * TickUnit.java
 * -------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TickUnit.java,v 1.1 2007/10/10 18:55:43 vauchers Exp $
 *
 * Changes (from 19-Dec-2001)
 * --------------------------
 * 19-Dec-2001 : Added standard header (DG);
 *
 */

package com.jrefinery.chart;

/**
 * Base class representing a tick unit.  This determines the spacing of the tick marks on an
 * axis.
 * <P>
 * This class (and subclasses) should be immutable, the reason being that ordered collections of
 * tick units are maintained and if one instance can be changed, it may destroy the order of the
 * collection that it belongs to.  In addition, if the implementations are immutable, they can
 * belong to multiple collections.
 */
public abstract class TickUnit implements Comparable {

    /** The size or value of the tick unit. */
    protected Number value;

    /**
     * Constructs a new tick unit.
     */
    public TickUnit(Number value) {
        this.value = value;
    }

    /**
     * Returns the value of the tick unit.
     */
    public Number getValue() {
        return this.value;
    }

    /**
     * Method required for the Comparable interface.
     */
    public abstract int compareTo(Object o);

}