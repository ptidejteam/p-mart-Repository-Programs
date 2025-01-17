/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * CategoryURLGenerator.java
 * -------------------------
 * (C) Copyright 2002, 2003, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson (richard_c_atkinson@ntlworld.com);
 * Contributors:     David Gilbert (for Simba Management Limited);
 *
 * $Id: CategoryURLGenerator.java,v 1.1 2007/10/10 19:57:58 vauchers Exp $
 *
 * Changes:
 * --------
 * 05-Aug-2002 : Version 1, contributed by Richard Atkinson;
 * 09-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 05-Nov-2002 : Replaced reference to CategoryDataset with TableDataset (DG);
 *
 */
package com.jrefinery.chart.urls;

import com.jrefinery.data.CategoryDataset;

/**
 * A URL generator for items in a {@link CategoryDataset}.
 *
 * @author Richard Atkinson
 */
public interface CategoryURLGenerator extends URLGenerator {

    /**
     * Returns a URL for one item in a dataset.
     *
     * @param data  the dataset.
     * @param series  the series (zero-based index).
     * @param category  the category.
     *
     * @return a string containing the URL.
     */
    public String generateURL(CategoryDataset data, int series, int category);

}
