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
 * -------------------
 * TextAnnotation.java
 * -------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TextAnnotation.java,v 1.1 2007/10/10 19:54:31 vauchers Exp $
 *
 * Changes:
 * --------
 * 28-Aug-2002 : Version 1 (DG);
 * 07-Nov-2002 : Fixed errors reported by Checkstyle, added accessor methods (DG);
 * 13-Jan-2003 : Reviewed Javadocs (DG);
 *
 */

package com.jrefinery.chart.annotations;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Color;

/**
 * A base class for text annotations.  This class records the content but not the location of the
 * annotation.
 *
 * @author David Gilbert
 */
public class TextAnnotation {

    /** The default font. */
    public static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /** The default paint. */
    public static final Paint DEFAULT_PAINT = Color.black;

    /** The text. */
    private String text;

    /** The font. */
    private Font font;

    /** The paint. */
    private Paint paint;

    /**
     * Creates a text annotation.
     *
     * @param text  the text.
     * @param font  the font.
     * @param paint  the paint.
     */
    protected TextAnnotation(String text, Font font, Paint paint) {
        this.text = text;
        this.font = font;
        this.paint = paint;
    }

    /**
     * Returns the text for the annotation.
     *
     * @return The text.
     */
    public String getText() {
        return this.text;
    }

    /**
     * Returns the font for the annotation.
     *
     * @return The font.
     */
    public Font getFont() {
        return this.font;
    }

    /**
     * Returns the paint for the annotation.
     *
     * @return The paint.
     */
    public Paint getPaint() {
        return this.paint;
    }

}
