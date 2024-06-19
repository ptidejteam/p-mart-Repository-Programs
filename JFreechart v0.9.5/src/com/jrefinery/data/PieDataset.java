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
 * ---------------
 * PieDataset.java
 * ---------------
 * (C) Copyright 2001-2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Sam (oldman);
 *
 * $Id: PieDataset.java,v 1.1 2007/10/10 19:54:14 vauchers Exp $
 *
 * Changes
 * -------
 * 17-Nov-2001 : Version 1 (DG);
 * 22-Jan-2002 : Removed the getCategoryCount() method, updated Javadoc comments (DG);
 * 18-Apr-2002 : getCategories() now returns List instead of Set (oldman);
 * 23-Oct-2002 : Reorganised the code: PieDataset now extends KeyedValues interface (DG);
 *
 */

package com.jrefinery.data;

/**
 * A general purpose dataset where values are associated with keys.
 * <p>
 * As the name suggests, you can use this dataset to supply data for pie charts.
 *
 * @author David Gilbert
 */
public interface PieDataset extends KeyedValues, Dataset {

    // no new methods added, just combines KeyedValues and Dataset interfaces.
    
}
