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
 * -----------------
 * CategoryPlot.java
 * -----------------
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Jeremy Bowman;
 *
 * $Id: CategoryPlot.java,v 1.1 2007/10/10 19:05:14 vauchers Exp $
 *
 * Changes (from 21-Jun-2001)
 * --------------------------
 * 21-Jun-2001 : Removed redundant JFreeChart parameter from constructors (DG);
 * 21-Aug-2001 : Added standard header. Fixed DOS encoding problem (DG);
 * 18-Sep-2001 : Updated header (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 * 12-Dec-2001 : Changed constructors to protected (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 16-Jan-2002 : Increased maximum intro and trail gap percents, plus added some argument checking
 *               code.  Thanks to Taoufik Romdhane for suggesting this (DG);
 * 05-Feb-2002 : Added accessor methods for the tooltip generator, incorporated alpha-transparency
 *               for Plot and subclasses (DG);
 * 06-Mar-2002 : Updated import statements (DG);
 * 14-Mar-2002 : Renamed BarPlot.java --> CategoryPlot.java, and changed code to use the
 *               CategoryItemRenderer interface (DG);
 * 22-Mar-2002 : Dropped the getCategories() method (DG);
 * 23-Apr-2002 : Moved the dataset from the JFreeChart class to the Plot class (DG);
 * 29-Apr-2002 : New methods to support printing values at the end of bars, contributed by
 *               Jeremy Bowman (DG);
 * 11-May-2002 : New methods for label visibility and overlaid plot support, contributed by
 *               Jeremy Bowman (DG);
 * 06-Jun-2002 : Removed the tooltip generator, this is now stored with the renderer.  Moved
 *               constants into the CategoryPlotConstants interface.  Updated Javadoc
 *               comments (DG);
 * 10-Jun-2002 : Overridden datasetChanged(...) method to update the upper and lower bound on the
 *               range axis (if necessary), updated Javadocs (DG);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 20-Aug-2002 : Changed the constructor for Marker (DG);
 * 28-Aug-2002 : Added listener notification to setDomainAxis(...) and setRangeAxis(...) (DG);
 * 23-Sep-2002 : Added getLegendItems() method and fixed errors reported by Checkstyle (DG);
 * 28-Oct-2002 : Changes to the CategoryDataset interface (DG);
 * 05-Nov-2002 : Base dataset is now TableDataset not CategoryDataset (DG);
 * 07-Nov-2002 : Renamed labelXXX as valueLabelXXX (DG);
 * 18-Nov-2002 : Added grid settings for both domain and range axis (previously these were set in
 *               the axes) (DG);
 * 19-Nov-2002 : Added axis location parameters to constructor (DG);
 * 17-Jan-2003 : Moved to com.jrefinery.chart.plot package (DG);
 * 14-Feb-2003 : Fixed bug in auto-range calculation for secondary axis (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 02-May-2003 : Moved render(...) method up from subclasses. Added secondary range markers.
 *               Added an attribute to control the dataset rendering order.  Added a
 *               drawAnnotations(...) method.  Changed the axis location from an int to an
 *               AxisLocation (DG);
 * 07-May-2003 : Merged HorizontalCategoryPlot and VerticalCategoryPlot into this class (DG);
 * 02-Jun-2003 : Removed check for range axis compatibility (DG);
 * 04-Jul-2003 : Added a domain gridline position attribute (DG);
 * 21-Jul-2003 : Moved DrawingSupplier to Plot superclass (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.Marker;
import org.jfree.chart.Spacer;
import org.jfree.chart.annotations.CategoryAnnotation;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.Tick;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.renderer.CategoryItemRenderer;
import org.jfree.chart.renderer.RangeType;
import org.jfree.data.CategoryDataset;
import org.jfree.data.DatasetChangeEvent;
import org.jfree.data.DatasetUtilities;
import org.jfree.data.Range;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectList;

/**
 * A general plotting class that uses data from a {@link CategoryDataset} and renders each data
 * item using a {@link CategoryItemRenderer}.
 *
 * @author David Gilbert
 */
public class CategoryPlot extends Plot implements ValueAxisPlot,
                                                  Serializable {

    /** The default visibility of the grid lines plotted against the domain axis. */
    public static final boolean DEFAULT_DOMAIN_GRIDLINES_VISIBLE = false;

    /** The default visibility of the grid lines plotted against the range axis. */
    public static final boolean DEFAULT_RANGE_GRIDLINES_VISIBLE = true;

    /** The default grid line stroke. */
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5f,
        BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_BEVEL,
        0.0f,
        new float[] {2.0f, 2.0f},
        0.0f);

    /** The default grid line paint. */
    public static final Paint DEFAULT_GRIDLINE_PAINT = Color.lightGray;

    /** The default value label font. */
    public static final Font DEFAULT_VALUE_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /** The plot orientation. */
    private PlotOrientation orientation;

    /** The offset between the data area and the axes. */
    private Spacer axisOffset;

    /** The domain axis. */
    private CategoryAxis domainAxis;

    /** The location of the domain axis. */
    private AxisLocation domainAxisLocation;

    /** Storage for the (optional) secondary domain axes. */
    private ObjectList secondaryDomainAxes;

    /** Storage for the (optional) secondary domain axis locations. */
    private ObjectList secondaryDomainAxisLocations;

    /**
     * A flag that controls whether or not the shared domain axis is drawn (only relevant when
     * the plot is being used as a subplot).
     */
    private boolean drawSharedDomainAxis;

    /** The range axis. */
    private ValueAxis rangeAxis;

    /** The range axis location. */
    private AxisLocation rangeAxisLocation;

    /** Storage for the (optional) secondary range axes. */
    private ObjectList secondaryRangeAxes;

    /** Storage for the (optional) secondary range axis locations. */
    private ObjectList secondaryRangeAxisLocations;

    /** The primary dataset for the plot. */
    private CategoryDataset dataset;

    /** Storage for the (optional) secondary datasets. */
    private ObjectList secondaryDatasets;

    /** Storage for keys that map secondary datasets to domain axes. */
    private ObjectList secondaryDatasetDomainAxisMap;
    
    /** Storage for keys that map secondary datasets to range axes. */
    private ObjectList secondaryDatasetRangeAxisMap;

    /** The renderer for the data items. */
    private CategoryItemRenderer renderer;

    /** Storage for the (optional) secondary renderers. */
    private ObjectList secondaryRenderers;

    /** The dataset rendering order. */
    private DatasetRenderingOrder renderingOrder = DatasetRenderingOrder.STANDARD;

    /** A flag that controls whether the grid-lines for the domain axis are visible. */
    private boolean domainGridlinesVisible;

    /** The position of the domain gridlines relative to the category. */
    private CategoryAnchor domainGridlinePosition;

    /** The stroke used to draw the domain grid-lines. */
    private transient Stroke domainGridlineStroke;

    /** The paint used to draw the domain  grid-lines. */
    private transient Paint domainGridlinePaint;

    /** A flag that controls whether the grid-lines for the range axis are visible. */
    private boolean rangeGridlinesVisible;

    /** The stroke used to draw the range axis grid-lines. */
    private transient Stroke rangeGridlineStroke;

    /** The paint used to draw the range axis grid-lines. */
    private transient Paint rangeGridlinePaint;

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

    /** A list of markers (optional) for the range axis. */
    private List rangeMarkers;

    /** A list of secondary markers (optional) for the secondary range axis. */
    private List secondaryRangeMarkers;

    /** A list of annotations (optional) for the plot. */
    private List annotations;

    /**
     * The weight for the plot (only relevant when the plot is used as a subplot within a
     * combined plot).
     */
    private int weight;

    /** The fixed space for the domain axis. */
    private AxisSpace fixedDomainAxisSpace;

    /** The fixed space for the range axis. */
    private AxisSpace fixedRangeAxisSpace;

    /** Temporary storage. */
    private List axesAtTop;

    /** Temporary storage. */
    private List axesAtBottom;

    /** Temporary storage. */
    private List axesAtLeft;

    /** Temporary storage. */
    private List axesAtRight;

    /** The anchor value. */
    private double anchorValue;

    /**
     * Default constructor.
     */
    public CategoryPlot() {
        this(null, null, null, null);
    }

    /**
     * Creates a new plot.
     *
     * @param dataset  the dataset.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param renderer  the item renderer.
     *
     */
    public CategoryPlot(CategoryDataset dataset,
                        CategoryAxis domainAxis,
                        ValueAxis rangeAxis,
                        CategoryItemRenderer renderer) {

        super();

        this.orientation = PlotOrientation.VERTICAL;

        // allocate storage for secondary dataset, axes and renderers (all optional)
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

        this.axisOffset = new Spacer(Spacer.ABSOLUTE, 0.0, 0.0, 0.0, 0.0);

        setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT, false);
        setRangeAxisLocation(AxisLocation.TOP_OR_LEFT, false);

        this.renderer = renderer;
        if (renderer != null) {
            renderer.setPlot(this);
        }

        this.domainAxis = domainAxis;
        if (domainAxis != null) {
            domainAxis.setPlot(this);
            domainAxis.addChangeListener(this);
        }
        this.drawSharedDomainAxis = false;

        this.rangeAxis = rangeAxis;
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
        }

        this.domainGridlinesVisible = DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
        this.domainGridlinePosition = CategoryAnchor.MIDDLE;
        this.domainGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.domainGridlinePaint = DEFAULT_GRIDLINE_PAINT;

        this.rangeGridlinesVisible = DEFAULT_RANGE_GRIDLINES_VISIBLE;
        this.rangeGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeGridlinePaint = DEFAULT_GRIDLINE_PAINT;

        this.rangeMarkers = null;
        this.secondaryRangeMarkers = null;

        Marker baseline = new Marker(0.0,
                                     new Color(0.8f, 0.8f, 0.8f, 0.5f),
                                     new java.awt.BasicStroke(1.0f),
                                     new Color(0.85f, 0.85f, 0.95f, 0.5f), 0.6f);
        addRangeMarker(baseline);

        this.axesAtTop = new ArrayList();
        this.axesAtBottom = new ArrayList();
        this.axesAtLeft = new ArrayList();
        this.axesAtRight = new ArrayList();

        this.anchorValue = 0.0;

    }

    /**
     * Returns a string describing the type of plot.
     *
     * @return The type.
     */
    public String getPlotType() {
        return "Category Plot";
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
     * @param orientation  the orientation.
     */
    public void setOrientation(PlotOrientation orientation) {
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
     * is <code>null</code>, then the method will return the parent plot's domain axis (if
     * there is a parent plot).
     *
     * @return the domain axis.
     */
    public CategoryAxis getDomainAxis() {

        CategoryAxis result = this.domainAxis;

        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof CategoryPlot) {
                CategoryPlot p = (CategoryPlot) parent;
                result = p.getDomainAxis();
            }
        }

        return result;

    }

    /**
     * Sets the domain axis for the plot (this must be compatible with the
     * plot type or an exception is thrown).
     *
     * @param axis  the new axis.
     */
    public void setDomainAxis(CategoryAxis axis) {

        if (axis != null) {
            try {
                axis.setPlot(this);
            }
            catch (PlotNotCompatibleException e) {
                // ignored...
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

    /**
     * Returns the domain axis location.
     *
     * @return the domain axis location.
     */
    public AxisLocation getDomainAxisLocation() {
        return this.domainAxisLocation;
    }

    /**
     * Sets the location of the domain axis.  A {@link PlotChangeEvent} is sent to all registered
     * listeners.
     *
     * @param location  the axis location.
     */
    public void setDomainAxisLocation(AxisLocation location) {
        setDomainAxisLocation(location, true);
    }

    /**
     * Sets the location of the domain axis.
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
     * Returns the domain axis edge.
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
    public CategoryAxis getSecondaryDomainAxis(int index) {
        CategoryAxis result = null;
        if (index < this.secondaryDomainAxes.size()) {
            result = (CategoryAxis) this.secondaryDomainAxes.get(index);
        }
        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof CategoryPlot) {
                CategoryPlot cp = (CategoryPlot) parent;
                result = cp.getSecondaryDomainAxis(index);
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
    public void setSecondaryDomainAxis(int index, CategoryAxis axis) {

        CategoryAxis existing = getSecondaryDomainAxis(index);
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
            CategoryAxis axis = (CategoryAxis) this.secondaryDomainAxes.get(i);
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
            CategoryAxis axis = (CategoryAxis) this.secondaryDomainAxes.get(i);
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
            if (parent instanceof CategoryPlot) {
                CategoryPlot cp = (CategoryPlot) parent;
                result = cp.getSecondaryDomainAxisLocation(index);
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
     * null, then the method will return the parent plot's range axis (if there
     * is a parent plot).
     *
     * @return the range axis.
     */
    public ValueAxis getRangeAxis() {

        ValueAxis result = this.rangeAxis;

        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof CategoryPlot) {
                CategoryPlot cp = (CategoryPlot) parent;
                result = cp.getRangeAxis();
            }
        }

        return result;

    }

    /**
     * Sets the range axis for the plot.
     * <P>
     * An exception is thrown if the new axis and the plot are not mutually compatible.
     *
     * @param axis  the new axis.
     */
    public void setRangeAxis(ValueAxis axis) {

        if (axis != null) {
            try {
                axis.setPlot(this);
            }
            catch (PlotNotCompatibleException e) {

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

    /**
     * Returns the range axis location.
     *
     * @return the range axis location.
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
     * Returns the range axis edge.
     *
     * @return The edge.
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
            if (parent instanceof CategoryPlot) {
                CategoryPlot cp = (CategoryPlot) parent;
                result = cp.getSecondaryRangeAxis(index);
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
            if (parent instanceof CategoryPlot) {
                CategoryPlot cp = (CategoryPlot) parent;
                result = cp.getSecondaryRangeAxisLocation(index);
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
    public CategoryDataset getDataset() {
        return this.dataset;
    }

    /**
     * Sets the dataset for the plot, replacing the existing dataset if there is one.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     */
    public void setDataset(CategoryDataset dataset) {

        // if there is an existing dataset, remove the plot from the list of change listeners...
        CategoryDataset existing = this.dataset;
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
    public CategoryDataset getSecondaryDataset(int index) {
        CategoryDataset result = null;
        if (this.secondaryDatasets.size() > index) {
            result = (CategoryDataset) this.secondaryDatasets.get(index);
        }
        return result;
    }

    /**
     * Adds or changes a secondary dataset for the plot.
     *
     * @param index  the dataset index.
     * @param dataset  the dataset.
     */
    public void setSecondaryDataset(int index, CategoryDataset dataset) {
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
     * Returns a reference to the renderer for the plot.
     *
     * @return The renderer.
     */
    public CategoryItemRenderer getRenderer() {
        return this.renderer;
    }

    /**
     * Sets the renderer for the plot.  A {@link PlotChangeEvent} is sent to all registered
     * listeners.
     *
     * @param renderer  the renderer.
     */
    public void setRenderer(CategoryItemRenderer renderer) {
        setRenderer(renderer, true);
    }

    /**
     * Sets the renderer for the plot.
     * <p>
     * You can set the renderer to <code>null</code>, but this is not recommended because:
     * <ul>
     *   <li>no data will be displayed;</li>
     *   <li>the plot background will not be painted;</li>
     * </ul>
     *
     * @param renderer  the renderer (<code>null</code> permitted).
     * @param notify  notify listeners?
     */
    public void setRenderer(CategoryItemRenderer renderer, boolean notify) {

        this.renderer = renderer;
        if (renderer != null) {
            renderer.setPlot(this);
        }
        if (notify) {
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns a secondary renderer.
     *
     * @param index  the renderer index.
     *
     * @return The renderer (possibly <code>null</code>).
     */
    public CategoryItemRenderer getSecondaryRenderer(int index) {
        CategoryItemRenderer result = null;
        if (this.secondaryRenderers.size() > index) {
            result = (CategoryItemRenderer) this.secondaryRenderers.get(index);
        }
        return result;

    }

    /**
     * Sets a secondary renderer.  A {@link PlotChangeEvent} is sent to all registered listeners.
     *
     * @param index  the index.
     * @param renderer  the renderer.
     */
    public void setSecondaryRenderer(int index, CategoryItemRenderer renderer) {
        CategoryItemRenderer existing = getSecondaryRenderer(index);
        if (existing != null) {
           // existing.removePropertyChangeListener(this);
        }
        this.secondaryRenderers.set(index, renderer);
        if (renderer != null) {
            renderer.setPlot(this);
        }
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the dataset rendering order.
     *
     * @return The dataset rendering order.
     */
    public DatasetRenderingOrder getDatasetRenderingOrder() {
        return this.renderingOrder;
    }

    /**
     * Sets the rendering order.  A {@link PlotChangeEvent} is sent to all registered listeners.
     * <P>
     * By default, the plot renders the secondary dataset first, then the primary dataset (so that
     * the primary dataset overlays the secondary dataset).  You can reverse this if you want to.
     *
     * @param order  the rendering order.
     */
    public void setDatasetRenderingOrder(DatasetRenderingOrder order) {
        this.renderingOrder = order;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the flag that controls whether the domain grid-lines are visible.
     *
     * @return the <code>true</code> or <code>false</code>.
     */
    public boolean isDomainGridlinesVisible() {
        return this.domainGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether or not grid-lines are drawn against the domain axis.
     * <p>
     * If the flag value changes, a {@link PlotChangeEvent} is sent to all registered listeners.
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
     * Returns the position used for the domain gridlines.
     * 
     * @return The gridline position.
     */
    public CategoryAnchor getDomainGridlinePosition() {
        return this.domainGridlinePosition;
    }

    /**
     * Sets the position used for the domain gridlines.
     * 
     * @param position  the position.
     */
    public void setDomainGridlinePosition(CategoryAnchor position) {
        this.domainGridlinePosition = position;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the stroke used to draw grid-lines against the domain axis.
     *
     * @return the stroke.
     */
    public Stroke getDomainGridlineStroke() {
        return this.domainGridlineStroke;
    }

    /**
     * Sets the stroke used to draw grid-lines against the domain axis.  A {@link PlotChangeEvent}
     * is sent to all registered listeners.
     *
     * @param stroke  the stroke.
     */
    public void setDomainGridlineStroke(Stroke stroke) {
        this.domainGridlineStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the paint used to draw grid-lines against the domain axis.
     *
     * @return the paint.
     */
    public Paint getDomainGridlinePaint() {
        return this.domainGridlinePaint;
    }

    /**
     * Sets the paint used to draw the grid-lines (if any) against the domain axis.
     * A {@link PlotChangeEvent} is sent to all registered listeners.
     *
     * @param paint  the paint.
     */
    public void setDomainGridlinePaint(Paint paint) {
        this.domainGridlinePaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the flag that controls whether the range grid-lines are visible.
     *
     * @return the flag.
     */
    public boolean isRangeGridlinesVisible() {
        return this.rangeGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether or not grid-lines are drawn against the range axis.
     * If the flag changes value, a {@link PlotChangeEvent} is sent to all registered listeners.
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
     * Returns the stroke used to draw the grid-lines against the range axis.
     *
     * @return the stroke.
     */
    public Stroke getRangeGridlineStroke() {
        return this.rangeGridlineStroke;
    }

    /**
     * Sets the stroke used to draw the grid-lines against the range axis.
     * A {@link PlotChangeEvent} is sent to all registered listeners.
     *
     * @param stroke  the stroke.
     */
    public void setRangeGridlineStroke(Stroke stroke) {
        this.rangeGridlineStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the paint used to draw the grid-lines against the range axis.
     *
     * @return the paint.
     */
    public Paint getRangeGridlinePaint() {
        return this.rangeGridlinePaint;
    }

    /**
     * Sets the paint used to draw the grid lines against the range axis.
     * A {@link PlotChangeEvent} is sent to all registered listeners.
     *
     * @param paint  the paint.
     */
    public void setRangeGridlinePaint(Paint paint) {
        this.rangeGridlinePaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the legend items for the plot.  By default, this method creates a legend item for
     * each series in the primary and secondary datasets.  You can change this behaviour by
     * overriding this method.
     *
     * @return the legend items.
     */
    public LegendItemCollection getLegendItems() {

        LegendItemCollection result = new LegendItemCollection();

        CategoryDataset data = getDataset();
        if (data != null) {
            int seriesCount = data.getRowCount();
            for (int i = 0; i < seriesCount; i++) {
                CategoryItemRenderer r = getRenderer();
                if (r != null) {
                    LegendItem item = r.getLegendItem(0, i);
                    result.add(item);
                }
            }
        }

        // get the legend items for the secondary datasets...
        int count = this.secondaryDatasets.size();
        for (int datasetIndex = 0; datasetIndex < count; datasetIndex++) {

            CategoryDataset dataset2 = getSecondaryDataset(datasetIndex);
            if (dataset2 != null) {
                CategoryItemRenderer renderer2 = getSecondaryRenderer(datasetIndex);
                if (renderer2 != null) {
                    int seriesCount = dataset2.getRowCount();
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
     * Handles a 'click' on the plot by updating the anchor value.
     *
     * @param x  x-coordinate of the click.
     * @param y  y-coordinate of the click.
     * @param info  an optional info collection object to return data back to the caller.
     *
     */
    public void handleClick(int x, int y, ChartRenderingInfo info) {

        // set the anchor value for the range axis...
        float java2D = 0.0f;
        if (this.orientation == PlotOrientation.HORIZONTAL) {
            java2D = (float) x;
        }
        else if (this.orientation == PlotOrientation.VERTICAL) {
            java2D = (float) y;

        }
        RectangleEdge edge = Plot.resolveRangeAxisLocation(getRangeAxisLocation(), 
                                                           this.orientation);
        double value = this.rangeAxis.translateJava2DtoValue(java2D, info.getDataArea(), edge);
        setAnchorValue(value);
        setRangeCrosshairValue(value);

    }

    /**
     * Zooms (in or out) on the plot's value axis.
     * <p>
     * If the value 0.0 is passed in as the zoom percent, the auto-range
     * calculation for the axis is restored (which sets the range to include
     * the minimum and maximum data values, thus displaying all the data).
     *
     * @param percent  the zoom amount.
     */
    public void zoom(double percent) {

        if (percent > 0.0) {
            double range = this.rangeAxis.getMaximumAxisValue() - rangeAxis.getMinimumAxisValue();
            double scaledRange = range * percent;
            rangeAxis.setRange(this.anchorValue - scaledRange / 2.0,
                               this.anchorValue + scaledRange / 2.0);
        }
        else {
            rangeAxis.setAutoRange(true);
        }

    }

    /**
     * Receives notification of a change to the plot's dataset.
     * <P>
     * The range axis bounds will be recalculated if necessary.
     *
     * @param event  information about the event (not used here).
     */
    public void datasetChanged(DatasetChangeEvent event) {

        if (this.rangeAxis != null) {
            this.rangeAxis.configure();
        }
        int count = this.secondaryRangeAxes.size();
        for (int axisIndex = 0; axisIndex < count; axisIndex++) {
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
     * Adds a marker for display against the range axis.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param marker the marker.
     */
    public void addRangeMarker(Marker marker) {

        if (this.rangeMarkers == null) {
            this.rangeMarkers = new java.util.ArrayList();
        }
        this.rangeMarkers.add(marker);
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Clears all the range markers for the plot.
     */
    public void clearRangeMarkers() {
        if (this.rangeMarkers != null) {
            this.rangeMarkers.clear();
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the list of range markers (read only).
     *
     * @return The list of range markers.
     */
    public List getRangeMarkers() {
        return Collections.unmodifiableList(this.rangeMarkers);
    }

    /**
     * Adds a marker for display against the secondary range axis.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param marker the marker.
     */
    public void addSecondaryRangeMarker(Marker marker) {

        if (this.secondaryRangeMarkers == null) {
            this.secondaryRangeMarkers = new java.util.ArrayList();
        }
        this.secondaryRangeMarkers.add(marker);
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Clears all the secondary range markers for the plot.
     */
    public void clearSecondaryRangeMarkers() {
        if (this.secondaryRangeMarkers != null) {
            this.secondaryRangeMarkers.clear();
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the list of secondary range markers (read only).
     *
     * @return The list of secondary range markers.
     */
    public List getSecondaryRangeMarkers() {
        if (this.secondaryRangeMarkers != null) {
            return Collections.unmodifiableList(this.secondaryRangeMarkers);
        }
        else {
            return null;
        }
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
     * Returns the pen-style (<code>Stroke</code>) used to draw the crosshair (if visible).
     *
     * @return the crosshair stroke.
     */
    public Stroke getRangeCrosshairStroke() {
        return rangeCrosshairStroke;
    }

    /**
     * Sets the pen-style (<code>Stroke</code>) used to draw the crosshairs (if visible).
     * A {@link PlotChangeEvent} is sent to all registered listeners.
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
     * Returns the list of annotations.
     *
     * @return The list of annotations.
     */
    public List getAnnotations() {
        return this.annotations;
    }

    /**
     * Adds an annotation to the plot.
     *
     * @param annotation  the annotation.
     */
    public void addAnnotation(CategoryAnnotation annotation) {

        if (this.annotations == null) {
            this.annotations = new java.util.ArrayList();
        }
        this.annotations.add(annotation);
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Calculates the space required for the axes.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     *
     * @return The space required for the axes.
     */
    protected AxisSpace calculateAxisSpace(Graphics2D g2, Rectangle2D plotArea) {

        AxisSpace space = new AxisSpace();

        // reserve some space for the domain axis...
        if (this.fixedDomainAxisSpace != null) {
            if (this.orientation == PlotOrientation.HORIZONTAL) {
                space.setLeft(this.fixedDomainAxisSpace.getLeft());
                space.setRight(this.fixedDomainAxisSpace.getRight());
            }
            else if (this.orientation == PlotOrientation.VERTICAL) {
                space.setTop(this.fixedDomainAxisSpace.getTop());
                space.setBottom(this.fixedDomainAxisSpace.getBottom());
            }
        }
        else {
            RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(getDomainAxisLocation(),
                                                                      this.orientation);
            if (this.domainAxis != null) {
                space = this.domainAxis.reserveSpace(g2, this, plotArea, domainEdge, space);
            }
            else {
                if (this.drawSharedDomainAxis) {
                    space = getDomainAxis().reserveSpace(g2, this, plotArea, domainEdge, space);
                }
            }
        }

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
            // reserve space for the range axis...
            Axis rangeAxis1 = this.rangeAxis;
            RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(getRangeAxisLocation(),
                                                                    this.orientation);
            if (rangeAxis1 != null) {
                space = rangeAxis1.reserveSpace(g2, this, plotArea, rangeEdge, space);
            }

            // reserve space for the secondary range axes (if any)...
            for (int i = 0; i < this.secondaryRangeAxes.size(); i++) {
                Axis rangeAxis2 = getSecondaryRangeAxis(i);
                if (rangeAxis2 != null) {
                    RectangleEdge secondaryAxisLocation = getSecondaryRangeAxisEdge(i);
                    space = rangeAxis2.reserveSpace(g2, this, plotArea,
                                                    secondaryAxisLocation, space);
                }
            }
        }
        return space;
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     * <P>
     * At your option, you may supply an instance of {@link ChartRenderingInfo}.
     * If you do, it will be populated with information about the drawing,
     * including various plot dimensions and tooltip info.
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot (including axes) should be drawn.
     * @param info  collects info as the chart is drawn.
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

        // calculate the data area...
        AxisSpace space = calculateAxisSpace(g2, plotArea);
        Rectangle2D dataArea = space.shrink(plotArea, null);

        if (info != null) {
            info.setDataArea(dataArea);
        }

        // if there is a renderer, it draws the background, otherwise use the default background...
        if (this.renderer != null) {
            this.renderer.drawBackground(g2, this, dataArea);
        }
        else {
            drawBackground(g2, dataArea);
        }

        drawAxes(g2, plotArea, dataArea);
        drawGridlines(g2, dataArea);

        // draw the range markers...
        drawSecondaryRangeMarkers(g2, dataArea);
        drawRangeMarkers(g2, dataArea);

        // now render data items...
        DatasetRenderingOrder order = getDatasetRenderingOrder();
        if (order == DatasetRenderingOrder.STANDARD) {
            render2(g2, dataArea, info);
            render(g2, dataArea, info);
        }
        else if (order == DatasetRenderingOrder.REVERSE) {
            render(g2, dataArea, info);
            render2(g2, dataArea, info);
        }

        // draw vertical crosshair if required...
        if (isRangeCrosshairVisible()) {
            drawRangeLine(g2, dataArea, getRangeCrosshairValue(),
                          getRangeCrosshairStroke(),
                          getRangeCrosshairPaint());
        }

        // draw the annotations (if any)...
        drawAnnotations(g2, dataArea);

        // draw an outline around the plot area...
        if (renderer != null) {
            renderer.drawOutline(g2, this, dataArea);
        }
        else {
            drawOutline(g2, dataArea);
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
            CategoryAxis secondaryAxis = (CategoryAxis) this.secondaryDomainAxes.get(index);
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
            Axis axis = (Axis) iterator.next();
            if (axis != null) {
                double used = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.TOP);
                cursor = cursor - used;
            }
        }

        // draw the bottom axes
        cursor = dataArea.getMaxY() + this.axisOffset.getBottomSpace(dataArea.getHeight());
        iterator = this.axesAtBottom.iterator();
        while (iterator.hasNext()) {
            Axis axis = (Axis) iterator.next();
            if (axis != null) {
                double used = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.BOTTOM);
                cursor = cursor + used;
            }
        }

        // draw the left axes
        cursor = dataArea.getMinX() - this.axisOffset.getLeftSpace(dataArea.getWidth());
        iterator = this.axesAtLeft.iterator();
        while (iterator.hasNext()) {
            Axis axis = (Axis) iterator.next();
            if (axis != null) {
                double used = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.LEFT);
                cursor = cursor - used;
            }
        }

        // draw the right axes
        cursor = dataArea.getMaxX() + this.axisOffset.getRightSpace(dataArea.getWidth());
        iterator = this.axesAtRight.iterator();
        while (iterator.hasNext()) {
            Axis axis = (Axis) iterator.next();
            if (axis != null) {
                double used = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.RIGHT);
                cursor = cursor + used;
            }
        }
        
    }
    
    /**
     * Draws a representation of the data within the dataArea region, using
     * the current renderer.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param info  an optional object for collection dimension information.
     */
    public void render(Graphics2D g2, Rectangle2D dataArea, ChartRenderingInfo info) {

        if (this.renderer == null) {
            return;
        }

        CategoryDataset data = getDataset();
        if (!DatasetUtilities.isEmptyOrNull(data)) {

            Shape savedClip = g2.getClip();
            g2.clip(dataArea);

            // set up the alpha-transparency...
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       getForegroundAlpha()));

            this.renderer.initialise(g2, dataArea, this, info);

            int columnCount = data.getColumnCount();
            int rowCount = data.getRowCount();
            for (int column = 0; column < columnCount; column++) {
                for (int row = 0; row < rowCount; row++) {
                    this.renderer.drawItem(g2, dataArea,
                                           this,
                                           getDomainAxis(),
                                           getRangeAxis(),
                                           data, row, column);
                }
            }

            g2.setClip(savedClip);
            g2.setComposite(originalComposite);

        }
        else {
            drawNoDataMessage(g2, dataArea);
        }

    }

    /**
     * Draws a representation of the data in the secondary dataset(s) within the
     * dataArea region, using the current renderer.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param info  an optional object for collection dimension information.
     */
    public void render2(Graphics2D g2, Rectangle2D dataArea, ChartRenderingInfo info) {

        Shape savedClip = g2.getClip();
        g2.clip(dataArea);
        // set up the alpha-transparency...
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getForegroundAlpha()));

        for (int i = 0; i < this.secondaryDatasets.size(); i++) {

            CategoryDataset data2 = getSecondaryDataset(i);
            if (!DatasetUtilities.isEmptyOrNull(dataset)) {

                ValueAxis rangeAxis = getSecondaryRangeAxis(i);
                if (rangeAxis == null) {
                    rangeAxis = getRangeAxis();
                }
                CategoryItemRenderer renderer = getSecondaryRenderer(i);
                if (renderer != null) {
                    renderer.initialise(g2, dataArea, this, info);

                    int columnCount = data2.getColumnCount();
                    int rowCount = data2.getRowCount();
                    for (int column = 0; column < columnCount; column++) {
                        for (int row = 0; row < rowCount; row++) {
                            ValueAxis axis2 = getSecondaryRangeAxis(i);
                            if (axis2 == null) {
                                axis2 = getRangeAxis();
                            }
                            renderer.drawItem(g2, dataArea,
                                              this,
                                              getDomainAxis(),
                                              axis2,
                                              data2, row, column);
                        }
                    }
                }

            }
        }
        g2.setClip(savedClip);
        g2.setComposite(originalComposite);

    }

    /**
     * Draws the gridlines for the plot.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     */
    protected void drawGridlines(Graphics2D g2, Rectangle2D dataArea) {

        // draw the domain grid lines, if any...
        if (isDomainGridlinesVisible()) {
            CategoryAnchor anchor = getDomainGridlinePosition();
            RectangleEdge domainAxisEdge = getDomainAxisEdge();
            Stroke gridStroke = getDomainGridlineStroke();
            Paint gridPaint = getDomainGridlinePaint();
            if ((gridStroke != null) && (gridPaint != null)) {
                // iterate over the categories
                CategoryDataset data = getDataset();
                if (data != null) {
                    CategoryAxis axis = getDomainAxis();
                    if (axis != null) {
                        int columnCount = data.getColumnCount();
                        for (int c = 0; c < columnCount; c++) {
                            double xx = axis.getCategoryJava2DCoordinate(
                                anchor, c, columnCount, dataArea, domainAxisEdge
                            );
                            CategoryItemRenderer renderer = getRenderer();
                            if (renderer != null) {
                                renderer.drawDomainGridline(g2, this, dataArea, xx);
                            }
                        }
                    }
                }
            }
        }

        // draw the range grid lines, if any...
        if (isRangeGridlinesVisible()) {
            Stroke gridStroke = getRangeGridlineStroke();
            Paint gridPaint = getRangeGridlinePaint();
            if ((gridStroke != null) && (gridPaint != null)) {
                ValueAxis axis = getRangeAxis();
                if (axis != null) {
                    Iterator iterator = getRangeAxis().getTicks().iterator();
                    while (iterator.hasNext()) {
                        Tick tick = (Tick) iterator.next();
                        CategoryItemRenderer renderer = getRenderer();
                        if (renderer != null) {
                            renderer.drawRangeGridline(g2, this, getRangeAxis(), dataArea,
                                                       tick.getNumericalValue());
                        }
                    }
                }
            }
        }

    }

    /**
     * Draws the annotations...
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     */
    protected void drawAnnotations(Graphics2D g2, Rectangle2D dataArea) {

        if (getAnnotations() != null) {
            Iterator iterator = getAnnotations().iterator();
            while (iterator.hasNext()) {
                CategoryAnnotation annotation = (CategoryAnnotation) iterator.next();
                annotation.draw(g2, this, dataArea, getDomainAxis(), getRangeAxis());
            }
        }

    }

    /**
     * Draws the range markers (if any).  This method is typically called from within the
     * draw(...) method.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     */
    protected void drawRangeMarkers(Graphics2D g2, Rectangle2D dataArea) {
        CategoryItemRenderer r = getRenderer();
        if ((getRangeMarkers() != null) && (r != null)) {
            Iterator iterator = getRangeMarkers().iterator();
            while (iterator.hasNext()) {
                Marker marker = (Marker) iterator.next();
                r.drawRangeMarker(g2, this, getRangeAxis(), marker, dataArea);
            }
        }
    }

    /**
     * Draws the secondary range markers (if any).  This method is typically called from within
     * the draw(...) method.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     */
    protected void drawSecondaryRangeMarkers(Graphics2D g2, Rectangle2D dataArea) {
        CategoryItemRenderer r = getSecondaryRenderer(0);
        if (r == null) {
            return;
        }
        if ((getSecondaryRangeMarkers() != null)) {
            Iterator iterator = getSecondaryRangeMarkers().iterator();
            while (iterator.hasNext()) {
                Marker marker = (Marker) iterator.next();
                renderer.drawRangeMarker(g2, this, getSecondaryRangeAxis(0), marker, dataArea);
            }
        }
    }

    /**
     * Utility method for drawing a line perpendicular to the range axis (used for crosshairs).
     *
     * @param g2  the graphics device.
     * @param dataArea  the area defined by the axes.
     * @param value  the data value.
     * @param stroke  the line stroke.
     * @param paint  the line paint.
     */
    protected void drawRangeLine(Graphics2D g2,
                                 Rectangle2D dataArea,
                                 double value, Stroke stroke, Paint paint) {

        double java2D = getRangeAxis().translateValueToJava2D(value, dataArea,
                                                              getRangeAxisEdge());
        Line2D line = null;
        if (this.orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(java2D, dataArea.getMinY(), java2D, dataArea.getMaxY());
        }
        else if (this.orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(dataArea.getMinX(), java2D, dataArea.getMaxX(), java2D);
        }
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);

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
     * Returns the range of data values that will be plotted against the range axis.
     * <P>
     * If the dataset is <code>null</code>, this method returns <code>null</code>.
     *
     * @param axis  the axis.
     *
     * @return The data range.
     */
    public Range getDataRange(ValueAxis axis) {

        Range result = null;
        List mappedDatasets = new ArrayList();
        
        if (axis == getRangeAxis()) {
            mappedDatasets.add(this.dataset);
            mappedDatasets.addAll(getSecondaryDatasetsMappedToRangeAxis(null));
        }
        else {
            // or is it a secondary range axis?
            int rangeIndex = this.secondaryRangeAxes.indexOf(axis);
            if (rangeIndex >= 0) {
                mappedDatasets.addAll(getSecondaryDatasetsMappedToRangeAxis(new Integer(rangeIndex)));
            }
        }

        // iterate through the datasets that map to the axis and get the union of the ranges.
        Iterator iterator = mappedDatasets.iterator();
        while (iterator.hasNext()) {
            CategoryDataset d = (CategoryDataset) iterator.next();
            RangeType rangeType = renderer.getRangeType();
            if (rangeType == RangeType.STACKED) {
                result = Range.combine(result, DatasetUtilities.getStackedRangeExtent(d));
            }
            else {
                result = Range.combine(result, DatasetUtilities.getRangeExtent(d));
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
     * Returns the weight for this plot when it is used as a subplot within a combined plot.
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
     * Returns a list of the categories for the plot.
     * 
     * @return A list of the categories for the plot.
     */
    public List getCategories() {
        List result = null;
        if (this.dataset != null) {
            result = Collections.unmodifiableList(this.dataset.getColumnKeys());
        }
        return result;
    }

    /** 
     * A utility method for adding axes to temporary lists.
     * 
     * @param axis  the axis.
     * @param edge  the edge.
     */
    private void addAxisToList(Axis axis, RectangleEdge edge) {
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

    /**
     * Returns the flag that controls whether or not the shared domain axis is drawn for 
     * each subplot.
     * 
     * @return A boolean.
     */
    public boolean getDrawSharedDomainAxis() {
        return this.drawSharedDomainAxis;
    }

    /**
     * Multiplies the range on the horizontal axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     */
    public void zoomHorizontalAxes(double factor) {
        if (this.orientation == PlotOrientation.HORIZONTAL) {
            if (this.rangeAxis != null) {
                this.rangeAxis.resizeRange(factor);
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
     * Zooms in on the horizontal axes.
     * 
     * @param lowerPercent  the lower bound.
     * @param upperPercent  the upper bound.
     */
    public void zoomHorizontalAxes(double lowerPercent, double upperPercent) {
        if (this.orientation == PlotOrientation.HORIZONTAL) {
            if (this.rangeAxis != null) {
                this.rangeAxis.zoomRange(lowerPercent, upperPercent);
            }
            for (int i = 0; i < this.secondaryRangeAxes.size(); i++) {
                ValueAxis rangeAxis2 = (ValueAxis) this.secondaryRangeAxes.get(i);
                if (rangeAxis2 != null) {
                    rangeAxis2.zoomRange(lowerPercent, upperPercent);
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
        if (this.orientation == PlotOrientation.VERTICAL) {
            if (this.rangeAxis != null) {
                this.rangeAxis.resizeRange(factor);
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
        if (this.orientation == PlotOrientation.VERTICAL) {
            if (this.rangeAxis != null) {
                this.rangeAxis.zoomRange(lowerPercent, upperPercent);
            }
            for (int i = 0; i < this.secondaryRangeAxes.size(); i++) {
                ValueAxis rangeAxis2 = (ValueAxis) this.secondaryRangeAxes.get(i);
                if (rangeAxis2 != null) {
                    rangeAxis2.zoomRange(lowerPercent, upperPercent);
                }
            }
        }
    }

    /**
     * Returns the anchor value.
     * 
     * @return The anchor value.
     */
    public double getAnchorValue() {
        return this.anchorValue;
    }

    /**
     * Sets the anchor value.
     * 
     * @param value  the anchor value.
     */
    public void setAnchorValue(double value) {
        setAnchorValue(value, true);
    }

    /**
     * Sets the anchor value.
     * 
     * @param value  the value.
     * @param notify  notify listeners?
     */
    public void setAnchorValue(double value, boolean notify) {
        this.anchorValue = value;
        if (notify) {
            notifyListeners(new PlotChangeEvent(this));
        }
    }

}
