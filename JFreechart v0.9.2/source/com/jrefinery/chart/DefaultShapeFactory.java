/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * DefaultShapeFactory.java
 * ------------------------
 * (C) Copyright 2002, by Jeremy Bowman.
 *
 * Original Author:  Jeremy Bowman;
 * Contributor(s):   -;
 *
 * $Id: DefaultShapeFactory.java,v 1.1 2007/10/10 19:41:58 vauchers Exp $
 *
 * Changes
 * -------
 * 13-May-2002 : Version 1 (JB);
 *
 */

package com.jrefinery.chart;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * Default provider of shapes for indicating data points on a Plot.
 *
 * @author Jeremy Bowman
 */
public class DefaultShapeFactory implements ShapeFactory {

    /**
     * Returns a Shape that can be used in plotting data.  Used in XYPlots.
     */
    public Shape getShape(int series, int item, double x, double y,
                          double scale) {

        if (series==0) {
            return new Rectangle2D.Double(x-0.5*scale, y-0.5*scale, scale, scale);
        }
        else {
            return new Ellipse2D.Double(x-0.5*scale, y-0.5*scale, scale, scale);
        }

    }

    /**
     * Returns a Shape that can be used in plotting data.  Used in
     * CategoryPlots.
     */
    public Shape getShape(int series, Object category, double x, double y,
                          double scale) {

        return new Ellipse2D.Double(x-0.5*scale, y-0.5*scale, scale, scale);

    }
}
