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
 * XYStepRenderer.java
 * -------------------
 * (C) Copyright 2002, 2003, by Roger Studner and Contributors.
 *
 * Original Author:  Roger Studner;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: XYStepRenderer.java,v 1.1 2007/10/10 20:00:08 vauchers Exp $
 *
 * Changes
 * -------
 * 13-May-2002 : Version 1, contributed by Roger Studner (DG);
 * 25-Jun-2002 : Updated import statements (DG);
 * 22-Jul-2002 : Added check for null data items (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 *
 */
package com.jrefinery.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.CrosshairInfo;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.plot.XYPlot;
import com.jrefinery.chart.tooltips.XYToolTipGenerator;
import com.jrefinery.chart.urls.XYURLGenerator;
import com.jrefinery.data.XYDataset;

/**
 * Line/Step item renderer for an {@link XYPlot}.  This class draws lines between data
 * points, only allowing horizontal or vertical lines (steps).
 *
 * @author Roger Studner
 */
public class XYStepRenderer extends AbstractXYItemRenderer 
                            implements XYItemRenderer, Serializable {

    /** A working line (to save creating many instances). */
    private transient Line2D line;

    /**
     * Constructs a new renderer with no tooltip or URL generation.
     */
    public XYStepRenderer() {

        this(null, null);
        
    }

    /**
     * Constructs a new renderer.
     * 
     * @param toolTipGenerator  the tooltip generator.
     * @param urlGenerator  the URL generator.
     */
    public XYStepRenderer(XYToolTipGenerator toolTipGenerator,
                          XYURLGenerator urlGenerator) {
    
        super(toolTipGenerator, urlGenerator);
        this.line = new Line2D.Double(0.0, 0.0, 0.0, 0.0);
            
    }
                          
    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param horizontalAxis  the horizontal axis.
     * @param verticalAxis  the vertical axis.
     * @param dataset  the dataset.
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairInfo collects information about the crosshairs.
     */
    public void drawItem(Graphics2D g2, Rectangle2D dataArea, ChartRenderingInfo info,
                         XYPlot plot, ValueAxis horizontalAxis, ValueAxis verticalAxis,
                         XYDataset dataset, int datasetIndex, int series, int item,
                         CrosshairInfo crosshairInfo) {

        Paint seriesPaint = getItemPaint(datasetIndex, series, item);
        Stroke seriesStroke = getItemStroke(datasetIndex, series, item);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        // get the data point...
        Number x1 = dataset.getXValue(series, item);
        Number y1 = dataset.getYValue(series, item);
        if (y1 == null) {
            return;
        }

        double transX1 = horizontalAxis.translateValueToJava2D(x1.doubleValue(), dataArea);
        double transY1 = verticalAxis.translateValueToJava2D(y1.doubleValue(), dataArea);

        if (item > 0) {
            // get the previous data point...
            Number x0 = dataset.getXValue(series, item - 1);
            Number y0 = dataset.getYValue(series, item - 1);
            if (y0 != null) {
                double transX0 = horizontalAxis.translateValueToJava2D(x0.doubleValue(), dataArea);
                double transY0 = verticalAxis.translateValueToJava2D(y0.doubleValue(), dataArea);

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

        // do we need to update the crosshair values?
        if (plot.isDomainCrosshairLockedOnData()) {
            if (plot.isRangeCrosshairLockedOnData()) {
                // both crosshairs
                crosshairInfo.updateCrosshairPoint(x1.doubleValue(), y1.doubleValue());
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

    }

}
