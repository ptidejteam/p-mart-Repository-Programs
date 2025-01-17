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
 * HighLowRenderer.java
 * --------------------
 * (C) Copyright 2001-2006, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Richard Atkinson;
 *                   Christian W. Zuckschwerdt;
 *
 * $Id: HighLowRenderer.java,v 1.1 2007/10/10 20:38:20 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1 (DG);
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem() method (DG);
 * 28-Mar-2002 : Added a property change listener mechanism so that renderers 
 *               no longer need to be immutable (DG);
 * 09-Apr-2002 : Removed translatedRangeZero from the drawItem() method, and 
 *               changed the return type of the drawItem method to void, 
 *               reflecting a change in the XYItemRenderer interface.  Added 
 *               tooltip code to drawItem() method (DG);
 * 05-Aug-2002 : Small modification to drawItem method to support URLs for 
 *               HTML image maps (RA);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Modified drawItem() method signature (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 31-Jul-2003 : Deprecated constructor (DG);
 * 20-Aug-2003 : Implemented Cloneable and PublicCloneable (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 29-Jan-2004 : Fixed bug (882392) when rendering with 
 *               PlotOrientation.HORIZONTAL (DG);
 * 25-Feb-2004 : Replaced CrosshairInfo with CrosshairState.  Renamed 
 *               XYToolTipGenerator --> XYItemLabelGenerator (DG);
 * 15-Jul-2004 : Switched getX() with getXValue() and getY() with 
 *               getYValue() (DG);
 * 01-Nov-2005 : Added optional openTickPaint and closeTickPaint settings (DG);
 * ------------- JFREECHART 1.0.0 ---------------------------------------------
 * 06-Jul-2006 : Replace dataset methods getX() --> getXValue() (DG);
 * 
 */

package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

/**
 * A renderer that draws high/low/open/close markers on an {@link XYPlot} 
 * (requires a {@link OHLCDataset}).  This renderer does not include code to 
 * calculate the crosshair point for the plot.
 */
public class HighLowRenderer extends AbstractXYItemRenderer
                             implements XYItemRenderer,
                                        Cloneable,
                                        PublicCloneable,
                                        Serializable {
    
    /** For serialization. */
    private static final long serialVersionUID = -8135673815876552516L;
    
    /** A flag that controls whether the open ticks are drawn. */
    private boolean drawOpenTicks;

    /** A flag that controls whether the close ticks are drawn. */
    private boolean drawCloseTicks;
    
    /** 
     * The paint used for the open ticks (if <code>null</code>, the series
     * paint is used instead).
     */
    private transient Paint openTickPaint;
    
    /** 
     * The paint used for the close ticks (if <code>null</code>, the series
     * paint is used instead).
     */
    private transient Paint closeTickPaint;

    /**
     * The default constructor.
     */
    public HighLowRenderer() {
        super();
        this.drawOpenTicks = true;
        this.drawCloseTicks = true;
    }

    /**
     * Returns the flag that controls whether open ticks are drawn.
     * 
     * @return A boolean.
     */
    public boolean getDrawOpenTicks() {
        return this.drawOpenTicks;
    }
    
    /**
     * Sets the flag that controls whether open ticks are drawn, and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param draw  the flag.
     */
    public void setDrawOpenTicks(boolean draw) {
        this.drawOpenTicks = draw;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Returns the flag that controls whether close ticks are drawn.
     * 
     * @return A boolean.
     */
    public boolean getDrawCloseTicks() {
        return this.drawCloseTicks;
    }
    
    /**
     * Sets the flag that controls whether close ticks are drawn, and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param draw  the flag.
     */
    public void setDrawCloseTicks(boolean draw) {
        this.drawCloseTicks = draw;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Returns the paint used to draw the ticks for the open values.
     * 
     * @return The paint used to draw the ticks for the open values (possibly 
     *         <code>null</code>).
     */
    public Paint getOpenTickPaint() {
        return this.openTickPaint;
    }
    
    /**
     * Sets the paint used to draw the ticks for the open values and sends a 
     * {@link RendererChangeEvent} to all registered listeners.  If you set
     * this to <code>null</code> (the default), the series paint is used 
     * instead.
     * 
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setOpenTickPaint(Paint paint) {
        this.openTickPaint = paint;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Returns the paint used to draw the ticks for the close values.
     * 
     * @return The paint used to draw the ticks for the close values (possibly 
     *         <code>null</code>).
     */
    public Paint getCloseTickPaint() {
        return this.closeTickPaint;
    }
    
    /**
     * Sets the paint used to draw the ticks for the close values and sends a 
     * {@link RendererChangeEvent} to all registered listeners.  If you set
     * this to <code>null</code> (the default), the series paint is used 
     * instead.
     * 
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setCloseTickPaint(Paint paint) {
        this.closeTickPaint = paint;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the plot is being drawn.
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

        double x = dataset.getXValue(series, item);
        if (!domainAxis.getRange().contains(x)) {
            return;    // the x value is not within the axis range
        }
        double xx = domainAxis.valueToJava2D(x, dataArea, 
                plot.getDomainAxisEdge());
        
        // setup for collecting optional entity info...
        Shape entityArea = null;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }

        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge location = plot.getRangeAxisEdge();

        Paint itemPaint = getItemPaint(series, item);
        Stroke itemStroke = getItemStroke(series, item);
        g2.setPaint(itemPaint);
        g2.setStroke(itemStroke);
        
        if (dataset instanceof OHLCDataset) {
            OHLCDataset hld = (OHLCDataset) dataset;
            
            double yHigh = hld.getHighValue(series, item);
            double yLow = hld.getLowValue(series, item);
            if (!Double.isNaN(yHigh) && !Double.isNaN(yLow)) {
                double yyHigh = rangeAxis.valueToJava2D(yHigh, dataArea, 
                        location);
                double yyLow = rangeAxis.valueToJava2D(yLow, dataArea, 
                        location);
                if (orientation == PlotOrientation.HORIZONTAL) {
                    g2.draw(new Line2D.Double(yyLow, xx, yyHigh, xx));
                    entityArea = new Rectangle2D.Double(Math.min(yyLow, yyHigh),
                            xx - 1.0, Math.abs(yyHigh - yyLow), 2.0);
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    g2.draw(new Line2D.Double(xx, yyLow, xx, yyHigh));   
                    entityArea = new Rectangle2D.Double(xx - 1.0, 
                            Math.min(yyLow, yyHigh), 2.0,  
                            Math.abs(yyHigh - yyLow));
                }
            }
            
            double delta = 2.0;
            if (domainAxis.isInverted()) {
                delta = -delta;
            }
            if (getDrawOpenTicks()) {
                double yOpen = hld.getOpenValue(series, item);
                if (!Double.isNaN(yOpen)) {
                    double yyOpen = rangeAxis.valueToJava2D(yOpen, dataArea, 
                            location);
                    if (this.openTickPaint != null) {
                        g2.setPaint(this.openTickPaint);
                    }
                    else {
                        g2.setPaint(itemPaint);
                    }
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        g2.draw(new Line2D.Double(yyOpen, xx + delta, yyOpen, 
                                xx));   
                    }
                    else if (orientation == PlotOrientation.VERTICAL) {
                        g2.draw(new Line2D.Double(xx - delta, yyOpen, xx, 
                                yyOpen));   
                    }
                }
            }
            
            if (getDrawCloseTicks()) {
                double yClose = hld.getCloseValue(series, item);
                if (!Double.isNaN(yClose)) {
                    double yyClose = rangeAxis.valueToJava2D(
                        yClose, dataArea, location);
                    if (this.closeTickPaint != null) {
                        g2.setPaint(this.closeTickPaint);
                    }
                    else {
                        g2.setPaint(itemPaint);
                    }
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        g2.draw(new Line2D.Double(yyClose, xx, yyClose, 
                                xx - delta));   
                    }
                    else if (orientation == PlotOrientation.VERTICAL) {
                        g2.draw(new Line2D.Double(xx, yyClose, xx + delta, 
                                yyClose));   
                    }
                }
            }
  
        }
        else {
            // not a HighLowDataset, so just draw a line connecting this point 
            // with the previous point...
            if (item > 0) {
                double x0 = dataset.getXValue(series, item - 1);
                double y0 = dataset.getYValue(series, item - 1);
                double y = dataset.getYValue(series, item);
                if (Double.isNaN(x0) || Double.isNaN(y0) || Double.isNaN(y)) {
                    return;
                }
                double xx0 = domainAxis.valueToJava2D(x0, dataArea, 
                        plot.getDomainAxisEdge());
                double yy0 = rangeAxis.valueToJava2D(y0, dataArea, location);
                double yy = rangeAxis.valueToJava2D(y, dataArea, location);
                if (orientation == PlotOrientation.HORIZONTAL) {
                    g2.draw(new Line2D.Double(yy0, xx0, yy, xx));
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    g2.draw(new Line2D.Double(xx0, yy0, xx, yy));
                }
            }
        }
        
        // add an entity for the item...
        if (entities != null) {
            String tip = null;
            XYToolTipGenerator generator = getToolTipGenerator(series, item);
            if (generator != null) {
                tip = generator.generateToolTip(dataset, series, item);
            }
            String url = null;
            if (getURLGenerator() != null) {
                url = getURLGenerator().generateURL(dataset, series, item);
            }
            XYItemEntity entity = new XYItemEntity(entityArea, dataset, 
                    series, item, tip, url);
            entities.add(entity);
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
     * Tests this renderer for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HighLowRenderer)) {
            return false;
        }
        HighLowRenderer that = (HighLowRenderer) obj;
        if (this.drawOpenTicks != that.drawOpenTicks) {
            return false;
        }
        if (this.drawCloseTicks != that.drawCloseTicks) {
            return false;
        }
        if (!PaintUtilities.equal(this.openTickPaint, that.openTickPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.closeTickPaint, that.closeTickPaint)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        return true;
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
        this.openTickPaint = SerialUtilities.readPaint(stream);
        this.closeTickPaint = SerialUtilities.readPaint(stream);
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
        SerialUtilities.writePaint(this.openTickPaint, stream);
        SerialUtilities.writePaint(this.closeTickPaint, stream);
    }

}
