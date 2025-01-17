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
 * ------------------------
 * PieToolTipGenerator.java
 * ------------------------
 * (C) Copyright 2001-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: PieToolTipGenerator.java,v 1.1 2007/10/10 19:34:53 vauchers Exp $
 *
 * Changes
 * -------
 * 15-Apr-2004 : Version 1, split from PieItemLabelGenerator (DG);
 *
 */

package org.jfree.chart.labels;

import org.jfree.data.PieDataset;

/**
 * A tool tip generator that is used by the {@link org.jfree.chart.plot.PiePlot} class.
 */
public interface PieToolTipGenerator {
    
    /**
     * Generates a tool tip text item for the specified item in the dataset.  This method can
     * return <code>null</code> to indicate that no tool tip should be displayed for an item.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param key  the section key (<code>null</code> not permitted).
     *
     * @return The tool tip text (possibly <code>null</code>).
     */
    public String generateToolTip(PieDataset dataset, Comparable key);
        
}
