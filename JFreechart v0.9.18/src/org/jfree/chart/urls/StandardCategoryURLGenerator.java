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
 * ---------------------------------
 * StandardCategoryURLGenerator.java
 * ---------------------------------
 * (C) Copyright 2002, 2003, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson (richard_c_atkinson@ntlworld.com);
 * Contributors:     David Gilbert (for Object Refinery Limited);
 *
 * $Id: StandardCategoryURLGenerator.java,v 1.1 2007/10/10 19:39:13 vauchers Exp $
 *
 * Changes:
 * --------
 * 05-Aug-2002 : Version 1, contributed by Richard Atkinson;
 * 29-Aug-2002 : Reversed seriesParameterName and itemParameterName in constructor
 *               Never should have been the other way round.  Also updated JavaDoc (RA);
 * 09-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 05-Nov-2002 : Base dataset is now TableDataset not CategoryDataset (DG);
 * 23-Mar-2003 : Implemented Serializable (DG);
 * 13-Aug-2003 : Implemented Cloneable (DG);
 * 23-Dec-2003 : Added fix for bug 861282 (DG);
 *
 */

package org.jfree.chart.urls;

import java.io.Serializable;

import org.jfree.data.CategoryDataset;
import org.jfree.util.ObjectUtils;

/**
 * A URL generator.
 *
 * @author Richard Atkinson
 */
public class StandardCategoryURLGenerator implements CategoryURLGenerator, 
                                                     Cloneable, Serializable {

    /** Prefix to the URL */
    private String prefix = "index.html";

    /** Series parameter name to go in each URL */
    private String seriesParameterName = "series";

    /** Category parameter name to go in each URL */
    private String categoryParameterName = "category";

    /**
     * Blank constructor
     */
    public StandardCategoryURLGenerator() {
        super();
    }

    /**
     * Constructor that overrides default prefix to the URL.
     *
     * @param prefix  the prefix to the URL
     */
    public StandardCategoryURLGenerator(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Constructor that overrides all the defaults.
     *
     * @param prefix  the prefix to the URL.
     * @param seriesParameterName  the name of the series parameter to go in each URL.
     * @param categoryParameterName  the name of the category parameter to go in each URL.
     */
    public StandardCategoryURLGenerator(String prefix,
                                        String seriesParameterName,
                                        String categoryParameterName) {

        this.prefix = prefix;
        this.seriesParameterName = seriesParameterName;
        this.categoryParameterName = categoryParameterName;

    }

    /**
     * Generates a URL for a particular item within a series.
     *
     * @param data  the dataset.
     * @param series  the series index (zero-based).
     * @param category  the category index (zero-based).
     *
     * @return the generated URL.
     */
    public String generateURL(CategoryDataset data, int series, int category) {
        String url = this.prefix;
        Comparable seriesKey = data.getRowKey(series);
        Comparable categoryKey = data.getColumnKey(category);
        boolean firstParameter = url.indexOf("?") == -1;
        url += firstParameter ? "?" : "&";
        url += this.seriesParameterName + "=" + seriesKey.toString();
        url += "&" + this.categoryParameterName + "=" + categoryKey.toString();
        return url;
    }

    /**
     * Returns an independent copy of the URL generator.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException not thrown by this class, but subclasses (if any) might.
     */
    public Object clone() throws CloneNotSupportedException {
    
        // all attributes are immutable, so we can just return the super.clone()
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

        if ((o instanceof StandardCategoryURLGenerator) == false) {
            return false;
        }

        StandardCategoryURLGenerator generator = (StandardCategoryURLGenerator) o;

        if (ObjectUtils.equal(this.prefix, generator.prefix) == false) {
            return false;
        }

        if (ObjectUtils.equal
            (this.seriesParameterName, generator.seriesParameterName) == false) {
            return false;
        }
        if (ObjectUtils.equal
            (this.categoryParameterName, generator.categoryParameterName) == false) {
            return false;
        }

        return true;

    }

    /**
     * Returns a hash code.
     * 
     * @return a hash code.
     */
    public int hashCode() {
        int result;
        result = (this.prefix != null ? this.prefix.hashCode() : 0);
        result = 29 * result 
            + (this.seriesParameterName != null ? this.seriesParameterName.hashCode() : 0);
        result = 29 * result 
            + (this.categoryParameterName != null ? this.categoryParameterName.hashCode() : 0);
        return result;
    }
}
