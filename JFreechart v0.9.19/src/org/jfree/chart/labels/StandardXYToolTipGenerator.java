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
 * StandardXYToolTipGenerator.java
 * -------------------------------
 * (C) Copyright 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: StandardXYToolTipGenerator.java,v 1.1 2007/10/10 19:34:53 vauchers Exp $
 *
 * Changes
 * -------
 * 12-May-2004 : Version 1 (DG);
 *
 */

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;

import org.jfree.data.XYDataset;
import org.jfree.util.PublicCloneable;

/**
 * A standard tool tip generator for use with an {@link org.jfree.chart.renderer.XYItemRenderer}.
 */
public class StandardXYToolTipGenerator extends AbstractXYItemLabelGenerator  
                                        implements XYToolTipGenerator,
                                                   Cloneable, 
                                                   PublicCloneable,
                                                   Serializable {

    /** The default tooltip format. */
    public static final String DEFAULT_TOOL_TIP_FORMAT = "{0}: ({1}, {2})";

    /**
     * Returns a tool tip generator that formats the x-values as dates and the 
     * y-values as numbers.
     * 
     * @return A tool tip generator (never <code>null</code>).
     */
    public static StandardXYToolTipGenerator getTimeSeriesInstance() {
        return new StandardXYToolTipGenerator(
            DEFAULT_TOOL_TIP_FORMAT, DateFormat.getInstance(), NumberFormat.getInstance()
        );
    }
    
    /**
     * Creates a tool tip generator using default number formatters.
     */
    public StandardXYToolTipGenerator() {
        this(
            DEFAULT_TOOL_TIP_FORMAT,
            NumberFormat.getNumberInstance(), NumberFormat.getNumberInstance()
        );
    }

    /**
     * Creates a tool tip generator using the specified number formatters.
     *
     * @param formatString  the item label format string (<code>null</code> not permitted).
     * @param xFormat  the format object for the x values (<code>null</code> not permitted).
     * @param yFormat  the format object for the y values (<code>null</code> not permitted).
     */
    public StandardXYToolTipGenerator(String formatString,
                                      NumberFormat xFormat, 
                                      NumberFormat yFormat) {
        
        super(formatString, xFormat, yFormat);
    
    }

    /**
     * Creates a tool tip generator using the specified number formatters.
     *
     * @param formatString  the label format string (<code>null</code> not permitted).
     * @param xFormat  the format object for the x values (<code>null</code> not permitted).
     * @param yFormat  the format object for the y values (<code>null</code> not permitted).
     */
    public StandardXYToolTipGenerator(String formatString,
                                      DateFormat xFormat, 
                                      NumberFormat yFormat) {
        
        super(formatString, xFormat, yFormat);
    
    }

    /**
     * Creates a tool tip generator using the specified date formatters.
     *
     * @param formatString  the label format string (<code>null</code> not permitted).
     * @param xFormat  the format object for the x values (<code>null</code> not permitted).
     * @param yFormat  the format object for the y values (<code>null</code> not permitted).
     */
    public StandardXYToolTipGenerator(String formatString,
                                      DateFormat xFormat, 
                                      DateFormat yFormat) {
        
        super(formatString, xFormat, yFormat);
    
    }

    /**
     * Generates the tool tip text for an item in a dataset.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The tooltip text (possibly <code>null</code>).
     */
    public String generateToolTip(XYDataset dataset, int series, int item) {
        return generateLabelString(dataset, series, item);
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
        if (obj instanceof StandardXYToolTipGenerator) {
            return super.equals(obj);
        }
        return false;
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
    
}
