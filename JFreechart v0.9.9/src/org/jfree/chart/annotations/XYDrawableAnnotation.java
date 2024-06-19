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
 * -------------------------
 * XYDrawableAnnotation.java
 * -------------------------
 * (C) Copyright 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYDrawableAnnotation.java,v 1.1 2007/10/10 20:07:43 vauchers Exp $
 *
 * Changes:
 * --------
 * 21-May-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.annotations;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.Drawable;

/**
 * A general annotation that can be placed on an {@link org.jfree.chart.plot.XYPlot}.
 *
 * @author David Gilbert
 */
public class XYDrawableAnnotation implements XYAnnotation, Serializable {

    /** The x-coordinate. */
    private double x;

    /** The y-coordinate. */
    private double y;

    /** The width. */
    private double width;

    /** The height. */
    private double height;

    /** The drawable object. */
    private Drawable drawable;

    /**
     * Creates a new annotation to be displayed within the given area.
     *
     * @param x  the x-coordinate for the area.
     * @param y  the y-coordinate for the area.
     * @param width  the width of the area.
     * @param height  the height of the area.
     * @param drawable  the drawable object.
     */
    public XYDrawableAnnotation(double x, double y, double width, double height,
                                Drawable drawable) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.drawable = drawable;

    }

    /**
     * Draws the annotation.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     */
    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea,
                     ValueAxis domainAxis, ValueAxis rangeAxis) {

        float j2DX = (float) domainAxis.translateValueToJava2D(this.x, dataArea,
                                                               plot.getDomainAxisLocation());
        float j2DY = (float) rangeAxis.translateValueToJava2D(this.y, dataArea,
                                                              plot.getRangeAxisLocation());
        Rectangle2D area = new Rectangle2D.Double(j2DX - this.width / 2.0,
                                                  j2DY - this.height / 2.0,
                                                  this.width, this.height);
        this.drawable.draw(g2, area);

    }

}
