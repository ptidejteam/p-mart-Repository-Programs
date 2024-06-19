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
 * -------------------------------
 * CategoryItemLabelGenerator.java
 * -------------------------------
 * (C) Copyright 2001-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CategoryItemLabelGenerator.java,v 1.1 2007/10/10 20:07:40 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1 (DG);
 * 16-Jan-2002 : Completed Javadocs (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 24-Oct-2002 : Method now specifies category index (DG);
 * 05-Nov-2002 : Replaced reference to CategoryDataset with TableDataset (DG);
 * 21-Jan-2003 : TableDataset merged with CategoryDataset (DG);
 * 10-Apr-2003 : Changed CategoryDataset --> KeyedValues2DDataset (DG);
 * 01-May-2003 : Added generateValueLabel(...) method (with a plan to renaming this interface
 *               to reflect its more general use) (DG);
 * 09-Jun-2003 : Renamed CategoryToolTipGenerator --> CategoryItemLabelGenerator (DG);
 *
 */

package org.jfree.chart.labels;

import org.jfree.data.CategoryDataset;

/**
 * Interface for a label (including tooltips)  generator for plots that use data from a
 * {@link CategoryDataset}.
 *
 * @author David Gilbert
 */
public interface CategoryItemLabelGenerator {

    /**
     * Generates a tooltip text item for a particular category within a series.
     *
     * @param data  the dataset.
     * @param series  the series index (zero-based).
     * @param category  the category index (zero-based).
     *
     * @return The tooltip text.
     */
    public String generateToolTip(CategoryDataset data, int series, int category);

    /**
     * Generates a label for an item in the dataset.  This is typically used to display a formatted
     * value, but any text can be used.
     *
     * @param data  the dataset.
     * @param series  the series index (zero-based).
     * @param category  the category index (zero-based).
     *
     * @return The label.
     */
    public String generateItemLabel(CategoryDataset data, int series, int category);

}
