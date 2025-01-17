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
 * ----------------------------------
 * StandardPieItemLabelGenerator.java
 * ----------------------------------
 * (C) Copyright 2001-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: StandardPieItemLabelGenerator.java,v 1.1 2007/10/10 19:25:31 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1 (DG);
 * 16-Jan-2002 : Completed Javadocs (DG);
 * 29-Aug-2002 : Changed to format numbers using default locale (RA);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 30-Oct-2002 : Changed PieToolTipGenerator interface (DG);
 * 21-Mar-2003 : Implemented Serializable (DG);
 * 13-Aug-2003 : Implemented Cloneable (DG);
 * 19-Aug-2003 : Renamed StandardPieToolTipGenerator --> StandardPieItemLabelGenerator (DG);
 *
 */

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.NumberFormat;

import org.jfree.data.PieDataset;
import org.jfree.util.ObjectUtils;

/**
 * A standard item label generator for plots that use data from a {@link PieDataset}.
 *
 * @author David Gilbert
 */
public class StandardPieItemLabelGenerator implements PieItemLabelGenerator, 
                                                      Cloneable,
                                                      Serializable {

    /** The number formatter. */
    private NumberFormat numberFormat;

    /**
     * Creates an item label generator with a default number formatter.
     */
    public StandardPieItemLabelGenerator() {
        this(NumberFormat.getInstance());
    }

    /**
     * Creates an item label generator with the specified number formatter.
     *
     * @param formatter  the number formatter.
     */
    public StandardPieItemLabelGenerator(NumberFormat formatter) {
        this.numberFormat = formatter;
    }

    /**
     * Returns the number formatter.
     *
     * @return the number formatter.
     */
    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }

    /**
     * Generates a tool tip text item for one section in a pie chart.
     *
     * @param data  the dataset.
     * @param key  the item key.
     * @param pieIndex  the pie index (ignored).
     *
     * @return The tool tip text (possibly <code>null</code>).
     */
    public String generateToolTip(PieDataset data, Comparable key, int pieIndex) {

        String result = null;
        Number value = data.getValue(key);
        if (value != null) {
            String sectionLabel = key.toString();
            result = sectionLabel + " = " + this.numberFormat.format(value);
        }

        return result;

    }

    /**
     * Returns an independent copy of the generator.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException  should not happen.
     */
    public Object clone() throws CloneNotSupportedException {
        
        StandardPieItemLabelGenerator clone = (StandardPieItemLabelGenerator) super.clone();
        
        if (this.numberFormat != null) {
            clone.numberFormat = (NumberFormat) this.numberFormat.clone();
        } 
        
        return clone;
        
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

        if (o instanceof StandardPieItemLabelGenerator) {
            StandardPieItemLabelGenerator generator = (StandardPieItemLabelGenerator) o;
            return ObjectUtils.equal(this.numberFormat, generator.getNumberFormat());
        }
        return false;

    }

}
