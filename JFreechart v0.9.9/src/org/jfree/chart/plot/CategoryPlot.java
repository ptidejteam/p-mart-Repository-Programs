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
 * $Id: CategoryPlot.java,v 1.1 2007/10/10 20:07:31 vauchers Exp $
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
 *
 */

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.Marker;
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


/**
 * A general plotting class that uses data from a {@link CategoryDataset} and renders each data
 * item using a {@link CategoryItemRenderer}.
 *
 * @author David Gilbert
 */
public class CategoryPlot extends Plot implements CategoryPlotConstants, ValueAxisPlot,
                                                  Serializable {

    /** The plot orientation. */
    private PlotOrientation orientation;

    /** The domain axis. */
    private CategoryAxis domainAxis;

    /** The location of the domain axis. */
    private AxisLocation domainAxisLocation;

    /**
     * A flag that controls whether or not the shared domain axis is drawn (only relevant when
     * the plot is being used as a subplot).
     */
    private boolean drawSharedDomainAxis;

    /** The range axis. */
    private ValueAxis rangeAxis;

    /** The range axis location. */
    private AxisLocation rangeAxisLocation;

    /** The renderer for the data items. */
    private CategoryItemRenderer renderer;

    /** The (optional) secondary range axis. */
    private ValueAxis secondaryRangeAxis;

    /** The (optional) secondary renderer. */
    private CategoryItemRenderer secondaryRenderer;

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

    private AxisSpace fixedDomainAxisSpace;
    
    private AxisSpace fixedRangeAxisSpace;

    /** The drawing supplier. */
    private DrawingSupplier drawingSupplier;

    /**
     * Creates a new plot.
     *
     * @param data  the dataset.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param renderer  the item renderer.
     *
     */
    public CategoryPlot(CategoryDataset data,
                        CategoryAxis domainAxis,
                        ValueAxis rangeAxis,
                        CategoryItemRenderer renderer) {

        super(data);

        this.orientation = PlotOrientation.VERTICAL;
        setDomainAxisLocation(AxisLocation.BOTTOM, false);
        setRangeAxisLocation(AxisLocation.LEFT, false);

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

        this.drawingSupplier = new DefaultDrawingSupplier();

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
            this.domainAxisLocation = shiftAxisLocation(this.domainAxisLocation);
            this.rangeAxisLocation = shiftAxisLocation(this.rangeAxisLocation);
            notifyListeners(new PlotChangeEvent(this));
        }
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
     * A convenience method that returns the dataset for the plot, cast as a
     * {@link CategoryDataset}.
     *
     * @return the dataset.
     */
    public CategoryDataset getCategoryDataset() {
        return (CategoryDataset) getDataset();
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
     * Sets the location of the domain axis (TOP, BOTTOM, LEFT or RIGHT).
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
     * Sets the location of the range axis (TOP, BOTTOM, LEFT or RIGHT).
     *
     * @param location  the location.
     */
    public void setRangeAxisLocation(AxisLocation location) {
        setRangeAxisLocation(location, true);
    }

    /**
     * Sets the location of the range axis (TOP, BOTTOM, LEFT or RIGHT).
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
     * Returns the secondary dataset, cast as a {@link CategoryDataset}.
     *
     * @return The secondary dataset (possibly <code>null</code>).
     */
    public CategoryDataset getSecondaryCategoryDataset() {
        return (CategoryDataset) getSecondaryDataset();
    }

    /**
     * Returns the (optional) secondary range axis.  Data from the secondary dataset is plotted
     * against this axis.
     *
     * @return The secondary range axis (possibly <code>null</code>).
     */
    public ValueAxis getSecondaryRangeAxis() {
        return this.secondaryRangeAxis;
    }

    /**
     * Sets the secondary range axis.
     * <p>
     * A {@link PlotChangeEvent} is sent to all registered listeners.
     *
     * @param axis  the axis (<code>null</code> permitted).
     */
    public void setSecondaryRangeAxis(ValueAxis axis) {

        ValueAxis existing = this.secondaryRangeAxis;
        this.secondaryRangeAxis = axis;
        if (axis != null) {
            try {
                axis.setPlot(this);
            }
            catch (PlotNotCompatibleException e) {
                this.secondaryRangeAxis = existing;
            }
            axis.addChangeListener(this);
        }

        // plot is likely registered as a listener with the existing axis...
        if (existing != null) {
            existing.removeChangeListener(this);
        }

        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Returns the renderer used to draw data items from the secondary dataset.
     *
     * @return The secondary renderer.
     */
    public CategoryItemRenderer getSecondaryRenderer() {
        return this.secondaryRenderer;
    }

    /**
     * Sets the renderer used to draw data items from the secondary dataset.
     *
     * @param renderer  the renderer.
     */
    public void setSecondaryRenderer(CategoryItemRenderer renderer) {
        this.secondaryRenderer = renderer;
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

    public CategoryAnchor getDomainGridlinePosition() {
        return this.domainGridlinePosition;
    }
    
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

        CategoryDataset data = getCategoryDataset();
        if (data != null) {
            int seriesCount = data.getRowCount();
            for (int i = 0; i < seriesCount; i++) {
                LegendItem item = this.renderer.getLegendItem(0, i);
                result.add(item);
            }
        }

        CategoryDataset data2 = getSecondaryCategoryDataset();
        if (data2 != null) {
            int seriesCount2 = data2.getRowCount();
            for (int i = 0; i < seriesCount2; i++) {
                LegendItem item = getSecondaryRenderer().getLegendItem(1, i);
                result.add(item);
            }
        }

        return result;

    }

    /**
     * Handles a 'click' on the plot by updating the anchor values...
     *
     * @param x  x-coordinate of the click.
     * @param y  y-coordinate of the click.
     * @param info  an optional info collection object to return data back to the caller.
     *
     */
    public void handleClick(int x, int y, ChartRenderingInfo info) {

        // set the anchor value for the range axis...
        PlotOrientation orientation = getOrientation();
        float java2D = 0.0f;
        if (orientation == PlotOrientation.HORIZONTAL) {
            java2D = (float) x;
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            java2D = (float) y;

        }
        ValueAxis rangeAxis = getRangeAxis();
        double value = rangeAxis.translateJava2DtoValue(java2D, info.getDataArea(),
                                                        getRangeAxisLocation());
        rangeAxis.setAnchorValue(value);
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

        ValueAxis rangeAxis = getRangeAxis();
        if (percent > 0.0) {
            double range = rangeAxis.getMaximumAxisValue() - rangeAxis.getMinimumAxisValue();
            double scaledRange = range * percent;
            rangeAxis.setAnchoredRange(scaledRange);
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
     * Returns the drawing supplier for the plot.
     *
     * @return The drawing supplier.
     */
    public DrawingSupplier getDrawingSupplier() {
        DrawingSupplier result = null;
        CategoryPlot parent = (CategoryPlot) getParent();
        if (parent != null) {
            return parent.getDrawingSupplier();
        }
        else {
            result = this.drawingSupplier;
        }
        return result;
    }

    /**
     * Sets the drawing supplier for the renderer.  The drawing supplier is responsible for
     * supplying a limitless (possibly repeating) sequence of <code>Paint</code>,
     * <code>Stroke</code> and <code>Shape</code> objects that the renderer can use to populate
     * its tables.  The supplier can be shared among multiple renderers.
     *
     * @param supplier  the new supplier.
     */
    public void setDrawingSupplier(DrawingSupplier supplier) {
        this.drawingSupplier = supplier;
    }

    /**
     * Calculates the data area.
     * 
     * @param g2
     * @param plotArea
     * 
     * @return The data area.
     */
    protected Rectangle2D getDataArea(Graphics2D g2, Rectangle2D plotArea) {

        Rectangle2D dataArea = null;

        // adjust the drawing area for the plot insets (if any)...
        Insets insets = getInsets();
        if (insets != null) {
            plotArea.setRect(plotArea.getX() + insets.left,
                             plotArea.getY() + insets.top,
                             plotArea.getWidth() - insets.left - insets.right,
                             plotArea.getHeight() - insets.top - insets.bottom);
        }

        // calculate the space required for the axes...
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
            Axis domainAxis = this.domainAxis;
            if (domainAxis != null) {
                space = domainAxis.reserveSpace(g2, this, plotArea, getDomainAxisLocation(), space);
            }
            else {
                if (this.drawSharedDomainAxis) {
                    space = getDomainAxis().reserveSpace(g2, this, plotArea,
                                                         getDomainAxisLocation(), space);
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
            if (rangeAxis1 != null) {
                space = rangeAxis1.reserveSpace(g2, this, plotArea, getRangeAxisLocation(), space);
            }

            // reserve space for the secondary range axis (if any)...
            AxisLocation secondaryAxisLocation = getOppositeAxisLocation(getRangeAxisLocation());
            Axis rangeAxis2 = getSecondaryRangeAxis();
            if (rangeAxis2 != null) {
                space = rangeAxis2.reserveSpace(g2, this, plotArea, 
                                                secondaryAxisLocation, space);
            }
        }
        dataArea = space.shrink(plotArea, null);

        return dataArea;

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

        Rectangle2D dataArea = getDataArea(g2, plotArea);

        if (info != null) {
            info.setDataArea(dataArea);
        }

        // if there is a renderer, it draws the background, otherwise use the default background...
        CategoryItemRenderer renderer = getRenderer();
        if (renderer != null) {
            renderer.drawBackground(g2, this, dataArea);
        }
        else {
            drawBackground(g2, dataArea);
        }

        if (domainAxis != null) {
            domainAxis.draw(g2, plotArea, dataArea, getDomainAxisLocation());
        }
        else {
            if (this.drawSharedDomainAxis) {
                getDomainAxis().draw(g2, plotArea, dataArea, getDomainAxisLocation());
            }
        }

        if (rangeAxis != null) {
            rangeAxis.draw(g2, plotArea, dataArea, getRangeAxisLocation());
        }

        if (this.secondaryRangeAxis != null) {
            AxisLocation l = getOppositeAxisLocation(getRangeAxisLocation());
            this.secondaryRangeAxis.draw(g2, plotArea, dataArea, l);
        }

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
     * Draws a representation of the data within the dataArea region, using
     * the current renderer.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param info  an optional object for collection dimension information.
     */
    public void render(Graphics2D g2, Rectangle2D dataArea, ChartRenderingInfo info) {

        CategoryDataset data = getCategoryDataset();
        if (!DatasetUtilities.isEmptyOrNull(data)) {

            Shape savedClip = g2.getClip();
            g2.clip(dataArea);

            // set up the alpha-transparency...
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       getForegroundAlpha()));

            CategoryItemRenderer renderer = getRenderer();
            renderer.initialise(g2, dataArea, this, info);

            int columnCount = data.getColumnCount();
            int rowCount = data.getRowCount();
            for (int column = 0; column < columnCount; column++) {
                for (int row = 0; row < rowCount; row++) {
                    renderer.drawItem(g2, dataArea,
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
     * Draws a representation of the data in the secondary dataset (if there is one) within the
     * dataArea region, using the current renderer.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param info  an optional object for collection dimension information.
     */
    public void render2(Graphics2D g2, Rectangle2D dataArea, ChartRenderingInfo info) {

        CategoryDataset dataset = getSecondaryCategoryDataset();
        if (!DatasetUtilities.isEmptyOrNull(dataset)) {

            Shape savedClip = g2.getClip();
            g2.clip(dataArea);

            // set up the alpha-transparency...
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       getForegroundAlpha()));

            ValueAxis rangeAxis = getSecondaryRangeAxis();
            if (rangeAxis == null) {
                rangeAxis = getRangeAxis();
            }
            CategoryItemRenderer renderer = getSecondaryRenderer();
            if (renderer != null) {
                renderer.initialise(g2, dataArea, this, info);

                int columnCount = dataset.getColumnCount();
                int rowCount = dataset.getRowCount();
                for (int column = 0; column < columnCount; column++) {
                    for (int row = 0; row < rowCount; row++) {
                        renderer.drawItem(g2, dataArea,
                                          this,
                                          getDomainAxis(),
                                          getSecondaryRangeAxis(),
                                          dataset, row, column);
                    }
                }
            }

            g2.setClip(savedClip);
            g2.setComposite(originalComposite);

        }

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
            AxisLocation domainAxisLocation = getDomainAxisLocation();
            Stroke gridStroke = getDomainGridlineStroke();
            Paint gridPaint = getDomainGridlinePaint();
            if ((gridStroke != null) && (gridPaint != null)) {
                // iterate over the categories
                CategoryDataset data = getCategoryDataset();
                if (data != null) {
                    CategoryAxis axis = getDomainAxis();
                    if (axis != null) {
                        int columnCount = data.getColumnCount();
                        for (int c = 0; c < columnCount; c++) {
                            double xx = axis.getCategoryJava2DCoordinate(
                                anchor, c, columnCount, dataArea, domainAxisLocation
                            );
                            renderer.drawDomainGridline(g2, this, dataArea, xx);
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
                        renderer.drawRangeGridline(g2, this, getRangeAxis(), dataArea,
                                                   tick.getNumericalValue());
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
        CategoryItemRenderer renderer = getRenderer();
        if ((getRangeMarkers() != null) && (renderer != null)) {
            Iterator iterator = getRangeMarkers().iterator();
            while (iterator.hasNext()) {
                Marker marker = (Marker) iterator.next();
                renderer.drawRangeMarker(g2, this, getRangeAxis(), marker, dataArea);
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
        CategoryItemRenderer renderer = getSecondaryRenderer();
        if (renderer == null) {
            return;
        }
        if ((getSecondaryRangeMarkers() != null)) {
            Iterator iterator = getSecondaryRangeMarkers().iterator();
            while (iterator.hasNext()) {
                Marker marker = (Marker) iterator.next();
                renderer.drawRangeMarker(g2, this, getSecondaryRangeAxis(), marker, dataArea);
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
                                                              getRangeAxisLocation());
        Line2D line = null;
        PlotOrientation orientation = this.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(java2D, dataArea.getMinY(), java2D, dataArea.getMaxY());
        }
        else if (orientation == PlotOrientation.VERTICAL) {
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

        CategoryDataset dataset = getCategoryDataset();
        if (dataset == null) {
            return null;
        }

        Range result = null;

        CategoryItemRenderer renderer = getRenderer();

        if (axis.equals(getSecondaryRangeAxis())) {
            dataset = getSecondaryCategoryDataset();
            if (getSecondaryRenderer() != null) {
                renderer = getSecondaryRenderer();
            }
        }

        if ((dataset != null) && (renderer != null)) {

            RangeType rangeType = renderer.getRangeType();
            if (rangeType == RangeType.STACKED) {
                result = DatasetUtilities.getStackedRangeExtent(dataset);
            }
            else {
                result = DatasetUtilities.getRangeExtent(dataset);
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
     * Translates an axis location from one orientation to the other.
     *
     * @param location  the existing location.
     *
     * @return The translated location.
     */
    private AxisLocation shiftAxisLocation(AxisLocation location) {

        AxisLocation result = null;
        if (location == AxisLocation.TOP) {
            result = AxisLocation.RIGHT;
        }
        else if (location == AxisLocation.BOTTOM) {
            result = AxisLocation.LEFT;
        }
        else if (location == AxisLocation.LEFT) {
            result = AxisLocation.BOTTOM;
        }
        else if (location == AxisLocation.RIGHT) {
            result = AxisLocation.TOP;
        }
        return result;

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
    
    public List getCategories() {
        List result = null;
        CategoryDataset dataset = getCategoryDataset();
        if (dataset != null) {
            result = Collections.unmodifiableList(dataset.getColumnKeys());
        }
        return result;
    }

}
