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
 * --------------------------------------------
 * DynamicDriveToolTipTagFragmentGenerator.java
 * --------------------------------------------
 * (C) Copyright 2003, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson;
 *
 * $Id: DynamicDriveToolTipTagFragmentGenerator.java,v 1.1 2007/10/10 19:12:34 vauchers Exp $
 *
 * Changes
 * -------
 * 12-Aug-2003 : Version 1 (RA);
 * 
 */
 
package org.jfree.chart.imagemap;

/**
 * Generates tooltips using the Dynamic Drive DHTML Tip Message 
 * library (http://www.dynamicdrive.com).
 *
 * @author Richard Atkinson
 */
public class DynamicDriveToolTipTagFragmentGenerator implements ToolTipTagFragmentGenerator {

    /** The title, empty string not to display */
    protected String title = "";

    /** The style number */
    protected int style = 1;

    /**
     * Blank constructor.
     */
    public DynamicDriveToolTipTagFragmentGenerator() {
    }

    /**
     * Creates a new generator with specific title and style settings.
     *
     * @param title  Title for use in all tooltips, use empty String not to display a title.
     * @param style  Style number, see http://www.dynamicdrive.com for more information
     */
    public DynamicDriveToolTipTagFragmentGenerator(String title, int style) {
        this.title = title;
        this.style = style;
    }

    /**
     * Generates a tooltip string to go in an HTML image map.
     *
     * @param toolTipText the tooltip.
     * @return the formatted HTML area tag attribute(s).
     */
    public String generateToolTipFragment(String toolTipText) {
        return " onMouseOver=\"return stm(['" + this.title + "','" + toolTipText + "'],Style[" 
            + style + "]);\"" + " onMouseOut=\"return htm();\"";
    }

}
