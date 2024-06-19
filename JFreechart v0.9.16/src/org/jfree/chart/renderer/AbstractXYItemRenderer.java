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
 * ---------------------------
 * AbstractXYItemRenderer.java
 * ---------------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Richard Atkinson;
 *                   Focus Computer Services Limited;
 *
 * $Id: AbstractXYItemRenderer.java,v 1.1 2007/10/10 19:25:28 vauchers Exp $
 *
 * Changes:
 * --------
 * 15-Mar-2002 : Version 1 (DG);
 * 09-Apr-2002 : Added a getToolTipGenerator() method reflecting the change in the XYItemRenderer
 *               interface (DG);
 * 05-Aug-2002 : Added a urlGenerator member variable to support HTML image maps (RA);
 * 20-Aug-2002 : Added property change events for the tooltip and URL generators (DG);
 * 22-Aug-2002 : Moved property change support into AbstractRenderer class (DG);
 * 23-Sep-2002 : Fixed errors reported by Checkstyle tool (DG);
 * 18-Nov-2002 : Added methods for drawing grid lines (DG);
 * 17-Jan-2003 : Moved plot classes into a separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Modified initialise(...) return type and drawItem(...) method signature (DG);
 * 15-May-2003 : Modified to take into account the plot orientation (DG);
 * 21-May-2003 : Added labels to markers (DG);
 * 05-Jun-2003 : Added domain and range grid bands (sponsored by Focus Computer Services Ltd) (DG);
 * 27-Jul-2003 : Added getRangeType() to support stacked XY area charts (RA);
 * 31-Jul-2003 : Deprecated all but the default constructor (DG);
 * 13-Aug-2003 : Implemented Cloneable (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 29-Oct-2003 : Added workaround for font alignment in PDF output (DG);
 * 05-Nov-2003 : Fixed marker rendering bug (833623) (DG);
 *
 */

package org.jfree.chart.renderer;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.LegendItem;
import org.jfree.chart.Marker;
import org.jfree.chart.MarkerLabelPosition;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.Range;
import org.jfree.data.XYDataset;
import org.jfree.util.ObjectUtils;
import org.jfree.util.PublicCloneable;

/**
 * A base class that can be used to create new {@link XYItemRenderer} implementations.
 *
 * @author David Gilbert
 */
public abstract class AbstractXYItemRenderer extends AbstractRenderer implements XYItemRenderer,
                                                                                 Cloneable, 
                                                                                 Serializable {
    
    /** The plot. */
    private XYPlot plot;
    
    /** The tool tip generator. */
    private XYToolTipGenerator toolTipGenerator;

    /** The URL text generator. */
    private XYURLGenerator urlGenerator;

    /**
     * Default constructor.
     */
    protected AbstractXYItemRenderer() {
        this.toolTipGenerator = null;
        this.urlGenerator = null;
    }

    /**
     * Creates a renderer with the specified tooltip generator.
     * <P>
     * Storage is allocated for property change listeners.
     *
     * @param toolTipGenerator  the tooltip generator (<code>null</code> permitted).
     * 
     * @deprecated Use default constructor then set tooltip generator.
     */
    protected AbstractXYItemRenderer(XYToolTipGenerator toolTipGenerator) {
        this(toolTipGenerator, null);
    }

    /**
     * Creates a renderer with the specified tooltip generator.
     * <P>
     * Storage is allocated for property change listeners.
     *
     * @param urlGenerator  the URL generator (<code>null</code> permitted).
     * 
     * @deprecated Use default constructor then set URL generator.
     */
    protected AbstractXYItemRenderer(XYURLGenerator urlGenerator) {
        this(null, urlGenerator);
    }

    /**
     * Creates a renderer with the specified tooltip generator and URL generator.
     * <P>
     * Storage is allocated for property change listeners.
     *
     * @param toolTipGenerator  the tooltip generator (<code>null</code> permitted).
     * @param urlGenerator  the URL generator (<code>null</code> permitted).
     * 
     * @deprecated Use default constructor then set URL generator.
     */
    protected AbstractXYItemRenderer(XYToolTipGenerator toolTipGenerator,
                                     XYURLGenerator urlGenerator) {

        this.toolTipGenerator = toolTipGenerator;
        this.urlGenerator = urlGenerator;

    }

    /**
     * Returns the number of passes through the data that the renderer requires in order to
     * draw the chart.  Most charts will require a single pass, but some require two passes.
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
     * Initialises the renderer.
     * <P>
     * This method will be called before the first item is rendered, giving the
     * renderer an opportunity to initialise any state information it wants to maintain.
     * The renderer can do nothing if it chooses.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param data  the data.
     * @param info  an optional info collection object to return data back to the caller.
     *
     * @return  The number of passes required by the renderer.
     */
    public XYItemRendererState initialise(Graphics2D g2,
                                          Rectangle2D dataArea,
                                          XYPlot plot,
                                          XYDataset data,
                                          PlotRenderingInfo info) {

        XYItemRendererState state = new XYItemRendererState(info);
        return state;

    }

    /**
     * Returns the tool tip generator.
     *
     * @return the tool tip generator (possibly <code>null</code>).
     */
    public XYToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tool tip generator.
     *
     * @param generator  the tool tip generator (<code>null</code> permitted).
     */
    public void setToolTipGenerator(XYToolTipGenerator generator) {

        Object oldValue = this.toolTipGenerator;
        this.toolTipGenerator = generator;
        firePropertyChanged("renderer.ToolTipGenerator", oldValue, generator);

    }

    /**
     * Returns the URL generator for HTML image maps.
     *
     * @return the URL generator (possibly <code>null</code>).
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

        Object oldValue = this.urlGenerator;
        this.urlGenerator = urlGenerator;
        firePropertyChanged("renderer.URLGenerator", oldValue, urlGenerator);

    }

    /**
     * Returns the range type for the renderer.
     * <p>
     * The default implementation returns <code>STANDARD</code>, subclasses may override this
     * behaviour.
     * <p>
     * The {@link org.jfree.chart.plot.XYPlot} uses this information when auto-calculating 
     * the range for the axis.
     *
     * @return the range type.
     */
    public RangeType getRangeType() {
        return RangeType.STANDARD;
    }

    /**
     * Returns a legend item for a series.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return a legend item for the series.
     */
    public LegendItem getLegendItem(int datasetIndex, int series) {

        LegendItem result = null;

        XYPlot plot = getPlot();
        if (plot != null) {
            XYDataset dataset;
            if (datasetIndex == 0) {
                dataset = plot.getDataset();
            }
            else {
                dataset = plot.getSecondaryDataset(datasetIndex - 1);
            }

            if (dataset != null) {
                String label = dataset.getSeriesName(series);
                String description = label;
                Shape shape = getSeriesShape(series);
                Paint paint = getSeriesPaint(series);
                Paint outlinePaint = getSeriesOutlinePaint(series);
                Stroke stroke = getSeriesStroke(series);

                result = new LegendItem(label, description,
                                        shape, paint, outlinePaint, stroke);
            }

        }

        return result;

    }

    /**
     * Fills a band between two values on the axis.  This can be used to color bands between the
     * grid lines.
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

        double x1 = axis.translateValueToJava2D(start, dataArea, plot.getDomainAxisEdge());
        double x2 = axis.translateValueToJava2D(end, dataArea, plot.getDomainAxisEdge());
        // need to change the next line to take account of plot orientation...
        Rectangle2D band = new Rectangle2D.Double(x1, dataArea.getMinY(),
                                                  x2 - x1, dataArea.getMaxY() - dataArea.getMinY());
        Paint paint = plot.getDomainTickBandPaint();

        if (paint != null) {
            g2.setPaint(paint);
            g2.fill(band);
        }

    }

    /**
     * Fills a band between two values on the range axis.  This can be used to color bands between
     * the grid lines.
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

        double y1 = axis.translateValueToJava2D(start, dataArea, plot.getRangeAxisEdge());
        double y2 = axis.translateValueToJava2D(end, dataArea, plot.getRangeAxisEdge());
        // need to change the next line to take account of the plot orientation
        Rectangle2D band = new Rectangle2D.Double(dataArea.getMinX(), y2,
                                                  dataArea.getWidth(), y1 - y2);
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
     * @param dataArea  the area for plotting data (not yet adjusted for any 3D effect).
     * @param value  the value at which the grid line should be drawn.
     *
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
        double v = axis.translateValueToJava2D(value, dataArea, plot.getDomainAxisEdge());
        Line2D line = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
        }

        Paint paint = plot.getDomainGridlinePaint();
        Stroke stroke = plot.getDomainGridlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line);

    }

    /**
     * Draws a grid line against the range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param dataArea  the area for plotting data (not yet adjusted for any 3D effect).
     * @param value  the value at which the grid line should be drawn.
     *
     */
    public void drawRangeGridLine(Graphics2D g2,
                                  XYPlot plot,
                                  ValueAxis axis,
                                  Rectangle2D dataArea,
                                  double value) {

        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }

        PlotOrientation orientation = plot.getOrientation();
        Line2D line = null;
        double v = axis.translateValueToJava2D(value, dataArea, plot.getRangeAxisEdge());
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
        }

        Paint paint = plot.getRangeGridlinePaint();
        Stroke stroke = plot.getRangeGridlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
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

        double value = marker.getValue();
        Range range = domainAxis.getRange();
        if (!range.contains(value)) {
            return;
        }

        double v = domainAxis.translateValueToJava2D(marker.getValue(), dataArea,
                                                     plot.getDomainAxisEdge());
                                                     
        PlotOrientation orientation = plot.getOrientation();
        Line2D line = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());            
        }
        Paint paint = marker.getOutlinePaint();
        Stroke stroke = marker.getOutlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line);

        String label = marker.getLabel();
        MarkerLabelPosition position = marker.getLabelPosition();
        if (label != null) {
            Font labelFont = marker.getLabelFont();
            g2.setFont(labelFont);
            g2.setPaint(marker.getLabelPaint());
            double[] coordinates = calculateDomainMarkerTextPosition(g2, orientation, dataArea, 
                                                                     v, label, labelFont,
                                                               position);
            g2.drawString(label, (int) coordinates[0], (int) coordinates[1]);
        }

    }

    /**
     * Calculates the (x, y) coordinates for drawing a marker label.
     * 
     * @param g2  the graphics device.
     * @param orientation  the plot orientation.
     * @param dataArea  the data area.
     * @param coordinate  the range value (converted to Java 2D space).
     * @param label  the label.
     * @param font  the font.
     * @param position  the label position.
     * 
     * @return the coordinates for drawing the marker label.
     */
    private double[] calculateDomainMarkerTextPosition(Graphics2D g2, 
                                                       PlotOrientation orientation,
                                                       Rectangle2D dataArea,
                                                       double coordinate,
                                                       String label,
                                                       Font font,
                                                       MarkerLabelPosition position) {
                                                     
        double[] result = new double[2];
        FontRenderContext frc = g2.getFontRenderContext();
        FontMetrics fm = g2.getFontMetrics();
        LineMetrics metrics = font.getLineMetrics(label, frc);
        Rectangle2D bounds = fm.getStringBounds(label, g2);
        if (orientation == PlotOrientation.HORIZONTAL) {
            if (position == MarkerLabelPosition.TOP_LEFT) {
                result[0] = dataArea.getMinX() + 2.0;
                result[1] = coordinate - metrics.getDescent() - metrics.getLeading();
            }
            else if (position == MarkerLabelPosition.TOP_RIGHT) {
                result[0] = dataArea.getMaxX() - bounds.getWidth() - 2.0;
                result[1] = coordinate - metrics.getDescent() - metrics.getLeading();
            }
            else if (position == MarkerLabelPosition.BOTTOM_LEFT) {
                result[0] = dataArea.getMinX() + 2.0;
                result[1] = coordinate + bounds.getHeight();
            }
            else if (position == MarkerLabelPosition.BOTTOM_RIGHT) {
                result[0] = dataArea.getMaxX() - bounds.getWidth() - 2.0;
                result[1] = coordinate + bounds.getHeight();
            }                       
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            if (position == MarkerLabelPosition.TOP_LEFT) {
                result[0] = coordinate - bounds.getWidth() - 2.0;
                result[1] = dataArea.getMinY() + bounds.getHeight();
            }
            else if (position == MarkerLabelPosition.TOP_RIGHT) {
                result[0] = coordinate + 2.0;
                result[1] = dataArea.getMinY() + bounds.getHeight();
            }
            else if (position == MarkerLabelPosition.BOTTOM_LEFT) {
                result[0] = coordinate - bounds.getWidth() - 2.0;
                result[1] = dataArea.getMaxY() - metrics.getDescent() - metrics.getLeading();
            }
            else if (position == MarkerLabelPosition.BOTTOM_RIGHT) {
                result[0] = coordinate + 2.0;
                result[1] = dataArea.getMaxY() - metrics.getDescent() - metrics.getLeading();
            }           
        }
        return result;    
        
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

        double value = marker.getValue();
        Range range = rangeAxis.getRange();
        if (!range.contains(value)) {
            return;
        }

        double v = rangeAxis.translateValueToJava2D(marker.getValue(), dataArea,
                                                    plot.getRangeAxisEdge());
        PlotOrientation orientation = plot.getOrientation();
        Line2D line = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());            
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
        }
        Paint paint = marker.getOutlinePaint();
        Stroke stroke = marker.getOutlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line);

        String label = marker.getLabel();
        MarkerLabelPosition position = marker.getLabelPosition();
        if (label != null) {
            Font labelFont = marker.getLabelFont();
            g2.setFont(labelFont);
            g2.setPaint(marker.getLabelPaint());
            double[] coordinates = calculateRangeMarkerTextPosition(g2, orientation, dataArea, 
                                                                    v, label, labelFont,
                                                               position);
            g2.drawString(label, (int) coordinates[0], (int) coordinates[1]);
        }

    }

    /**
     * Calculates the (x, y) coordinates for drawing a marker label.
     * 
     * @param g2  the graphics device.
     * @param orientation  the plot orientation.
     * @param dataArea  the data area.
     * @param coordinate  the range value (converted to Java 2D space).
     * @param label  the label.
     * @param font  the font.
     * @param position  the label position.
     * 
     * @return the coordinates for drawing the marker label.
     */
    private double[] calculateRangeMarkerTextPosition(Graphics2D g2, 
                                                      PlotOrientation orientation,
                                                      Rectangle2D dataArea,
                                                      double coordinate,
                                                      String label,
                                                      Font font,
                                                      MarkerLabelPosition position) {
                                                     
        double[] result = new double[2];
        FontRenderContext frc = g2.getFontRenderContext();
        FontMetrics fm = g2.getFontMetrics();
        LineMetrics metrics = font.getLineMetrics(label, frc);
        Rectangle2D bounds = fm.getStringBounds(label, g2);
        if (orientation == PlotOrientation.HORIZONTAL) {
            if (position == MarkerLabelPosition.TOP_LEFT) {
                result[0] = coordinate - bounds.getWidth() - 2.0;
                result[1] = dataArea.getMinY() + bounds.getHeight();
            }
            else if (position == MarkerLabelPosition.TOP_RIGHT) {
                result[0] = coordinate + 2.0;
                result[1] = dataArea.getMinY() + bounds.getHeight();
            }
            else if (position == MarkerLabelPosition.BOTTOM_LEFT) {
                result[0] = coordinate - bounds.getWidth() - 2.0;
                result[1] = dataArea.getMaxY() - metrics.getDescent() - metrics.getLeading();
            }
            else if (position == MarkerLabelPosition.BOTTOM_RIGHT) {
                result[0] = coordinate + 2.0;
                result[1] = dataArea.getMaxY() - metrics.getDescent() - metrics.getLeading();
            }           
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            if (position == MarkerLabelPosition.TOP_LEFT) {
                result[0] = dataArea.getMinX() + 2.0;
                result[1] = coordinate - metrics.getDescent() - metrics.getLeading();
            }
            else if (position == MarkerLabelPosition.TOP_RIGHT) {
                result[0] = dataArea.getMaxX() - bounds.getWidth() - 2.0;
                result[1] = coordinate - metrics.getDescent() - metrics.getLeading();
            }
            else if (position == MarkerLabelPosition.BOTTOM_LEFT) {
                result[0] = dataArea.getMinX() + 2.0;
                result[1] = coordinate + bounds.getHeight();
            }
            else if (position == MarkerLabelPosition.BOTTOM_RIGHT) {
                result[0] = dataArea.getMaxX() - bounds.getWidth() - 2.0;
                result[1] = coordinate + bounds.getHeight();
            }                       
        }
        return result;    
        
    }
    
    /**
     * Returns a clone of the renderer.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if the renderer does not support cloning.
     */
    protected Object clone() throws CloneNotSupportedException {
        AbstractXYItemRenderer clone = (AbstractXYItemRenderer) super.clone();
        // 'plot' : just retain reference, not a deep copy
        if (this.toolTipGenerator != null && this.toolTipGenerator instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.toolTipGenerator;
            clone.toolTipGenerator = (XYToolTipGenerator) pc.clone();
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

        if (obj instanceof AbstractXYItemRenderer) {
            AbstractXYItemRenderer renderer = (AbstractXYItemRenderer) obj;
            if (super.equals(obj)) {
                boolean b0 = ObjectUtils.equal(this.toolTipGenerator, renderer.toolTipGenerator);
                boolean b1 = ObjectUtils.equal(this.urlGenerator, renderer.urlGenerator);
                return b0 && b1;
            }
            else {
                return false;
            }
        }

        return false;

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

}
