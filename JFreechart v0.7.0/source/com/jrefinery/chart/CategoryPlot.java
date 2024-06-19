/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * This file...
 * $Id: CategoryPlot.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * (C) Copyright 2000, 2001, Simba Management Limited;
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
 * Change History: (from 18-Sep-2001)
 * ----------------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
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
     * A convenience method that returns a list of the categories in the data source.
     */
    public List getCategories();

    /**
     * Returns the x or y coordinate (depending on the orientation of the plot) in Java 2D User
     * Space of the center of the specified category.
     * @param categoryIndex The index of the category of interest (first category index = 0);
     * @param area The region within which the plot will be drawn;
     */
    public double getCategoryCoordinate(int categoryIndex, Rectangle2D area);

    /**
     * A convenience method that returns the data source for the plot, cast as a
     * CategoryDataSource.
     */
    public CategoryDataset getDataset();

}
