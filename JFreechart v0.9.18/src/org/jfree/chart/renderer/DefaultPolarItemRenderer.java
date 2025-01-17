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
 * -----------------------------
 * DefaultPolarItemRenderer.java
 * -----------------------------
 * (C) Copyright 2004, by Solution Engineering, Inc. and Contributors.
 *
 * Original Author:  Daniel Bridenbecker, Solution Engineering, Inc.;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: DefaultPolarItemRenderer.java,v 1.1 2007/10/10 19:39:14 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Jan-2004 : Version 1, contributed by DB with minor changes by DG (DG);
 *
 */

package org.jfree.chart.renderer;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.data.XYDataset;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;
import org.jfree.util.BooleanList;
import org.jfree.util.BooleanUtils;

/**
 * A renderer that can be used with the {@link PolarPlot} class.
 *
 * @author  Daniel Bridenbecker, Solution Engineering, Inc.
 */
public class DefaultPolarItemRenderer extends AbstractSeriesRenderer implements PolarItemRenderer {
       
    /** The plot that the renderer is assigned to. */
    private PolarPlot plot;

    /** Flags that control whether the renderer fills each series or not. */
    private BooleanList seriesFilled;
   
    /**
     * Creates a new instance of DefaultPolarItemRenderer
     */
    public DefaultPolarItemRenderer() {
        this.seriesFilled = new BooleanList();
    }
   
    // --------------------------------
    // --- AbstractRenderer Methods ---
    // --------------------------------
   
    /** 
     * Returns the drawing supplier from the plot.
     *
     * @return The drawing supplier.
     */
    public DrawingSupplier getDrawingSupplier() {
        DrawingSupplier result = null;
        PolarPlot p = getPlot();
        if (p != null) {
            result = p.getDrawingSupplier();
        }
        return result;
    }
   
    // ----------------------
    // --- Public Methods ---
    // ----------------------
    /**
     * Set the plot associated with this renderer.
     * 
     * @param plot  the plot.
     */
    public void setPlot(PolarPlot plot) {
        this.plot = plot;
    }

    /**
     * Return the plot associated with this renderer.
     * 
     * @return The plot.
     */
    public PolarPlot getPlot() {
        return this.plot;
    }

    /**
     * Plots the data for a given series.
     * 
     * @param g2  the drawing surface.
     * @param dataArea  the data area.
     * @param info  collects plot rendering info.
     * @param plot  the plot.
     * @param dataset  the dataset.
     * @param seriesIndex  the series index.
     */
    public void drawSeries(Graphics2D g2, 
                           Rectangle2D dataArea, 
                           PlotRenderingInfo info,
                           PolarPlot plot,
                           XYDataset dataset,
                           int seriesIndex) {
        
        Polygon poly = new Polygon();
        int numPoints = dataset.getItemCount(seriesIndex);
        for (int i = 0; i < numPoints; i++) {
            double theta = dataset.getXValue(seriesIndex, i).doubleValue();
            double radius = dataset.getYValue(seriesIndex, i).doubleValue();
            Point p = plot.translateValueThetaRadiusToJava2D(theta, radius, dataArea);
            poly.addPoint(p.x, p.y);
        }
        g2.setPaint(getSeriesPaint(seriesIndex));
        g2.setStroke(getSeriesStroke(seriesIndex));
        if (isSeriesFilled(seriesIndex)) {
            Composite savedComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2.fill(poly);
            g2.setComposite(savedComposite);
        }
        else {
            g2.draw(poly);
        }
    }

    /**
     * Returns <code>true</code> if the renderer should fill the specified series, and 
     * <code>false</code> otherwise.
     * 
     * @param series  the series index (zero-based).
     * 
     * @return A boolean.
     */
    public boolean isSeriesFilled(int series) {
        boolean result = false;
        Boolean b = this.seriesFilled.getBoolean(series);
        if (b != null) {
            result = b.booleanValue();
        }
        return result;
    }

    /**
     * Sets a flag that controls whether or not a series is filled.
     * 
     * @param series  the series index.
     * @param filled  the flag.
     */
    public void setSeriesFilled(int series, boolean filled) {
        this.seriesFilled.setBoolean(series, BooleanUtils.valueOf(filled));
    }
    
    /**
     * Draw the angular gridlines - the spokes.
     * 
     * @param g2  the drawing surface.
     * @param plot  the plot.
     * @param ticks  the ticks.
     * @param dataArea  the data area.
     */
    public void drawAngularGridLines(Graphics2D g2, 
                                     PolarPlot plot, 
                                     List ticks,
                                     Rectangle2D dataArea) {
        
        g2.setFont(plot.getAngleLabelFont());
        g2.setStroke(plot.getAngleGridlineStroke());
        g2.setPaint(plot.getAngleGridlinePaint());
      
        double maxRadius = plot.getMaxRadius();

        Point center = plot.translateValueThetaRadiusToJava2D(0.0, 0.0, dataArea);
        Iterator iterator = ticks.iterator();
        while (iterator.hasNext()) {
            NumberTick tick = (NumberTick) iterator.next();
            Point p = plot.translateValueThetaRadiusToJava2D(
                tick.getNumber().doubleValue(), maxRadius, dataArea
            );
            g2.setPaint(plot.getAngleGridlinePaint());
            g2.drawLine(center.x, center.y, p.x, p.y);
            if (plot.isAngleLabelsVisible()) {
                int x = p.x;
                int y = p.y;
                g2.setPaint(plot.getAngleLabelPaint());
                RefineryUtilities.drawAlignedString(
                    tick.getText(), g2, x, y, TextAnchor.CENTER
                );
            }
        }
     }

    /**
     * Draw the radial gridlines - the rings.
     * 
     * @param g2  the drawing surface.
     * @param plot  the plot.
     * @param radialAxis  the radial axis.
     * @param ticks  the ticks.
     * @param dataArea  the data area.
     */
    public void drawRadialGridLines(Graphics2D g2, 
                                    PolarPlot plot,
                                    ValueAxis radialAxis,
                                    List ticks,
                                    Rectangle2D dataArea) {
        
        g2.setFont(radialAxis.getTickLabelFont());
        g2.setPaint(plot.getRadiusGridlinePaint());
        g2.setStroke(plot.getRadiusGridlineStroke());

        Point center = plot.translateValueThetaRadiusToJava2D(0.0, 0.0, dataArea);
        
        Iterator iterator = ticks.iterator();
        while (iterator.hasNext()) {
            NumberTick tick = (NumberTick) iterator.next();
            Point p = plot.translateValueThetaRadiusToJava2D(
                90.0, tick.getNumber().doubleValue(), dataArea
            );
            int r = p.x - center.x;
            int upperLeftX = center.x - r;
            int upperLeftY = center.y - r;
            int d = 2 * r;
            Ellipse2D ring = new Ellipse2D.Double(upperLeftX, upperLeftY, d, d);
            g2.setPaint(plot.getRadiusGridlinePaint());
            g2.draw(ring);
        }
    }

    /**
     * Return the legend for the given series.
     * 
     * @param series  the series index.
     * 
     * @return The legend item.
     */
    public LegendItem getLegendItem(int series) {
        LegendItem result = null;
      
        PolarPlot polarPlot = getPlot();
        if (polarPlot != null) {
            XYDataset dataset;
            dataset = polarPlot.getDataset();
            if (dataset != null) {
                String label = dataset.getSeriesName(series);
                String description = label;
                Shape shape = getSeriesShape(series);
                Paint paint = getSeriesPaint(series);
                Paint outlinePaint = getSeriesOutlinePaint(series);
                Stroke stroke = getSeriesStroke(series);
                result = new LegendItem(
                    label, 
                    description,
                    shape,
                    true,
                    paint, 
                    stroke,
                    outlinePaint, 
                    stroke
                );
            }
        }
        return result;
    }

}
