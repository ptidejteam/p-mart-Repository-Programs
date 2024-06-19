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
 * --------------------------------
 * ToolTipTagFragmentGenerator.java
 * --------------------------------
 * (C) Copyright 2003, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson;
 *
 * $Id: ToolTipTagFragmentGenerator.java,v 1.1 2007/10/10 19:15:26 vauchers Exp $
 *
 * Changes
 * -------
 * 12-Aug-2003 : Version 1 (RA);
 * 
 */
 
package org.jfree.chart.imagemap;

/**
 * Interface for generating the tooltip fragment of an HTML image map area tag.
 *
 * @author Richard Atkinson
 */
public interface ToolTipTagFragmentGenerator {

    /**
     * Generates a tooltip string to go in an HTML image map.
     *
     * @param toolTipText the tooltip.
     * @return the formatted HTML area tag attribute(s).
     */
    public String generateToolTipFragment(String toolTipText);

}
