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
 * --------------------
 * PieURLGenerator.java
 * --------------------
 * (C) Copyright 2002, 2003, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson (richard_c_atkinson@ntlworld.com);
 * Contributors:     David Gilbert (for Object Refinery Limited);
 *
 * $Id: PieURLGenerator.java,v 1.1 2007/10/10 19:12:32 vauchers Exp $
 *
 * Changes:
 * --------
 * 05-Aug-2002 : Version 1, contributed by Richard Atkinson;
 * 09-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 07-Mar-2003 : Modified to use KeyedValuesDataset and added pieIndex parameter (DG);
 * 24-Apr-2003 : Switched around PieDataset and KeyedValuesDataset (DG);
 *
 */
package org.jfree.chart.urls;

import org.jfree.data.PieDataset;

/**
 * Interface for a URL generator for plots that use data from a {@link PieDataset}.
 *
 * @author Richard Atkinson
 */
public interface PieURLGenerator extends URLGenerator {

    /**
     * Generates a URL for one item in a {@link PieDataset}.
     *
     * @param data  the dataset.
     * @param key  the item key.
     * @param pieIndex  the pie index (differentiates between pies in a 'multi' pie chart).
     *
     * @return a string containing the URL.
     */
    public String generateURL(PieDataset data, Comparable key, int pieIndex);

}
