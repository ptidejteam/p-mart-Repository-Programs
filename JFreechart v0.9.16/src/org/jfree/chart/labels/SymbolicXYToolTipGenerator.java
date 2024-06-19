/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -------------------------------
 * SymbolicXYToolTipGenerator.java
 * -------------------------------
 * (C) Copyright 2001-2003, by Anthony Boulestreau and Contributors.
 *
 * Original Author:  Anthony Boulestreau;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $ Id $
 * 
 * Changes
 * -------
 * 29-Mar-2002 : Version 1, contributed by Anthony Boulestreau (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 23-Mar-2003 : Implemented Serializable (DG);
 * 13-Aug-2003 : Implemented Cloneable (DG);
 * 17-Nov-2003 : Implemented PublicCloneable (DG);
 *
 */

package org.jfree.chart.labels;

import java.io.Serializable;

import org.jfree.data.XYDataset;
import org.jfree.data.XisSymbolic;
import org.jfree.data.YisSymbolic;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.util.PublicCloneable;

/**
 * A standard tooltip generator for plots that use data from an {@link XYDataset}.
 *
 * @author Anthony Boulestreau
 */
public class SymbolicXYToolTipGenerator implements XYToolTipGenerator, 
                                                   Cloneable, PublicCloneable,
                                                   Serializable {

    /**
     * Generates a tool tip text item for a particular item within a series.
     *
     * @param data  the dataset.
     * @param series  the series number (zero-based index).
     * @param item  the item number (zero-based index).
     *
     * @return the tool tip text.
     */
    public String generateToolTip(XYDataset data, int series, int item) {

        String x, y;
        if (data instanceof YisSymbolic) {
            y = ((YisSymbolic) data).getYSymbolicValue(series, item);
        }
        else {
            Number n = data.getYValue(series, item);
            y = Double.toString(round(n.doubleValue(), 2));
        }
        if (data instanceof XisSymbolic) {
            x = ((XisSymbolic) data).getXSymbolicValue(series, item);
        }
        else if (data instanceof TimeSeriesCollection) {
            RegularTimePeriod p
                = ((TimeSeriesCollection) data).getSeries(series).getTimePeriod(item);
            x = p.toString();
        }
        else {
            Number n = data.getXValue(series, item);
            x = Double.toString(round(n.doubleValue(), 2));
        }
        return "X: " + x + ", Y: " + y;
    }

    /**
    * Round a double value.
    *
    * @param value  the value.
    * @param nb  the exponent.
    *
    * @return  the rounded value.
    */
    private static double round(double value, int nb) {
        if (nb <= 0) {
            return Math.floor(value + 0.5d);
        }
        double p = Math.pow(10, nb);
        double tempval = Math.floor(value * p + 0.5d);
        return tempval / p;
    }
    
    /**
     * Returns an independent copy of the generator.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if cloning is not supported.
     */
    public Object clone() throws CloneNotSupportedException {
        
        return super.clone();
        
    }    

    /**
     * Tests if this object is equal to another.
     *
     * @param o  the other object.
     *
     * @return A boolean.
     */
    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }

        if (o instanceof SymbolicXYToolTipGenerator) {
            return true;
        }
        return false;

    }
    
}
