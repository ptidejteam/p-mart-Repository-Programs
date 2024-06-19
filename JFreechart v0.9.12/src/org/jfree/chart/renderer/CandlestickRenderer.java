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
 * ------------------------
 * CandlestickRenderer.java
 * ------------------------
 * (C) Copyright 2001-2003, by Object Refinery Limited.
 *
 * Original Authors:  David Gilbert (for Object Refinery Limited);
 *                    Sylvain Vieujot;
 * Contributor(s):    Richard Atkinson;
 *                    Christian W. Zuckschwerdt;
 *
 * $Id: CandlestickRenderer.java,v 1.1 2007/10/10 19:12:28 vauchers Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1.  Based on code in the CandlestickPlot class, written by Sylvain
 *               Vieujot, which now is redundant (DG);
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem(...) method (DG);
 * 28-Mar-2002 : Added a property change listener mechanism so that renderers no longer need to be
 *               immutable.  Added properties for up and down colors (DG);
 * 04-Apr-2002 : Updated with new automatic width calculation and optional volume display,
 *               contributed by Sylvain Vieujot (DG);
 * 09-Apr-2002 : Removed translatedRangeZero from the drawItem(...) method, and changed the return
 *               type of the drawItem method to void, reflecting a change in the XYItemRenderer
 *               interface.  Added tooltip code to drawItem(...) method (DG);
 * 25-Jun-2002 : Removed redundant code (DG);
 * 05-Aug-2002 : Small modification to drawItem method to support URLs for HTML image maps (RA);
 * 19-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Modified drawItem(...) method signature (DG);
 * 30-Jun-2003 : Added support for PlotOrientation (for completeness, this renderer is unlikely
 *               to be used with a HORIZONTAL orientation) (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 20-Aug-2003 : Implemented Cloneable and PublicCloneable (DG);
 * 29-Aug-2003 : Moved maxVolume calculation to initialise method (see bug report 796619) (DG);
 * 02-Sep-2003 : Added maxCandleWidthInMilliseconds as workaround for bug 796621 (DG);
 * 08-Sep-2003 : Changed ValueAxis API (DG);
 * 
 */

package org.jfree.chart.renderer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
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

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.CrosshairInfo;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.HighLowToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.HighLowDataset;
import org.jfree.data.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * A renderer that draws candlesticks on an {@link XYPlot} (requires a {@link HighLowDataset}).
 *
 * @author Sylvain Vieujot
 */
public class CandlestickRenderer extends AbstractXYItemRenderer implements XYItemRenderer, 
                                                                           Cloneable,
                                                                           PublicCloneable,
                                                                           Serializable {

    /** The candle width. */
    private double candleWidth;
    
    /** The maximum candlewidth in milliseconds. */
    private double maxCandleWidthInMilliseconds = 1000.0 * 60.0 * 60.0 * 20.0;
    
    /** Temporary storage for the maximum candle width. */
    private double maxCandleWidth;

    /** The paint used to fill the candle when the price moved up from open to close. */
    private transient Paint upPaint;

    /** The paint used to fill the candle when the price moved down from open to close. */
    private transient Paint downPaint;

    /** A flag controlling whether or not volume bars are drawn on the chart. */
    private boolean drawVolume;
    
    /** Temporary storage for the maximum volume. */
    private double maxVolume;

    /**
     * Creates a new renderer for candlestick charts.
     */
    public CandlestickRenderer() {
        this(-1.0);
    }

    /**
     * Creates a new renderer for candlestick charts.
     * <P>
     * Use -1 for the candle width if you prefer the width to be calculated automatically.
     *
     * @param candleWidth  The candle width.
     */
    public CandlestickRenderer(double candleWidth) {

        this(candleWidth, true, new HighLowToolTipGenerator());

    }

    /**
     * Creates a new renderer for candlestick charts.
     * <P>
     * Use -1 for the candle width if you prefer the width to be calculated automatically.
     *
     * @param candleWidth  The candle width.
     * @param drawVolume  A flag indicating whether or not volume bars should be drawn.
     * @param toolTipGenerator  The tool tip generator. <code>null</code> is none.
     */
    public CandlestickRenderer(double candleWidth, boolean drawVolume,
                               XYToolTipGenerator toolTipGenerator) {

        super();
        setToolTipGenerator(toolTipGenerator);
        this.candleWidth = candleWidth;
        this.drawVolume = drawVolume;
        this.upPaint = Color.green;
        this.downPaint = Color.red;

    }

    /**
     * Returns the width of each candle.
     *
     * @return The candle width.
     */
    public double getCandleWidth() {
        return this.candleWidth;
    }

    /**
     * Sets the candle width.
     * <P>
     * If you set the width to a negative value, the renderer will calculate
     * the candle width automatically based on the space available on the chart.
     *
     * @param width  The width.
     */
    public void setCandleWidth(double width) {

        if (width != this.candleWidth) {
            Double old = new Double(this.candleWidth);
            this.candleWidth = width;
            firePropertyChanged("CandleStickRenderer.candleWidth", old, new Double(width));
        }

    }

    /**
     * Returns the maximum width (in milliseconds) of each candle.
     *
     * @return The maximum candle width in milliseconds.
     */
    public double getMaxCandleWidthInMilliseconds() {
        return this.maxCandleWidthInMilliseconds;
    }

    /**
     * Sets the maximum candle width (in milliseconds).  
     *
     * @param millis  The maximum width.
     */
    public void setMaxCandleWidthInMilliseconds(double millis) {

        this.maxCandleWidthInMilliseconds = millis;
        firePropertyChanged("CandlestickRenderer.maxCandleWidthInMilliseconds", 
                            null, new Double(millis));

    }

    /**
     * Returns the paint used to fill candles when the price moves up from open
     * to close.
     *
     * @return The paint.
     */
    public Paint getUpPaint() {
        return this.upPaint;
    }

    /**
     * Sets the paint used to fill candles when the price moves up from open
     * to close.
     * <P>
     * Registered property change listeners are notified that the
     * "CandleStickRenderer.upPaint" property has changed.
     *
     * @param paint The paint.
     */
    public void setUpPaint(Paint paint) {

        Paint old = this.upPaint;
        this.upPaint = paint;
        firePropertyChanged("CandleStickRenderer.upPaint", old, paint);

    }

    /**
     * Returns the paint used to fill candles when the price moves down from
     * open to close.
     *
     * @return The paint.
     */
    public Paint getDownPaint() {
        return this.downPaint;
    }

    /**
     * Sets the paint used to fill candles when the price moves down from open
     * to close.
     * <P>
     * Registered property change listeners are notified that the
     * "CandleStickRenderer.downPaint" property has changed.
     *
     * @param paint  The paint.
     */
    public void setDownPaint(Paint paint) {

        Paint old = this.downPaint;
        this.downPaint = paint;
        firePropertyChanged("CandleStickRenderer.downPaint", old, paint);

    }

    /**
     * Returns a flag indicating whether or not volume bars are drawn on the
     * chart.
     *
     * @return <code>true</code> if volume bars are drawn on the chart.
     */
    public boolean drawVolume() {
        return this.drawVolume;
    }

    /**
     * Sets a flag that controls whether or not volume bars are drawn in the
     * background.
     *
     * @param flag The flag.
     */
    public void setDrawVolume(boolean flag) {

        if (this.drawVolume != flag) {
            this.drawVolume = flag;
            firePropertyChanged("CandlestickRenderer.drawVolume", null, new Boolean(flag));
        }

    }

    /**
     * Initialises the renderer then returns the number of 'passes' through the data that the
     * renderer will require (usually just one).  This method will be called before the first
     * item is rendered, giving the renderer an opportunity to initialise any
     * state information it wants to maintain.  The renderer can do nothing if it chooses.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param dataset  the data.
     * @param info  an optional info collection object to return data back to the caller.
     *
     * @return The number of passes the renderer requires.
     */
    public int initialise(Graphics2D g2,
                          Rectangle2D dataArea,
                          XYPlot plot,
                          XYDataset dataset,
                          ChartRenderingInfo info) {
          
        // calculate the maximum allowed candle width from the axis...
        ValueAxis axis = plot.getDomainAxis();
        double x1 = axis.getLowerBound();
        double x2 = x1 + this.maxCandleWidthInMilliseconds;
        RectangleEdge edge = plot.getDomainAxisEdge();
        double xx1 = axis.translateValueToJava2D(x1, dataArea, edge);
        double xx2 = axis.translateValueToJava2D(x2, dataArea, edge);
        this.maxCandleWidth = (xx2 - xx1);
        
        // calculate the highest volume in the dataset... 
        if (this.drawVolume) {
            HighLowDataset highLowDataset = (HighLowDataset) dataset;
            this.maxVolume = 0.0;
            for (int series = 0; series < highLowDataset.getSeriesCount(); series++) {
                for (int item = 0; item < highLowDataset.getItemCount(series); item++) {
                    double volume = highLowDataset.getVolumeValue(series, item).doubleValue();
                    if (volume > this.maxVolume) {
                        this.maxVolume = volume;
                    }
                    
                }    
            }
        }
        
        return 1;
                              
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects info about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairInfo  information about crosshairs on a plot.
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2, Rectangle2D dataArea,
                         ChartRenderingInfo info,
                         XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                         XYDataset dataset, int series, int item,
                         CrosshairInfo crosshairInfo,
                         int pass) {
                             
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            drawHorizontalItem(g2, dataArea, info, plot, domainAxis, rangeAxis, 
                               dataset, series, item, 
                               crosshairInfo, pass);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            drawVerticalItem(g2, dataArea, info, plot, domainAxis, rangeAxis, 
                             dataset, series, item, 
                             crosshairInfo, pass);
        }
    
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects info about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairInfo  information about crosshairs on a plot.
     * @param pass  the pass index.
     */
    public void drawHorizontalItem(Graphics2D g2, Rectangle2D dataArea,
                                   ChartRenderingInfo info,
                                   XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                                   XYDataset dataset, int series, int item,
                                   CrosshairInfo crosshairInfo,
                                   int pass) {   

        // setup for collecting optional entity info...
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getEntityCollection();
        }

        HighLowDataset highLowData = (HighLowDataset) dataset;

        Number x = highLowData.getXValue(series, item);
        Number yHigh = highLowData.getHighValue(series, item);
        Number yLow = highLowData.getLowValue(series, item);
        Number yOpen = highLowData.getOpenValue(series, item);
        Number yClose = highLowData.getCloseValue(series, item);

        double xx = domainAxis.translateValueToJava2D(x.doubleValue(), dataArea,
                                                      plot.getDomainAxisEdge());

        RectangleEdge edge = plot.getRangeAxisEdge();
        double yyHigh = rangeAxis.translateValueToJava2D(yHigh.doubleValue(), dataArea, edge);
        double yyLow = rangeAxis.translateValueToJava2D(yLow.doubleValue(), dataArea, edge);
        double yyOpen = rangeAxis.translateValueToJava2D(yOpen.doubleValue(), dataArea, edge);
        double yyClose = rangeAxis.translateValueToJava2D(yClose.doubleValue(), dataArea, edge);

        double volumeWidth = candleWidth;
        double stickWidth = candleWidth;
        if (candleWidth <= 0.0) {
            int itemCount = highLowData.getItemCount(series);
            volumeWidth = (dataArea.getHeight()) / itemCount * 4.5 / 7;
            if (volumeWidth < 1) {
                volumeWidth = 1;
            }
            if (volumeWidth > this.maxCandleWidth) {
                volumeWidth = this.maxCandleWidth;
            }
            stickWidth = volumeWidth;
            if (stickWidth < 3) {
                stickWidth = 3;
            }
            if (stickWidth > this.maxCandleWidth) {
                stickWidth = this.maxCandleWidth;
            }
        }

        Paint p = getItemPaint(series, item);
        Stroke s = getItemStroke(series, item);

        g2.setStroke(s);

        if (drawVolume) {
            int volume = highLowData.getVolumeValue(series, item).intValue();
            double volumeHeight = volume / this.maxVolume;

            double minX = dataArea.getMinX();
            double maxX = dataArea.getMaxX();

            double xxVolume = volumeHeight * (maxX - minX);

            g2.setPaint(Color.gray);
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));

            g2.fill(new Rectangle2D.Double(minX,
                                           xx - volumeWidth / 2,
                                           xxVolume, volumeWidth));

            g2.setComposite(originalComposite);
        }

        g2.setPaint(p);

        // draw the upper shadow
        if ((yyHigh > yyOpen) && (yyHigh > yyClose)) {
            g2.draw(new Line2D.Double(yyHigh, xx, Math.max(yyOpen, yyClose), xx));
        }

        // draw the lower shadow
        if ((yyLow < yyOpen) && (yyLow < yyClose)) {
            g2.draw(new Line2D.Double(yyLow, xx, Math.min(yyOpen, yyClose), xx));
        }


        // draw the body
        Shape body = null;
        if (yyOpen < yyClose) {
            body = new Rectangle2D.Double(yyOpen, xx - stickWidth / 2, 
                                          yyClose - yyOpen, stickWidth);
            if (upPaint != null) {
                g2.setPaint(upPaint);
                g2.fill(body);
            }
            g2.setPaint(p);
            g2.draw(body);
        }
        else {
            body = new Rectangle2D.Double(yyClose, xx - stickWidth / 2, 
                                          yyOpen - yyClose, stickWidth);
            if (downPaint != null) {
                g2.setPaint(downPaint);
            }
            g2.fill(body);
            g2.setPaint(p);
            g2.draw(body);
        }

        // add an entity for the item...
        if (entities != null) {
            String tip = null;
            if (getToolTipGenerator() != null) {
                tip = getToolTipGenerator().generateToolTip(dataset, series, item);
            }
            String url = null;
            if (getURLGenerator() != null) {
                url = getURLGenerator().generateURL(dataset, series, item);
            }
            XYItemEntity entity = new XYItemEntity(body, dataset, series, item, tip, url);
            entities.addEntity(entity);
        }

    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects info about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairInfo  information about crosshairs on a plot.
     * @param pass  the pass index.
     */
    public void drawVerticalItem(Graphics2D g2, Rectangle2D dataArea,
                                 ChartRenderingInfo info,
                                 XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                                 XYDataset dataset, int series, int item,
                                 CrosshairInfo crosshairInfo,
                                 int pass) {   

        // setup for collecting optional entity info...
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getEntityCollection();
        }

        HighLowDataset highLowData = (HighLowDataset) dataset;

        Number x = highLowData.getXValue(series, item);
        Number yHigh = highLowData.getHighValue(series, item);
        Number yLow = highLowData.getLowValue(series, item);
        Number yOpen = highLowData.getOpenValue(series, item);
        Number yClose = highLowData.getCloseValue(series, item);

        double xx = domainAxis.translateValueToJava2D(x.doubleValue(), dataArea,
                                                      plot.getDomainAxisEdge());

        RectangleEdge edge = plot.getRangeAxisEdge();
        double yyHigh = rangeAxis.translateValueToJava2D(yHigh.doubleValue(), dataArea, edge);
        double yyLow = rangeAxis.translateValueToJava2D(yLow.doubleValue(), dataArea, edge);
        double yyOpen = rangeAxis.translateValueToJava2D(yOpen.doubleValue(), dataArea, edge);
        double yyClose = rangeAxis.translateValueToJava2D(yClose.doubleValue(), dataArea, edge);

        double volumeWidth = candleWidth;
        double stickWidth = candleWidth;
        if (candleWidth <= 0.0) {
            int itemCount = highLowData.getItemCount(series);
            volumeWidth = (dataArea.getMaxX() - dataArea.getMinX()) / itemCount * 4.5 / 7;
            if (volumeWidth < 1) {
                volumeWidth = 1;
            }
            if (volumeWidth > this.maxCandleWidth) {
                volumeWidth = this.maxCandleWidth;
            }
            stickWidth = volumeWidth;
            if (stickWidth < 3) {
                stickWidth = 3;
            }
            if (stickWidth > this.maxCandleWidth) {
                stickWidth = this.maxCandleWidth;
            }
        }

        Paint p = getItemPaint(series, item);
        Stroke s = getItemStroke(series, item);

        g2.setStroke(s);

        if (drawVolume) {
            int volume = highLowData.getVolumeValue(series, item).intValue();
            double volumeHeight = volume / this.maxVolume;

            double minY = dataArea.getMinY();
            double maxY = dataArea.getMaxY();

            double yyVolume = volumeHeight * (maxY - minY);

            g2.setPaint(Color.gray);
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));

            g2.fill(new Rectangle2D.Double(xx - volumeWidth / 2,
                                           maxY - yyVolume, volumeWidth, yyVolume));

            g2.setComposite(originalComposite);
        }

        g2.setPaint(p);

        // draw the upper shadow
        if ((yyHigh < yyOpen) && (yyHigh < yyClose)) {
            g2.draw(new Line2D.Double(xx, yyHigh, xx,
                Math.min(yyOpen, yyClose)));
        }

        // draw the lower shadow
        if ((yyLow > yyOpen) && (yyLow > yyClose)) {
            g2.draw(new Line2D.Double(xx, yyLow, xx,
                Math.max(yyOpen, yyClose)));
        }


        // draw the body
        Shape body = null;
        if (yyOpen > yyClose) {
            body = new Rectangle2D.Double(xx - stickWidth / 2, yyClose,
                                          stickWidth, yyOpen - yyClose);
            if (upPaint != null) {
                g2.setPaint(upPaint);
                g2.fill(body);
            }
            g2.setPaint(p);
            g2.draw(body);
        }
        else {
            body = new Rectangle2D.Double(xx - stickWidth / 2, yyOpen,
                                          stickWidth, yyClose - yyOpen);
            if (downPaint != null) {
                g2.setPaint(downPaint);
            }
            g2.fill(body);
            g2.setPaint(p);
            g2.draw(body);
        }

        // add an entity for the item...
        if (entities != null) {
            String tip = null;
            if (getToolTipGenerator() != null) {
                tip = getToolTipGenerator().generateToolTip(dataset, series, item);
            }
            String url = null;
            if (getURLGenerator() != null) {
                url = getURLGenerator().generateURL(dataset, series, item);
            }
            XYItemEntity entity = new XYItemEntity(body, dataset, series, item, tip, url);
            entities.addEntity(entity);
        }

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

        if (obj instanceof CandlestickRenderer) {
            CandlestickRenderer renderer = (CandlestickRenderer) obj;
            boolean result = super.equals(obj);
            result = result && (this.candleWidth == renderer.getCandleWidth());
            result = result && (this.upPaint.equals(renderer.getUpPaint()));
            result = result && (this.downPaint.equals(renderer.getDownPaint()));
            result = result && (this.drawVolume == renderer.drawVolume);
            return result;
        }

        return false;

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
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {

        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.upPaint, stream);
        SerialUtilities.writePaint(this.downPaint, stream);

    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {

        stream.defaultReadObject();
        this.upPaint = SerialUtilities.readPaint(stream);
        this.downPaint = SerialUtilities.readPaint(stream);

    }

}
