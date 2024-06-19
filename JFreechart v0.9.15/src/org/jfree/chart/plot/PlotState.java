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
 * --------------
 * PlotState.java
 * --------------
 * (C) Copyright 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: PlotState.java,v 1.1 2007/10/10 19:22:00 vauchers Exp $
 *
 * Changes
 * -------
 * 30-Oct-2003 : Version 1 (DG);
 *
 */
package org.jfree.chart.plot;

import java.util.HashMap;
import java.util.Map;

/**
 * Records information about the state of a plot during the drawing process.
 */
public class PlotState {

    /** The shared axis states. */
    private Map sharedAxisStates;
    
    /**
     * Creates a new state object.
     */
    public PlotState() {
        this.sharedAxisStates = new HashMap();    
    }
    
    /**
     * Returns a map containing the shared axis states.
     * 
     * @return A map.
     */
    public Map getSharedAxisStates() {
        return this.sharedAxisStates;
    }
    
}
