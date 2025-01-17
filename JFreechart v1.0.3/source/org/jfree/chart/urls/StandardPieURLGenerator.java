/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ----------------------------
 * StandardPieURLGenerator.java
 * ----------------------------
 * (C) Copyright 2002-2005, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson;
 * Contributors:     David Gilbert (for Object Refinery Limited);
 *
 * $Id: StandardPieURLGenerator.java,v 1.1 2007/10/10 20:29:53 vauchers Exp $
 *
 * Changes:
 * --------
 * 05-Aug-2002 : Version 1, contributed by Richard Atkinson;
 * 09-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 07-Mar-2003 : Modified to use KeyedValuesDataset and added pieIndex 
 *               parameter (DG);
 * 21-Mar-2003 : Implemented Serializable (DG);
 * 24-Apr-2003 : Switched around PieDataset and KeyedValuesDataset (DG);
 * 31-Mar-2004 : Added an optional 'pieIndex' parameter (DG);
 * 13-Jan-2005 : Fixed for compliance with XHTML 1.0 (DG):
 *
 */
 
package org.jfree.chart.urls;

import java.io.Serializable;

import org.jfree.data.general.PieDataset;

/**
 * A URL generator for pie charts.
 *
 * @author Richard Atkinson
 */
public class StandardPieURLGenerator implements PieURLGenerator, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = 1626966402065883419L;
    
    /** The prefix. */
    private String prefix = "index.html";

    /** The category parameter name. */
    private String categoryParameterName = "category";
    
    /** The pie index parameter name. */
    private String indexParameterName = "pieIndex";

    /**
     * Default constructor.
     */
    public StandardPieURLGenerator() {
        super();
    }

    /**
     * Creates a new generator.
     *
     * @param prefix  the prefix.
     */
    public StandardPieURLGenerator(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Creates a new generator.
     *
     * @param prefix  the prefix.
     * @param categoryParameterName  the category parameter name.
     */
    public StandardPieURLGenerator(String prefix, 
                                   String categoryParameterName) {
        this.prefix = prefix;
        this.categoryParameterName = categoryParameterName;
    }

    /**
     * Creates a new generator.
     *
     * @param prefix  the prefix.
     * @param categoryParameterName  the category parameter name.
     * @param indexParameterName  the index parameter name 
     *                            (<code>null</code> permitted).
     */
    public StandardPieURLGenerator(String prefix, 
                                   String categoryParameterName, 
                                   String indexParameterName) {
        this.prefix = prefix;
        this.categoryParameterName = categoryParameterName;
        this.indexParameterName = indexParameterName;
    }

    /**
     * Generates a URL.
     *
     * @param data  the dataset.
     * @param key  the item key.
     * @param pieIndex  the pie index (ignored).
     *
     * @return A string containing the generated URL.
     */
    public String generateURL(PieDataset data, Comparable key, int pieIndex) {

        String url = this.prefix;
        if (url.indexOf("?") > -1) {
            url += "&amp;" + this.categoryParameterName + "=" + key.toString();
        }
        else {
            url += "?" + this.categoryParameterName + "=" + key.toString();
        }
        if (this.indexParameterName != null) {
            url += "&amp;" + this.indexParameterName + "=" 
                   + String.valueOf(pieIndex);
        }
        return url;

    }

    /**
     * Tests if this object is equal to another.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        if ((obj instanceof StandardPieURLGenerator) == false) {
            return false;
        }

        StandardPieURLGenerator generator = (StandardPieURLGenerator) obj;
        return (
            this.categoryParameterName.equals(generator.categoryParameterName))
            && (this.prefix.equals(generator.prefix)
        );

    }
}
