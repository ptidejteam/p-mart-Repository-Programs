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
 * ------------------------
 * XYAreaRendererState.java
 * ------------------------
 * (C) Copyright 2003, 2004, by Onject Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYAreaRendererState.java,v 1.1 2007/10/10 19:25:28 vauchers Exp $
 *
 * Changes:
 * --------
 * 05-Jan-2004 : Added standard header (DG);
 * 
 */

package org.jfree.chart.renderer;

import java.awt.Polygon;
import java.awt.geom.Line2D;

import org.jfree.chart.plot.PlotRenderingInfo;

/**
 * The state object used by the renderer for one chart drawing.  The state is set-up by the
 * initialise() method, and the plot will pass this state object to each invocation of the
 * drawItem(...) method.  At the end of drawing the chart, the state is discarded.
 * <p>
 * If a chart is being drawn to several targets simultaneously, a different state instance will 
 * be used for each drawing.
 */
public class XYAreaRendererState extends XYItemRendererState {
    
    /** Working storage for the area under one series. */
    public Polygon area;
    
    /** Working line that can be recycled. */
    public Line2D line;
    
    /**
     * Creates a new state.
     * 
     * @param info  the plot rendering info.
     */
    public XYAreaRendererState(PlotRenderingInfo info) {
        super(info);
        this.area = new Polygon();
        this.line = new Line2D.Double();
    }
    
}
