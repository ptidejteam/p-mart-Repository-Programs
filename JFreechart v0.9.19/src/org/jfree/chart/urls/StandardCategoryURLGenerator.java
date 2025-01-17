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
 * (C) Copyright 2002-2004, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson (richard_c_atkinson@ntlworld.com);
 * Contributors:     David Gilbert (for Object Refinery Limited);
 *                   Cleland Early;
 *
 * $Id: StandardCategoryURLGenerator.java,v 1.1 2007/10/10 19:34:50 vauchers Exp $
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
 * 21-May-2004 : Added URL encoding - see patch 947854 (DG);
 *
 */

package org.jfree.chart.urls;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jfree.data.CategoryDataset;
import org.jfree.util.ObjectUtils;

/**
 * A URL generator that can be assigned to a 
 * {@link org.jfree.chart.renderer.CategoryItemRenderer}.
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
     * Creates a new generator with default settings.
     */
    public StandardCategoryURLGenerator() {
        super();
    }

    /**
     * Constructor that overrides default prefix to the URL.
     *
     * @param prefix  the prefix to the URL (<code>null</code> not permitted).
     */
    public StandardCategoryURLGenerator(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Null 'prefix' argument.");   
        }
        this.prefix = prefix;
    }

    /**
     * Constructor that overrides all the defaults.
     *
     * @param prefix  the prefix to the URL (<code>null</code> not permitted).
     * @param seriesParameterName  the name of the series parameter to go in 
     *                             each URL (<code>null</code> not permitted).
     * @param categoryParameterName  the name of the category parameter to go in 
     *                               each URL (<code>null</code> not permitted).
     */
    public StandardCategoryURLGenerator(String prefix,
                                        String seriesParameterName,
                                        String categoryParameterName) {

        if (prefix == null) {
            throw new IllegalArgumentException("Null 'prefix' argument.");   
        }
        if (seriesParameterName == null) {
            throw new IllegalArgumentException("Null 'seriesParameterName' argument.");   
        }
        if (categoryParameterName == null) {
            throw new IllegalArgumentException("Null 'categoryParameterName' argument.");   
        }
        this.prefix = prefix;
        this.seriesParameterName = seriesParameterName;
        this.categoryParameterName = categoryParameterName;

    }

    /**
     * Generates a URL for a particular item within a series.
     *
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param category  the category index (zero-based).
     *
     * @return The generated URL.
     */
    public String generateURL(CategoryDataset dataset, int series, int category) {
        String url = this.prefix;
        Comparable seriesKey = dataset.getRowKey(series);
        Comparable categoryKey = dataset.getColumnKey(category);
        boolean firstParameter = url.indexOf("?") == -1;
        url += firstParameter ? "?" : "&";
        try {
            url += this.seriesParameterName + "=" 
                   + URLEncoder.encode(seriesKey.toString(), "UTF-8");
        }
        catch (UnsupportedEncodingException uee) {
            url += this.seriesParameterName + "=" + seriesKey.toString();
        }
        try {
            url += "&" + this.categoryParameterName + "=" 
                   + URLEncoder.encode(categoryKey.toString(), "UTF-8");
        }
        catch (UnsupportedEncodingException uee) {
            url += "&" + this.categoryParameterName + "=" + categoryKey.toString();
        }
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
     * Tests the generator for equality with an arbitrary object.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardCategoryURLGenerator)) {
            return false;
        }
        StandardCategoryURLGenerator generator = (StandardCategoryURLGenerator) obj;
        if (!ObjectUtils.equal(this.prefix, generator.prefix)) {
            return false;
        }

        if (!ObjectUtils.equal(this.seriesParameterName, generator.seriesParameterName)) {
            return false;
        }
        if (!ObjectUtils.equal(this.categoryParameterName, generator.categoryParameterName)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code.
     * 
     * @return A hash code.
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
