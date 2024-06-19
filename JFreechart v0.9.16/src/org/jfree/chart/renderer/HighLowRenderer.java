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
 * --------------------
 * HighLowRenderer.java
 * --------------------
 * (C) Copyright 2001-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Richard Atkinson;
 *                   Christian W. Zuckschwerdt;
 *
 * $Id: HighLowRenderer.java,v 1.1 2007/10/10 19:25:28 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1 (DG);
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem(...) method (DG);
 * 28-Mar-2002 : Added a property change listener mechanism so that renderers no longer need to be
 *               immutable (DG);
 * 09-Apr-2002 : Removed translatedRangeZero from the drawItem(...) method, and changed the return
 *               type of the drawItem method to void, reflecting a change in the XYItemRenderer
 *               interface.  Added tooltip code to drawItem(...) method (DG);
 * 05-Aug-2002 : Small modification to drawItem method to support URLs for HTML image maps (RA);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Modified drawItem(...) method signature (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 31-Jul-2003 : Deprecated constructor (DG);
 * 20-Aug-2003 : Implemented Cloneable and PublicCloneable (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 
 */

package org.jfree.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.CrosshairInfo;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.HighLowDataset;
import org.jfree.data.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * A renderer that draws high/low/open/close markers on an {@link XYPlot} (requires
 * a {@link HighLowDataset}).
 *
 * @author David Gilbert
 */
public class HighLowRenderer extends AbstractXYItemRenderer implements XYItemRenderer, 
                                                                       Cloneable,
                                                                       PublicCloneable,
                                                                       Serializable {

    /**
     * The default constructor.
     */
    public HighLowRenderer() {
        super();
    }

    /**
     * Creates a new renderer with the specified tool tip generator.
     *
     * @param toolTipGenerator  the tool tip generator.
     * 
     * @deprecated Use default constructor then set tooltip generator.
     */
    public HighLowRenderer(XYToolTipGenerator toolTipGenerator) {
        super();
        setToolTipGenerator(toolTipGenerator);
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairInfo  information about crosshairs on a plot.
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2,
                         XYItemRendererState state,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                         XYDataset dataset, int series, int item,
                         CrosshairInfo crosshairInfo,
                         int pass) {

        // setup for collecting optional entity info...
        Shape entityArea = null;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }

        HighLowDataset highLowData = (HighLowDataset) dataset;

        Number x = highLowData.getXValue(series, item);
        Number yHigh  = highLowData.getHighValue(series, item);
        Number yLow   = highLowData.getLowValue(series, item);
        Number yOpen  = highLowData.getOpenValue(series, item);
        Number yClose = highLowData.getCloseValue(series, item);

        double xx = domainAxis.translateValueToJava2D(x.doubleValue(), dataArea, 
                                                      plot.getDomainAxisEdge());

        RectangleEdge location = plot.getRangeAxisEdge();
        double yyHigh = rangeAxis.translateValueToJava2D(yHigh.doubleValue(), dataArea, location);
        double yyLow = rangeAxis.translateValueToJava2D(yLow.doubleValue(), dataArea, location);
        double yyOpen = rangeAxis.translateValueToJava2D(yOpen.doubleValue(), dataArea, location);
        double yyClose = rangeAxis.translateValueToJava2D(yClose.doubleValue(), dataArea, location);

        Paint p = getItemPaint(series, item);
        Stroke s = getItemStroke(series, item);

        HighLow hl = new HighLow(xx, yyHigh, yyLow, yyOpen, yyClose, s, p);
        Line2D l1 = hl.getOpenTickLine();
        Line2D l2 = hl.getLine();
        Line2D l3 = hl.getCloseTickLine();

        g2.setPaint(p);
        g2.setStroke(s);
        g2.draw(l1);
        g2.draw(l2);
        g2.draw(l3);

        // add an entity for the item...
        if (entities != null) {
            if (entityArea == null) {
                entityArea = hl.getBounds();
            }
            String tip = null;
            if (getToolTipGenerator() != null) {
                tip = getToolTipGenerator().generateToolTip(dataset, series, item);
            }
            String url = null;
            if (getURLGenerator() != null) {
                url = getURLGenerator().generateURL(dataset, series, item);
            }
            XYItemEntity entity = new XYItemEntity(entityArea, dataset, series, item, tip, url);
            entities.addEntity(entity);
        }

    }
    
    /**
     * Returns a clone of the renderer.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException  if the renderer cannot be cloned.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
