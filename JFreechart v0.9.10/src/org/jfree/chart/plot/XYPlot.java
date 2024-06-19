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
 * -----------
 * XYPlot.java
 * -----------
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Craig MacFarlane;
 *                   Mark Watson (www.markwatson.com);
 *                   Jonathan Nash;
 *                   Gideon Krause;
 *                   Klaus Rheinwald;
 *                   Xavier Poinsard;
 *
 * $Id: XYPlot.java,v 1.1 2007/10/10 19:05:14 vauchers Exp $
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
 * 30-Apr-2003 : Moved annotation drawing into a separate method (DG);
 * 01-May-2003 : Added multi-pass mechanism for renderers (DG);
 * 02-May-2003 : Changed axis locations from int to AxisLocation (DG);
 * 15-May-2003 : Added an orientation attribute (DG);
 * 02-Jun-2003 : Removed range axis compatibility test (DG);
 * 05-Jun-2003 : Added domain and range grid bands (sponsored by Focus Computer Services Ltd) (DG);
 * 26-Jun-2003 : Fixed bug (757303) in getDataRange(...) method (DG);
 * 02-Jul-2003 : Added patch from bug report 698646 (secondary axes for overlaid plots) (DG);
 * 23-Jul-2003 : Added support for multiple secondary datasets, axes and renderers (DG);
 * 
 */

package org.jfree.chart.plot;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.CrosshairInfo;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.Marker;
import org.jfree.chart.Spacer;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.Tick;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.renderer.XYItemRenderer;
import org.jfree.data.DatasetChangeEvent;
import org.jfree.data.DatasetUtilities;
import org.jfree.data.Range;
import org.jfree.data.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectList;
import org.jfree.util.ObjectUtils;

/**
 * A general class for plotting data in the form of (x, y) pairs.  This plot can
 * use data from any class that implements the {@link XYDataset} interface.
 * <P>
 * <code>XYPlot</code> makes use of an {@link XYItemRenderer} to draw each point on the plot.
 * By using different renderers, various chart types can be produced.
 * <p>
 * The {@link org.jfree.chart.ChartFactory} class contains static methods for creating
 * pre-configured charts.
 *
 * @author David Gilbert
 */
public class XYPlot extends Plot implements ValueAxisPlot, PropertyChangeListener, Serializable {

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

    /** The plot orientation. */
    private PlotOrientation orientation;

    /** The offset between the data area and the axes. */
    private Spacer axisOffset;

    /** The domain axis (used for the x-values). */
    private ValueAxis domainAxis;

    /** The domain axis location. */
    private AxisLocation domainAxisLocation;

    /** Storage for the (optional) secondary domain axes. */
    private ObjectList secondaryDomainAxes;

    /** Storage for the (optional) secondary domain axis locations. */
    private ObjectList secondaryDomainAxisLocations;

    /** The range axis (used for the y-values). */
    private ValueAxis rangeAxis;

    /** The range axis location. */
    private AxisLocation rangeAxisLocation;

    /** Storage for the (optional) secondary range axes. */
    private ObjectList secondaryRangeAxes;

    /** Storage for the (optional) secondary range axis locations. */
    private ObjectList secondaryRangeAxisLocations;

    /** The dataset. */
    private XYDataset dataset;

    /** Storage for the (optional) secondary datasets. */
    private ObjectList secondaryDatasets;

    /** Storage for keys that map secondary datasets to domain axes. */
    private ObjectList secondaryDatasetDomainAxisMap;
    
    /** Storage for keys that map secondary datasets to range axes. */
    private ObjectList secondaryDatasetRangeAxisMap;

    /** Object responsible for drawing the visual representation of each point on the plot. */
    private XYItemRenderer renderer;

    /** Storage for the (optional) secondary renderers. */
    private ObjectList secondaryRenderers;

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

    /** A list of secondary markers (optional) for the secondary domain axis. */
    private List secondaryDomainMarkers;

    /** A list of markers (optional) for the range axis. */
    private List rangeMarkers;

    /** A list of secondary markers (optional) for the secondary range axis. */
    private List secondaryRangeMarkers;

    /** A list of annotations (optional) for the plot. */
    private List annotations;

    /** The paint used for the domain tick bands (if any). */
    private Paint domainTickBandPaint;

    /** The paint used for the range tick bands (if any). */
    private Paint rangeTickBandPaint;

    /** The fixed domain axis space. */
    private AxisSpace fixedDomainAxisSpace;

    /** The fixed range axis space. */
    private AxisSpace fixedRangeAxisSpace;

    /** The weight for this plot (only relevant if this is a subplot in a combined plot). */
    private int weight;

    /** The anchor value. */
    private double anchorX;

    /** The anchor value. */
    private double anchorY;

    /** Temporary storage. */
    private List axesAtTop;

    /** Temporary storage. */
    private List axesAtBottom;

    /** Temporary storage. */
    private List axesAtLeft;

    /** Temporary storage. */
    private List axesAtRight;

    /**
     * Default constructor.
     */
    public XYPlot() {
        this(null, null, null, null);
    }

    /**
     * Creates a new plot.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     * @param domainAxis  the domain axis (<code>null</code> permitted).
     * @param rangeAxis  the range axis (<code>null</code> permitted).
     * @param renderer  the renderer (<code>null</code> permitted).
     */
    public XYPlot(XYDataset dataset,
                  ValueAxis domainAxis,
                  ValueAxis rangeAxis,
                  XYItemRenderer renderer) {

        super();

        this.orientation = PlotOrientation.VERTICAL;
        this.weight = 1;  // only relevant when this is a subplot
        this.axisOffset = new Spacer(Spacer.ABSOLUTE, 0.0, 0.0, 0.0, 0.0);

        // allocate storage for secondary datasets, axes and renderers (all optional)
        this.secondaryDomainAxes = new ObjectList();
        this.secondaryDomainAxisLocations = new ObjectList();
        
        this.secondaryRangeAxes = new ObjectList();
        this.secondaryRangeAxisLocations = new ObjectList();
        
        this.secondaryDatasets = new ObjectList();
        this.secondaryDatasetDomainAxisMap = new ObjectList();
        this.secondaryDatasetRangeAxisMap = new ObjectList();
        
        this.secondaryRenderers = new ObjectList();

        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }

        this.domainAxis = domainAxis;
        if (domainAxis != null) {
            domainAxis.setPlot(this);
            domainAxis.addChangeListener(this);
        }
        this.domainAxisLocation = AxisLocation.BOTTOM_OR_LEFT;

        this.rangeAxis = rangeAxis;
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
        }
        this.rangeAxisLocation = AxisLocation.TOP_OR_LEFT;

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

        this.axesAtTop = new ArrayList();
        this.axesAtBottom = new ArrayList();
        this.axesAtLeft = new ArrayList();
        this.axesAtRight = new ArrayList();

        this.anchorX = 0.0;
        this.anchorY = 0.0;

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
     * Returns the orientation of the plot.
     *
     * @return The orientation of the plot.
     */
    public PlotOrientation getOrientation() {
        return this.orientation;
    }

    /**
     * Sets the orientation for the plot.
     *
     * @param orientation  the orientation (<code>null</code> not allowed).
     */
    public void setOrientation(PlotOrientation orientation) {
        if (orientation == null) {
            throw new IllegalArgumentException("XYPlot.setOrientation(...): null not allowed.");
        }
        if (orientation != this.orientation) {
            this.orientation = orientation;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the axis offset.
     *
     * @return The axis offset.
     */
    public Spacer getAxisOffset() {
        return this.axisOffset;
    }

    /**
     * Sets the axis offsets (gap between the data area and the axes).
     *
     * @param offset  the offset.
     */
    public void setAxisOffset(Spacer offset) {
        this.axisOffset = offset;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the domain axis for the plot.  If the domain axis for this plot
     * is null, then the method will return the parent plot's domain axis (if
     * there is a parent plot).
     *
     * @return The domain axis.
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
     * Sets the domain axis for the plot.
     *
     * @param axis  the new axis.
     */
    public void setDomainAxis(ValueAxis axis) {

        if (axis != null) {

            try {
                axis.setPlot(this);
            }
            catch (PlotNotCompatibleException e) {
            }
            axis.addChangeListener(this);
        }

        // plot is likely registered as a listener with the existing axis...
        if (this.domainAxis != null) {
            this.domainAxis.removeChangeListener(this);
        }

        this.domainAxis = axis;
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Returns the location of the domain axis.
     *
     * @return the location.
     */
    public AxisLocation getDomainAxisLocation() {
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
    public void setDomainAxisLocation(AxisLocation location) {
        setDomainAxisLocation(location, true);
    }

    /**
     * Sets the location of the domain axis (TOP, BOTTOM, LEFT or RIGHT).
     *
     * @param location  the axis location.
     * @param notify  a flag that controls whether listeners are notified.
     */
    public void setDomainAxisLocation(AxisLocation location, boolean notify) {

        if (location != this.domainAxisLocation) {
            this.domainAxisLocation = location;
            if (notify) {
                notifyListeners(new PlotChangeEvent(this));
            }
        }

    }

    /**
     * Returns the edge for the domain axis (taking into account the plot's orientation.
     * 
     * @return The edge.
     */
    public RectangleEdge getDomainAxisEdge() {
        return Plot.resolveDomainAxisLocation(this.domainAxisLocation, this.orientation);
    }

    /**
     * Returns a secondary domain axis.
     *
     * @param index  the axis index.
     *
     * @return The axis (<code>null</code> possible).
     */
    public ValueAxis getSecondaryDomainAxis(int index) {
        ValueAxis result = null;
        if (index < this.secondaryDomainAxes.size()) {
            result = (ValueAxis) this.secondaryDomainAxes.get(index);
        }
        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof XYPlot) {
                XYPlot xy = (XYPlot) parent;
                result = xy.getSecondaryDomainAxis(index);
            }
        }
        return result;
    }

    /**
     * Sets a secondary domain axis.
     *
     * @param index  the axis index.
     * @param axis  the axis.
     */
    public void setSecondaryDomainAxis(int index, ValueAxis axis) {

        ValueAxis existing = getSecondaryDomainAxis(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }

        if (axis != null) {
            try {
                axis.setPlot(this);
            }
            catch (PlotNotCompatibleException e) {
            }
        }

        this.secondaryDomainAxes.set(index, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Clears the secondary domain axes from the plot.
     */
    public void clearSecondaryDomainAxes() {
        for (int i = 0; i < this.secondaryDomainAxes.size(); i++) {
            ValueAxis axis = (ValueAxis) this.secondaryDomainAxes.get(i);
            if (axis != null) {
                axis.removeChangeListener(this);
            }
        }
        this.secondaryDomainAxes.clear();
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Configures the secondary domain axes.
     */
    public void configureSecondaryDomainAxes() {
        for (int i = 0; i < this.secondaryDomainAxes.size(); i++) {
            ValueAxis axis = (ValueAxis) this.secondaryDomainAxes.get(i);
            if (axis != null) {
                axis.configure();
            }
        }
    }

    /**
     * Returns the location for a secondary domain axis.
     *
     * @param index  the axis index.
     *
     * @return The location.
     */
    public AxisLocation getSecondaryDomainAxisLocation(int index) {
        AxisLocation result = null;
        if (index < this.secondaryDomainAxisLocations.size()) {
            result = (AxisLocation) this.secondaryDomainAxisLocations.get(index);
        }
        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof XYPlot) {
                XYPlot xy = (XYPlot) parent;
                result = xy.getSecondaryDomainAxisLocation(index);
            }
        }
        return result;

    }

    /**
     * Sets the location for a secondary domain axis.
     *
     * @param index  the axis index.
     * @param location  the location.
     */
    public void setSecondaryDomainAxisLocation(int index, AxisLocation location) {
        this.secondaryDomainAxisLocations.set(index, location);
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the edge for a secondary domain axis.
     *
     * @param index  the axis index.
     *
     * @return The edge.
     */
    public RectangleEdge getSecondaryDomainAxisEdge(int index) {
        AxisLocation location = getSecondaryDomainAxisLocation(index);
        RectangleEdge result = Plot.resolveDomainAxisLocation(location, this.orientation);
        if (result == null) {
            result = RectangleEdge.opposite(getDomainAxisEdge());
        }
        return result;
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
     */
    public void setRangeAxis(ValueAxis axis)  {

        if (axis != null) {
            try {
                axis.setPlot(this);
            }
            catch (PlotNotCompatibleException e) {
            }
        }

        // plot is likely registered as a listener with the existing axis...
        if (this.rangeAxis != null) {
            this.rangeAxis.removeChangeListener(this);
        }

        this.rangeAxis = axis;
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Returns the location of the range axis.
     *
     * @return the location.
     */
    public AxisLocation getRangeAxisLocation() {
        return this.rangeAxisLocation;
    }

    /**
     * Sets the location of the range axis.
     *
     * @param location  the location.
     */
    public void setRangeAxisLocation(AxisLocation location) {
        setRangeAxisLocation(location, true);
    }

    /**
     * Sets the location of the range axis.
     *
     * @param location  the location.
     * @param notify  a flag that controls whether listeners are notified.
     */
    public void setRangeAxisLocation(AxisLocation location, boolean notify) {

        if (location != this.rangeAxisLocation) {
            this.rangeAxisLocation = location;
            if (notify) {
                notifyListeners(new PlotChangeEvent(this));
            }
        }

    }

    /**
     * Returns the edge for the range axis.
     * 
     * @return The range axis edge.
     */
    public RectangleEdge getRangeAxisEdge() {
        return Plot.resolveRangeAxisLocation(this.rangeAxisLocation, this.orientation);
    }
    
    /**
     * Returns a secondary range axis.
     *
     * @param index  the axis index.
     *
     * @return The axis (<code>null</code> possible).
     */
    public ValueAxis getSecondaryRangeAxis(int index) {
        ValueAxis result = null;
        if (index < this.secondaryRangeAxes.size()) {
            result = (ValueAxis) this.secondaryRangeAxes.get(index);
        }
        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof XYPlot) {
                XYPlot xy = (XYPlot) parent;
                result = xy.getSecondaryRangeAxis(index);
            }
        }
        return result;
    }

    /**
     * Sets a secondary range axis.
     *
     * @param index  the axis index.
     * @param axis  the axis.
     */
    public void setSecondaryRangeAxis(int index, ValueAxis axis) {

        ValueAxis existing = getSecondaryRangeAxis(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }

        if (axis != null) {
            try {
                axis.setPlot(this);
            }
            catch (PlotNotCompatibleException e) {
            }
        }

        this.secondaryRangeAxes.set(index, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Clears the secondary range axes from the plot.
     */
    public void clearSecondaryRangeAxes() {
        for (int i = 0; i < this.secondaryRangeAxes.size(); i++) {
            ValueAxis axis = (ValueAxis) this.secondaryRangeAxes.get(i);
            if (axis != null) {
                axis.removeChangeListener(this);
            }
        }
        this.secondaryRangeAxes.clear();
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Configures the secondary range axes.
     */
    public void configureSecondaryRangeAxes() {
        for (int i = 0; i < this.secondaryRangeAxes.size(); i++) {
            ValueAxis axis = (ValueAxis) this.secondaryRangeAxes.get(i);
            if (axis != null) {
                axis.configure();
            }
        }
    }

    /**
     * Returns the location for a secondary range axis.
     *
     * @param index  the axis index.
     *
     * @return The location.
     */
    public AxisLocation getSecondaryRangeAxisLocation(int index) {
        AxisLocation result = null;
        if (index < this.secondaryRangeAxisLocations.size()) {
            result = (AxisLocation) this.secondaryRangeAxisLocations.get(index);
        }
        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof XYPlot) {
                XYPlot xy = (XYPlot) parent;
                result = xy.getSecondaryRangeAxisLocation(index);
            }
        }
        return result;

    }

    /**
     * Sets the location for a secondary range axis.
     *
     * @param index  the axis index.
     * @param location  the location.
     */
    public void setSecondaryRangeAxisLocation(int index, AxisLocation location) {
        this.secondaryRangeAxisLocations.set(index, location);
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the edge for a secondary range axis.
     *
     * @param index  the axis index.
     *
     * @return The edge.
     */
    public RectangleEdge getSecondaryRangeAxisEdge(int index) {
        AxisLocation location = getSecondaryRangeAxisLocation(index);
        RectangleEdge result = Plot.resolveRangeAxisLocation(location, this.orientation);
        if (result == null) {
            result = RectangleEdge.opposite(getRangeAxisEdge());
        }
        return result;
    }

    /**
     * Returns the primary dataset for the plot.
     *
     * @return The primary dataset (possibly <code>null</code>).
     */
    public XYDataset getDataset() {
        return this.dataset;
    }

    /**
     * Sets the dataset for the plot, replacing the existing dataset if there is one.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     */
    public void setDataset(XYDataset dataset) {

        // if there is an existing dataset, remove the plot from the list of change listeners...
        XYDataset existing = this.dataset;
        if (existing != null) {
            existing.removeChangeListener(this);
        }

        // set the new dataset, and register the chart as a change listener...
        this.dataset = dataset;
        if (dataset != null) {
            setDatasetGroup(dataset.getGroup());
            dataset.addChangeListener(this);
        }

        // send a dataset change event to self...
        DatasetChangeEvent event = new DatasetChangeEvent(this, dataset);
        datasetChanged(event);

    }

    /**
     * Returns one of the secondary datasets.
     *
     * @param index  the dataset index.
     *
     * @return The dataset (possibly <code>null</code>).
     */
    public XYDataset getSecondaryDataset(int index) {
        XYDataset result = null;
        if (this.secondaryDatasets.size() > index) {
            result = (XYDataset) this.secondaryDatasets.get(index);
        }
        return result;
    }

    /**
     * Returns the number of secondary datasets. 
     * 
     * @return The number of secondary datasets.
     */
    public int getSecondaryDatasetCount() {
        return this.secondaryDatasets.size();
    }

    /**
     * Adds or changes a secondary dataset for the plot.
     *
     * @param index  the dataset index.
     * @param dataset  the dataset.
     */
    public void setSecondaryDataset(int index, XYDataset dataset) {
        this.secondaryDatasets.set(index, dataset);
        // send a dataset change event to self...
        DatasetChangeEvent event = new DatasetChangeEvent(this, dataset);
        datasetChanged(event);
    }
    
    /**
     * Maps a secondary dataset to a particular domain axis.
     * 
     * @param index  the dataset index (zero-based).
     * @param key  the key (<code>null</code> for primary axis, or the index of the secondary
     *             axis).
     */
    public void mapSecondaryDatasetToDomainAxis(int index, Integer key) {
        this.secondaryDatasetDomainAxisMap.set(index, key);  
        // fake a dataset change event to update axes...
        datasetChanged(new DatasetChangeEvent(this, this.dataset));  
    }

    /**
     * Maps a secondary dataset to a particular range axis.
     * 
     * @param index  the dataset index (zero-based).
     * @param key  the key (<code>null</code> for primary axis, or the index of the secondary
     *             axis).
     */
    public void mapSecondaryDatasetToRangeAxis(int index, Integer key) {
        this.secondaryDatasetRangeAxisMap.set(index, key);
        // fake a dataset change event to update axes...
        datasetChanged(new DatasetChangeEvent(this, this.dataset));  
    }

    /**
     * Returns the item renderer.
     *
     * @return The item renderer (possibly <code>null</code>).
     */
    public XYItemRenderer getRenderer() {
        return this.renderer;
    }

    /**
     * Sets the item renderer, and notifies all listeners of a change to the plot.
     * <P>
     * If the renderer is set to <code>null</code>, no chart will be drawn.
     *
     * @param renderer  the new renderer (<code>null</code> permitted).
     */
    public void setRenderer(XYItemRenderer renderer) {

        if (this.renderer != null) {
            this.renderer.removePropertyChangeListener(this);
        }

        this.renderer = renderer;
        if (renderer != null) {
            renderer.setPlot(this);
        }

        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Returns a secondary renderer.
     *
     * @param index  the renderer index.
     *
     * @return The renderer (possibly <code>null</code>).
     */
    public XYItemRenderer getSecondaryRenderer(int index) {
        XYItemRenderer result = null;
        if (this.secondaryRenderers.size() > index) {
            result = (XYItemRenderer) this.secondaryRenderers.get(index);
        }
        return result;

    }

    /**
     * Sets a secondary renderer.  A {@link PlotChangeEvent} is sent to all registered listeners.
     *
     * @param index  the index.
     * @param renderer  the renderer.
     */
    public void setSecondaryRenderer(int index, XYItemRenderer renderer) {
        XYItemRenderer existing = getSecondaryRenderer(index);
        if (existing != null) {
            existing.removePropertyChangeListener(this);
        }
        this.secondaryRenderers.set(index, renderer);
        if (renderer != null) {
            renderer.setPlot(this);
        }
        notifyListeners(new PlotChangeEvent(this));
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
     * Returns the paint used for the domain tick bands.  If this is <code>null</code>,
     * no tick bands will be drawn.
     *
     * @return The paint (possibly <code>null</code>).
     */
    public Paint getDomainTickBandPaint() {
        return this.domainTickBandPaint;
    }

    /**
     * Sets the paint for the domain tick bands.
     *
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setDomainTickBandPaint(Paint paint) {
        this.domainTickBandPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the paint used for the range tick bands.  If this is <code>null</code>,
     * no tick bands will be drawn.
     *
     * @return The paint (possibly <code>null</code>).
     */
    public Paint getRangeTickBandPaint() {
        return this.rangeTickBandPaint;
    }

    /**
     * Sets the paint for the range tick bands.
     *
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setRangeTickBandPaint(Paint paint) {
        this.rangeTickBandPaint = paint;
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
     * @param marker  the marker.
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
     * Calculates the space required for the axes.
     * 
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * 
     * @return The required space.
     */
    protected AxisSpace calculateAxisSpace(Graphics2D g2, Rectangle2D plotArea) {
        
        AxisSpace space = new AxisSpace();
 
        // reserve some space for the domain axis...
        if (this.fixedDomainAxisSpace != null) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                space.setLeft(this.fixedDomainAxisSpace.getLeft());
                space.setRight(this.fixedDomainAxisSpace.getRight());
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                space.setTop(this.fixedDomainAxisSpace.getTop());
                space.setBottom(this.fixedDomainAxisSpace.getBottom());
            }
        }
        else {
            // reserve space for the primary domain axis...
            if (this.domainAxis != null) {
                space = this.domainAxis.reserveSpace(g2, this, plotArea, getDomainAxisEdge(), 
                                                     space);
            }
            
            // reserve space for any secondary domain axes...
            for (int i = 0; i < this.secondaryDomainAxes.size(); i++) {
                Axis secondaryDomainAxis = getSecondaryDomainAxis(i);
                if (secondaryDomainAxis != null) {
                    RectangleEdge edge = getSecondaryDomainAxisEdge(i);
                    space = secondaryDomainAxis.reserveSpace(g2, this, plotArea, edge, space);
                }
            }
        }

        // reserve some space for the range axis...
        if (this.fixedRangeAxisSpace != null) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                space.setTop(this.fixedRangeAxisSpace.getTop());
                space.setBottom(this.fixedRangeAxisSpace.getBottom());
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                space.setLeft(this.fixedRangeAxisSpace.getLeft());
                space.setRight(this.fixedRangeAxisSpace.getRight());
            }
        }
        else {
            Axis rangeAxis1 = this.rangeAxis;
            if (rangeAxis1 != null) {
                space = rangeAxis1.reserveSpace(g2, this, plotArea, getRangeAxisEdge(), space);
            }

            // reserve space for the secondary range axes (if any)...
            for (int i = 0; i < this.secondaryRangeAxes.size(); i++) {
                Axis secondaryRangeAxis = getSecondaryRangeAxis(i);
                if (secondaryRangeAxis != null) {
                    RectangleEdge edge = getSecondaryRangeAxisEdge(i);
                    space = secondaryRangeAxis.reserveSpace(g2, this, plotArea, edge, space);
                }
            }
        }
        return space;
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     * <P>
     * This plot relies on an {@link XYItemRenderer} to draw each item in the plot.  This
     * allows the visual representation of the data to be changed easily.
     * <P>
     * The optional info argument collects information about the rendering of
     * the plot (dimensions, tooltip information etc).  Just pass in <code>null</code> if
     * you do not need this information.
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot (including axes and labels) should be drawn.
     * @param info  collects chart drawing information (<code>null</code> permitted).
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

        AxisSpace space = calculateAxisSpace(g2, plotArea);
        Rectangle2D dataArea = space.shrink(plotArea, null);
        this.axisOffset.trim(dataArea);

        if (info != null) {
            info.setDataArea(dataArea);
        }

        CrosshairInfo crosshairInfo = new CrosshairInfo();
        crosshairInfo.setCrosshairDistance(Double.POSITIVE_INFINITY);
        crosshairInfo.setAnchorX(getAnchorX());
        crosshairInfo.setAnchorY(getAnchorY());

        // draw the plot background and axes...
        drawBackground(g2, dataArea);
        drawAxes(g2, plotArea, dataArea);

        if (this.renderer != null) {
            Shape originalClip = g2.getClip();
            Composite originalComposite = g2.getComposite();

            g2.clip(dataArea);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       getForegroundAlpha()));

            drawDomainTickBands(g2, dataArea);
            drawRangeTickBands(g2, dataArea);
            drawGridlines(g2, dataArea);

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
                    renderer.drawRangeMarker(g2, this, getSecondaryRangeAxis(0), marker, dataArea);
                }
            }

            // draw...
            render(g2, dataArea, info, crosshairInfo);
            render2(g2, dataArea, info, crosshairInfo);
            drawAnnotations(g2, dataArea, info);

            g2.setClip(originalClip);
            g2.setComposite(originalComposite);
        }
        drawOutline(g2, dataArea);

    }

    /**
     * Draws the domain tick bands, if any.
     * 
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     */
    public void drawDomainTickBands(Graphics2D g2, Rectangle2D dataArea) {
        // draw the domain tick bands, if any...
        Paint bandPaint = getDomainTickBandPaint();
        if (bandPaint != null) {
            boolean fillBand = false;
            final ValueAxis xAxis = getDomainAxis();
            double previous = xAxis.getMinimumAxisValue();
            Iterator iterator = xAxis.getTicks().iterator();
            while (iterator.hasNext()) {
                Tick tick = (Tick) iterator.next();
                double current = tick.getNumericalValue();
                if (fillBand) {
                    this.renderer.fillDomainGridBand(g2, this, xAxis, dataArea, previous, current);
                }
                previous = current;
                fillBand = !fillBand;
            }
            double end = xAxis.getMaximumAxisValue();
            if (fillBand) {
                this.renderer.fillDomainGridBand(g2, this, xAxis, dataArea, previous, end);
            }
        }    
    }

    /**
     * Draws the range tick bands, if any.
     * 
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     */
    public void drawRangeTickBands(Graphics2D g2, Rectangle2D dataArea) {    

        // draw the range tick bands, if any...
        Paint bandPaint = getRangeTickBandPaint();
        if (bandPaint != null) {
            boolean fillBand = false;
            final ValueAxis rangeAxis = getRangeAxis();
            double previous = rangeAxis.getMinimumAxisValue();
            Iterator iterator = rangeAxis.getTicks().iterator();
            while (iterator.hasNext()) {
                Tick tick = (Tick) iterator.next();
                double current = tick.getNumericalValue();
                if (fillBand) {
                    renderer.fillRangeGridBand(g2, this, rangeAxis, dataArea, previous, current);
                }
                previous = current;
                fillBand = !fillBand;
            }
            double end = rangeAxis.getMaximumAxisValue();
            if (fillBand) {
                renderer.fillRangeGridBand(g2, this, rangeAxis, dataArea, previous, end);
            }
        }
    }

    /**
     * A utility method for drawing the axes.
     * 
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param dataArea  the data area.
     */
    protected void drawAxes(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea) {
        
        this.axesAtTop.clear();
        this.axesAtBottom.clear();
        this.axesAtLeft.clear();
        this.axesAtRight.clear();

        // add each axis to the appropriate list...
        if (this.domainAxis != null) {
            addAxisToList(this.domainAxis, getDomainAxisEdge());
        }
        if (this.rangeAxis != null) {
            addAxisToList(this.rangeAxis, getRangeAxisEdge());
        }
        
        // add secondary domain axes to lists...
        for (int index = 0; index < this.secondaryDomainAxes.size(); index++) {
            ValueAxis secondaryAxis = (ValueAxis) this.secondaryDomainAxes.get(index);
            if (secondaryAxis != null) {
                addAxisToList(secondaryAxis, getSecondaryDomainAxisEdge(index));
            }
        }

        // add secondary range axes to lists...
        for (int index = 0; index < this.secondaryRangeAxes.size(); index++) {
            ValueAxis secondaryAxis = (ValueAxis) this.secondaryRangeAxes.get(index);
            if (secondaryAxis != null) {
                addAxisToList(secondaryAxis, getSecondaryRangeAxisEdge(index));
            }
        }

        // draw the top axes
        double cursor = dataArea.getMinY() - this.axisOffset.getTopSpace(dataArea.getHeight());
        Iterator iterator = this.axesAtTop.iterator();
        while (iterator.hasNext()) {
            ValueAxis axis = (ValueAxis) iterator.next();
            double used = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.TOP);
            cursor = cursor - used;
        }

        // draw the bottom axes
        cursor = dataArea.getMaxY() + this.axisOffset.getBottomSpace(dataArea.getHeight());
        iterator = this.axesAtBottom.iterator();
        while (iterator.hasNext()) {
            ValueAxis axis = (ValueAxis) iterator.next();
            double used = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.BOTTOM);
            cursor = cursor + used;
        }

        // draw the left axes
        cursor = dataArea.getMinX() - this.axisOffset.getLeftSpace(dataArea.getWidth());
        iterator = this.axesAtLeft.iterator();
        while (iterator.hasNext()) {
            ValueAxis axis = (ValueAxis) iterator.next();
            double used = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.LEFT);
            cursor = cursor - used;
        }

        // draw the right axes
        cursor = dataArea.getMaxX() + this.axisOffset.getRightSpace(dataArea.getWidth());
        iterator = this.axesAtRight.iterator();
        while (iterator.hasNext()) {
            ValueAxis axis = (ValueAxis) iterator.next();
            double used = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.RIGHT);
            cursor = cursor + used;
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
    public void render(Graphics2D g2, Rectangle2D dataArea,
                       ChartRenderingInfo info, CrosshairInfo crosshairInfo) {

        // now get the data and plot it (the visual representation will depend
        // on the renderer that has been set)...
        XYDataset data = getDataset();
        if (!DatasetUtilities.isEmptyOrNull(data)) {

            int passCount = this.renderer.initialise(g2, dataArea, this, data, info);
            ValueAxis xAxis = getDomainAxis();
            ValueAxis yAxis = getRangeAxis();
            for (int pass = 0; pass < passCount; pass++) {
                int seriesCount = data.getSeriesCount();
                for (int series = 0; series < seriesCount; series++) {
                    int itemCount = data.getItemCount(series);
                    for (int item = 0; item < itemCount; item++) {
                        renderer.drawItem(g2, dataArea, info, this,
                                          xAxis, yAxis,
                                          data, series, item,
                                          crosshairInfo, pass);

                    }
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

        for (int i = 0; i < getSecondaryDatasetCount(); i++) {

            XYDataset dataset2 = getSecondaryDataset(i);
            if (!DatasetUtilities.isEmptyOrNull(dataset2)) {

                ValueAxis xAxis = getDomainAxis();
                Integer key = (Integer) this.secondaryDatasetDomainAxisMap.get(i);
                if (key != null) {
                    ValueAxis axis = getSecondaryDomainAxis(key.intValue());
                    if (axis != null) {
                        xAxis = axis;
                    }
                }
                
                ValueAxis yAxis = getRangeAxis();
                Integer key2 = (Integer) this.secondaryDatasetRangeAxisMap.get(i);
                if (key2 != null) {
                    ValueAxis axis = getSecondaryRangeAxis(key2.intValue());
                    if (axis != null) {
                        yAxis = axis;
                    }
                }
                
                XYItemRenderer renderer = getSecondaryRenderer(i);
                if (renderer == null) {
                    renderer = getRenderer();
                }

                int passCount = renderer.initialise(g2, dataArea, this, dataset2, info);

                for (int pass = 0; pass < passCount; pass++) {
                    int seriesCount = dataset2.getSeriesCount();
                    for (int series = 0; series < seriesCount; series++) {
                        int itemCount = dataset2.getItemCount(series);
                        for (int item = 0; item < itemCount; item++) {
                            renderer.drawItem(g2, dataArea, info, this,
                                              xAxis, yAxis,
                                              dataset2, series, item,
                                              crosshairInfo, pass);

                        }
                    }
                }
            }
        }
    }

    /**
     * Draws the gridlines for the plot, if they are visible.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     */
    protected void drawGridlines(Graphics2D g2, Rectangle2D dataArea) {

        // no renderer, no gridlines...
        if (this.renderer == null) {
            return;
        }

        // draw the domain grid lines, if any...
        if (isDomainGridlinesVisible()) {
            Stroke gridStroke = getDomainGridlineStroke();
            Paint gridPaint = getDomainGridlinePaint();
            if ((gridStroke != null) && (gridPaint != null)) {
                Iterator iterator = getDomainAxis().getTicks().iterator();
                while (iterator.hasNext()) {
                    Tick tick = (Tick) iterator.next();
                    this.renderer.drawDomainGridLine(
                        g2, this, getDomainAxis(), dataArea, tick.getNumericalValue()
                    );
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
                    this.renderer.drawRangeGridLine(
                        g2, this, getRangeAxis(), dataArea, tick.getNumericalValue()
                    );
                }
            }
        }

    }

    /**
     * Draws the annotations for the plot.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param info  the chart rendering info.
     */
    public void drawAnnotations(Graphics2D g2,
                                Rectangle2D dataArea,
                                ChartRenderingInfo info) {

        // draw the annotations...
        if (this.annotations != null) {
            Iterator iterator = this.annotations.iterator();
            while (iterator.hasNext()) {
                XYAnnotation annotation = (XYAnnotation) iterator.next();
                annotation.draw(g2, this, dataArea, getDomainAxis(), getRangeAxis());
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

        double xx = getDomainAxis().translateValueToJava2D(value, dataArea, getDomainAxisEdge());
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

        double yy = getRangeAxis().translateValueToJava2D(value, dataArea, getRangeAxisEdge());
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
            double hvalue = hva.translateJava2DtoValue((float) x, info.getDataArea(), getDomainAxisEdge());

            setAnchorX(hvalue);
            setDomainCrosshairValue(hvalue);
        }

        // set the anchor value for the vertical axis...
        ValueAxis vva = getRangeAxis();
        if (vva != null) {
            double vvalue = vva.translateJava2DtoValue((float) y, info.getDataArea(), getRangeAxisEdge());
            setAnchorY(vvalue);
            setRangeCrosshairValue(vvalue);
        }

    }

    /**
     * Zooms the axis ranges by the specified percentage about the anchor point.
     *
     * @param percent  the amount of the zoom.
     */
    public void zoom(double percent) {

        if (percent > 0.0) {
            ValueAxis domainAxis = getDomainAxis();
            double range = domainAxis.getMaximumAxisValue() - domainAxis.getMinimumAxisValue();
            double scaledRange = range * percent;
            domainAxis.setRange(this.anchorX - scaledRange / 2.0,
                                this.anchorX + scaledRange / 2.0);

            ValueAxis rangeAxis = getRangeAxis();
            range = rangeAxis.getMaximumAxisValue()
                - rangeAxis.getMinimumAxisValue();
            scaledRange = range * percent;
            rangeAxis.setRange(this.anchorY - scaledRange / 2.0,
                               this.anchorY + scaledRange / 2.0);
        }
        else {
            getRangeAxis().setAutoRange(true);
            getDomainAxis().setAutoRange(true);
        }

    }

    /**
     * A utility method that returns a list of datasets that are mapped to a particular axis.
     * 
     * @param index  the axis index (<code>null</code> for primary axis).
     * 
     * @return A list of datasets.
     */
    private List getSecondaryDatasetsMappedToDomainAxis(Integer index) {
        List result = new ArrayList();
        for (int i = 0; i < this.secondaryDatasetDomainAxisMap.size(); i++) {
            Integer m = (Integer) this.secondaryDatasetDomainAxisMap.get(i);
            if (m == null) {
                if (index == null) {  // secondary dataset to primary axis
                    result.add(this.secondaryDatasets.get(i));
                }
            }
            else {
                if (m.equals(index)) {
                    result.add(this.secondaryDatasets.get(i));
                }
            }
            
        }
        return result;    
    }
    
    /**
     * A utility method that returns a list of datasets that are mapped to a particular axis.
     * 
     * @param index  the axis index (<code>null</code> for primary axis).
     * 
     * @return A list of datasets.
     */
    private List getSecondaryDatasetsMappedToRangeAxis(Integer index) {
        List result = new ArrayList();
        for (int i = 0; i < this.secondaryDatasetRangeAxisMap.size(); i++) {
            Integer m = (Integer) this.secondaryDatasetRangeAxisMap.get(i);
            if (m == null) {
                if (index == null) {  // secondary dataset to primary axis
                    result.add(this.secondaryDatasets.get(i));
                }
            }
            else {
                if (m.equals(index)) {
                    result.add(this.secondaryDatasets.get(i));
                }
            }
            
        }
        return result;    
    }

    /**
     * Returns the range for the specified axis.
     *
     * @param axis  the axis.
     *
     * @return the range.
     */
    public Range getDataRange(ValueAxis axis) {

        Range result = null;
        List mappedDatasets = new ArrayList();
        boolean domainAxis = true;
        
        if (axis == getDomainAxis()) {
            domainAxis = true;
            mappedDatasets.add(this.dataset);
            mappedDatasets.addAll(getSecondaryDatasetsMappedToDomainAxis(null));
            
        }
        else if (axis == getRangeAxis()) {
            domainAxis = false;
            mappedDatasets.add(this.dataset);
            mappedDatasets.addAll(getSecondaryDatasetsMappedToRangeAxis(null));
        }
        else {
            // is it a secondary domain axis?
            int domainIndex = this.secondaryDomainAxes.indexOf(axis);
            if (domainIndex >= 0) {
                domainAxis = true;
                mappedDatasets.addAll(getSecondaryDatasetsMappedToDomainAxis(new Integer(domainIndex)));
            }
            // or is it a secondary range axis?
            int rangeIndex = this.secondaryRangeAxes.indexOf(axis);
            if (rangeIndex >= 0) {
                domainAxis = false;
                mappedDatasets.addAll(getSecondaryDatasetsMappedToRangeAxis(new Integer(rangeIndex)));
            }
        }

        // iterate through the datasets that map to the axis and get the union of the ranges.
        Iterator iterator = mappedDatasets.iterator();
        while (iterator.hasNext()) {
            XYDataset d = (XYDataset) iterator.next();
            if (domainAxis) {
                result = Range.combine(result, DatasetUtilities.getDomainExtent(d));
            }
            else {
                result = Range.combine(result, DatasetUtilities.getRangeExtent(d));
            }
        }
        return result;

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

        for (int axisIndex = 0; axisIndex < this.secondaryDomainAxes.size(); axisIndex++) {
            ValueAxis secondaryDomainAxis = getSecondaryDomainAxis(axisIndex);
            if (secondaryDomainAxis != null) {
                secondaryDomainAxis.configure();
            }
        }

        for (int axisIndex = 0; axisIndex < this.secondaryRangeAxes.size(); axisIndex++) {
            ValueAxis secondaryRangeAxis = getSecondaryRangeAxis(axisIndex);
            if (secondaryRangeAxis != null) {
                secondaryRangeAxis.configure();
            }
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
                boolean b5 = ObjectUtils.equalOrBothNull(this.secondaryRangeAxes,
                                                         p.secondaryRangeAxes);
                boolean b6 = ObjectUtils.equalOrBothNull(this.secondaryRenderers,
                                                         p.secondaryRenderers);
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

    /**
     * Returns the fixed domain axis space.
     *
     * @return The fixed domain axis space (possibly <code>null</code>).
     */
    public AxisSpace getFixedDomainAxisSpace() {
        return this.fixedDomainAxisSpace;
    }

    /**
     * Sets the fixed domain axis space.
     *
     * @param space  the space.
     */
    public void setFixedDomainAxisSpace(AxisSpace space) {
        this.fixedDomainAxisSpace = space;
    }

    /**
     * Returns the fixed range axis space.
     *
     * @return The fixed range axis space.
     */
    public AxisSpace getFixedRangeAxisSpace() {
        return this.fixedRangeAxisSpace;
    }

    /**
     * Sets the fixed range axis space.
     *
     * @param space  the space.
     */
    public void setFixedRangeAxisSpace(AxisSpace space) {
        this.fixedRangeAxisSpace = space;
    }

    /**
     * Multiplies the range on the horizontal axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     */
    public void zoomHorizontalAxes(double factor) {

        PlotOrientation orientation = getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            ValueAxis rangeAxis = getRangeAxis();
            if (rangeAxis != null) {
                rangeAxis.resizeRange(factor);
            }
            for (int i = 0; i < this.secondaryRangeAxes.size(); i++) {
                ValueAxis rangeAxis2 = (ValueAxis) this.secondaryRangeAxes.get(i);
                if (rangeAxis2 != null) {
                    rangeAxis2.resizeRange(factor);
                }
            }
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            ValueAxis domainAxis = getDomainAxis();
            if (domainAxis != null) {
                domainAxis.resizeRange(factor);
            }
        }
    }

    /**
     * Zooms in on the horizontal axes.
     * 
     * @param lowerPercent  the lower bound.
     * @param upperPercent  the upper bound.
     */
    public void zoomHorizontalAxes(double lowerPercent, double upperPercent) {

        PlotOrientation orientation = getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            ValueAxis rangeAxis = getRangeAxis();
            if (rangeAxis != null) {
                rangeAxis.zoomRange(lowerPercent, upperPercent);
            }
            for (int i = 0; i < this.secondaryRangeAxes.size(); i++) {
                ValueAxis rangeAxis2 = (ValueAxis) this.secondaryRangeAxes.get(i);
                if (rangeAxis2 != null) {
                    rangeAxis2.zoomRange(lowerPercent, upperPercent);
                }
            }
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            ValueAxis domainAxis = getDomainAxis();
            if (domainAxis != null) {
                domainAxis.zoomRange(lowerPercent, upperPercent);
            }
        }
    }

    /**
     * Multiplies the range on the vertical axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     */
    public void zoomVerticalAxes(double factor) {

        PlotOrientation orientation = getOrientation();

        if (orientation == PlotOrientation.HORIZONTAL) {
            ValueAxis domainAxis = getDomainAxis();
            if (domainAxis != null) {
                domainAxis.resizeRange(factor);
            }
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            ValueAxis rangeAxis = getRangeAxis();
            if (rangeAxis != null) {
                rangeAxis.resizeRange(factor);
            }
            for (int i = 0; i < this.secondaryRangeAxes.size(); i++) {
                ValueAxis rangeAxis2 = (ValueAxis) this.secondaryRangeAxes.get(i);
                if (rangeAxis2 != null) {
                    rangeAxis2.resizeRange(factor);
                }
            }
        }

    }

    /**
     * Zooms in on the vertical axes.
     *
     * @param lowerPercent  the lower bound.
     * @param upperPercent  the upper bound.
     */
    public void zoomVerticalAxes(double lowerPercent, double upperPercent) {

        PlotOrientation orientation = getOrientation();
        if (orientation == PlotOrientation.VERTICAL) {
            ValueAxis rangeAxis = getRangeAxis();
            if (rangeAxis != null) {
                rangeAxis.zoomRange(lowerPercent, upperPercent);
            }
            for (int i = 0; i < this.secondaryRangeAxes.size(); i++) {
                ValueAxis rangeAxis2 = (ValueAxis) this.secondaryRangeAxes.get(i);
                if (rangeAxis2 != null) {
                    rangeAxis2.zoomRange(lowerPercent, upperPercent);
                }
            }
        }
        else if (orientation == PlotOrientation.HORIZONTAL) {
            ValueAxis domainAxis = getDomainAxis();
            if (domainAxis != null) {
                domainAxis.zoomRange(lowerPercent, upperPercent);
            }
        }
    }

    /**
     * Returns the anchor point x-value.
     *
     * @return The anchor value.
     */
    public double getAnchorX() {
        return this.anchorX;
    }

    /**
     * Sets the anchor point x-value.
     *
     * @param x  the value.
     * @param notify  a flag that controls whether or not listeners are notified.
     */
    public void setAnchorX(double x, boolean notify) {
        this.anchorX = x;
        if (notify) {
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Sets the anchor point x-value.  A {@link PlotChangeEvent} is sent to all registered
     * listeners.
     *
     * @param x  the value.
     */
    protected void setAnchorX(double x) {
        setAnchorX(x, true);
    }

    /**
     * Returns the anchor point y-value.
     *
     * @return The anchor value.
     */
    public double getAnchorY() {
        return this.anchorY;
    }

    /**
     * Sets the anchor point y-value.
     *
     * @param y  the value.
     * @param notify  a flag that controls whether or not listeners are notified.
     */
    public void setAnchorY(double y, boolean notify) {
        this.anchorY = y;
        if (notify) {
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Sets the anchor point y-value.  A {@link PlotChangeEvent} is sent to all registered
     * listeners.
     *
     * @param y  the value.
     */
    protected void setAnchorY(double y) {
        setAnchorY(y, true);
    }
    
    /**
     * Returns the number of series in the primary dataset for this plot.  If the dataset is
     * <code>null</code>, the method returns 0.
     *
     * @return The series count.
     */
    public int getSeriesCount() {

        int result = 0;

        if (this.dataset != null) {
            result = this.dataset.getSeriesCount();
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
        XYDataset dataset1 = getDataset();
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

        // get the legend items for the secondary datasets...
        int count = this.secondaryDatasets.size();
        for (int datasetIndex = 0; datasetIndex < count; datasetIndex++) {

            XYDataset dataset2 = getSecondaryDataset(datasetIndex);
            if (dataset2 != null) {
                XYItemRenderer renderer2 = getSecondaryRenderer(datasetIndex);
                if (renderer2 != null) {
                    int seriesCount = dataset2.getSeriesCount();
                    for (int i = 0; i < seriesCount; i++) {
                        LegendItem item = renderer2.getLegendItem(datasetIndex + 1, i);
                        result.add(item);
                    }
                }
            }
        }

        return result;

    }

    
    /**
     * A utility method for allocating an axis to one of the temporary lists.
     * 
     * @param axis  the axis.
     * @param edge  the edge.
     */
    private void addAxisToList(ValueAxis axis, RectangleEdge edge) {
        if (edge == RectangleEdge.TOP) {
            this.axesAtTop.add(axis);
        }
        else if (edge == RectangleEdge.BOTTOM) {
            this.axesAtBottom.add(axis);
        }
        else if (edge == RectangleEdge.LEFT) {
            this.axesAtLeft.add(axis);
        }
        else if (edge == RectangleEdge.RIGHT) {
            this.axesAtRight.add(axis);
        }
    }

}
