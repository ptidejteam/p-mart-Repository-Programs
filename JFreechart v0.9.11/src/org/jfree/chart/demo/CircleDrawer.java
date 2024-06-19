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
 * -----------------
 * CircleDrawer.java
 * -----------------
 * (C) Copyright 2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CircleDrawer.java,v 1.1 2007/10/10 19:09:08 vauchers Exp $
 *
 * Changes
 * -------
 * 21-May-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.jfree.ui.Drawable;

/**
 * An implementation of the Drawable interface, to illustrate the use of the XYDrawableAnnotation 
 * class.
 * 
 * @author David Gilbert
 */
public class CircleDrawer implements Drawable {

    /** The outline paint. */
    private Paint outlinePaint;

    /** The outline stroke. */
    private Stroke outlineStroke;

    /** The fill paint. */
    private Paint fillPaint;

    /**
     * Creates a new instance.
     * 
     * @param outlinePaint  the outline paint.
     * @param outlineStroke  the outline stroke.
     * @param fillPaint  the fill paint.
     */
    public CircleDrawer(Paint outlinePaint, Stroke outlineStroke, Paint fillPaint) {
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;
        this.fillPaint = fillPaint;
    }

    /**
     * Draws the circle.
     * 
     * @param g2  the graphics device.
     * @param area  the area in which to draw.
     */
    public void draw(Graphics2D g2, Rectangle2D area) {
        Ellipse2D ellipse = new Ellipse2D.Double(area.getX(), area.getY(),
                                                 area.getWidth(), area.getHeight());
        if (this.fillPaint != null) {
            g2.setPaint(this.fillPaint);
            g2.fill(ellipse);
        }
        if (this.outlinePaint != null && this.outlineStroke != null) {
            g2.setPaint(this.outlinePaint);
            g2.setStroke(this.outlineStroke);
            g2.draw(ellipse);
        }

        g2.setPaint(Color.black);
        g2.setStroke(new BasicStroke(1.0f));
        Line2D line1 = new Line2D.Double(area.getCenterX(), area.getMinY(),
                                         area.getCenterX(), area.getMaxY());
        Line2D line2 = new Line2D.Double(area.getMinX(), area.getCenterY(), 
                                         area.getMaxX(), area.getCenterY());
        g2.draw(line1);
        g2.draw(line2);
    }
}
