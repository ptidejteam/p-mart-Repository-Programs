/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2006, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * --------------------
 * XYAreaRenderer2.java
 * --------------------
 * (C) Copyright 2004-2006, by Hari and Contributors.
 *
 * Original Author:  Hari (ourhari@hotmail.com);
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Richard Atkinson;
 *                   Christian W. Zuckschwerdt;
 *
 * $Id: XYAreaRenderer2.java,v 1.1 2007/10/10 20:29:43 vauchers Exp $
 *
 * Changes:
 * --------
 * 03-Apr-2002 : Version 1, contributed by Hari.  This class is based on the 
 *               StandardXYItemRenderer class (DG);
 * 09-Apr-2002 : Removed the translated zero from the drawItem method - 
 *               overridden the initialise() method to calculate it (DG);
 * 30-May-2002 : Added tool tip generator to constructor to match super 
 *               class (DG);
 * 25-Jun-2002 : Removed unnecessary local variable (DG);
 * 05-Aug-2002 : Small modification to drawItem method to support URLs for 
 *               HTML image maps (RA);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 07-Nov-2002 : Renamed AreaXYItemRenderer --> XYAreaRenderer (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Modified drawItem() method signature (DG);
 * 27-Jul-2003 : Made line and polygon properties protected rather than 
 *               private (RA);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 20-Aug-2003 : Implemented Cloneable and PublicCloneable (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 07-Oct-2003 : Added renderer state (DG);
 * 08-Dec-2003 : Modified hotspot for chart entity (DG);
 * 10-Feb-2004 : Changed the drawItem() method to make cut-and-paste 
 *               overriding easier.  Also moved state class into this 
 *               class (DG);
 * 25-Feb-2004 : Replaced CrosshairInfo with CrosshairState.  Renamed 
 *               XYToolTipGenerator --> XYItemLabelGenerator (DG);
 * 15-Jul-2004 : Switched getX() with getXValue() and getY() with 
 *               getYValue() (DG);
 * 11-Nov-2004 : Now uses ShapeUtilities to translate shapes (DG);
 * 19-Jan-2005 : Now accesses only primitives from the dataset (DG);
 * 21-Mar-2005 : Override getLegendItem() (DG);
 * 20-Apr-2005 : Use generators for legend tooltips and URLs (DG);
 * 
 */

package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.util.PublicCloneable;

/**
 * Area item renderer for an {@link XYPlot}.  
 */
public class XYAreaRenderer2 extends AbstractXYItemRenderer 
                             implements XYItemRenderer, 
                                        Cloneable,
                                        PublicCloneable,
                                        Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -7378069681579984133L;

    /** A flag indicating whether or not lines are drawn between XY points. */
    private boolean plotLines;

    /** A flag that controls whether or not the outline is shown. */
    private boolean showOutline;

    /** 
     * The shape used to represent an area in each legend item (this should 
     * never be <code>null</code>). 
     */
    private transient Shape legendArea;

    /**
     * Constructs a new renderer.
     */
    public XYAreaRenderer2() {
        this(null, null);
    }

    /**
     * Constructs a new renderer.
     * <p>
     * To specify the type of renderer, use one of the constants: SHAPES, LINES,
     * SHAPES_AND_LINES, AREA or AREA_AND_SHAPES.
     *
     * @param labelGenerator  the tool tip generator to use.  <code>null</code> 
     *                        is none.
     * @param urlGenerator  the URL generator (null permitted).
     */
    public XYAreaRenderer2(XYToolTipGenerator labelGenerator, 
                           XYURLGenerator urlGenerator) {
        super();
        this.plotLines = false;
        this.showOutline = false;
        setBaseToolTipGenerator(labelGenerator);
        setURLGenerator(urlGenerator);
        GeneralPath area = new GeneralPath();
        area.moveTo(0.0f, -4.0f);
        area.lineTo(3.0f, -2.0f);
        area.lineTo(4.0f, 4.0f);
        area.lineTo(-4.0f, 4.0f);
        area.lineTo(-3.0f, -2.0f);
        area.closePath();
        this.legendArea = area;
    }

    /**
     * Returns a flag that controls whether or not outlines of the areas are 
     * drawn.
     *
     * @return The flag.
     */
    public boolean isOutline() {
        return this.showOutline;
    }

    /**
     * Sets a flag that controls whether or not outlines of the areas are drawn.
     *
     * @param show  the flag.
     */
    public void setOutline(boolean show) {
        this.showOutline = show;
    }

    /**
     * Returns true if lines are being plotted by the renderer.
     *
     * @return <code>true</code> if lines are being plotted by the renderer.
     */
    public boolean getPlotLines() {
        return this.plotLines;
    }

    /**
     * Returns the shape used to represent an area in the legend.
     * 
     * @return The legend area (never <code>null</code>).
     */
    public Shape getLegendArea() {
        return this.legendArea;   
    }
    
    /**
     * Sets the shape used as an area in each legend item and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param area  the area (<code>null</code> not permitted).
     */
    public void setLegendArea(Shape area) {
        if (area == null) {
            throw new IllegalArgumentException("Null 'area' argument.");   
        }
        this.legendArea = area;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns a default legend item for the specified series.  Subclasses 
     * should override this method to generate customised items.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return A legend item for the series.
     */
    public LegendItem getLegendItem(int datasetIndex, int series) {
        LegendItem result = null;
        XYPlot xyplot = getPlot();
        if (xyplot != null) {
            XYDataset dataset = xyplot.getDataset(datasetIndex);
            if (dataset != null) {
                XYSeriesLabelGenerator lg = getLegendItemLabelGenerator();
                String label = lg.generateLabel(dataset, series);
                String description = label;
                String toolTipText = null;
                if (getLegendItemToolTipGenerator() != null) {
                    toolTipText = getLegendItemToolTipGenerator().generateLabel(
                            dataset, series);
                }
                String urlText = null;
                if (getLegendItemURLGenerator() != null) {
                    urlText = getLegendItemURLGenerator().generateLabel(
                            dataset, series);
                }
                Paint paint = getSeriesPaint(series);
                result = new LegendItem(label, description, toolTipText, 
                        urlText, this.legendArea, paint);
            }
        }
        return result;
    }
    
    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color 
     *              information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot 
     *                        (<code>null</code> permitted).
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2,
                         XYItemRendererState state,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairState crosshairState,
                         int pass) {
        
        if (!getItemVisible(series, item)) {
            return;   
        }
        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1)) {
            y1 = 0.0;
        }
        
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, 
                plot.getDomainAxisEdge());
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, 
                plot.getRangeAxisEdge());
        
        // get the previous point and the next point so we can calculate a 
        // "hot spot" for the area (used by the chart entity)...
        double x0 = dataset.getXValue(series, Math.max(item - 1, 0));
        double y0 = dataset.getYValue(series, Math.max(item - 1, 0));
        if (Double.isNaN(y0)) {
            y0 = 0.0;
        }
        double transX0 = domainAxis.valueToJava2D(x0, dataArea, 
                plot.getDomainAxisEdge());
        double transY0 = rangeAxis.valueToJava2D(y0, dataArea, 
                plot.getRangeAxisEdge());
        
        int itemCount = dataset.getItemCount(series);
        double x2 = dataset.getXValue(series, Math.min(item + 1, 
                itemCount - 1));
        double y2 = dataset.getYValue(series, Math.min(item + 1, 
                itemCount - 1));
        if (Double.isNaN(y2)) {
            y2 = 0.0;
        }
        double transX2 = domainAxis.valueToJava2D(x2, dataArea, 
                plot.getDomainAxisEdge());
        double transY2 = rangeAxis.valueToJava2D(y2, dataArea, 
                plot.getRangeAxisEdge());
        
        double transZero = rangeAxis.valueToJava2D(0.0, dataArea, 
                plot.getRangeAxisEdge());
        Polygon hotspot = null;
        if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
            hotspot = new Polygon();
            hotspot.addPoint((int) transZero, 
                    (int) ((transX0 + transX1) / 2.0));
            hotspot.addPoint((int) ((transY0 + transY1) / 2.0), 
                    (int) ((transX0 + transX1) / 2.0));
            hotspot.addPoint((int) transY1, (int) transX1);
            hotspot.addPoint((int) ((transY1 + transY2) / 2.0), 
                    (int) ((transX1 + transX2) / 2.0));
            hotspot.addPoint((int) transZero, 
                    (int) ((transX1 + transX2) / 2.0));
        }
        else {  // vertical orientation
            hotspot = new Polygon();
            hotspot.addPoint((int) ((transX0 + transX1) / 2.0), 
                    (int) transZero);
            hotspot.addPoint((int) ((transX0 + transX1) / 2.0), 
                    (int) ((transY0 + transY1) / 2.0));
            hotspot.addPoint((int) transX1, (int) transY1);
            hotspot.addPoint((int) ((transX1 + transX2) / 2.0), 
                    (int) ((transY1 + transY2) / 2.0));
            hotspot.addPoint((int) ((transX1 + transX2) / 2.0), 
                    (int) transZero);
        }
                
        PlotOrientation orientation = plot.getOrientation();
        Paint paint = getItemPaint(series, item);
        Stroke stroke = getItemStroke(series, item);
        g2.setPaint(paint);
        g2.setStroke(stroke);

        if (getPlotLines()) {
            if (item > 0) {
                if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    state.workingLine.setLine(transX0, transY0, transX1, 
                            transY1);
                }
                else if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                    state.workingLine.setLine(transY0, transX0, transY1, 
                            transX1);
                }
                g2.draw(state.workingLine);
            }
        }

        // Check if the item is the last item for the series.
        // and number of items > 0.  We can't draw an area for a single point.
        g2.fill(hotspot);

        // draw an outline around the Area.
        if (isOutline()) {
            g2.setStroke(getSeriesOutlineStroke(series));
            g2.setPaint(getSeriesOutlinePaint(series));
            g2.draw(hotspot);
        }
        updateCrosshairValues(
            crosshairState, x1, y1, transX1, transY1, orientation
        );
        
        // collect entity and tool tip information...
        if (state.getInfo() != null) {
            EntityCollection entities = state.getEntityCollection();
            if (entities != null && hotspot != null) {
                String tip = null;
                XYToolTipGenerator generator = getToolTipGenerator(
                    series, item
                );
                if (generator != null) {
                    tip = generator.generateToolTip(dataset, series, item);
                }
                String url = null;
                if (getURLGenerator() != null) {
                    url = getURLGenerator().generateURL(dataset, series, item);
                }
                XYItemEntity entity = new XYItemEntity(hotspot, dataset, 
                        series, item, tip, url);
                entities.add(entity);
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
    
    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) 
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendArea = SerialUtilities.readShape(stream);
    }
    
    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.legendArea, stream);
    }

}

