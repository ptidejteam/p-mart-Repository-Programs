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
 * --------------------
 * PieURLGenerator.java
 * --------------------
 * (C) Copyright 2002-2005, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson;
 * Contributors:     David Gilbert (for Object Refinery Limited);
 *
 * $Id: PieURLGenerator.java,v 1.1 2007/10/10 20:22:58 vauchers Exp $
 *
 * Changes:
 * --------
 * 05-Aug-2002 : Version 1, contributed by Richard Atkinson;
 * 09-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 07-Mar-2003 : Modified to use KeyedValuesDataset and added pieIndex 
 *               parameter (DG);
 * 24-Apr-2003 : Switched around PieDataset and KeyedValuesDataset (DG);
 *
 */
package org.jfree.chart.urls;

import org.jfree.data.general.PieDataset;

/**
 * Interface for a URL generator for plots that use data from a 
 * {@link PieDataset}.
 *
 * @author Richard Atkinson
 */
public interface PieURLGenerator {

    /**
     * Generates a URL for one item in a {@link PieDataset}. As a guideline, 
     * the URL should be valid within the context of an XHTML 1.0 document.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param key  the item key.
     * @param pieIndex  the pie index (differentiates between pies in a 
     *                  'multi' pie chart).
     *
     * @return A string containing the URL.
     */
    public String generateURL(PieDataset dataset, Comparable key, int pieIndex);

}
