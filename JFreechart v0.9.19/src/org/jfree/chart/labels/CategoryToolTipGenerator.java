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
 * CategoryToolTipGenerator.java
 * -----------------------------
 * (C) Copyright 2001-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CategoryToolTipGenerator.java,v 1.1 2007/10/10 19:34:53 vauchers Exp $
 *
 * Changes
 * -------
 * 15-Apr-2004 : Separated tool tip method from CategoryItemLabelGenerator interface (DG);
 *
 */

package org.jfree.chart.labels;

import org.jfree.data.CategoryDataset;

/**
 * A <i>category tool tip generator</i> is an object that can be assigned to a 
 * {@link org.jfree.chart.renderer.CategoryItemRenderer} and that assumes responsibility for 
 * creating text items to be used as tooltips for the items in 
 * a {@link org.jfree.chart.plot.CategoryPlot}. 
 * <p>
 * To assist with cloning charts, classes that implement this interface should also implement
 * the {@link org.jfree.util.PublicCloneable} interface.
 * 
 */
public interface CategoryToolTipGenerator {

    /**
     * Generates the tool tip text for an item in a dataset.  Note: in the current dataset
     * implementation, each row is a series, and each column contains values for a 
     * particular category.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The tooltip text (possibly <code>null</code>).
     */
    public String generateToolTip(CategoryDataset dataset, int row, int column);
    
}
