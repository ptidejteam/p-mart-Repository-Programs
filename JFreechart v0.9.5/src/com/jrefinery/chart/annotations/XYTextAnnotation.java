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
 * -----------------
 * XYAnnotation.java
 * -----------------
 * (C) Copyright 2002, 2003 by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: XYTextAnnotation.java,v 1.1 2007/10/10 19:54:31 vauchers Exp $
 *
 * Changes:
 * --------
 * 28-Aug-2002 : Version 1 (DG);
 * 07-Nov-2002 : Fixed errors reported by Checkstyle (DG);
 * 13-Jan-2003 : Reviewed Javadocs (DG);
 *
 */

package com.jrefinery.chart.annotations;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import com.jrefinery.chart.axis.ValueAxis;

/**
 * A text annotation that can be placed on an {@link com.jrefinery.chart.plot.XYPlot}.
 *
 * @author David Gilbert
 */
public class XYTextAnnotation extends TextAnnotation implements XYAnnotation {

    /** The x-coordinate. */
    private double x;

    /** The y-coordinate. */
    private double y;

    /**
     * Creates a new annotation to be displayed at the given coordinates.
     *
     * @param text  the text.
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     */
    public XYTextAnnotation(String text, double x, double y) {
        this(text, TextAnnotation.DEFAULT_FONT, x, y);
    }

    /**
     * Creates a new annotation to be displayed at the given coordinates.
     *
     * @param text  the text.
     * @param font  the font.
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     */
    public XYTextAnnotation(String text, Font font, double x, double y) {
        this(text, font, TextAnnotation.DEFAULT_PAINT, x, y);
    }

    /**
     * Creates a new annotation to be displayed at the given coordinates.
     *
     * @param text  the text.
     * @param font  the font.
     * @param paint  the paint.
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     */
    public XYTextAnnotation(String text, Font font, Paint paint, double x, double y) {
        super(text, font, paint);
        this.x = x;
        this.y = y;
    }

    /**
     * Draws the annotation.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     */
    public void draw(Graphics2D g2, Rectangle2D dataArea,
                     ValueAxis domainAxis, ValueAxis rangeAxis) {

        float baseX = (float) domainAxis.translateValueToJava2D(this.x, dataArea);
        float baseY = (float) rangeAxis.translateValueToJava2D(this.y, dataArea);

        //    FontRenderContext frc = g2.getFontRenderContext();
        //    Rectangle2D labelBounds = this.font.getStringBounds(this.text, frc);
        //    LineMetrics lm = this.font.getLineMetrics(this.text, frc);
        //    float labelx = baseX - labelBounds.getWidth()/2;
        //    float labely = baseY;
            //(float)(drawArea.getMaxY()-labelInsets.bottom
            //    - lm.getDescent() -lm.getLeading());
            //g2.drawString(label, labelx, labely);

        g2.setFont(getFont());
        g2.setPaint(getPaint());
        g2.drawString(getText(), baseX, baseY);

    }

}
