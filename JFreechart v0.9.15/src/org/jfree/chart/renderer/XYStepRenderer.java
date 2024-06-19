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
 * -------------------
 * XYStepRenderer.java
 * -------------------
 * (C) Copyright 2002, 2003, by Roger Studner and Contributors.
 *
 * Original Author:  Roger Studner;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Matthias Rose;
 *
 * $Id: XYStepRenderer.java,v 1.1 2007/10/10 19:21:54 vauchers Exp $
 *
 * Changes
 * -------
 * 13-May-2002 : Version 1, contributed by Roger Studner (DG);
 * 25-Jun-2002 : Updated import statements (DG);
 * 22-Jul-2002 : Added check for null data items (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Modified drawItem(...) method signature (DG);
 * 20-Aug-2003 : Implemented Cloneable and PublicCloneable (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 28-Oct-2003 : Added tooltips, code contributed by Matthias Rose (RFE 824857) (DG);
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
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * Line/Step item renderer for an {@link XYPlot}.  This class draws lines between data
 * points, only allowing horizontal or vertical lines (steps).
 *
 * @author Roger Studner
 */
public class XYStepRenderer extends AbstractXYItemRenderer implements XYItemRenderer, 
                                                                      Cloneable,
                                                                      PublicCloneable,
                                                                      Serializable {

    /** A working line (to save creating many instances). */
    private transient Line2D line;

    /**
     * Constructs a new renderer with no tooltip or URL generation.
     */
    public XYStepRenderer() {
        super();
        this.line = new Line2D.Double(0.0, 0.0, 0.0, 0.0);
    }

    /**
     * Constructs a new renderer.
     *
     * @param toolTipGenerator  the tooltip generator.
     * @param urlGenerator  the URL generator.
     */
    public XYStepRenderer(XYToolTipGenerator toolTipGenerator,
                          XYURLGenerator urlGenerator) {

        
        super();
        setToolTipGenerator(toolTipGenerator);
        setURLGenerator(urlGenerator);
        this.line = new Line2D.Double(0.0, 0.0, 0.0, 0.0);

    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param horizontalAxis  the horizontal axis.
     * @param verticalAxis  the vertical axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairInfo collects information about the crosshairs.
     * @param pass  the pass index (ignored here).
     */
    public void drawItem(Graphics2D g2, 
                         XYItemRendererState state,
                         Rectangle2D dataArea, 
                         PlotRenderingInfo info,
                         XYPlot plot, 
                         ValueAxis horizontalAxis, 
                         ValueAxis verticalAxis,
                         XYDataset dataset, 
                         int series, 
                         int item,
                         CrosshairInfo crosshairInfo, 
                         int pass) {

        Paint seriesPaint = getItemPaint(series, item);
        Stroke seriesStroke = getItemStroke(series, item);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        // get the data point...
        Number x1 = dataset.getXValue(series, item);
        Number y1 = dataset.getYValue(series, item);
        if (y1 == null) {
            return;
        }

        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double transX1 = horizontalAxis.translateValueToJava2D(x1.doubleValue(), dataArea, 
                                                               xAxisLocation);
        double transY1 = verticalAxis.translateValueToJava2D(y1.doubleValue(), dataArea, 
                                                             yAxisLocation);

        if (item > 0) {
            // get the previous data point...
            Number x0 = dataset.getXValue(series, item - 1);
            Number y0 = dataset.getYValue(series, item - 1);
            if (y0 != null) {
                double transX0 = horizontalAxis.translateValueToJava2D(x0.doubleValue(), dataArea, 
                                                                       xAxisLocation);
                double transY0 = verticalAxis.translateValueToJava2D(y0.doubleValue(), dataArea, 
                                                                     yAxisLocation);

                PlotOrientation orientation = plot.getOrientation();
                if (orientation == PlotOrientation.HORIZONTAL) {
                    if (transY0 == transY1) { //this represents the situation for drawing a
                                              //horizontal bar.
                        line.setLine(transY0, transX0, transY1, transX1);
                        g2.draw(line);
                    }
                    else {  //this handles the need to perform a 'step'.
                        line.setLine(transY0, transX0, transY1, transX0);
                        g2.draw(line);
                        line.setLine(transY1, transX0, transY1, transX1);
                        g2.draw(line);
                    }
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    if (transY0 == transY1) { //this represents the situation for drawing a
                                              //horizontal bar.
                        line.setLine(transX0, transY0, transX1, transY1);
                        g2.draw(line);
                    }
                    else {  //this handles the need to perform a 'step'.
                        line.setLine(transX0, transY0, transX1, transY0);
                        g2.draw(line);
                        line.setLine(transX1, transY0, transX1, transY1);
                        g2.draw(line);
                    }
                }

            }
        }

        // do we need to update the crosshair values?
        if (plot.isDomainCrosshairLockedOnData()) {
            if (plot.isRangeCrosshairLockedOnData()) {
                // both crosshairs
                crosshairInfo.updateCrosshairPoint(x1.doubleValue(), y1.doubleValue(),
                                                   transX1, transY1);
            }
            else {
                // just the horizontal axis...
                crosshairInfo.updateCrosshairX(x1.doubleValue());

            }
        }
        else {
            if (plot.isRangeCrosshairLockedOnData()) {
                // just the vertical axis...
                crosshairInfo.updateCrosshairY(y1.doubleValue());
            }
        }
        // collect entity and tool tip information...
       
        if (state.getInfo() != null) {
            EntityCollection entities = state.getInfo().getOwner().getEntityCollection();
            if (entities != null) {
                Shape shape = plot.getOrientation() == PlotOrientation.VERTICAL
                    ? new Rectangle2D.Double(transX1 - 2, transY1 - 2, 4.0, 4.0)
                    : new Rectangle2D.Double(transY1 - 2, transX1 - 2, 4.0, 4.0);           
                if (shape != null) {
                    String tip = null;
                    if (getToolTipGenerator() != null) {
                        tip = getToolTipGenerator().generateToolTip(dataset, series, item);
                    }
                    String url = null;
                    if (getURLGenerator() != null) {
                        url = getURLGenerator().generateURL(dataset, series, item);
                    }
                    XYItemEntity entity = new XYItemEntity(shape, dataset, series, item, tip, url);
                    entities.addEntity(entity);
                }
            }
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
