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
 * -----------------------------
 * StandardXYLabelGenerator.java
 * -----------------------------
 * (C) Copyright 2001-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: StandardXYLabelGenerator.java,v 1.1 2007/10/10 19:34:53 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1 (DG);
 * 16-Jan-2002 : Completed Javadocs (DG);
 * 02-Apr-2002 : Modified to handle null y-values (DG);
 * 09-Apr-2002 : Added formatting objects for the x and y values (DG);
 * 30-May-2002 : Added series name to standard tool tip (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 23-Mar-2003 : Implemented Serializable (DG);
 * 13-Aug-2003 : Implemented Cloneable (DG);
 * 17-Nov-2003 : Implemented PublicCloneable (DG);
 * 25-Feb-2004 : Renamed XYToolTipGenerator --> XYItemLabelGenerator and
 *               StandardXYToolTipGenerator --> StandardXYItemLabelGenerator (DG);
 * 26-Feb-2004 : Modified to use MessageFormat (DG);
 * 27-Feb-2004 : Added abstract superclass (DG);
 * 11-May-2004 : Split into StandardXYToolTipGenerator and StandardXYLabelGenerator (DG);
 *
 */

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;

import org.jfree.data.XYDataset;
import org.jfree.util.PublicCloneable;

/**
 * A standard label generator for plots that use data from an {@link org.jfree.data.XYDataset}.
 */
public class StandardXYLabelGenerator extends AbstractXYItemLabelGenerator  
                                      implements XYLabelGenerator, 
                                                 Cloneable, 
                                                 PublicCloneable,
                                                 Serializable {

    /** The default item label format. */
    public static final String DEFAULT_ITEM_LABEL_FORMAT = "{2}";

    /**
     * Creates an item label generator using default number formatters.
     */
    public StandardXYLabelGenerator() {
        this(
            DEFAULT_ITEM_LABEL_FORMAT, 
            NumberFormat.getNumberInstance(), NumberFormat.getNumberInstance()
        );
    }


    /**
     * Creates an item label generator using the specified number formatters.
     *
     * @param formatString  the item label format string (<code>null</code> not permitted).
     * @param xFormat  the format object for the x values (<code>null</code> not permitted).
     * @param yFormat  the format object for the y values (<code>null</code> not permitted).
     */
    public StandardXYLabelGenerator(String formatString,
                                    NumberFormat xFormat, 
                                    NumberFormat yFormat) {
        
        super(formatString, xFormat, yFormat);
    
    }

    /**
     * Creates an item label generator using the specified number formatters.
     *
     * @param formatString  the item label format string (<code>null</code> not permitted).
     * @param xFormat  the format object for the x values (<code>null</code> not permitted).
     * @param yFormat  the format object for the y values (<code>null</code> not permitted).
     */
    public StandardXYLabelGenerator(String formatString,
                                    DateFormat xFormat, 
                                    NumberFormat yFormat) {
        
        super(formatString, xFormat, yFormat);
    
    }

    /**
     * Creates a label generator using the specified date formatters.
     *
     * @param formatString  the label format string (<code>null</code> not permitted).
     * @param xFormat  the format object for the x values (<code>null</code> not permitted).
     * @param yFormat  the format object for the y values (<code>null</code> not permitted).
     */
    public StandardXYLabelGenerator(String formatString,
                                    DateFormat xFormat, 
                                    DateFormat yFormat) {
        
        super(formatString, xFormat, yFormat);
    
    }

    /**
     * Generates the item label text for an item in a dataset.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The label text (possibly <code>null</code>).
     */
    public String generateLabel(XYDataset dataset, int series, int item) {
        return generateLabelString(dataset, series, item);
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
     * Tests this object for equality with an arbitrary object.
     *
     * @param obj  the other object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof StandardXYLabelGenerator) {
            return super.equals(obj);
        }
        return false;
    }

}
