/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
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
 * ---------------------------
 * AbstractXYItemRenderer.java
 * ---------------------------
 * (C) Copyright 2002-2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Richard Atkinson;
 *                   Focus Computer Services Limited;
 *                   Tim Bardzil;
 *
 * $Id: AbstractXYItemRenderer.java,v 1.1 2007/10/10 20:17:13 vauchers Exp $
 *
 * Changes:
 * --------
 * 15-Mar-2002 : Version 1 (DG);
 * 09-Apr-2002 : Added a getToolTipGenerator() method reflecting the change in 
 *               the XYItemRenderer interface (DG);
 * 05-Aug-2002 : Added a urlGenerator member variable to support HTML image 
 *               maps (RA);
 * 20-Aug-2002 : Added property change events for the tooltip and URL 
 *               generators (DG);
 * 22-Aug-2002 : Moved property change support into AbstractRenderer class (DG);
 * 23-Sep-2002 : Fixed errors reported by Checkstyle tool (DG);
 * 18-Nov-2002 : Added methods for drawing grid lines (DG);
 * 17-Jan-2003 : Moved plot classes into a separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Modified initialise() return type and drawItem() method 
 *               signature (DG);
 * 15-May-2003 : Modified to take into account the plot orientation (DG);
 * 21-May-2003 : Added labels to markers (DG);
 * 05-Jun-2003 : Added domain and range grid bands (sponsored by Focus Computer 
 *               Services Ltd) (DG);
 * 27-Jul-2003 : Added getRangeType() to support stacked XY area charts (RA);
 * 31-Jul-2003 : Deprecated all but the default constructor (DG);
 * 13-Aug-2003 : Implemented Cloneable (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 29-Oct-2003 : Added workaround for font alignment in PDF output (DG);
 * 05-Nov-2003 : Fixed marker rendering bug (833623) (DG);
 * 11-Feb-2004 : Updated labelling for markers (DG);
 * 25-Feb-2004 : Added updateCrosshairValues() method.  Moved deprecated code 
 *               to bottom of source file (DG);
 * 16-Apr-2004 : Added support for IntervalMarker in drawRangeMarker() method 
 *               - thanks to Tim Bardzil (DG);
 * 05-May-2004 : Fixed bug (948310) where interval markers extend beyond axis 
 *               range (DG);
 * 03-Jun-2004 : Fixed more bugs in drawing interval markers (DG);
 * 26-Aug-2004 : Added the addEntity() method (DG);
 * 29-Sep-2004 : Added annotation support (with layers) (DG);
 * 30-Sep-2004 : Moved drawRotatedString() from RefineryUtilities --> 
 *               TextUtilities (DG);
 * 06-Oct-2004 : Added findDomainBounds() method and renamed 
 *               getRangeExtent() --> findRangeBounds() (DG);
 * 07-Jan-2005 : Removed deprecated code (DG);
 * 27-Jan-2005 : Modified getLegendItem() to omit hidden series (DG);
 * 24-Feb-2005 : Added getLegendItems() method (DG);
 * 08-Mar-2005 : Fixed positioning of marker labels (DG);
 * 20-Apr-2005 : Renamed XYLabelGenerator --> XYItemLabelGenerator and
 *               added generators for legend labels, tooltips and URLs (DG);
 * 01-Jun-2005 : Handle one dimension of the marker label adjustment 
 *               automatically (DG);
 *
 */

package org.jfree.chart.renderer.xy;

import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYSeriesLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.text.TextUtilities;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectList;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

/**
 * A base class that can be used to create new {@link XYItemRenderer} 
 * implementations.
 */
public abstract class AbstractXYItemRenderer extends AbstractRenderer
                                             implements XYItemRenderer,
                                                        Cloneable,
                                                        Serializable {

    /** For serialization. */
    private static final long serialVersionUID = 8019124836026607990L;
    
    /** The plot. */
    private XYPlot plot;
    
    /** The item label generator for ALL series. */
    private XYItemLabelGenerator itemLabelGenerator;

    /** A list of item label generators (one per series). */
    private ObjectList itemLabelGeneratorList;

    /** The base item label generator. */
    private XYItemLabelGenerator baseItemLabelGenerator;

    /** The tool tip generator for ALL series. */
    private XYToolTipGenerator toolTipGenerator;

    /** A list of tool tip generators (one per series). */
    private ObjectList toolTipGeneratorList;

    /** The base tool tip generator. */
    private XYToolTipGenerator baseToolTipGenerator;

    /** The URL text generator. */
    private XYURLGenerator urlGenerator;
    
    /** 
     * Annotations to be drawn in the background layer ('underneath' the data 
     * items). 
     */
    private List backgroundAnnotations;
    
    /** 
     * Annotations to be drawn in the foreground layer ('on top' of the data 
     * items). 
     */
    private List foregroundAnnotations;
    
    private int defaultEntityRadius;
    
    private XYSeriesLabelGenerator legendItemLabelGenerator;
    
    private XYSeriesLabelGenerator legendItemToolTipGenerator;
    
    private XYSeriesLabelGenerator legendItemURLGenerator;

    /**
     * Creates a renderer where the tooltip generator and the URL generator are
     * both <code>null</code>.
     */
    protected AbstractXYItemRenderer() {
        this.itemLabelGenerator = null;
        this.itemLabelGeneratorList = new ObjectList();
        this.toolTipGenerator = null;
        this.toolTipGeneratorList = new ObjectList();
        this.urlGenerator = null;
        this.backgroundAnnotations = new java.util.ArrayList();
        this.foregroundAnnotations = new java.util.ArrayList();
        this.defaultEntityRadius = 3;
        this.legendItemLabelGenerator 
            = new StandardXYSeriesLabelGenerator("{0}");
    }

    /**
     * Returns the number of passes through the data that the renderer requires
     * in order to draw the chart.  Most charts will require a single pass, but 
     * some require two passes.
     *
     * @return The pass count.
     */
    public int getPassCount() {
        return 1;
    }

    /**
     * Returns the plot that the renderer is assigned to.
     *
     * @return The plot.
     */
    public XYPlot getPlot() {
        return this.plot;
    }

    /**
     * Sets the plot that the renderer is assigned to.
     *
     * @param plot  the plot.
     */
    public void setPlot(XYPlot plot) {
        this.plot = plot;
    }

    /**
     * Initialises the renderer and returns a state object that should be 
     * passed to all subsequent calls to the drawItem() method.
     * <P>
     * This method will be called before the first item is rendered, giving the
     * renderer an opportunity to initialise any state information it wants to 
     * maintain.  The renderer can do nothing if it chooses.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param data  the data.
     * @param info  an optional info collection object to return data back to 
     *              the caller.
     *
     * @return The renderer state (never <code>null</code>).
     */
    public XYItemRendererState initialise(Graphics2D g2,
                                          Rectangle2D dataArea,
                                          XYPlot plot,
                                          XYDataset data,
                                          PlotRenderingInfo info) {

        XYItemRendererState state = new XYItemRendererState(info);
        return state;

    }

    // ITEM LABEL GENERATOR

    /**
     * Returns the label generator for a data item.  This implementation simply 
     * passes control to the {@link #getSeriesItemLabelGenerator(int)} method.  
     * If, for some reason, you want a different generator for individual 
     * items, you can override this method.
     *
     * @param row  the row index (zero based).
     * @param column  the column index (zero based).
     *
     * @return The generator (possibly <code>null</code>).
     */
    public XYItemLabelGenerator getItemLabelGenerator(int row, int column) {
        return getSeriesItemLabelGenerator(row);
    }

    /**
     * Returns the item label generator for a series.
     *
     * @param series  the series index (zero based).
     *
     * @return The generator (possibly <code>null</code>).
     */
    public XYItemLabelGenerator getSeriesItemLabelGenerator(int series) {

        // return the generator for ALL series, if there is one...
        if (this.itemLabelGenerator != null) {
            return this.itemLabelGenerator;
        }

        // otherwise look up the generator table
        XYItemLabelGenerator generator
            = (XYItemLabelGenerator) this.itemLabelGeneratorList.get(series);
        if (generator == null) {
            generator = this.baseItemLabelGenerator;
        }
        return generator;

    }

    /**
     * Sets the item label generator for ALL series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator (<code>null</code> permitted).
     */
    public void setItemLabelGenerator(XYItemLabelGenerator generator) {
        this.itemLabelGenerator = generator;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Sets the item label generator for a series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero based).
     * @param generator  the generator (<code>null</code> permitted).
     */
    public void setSeriesItemLabelGenerator(int series, 
                                            XYItemLabelGenerator generator) {
        this.itemLabelGeneratorList.set(series, generator);
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the base item label generator.
     *
     * @return The generator (possibly <code>null</code>).
     */
    public XYItemLabelGenerator getBaseItemLabelGenerator() {
        return this.baseItemLabelGenerator;
    }

    /**
     * Sets the base item label generator and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator (<code>null</code> permitted).
     */
    public void setBaseItemLabelGenerator(XYItemLabelGenerator generator) {
        this.baseItemLabelGenerator = generator;
        notifyListeners(new RendererChangeEvent(this));
    }

    // TOOL TIP GENERATOR

    /**
     * Returns the tool tip generator for a data item.  This implementation 
     * simply passes control to the getSeriesToolTipGenerator() method.  If, 
     * for some reason, you want a different generator for individual items, 
     * you can override this method.
     *
     * @param row  the row index (zero based).
     * @param column  the column index (zero based).
     *
     * @return The generator (possibly <code>null</code>).
     */
    public XYToolTipGenerator getToolTipGenerator(int row, int column) {
        return getSeriesToolTipGenerator(row);
    }

    /**
     * Returns the tool tip generator for a series.
     *
     * @param series  the series index (zero based).
     *
     * @return The generator (possibly <code>null</code>).
     */
    public XYToolTipGenerator getSeriesToolTipGenerator(int series) {

        // return the generator for ALL series, if there is one...
        if (this.toolTipGenerator != null) {
            return this.toolTipGenerator;
        }

        // otherwise look up the generator table
        XYToolTipGenerator generator
            = (XYToolTipGenerator) this.toolTipGeneratorList.get(series);
        if (generator == null) {
            generator = this.baseToolTipGenerator;
        }
        return generator;

    }

    /**
     * Sets the tool tip generator for ALL series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator (<code>null</code> permitted).
     */
    public void setToolTipGenerator(XYToolTipGenerator generator) {
        this.toolTipGenerator = generator;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Sets the tool tip generator for a series and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero based).
     * @param generator  the generator (<code>null</code> permitted).
     */
    public void setSeriesToolTipGenerator(int series, 
                                          XYToolTipGenerator generator) {
        this.toolTipGeneratorList.set(series, generator);
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the base tool tip generator.
     *
     * @return The generator (possibly <code>null</code>).
     */
    public XYToolTipGenerator getBaseToolTipGenerator() {
        return this.baseToolTipGenerator;
    }

    /**
     * Sets the base tool tip generator and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param generator  the generator (<code>null</code> permitted).
     */
    public void setBaseToolTipGenerator(XYToolTipGenerator generator) {
        this.baseToolTipGenerator = generator;
        notifyListeners(new RendererChangeEvent(this));
    }

    // URL GENERATOR
    
    /**
     * Returns the URL generator for HTML image maps.
     *
     * @return The URL generator (possibly <code>null</code>).
     */
    public XYURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    /**
     * Sets the URL generator for HTML image maps.
     *
     * @param urlGenerator  the URL generator (<code>null</code> permitted).
     */
    public void setURLGenerator(XYURLGenerator urlGenerator) {
        this.urlGenerator = urlGenerator;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Adds an annotation and sends a {@link RendererChangeEvent} to all 
     * registered listeners.  The annotation is added to the foreground
     * layer.
     * 
     * @param annotation  the annotation (<code>null</code> not permitted).
     */
    public void addAnnotation(XYAnnotation annotation) {
        // defer argument checking
        addAnnotation(annotation, Layer.FOREGROUND);
    }
    
    /**
     * Adds an annotation to the specified layer.
     * 
     * @param annotation  the annotation (<code>null</code> not permitted).
     * @param layer  the layer (<code>null</code> not permitted).
     */
    public void addAnnotation(XYAnnotation annotation, Layer layer) {
        if (annotation == null) {
            throw new IllegalArgumentException("Null 'annotation' argument.");
        }
        if (layer.equals(Layer.FOREGROUND)) {
            this.foregroundAnnotations.add(annotation);
            notifyListeners(new RendererChangeEvent(this));
        }
        else if (layer.equals(Layer.BACKGROUND)) {
            this.backgroundAnnotations.add(annotation);
            notifyListeners(new RendererChangeEvent(this));
        }
        else {
            // should never get here
            throw new RuntimeException("Unknown layer.");
        }
    }
    /**
     * Removes the specified annotation and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     * 
     * @param annotation  the annotation to remove (<code>null</code> not 
     *                    permitted).
     * 
     * @return A boolean to indicate whether or not the annotation was 
     *         successfully removed.
     */
    public boolean removeAnnotation(XYAnnotation annotation) {
        boolean removed = this.foregroundAnnotations.remove(annotation);
        removed = removed & this.backgroundAnnotations.remove(annotation);
        notifyListeners(new RendererChangeEvent(this));
        return removed;
    }
    
    /**
     * Removes all annotations and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     */
    public void removeAnnotations() {
        this.foregroundAnnotations.clear();
        this.backgroundAnnotations.clear();
        notifyListeners(new RendererChangeEvent(this));        
    }
    
    /**
     * Returns the radius of the circle used for the default entity area
     * when no area is specified.
     *
     * @return A radius.
     */
    public int getDefaultEntityRadius() {
        return this.defaultEntityRadius;
    }
    
    /**
     * Sets the radius of the circle used for the default entity area
     * when no area is specified.
     * 
     * @param radius  the radius.
     */
    public void setDefaultEntityRadius(int radius) {
        this.defaultEntityRadius = radius;
    }
    
    /**
     * Returns the legend item label generator.
     * 
     * @return The label generator (never <code>null</code>).
     */
    public XYSeriesLabelGenerator getLegendItemLabelGenerator() {
        return this.legendItemLabelGenerator;
    }
    
    /**
     * Sets the legend item label generator.
     * 
     * @param generator  the generator (<code>null</code> not permitted).
     */
    public void setLegendItemLabelGenerator(XYSeriesLabelGenerator generator) {
        if (generator == null) {
            throw new IllegalArgumentException("Null 'generator' argument.");
        }
        this.legendItemLabelGenerator = generator;
    }
    
    /**
     * Returns the legend item tool tip generator.
     * 
     * @return The tool tip generator (possibly <code>null</code>).
     */
    public XYSeriesLabelGenerator getLegendItemToolTipGenerator() {
        return this.legendItemToolTipGenerator;
    }
    
    /**
     * Sets the legend item tool tip generator.
     * 
     * @param generator  the generator (<code>null</code> permitted).
     */
    public void setLegendItemToolTipGenerator(XYSeriesLabelGenerator generator) 
    {
        this.legendItemToolTipGenerator = generator;
    }
    
    /**
     * Returns the legend item URL generator.
     * 
     * @return The URL generator (possibly <code>null</code>).
     */
    public XYSeriesLabelGenerator getLegendItemURLGenerator() {
        return this.legendItemURLGenerator;
    }
    
    /**
     * Sets the legend item URL generator.
     * 
     * @param generator  the generator (<code>null</code> permitted).
     */
    public void setLegendItemURLGenerator(XYSeriesLabelGenerator generator) 
    {
        this.legendItemURLGenerator = generator;
    }
    
    /**
     * Returns the lower and upper bounds (range) of the x-values in the 
     * specified dataset.
     * 
     * @param dataset  the dataset (<code>null</code> permitted).
     * 
     * @return The range (<code>null</code> if the dataset is <code>null</code>
     *         or empty).
     */
    public Range findDomainBounds(XYDataset dataset) {
        if (dataset != null) {
            return DatasetUtilities.findDomainBounds(dataset, false);
        }
        else {
            return null;
        }
    }

    /**
     * Returns the range of values the renderer requires to display all the 
     * items from the specified dataset.
     * 
     * @param dataset  the dataset (<code>null</code> permitted).
     * 
     * @return The range (<code>null</code> if the dataset is <code>null</code> 
     *         or empty).
     */
    public Range findRangeBounds(XYDataset dataset) {
        if (dataset != null) {
            return DatasetUtilities.findRangeBounds(dataset, false);
        }
        else {
            return null;
        }
    }

    /**
     * Returns a (possibly empty) collection of legend items for the series
     * that this renderer is responsible for drawing.
     *
     * @return The legend item collection (never <code>null</code>).
     */
    public LegendItemCollection getLegendItems() {
        if (this.plot == null) {
            return new LegendItemCollection();
        }
        LegendItemCollection result = new LegendItemCollection();
        int index = this.plot.getIndexOf(this);
        XYDataset dataset = this.plot.getDataset(index);
        if (dataset != null) {
            int seriesCount = dataset.getSeriesCount();
            for (int i = 0; i < seriesCount; i++) {
                if (isSeriesVisibleInLegend(i)) {
                    LegendItem item = getLegendItem(index, i);
                    if (item != null) {
                        result.add(item);
                    }
                }
            }
   
        }
        return result;
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
                String label = this.legendItemLabelGenerator.generateLabel(
                    dataset, series
                );
                String description = label;
                String toolTipText = null;
                if (getLegendItemToolTipGenerator() != null) {
                    toolTipText = getLegendItemToolTipGenerator().generateLabel(
                        dataset, series
                    );
                }
                String urlText = null;
                if (getLegendItemURLGenerator() != null) {
                    urlText = getLegendItemURLGenerator().generateLabel(
                        dataset, series
                    );
                }
                Shape shape = getSeriesShape(series);
                Paint paint = getSeriesPaint(series);
                Paint outlinePaint = getSeriesOutlinePaint(series);
                Stroke outlineStroke = getSeriesOutlineStroke(series);
                result = new LegendItem(label, description, toolTipText, 
                        urlText, shape, paint, outlineStroke, outlinePaint);
            }
        }
        return result;
    }

    /**
     * Fills a band between two values on the axis.  This can be used to color 
     * bands between the grid lines.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the domain axis.
     * @param dataArea  the data area.
     * @param start  the start value.
     * @param end  the end value.
     */
    public void fillDomainGridBand(Graphics2D g2,
                                   XYPlot plot,
                                   ValueAxis axis,
                                   Rectangle2D dataArea,
                                   double start, double end) {

        double x1 = axis.valueToJava2D(
            start, dataArea, plot.getDomainAxisEdge()
        );
        double x2 = axis.valueToJava2D(
            end, dataArea, plot.getDomainAxisEdge()
        );
        // TODO: need to change the next line to take account of plot 
        //       orientation...
        Rectangle2D band = new Rectangle2D.Double(
            x1, dataArea.getMinY(), 
            x2 - x1, dataArea.getMaxY() - dataArea.getMinY()
        );
        Paint paint = plot.getDomainTickBandPaint();

        if (paint != null) {
            g2.setPaint(paint);
            g2.fill(band);
        }

    }

    /**
     * Fills a band between two values on the range axis.  This can be used to 
     * color bands between the grid lines.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the range axis.
     * @param dataArea  the data area.
     * @param start  the start value.
     * @param end  the end value.
     */
    public void fillRangeGridBand(Graphics2D g2,
                                  XYPlot plot,
                                  ValueAxis axis,
                                  Rectangle2D dataArea,
                                  double start, double end) {

        double y1 = axis.valueToJava2D(
            start, dataArea, plot.getRangeAxisEdge()
        );
        double y2 = axis.valueToJava2D(end, dataArea, plot.getRangeAxisEdge());
        // TODO: need to change the next line to take account of the plot 
        //       orientation
        Rectangle2D band = new Rectangle2D.Double(
            dataArea.getMinX(), y2, dataArea.getWidth(), y1 - y2
        );
        Paint paint = plot.getRangeTickBandPaint();

        if (paint != null) {
            g2.setPaint(paint);
            g2.fill(band);
        }

    }

    /**
     * Draws a grid line against the range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param dataArea  the area for plotting data (not yet adjusted for any 
     *                  3D effect).
     * @param value  the value at which the grid line should be drawn.
     */
    public void drawDomainGridLine(Graphics2D g2,
                                   XYPlot plot,
                                   ValueAxis axis,
                                   Rectangle2D dataArea,
                                   double value) {

        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }

        PlotOrientation orientation = plot.getOrientation();
        double v = axis.valueToJava2D(
            value, dataArea, plot.getDomainAxisEdge()
        );
        Line2D line = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(
                dataArea.getMinX(), v, dataArea.getMaxX(), v
            );
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(
                v, dataArea.getMinY(), v, dataArea.getMaxY()
            );
        }

        Paint paint = plot.getDomainGridlinePaint();
        Stroke stroke = plot.getDomainGridlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line);

    }

    /**
     * Draws a line perpendicular to the range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param dataArea  the area for plotting data (not yet adjusted for any 3D
     *                  effect).
     * @param value  the value at which the grid line should be drawn.
     * @param paint  the paint.
     * @param stroke  the stroke.
     */
    public void drawRangeLine(Graphics2D g2,
                              XYPlot plot,
                              ValueAxis axis,
                              Rectangle2D dataArea,
                              double value,
                              Paint paint,
                              Stroke stroke) {

        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }

        PlotOrientation orientation = plot.getOrientation();
        Line2D line = null;
        double v = axis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(
                v, dataArea.getMinY(), v, dataArea.getMaxY()
            );
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(
                dataArea.getMinX(), v, dataArea.getMaxX(), v
            );
        }
        
        g2.setPaint(paint);
        g2.setStroke(stroke);
        g2.draw(line);

    }

    /**
     * Draws a vertical line on the chart to represent a 'range marker'.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param marker  the marker line.
     * @param dataArea  the axis data area.
     */
    public void drawDomainMarker(Graphics2D g2,
                                 XYPlot plot,
                                 ValueAxis domainAxis,
                                 Marker marker,
                                 Rectangle2D dataArea) {

        if (marker instanceof ValueMarker) {
            ValueMarker vm = (ValueMarker) marker;
            double value = vm.getValue();
            Range range = domainAxis.getRange();
            if (!range.contains(value)) {
                return;
            }

            double v = domainAxis.valueToJava2D(
                value, dataArea, plot.getDomainAxisEdge()
            );

            PlotOrientation orientation = plot.getOrientation();
            Line2D line = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                line = new Line2D.Double(
                    dataArea.getMinX(), v, dataArea.getMaxX(), v
                );
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                line = new Line2D.Double(
                    v, dataArea.getMinY(), v, dataArea.getMaxY()
                );
            }

            g2.setPaint(marker.getPaint());
            g2.setStroke(marker.getStroke());
            g2.draw(line);

            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                g2.setPaint(marker.getLabelPaint());
                Point2D coordinates = calculateDomainMarkerTextAnchorPoint(
                    g2, orientation, dataArea, line.getBounds2D(), 
                    marker.getLabelOffset(), 
                    LengthAdjustmentType.EXPAND, anchor
                );
                TextUtilities.drawAlignedString(
                    label, g2, 
                    (float) coordinates.getX(), (float) coordinates.getY(), 
                    marker.getLabelTextAnchor()
                );
            }
        }
        else if (marker instanceof IntervalMarker) {
            IntervalMarker im = (IntervalMarker) marker;
            double start = im.getStartValue();
            double end = im.getEndValue();
            Range range = domainAxis.getRange();
            if (!(range.intersects(start, end))) {
                return;
            }

            // don't draw beyond the axis range...
            start = range.constrain(start);
            end = range.constrain(end);

            double v0 = domainAxis.valueToJava2D(
                start, dataArea, plot.getDomainAxisEdge()
            );
            double v1 = domainAxis.valueToJava2D(
                end, dataArea, plot.getDomainAxisEdge()
            );

            PlotOrientation orientation = plot.getOrientation();
            Rectangle2D rect = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                rect = new Rectangle2D.Double(
                    dataArea.getMinX(), Math.min(v0, v1), 
                    dataArea.getWidth(), Math.abs(v1 - v0)
                );
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                rect = new Rectangle2D.Double(
                    Math.min(v0, v1), dataArea.getMinY(), 
                    Math.abs(v1 - v0), dataArea.getHeight()
                );
            }

            Paint p = marker.getPaint();
            if (p instanceof GradientPaint) {
                GradientPaint gp = (GradientPaint) p;
                GradientPaintTransformer t = im.getGradientPaintTransformer();
                if (t != null) {
                    gp = t.transform(gp, rect);  
                }
                g2.setPaint(gp);
            }
            else {
                g2.setPaint(p);
            }
            g2.fill(rect);

            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                g2.setPaint(marker.getLabelPaint());
                Point2D coordinates = calculateDomainMarkerTextAnchorPoint(
                    g2, orientation, dataArea, rect, marker.getLabelOffset(), 
                    marker.getLabelOffsetType(), anchor
                );
                TextUtilities.drawAlignedString(
                    label, g2, (float) coordinates.getX(), 
                    (float) coordinates.getY(), 
                    marker.getLabelTextAnchor()
                );
            }

        }

    }

    /**
     * Calculates the (x, y) coordinates for drawing a marker label.
     *
     * @param g2  the graphics device.
     * @param orientation  the plot orientation.
     * @param dataArea  the data area.
     * @param markerArea  the rectangle surrounding the marker area.
     * @param markerOffset  the marker label offset.
     * @param labelOffsetType  the label offset type.
     * @param anchor  the label anchor.
     *
     * @return The coordinates for drawing the marker label.
     */
    protected Point2D calculateDomainMarkerTextAnchorPoint(Graphics2D g2,
            PlotOrientation orientation,
            Rectangle2D dataArea,
            Rectangle2D markerArea,
            RectangleInsets markerOffset,
            LengthAdjustmentType labelOffsetType,
            RectangleAnchor anchor) {

        Rectangle2D anchorRect = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            anchorRect = markerOffset.createAdjustedRectangle(
                markerArea, LengthAdjustmentType.CONTRACT, labelOffsetType
            );
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            anchorRect = markerOffset.createAdjustedRectangle(
                markerArea, labelOffsetType, LengthAdjustmentType.CONTRACT
            );
        }
        return RectangleAnchor.coordinates(anchorRect, anchor);

    }

    /**
     * Draws a horizontal line across the chart to represent a 'range marker'.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param rangeAxis  the range axis.
     * @param marker  the marker line.
     * @param dataArea  the axis data area.
     */
    public void drawRangeMarker(Graphics2D g2,
                                XYPlot plot,
                                ValueAxis rangeAxis,
                                Marker marker,
                                Rectangle2D dataArea) {

        if (marker instanceof ValueMarker) {
            ValueMarker vm = (ValueMarker) marker;
            double value = vm.getValue();
            Range range = rangeAxis.getRange();
            if (!range.contains(value)) {
                return;
            }

            double v = rangeAxis.valueToJava2D(
                value, dataArea, plot.getRangeAxisEdge()
            );
            PlotOrientation orientation = plot.getOrientation();
            Line2D line = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                line = new Line2D.Double(
                    v, dataArea.getMinY(), v, dataArea.getMaxY()
                );
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                line = new Line2D.Double(
                    dataArea.getMinX(), v, dataArea.getMaxX(), v
                );
            }
            g2.setPaint(marker.getPaint());
            g2.setStroke(marker.getStroke());
            g2.draw(line);

            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                g2.setPaint(marker.getLabelPaint());
                Point2D coordinates = calculateRangeMarkerTextAnchorPoint(
                    g2, orientation, dataArea, line.getBounds2D(), 
                    marker.getLabelOffset(), 
                    LengthAdjustmentType.EXPAND, anchor
                );
                TextUtilities.drawAlignedString(
                    label, g2, (float) coordinates.getX(), 
                    (float) coordinates.getY(), 
                    marker.getLabelTextAnchor()
                );
            }
        }
        else if (marker instanceof IntervalMarker) {
            
            IntervalMarker im = (IntervalMarker) marker;
            double start = im.getStartValue();
            double end = im.getEndValue();
            Range range = rangeAxis.getRange();
            if (!(range.intersects(start, end))) {
                return;
            }

            // don't draw beyond the axis range...
            start = range.constrain(start);
            end = range.constrain(end);
            
            double v0 = rangeAxis.valueToJava2D(
                start, dataArea, plot.getRangeAxisEdge()
            );
            double v1 = rangeAxis.valueToJava2D(
                end, dataArea, plot.getRangeAxisEdge()
            );

            PlotOrientation orientation = plot.getOrientation();
            Rectangle2D rect = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                rect = new Rectangle2D.Double(
                    Math.min(v0, v1), dataArea.getMinY(), 
                    Math.abs(v1 - v0), dataArea.getHeight()
                );
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                rect = new Rectangle2D.Double(
                    dataArea.getMinX(), Math.min(v0, v1), 
                    dataArea.getWidth(), Math.abs(v0 - v1)
                );
            }

            Paint p = marker.getPaint();
            if (p instanceof GradientPaint) {
                GradientPaint gp = (GradientPaint) p;
                GradientPaintTransformer t = im.getGradientPaintTransformer();
                if (t != null) {
                    gp = t.transform(gp, rect);  
                }
                g2.setPaint(gp);
            }
            else {
                g2.setPaint(p);
            }
            g2.fill(rect);
            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                g2.setPaint(marker.getLabelPaint());
                Point2D coordinates = calculateRangeMarkerTextAnchorPoint(
                    g2, orientation, dataArea, rect, marker.getLabelOffset(), 
                    marker.getLabelOffsetType(), anchor
                );
                TextUtilities.drawAlignedString(
                    label, g2, (float) coordinates.getX(), 
                    (float) coordinates.getY(), 
                    marker.getLabelTextAnchor()
                );
            }
        } 
    }

    /**
     * Calculates the (x, y) coordinates for drawing a marker label.
     *
     * @param g2  the graphics device.
     * @param orientation  the plot orientation.
     * @param dataArea  the data area.
     * @param markerArea  the marker area.
     * @param markerOffset  the marker offset.
     * @param anchor  the label anchor.
     *
     * @return The coordinates for drawing the marker label.
     */
    private Point2D calculateRangeMarkerTextAnchorPoint(Graphics2D g2,
                                      PlotOrientation orientation,
                                      Rectangle2D dataArea,
                                      Rectangle2D markerArea,
                                      RectangleInsets markerOffset,
                                      LengthAdjustmentType labelOffsetForRange,
                                      RectangleAnchor anchor) {

        Rectangle2D anchorRect = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            anchorRect = markerOffset.createAdjustedRectangle(
                markerArea, labelOffsetForRange, LengthAdjustmentType.CONTRACT
            );
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            anchorRect = markerOffset.createAdjustedRectangle(
                markerArea, LengthAdjustmentType.CONTRACT, labelOffsetForRange
            );
        }
        return RectangleAnchor.coordinates(anchorRect, anchor);

    }

    /**
     * Returns a clone of the renderer.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if the renderer does not support 
     *         cloning.
     */
    protected Object clone() throws CloneNotSupportedException {
        AbstractXYItemRenderer clone = (AbstractXYItemRenderer) super.clone();
        // 'plot' : just retain reference, not a deep copy
        if (this.itemLabelGenerator != null 
                && this.itemLabelGenerator instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.itemLabelGenerator;
            clone.itemLabelGenerator = (XYItemLabelGenerator) pc.clone();
        }
        return clone;
    }

    /**
     * Tests this renderer for equality with another object.
     *
     * @param obj  the object.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof AbstractXYItemRenderer)) {
            return false;
        }

        AbstractXYItemRenderer renderer = (AbstractXYItemRenderer) obj;
        if (!super.equals(obj)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.itemLabelGenerator, renderer.itemLabelGenerator
        )) {
            return false;
        }
        if (!ObjectUtilities.equal(this.urlGenerator, renderer.urlGenerator)) {
            return false;
        }
        return true;
    }

    /**
     * Returns the drawing supplier from the plot.
     *
     * @return The drawing supplier (possibly <code>null</code>).
     */
    public DrawingSupplier getDrawingSupplier() {
        DrawingSupplier result = null;
        XYPlot p = getPlot();
        if (p != null) {
            result = p.getDrawingSupplier();
        }
        return result;
    }

    /**
     * Considers the current (x, y) coordinate and updates the crosshair point 
     * if it meets the criteria (usually means the (x, y) coordinate is the 
     * closest to the anchor point so far).
     *
     * @param crosshairState  the crosshair state (<code>null</code> permitted, 
     *                        but the method does nothing in that case).
     * @param x  the x-value (in data space).
     * @param y  the y-value (in data space).
     * @param transX  the x-value translated to Java2D space.
     * @param transY  the y-value translated to Java2D space.
     * @param orientation  the plot orientation (<code>null</code> not 
     *                     permitted).
     */
    protected void updateCrosshairValues(CrosshairState crosshairState,
                                         double x, double y, double transX, 
                                         double transY,
                                         PlotOrientation orientation) {

        if (orientation == null) {
            throw new IllegalArgumentException("Null 'orientation' argument.");
        }

        if (crosshairState != null) {
            // do we need to update the crosshair values?
            if (this.plot.isDomainCrosshairLockedOnData()) {
                if (this.plot.isRangeCrosshairLockedOnData()) {
                    // both axes
                    crosshairState.updateCrosshairPoint(
                        x, y, transX, transY, orientation
                    );
                }
                else {
                    // just the domain axis...
                    crosshairState.updateCrosshairX(x);
                }
            }
            else {
                if (this.plot.isRangeCrosshairLockedOnData()) {
                    // just the range axis...
                    crosshairState.updateCrosshairY(y);
                }
            }
        }

    }
    
    /**
     * Draws an item label.
     *
     * @param g2  the graphics device.
     * @param orientation  the orientation.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param x  the x coordinate (in Java2D space).
     * @param y  the y coordinate (in Java2D space).
     * @param negative  indicates a negative value (which affects the item 
     *                  label position).
     */
    protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation,
            XYDataset dataset, int series, int item, double x, double y, 
            boolean negative) {
                                     
        XYItemLabelGenerator generator = getItemLabelGenerator(series, item);
        if (generator != null) {
            Font labelFont = getItemLabelFont(series, item);
            Paint paint = getItemLabelPaint(series, item);
            g2.setFont(labelFont);
            g2.setPaint(paint);
            String label = generator.generateLabel(dataset, series, item);

            // get the label position..
            ItemLabelPosition position = null;
            if (!negative) {
                position = getPositiveItemLabelPosition(series, item);
            }
            else {
                position = getNegativeItemLabelPosition(series, item);
            }

            // work out the label anchor point...
            Point2D anchorPoint = calculateLabelAnchorPoint(
                position.getItemLabelAnchor(), x, y, orientation);
            TextUtilities.drawRotatedString(label, g2, 
                    (float) anchorPoint.getX(), (float) anchorPoint.getY(),
                    position.getTextAnchor(), position.getAngle(), 
                    position.getRotationAnchor());
        }

    }
    
    /**
     * Draws all the annotations for the specified layer.
     * 
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param layer  the layer.
     * @param info  the plot rendering info.
     */
    public void drawAnnotations(Graphics2D g2, 
                                Rectangle2D dataArea, 
                                ValueAxis domainAxis, 
                                ValueAxis rangeAxis, 
                                Layer layer, 
                                PlotRenderingInfo info) {
        
        Iterator iterator = null;
        if (layer.equals(Layer.FOREGROUND)) {
            iterator = this.foregroundAnnotations.iterator();
        }
        else if (layer.equals(Layer.BACKGROUND)) {
            iterator = this.backgroundAnnotations.iterator();
        }
        else {
            // should not get here
            throw new RuntimeException("Unknown layer.");
        }
        while (iterator.hasNext()) {
            XYAnnotation annotation = (XYAnnotation) iterator.next();
            annotation.draw(
                g2, this.plot, dataArea, domainAxis, rangeAxis, 0, info
            );
        }
        
    }

    /**
     * Adds an entity to the collection.
     * 
     * @param entities  the entity collection being populated.
     * @param area  the entity area (if <code>null</code> a default will be 
     *              used).
     * @param dataset  the dataset.
     * @param series  the series.
     * @param item  the item.
     * @param entityX  the entity's center x-coordinate in user space.
     * @param entityY  the entity's center y-coordinate in user space.
     */
    protected void addEntity(EntityCollection entities, Shape area, 
                             XYDataset dataset, int series, int item,
                             double entityX, double entityY) {
        if (!getItemCreateEntity(series, item)) {
            return;
        }
        if (area == null) {
            area = new Ellipse2D.Double(
                entityX - this.defaultEntityRadius, 
                entityY - this.defaultEntityRadius, 
                this.defaultEntityRadius * 2, this.defaultEntityRadius * 2
            );
        }
        String tip = null;
        XYToolTipGenerator generator = getToolTipGenerator(series, item);
        if (generator != null) {
            tip = generator.generateToolTip(dataset, series, item);
        }
        String url = null;
        if (getURLGenerator() != null) {
            url = getURLGenerator().generateURL(dataset, series, item);
        }
        XYItemEntity entity = new XYItemEntity(
            area, dataset, series, item, tip, url
        );
        entities.add(entity);
    }

}
