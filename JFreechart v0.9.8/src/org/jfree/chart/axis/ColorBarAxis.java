/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * -----------------
 * ColorBarAxis.java
 * -----------------
 * (C) Copyright 2002, 2003 by David M. O'Donnell and Contributors.
 *
 * Original Author:  David M. O'Donnell;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: ColorBarAxis.java,v 1.1 2007/10/10 20:03:22 vauchers Exp $
 *
 * Changes
 * -------
 * 26-Nov-2002 : Version 1 contributed by David M. O'Donnell (DG);
 *
 */

package org.jfree.chart.axis;

import java.awt.Paint;

import org.jfree.chart.ui.ColorPalette;

/**
 * An interface required for color bar axes.
 *
 * @author David M. O'Donnell
 */
public interface ColorBarAxis {

    /**
     * Returns the current color palette.
     *
     * @return the color palette.
     */
    public ColorPalette getColorPalette();

    /**
     * Sets the current color palette.
     *
     * @param palette the new palette.
     */
    public void setColorPalette(ColorPalette palette);

    /**
     * Returns the Paint associated with a value.
     *
     * @param value  the value.
     *
     * @return the paint.
     */
    public Paint getPaint(double value);

    /**
     * This is cheat to make autoAdjustRange public.
     */
    public void doAutoRange();

}
