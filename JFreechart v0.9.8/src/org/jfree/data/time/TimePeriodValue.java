/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * --------------------
 * TimePeriodValue.java
 * --------------------
 * (C) Copyright 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TimePeriodValue.java,v 1.1 2007/10/10 20:03:22 vauchers Exp $
 *
 * Changes
 * -------
 * 22-Apr-2003 : Version 1 (DG);
 *  
 */

package org.jfree.data.time;

import java.io.Serializable;

/**
 * Represents a time period and an associated value.
 *
 * @author David Gilbert
 */
public class TimePeriodValue implements Cloneable, Serializable {

    /** The time period. */
    private TimePeriod period;

    /** The value associated with the time period. */
    private Number value;

    /**
     * Constructs a new data item.
     *
     * @param period  the time period.
     * @param value  the value associated with the time period.
     */
    public TimePeriodValue(TimePeriod period, Number value) {

        this.period = period;
        this.value = value;

    }

    /**
     * Constructs a new data pair.
     *
     * @param period  the time period.
     * @param value  the value associated with the time period.
     */
    public TimePeriodValue(TimePeriod period, double value) {

        this(period, new Double(value));

    }

    /**
     * Clones the data pair.
     * <P>
     * Notes:
     * --> no need to clone the period or value since they are immutable classes.
     *
     * @return a clone of this data pair.
     */
    public Object clone() {

        Object clone = null;

        try {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e) { // won't get here...
            System.err.println("TimePeriodValue.clone(): operation not supported.");
        }

        return clone;

    }

    /**
     * Returns the time period.
     *
     * @return the time period.
     */
    public TimePeriod getPeriod() {
        return this.period;
    }

    /**
     * Returns the value.
     *
     * @return the value.
     */
    public Number getValue() {
        return this.value;
    }

    /**
     * Sets the value for this data item.
     *
     * @param value  the new value.
     */
    public void setValue(Number value) {
        this.value = value;
    }

    /**
     * Tests this object for equality with the target object.
     * 
     * @param target  the other object.
     * 
     * @return A boolean.
     */
    public boolean equals(Object target) {
    
        boolean result = false;
        
        if (this == target) {
            result = true;
        }
        else {
            if (target instanceof TimePeriodValue) {
				TimePeriodValue item = (TimePeriodValue) target;
                Number value = item.getValue();
                boolean sameValues;
                if (this.value == null) {
                    sameValues = (value == null);
                }
                else {
                    sameValues = this.value.equals(value);
                }
                result = this.period.equals(item.getPeriod()) && sameValues;
            }
        }
        
        return result;
        
    }

}
