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
 * ---------------
 * ChartColor.java
 * ---------------
 * (C) Copyright 2003, by Cameron Riley and Contributors.
 *
 * Original Author:  ;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: ChartColor.java,v 1.1 2007/10/10 20:03:12 vauchers Exp $
 *
 * Changes
 * -------
 * 23-Jan-2003 : Version 1, contributed by Cameron Riley (DG);
 *
 */


package org.jfree.chart;

import java.awt.Color;
import java.awt.Paint;

/**
 * Class to extend the number of Colors available to the charts. This 
 * extends the java.awt.Color object and extends the number of final
 * Colors publically accessible.
 *
 * @author <a href="criley@ekmail.com">Cameron Riley</a>
 */
public class ChartColor extends Color {

    /** A very dark red color. */
    public static final Color VERY_DARK_RED = new Color(0x80, 0x00, 0x00);

    /** A dark red color. */
    public static final Color DARK_RED = new Color(0xc0, 0x00, 0x00);

    /** A light red color. */
    public static final Color LIGHT_RED = new Color(0xFF, 0x40, 0x40);

    /** A very light red color. */
    public static final Color VERY_LIGHT_RED = new Color(0xFF, 0x80, 0x80);

    /** A very dark yellow color. */
    public static final Color VERY_DARK_YELLOW = new Color(0x80, 0x80, 0x00);

    /** A dark yellow color. */
    public static final Color DARK_YELLOW = new Color(0xC0, 0xC0, 0x00);

    /** A light yellow color. */
    public static final Color LIGHT_YELLOW = new Color(0xFF, 0xFF, 0x40);

    /** A very light yellow color. */
    public static final Color VERY_LIGHT_YELLOW = new Color(0xFF, 0xFF, 0x80);

    /** A very dark green color. */
    public static final Color VERY_DARK_GREEN = new Color(0x00, 0x80, 0x00);

    /** A dark green color. */
    public static final Color DARK_GREEN = new Color(0x00, 0xC0, 0x00);

    /** A light green color. */
    public static final Color LIGHT_GREEN = new Color(0x40, 0xFF, 0x40);

    /** A very light green color. */
    public static final Color VERY_LIGHT_GREEN = new Color(0x80, 0xFF, 0x80);

    /** A very dark cyan color. */
    public static final Color VERY_DARK_CYAN = new Color(0x00, 0x80, 0x80);

    /** A dark cyan color. */
    public static final Color DARK_CYAN = new Color(0x00, 0xC0, 0xC0);

    /** A light cyan color. */
    public static final Color LIGHT_CYAN = new Color(0x40, 0xFF, 0xFF);

    /** Aa very light cyan color. */
    public static final Color VERY_LIGHT_CYAN = new Color(0x80, 0xFF, 0xFF);

    /** A very dark blue color. */
    public static final Color VERY_DARK_BLUE = new Color(0x00, 0x00, 0x80);

    /** A dark blue color. */
    public static final Color DARK_BLUE = new Color(0x00, 0x00, 0xC0);

    /** A light blue color. */
    public static final Color LIGHT_BLUE = new Color(0x40, 0x40, 0xFF);

    /** A very light blue color. */
    public static final Color VERY_LIGHT_BLUE = new Color(0x80, 0x80, 0xFF);

    /** A very dark magenta/purple color. */
    public static final Color VERY_DARK_MAGENTA = new Color(0x80, 0x00, 0x80);

    /** A dark magenta color. */
    public static final Color DARK_MAGENTA = new Color(0xC0, 0x00, 0xC0);

    /** A light magenta color. */
    public static final Color LIGHT_MAGENTA = new Color(0xFF, 0x40, 0xFF);

    /** A very light magenta color. */
    public static final Color VERY_LIGHT_MAGENTA = new Color(0xFF, 0x80, 0xFF);

    /**
     * Creates a Color with an opaque sRGB with red, green and blue values in range 0-255.
     * 
     * @param r  the red component in range 0x00-0xFF.
     * @param g  the green component in range 0x00-0xFF.
     * @param b  the blue component in range 0x00-0xFF.
     */
    public ChartColor(int r, int g, int b) {
        super(r, g, b);
    }

    /**
     * Convenience method to return an array of <code>Paint</code> objects that represent
     * the pre-defined colors in the <code>Color<code> and <code>ChartColor</code> objects.
     * 
     * @return an array of objects with the <code>Paint</code> interface.
     */
    public static Paint[] createDefaultPaintArray() {
        
        return new Paint[] {
            Color.red, 
            Color.blue, 
            Color.green,
            Color.yellow, 
            Color.orange, 
            Color.magenta,
            Color.cyan, 
            Color.pink, 
            Color.gray,
            ChartColor.DARK_RED, 
            ChartColor.DARK_BLUE, 
            ChartColor.DARK_GREEN, 
            ChartColor.DARK_YELLOW, 
            ChartColor.DARK_MAGENTA, 
            ChartColor.DARK_CYAN, 
            Color.darkGray,
            ChartColor.LIGHT_RED, 
            ChartColor.LIGHT_BLUE, 
            ChartColor.LIGHT_GREEN, 
            ChartColor.LIGHT_YELLOW, 
            ChartColor.LIGHT_MAGENTA, 
            ChartColor.LIGHT_CYAN, 
            Color.lightGray,
            ChartColor.VERY_DARK_RED, 
            ChartColor.VERY_DARK_BLUE,  
            ChartColor.VERY_DARK_GREEN, 
            ChartColor.VERY_DARK_YELLOW, 
            ChartColor.VERY_DARK_MAGENTA, 
            ChartColor.VERY_DARK_CYAN, 
            ChartColor.VERY_LIGHT_RED, 
            ChartColor.VERY_LIGHT_BLUE,  
            ChartColor.VERY_LIGHT_GREEN, 
            ChartColor.VERY_LIGHT_YELLOW, 
            ChartColor.VERY_LIGHT_MAGENTA, 
            ChartColor.VERY_LIGHT_CYAN 
        };
    }
    
}
