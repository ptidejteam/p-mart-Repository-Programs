/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ------------------
 * AxisConstants.java
 * ------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: AxisConstants.java,v 1.1 2007/10/10 19:29:22 vauchers Exp $
 *
 * Changes
 * -------
 * 06-Mar-2002 : Version 1 (DG);
 * 25-Apr-2002 : Removed redundant HORIZONTAL and VERTICAL constants (DG);
 * 05-Sep-2002 : Added DEFAULT_TICK_PAINT (DG);
 * 16-Oct-2002 : Changed default tick paint to light gray (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis.  Added axis location
 *               constants (DG);
 * 02-May-2003 : Removed TOP, BOTTOM, LEFT and RIGHT (see AxisLocation) (DG);
 * 06-Jan-2004 : Added defaults for axis lines (DG);
 *
 */

package org.jfree.chart.axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Stroke;

/**
 * Useful constants for the {@link Axis} class and its subclasses.
 *
 * @author David Gilbert
 */
public interface AxisConstants {

    /** The default axis visibility. */
    public static final boolean DEFAULT_AXIS_VISIBLE = true;

    /** The default axis label font. */
    public static final Font DEFAULT_AXIS_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 12);

    /** The default axis label paint. */
    public static final Paint DEFAULT_AXIS_LABEL_PAINT = Color.black;

    /** The default axis label insets. */
    public static final Insets DEFAULT_AXIS_LABEL_INSETS = new Insets(3, 3, 3, 3);

    /** The default axis line paint. */
    public static final Paint DEFAULT_AXIS_LINE_PAINT = Color.gray;
    
    /** The default axis line stroke. */
    public static final Stroke DEFAULT_AXIS_LINE_STROKE = new BasicStroke(1.0f);

    /** The default tick labels visibility. */
    public static final boolean DEFAULT_TICK_LABELS_VISIBLE = true;

    /** The default tick label font. */
    public static final Font DEFAULT_TICK_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /** The default tick label paint. */
    public static final Paint DEFAULT_TICK_LABEL_PAINT = Color.black;

    /** The default tick label insets. */
    public static final Insets DEFAULT_TICK_LABEL_INSETS = new Insets(2, 4, 2, 4);

    /** The default tick marks visible. */
    public static final boolean DEFAULT_TICK_MARKS_VISIBLE = true;

    /** The default tick stroke. */
    public static final Stroke DEFAULT_TICK_MARK_STROKE = new BasicStroke(1);

    /** The default tick paint. */
    public static final Paint DEFAULT_TICK_MARK_PAINT = Color.gray;

    /** The default tick mark inside length. */
    public static final float DEFAULT_TICK_MARK_INSIDE_LENGTH = 0.0f;

    /** The default tick mark outside length. */
    public static final float DEFAULT_TICK_MARK_OUTSIDE_LENGTH = 2.0f;

}
