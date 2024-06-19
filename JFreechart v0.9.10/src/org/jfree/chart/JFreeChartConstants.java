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
 * ------------------------
 * JFreeChartConstants.java
 * ------------------------
 * (C) Copyright 2002, 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: JFreeChartConstants.java,v 1.1 2007/10/10 19:05:20 vauchers Exp $
 *
 * Changes
 * -------
 * 06-Mar-2002 : Version 1 (DG);
 * 11-Mar-2002 : Moved some constants into JFreeChartInfo.java (DG);
 * 22-Jan-2003 : Added default background image alignment (DG);
 *
 */

package org.jfree.chart;

import java.awt.Font;
import java.awt.Image;
import java.awt.Paint;
import java.awt.SystemColor;

import org.jfree.ui.Align;

/**
 * Useful constants relating to the {@link JFreeChart} class.
 *
 * @author David Gilbert
 */
public interface JFreeChartConstants {

    /** The default font for titles. */
    public static final Font DEFAULT_TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);

    /** The default background color. */
    public static final Paint DEFAULT_BACKGROUND_PAINT = SystemColor.control;

    /** The default background image. */
    public static final Image DEFAULT_BACKGROUND_IMAGE = null;

    /** The default background image alignment. */
    public static final int DEFAULT_BACKGROUND_IMAGE_ALIGNMENT = Align.FIT;

    /** The default background image alpha. */
    public static float DEFAULT_BACKGROUND_IMAGE_ALPHA = 0.5f;

}
