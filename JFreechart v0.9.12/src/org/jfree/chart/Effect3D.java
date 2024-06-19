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
 * -------------
 * Effect3D.java
 * -------------
 * (C) Copyright 2002, 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: Effect3D.java,v 1.1 2007/10/10 19:12:21 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Nov-2002 : Version 1 (DG);
 * 14-Nov-2002 : Modified to have independent x and y offsets (DG);
 *
 */

package org.jfree.chart;

/**
 * An interface that should be implemented by renderers that use a 3D effect.  This allows
 * the axes to mirror the same effect by querying the renderer.
 *
 * @author David Gilbert
 */
public interface Effect3D {

    /**
     * Returns the x-offset (in Java2D units) for the 3D effect.
     *
     * @return the offset.
     */
    public double getXOffset();

    /**
     * Returns the y-offset (in Java2D units) for the 3D effect.
     *
     * @return the offset.
     */
    public double getYOffset();
}
