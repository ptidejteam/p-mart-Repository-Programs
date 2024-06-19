/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * OverlaidPlot.java
 * -----------------
 * (C) Copyright 2001, 2002, by Bill Kelemen.
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   -;
 *
 * $Id: OverlaidPlot.java,v 1.1 2007/10/10 18:55:45 vauchers Exp $
 *
 * Changes:
 * --------
 * 06-Dec-2001 : Version 1 (BK);
 * 12-Dec-2001 : Removed unnecessary 'throws' clause in constructor (DG);
 * 08-Jan-2002 : Moved to new package com.jrefinery.chart.combination (DG);
 *
 */

package com.jrefinery.chart.combination;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import com.jrefinery.chart.Axis;

/**
 * Extends a CombinedPlot to implement an OverlaidPlot. At this time does not
 * add anything new to a CombinedPlot, except a easier to read name when creating
 * an OverlaidPlot.
 *
 * @author Bill Kelemen (bill@kelemen-usa.com)
 */
public class OverlaidPlot extends CombinedPlot {

    /**
     * Constructor.
     * @param horizontal Common horizontal axis to use for all sub-plots.
     * @param vertical Common vertical axis to use for all sub-plots.
     */
    public OverlaidPlot(Axis horizontal, Axis vertical) {
        super(horizontal, vertical);
    }

}