/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000-2002, Simba Management Limited and Contributors.
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
 * -----------------
 * CategoryPlot.java
 * -----------------
 * (C) Copyright 2000-2002, Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: CategoryPlot.java,v 1.1 2007/10/10 18:55:43 vauchers Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 12-Dec-2001 : Updated Javadoc comments (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.geom.*;
import java.util.*;
import com.jrefinery.data.*;

/**
 * The interface through which axes can communicate with a plot to determine the positioning of
 * categories (which will depend on the visual representation used by the plot);
 * <P>
 * Plots that implement this interface include BarPlot and LinePlot.
 * @see BarPlot
 * @see LinePlot
 */
public interface CategoryPlot {

    /**
     * A convenience method that returns a list of the categories in the dataset.
     */
    public List getCategories();

    /**
     * Returns the x or y coordinate (depending on the orientation of the plot) in Java 2D User
     * Space of the center of the specified category.
     * @param category The category (zero-based index).
     * @param area The region within which the plot will be drawn.
     */
    public double getCategoryCoordinate(int category, Rectangle2D area);

    /**
     * A convenience method that returns the data source for the plot, cast as a CategoryDataset.
     */
    public CategoryDataset getDataset();

}
