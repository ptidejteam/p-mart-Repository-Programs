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
 * CustomXYURLGenerator.java
 * -------------------------
 * (C) Copyright 2002, 2003, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson (richard_c_atkinson@ntlworld.com);
 * Contributors:     David Gilbert (for Object Refinery Limited);
 *
 * $Id: CustomXYURLGenerator.java,v 1.1 2007/10/10 19:12:32 vauchers Exp $
 *
 * Changes:
 * --------
 * 05-Aug-2002 : Version 1, contributed by Richard Atkinson;
 * 09-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 23-Mar-2003 : Implemented Serializable (DG);
 *
 */
package org.jfree.chart.urls;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jfree.data.XYDataset;

/**
 * A custom URL generator.
 *
 * @author Richard Atkinson
 */
public class CustomXYURLGenerator implements XYURLGenerator, Serializable {

    /** Storage for the URLs. */
    private ArrayList urlSeries = new ArrayList();

    /**
     * Default constructor.
     */
    public CustomXYURLGenerator() {
    }

    /**
     * Returns the number of URL lists stored by the renderer.
     *
     * @return The list count.
     */
    public int getListCount() {
        return this.urlSeries.size();
    }

    /**
     * Returns the number of URLs in a given list.
     *
     * @param list  the list index (zero based).
     *
     * @return The URL count.
     */
    public int getURLCount(int list) {

        int result = 0;
        List tooltips = (List) this.urlSeries.get(list);
        if (tooltips != null) {
            result = tooltips.size();
        }
        return result;
    }

    /**
     * Returns the URL for an item.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The URL.
     */
    public String getURL(int series, int item) {

        String result = null;

        if (series < getListCount()) {
            List tooltips = (List) this.urlSeries.get(series);
            if (tooltips != null) {
                if (item < tooltips.size()) {
                    result = (String) tooltips.get(item);
                }
            }
        }

        return result;
    }

    /**
     * Generates a URL.
     *
     * @param data  the dataset.
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return a string containing the URL.
     */
    public String generateURL(XYDataset data, int series, int item) {
        return getURL(series, item);
    }

    /**
     * Adds a list of URLs.
     *
     * @param urls  the list of URLs.
     */
    public void addURLSeries(List urls) {
        this.urlSeries.add(urls);
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

        if (o instanceof CustomXYURLGenerator) {
            CustomXYURLGenerator generator = (CustomXYURLGenerator) o;
            boolean result = true;
            for (int series = 0; series < getListCount(); series++) {
                for (int item = 0; item < getURLCount(series); item++) {
                    String u1 = getURL(series, item);
                    String u2 = generator.getURL(series, item);
                    if (u1 != null) {
                        result = result && u1.equals(u2);
                    }
                    else {
                        result = result && (u2 == null);
                    }
                }
            }
            return result;
        }

        return false;

    }

}
