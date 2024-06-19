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
 *                   Richard Atkinson;
 *                   Arnaud Lelievre;
 *                   Nicolas Brodu;
 *                   Eduardo Ramalho;
 *
 * $Id: XYPlot.java,v 1.1 2007/10/10 19:19:05 vauchers Exp $
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
 * 27-Jul-2003 : Added support for stacked XY area charts (RA);
 * 19-Aug-2003 : Implemented Cloneable (DG);
 * 01-Sep-2003 : Fixed bug where change to secondary datasets didn't generate change 
 * 08-Sep-2003 : Added internationalization via use of properties resourceBundle (RFE 690236) (AL); 
 *               event (797466) (DG)
 * 08-Sep-2003 : Changed ValueAxis API (DG);
 * 08-Sep-2003 : Fixes for serialization (NB);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 17-Sep-2003 : Fixed zooming to include secondary domain axes (DG);
 * 18-Sep-2003 : Added getSecondaryDomainAxisCount() and getSecondaryRangeAxisCount() methods 
 *               suggested by Eduardo Ramalho (RFE 808548) (DG);
 * 23-Sep-2003 : Split domain and range markers into foreground and background (DG);
 * 06-Oct-2003 : Fixed bug in clearDomainMarkers() and clearRangeMarkers() methods.  Fixed
 *               bug (815876) in addSecondaryRangeMarker(...) method.  Added new 
 *               addSecondaryDomainMarker methods (see bug id 815869) (DG);
 * 10-Nov-2003 : Added getSecondaryDomain/RangeAxisMappedToDataset(...) methods requested by 
 *               Eduardo Ramalho (DG);
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.jfree.chart.CrosshairInfo;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.Marker;
import org.jfree.chart.Spacer;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisCollection;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.ValueTick;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.renderer.RangeType;
import org.jfree.chart.renderer.XYItemRenderer;
import org.jfree.chart.renderer.XYItemRendererState;
import org.jfree.data.DatasetChangeEvent;
import org.jfree.data.DatasetUtilities;
import org.jfree.data.Range;
import org.jfree.data.TableXYDataset;
import org.jfree.data.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.Layer;
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
public class XYPlot extends Plot implements ValueAxisPlot,
                                            PropertyChangeListener,
                                            Cloneable,
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

    /** The resourceBundle for the localization. */
    static protected ResourceBundle localizationResources = 
                            ResourceBundle.getBundle("org.jfree.chart.plot.LocalizationBundle");

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
    private transient List foregroundDomainMarkers;

    /** A list of markers (optional) for the domain axis. */
    private transient List backgroundDomainMarkers;

    /** A list of secondary markers (optional) for the secondary domain axis. */
    private transient Map secondaryForegroundDomainMarkers;

    /** A list of secondary markers (optional) for the secondary domain axis. */
    private transient Map secondaryBackgroundDomainMarkers;

    /** A list of markers (optional) for the range axis. */
    private transient List foregroundRangeMarkers;

    /** A list of markers (optional) for the range axis. */
    private transient List backgroundRangeMarkers;

    /** A list of secondary markers (optional) for the secondary range axis. */
    private transient Map secondaryForegroundRangeMarkers;

    /** A list of secondary markers (optional) for the secondary range axis. */
    private transient Map secondaryBackgroundRangeMarkers;

    /** A list of annotations (optional) for the plot. */
    private List annotations;

    /** The paint used for the domain tick bands (if any). */
    private transient Paint domainTickBandPaint;

    /** The paint used for the range tick bands (if any). */
    private transient Paint rangeTickBandPaint;

    /** The fixed domain axis space. */
    private AxisSpace fixedDomainAxisSpace;

    /** The fixed range axis space. */
    private AxisSpace fixedRangeAxisSpace;

    /** The weight for this plot (only relevant if this is a subplot in a combined plot). */
    private int weight;

    /** The domain anchor value. */
    private double domainAnchor;

    /** The range anchor value. */
    private double rangeAnchor;

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

        this.domainAnchor = 0.0;
        this.rangeAnchor = 0.0;

        this.domainAxis = domainAxis;
        if (domainAxis != null) {
            domainAxis.setPlot(this);
            domainAxis.addChangeListener(this);
            this.domainAnchor = domainAxis.getRange().getCentralValue();
        }
        this.domainAxisLocation = AxisLocation.BOTTOM_OR_LEFT;

        this.rangeAxis = rangeAxis;
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
            this.rangeAnchor = rangeAxis.getRange().getCentralValue();
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

        this.foregroundDomainMarkers = new java.util.ArrayList();
        this.backgroundDomainMarkers = new java.util.ArrayList();
        this.secondaryForegroundDomainMarkers = new HashMap();
        this.secondaryBackgroundDomainMarkers = new HashMap();
        
        this.rangeGridlinesVisible = true;
        this.rangeGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeGridlinePaint = DEFAULT_GRIDLINE_PAINT;

        this.foregroundRangeMarkers = new java.util.ArrayList();
        this.backgroundRangeMarkers = new java.util.ArrayList();
        this.secondaryForegroundRangeMarkers = new HashMap();
        this.secondaryBackgroundRangeMarkers = new HashMap();
        
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
     * Returns the plot type as a string.
     *
     * @return a short string describing the type of plot.
     */
    public String getPlotType() {
        return localizationResources.getString("XY_Plot");
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
            axis.setPlot(this);
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
            axis.setPlot(this);
        }

        this.secondaryDomainAxes.set(index, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Returns the number of secondary domain axes.
     * 
     * @return The axis count.
     */
    public int getSecondaryDomainAxisCount() {
        return this.secondaryDomainAxes.size();
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
            axis.setPlot(this);
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
            axis.setPlot(this);
        }

        this.secondaryRangeAxes.set(index, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Returns the number of secondary range axes.
     * 
     * @return The axis count.
     */
    public int getSecondaryRangeAxisCount() {
        return this.secondaryRangeAxes.size();
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
        XYDataset existing = (XYDataset) this.secondaryDatasets.get(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.secondaryDatasets.set(index, dataset);
        dataset.addChangeListener(this);

        // map dataset to main axis by default
        if (index >= this.secondaryDatasetRangeAxisMap.size()) {
            this.secondaryDatasetRangeAxisMap.set(index, null);
        }
        if (index >= this.secondaryDatasetDomainAxisMap.size()) {
            this.secondaryDatasetDomainAxisMap.set(index, null);
        }

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
        addDomainMarker(marker, Layer.FOREGROUND);
    }

    /**
     * Adds a marker for the domain axis.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param marker  the marker.
     * @param layer  the layer (foreground or background).
     */
    public void addDomainMarker(Marker marker, Layer layer) {
        if (layer == Layer.FOREGROUND) {
            if (this.foregroundDomainMarkers == null) {
                this.foregroundDomainMarkers = new java.util.ArrayList();
            }
            this.foregroundDomainMarkers.add(marker);
            notifyListeners(new PlotChangeEvent(this));
        }
        else if (layer == Layer.BACKGROUND) {
            if (this.backgroundDomainMarkers == null) {
                this.backgroundDomainMarkers = new java.util.ArrayList();
            }
            this.backgroundDomainMarkers.add(marker);
            notifyListeners(new PlotChangeEvent(this));            
        }
    }

    /**
     * Clears all the domain markers.
     */
    public void clearDomainMarkers() {
        if (this.foregroundDomainMarkers != null) {
            this.foregroundDomainMarkers.clear();
        }
        if (this.backgroundDomainMarkers != null) {
            this.backgroundDomainMarkers.clear();
        }
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Adds a secondary marker for the domain axis.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the domain axis, however this is entirely up to the renderer.
     *
     * @param marker  the marker.
     */
    public void addSecondaryDomainMarker(Marker marker) {
        addSecondaryDomainMarker(0, marker, Layer.FOREGROUND);
    }
    
    /**
     * Adds a secondary marker for the domain axis.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the domain axis, however this is entirely up to the renderer.
     *
     * @param index  the secondary axis index.
     * @param marker  the marker.
     * @param layer  the layer (foreground or background).
     */
    public void addSecondaryDomainMarker(int index, Marker marker, Layer layer) {
        Collection markers;
        if (layer == Layer.FOREGROUND) {
            markers = (Collection) this.secondaryForegroundDomainMarkers.get(new Integer(index));
            if (markers == null) {
                markers = new java.util.ArrayList();
                this.secondaryForegroundDomainMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        }
        else if (layer == Layer.BACKGROUND) {
            markers = (Collection) this.secondaryBackgroundDomainMarkers.get(new Integer(index));
            if (markers == null) {
                markers = new java.util.ArrayList();
                this.secondaryBackgroundDomainMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);            
        }
        notifyListeners(new PlotChangeEvent(this));
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
        addRangeMarker(marker, Layer.FOREGROUND);
    }
    
    /**
     * Adds a marker for the range axis.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param marker  the marker.
     * @param layer  the layer (foreground or background).
     */
    public void addRangeMarker(Marker marker, Layer layer) {
        if (layer == Layer.FOREGROUND) {
            if (this.foregroundRangeMarkers == null) {
                this.foregroundRangeMarkers = new java.util.ArrayList();
            }
            this.foregroundRangeMarkers.add(marker);
            notifyListeners(new PlotChangeEvent(this));
        }
        else if (layer == Layer.BACKGROUND) {
            if (this.backgroundRangeMarkers == null) {
                this.backgroundRangeMarkers = new java.util.ArrayList();
            }
            this.backgroundRangeMarkers.add(marker);
            notifyListeners(new PlotChangeEvent(this));            
        }
    }

    /**
     * Clears all the range markers.
     */
    public void clearRangeMarkers() {
        if (this.foregroundRangeMarkers != null) {
            this.foregroundRangeMarkers.clear();
        }
        if (this.backgroundRangeMarkers != null) {
            this.backgroundRangeMarkers.clear();
        }
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Adds a secondary marker for the range axis.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param marker  the marker.
     */
    public void addSecondaryRangeMarker(Marker marker) {
        addSecondaryRangeMarker(0, marker, Layer.FOREGROUND);
    }
    
    /**
     * Adds a secondary marker for the range axis.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param index  the secondary axis index.
     * @param marker  the marker.
     * @param layer  the layer (foreground or background).
     */
    public void addSecondaryRangeMarker(int index, Marker marker, Layer layer) {
        Collection markers;
        if (layer == Layer.FOREGROUND) {
            markers = (Collection) this.secondaryForegroundRangeMarkers.get(new Integer(index));
            if (markers == null) {
                markers = new java.util.ArrayList();
                this.secondaryForegroundRangeMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        }
        else if (layer == Layer.BACKGROUND) {
            markers = (Collection) this.secondaryBackgroundRangeMarkers.get(new Integer(index));
            if (markers == null) {
                markers = new java.util.ArrayList();
                this.secondaryBackgroundRangeMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);            
        }
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Clears all the secondary range markers.
     * 
     * @deprecated Use clearSecondaryRangeMarkers(int).
     */
    public void clearSecondaryRangeMarkers() {
        clearSecondaryRangeMarkers(0);
    }

    /**
     * Clears the (foreground and background) range markers for a particular secondary range axis.
     * 
     * @param index  the secondary range axis index.
     */
    public void clearSecondaryRangeMarkers(int index) {
        Integer key = new Integer(index);
        Collection markers = (Collection) this.secondaryBackgroundRangeMarkers.get(key);
        if (markers != null) {
            markers.clear();
        }
        markers = (Collection) this.secondaryForegroundRangeMarkers.get(key);
        if (markers != null) {
            markers.clear();
        }
        notifyListeners(new PlotChangeEvent(this));
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
     * Calculates the space required for the domain axis/axes.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param space  a carrier for the result (<code>null</code> permitted).
     *
     * @return  The required space.
     */
    protected AxisSpace calculateDomainAxisSpace(Graphics2D g2, Rectangle2D plotArea,
                                                 AxisSpace space) {

        if (space == null) {
            space = new AxisSpace();
        }

        // reserve some space for the domain axis...
        if (this.fixedDomainAxisSpace != null) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                space.ensureAtLeast(this.fixedDomainAxisSpace.getLeft(), RectangleEdge.LEFT);
                space.ensureAtLeast(this.fixedDomainAxisSpace.getRight(), RectangleEdge.RIGHT);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                space.ensureAtLeast(this.fixedDomainAxisSpace.getTop(), RectangleEdge.TOP);
                space.ensureAtLeast(this.fixedDomainAxisSpace.getBottom(), RectangleEdge.BOTTOM);
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

        return space;

    }

    /**
     * Calculates the space required for the range axis/axes.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param space  a carrier for the result (<code>null</code> permitted).
     *
     * @return  The required space.
     */
    protected AxisSpace calculateRangeAxisSpace(Graphics2D g2, Rectangle2D plotArea,
                                                AxisSpace space) {

        if (space == null) {
            space = new AxisSpace();
        }

        // reserve some space for the range axis...
        if (this.fixedRangeAxisSpace != null) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                space.ensureAtLeast(this.fixedRangeAxisSpace.getTop(), RectangleEdge.TOP);
                space.ensureAtLeast(this.fixedRangeAxisSpace.getBottom(), RectangleEdge.BOTTOM);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                space.ensureAtLeast(this.fixedRangeAxisSpace.getLeft(), RectangleEdge.LEFT);
                space.ensureAtLeast(this.fixedRangeAxisSpace.getRight(), RectangleEdge.RIGHT);
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
     * Calculates the space required for the axes.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     *
     * @return The required space.
     */
    protected AxisSpace calculateAxisSpace(Graphics2D g2, Rectangle2D plotArea) {

        AxisSpace space = new AxisSpace();
        space = calculateDomainAxisSpace(g2, plotArea, space);
        space = calculateRangeAxisSpace(g2, plotArea, space);
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
     * @param parentState  the state from the parent plot, if there is one.
     * @param state  collects chart drawing information (<code>null</code> permitted).
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, PlotState parentState,
                     PlotRenderingInfo state) {

        // if the plot area is too small, just return...
        boolean b1 = (plotArea.getWidth() <= MINIMUM_WIDTH_TO_DRAW);
        boolean b2 = (plotArea.getHeight() <= MINIMUM_HEIGHT_TO_DRAW);
        if (b1 || b2) {
            return;
        }

        // record the plot area...
        if (state != null) {
            state.setPlotArea(plotArea);
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

        if (state != null) {
            state.setDataArea(dataArea);
        }

        // draw the plot background and axes...
        drawBackground(g2, dataArea);
        Map axisStateMap = drawAxes(g2, plotArea, dataArea);

        CrosshairInfo crosshairInfo = new CrosshairInfo();
        crosshairInfo.setCrosshairDistance(Double.POSITIVE_INFINITY);
        crosshairInfo.setAnchorX(getDomainAnchor());
        crosshairInfo.setAnchorY(getRangeAnchor());
        double xx = getDomainAxis().translateValueToJava2D(getDomainAnchor(), dataArea, 
                                                           getDomainAxisEdge());
        double yy = getRangeAxis().translateValueToJava2D(getRangeAnchor(), dataArea, 
                                                          getRangeAxisEdge());
        crosshairInfo.setAnchorXView(xx);
        crosshairInfo.setAnchorYView(yy);
        
        if (this.renderer != null) {
            Shape originalClip = g2.getClip();
            Composite originalComposite = g2.getComposite();

            g2.clip(dataArea);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       getForegroundAlpha()));

            AxisState domainAxisState = (AxisState) axisStateMap.get(getDomainAxis());
            if (domainAxisState == null) {
                if (parentState != null) {
                    domainAxisState = (AxisState) parentState.getSharedAxisStates().get(
                        getDomainAxis());
                }
            }
            if (domainAxisState != null) {     
                drawDomainTickBands(g2, dataArea, domainAxisState.getTicks());
                drawDomainGridlines(g2, dataArea, domainAxisState.getTicks());
            }
            
            AxisState rangeAxisState = (AxisState) axisStateMap.get(getRangeAxis());
            if (rangeAxisState == null) {
                if (parentState != null) {
                    rangeAxisState = (AxisState) parentState.getSharedAxisStates().get(
                        getRangeAxis());
                }
            }
            if (rangeAxisState != null) {
                drawRangeTickBands(g2, dataArea, rangeAxisState.getTicks());
                drawRangeGridlines(g2, dataArea, rangeAxisState.getTicks());
            }
            
            // draw the markers...
            for (int i = 0; i < this.secondaryDomainAxes.size(); i++) {
                drawSecondaryDomainMarkers(g2, dataArea, i, Layer.BACKGROUND);
            }
            for (int i = 0; i < this.secondaryRangeAxes.size(); i++) {
                drawSecondaryRangeMarkers(g2, dataArea, i, Layer.BACKGROUND);
            }
            drawDomainMarkers(g2, dataArea, Layer.BACKGROUND);
            drawRangeMarkers(g2, dataArea, Layer.BACKGROUND);

            // draw...
            render(g2, dataArea, state, crosshairInfo);
            render2(g2, dataArea, state, crosshairInfo);

            for (int i = 0; i < this.secondaryDomainAxes.size(); i++) {
                drawSecondaryDomainMarkers(g2, dataArea, i, Layer.FOREGROUND);
            }
            for (int i = 0; i < this.secondaryRangeAxes.size(); i++) {
                drawSecondaryRangeMarkers(g2, dataArea, i, Layer.FOREGROUND);
            }

            drawDomainMarkers(g2, dataArea, Layer.FOREGROUND);
            drawRangeMarkers(g2, dataArea, Layer.FOREGROUND);

            drawAnnotations(g2, dataArea, state);

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
     * @param ticks  the ticks.
     */
    public void drawDomainTickBands(Graphics2D g2, Rectangle2D dataArea, List ticks) {
        // draw the domain tick bands, if any...
        Paint bandPaint = getDomainTickBandPaint();
        if (bandPaint != null) {
            boolean fillBand = false;
            final ValueAxis xAxis = getDomainAxis();
            double previous = xAxis.getLowerBound();
            Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {
                ValueTick tick = (ValueTick) iterator.next();
                double current = tick.getValue();
                if (fillBand) {
                    this.renderer.fillDomainGridBand(g2, this, xAxis, dataArea, previous, current);
                }
                previous = current;
                fillBand = !fillBand;
            }
            double end = xAxis.getUpperBound();
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
     * @param ticks  the ticks.
     */
    public void drawRangeTickBands(Graphics2D g2, Rectangle2D dataArea, List ticks) {

        // draw the range tick bands, if any...
        Paint bandPaint = getRangeTickBandPaint();
        if (bandPaint != null) {
            boolean fillBand = false;
            final ValueAxis rangeAxis = getRangeAxis();
            double previous = rangeAxis.getLowerBound();
            Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {
                ValueTick tick = (ValueTick) iterator.next();
                double current = tick.getValue();
                if (fillBand) {
                    renderer.fillRangeGridBand(g2, this, rangeAxis, dataArea, previous, current);
                }
                previous = current;
                fillBand = !fillBand;
            }
            double end = rangeAxis.getUpperBound();
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
     * 
     * @return A map containing the state for each axis drawn.
     */
    protected Map drawAxes(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea) {

        AxisCollection axisCollection = new AxisCollection();

        // add each axis to the appropriate list...
        if (this.domainAxis != null) {
            axisCollection.add(this.domainAxis, getDomainAxisEdge());
        }
        if (this.rangeAxis != null) {
            axisCollection.add(this.rangeAxis, getRangeAxisEdge());
        }

        // add secondary domain axes to lists...
        for (int index = 0; index < this.secondaryDomainAxes.size(); index++) {
            ValueAxis secondaryAxis = (ValueAxis) this.secondaryDomainAxes.get(index);
            if (secondaryAxis != null) {
                axisCollection.add(secondaryAxis, getSecondaryDomainAxisEdge(index));
            }
        }

        // add secondary range axes to lists...
        for (int index = 0; index < this.secondaryRangeAxes.size(); index++) {
            ValueAxis secondaryAxis = (ValueAxis) this.secondaryRangeAxes.get(index);
            if (secondaryAxis != null) {
                axisCollection.add(secondaryAxis, getSecondaryRangeAxisEdge(index));
            }
        }

        Map axisStateMap = new HashMap();

        // draw the top axes
        double cursor = dataArea.getMinY() - this.axisOffset.getTopSpace(dataArea.getHeight());
        Iterator iterator = axisCollection.getAxesAtTop().iterator();
        while (iterator.hasNext()) {
            ValueAxis axis = (ValueAxis) iterator.next();
            AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.TOP);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }

        // draw the bottom axes
        cursor = dataArea.getMaxY() + this.axisOffset.getBottomSpace(dataArea.getHeight());
        iterator = axisCollection.getAxesAtBottom().iterator();
        while (iterator.hasNext()) {
            ValueAxis axis = (ValueAxis) iterator.next();
            AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.BOTTOM);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }

        // draw the left axes
        cursor = dataArea.getMinX() - this.axisOffset.getLeftSpace(dataArea.getWidth());
        iterator = axisCollection.getAxesAtLeft().iterator();
        while (iterator.hasNext()) {
            ValueAxis axis = (ValueAxis) iterator.next();
            AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.LEFT);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }

        // draw the right axes
        cursor = dataArea.getMaxX() + this.axisOffset.getRightSpace(dataArea.getWidth());
        iterator = axisCollection.getAxesAtRight().iterator();
        while (iterator.hasNext()) {
            ValueAxis axis = (ValueAxis) iterator.next();
            AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.RIGHT);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }

        return axisStateMap;
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
                       PlotRenderingInfo info, CrosshairInfo crosshairInfo) {

        // now get the data and plot it (the visual representation will depend
        // on the renderer that has been set)...
        XYDataset data = getDataset();
        if (!DatasetUtilities.isEmptyOrNull(data)) {

            XYItemRendererState state = this.renderer.initialise(g2, dataArea, this, data, info);
            int passCount = this.renderer.getPassCount();
            ValueAxis xAxis = getDomainAxis();
            ValueAxis yAxis = getRangeAxis();
            for (int pass = 0; pass < passCount; pass++) {
                int seriesCount = data.getSeriesCount();
                for (int series = 0; series < seriesCount; series++) {
                    int itemCount = data.getItemCount(series);
                    for (int item = 0; item < itemCount; item++) {
                        renderer.drawItem(g2, state, dataArea, info, this,
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
     * Returns the domain axis for a secondary dataset.
     * 
     * @param index  the dataset index.
     * 
     * @return the axis 
     */
    public ValueAxis getSecondaryDomainAxisMappedToDataset(int index) {
        
        if (index >= this.getSecondaryDatasetCount()) {
            throw new IllegalArgumentException(); 
        }

        ValueAxis valueAxis = null;
        Integer axisIndex = (Integer) this.secondaryDatasetDomainAxisMap.get(index);
        // null value represents the primary axis 
        if (axisIndex == null) {
            valueAxis = getDomainAxis();
        }
        else {
            valueAxis = getSecondaryDomainAxis(axisIndex.intValue() );
        }
        return valueAxis;
        
    }

    /**
     * Returns the range axis for a secondary dataset.
     * 
     * @param index  the dataset index.
     * 
     * @return the axis 
     */
    public ValueAxis getSecondaryRangeAxisMappedToDataset(int index) {
        
        if (index >= this.getSecondaryDatasetCount()) {
            throw new IllegalArgumentException(); 
        }

        ValueAxis valueAxis = null;
        Integer axisIndex = (Integer) this.secondaryDatasetRangeAxisMap.get(index);
        // null value represents the primary axis 
        if (axisIndex == null) {
            valueAxis = getRangeAxis();
        }
        else {
            valueAxis = getSecondaryRangeAxis(axisIndex.intValue() );
        }
        return valueAxis;
        
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
                        PlotRenderingInfo info, CrosshairInfo crosshairInfo) {

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

                XYItemRendererState state = renderer.initialise(g2, dataArea, this, dataset2, info);
                int passCount = renderer.getPassCount();
                
                for (int pass = 0; pass < passCount; pass++) {
                    int seriesCount = dataset2.getSeriesCount();
                    for (int series = 0; series < seriesCount; series++) {
                        int itemCount = dataset2.getItemCount(series);
                        for (int item = 0; item < itemCount; item++) {
                            renderer.drawItem(g2, state, dataArea, info, this,
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
     * @param ticks  the ticks.
     */
    protected void drawDomainGridlines(Graphics2D g2, Rectangle2D dataArea, List ticks) {

        // no renderer, no gridlines...
        if (this.renderer == null) {
            return;
        }

        // draw the domain grid lines, if any...
        if (isDomainGridlinesVisible()) {
            Stroke gridStroke = getDomainGridlineStroke();
            Paint gridPaint = getDomainGridlinePaint();
            if ((gridStroke != null) && (gridPaint != null)) {
                Iterator iterator = ticks.iterator();
                while (iterator.hasNext()) {
                    ValueTick tick = (ValueTick) iterator.next();
                    this.renderer.drawDomainGridLine(
                        g2, this, getDomainAxis(), dataArea, tick.getValue()
                    );
                }
            }
        }
    }
    
    /**
     * Draws the gridlines for the plot, if they are visible.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param ticks  the ticks.
     */
    protected void drawRangeGridlines(Graphics2D g2, Rectangle2D dataArea, List ticks) {

        // draw the range grid lines, if any...
        if (isRangeGridlinesVisible()) {
            Stroke gridStroke = getRangeGridlineStroke();
            Paint gridPaint = getRangeGridlinePaint();
            if ((gridStroke != null) && (gridPaint != null)) {
                ValueAxis axis = getRangeAxis();
                if (axis != null) {
                    Iterator iterator = ticks.iterator();
                    while (iterator.hasNext()) {
                        ValueTick tick = (ValueTick) iterator.next();
                        this.renderer.drawRangeGridLine(
                            g2, this, getRangeAxis(), dataArea, tick.getValue()
                        );
                    }
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
                                PlotRenderingInfo info) {

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
     * Draws the domain markers (if any) for the specified layer.  This method is typically called 
     * from within the draw(...) method.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param layer  the layer (foreground or background).
     */
    protected void drawDomainMarkers(Graphics2D g2, Rectangle2D dataArea, Layer layer) {
        XYItemRenderer r = getRenderer();
        List markers = getDomainMarkers(layer);
        if (markers != null && (r != null)) {
            Iterator iterator = markers.iterator();
            while (iterator.hasNext()) {
                Marker marker = (Marker) iterator.next();
                r.drawDomainMarker(g2, this, getDomainAxis(), marker, dataArea);
            }
        }
    }

    /**
     * Draws the range markers (if any) for the specified layer.  This method is typically called 
     * from within the draw(...) method.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param layer  the layer (foreground or background).
     */
    protected void drawRangeMarkers(Graphics2D g2, Rectangle2D dataArea, Layer layer) {
        XYItemRenderer r = getRenderer();
        List markers = getRangeMarkers(layer);
        if (markers != null && (r != null)) {
            Iterator iterator = markers.iterator();
            while (iterator.hasNext()) {
                Marker marker = (Marker) iterator.next();
                r.drawRangeMarker(g2, this, getRangeAxis(), marker, dataArea);
            }
        }
    }

    /**
     * Draws the secondary domain markers (if any) for an axis and layer.  This method is 
     * typically called from within the draw(...) method.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param index  the secondary domain axis index.
     * @param layer  the layer (foreground or background).
     */
    protected void drawSecondaryDomainMarkers(Graphics2D g2, Rectangle2D dataArea, int index,
                                              Layer layer) {
                                                 
        XYItemRenderer r = getSecondaryRenderer(index);
        if (r == null) {
            return;
        }
        
        Collection markers = getSecondaryDomainMarkers(index, layer);
        ValueAxis axis = getSecondaryDomainAxis(index);
        if (markers != null && axis != null) {
            Iterator iterator = markers.iterator();
            while (iterator.hasNext()) {
                Marker marker = (Marker) iterator.next();
                renderer.drawDomainMarker(g2, this, axis, marker, dataArea);
            }
        }
        
    }

    /**
     * Draws the secondary range markers (if any) for an axis and layer.  This method is 
     * typically called from within the draw(...) method.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param index  the secondary range axis index.
     * @param layer  the layer (foreground or background).
     */
    protected void drawSecondaryRangeMarkers(Graphics2D g2, Rectangle2D dataArea, int index,
                                             Layer layer) {
                                                 
        XYItemRenderer r = getSecondaryRenderer(index);
        if (r == null) {
            return;
        }
        
        Collection markers = getSecondaryRangeMarkers(index, layer);
        ValueAxis axis = getSecondaryRangeAxis(index);
        if (markers != null && axis != null) {
            Iterator iterator = markers.iterator();
            while (iterator.hasNext()) {
                Marker marker = (Marker) iterator.next();
                renderer.drawRangeMarker(g2, this, axis, marker, dataArea);
            }
        }
        
    }

    /**
     * Returns the list of domain markers (read only) for the specified layer.
     * 
     * @param layer  the layer (foreground or background).
     *
     * @return The list of domain markers.
     */
    public List getDomainMarkers(Layer layer) {
        if (layer == Layer.FOREGROUND) {
            return Collections.unmodifiableList(this.foregroundDomainMarkers);            
        }
        else if (layer == Layer.BACKGROUND) {
            return Collections.unmodifiableList(this.backgroundDomainMarkers);            
        }
        else {
            throw new IllegalStateException("XYPlot.getDomainMarkers(..): layer?");
        }
    }
    
    /**
     * Returns the list of range markers (read only) for the specified layer.
     *
     * @param layer  the layer (foreground or background).
     * 
     * @return The list of range markers.
     */
    public List getRangeMarkers(Layer layer) {
        if (layer == Layer.FOREGROUND) {
            return Collections.unmodifiableList(this.foregroundRangeMarkers);            
        }
        else if (layer == Layer.BACKGROUND) {
            return Collections.unmodifiableList(this.backgroundRangeMarkers);            
        }
        else {
            throw new IllegalStateException("XYPlot.getRangeMarkers(..): layer?");
        }
    }
    
    /**
     * Returns a collection of secondary domain markers for a particular axis and layer.
     * 
     * @param index  the secondary axis index.
     * @param layer  the layer.
     * 
     * @return A collection of markers (possibly <code>null</code>).
     */
    public Collection getSecondaryDomainMarkers(int index, Layer layer) {
        Collection result = null;
        Integer key = new Integer(index);
        if (layer == Layer.FOREGROUND) {
            result = (Collection) this.secondaryForegroundDomainMarkers.get(key);
        }    
        else if (layer == Layer.BACKGROUND) {
            result = (Collection) this.secondaryBackgroundDomainMarkers.get(key);
        }
        if (result != null) {
            result = Collections.unmodifiableCollection(result);
        }
        return result;
    }
    
    /**
     * Returns a collection of secondary range markers for a particular axis and layer.
     * 
     * @param index  the secondary axis index.
     * @param layer  the layer.
     * 
     * @return A collection of markers (possibly <code>null</code>).
     */
    public Collection getSecondaryRangeMarkers(int index, Layer layer) {
        Collection result = null;
        Integer key = new Integer(index);
        if (layer == Layer.FOREGROUND) {
            result = (Collection) this.secondaryForegroundRangeMarkers.get(key);
        }    
        else if (layer == Layer.BACKGROUND) {
            result = (Collection) this.secondaryBackgroundRangeMarkers.get(key);
        }
        if (result != null) {
            result = Collections.unmodifiableCollection(result);
        }
        return result;
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

        if (getDomainAxis().getRange().contains(value)) {
            double xx = getDomainAxis().translateValueToJava2D(value, dataArea, 
                                                               getDomainAxisEdge());
            Line2D line = new Line2D.Double(xx, dataArea.getMinY(),
                                            xx, dataArea.getMaxY());
            g2.setStroke(stroke);
            g2.setPaint(paint);
            g2.draw(line);
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
     * @param x  the x-coordinate, where the click occurred, in Java2D space.
     * @param y  the y-coordinate, where the click occurred, in Java2D space.
     * @param info  object containing information about the plot dimensions.
     */
    public void handleClick(int x, int y, PlotRenderingInfo info) {

        Rectangle2D dataArea = info.getDataArea();
        if (dataArea.contains(x, y)) {
            // set the anchor value for the horizontal axis...
            ValueAxis da = getDomainAxis();
            if (da != null) {
                double hvalue = da.translateJava2DToValue(x, info.getDataArea(), 
                                                          getDomainAxisEdge());

                setAnchorX(hvalue);
                setDomainCrosshairValue(hvalue);
            }

            // set the anchor value for the vertical axis...
            ValueAxis ra = getRangeAxis();
            if (ra != null) {
                double vvalue = ra.translateJava2DToValue(y, info.getDataArea(), 
                                                          getRangeAxisEdge());
                setAnchorY(vvalue);
                setRangeCrosshairValue(vvalue);
            }
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
            double range = domainAxis.getRange().getLength();
            double scaledRange = range * percent;
            domainAxis.setRange(this.domainAnchor - scaledRange / 2.0,
                                this.domainAnchor + scaledRange / 2.0);

            ValueAxis rangeAxis = getRangeAxis();
            range = rangeAxis.getRange().getLength();
            scaledRange = range * percent;
            rangeAxis.setRange(this.rangeAnchor - scaledRange / 2.0,
                               this.rangeAnchor + scaledRange / 2.0);
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
                mappedDatasets.addAll(getSecondaryDatasetsMappedToDomainAxis(
                                          new Integer(domainIndex)));
            }
            // or is it a secondary range axis?
            int rangeIndex = this.secondaryRangeAxes.indexOf(axis);
            if (rangeIndex >= 0) {
                domainAxis = false;
                mappedDatasets.addAll(getSecondaryDatasetsMappedToRangeAxis(
                                          new Integer(rangeIndex)));
            }
        }

        // iterate through the datasets that map to the axis and get the union of the ranges.
        XYItemRenderer renderer = this.getRenderer();
        Iterator iterator = mappedDatasets.iterator();
        while (iterator.hasNext()) {
            XYDataset d = (XYDataset) iterator.next();
            if (domainAxis) {
                result = Range.combine(result, DatasetUtilities.getDomainExtent(d));
            }
            else {
                if (renderer == null ? false : renderer.getRangeType() == RangeType.STACKED) {
                    result = Range.combine(
                        result, DatasetUtilities.getStackedRangeExtent((TableXYDataset) d)
                    );
                }
                else {
                    result = Range.combine(result, DatasetUtilities.getRangeExtent(d));
                }
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
            for (int i = 0; i < this.secondaryDomainAxes.size(); i++) {
                ValueAxis domainAxis2 = (ValueAxis) this.secondaryDomainAxes.get(i);
                if (domainAxis2 != null) {
                    domainAxis2.resizeRange(factor);
                }
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
            for (int i = 0; i < this.secondaryDomainAxes.size(); i++) {
                ValueAxis domainAxis2 = (ValueAxis) this.secondaryDomainAxes.get(i);
                if (domainAxis2 != null) {
                    domainAxis2.zoomRange(lowerPercent, upperPercent);
                }
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
            for (int i = 0; i < this.secondaryDomainAxes.size(); i++) {
                ValueAxis domainAxis2 = (ValueAxis) this.secondaryDomainAxes.get(i);
                if (domainAxis2 != null) {
                    domainAxis2.resizeRange(factor);
                }
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
            for (int i = 0; i < this.secondaryDomainAxes.size(); i++) {
                ValueAxis domainAxis2 = (ValueAxis) this.secondaryDomainAxes.get(i);
                if (domainAxis2 != null) {
                    domainAxis2.zoomRange(lowerPercent, upperPercent);
                }
            }
        }
    }

    /**
     * Returns the anchor point x-value.
     *
     * @return The anchor value.
     * 
     * @deprecated Use getDomainAnchor.
     */
    public double getAnchorX() {
        return getDomainAnchor();
    }

    /**
     * Returns the domain anchor.
     * 
     * @return The domain anchor.
     */
    public double getDomainAnchor() {
        return this.domainAnchor;
    }
    
    /**
     * Sets the anchor point x-value.
     *
     * @param x  the value.
     * @param notify  a flag that controls whether or not listeners are notified.
     * 
     * @deprecated Use setDomainAnchor.
     */
    public void setAnchorX(double x, boolean notify) {
        setDomainAnchor(x, notify);
    }

    /**
     * Sets the anchor value for the domain (X) axis.  If requested, a {@link PlotChangeEvent} is 
     * sent to all registered listeners.
     * 
     * @param x  the anchor value.
     * @param notify  notify listeners?
     */
    public void setDomainAnchor(double x, boolean notify) {
        this.domainAnchor = x;
        if (notify) {
            notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    /**
     * Sets the anchor point x-value.  A {@link PlotChangeEvent} is sent to all registered
     * listeners.
     *
     * @param x  the value.
     * 
     * @deprecated Use setDomainAnchor(x).
     */
    protected void setAnchorX(double x) {
        setDomainAnchor(x, true);
    }

    /**
     * Sets the anchor value for the domain (X) axis.  A {@link PlotChangeEvent} is sent to all 
     * registered listeners.
     * 
     * @param x  the anchor value.
     */
    protected void setDomainAnchor(double x) {
        setDomainAnchor(x, true);
    }

    /**
     * Returns the anchor point y-value.
     *
     * @return The anchor value.
     * 
     * @deprecated Use getRangeAnchor().
     */
    public double getAnchorY() {
        return getRangeAnchor();
    }

    /**
     * Returns the range anchor value.
     * 
     * @return The range anchor value.
     */
    public double getRangeAnchor() {
        return this.rangeAnchor;
    }
    
    /**
     * Sets the anchor point y-value.
     *
     * @param y  the value.
     * @param notify  a flag that controls whether or not listeners are notified.
     * 
     * @deprecated Use setRangeAnchor(y, notify).
     */
    public void setAnchorY(double y, boolean notify) {
        setRangeAnchor(y, notify);
    }

    /**
     * Sets the anchor value for the range (Y) axis.  If requested, a {@link PlotChangeEvent} is
     * sent to all registered listeners.
     * 
     * @param y  the anchor value.
     * @param notify  notify listeners?
     */
    public void setRangeAnchor(double y, boolean notify) {
        this.rangeAnchor = y;
        if (notify) {
            notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    /**
     * Sets the anchor point y-value.  A {@link PlotChangeEvent} is sent to all registered
     * listeners.
     *
     * @param y  the value.
     * 
     * @deprecated Use setRangeAnchor(y).
     */
    protected void setAnchorY(double y) {
        setAnchorY(y, true);
    }

    /**
     * Sets the anchor value for the range (Y) axis.  A {@link PlotChangeEvent} is sent to all
     * registered listeners.
     * 
     * @param y  the anchor value.
     */
    protected void setRangeAnchor(double y) {
        setRangeAnchor(y, true);
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

        if (super.equals(obj) && obj instanceof XYPlot) {
            XYPlot p = (XYPlot) obj;
            boolean b0 = (this.orientation == p.orientation);
            boolean b1 = ObjectUtils.equal(this.axisOffset, p.axisOffset);
            boolean b2 = ObjectUtils.equal(this.domainAxis, p.domainAxis);
            boolean b3 = (this.domainAxisLocation == p.domainAxisLocation);
            boolean b4 = (this.secondaryDomainAxes.equals(p.secondaryDomainAxes));
            boolean b5 = (this.secondaryDomainAxisLocations.equals(p.secondaryDomainAxisLocations));
            boolean b6 = ObjectUtils.equal(this.rangeAxis, p.rangeAxis);
            boolean b7 = (this.rangeAxisLocation == p.rangeAxisLocation);
            boolean b8 = ObjectUtils.equal(this.renderer, p.renderer);
            boolean b9 = ObjectUtils.equal(this.secondaryRangeAxes, p.secondaryRangeAxes);
            boolean b10 = (this.secondaryRangeAxisLocations.equals(p.secondaryRangeAxisLocations));

            boolean b11 = ObjectUtils.equal(this.secondaryDatasetDomainAxisMap,
                                            p.secondaryDatasetDomainAxisMap);
            boolean b12 = ObjectUtils.equal(this.secondaryDatasetRangeAxisMap,
                                            p.secondaryDatasetRangeAxisMap);
            boolean b13 = ObjectUtils.equal(this.secondaryRenderers, p.secondaryRenderers);
            boolean b14 = (this.domainGridlinesVisible == p.domainGridlinesVisible);
            boolean b15 = ObjectUtils.equal(this.domainGridlineStroke, p.domainGridlineStroke);
            boolean b16 = ObjectUtils.equal(this.domainGridlinePaint, p.domainGridlinePaint);
            boolean b17 = (this.rangeGridlinesVisible == p.rangeGridlinesVisible);
            boolean b18 = ObjectUtils.equal(this.rangeGridlineStroke, p.rangeGridlineStroke);
            boolean b19 = ObjectUtils.equal(this.rangeGridlinePaint, p.rangeGridlinePaint);
            boolean b20 = (this.domainCrosshairVisible == p.domainCrosshairVisible);
            boolean b21 = (this.domainCrosshairValue == p.domainCrosshairValue);
            boolean b22 = ObjectUtils.equal(this.domainCrosshairStroke, p.domainCrosshairStroke);
            boolean b23 = ObjectUtils.equal(this.domainCrosshairPaint, p.domainCrosshairPaint);
            boolean b24 = (this.domainCrosshairLockedOnData == p.domainCrosshairLockedOnData);
            boolean b25 = (this.rangeCrosshairVisible == p.rangeCrosshairVisible);
            boolean b26 = (this.rangeCrosshairValue == p.rangeCrosshairValue);
            boolean b27 = ObjectUtils.equal(this.rangeCrosshairStroke, p.rangeCrosshairStroke);
            boolean b28 = ObjectUtils.equal(this.rangeCrosshairPaint, p.rangeCrosshairPaint);
            boolean b29 = (this.rangeCrosshairLockedOnData == p.rangeCrosshairLockedOnData);
            boolean b30 = ObjectUtils.equal(this.foregroundDomainMarkers, 
                                            p.foregroundDomainMarkers);
            boolean b31 = ObjectUtils.equal(this.backgroundDomainMarkers, 
                                            p.backgroundDomainMarkers);
            boolean b32 = ObjectUtils.equal(this.foregroundRangeMarkers, p.foregroundRangeMarkers);
            boolean b33 = ObjectUtils.equal(this.backgroundRangeMarkers, p.backgroundRangeMarkers);
            boolean b34 = ObjectUtils.equal(this.secondaryForegroundDomainMarkers, 
                                            p.secondaryForegroundDomainMarkers);
            boolean b35 = ObjectUtils.equal(this.secondaryBackgroundDomainMarkers, 
                                            p.secondaryBackgroundDomainMarkers);
            boolean b36 = ObjectUtils.equal(this.secondaryForegroundRangeMarkers, 
                                            p.secondaryForegroundRangeMarkers);
            boolean b37 = ObjectUtils.equal(this.secondaryBackgroundRangeMarkers, 
                                            p.secondaryBackgroundRangeMarkers);
            boolean b38 = ObjectUtils.equal(this.annotations, p.annotations);
            boolean b39 = (this.weight == p.weight);

            return b0 && b1 && b2 && b3 && b4 && b5 && b6 && b7 && b8 && b9 && b10
                      && b11 && b12 && b13 && b14 && b15 && b16 && b17 && b18 && b19 && b20
                      && b21 && b22 && b23 && b24 && b25 && b26 && b27 && b28 && b29 && b30
                      && b31 && b32 && b33 && b34 && b35 && b36 && b37 && b38 && b39;

        }

        return false;

    }

    /**
     * Returns a clone of the plot.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException  this can occur if some component of the plot cannot
     *         be cloned.
     */
    public Object clone() throws CloneNotSupportedException {

        XYPlot clone = (XYPlot) super.clone();
        //private PlotOrientation orientation <-- immutable
        //private Spacer axisOffset <-- immutable
        clone.domainAxis = (ValueAxis) ObjectUtils.clone(this.domainAxis);
        if (clone.domainAxis != null) {
            clone.domainAxis.setPlot(clone);
            clone.domainAxis.addChangeListener(clone);
        }
        //private AxisLocation domainAxisLocation <-- immutable
        clone.secondaryDomainAxes = (ObjectList) ObjectUtils.clone(this.secondaryDomainAxes);
        for (int i = 0; i < clone.secondaryDomainAxes.size(); ++i) {
            ((ValueAxis) clone.secondaryDomainAxes.get(i)).setPlot(clone);
            ((ValueAxis) clone.secondaryDomainAxes.get(i)).addChangeListener(clone);
        }
        clone.secondaryDomainAxisLocations = (ObjectList) this.secondaryDomainAxisLocations.clone();

        clone.rangeAxis = (ValueAxis) ObjectUtils.clone(this.rangeAxis);
        if (clone.rangeAxis != null) {
            clone.rangeAxis.setPlot(clone);
            clone.rangeAxis.addChangeListener(clone);
        }
        //private AxisLocation rangeAxisLocation <-- immutable
        clone.secondaryRangeAxes = (ObjectList) ObjectUtils.clone(this.secondaryRangeAxes);
        for (int i = 0; i < clone.secondaryRangeAxes.size(); ++i) {
            ((ValueAxis) clone.secondaryRangeAxes.get(i)).setPlot(clone);
            ((ValueAxis) clone.secondaryRangeAxes.get(i)).addChangeListener(clone);
        }
        clone.secondaryRangeAxisLocations = (ObjectList) ObjectUtils.clone(
            this.secondaryRangeAxisLocations);

        //private XYDataset dataset <-- just keep the reference, don't clone the dataset
        if (clone.dataset != null) {
            clone.dataset.addChangeListener(clone); 
        }

        clone.secondaryDatasets = (ObjectList) ObjectUtils.clone(this.secondaryDatasets);
        for (int i = 0; i < clone.secondaryDatasets.size(); ++i) {
            ((XYDataset) clone.secondaryDatasets.get(i)).addChangeListener(clone);
        }

        clone.secondaryDatasetDomainAxisMap = (ObjectList) ObjectUtils.clone(
            this.secondaryDatasetDomainAxisMap);
        clone.secondaryDatasetRangeAxisMap = (ObjectList) ObjectUtils.clone(
            this.secondaryDatasetRangeAxisMap);
        clone.renderer = (XYItemRenderer) ObjectUtils.clone(this.renderer);
        clone.secondaryRenderers = (ObjectList) ObjectUtils.clone(this.secondaryRenderers);
        //private boolean domainGridlinesVisible <-- primitive
        //private transient Stroke domainGridlineStroke <-- immutable
        //private transient Paint domainGridlinePaint <-- immutable
        //private boolean rangeGridlinesVisible <-- primitive
        //private transient Stroke rangeGridlineStroke <-- immutable
        //private transient Paint rangeGridlinePaint <-- immutable
        //private boolean domainCrosshairVisible <-- primitive
        //private double domainCrosshairValue <-- primitive
        //private transient Stroke domainCrosshairStroke <-- immutable
        //private transient Paint domainCrosshairPaint <-- immutable
        //private boolean domainCrosshairLockedOnData = true <-- primitive
        //private boolean rangeCrosshairVisible <-- primitive
        //private double rangeCrosshairValue <-- primitive
        //private transient Stroke rangeCrosshairStroke <-- immutable
        //private transient Paint rangeCrosshairPaint <-- immutable
        //private boolean rangeCrosshairLockedOnData = true <-- primitive
        clone.foregroundDomainMarkers = ObjectUtils.clone(this.foregroundDomainMarkers);
        clone.backgroundDomainMarkers = ObjectUtils.clone(this.backgroundDomainMarkers);
//        clone.secondaryForegroundDomainMarkers = ObjectUtils.clone(
//            this.secondaryForegroundDomainMarkers);
//        clone.secondaryBackgroundDomainMarkers = ObjectUtils.clone(
//            this.secondaryBackgroundDomainMarkers);
        clone.foregroundRangeMarkers = ObjectUtils.clone(this.foregroundRangeMarkers);
        clone.backgroundRangeMarkers = ObjectUtils.clone(this.backgroundRangeMarkers);
//        clone.secondaryRangeMarkers = ObjectUtils.clone(this.secondaryRangeMarkers);
        clone.annotations = ObjectUtils.clone(this.annotations);
        //private Paint domainTickBandPaint <-- immutable
        //private Paint rangeTickBandPaint <-- immutable
        clone.fixedDomainAxisSpace = (AxisSpace) ObjectUtils.clone(this.fixedDomainAxisSpace);
        clone.fixedRangeAxisSpace = (AxisSpace) ObjectUtils.clone(this.fixedRangeAxisSpace);
        //private int weight <-- primitive
        //private double anchorX <-- primitive
        //private double anchorY <-- primitive

        return clone;

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
        SerialUtilities.writePaint(this.domainTickBandPaint, stream);
        SerialUtilities.writePaint(this.rangeTickBandPaint, stream);
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
        this.domainTickBandPaint = SerialUtilities.readPaint(stream);
        this.rangeTickBandPaint = SerialUtilities.readPaint(stream);

        if (this.domainAxis != null) {
            this.domainAxis.setPlot(this);
            this.domainAxis.addChangeListener(this);
        }

        if (this.rangeAxis != null) {
            this.rangeAxis.setPlot(this);
            this.rangeAxis.addChangeListener(this);
        }
        
        if (this.dataset != null) {
            this.dataset.addChangeListener(this);
        }
        this.foregroundDomainMarkers = new java.util.ArrayList();
        this.backgroundDomainMarkers = new java.util.ArrayList();
        this.foregroundRangeMarkers = new java.util.ArrayList();
        this.backgroundRangeMarkers = new java.util.ArrayList();
        this.secondaryForegroundDomainMarkers = new HashMap();
        this.secondaryBackgroundDomainMarkers = new HashMap();
        this.secondaryForegroundRangeMarkers = new HashMap();
        this.secondaryBackgroundRangeMarkers = new HashMap();

    }

}
