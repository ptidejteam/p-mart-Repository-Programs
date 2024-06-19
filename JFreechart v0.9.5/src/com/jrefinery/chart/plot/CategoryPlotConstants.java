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
 * --------------------------
 * CategoryPlotConstants.java
 * --------------------------
 * (C) Copyright 2002, 2003 by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: CategoryPlotConstants.java,v 1.1 2007/10/10 19:54:14 vauchers Exp $
 *
 * Changes
 * -------

 * 06-Jun-2002 : Version 1 (code moved from CategoryPlot) (DG);
 * 28-Aug-2002 : Increased maximum values to make them less likely to get in the way, they are
 *               really just sanity checks anyway (DG);
 * 19-Nov-2002 : Added default grid line stroke and paint settings (DG);
 * 09-Jan-2002 : Removed intro and trail gap constants (no longer required) and renamed grid-line
 *               constants (DG);
 *
 */

package com.jrefinery.chart.plot;

import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Font;

/**
 * Useful constants for the {@link CategoryPlot} class.
 *
 * @author David Gilbert
 */
public interface CategoryPlotConstants {

    /** The default visibility of the grid lines plotted against the domain axis. */
    public static final boolean DEFAULT_DOMAIN_GRIDLINES_VISIBLE = false;
    
    /** The default visibility of the grid lines plotted against the range axis. */
    public static final boolean DEFAULT_RANGE_GRIDLINES_VISIBLE = true;
    
    /** The default grid line stroke. */
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5f,
        BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_BEVEL,
        0.0f,
        new float[] {2.0f, 2.0f},
        0.0f);

    /** The default grid line paint. */
    public static final Paint DEFAULT_GRIDLINE_PAINT = Color.lightGray;

    /** The default value label font. */
    public static final Font DEFAULT_VALUE_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);

}
