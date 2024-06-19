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
 * -----------
 * XYPlot.java
 * -----------
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Craig MacFarlane;
 *                   Mark Watson (www.markwatson.com);
 *                   Jonathan Nash;
 *                   Gideon Krause;
 *                   Klaus Rheinwald;
 *
 * $Id: XYPlot.java,v 1.1 2007/10/10 20:00:17 vauchers Exp $
 *
 * Changes (from 21-Jun-2001)
 * --------------------------
 * 21-Jun-2001 : Removed redundant JFreeChart parameter from constructors (DG);
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 19-Oct-2001 : Removed the code for drawing the visual representation of each data point into
 *               a separate class StandardXYItemRenderer.  This will make it easier to add
 *               variations to the way the charts are drawn.  Based on code contributed by
 *               Mark Watson (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 20-Nov-2001 : Fixed clipping bug that shows up when chart is displayed inside JScrollPane (DG);
 * 12-Dec-2001 : Removed unnecessary 'throws' clauses from constructor (DG);
 * 13-Dec-2001 : Added skeleton code for tooltips.  Added new constructor. (DG);
 * 16-Jan-2002 : Renamed the tooltips class (DG);
 * 22-Jan-2002 : Added DrawInfo class, incorporating tooltips and crosshairs.  Crosshairs based
 *               on code by Jonathan Nash (DG);
 * 05-Feb-2002 : Added alpha-transparency setting based on code by Sylvain Vieujot (DG);
 * 26-Feb-2002 : Updated getMinimumXXX() and getMaximumXXX() methods to handle special case when
 *               chart is null (DG);
 * 28-Feb-2002 : Renamed Datasets.java --> DatasetUtilities.java (DG);
 * 28-Mar-2002 : The plot now registers with the renderer as a property change listener.  Also
 *               added a new constructor (DG);
 * 09-Apr-2002 : Removed the transRangeZero from the renderer.drawItem(...) method.  Moved the
 *               tooltip generator into the renderer (DG);
 * 23-Apr-2002 : Fixed bug in methods for drawing horizontal and vertical lines (DG);
 * 13-May-2002 : Small change to the draw(...) method so that it works for OverlaidXYPlot also (DG);
 * 25-Jun-2002 : Removed redundant import (DG);
 * 20-Aug-2002 : Renamed getItemRenderer() --> getRenderer(), and
 *               setXYItemRenderer() --> setRenderer() (DG);
 * 28-Aug-2002 : Added mechanism for (optional) plot annotations (DG);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 18-Nov-2002 : Added grid settings for both domain and range axis (previously these were set in
 *               the axes) (DG);
 * 09-Jan-2003 : Further additions to the grid settings, plus integrated plot border bug fix
 *               contributed by Gideon Krause (DG);
 * 22-Jan-2003 : Removed monolithic constructor (DG);
 * 04-Mar-2003 : Added 'no data' message, see bug report 691634.  Added secondary range markers 
 *               using code contributed by Klaus Rheinwald (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 03-Apr-2003 : Added setDomainAxisLocation(...) method (DG);
 * 
 */

package com.jrefinery.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.CrosshairInfo;
import com.jrefinery.chart.LegendItem;
import com.jrefinery.chart.LegendItemCollection;
import com.jrefinery.chart.Marker;
import com.jrefinery.chart.annotations.Annotation;
import com.jrefinery.chart.annotations.XYAnnotation;
import com.jrefinery.chart.axis.AxisNotCompatibleException;
import com.jrefinery.chart.axis.HorizontalAxis;
import com.jrefinery.chart.axis.Tick;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.axis.VerticalAxis;
import com.jrefinery.chart.event.PlotChangeEvent;
import com.jrefinery.chart.renderer.StandardXYItemRenderer;
import com.jrefinery.chart.renderer.XYItemRenderer;
import com.jrefinery.data.Dataset;
import com.jrefinery.data.DatasetChangeEvent;
import com.jrefinery.data.DatasetUtilities;
import com.jrefinery.data.Range;
import com.jrefinery.data.SeriesDataset;
import com.jrefinery.data.XYDataset;
import com.jrefinery.io.SerialUtilities;
import com.jrefinery.util.ObjectUtils;

/**
 * A general class for plotting data in the form of (x, y) pairs.  This plot can
 * use data from any class that implements the {@link XYDataset} interface.
 * <P>
 * <code>XYPlot</code> makes use of an {@link XYItemRenderer} to draw each point on the plot.
 * By using different renderers, various chart types can be produced.
 * <p>
 * The {@link com.jrefinery.chart.ChartFactory} class contains static methods for creating 
 * pre-configured charts.
 *
 * @author David Gilbert
 */
public class XYPlot extends Plot implements HorizontalValuePlot,
                                            VerticalValuePlot,
                                            PropertyChangeListener,
                                            Serializable {

    /** The default grid line stroke. */
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5f,
        BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_BEVEL,
        0.0f,
        new float[] {2.0f, 2.0f},
        0.0f);

    /** The default grid line paint. */
    public static final Paint DEFAULT_GRIDLINE_PAINT = Color.lightGray;

    /** The default crosshair visibility. */
    public static final boolean DEFAULT_CROSSHAIR_VISIBLE = false;

    /** The default crosshair stroke. */
    public static final Stroke DEFAULT_CROSSHAIR_STROKE = DEFAULT_GRIDLINE_STROKE;

    /** The default crosshair paint. */
    public static final Paint DEFAULT_CROSSHAIR_PAINT = Color.blue;

    /** The domain axis (used for the x-values). */
    private ValueAxis domainAxis;

    /** The domain axis location. */
    private int domainAxisLocation;

    /** The range axis (used for the y-values). */
    private ValueAxis rangeAxis;

    /** The range axis location. */
    private int rangeAxisLocation;

    /** Object responsible for drawing the visual representation of each point on the plot. */
    private XYItemRenderer renderer;

    /** An optional secondary range axis (location is opposite primary axis). */
    private ValueAxis secondaryRangeAxis;

    /** An optional renderer for the secondary dataset. */
    private XYItemRenderer secondaryRenderer;

    /** A flag that controls whether the domain grid-lines are visible. */
    private boolean domainGridlinesVisible;

    /** The stroke used to draw the domain grid-lines. */
    private transient Stroke domainGridlineStroke;

    /** The paint used to draw the domain grid-lines. */
    private transient Paint domainGridlinePaint;

    /** A flag that controls whether the range grid-lines are visible. */
    private boolean rangeGridlinesVisible;

    /** The stroke used to draw the range grid-lines. */
    private transient Stroke rangeGridlineStroke;

    /** The paint used to draw the range grid-lines. */
    private transient Paint rangeGridlinePaint;

    /** A flag that controls whether or not a domain crosshair is drawn..*/
    private boolean domainCrosshairVisible;

    /** The domain crosshair value. */
    private double domainCrosshairValue;

    /** The pen/brush used to draw the crosshair (if any). */
    private transient Stroke domainCrosshairStroke;

    /** The color used to draw the crosshair (if any). */
    private transient Paint domainCrosshairPaint;

    /** A flag that controls whether or not the crosshair locks onto actual data points. */
    private boolean domainCrosshairLockedOnData = true;

    /** A flag that controls whether or not a range crosshair is drawn..*/
    private boolean rangeCrosshairVisible;

    /** The range crosshair value. */
    private double rangeCrosshairValue;

    /** The pen/brush used to draw the crosshair (if any). */
    private transient Stroke rangeCrosshairStroke;

    /** The color used to draw the crosshair (if any). */
    private transient Paint rangeCrosshairPaint;

    /** A flag that controls whether or not the crosshair locks onto actual data points. */
    private boolean rangeCrosshairLockedOnData = true;

    /** A list of markers (optional) for the domain axis. */
    private List domainMarkers;

    /** A list of markers (optional) for the range axis. */
    private List rangeMarkers;

    /** A list of secondary markers (optional) for the secondary range axis. */
    private List secondaryRangeMarkers;

    /** A list of annotations (optional) for the plot. */
    private List annotations;

    /** The weight for this plot in a combined plot. */
    private int weight;

    /**
     * Constructs an XYPlot with the specified axes (other attributes take default values).
     *
     * @param data  the dataset.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     */
    public XYPlot(XYDataset data, ValueAxis domainAxis, ValueAxis rangeAxis) {

        this(data,
             domainAxis,
             rangeAxis,
             new StandardXYItemRenderer()
         );

    }

    /**
     * Constructs an XYPlot with the specified axes and renderer (other
     * attributes take default values).
     *
     * @param data  the dataset.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param renderer  the renderer
     */
    public XYPlot(XYDataset data,
                  ValueAxis domainAxis,
                  ValueAxis rangeAxis,
                  XYItemRenderer renderer) {

        super(data);

        this.domainAxis = domainAxis;
        if (domainAxis != null) {
            domainAxis.setPlot(this);
            domainAxis.addChangeListener(this);
        }
        this.domainAxisLocation = BOTTOM;

        this.rangeAxis = rangeAxis;
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
        }
        this.rangeAxisLocation = LEFT;

        this.weight = 1;  // only relevant when this is a subplot
        this.renderer = renderer;
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addPropertyChangeListener(this);
        }

        this.domainGridlinesVisible = true;
        this.domainGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.domainGridlinePaint = DEFAULT_GRIDLINE_PAINT;

        this.rangeGridlinesVisible = true;
        this.rangeGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeGridlinePaint = DEFAULT_GRIDLINE_PAINT;

        this.domainCrosshairVisible = false;
        this.domainCrosshairValue = 0.0;
        this.domainCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
        this.domainCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;

        this.rangeCrosshairVisible = false;
        this.rangeCrosshairValue = 0.0;
        this.rangeCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
        this.rangeCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;

    }

    /**
     * Returns the number of series in the dataset for this plot.
     *
     * @return the series count.
     */
    public int getSeriesCount() {

        int result = 0;

        SeriesDataset data = getXYDataset();
        if (data != null) {
            result = data.getSeriesCount();
        }

        return result;

    }

    /**
     * Returns the legend items for the plot.  Each legend item is generated by the plot's
     * renderer, since the renderer is responsible for the visual representation of the
     * data.
     *
     * @return the legend items.
     */
    public LegendItemCollection getLegendItems() {

        LegendItemCollection result = new LegendItemCollection();

        // get the legend items for the main dataset...
        XYDataset dataset1 = getXYDataset();
        if (dataset1 != null) {
            XYItemRenderer renderer = getRenderer();
            if (renderer != null) {
                int seriesCount = dataset1.getSeriesCount();
                for (int i = 0; i < seriesCount; i++) {
                    LegendItem item = renderer.getLegendItem(0, i);
                    result.add(item);
                }
            }
        }

        // get the legend items for the secondary dataset...
        XYDataset dataset2 = getSecondaryXYDataset();
        if (dataset2 != null) {
            // get the secondary renderer, or default to the primary renderer
            XYItemRenderer renderer2 = getSecondaryRenderer();
            if (renderer2 == null) {
                renderer2 = getRenderer();
            }
            if (renderer2 != null) {
                int seriesCount = dataset2.getSeriesCount();
                for (int i = 0; i < seriesCount; i++) {
                    LegendItem item = renderer2.getLegendItem(1, i);
                    result.add(item);
                }
            }
        }

        return result;

    }

    /**
     * Returns the weight for this plot when it is used as a subplot within a
     * combined plot.
     *
     * @return the weight.
     */
    public int getWeight() {
        return this.weight;
    }

    /**
     * Sets the weight for the plot.
     *
     * @param weight  the weight.
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Returns the item renderer.
     *
     * @return the item renderer (possibly null).
     */
    public XYItemRenderer getRenderer() {
        return this.renderer;
    }

    /**
     * Sets the item renderer, and notifies all listeners of a change to the plot.
     * <P>
     * If the renderer is set to null, no chart will be drawn.
     *
     * @param renderer  the new renderer (null permitted).
     */
    public void setRenderer(XYItemRenderer renderer) {

        boolean changed = false;

        if (this.renderer != null) {
            if (!this.renderer.equals(renderer)) {
                this.renderer.removePropertyChangeListener(this);
                this.renderer = renderer;
                changed = true;
            }
        }
        else {
            if (renderer != null) {
                this.renderer = renderer;
                changed = true;
            }
        }

        if (changed) {
            this.renderer.setPlot(this);
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the secondary item renderer.  This renderer is responsible for the visual
     * representation of each item in the secondary dataset (if there is one).  If no
     * secondary renderer is set, then the primary renderer is used instead.
     *
     * @return the renderer (possibly null).
     */
    public XYItemRenderer getSecondaryRenderer() {
        return this.secondaryRenderer;
    }

    /**
     * Sets the secondary item renderer.  This renderer is responsible for the visual
     * representation of each item in the secondary dataset (if there is one).
     * <p>
     * You can set this renderer to <code>null</code>, in which case the primary renderer
     * is used instead.
     *
     * @param renderer  the new renderer (null permitted).
     */
    public void setSecondaryRenderer(XYItemRenderer renderer) {

        boolean changed = false;

        if (this.secondaryRenderer != null) {
            if (!this.secondaryRenderer.equals(renderer)) {
                this.secondaryRenderer.removePropertyChangeListener(this);
                this.secondaryRenderer = renderer;
                changed = true;
            }
        }
        else {
            if (renderer != null) {
                this.secondaryRenderer = renderer;
                changed = true;
            }
        }

        if (changed) {
            this.secondaryRenderer.setPlot(this);
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the secondary range axis.
     *
     * @return the axis.
     */
    public ValueAxis getSecondaryRangeAxis() {
        return this.secondaryRangeAxis;
    }

    /**
     * Sets the secondary range axis for the plot.  The axis will be drawn on the opposite
     * side of the chart to the primary range axis.  If there is a secondary dataset, it will
     * be plotted against the secondary range axis, but if there is no secondary dataset, the
     * secondary axis will mirror the values on the primary range axis.
     *
     * @param axis  the axis.
     */
    public void setSecondaryRangeAxis(ValueAxis axis) {

        if (isCompatibleRangeAxis(axis)) {

            if (axis != null) {
                try {
                    axis.setPlot(this);
                }
                catch (PlotNotCompatibleException e) {
                    throw new AxisNotCompatibleException(
                        "XYPlot.setSecondaryRangeAxis(...): plot not compatible with axis.");
                }
                axis.addChangeListener(this);
            }

            // plot is likely registered as a listener with the existing axis...
            if (this.secondaryRangeAxis != null) {
                this.secondaryRangeAxis.removeChangeListener(this);
            }

            this.secondaryRangeAxis = axis;
            notifyListeners(new PlotChangeEvent(this));

        }
        else {
            throw new AxisNotCompatibleException(
                "XYPlot.setSecondaryRangeAxis(...): axis not compatible with plot.");
        }

    }

    /**
     * A convenience method that returns the dataset for the plot, cast as an XYDataset.
     *
     * @return the dataset for the plot, cast as an XYDataset.
     */
    public XYDataset getXYDataset() {
        return (XYDataset) getDataset();
    }

    /**
     * Returns the secondary dataset (possibly null), cast as an XYDataset.
     *
     * @return the secondary dataset.
     */
    public XYDataset getSecondaryXYDataset() {
        return (XYDataset) getSecondaryDataset();
    }

    /**
     * Returns the domain axis for the plot.  If the domain axis for this plot
     * is null, then the method will return the parent plot's domain axis (if
     * there is a parent plot).
     *
     * @return the domain axis.
     */
    public ValueAxis getDomainAxis() {

        ValueAxis result = this.domainAxis;

        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof XYPlot) {
                XYPlot xy = (XYPlot) parent;
                result = xy.getDomainAxis();
            }
        }

        return result;

    }

    /**
     * Sets the domain axis for the plot (this must be compatible with the plot
     * type or an exception is thrown).
     *
     * @param axis  the new axis.
     *
     * @throws AxisNotCompatibleException if the axis is not compatible.
     */
    public void setDomainAxis(ValueAxis axis) throws AxisNotCompatibleException {

        if (isCompatibleDomainAxis(axis)) {

            if (axis != null) {

                try {
                    axis.setPlot(this);
                }
                catch (PlotNotCompatibleException e) {
                    throw new AxisNotCompatibleException(
                        "Plot.setDomainAxis(...): "
                        + "plot not compatible with axis.");
                }
                axis.addChangeListener(this);
            }

            // plot is likely registered as a listener with the existing axis...
            if (this.domainAxis != null) {
                this.domainAxis.removeChangeListener(this);
            }

            this.domainAxis = axis;
            notifyListeners(new PlotChangeEvent(this));

        }
        else {
            throw new AxisNotCompatibleException(
                "Plot.setDomainAxis(...): axis not compatible with plot.");
        }

    }

    /**
     * Returns the location of the domain axis.
     *
     * @return the location.
     */
    public int getDomainAxisLocation() {
        return this.domainAxisLocation;
    }

    /**
     * Sets the location of the domain axis.
     * <p>
     * Use one of the constants <code>LEFT</code>, <code>RIGHT</code>, <code>TOP</code> or 
     * <code>BOTTOM</code>.
     *
     * @param location  the axis location.
     */
    public void setDomainAxisLocation(int location) {
        setDomainAxisLocation(location, true);
    }

    /**
     * Sets the location of the domain axis.
     * <p>
     * Use one of the constants <code>LEFT</code>, <code>RIGHT</code>, <code>TOP</code> or 
     * <code>BOTTOM</code>.
     *
     * @param location  the axis location.
     * @param notify  a flag that controls whether listeners are notified.
     */
    public void setDomainAxisLocation(int location, boolean notify) {

        if (location != this.domainAxisLocation) {
            this.domainAxisLocation = location;
            if (notify) {
                notifyListeners(new PlotChangeEvent(this));
            }
        }

    }

    /**
     * Returns <code>true</code> if the domain gridlines are visible, and <code>false<code>
     * otherwise.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean isDomainGridlinesVisible() {
        return this.domainGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether or not the domain grid-lines are visible.
     * <p>
     * If the flag value is changed, a {@link PlotChangeEvent} is sent to all registered listeners.
     *
     * @param visible  the new value of the flag.
     */
    public void setDomainGridlinesVisible(boolean visible) {
        if (this.domainGridlinesVisible != visible) {
            this.domainGridlinesVisible = visible;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the stroke for the grid-lines (if any) plotted against the domain axis.
     *
     * @return the stroke.
     */
    public Stroke getDomainGridlineStroke() {
        return this.domainGridlineStroke;
    }

    /**
     * Sets the stroke for the grid lines plotted against the domain axis.
     * <p>
     * If you set this to <code>null</code>, no grid lines will be drawn.
     *
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setDomainGridlineStroke(Stroke stroke) {
        this.domainGridlineStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the paint for the grid lines (if any) plotted against the domain axis.
     *
     * @return the paint.
     */
    public Paint getDomainGridlinePaint() {
        return this.domainGridlinePaint;
    }

    /**
     * Sets the paint for the grid lines plotted against the domain axis.
     * <p>
     * If you set this to <code>null</code>, no grid lines will be drawn.
     *
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setDomainGridlinePaint(Paint paint) {
        this.domainGridlinePaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the range axis for the plot.  If the range axis for this plot is
     * null, then the method will return the parent plot's range axis (if
     * there is a parent plot).
     *
     * @return the range axis.
     */
    public ValueAxis getRangeAxis() {

        ValueAxis result = this.rangeAxis;

        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof XYPlot) {
                XYPlot xy = (XYPlot) parent;
                result = xy.getRangeAxis();
            }
        }

        return result;

    }

    /**
     * Sets the range axis for the plot.
     * <P>
     * An exception is thrown if the new axis and the plot are not mutually compatible.
     *
     * @param axis the new axis (null permitted).
     *
     * @throws AxisNotCompatibleException if the axis is not compatible.
     */
    public void setRangeAxis(ValueAxis axis) throws AxisNotCompatibleException {

        if (isCompatibleRangeAxis(axis)) {

            if (axis != null) {
                try {
                    axis.setPlot(this);
                }
                catch (PlotNotCompatibleException e) {
                    throw new AxisNotCompatibleException(
                        "Plot.setRangeAxis(...): plot not compatible with axis.");
                }
                axis.addChangeListener(this);
            }

            // plot is likely registered as a listener with the existing axis...
            if (this.rangeAxis != null) {
                this.rangeAxis.removeChangeListener(this);
            }

            this.rangeAxis = axis;
            notifyListeners(new PlotChangeEvent(this));

        }
        else {
            throw new AxisNotCompatibleException(
                "Plot.setRangeAxis(...): axis not compatible with plot.");
        }

    }

    /**
     * Returns the location of the range axis.
     *
     * @return the location.
     */
    public int getRangeAxisLocation() {
        return this.rangeAxisLocation;
    }

    /**
     * Sets the location of the range axis.
     * <p>
     * Use one of the constants LEFT, RIGHT, TOP or BOTTOM.
     *
     * @param location  the location.
     */
    public void setRangeAxisLocation(int location) {
        setRangeAxisLocation(location, true);
    }

    /**
     * Sets the location of the range axis.
     * <p>
     * Use one of the constants LEFT, RIGHT, TOP or BOTTOM.
     *
     * @param location  the location.
     * @param notify  a flag that controls whether listeners are notified.
     */
    public void setRangeAxisLocation(int location, boolean notify) {
        
        if (location != this.rangeAxisLocation) {
            this.rangeAxisLocation = location;
            if (notify) {
                notifyListeners(new PlotChangeEvent(this));
            }
        }
        
    }

    /**
     * Returns <code>true</code> if the range axis grid is visible, and <code>false<code>
     * otherwise.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean isRangeGridlinesVisible() {
        return this.rangeGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether or not the range axis grid lines are visible.
     * <p>
     * If the flag value is changed, a {@link PlotChangeEvent} is sent to all registered listeners.
     *
     * @param visible  the new value of the flag.
     */
    public void setRangeGridlinesVisible(boolean visible) {
        if (this.rangeGridlinesVisible != visible) {
            this.rangeGridlinesVisible = visible;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the stroke for the grid lines (if any) plotted against the range axis.
     *
     * @return the stroke.
     */
    public Stroke getRangeGridlineStroke() {
        return this.rangeGridlineStroke;
    }

    /**
     * Sets the stroke for the grid lines plotted against the range axis.
     * <p>
     * If you set this to <code>null</code>, no grid lines will be drawn.
     *
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setRangeGridlineStroke(Stroke stroke) {
        this.rangeGridlineStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the paint for the grid lines (if any) plotted against the range axis.
     *
     * @return the paint.
     */
    public Paint getRangeGridlinePaint() {
        return this.rangeGridlinePaint;
    }

    /**
     * Sets the paint for the grid lines plotted against the range axis.
     * <p>
     * If you set this to <code>null</code>, no grid lines will be drawn.
     *
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setRangeGridlinePaint(Paint paint) {
        this.rangeGridlinePaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Adds a marker for the domain axis.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param marker the marker.
     */
    public void addDomainMarker(Marker marker) {

        if (this.domainMarkers == null) {
            this.domainMarkers = new java.util.ArrayList();
        }
        this.domainMarkers.add(marker);
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Clears all the domain markers.
     */
    public void clearDomainMarkers() {
        if (this.domainMarkers != null) {
            this.domainMarkers.clear();
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Adds a marker for the range axis.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param marker The marker.
     */
    public void addRangeMarker(Marker marker) {

        if (this.rangeMarkers == null) {
            this.rangeMarkers = new java.util.ArrayList();
        }
        this.rangeMarkers.add(marker);
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Clears all the range markers.
     */
    public void clearRangeMarkers() {
        if (this.rangeMarkers != null) {
            this.rangeMarkers.clear();
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Adds a secondary marker for the range axis.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param marker The marker.
     */
    public void addSecondaryRangeMarker(Marker marker) { 

        if (this.secondaryRangeMarkers == null) {
            this.secondaryRangeMarkers = new java.util.ArrayList();
        }
        this.secondaryRangeMarkers.add(marker);
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Clears all the secondary range markers.
     */
    public void clearSecondaryRangeMarkers() {
        if (this.secondaryRangeMarkers != null) {
            this.secondaryRangeMarkers.clear();
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Adds an annotation to the plot.
     *
     * @param annotation  the annotation.
     */
    public void addAnnotation(XYAnnotation annotation) {

        if (this.annotations == null) {
            this.annotations = new java.util.ArrayList();
        }
        this.annotations.add(annotation);
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Clears all the annotations.
     */
    public void clearAnnotations() {
        if (this.annotations != null) {
            this.annotations.clear();
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Checks the compatibility of a domain axis, returning true if the axis is
     * compatible with the plot, and false otherwise.
     *
     * @param axis  the proposed axis.
     *
     * @return <code>true</code> if the axis is compatible with the plot.
     */
    public boolean isCompatibleDomainAxis(ValueAxis axis) {

        if (axis == null) {
            return true;
        }
        if (axis instanceof HorizontalAxis) {
            return true;
        }
        else {
            return false;
        }

    }

    /**
     * Checks the compatibility of a range axis, returning true if the axis is
     * compatible with the plot, and false otherwise.
     *
     * @param axis  the proposed axis.
     *
     * @return <code>true</code> if the axis is compatible with the plot.
     */
    public boolean isCompatibleRangeAxis(ValueAxis axis) {

        if (axis == null) {
            return true;
        }
        if (axis instanceof VerticalAxis) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Draws the XY plot on a Java 2D graphics device (such as the screen or a printer).
     * <P>
     * XYPlot relies on an XYItemRenderer to draw each item in the plot.  This
     * allows the visual representation of the data to be changed easily.
     * <P>
     * The optional info argument collects information about the rendering of
     * the plot (dimensions, tooltip information etc).  Just pass in null if
     * you do not need this information.
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot (including axis
     *                  labels) should be drawn.
     * @param info  collects chart drawing information (null permitted).
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {

        // if the plot area is too small, just return...
        boolean b1 = (plotArea.getWidth() <= MINIMUM_WIDTH_TO_DRAW);
        boolean b2 = (plotArea.getHeight() <= MINIMUM_HEIGHT_TO_DRAW);
        if (b1 || b2) {
            return;
        }

        // record the plot area...
        if (info != null) {
            info.setPlotArea(plotArea);
        }

        // adjust the drawing area for the plot insets (if any)...
        Insets insets = getInsets();
        if (insets != null) {
            plotArea.setRect(plotArea.getX() + insets.left,
                             plotArea.getY() + insets.top,
                             plotArea.getWidth() - insets.left - insets.right,
                             plotArea.getHeight() - insets.top - insets.bottom);
        }

        // estimate the height of the horizontal axis...
        double hAxisHeight = 0.0;
        if (this.domainAxis != null) {
            HorizontalAxis hAxis = (HorizontalAxis) this.domainAxis;
            hAxisHeight = hAxis.reserveHeight(g2, this, plotArea, this.domainAxisLocation);
        }

        // estimate the width of the vertical axis...
        double vAxis1Width = 0.0;
        if (this.rangeAxis != null) {
            VerticalAxis vAxis1 = (VerticalAxis) this.rangeAxis;
            vAxis1Width = vAxis1.reserveWidth(g2, this, plotArea, getRangeAxisLocation(),
                                              hAxisHeight,
                                              getDomainAxisLocation());
        }

        // estimate the width of the secondary range axis (if any)...
        double vAxis2Width = 0.0;
        int secondaryAxisLocation = getOppositeAxisLocation(getRangeAxisLocation());
        VerticalAxis vAxis2 = (VerticalAxis) this.secondaryRangeAxis;
        if (vAxis2 != null) {
            vAxis2Width = vAxis2.reserveWidth(g2, this, plotArea, secondaryAxisLocation,
                                              hAxisHeight, getDomainAxisLocation());
        }

        // ...and therefore what is left for the plot itself...
        double x1 = getRectX(plotArea.getX(), vAxis1Width, vAxis2Width, getRangeAxisLocation());
        double y1 = getRectY(plotArea.getY(), hAxisHeight, 0.0, getDomainAxisLocation());
        Rectangle2D dataArea = new Rectangle2D.Double(x1, y1,
                                                  plotArea.getWidth() - vAxis1Width - vAxis2Width,
                                                  plotArea.getHeight() - hAxisHeight);

        if (info != null) {
            info.setDataArea(dataArea);
        }

        CrosshairInfo crosshairInfo = new CrosshairInfo();
        crosshairInfo.setCrosshairDistance(Double.POSITIVE_INFINITY);
        crosshairInfo.setAnchorX(getDomainAxis().getAnchorValue());
        crosshairInfo.setAnchorY(getRangeAxis().getAnchorValue());

        // draw the plot background and axes...
        drawBackground(g2, dataArea);
        if (this.domainAxis != null) {
            this.domainAxis.draw(g2, plotArea, dataArea, this.domainAxisLocation);
        }
        if (this.rangeAxis != null) {
            this.rangeAxis.draw(g2, plotArea, dataArea, this.rangeAxisLocation);
        }
        if (this.secondaryRangeAxis != null) {
            this.secondaryRangeAxis.draw(g2, plotArea, dataArea, secondaryAxisLocation);
        }

        if (renderer != null) {
            Shape originalClip = g2.getClip();
            Composite originalComposite = g2.getComposite();

            g2.clip(dataArea);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       getForegroundAlpha()));
            // draw the domain grid lines, if any...
            if (isDomainGridlinesVisible()) {
                Stroke gridStroke = getDomainGridlineStroke();
                Paint gridPaint = getDomainGridlinePaint();
                if ((gridStroke != null) && (gridPaint != null)) {
                    Iterator iterator = getDomainAxis().getTicks().iterator();
                    while (iterator.hasNext()) {
                        Tick tick = (Tick) iterator.next();
                        renderer.drawDomainGridLine(g2, this, getDomainAxis(), dataArea,
                                                    tick.getNumericalValue());
                    }
                }
            }

            // draw the range grid lines, if any...
            if (isRangeGridlinesVisible()) {
                Stroke gridStroke = getRangeGridlineStroke();
                Paint gridPaint = getRangeGridlinePaint();
                if ((gridStroke != null) && (gridPaint != null)) {
                    Iterator iterator = getRangeAxis().getTicks().iterator();
                    while (iterator.hasNext()) {
                        Tick tick = (Tick) iterator.next();
                        renderer.drawRangeGridLine(g2, this, getRangeAxis(), dataArea,
                                                   tick.getNumericalValue());
                    }
                }
            }

            if (this.domainMarkers != null) {
                Iterator iterator = this.domainMarkers.iterator();
                while (iterator.hasNext()) {
                    Marker marker = (Marker) iterator.next();
                    renderer.drawDomainMarker(g2, this, getDomainAxis(), marker, dataArea);
                }
            }

            if (this.rangeMarkers != null) {
                Iterator iterator = this.rangeMarkers.iterator();
                while (iterator.hasNext()) {
                    Marker marker = (Marker) iterator.next();
                    renderer.drawRangeMarker(g2, this, getRangeAxis(), marker, dataArea);
                }
            }

            if (this.secondaryRangeMarkers != null) {
                Iterator iterator = this.secondaryRangeMarkers.iterator();
                while (iterator.hasNext()) {
                    Marker marker = (Marker) iterator.next();
                    renderer.drawRangeMarker(g2, this, getSecondaryRangeAxis(), marker, dataArea);
                }
            }

            render(g2, dataArea, info, crosshairInfo);
            render2(g2, dataArea, info, crosshairInfo);

            // draw the annotations...
            if (this.annotations != null) {
                Iterator iterator = this.annotations.iterator();
                while (iterator.hasNext()) {
                    Annotation annotation = (Annotation) iterator.next();
                    if (annotation instanceof XYAnnotation) {
                        XYAnnotation xya = (XYAnnotation) annotation;
                        xya.draw(g2, dataArea, getDomainAxis(), getRangeAxis());
                    }
                }
            }

            g2.setClip(originalClip);
            g2.setComposite(originalComposite);
        }
        drawOutline(g2, dataArea);

    }

    /**
     * Draws a representation of the data within the dataArea region, using the
     * current renderer.
     * <P>
     * The <code>info</code> and <code>crosshairInfo</code> arguments may be <code>null</code>.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param info  an optional object for collection dimension information.
     * @param crosshairInfo  an optional object for collecting crosshair info.
     */
    public void render(Graphics2D g2, Rectangle2D dataArea,
                       ChartRenderingInfo info, CrosshairInfo crosshairInfo) {

        // now get the data and plot it (the visual representation will depend
        // on the renderer that has been set)...
        XYDataset data = getXYDataset();
        if (!DatasetUtilities.isEmpty(data)) {

            this.renderer.initialise(g2, dataArea, this, data, info);

            ValueAxis domainAxis = getDomainAxis();
            ValueAxis rangeAxis = getRangeAxis();
            int seriesCount = data.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                Paint seriesPaint = renderer.getSeriesPaint(0, series);
                Stroke seriesStroke = renderer.getSeriesStroke(0, series);
                g2.setPaint(seriesPaint);
                g2.setStroke(seriesStroke);
                int itemCount = data.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    renderer.drawItem(g2, dataArea, info, this,
                                      domainAxis, rangeAxis,
                                      data, 0, series, item,
                                      crosshairInfo);

                }
            }

            // draw vertical crosshair if required...
            setDomainCrosshairValue(crosshairInfo.getCrosshairX(), false);
            if (isDomainCrosshairVisible()) {
                drawVerticalLine(g2, dataArea,
                                 getDomainCrosshairValue(),
                                 getDomainCrosshairStroke(),
                                 getDomainCrosshairPaint());
            }

            // draw horizontal crosshair if required...
            setRangeCrosshairValue(crosshairInfo.getCrosshairY(), false);
            if (isRangeCrosshairVisible()) {
                drawHorizontalLine(g2, dataArea,
                                   getRangeCrosshairValue(),
                                   getRangeCrosshairStroke(),
                                   getRangeCrosshairPaint());
            }

        }
        else {
            drawNoDataMessage(g2, dataArea);
        }

    }

    /**
     * Draws a representation of the data within the dataArea region, using the
     * current renderer.
     * <P>
     * The <code>info</code> and <code>crosshairInfo</code> arguments may be <code>null</code>.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param info  an optional object for collection dimension information.
     * @param crosshairInfo  an optional object for collecting crosshair info.
     */
    public void render2(Graphics2D g2, Rectangle2D dataArea,
                        ChartRenderingInfo info, CrosshairInfo crosshairInfo) {

        // now get the data and plot it (the visual representation will depend
        // on the renderer that has been set)...
        XYDataset dataset = getSecondaryXYDataset();
        if (!DatasetUtilities.isEmpty(dataset)) {
            XYItemRenderer renderer = this.secondaryRenderer;
            if (renderer == null) {
                renderer = getRenderer();
            }

            renderer.initialise(g2, dataArea, this, dataset, info);

            ValueAxis domainAxis = getDomainAxis();
            ValueAxis rangeAxis = getSecondaryRangeAxis();
            int seriesCount = dataset.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                Paint seriesPaint = renderer.getSeriesPaint(1, series);
                Stroke seriesStroke = renderer.getSeriesStroke(1, series);
                g2.setPaint(seriesPaint);
                g2.setStroke(seriesStroke);
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    renderer.drawItem(g2, dataArea, info, this,
                                      domainAxis, rangeAxis,
                                      dataset, 1, series, item,
                                      crosshairInfo);

                }
            }

        }

    }

    /**
     * Utility method for drawing a crosshair on the chart (if required).
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param value  the coordinate, where to draw the line.
     * @param stroke  the stroke to use.
     * @param paint  the paint to use.
     */
    protected void drawVerticalLine(Graphics2D g2, Rectangle2D dataArea,
                                    double value, Stroke stroke, Paint paint) {

        double xx = getDomainAxis().translateValueToJava2D(value, dataArea);
        Line2D line = new Line2D.Double(xx, dataArea.getMinY(),
                                        xx, dataArea.getMaxY());
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);

    }

    /**
     * Utility method for drawing a crosshair on the chart (if required).
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param value  the coordinate, where to draw the line.
     * @param stroke  the stroke to use.
     * @param paint  the paint to use.
     */
    protected void drawHorizontalLine(Graphics2D g2, Rectangle2D dataArea,
                                      double value, Stroke stroke, Paint paint) {

        double yy = getRangeAxis().translateValueToJava2D(value, dataArea);
        Line2D line = new Line2D.Double(dataArea.getMinX(), yy,
                                        dataArea.getMaxX(), yy);
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);

    }

    /**
     * Handles a 'click' on the plot by updating the anchor values...
     *
     * @param x  x-coordinate, where the click occured.
     * @param y  y-coordinate, where the click occured.
     * @param info  an object for collection dimension information.
     */
    public void handleClick(int x, int y, ChartRenderingInfo info) {

        // set the anchor value for the horizontal axis...
        ValueAxis hva = getDomainAxis();
        if (hva != null) {
            double hvalue = hva.translateJava2DtoValue((float) x, info.getDataArea());

            hva.setAnchorValue(hvalue);
            setDomainCrosshairValue(hvalue);
        }

        // set the anchor value for the vertical axis...
        ValueAxis vva = getRangeAxis();
        if (vva != null) {
            double vvalue = vva.translateJava2DtoValue((float) y, info.getDataArea());
            vva.setAnchorValue(vvalue);
            setRangeCrosshairValue(vvalue);
        }

    }

    /**
     * Zooms the axis ranges by the specified percentage about the anchor point.
     *
     * @param percent  the amount of the zoom.
     */
    public void zoom(double percent) {

        if (percent > 0) {
            ValueAxis domainAxis = getDomainAxis();
            double range = domainAxis.getMaximumAxisValue() - domainAxis.getMinimumAxisValue();
            double scaledRange = range * percent;
            domainAxis.setAnchoredRange(scaledRange);

            ValueAxis rangeAxis = getRangeAxis();
            range = rangeAxis.getMaximumAxisValue()
                - rangeAxis.getMinimumAxisValue();
            scaledRange = range * percent;
            rangeAxis.setAnchoredRange(scaledRange);
        }
        else {
            getRangeAxis().setAutoRange(true);
            getDomainAxis().setAutoRange(true);
        }

    }

    /**
     * Returns the plot type as a string.
     *
     * @return a short string describing the type of plot.
     */
    public String getPlotType() {
        return "XY Plot";
    }

    /**
     * Returns the range for the horizontal axis.
     *
     * @param axis  the axis.
     * 
     * @return the range.
     */
    public Range getHorizontalDataRange(ValueAxis axis) {

        Range result = null;

        Dataset dataset = getDataset();
        if (axis.equals(getSecondaryRangeAxis())) {
            dataset = getSecondaryDataset();
        }
        if (dataset != null) {
            result = DatasetUtilities.getDomainExtent(dataset);
        }

        return result;

    }

    /**
     * Returns the range for the vertical axis.
     *
     * @param axis  the axis.
     * 
     * @return the range for the vertical axis.
     */
    public Range getVerticalDataRange(ValueAxis axis) {

        Range result = null;

        Dataset dataset = getDataset();
        if (axis.equals(getSecondaryRangeAxis())) {
            dataset = getSecondaryDataset();
        }
        if (dataset != null) {
            result = DatasetUtilities.getRangeExtent(dataset);
        }

        return result;

    }

    /**
     * Notifies all registered listeners of a property change.
     * <P>
     * One source of property change events is the plot's renderer.
     *
     * @param event  information about the property change.
     */
    public void propertyChange(PropertyChangeEvent event) {

        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Receives notification of a change to the plot's dataset.
     * <P>
     * The axis ranges are updated if necessary.
     *
     * @param event  information about the event (not used here).
     */
    public void datasetChanged(DatasetChangeEvent event) {

        if (this.domainAxis != null) {
            this.domainAxis.configure();
        }

        if (this.rangeAxis != null) {
            this.rangeAxis.configure();
        }

        if (this.secondaryRangeAxis != null) {
            this.secondaryRangeAxis.configure();
        }

        if (getParent() != null) {
            getParent().datasetChanged(event);
        }
        else {
            PlotChangeEvent e = new PlotChangeEvent(this);
            notifyListeners(e);
        }

    }

    /**
     * Returns the horizontal axis.
     *
     * @return the horizontal axis.
     */
    public HorizontalAxis getHorizontalAxis() {
        return (HorizontalAxis) getDomainAxis();
    }

    /**
     * Returns the horizontal axis.
     * <P>
     * This method is part of the HorizontalValuePlot interface.
     *
     * @return the horizontal axis.
     */
    public ValueAxis getHorizontalValueAxis() {
        return getDomainAxis();
    }

    /**
     * Returns the vertical axis.
     *
     * @return the vertical axis.
     */
    public VerticalAxis getVerticalAxis() {
        return (VerticalAxis) getRangeAxis();
    }

    /**
     * Returns the vertical axis.
     * <P>
     * This method is part of the VerticalValuePlot interface.
     *
     * @return the vertical axis.
     */
    public ValueAxis getVerticalValueAxis() {
        return getRangeAxis();
    }

    /**
     * Returns a flag indicating whether or not the domain crosshair is visible.
     *
     * @return the flag.
     */
    public boolean isDomainCrosshairVisible() {
        return this.domainCrosshairVisible;
    }

    /**
     * Sets the flag indicating whether or not the domain crosshair is visible.
     *
     * @param flag  the new value of the flag.
     */
    public void setDomainCrosshairVisible(boolean flag) {

        if (this.domainCrosshairVisible != flag) {
            this.domainCrosshairVisible = flag;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns a flag indicating whether or not the crosshair should "lock-on"
     * to actual data values.
     *
     * @return the flag.
     */
    public boolean isDomainCrosshairLockedOnData() {
        return this.domainCrosshairLockedOnData;
    }

    /**
     * Sets the flag indicating whether or not the domain crosshair should "lock-on"
     * to actual data values.
     *
     * @param flag  the flag.
     */
    public void setDomainCrosshairLockedOnData(boolean flag) {

        if (this.domainCrosshairLockedOnData != flag) {
            this.domainCrosshairLockedOnData = flag;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the domain crosshair value.
     *
     * @return The value.
     */
    public double getDomainCrosshairValue() {
        return this.domainCrosshairValue;
    }

    /**
     * Sets the domain crosshair value.
     * <P>
     * Registered listeners are notified that the plot has been modified, but
     * only if the crosshair is visible.
     *
     * @param value  the new value.
     */
    public void setDomainCrosshairValue(double value) {

        setDomainCrosshairValue(value, true);

    }

    /**
     * Sets the domain crosshair value.
     * <P>
     * Registered listeners are notified that the axis has been modified, but
     * only if the crosshair is visible.
     *
     * @param value  the new value.
     * @param notify  a flag that controls whether or not listeners are notified.
     */
    public void setDomainCrosshairValue(double value, boolean notify) {

        this.domainCrosshairValue = value;
        if (isDomainCrosshairVisible() && notify) {
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the Stroke used to draw the crosshair (if visible).
     *
     * @return the crosshair stroke.
     */
    public Stroke getDomainCrosshairStroke() {
        return domainCrosshairStroke;
    }

    /**
     * Sets the Stroke used to draw the crosshairs (if visible) and notifies
     * registered listeners that the axis has been modified.
     *
     * @param stroke  the new crosshair stroke.
     */
    public void setDomainCrosshairStroke(Stroke stroke) {
        domainCrosshairStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the domain crosshair color.
     *
     * @return the crosshair color.
     */
    public Paint getDomainCrosshairPaint() {
        return this.domainCrosshairPaint;
    }

    /**
     * Sets the Paint used to color the crosshairs (if visible) and notifies
     * registered listeners that the axis has been modified.
     *
     * @param paint the new crosshair paint.
     */
    public void setDomainCrosshairPaint(Paint paint) {
        this.domainCrosshairPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns a flag indicating whether or not the range crosshair is visible.
     *
     * @return the flag.
     */
    public boolean isRangeCrosshairVisible() {
        return this.rangeCrosshairVisible;
    }

    /**
     * Sets the flag indicating whether or not the range crosshair is visible.
     *
     * @param flag  the new value of the flag.
     */
    public void setRangeCrosshairVisible(boolean flag) {

        if (this.rangeCrosshairVisible != flag) {
            this.rangeCrosshairVisible = flag;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns a flag indicating whether or not the crosshair should "lock-on"
     * to actual data values.
     *
     * @return the flag.
     */
    public boolean isRangeCrosshairLockedOnData() {
        return this.rangeCrosshairLockedOnData;
    }

    /**
     * Sets the flag indicating whether or not the range crosshair should "lock-on"
     * to actual data values.
     *
     * @param flag  the flag.
     */
    public void setRangeCrosshairLockedOnData(boolean flag) {

        if (this.rangeCrosshairLockedOnData != flag) {
            this.rangeCrosshairLockedOnData = flag;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the range crosshair value.
     *
     * @return The value.
     */
    public double getRangeCrosshairValue() {
        return this.rangeCrosshairValue;
    }

    /**
     * Sets the domain crosshair value.
     * <P>
     * Registered listeners are notified that the plot has been modified, but
     * only if the crosshair is visible.
     *
     * @param value  the new value.
     */
    public void setRangeCrosshairValue(double value) {

        setRangeCrosshairValue(value, true);

    }

    /**
     * Sets the range crosshair value.
     * <P>
     * Registered listeners are notified that the axis has been modified, but
     * only if the crosshair is visible.
     *
     * @param value  the new value.
     * @param notify  a flag that controls whether or not listeners are notified.
     */
    public void setRangeCrosshairValue(double value, boolean notify) {

        this.rangeCrosshairValue = value;
        if (isRangeCrosshairVisible() && notify) {
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the Stroke used to draw the crosshair (if visible).
     *
     * @return the crosshair stroke.
     */
    public Stroke getRangeCrosshairStroke() {
        return rangeCrosshairStroke;
    }

    /**
     * Sets the Stroke used to draw the crosshairs (if visible) and notifies
     * registered listeners that the axis has been modified.
     *
     * @param stroke  the new crosshair stroke.
     */
    public void setRangeCrosshairStroke(Stroke stroke) {
        rangeCrosshairStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the range crosshair color.
     *
     * @return the crosshair color.
     */
    public Paint getRangeCrosshairPaint() {
        return this.rangeCrosshairPaint;
    }

    /**
     * Sets the Paint used to color the crosshairs (if visible) and notifies
     * registered listeners that the axis has been modified.
     *
     * @param paint the new crosshair paint.
     */
    public void setRangeCrosshairPaint(Paint paint) {
        this.rangeCrosshairPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Tests this plot for equality with another object.
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
        
        if (obj instanceof XYPlot) {
            XYPlot p = (XYPlot) obj;
            if (super.equals(obj)) {
                boolean b0 = ObjectUtils.equalOrBothNull(this.domainAxis, p.domainAxis);
                boolean b1 = (this.domainAxisLocation == p.domainAxisLocation);
                boolean b2 = ObjectUtils.equalOrBothNull(this.rangeAxis, p.rangeAxis);
                boolean b3 = (this.rangeAxisLocation == p.rangeAxisLocation);
                boolean b4 = ObjectUtils.equalOrBothNull(this.renderer, p.renderer);
                boolean b5 = ObjectUtils.equalOrBothNull(this.secondaryRangeAxis, 
                                                         p.secondaryRangeAxis);
                boolean b6 = ObjectUtils.equalOrBothNull(this.secondaryRenderer, 
                                                         p.secondaryRenderer);
                boolean b7 = (this.domainGridlinesVisible == p.domainGridlinesVisible);
                boolean b8 = ObjectUtils.equalOrBothNull(this.domainGridlineStroke, 
                                                         p.domainGridlineStroke);
                boolean b9 = ObjectUtils.equalOrBothNull(this.domainGridlinePaint, 
                                                         p.domainGridlinePaint);
                boolean b10 = (this.rangeGridlinesVisible == p.rangeGridlinesVisible);
                boolean b11 = ObjectUtils.equalOrBothNull(this.rangeGridlineStroke, 
                                                          p.rangeGridlineStroke);
                boolean b12 = ObjectUtils.equalOrBothNull(this.rangeGridlinePaint, 
                                                          p.rangeGridlinePaint);
                boolean b13 = (this.domainCrosshairVisible == p.domainCrosshairVisible);
                boolean b14 = (this.domainCrosshairValue == p.domainCrosshairValue);
                boolean b15 = ObjectUtils.equalOrBothNull(this.domainCrosshairStroke, 
                                                          p.domainCrosshairStroke);
                boolean b16 = ObjectUtils.equalOrBothNull(this.domainCrosshairPaint, 
                                                          p.domainCrosshairPaint);
                boolean b17 = (this.domainCrosshairLockedOnData == p.domainCrosshairLockedOnData);
                boolean b18 = (this.rangeCrosshairVisible == p.rangeCrosshairVisible);
                boolean b19 = (this.rangeCrosshairValue == p.rangeCrosshairValue);
                boolean b20 = ObjectUtils.equalOrBothNull(this.rangeCrosshairStroke, 
                                                          p.rangeCrosshairStroke);
                boolean b21 = ObjectUtils.equalOrBothNull(this.rangeCrosshairPaint, 
                                                          p.rangeCrosshairPaint);
                boolean b22 = (this.rangeCrosshairLockedOnData == p.rangeCrosshairLockedOnData);

                boolean b23 = ObjectUtils.equalOrBothNull(this.domainMarkers, p.domainMarkers);
                boolean b24 = ObjectUtils.equalOrBothNull(this.rangeMarkers, p.rangeMarkers);
                boolean b25 = ObjectUtils.equalOrBothNull(this.secondaryRangeMarkers, 
                                                          p.secondaryRangeMarkers);
                boolean b26 = ObjectUtils.equalOrBothNull(this.annotations, p.annotations);
                boolean b27 = (this.weight == p.weight);
                
                return b0 && b1 && b2 && b3 && b4 && b5 && b6 && b7 && b8 && b9
                       && b10 && b11 && b12 && b13 && b14 && b15 && b16 && b17 && b18 && b19 
                       && b20 && b21 && b22 && b23 && b24 && b25 && b26 && b27;
            }
        }
        
        return false;
        
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
        SerialUtilities.writeStroke(this.domainGridlineStroke, stream);
        SerialUtilities.writePaint(this.domainGridlinePaint, stream);
        SerialUtilities.writeStroke(this.rangeGridlineStroke, stream);
        SerialUtilities.writePaint(this.rangeGridlinePaint, stream);
        SerialUtilities.writeStroke(this.domainCrosshairStroke, stream);
        SerialUtilities.writePaint(this.domainCrosshairPaint, stream);
        SerialUtilities.writeStroke(this.rangeCrosshairStroke, stream);
        SerialUtilities.writePaint(this.rangeCrosshairPaint, stream);
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
        this.domainGridlineStroke = SerialUtilities.readStroke(stream);
        this.domainGridlinePaint = SerialUtilities.readPaint(stream);
        this.rangeGridlineStroke = SerialUtilities.readStroke(stream);
        this.rangeGridlinePaint = SerialUtilities.readPaint(stream);
        this.domainCrosshairStroke = SerialUtilities.readStroke(stream);
        this.domainCrosshairPaint = SerialUtilities.readPaint(stream);
        this.rangeCrosshairStroke = SerialUtilities.readStroke(stream);
        this.rangeCrosshairPaint = SerialUtilities.readPaint(stream);
        
        if (this.domainAxis != null) {
            this.domainAxis.addChangeListener(this);
        }
        
        if (this.rangeAxis != null) {
            this.rangeAxis.addChangeListener(this);
        }
    }

}
