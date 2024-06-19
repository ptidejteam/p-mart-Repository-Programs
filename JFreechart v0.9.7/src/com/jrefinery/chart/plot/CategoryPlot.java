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
 * -----------------
 * CategoryPlot.java
 * -----------------
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Jeremy Bowman;
 *
 * $Id: CategoryPlot.java,v 1.1 2007/10/10 20:00:17 vauchers Exp $
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
 *
 */

package com.jrefinery.chart.plot;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import com.jrefinery.chart.LegendItem;
import com.jrefinery.chart.LegendItemCollection;
import com.jrefinery.chart.Marker;
import com.jrefinery.chart.annotations.CategoryAnnotation;
import com.jrefinery.chart.axis.AxisNotCompatibleException;
import com.jrefinery.chart.axis.CategoryAxis;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.event.PlotChangeEvent;
import com.jrefinery.chart.renderer.CategoryItemRenderer;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.DatasetChangeEvent;
import com.jrefinery.io.SerialUtilities;


/**
 * A general plotting class that uses data from a {@link CategoryDataset} and renders each data 
 * item using a {@link CategoryItemRenderer}.
 *
 * @author David Gilbert
 */
public abstract class CategoryPlot extends Plot implements CategoryPlotConstants, Serializable {

    /** The domain axis. */
    private CategoryAxis domainAxis;

    /** The location of the domain axis. */
    private int domainAxisLocation;

    /** The range axis. */
    private ValueAxis rangeAxis;

    /** The range axis location. */
    private int rangeAxisLocation;

    /** The renderer for the data items. */
    private CategoryItemRenderer renderer;

    /** The (optional) secondary range axis. */
    private ValueAxis secondaryRangeAxis;

    /** The (optional) secondary renderer. */
    private CategoryItemRenderer secondaryRenderer;

    /** A flag that controls whether the grid-lines for the domain axis are visible. */
    private boolean domainGridlinesVisible;

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

    /** The range anchor. */
    private double rangeAnchor;
    
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

    /** A flag indicating whether or not value labels are shown. */
    private boolean valueLabelsVisible;

    /** The value label font. */
    private Font valueLabelFont;

    /** The value label paint. */
    private transient Paint valueLabelPaint;

    /** The value label format pattern String. */
    private String valueLabelFormatPattern;

    /** The value label format. */
    private NumberFormat valueLabelFormatter;

    /** A flag indicating whether or not value labels are drawn vertically. */
    private boolean verticalValueLabels;

    /** A list of annotations (optional) for the plot. */
    private List annotations;

    /**
     * Constructs a category plot, using default values where necessary.
     * <p>
     * The domain axis location defaults to BOTTOM and the range axis location defaults to LEFT.
     * These settings are appropriate for a 'vertical' category plot, but must be overridden
     * for a 'horizontal' category plot.
     *
     * @param data  the dataset.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param renderer  the item renderer.
     *
     */
    protected CategoryPlot(CategoryDataset data,
                           CategoryAxis domainAxis,
                           ValueAxis rangeAxis,
                           CategoryItemRenderer renderer) {

        super(data);

        this.renderer = renderer;
        if (renderer != null) {
            renderer.setPlot(this);
        }

        this.domainAxis = domainAxis;
        if (domainAxis != null) {
            domainAxis.setPlot(this);
            domainAxis.addChangeListener(this);
        }

        this.rangeAxis = rangeAxis;
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
        }

        this.domainGridlinesVisible = DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
        this.domainGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.domainGridlinePaint = DEFAULT_GRIDLINE_PAINT;

        this.rangeGridlinesVisible = DEFAULT_RANGE_GRIDLINES_VISIBLE;
        this.rangeGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeGridlinePaint = DEFAULT_GRIDLINE_PAINT;

        this.valueLabelsVisible = false;
        this.valueLabelFont = DEFAULT_VALUE_LABEL_FONT;
        this.valueLabelPaint = Color.black;
        this.valueLabelFormatter = java.text.NumberFormat.getInstance();
        this.valueLabelFormatPattern = null;
        this.verticalValueLabels = false;

        this.rangeMarkers = null;

        Marker baseline = new Marker(0.0,
                                     new Color(0.8f, 0.8f, 0.8f, 0.5f),
                                     new java.awt.BasicStroke(1.0f),
                                     new Color(0.85f, 0.85f, 0.95f, 0.5f), 0.6f);
        addRangeMarker(baseline);

    }

    /**
     * Returns the legend items for the plot.
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
     * Returns a reference to the renderer for the plot.
     *
     * @return the renderer.
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
     * is null, then the method will return the parent plot's domain axis (if
     * there is a parent plot).
     *
     * @return the domain axis.
     */
    public CategoryAxis getDomainAxis() {

        CategoryAxis result = this.domainAxis;

        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof CategoryPlot) {
                CategoryPlot cp = (CategoryPlot) parent;
                result = cp.getDomainAxis();
            }
        }

        return result;

    }

    /**
     * Sets the domain axis for the plot (this must be compatible with the
     * plot type or an exception is thrown).
     *
     * @param axis  the new axis.
     *
     * @throws AxisNotCompatibleException  if axis are not compatible.
     */
    public void setDomainAxis(CategoryAxis axis) throws AxisNotCompatibleException {

        if (isCompatibleDomainAxis(axis)) {

            if (axis != null) {
                try {
                    axis.setPlot(this);
                }
                catch (PlotNotCompatibleException e) {
                    throw new AxisNotCompatibleException(
                        "CategoryPlot.setDomainAxis(...): plot not compatible with axis.");
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
                "CategoryPlot.setDomainAxis(...): axis not compatible with plot.");
        }

    }

    /**
     * Returns the domain axis location.
     *
     * @return the domain axis location.
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
     *
     * @throws AxisNotCompatibleException if axis are not compatible.
     */
    public void setRangeAxis(ValueAxis axis) throws AxisNotCompatibleException {

        if (isCompatibleRangeAxis(axis)) {

            if (axis != null) {
                try {
                    axis.setPlot(this);
                }
                catch (PlotNotCompatibleException e) {
                    throw new AxisNotCompatibleException(
                        "CategoryPlot.setRangeAxis(...): plot not compatible with axis.");
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
     * Returns the range axis location.
     *
     * @return the range axis location.
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

        if (isCompatibleRangeAxis(axis)) {

            ValueAxis existing = this.secondaryRangeAxis;
            this.secondaryRangeAxis = axis;
            if (axis != null) {
                try {
                    axis.setPlot(this);
                }
                catch (PlotNotCompatibleException e) {
                    this.secondaryRangeAxis = existing;
                    throw new AxisNotCompatibleException(
                        "CategoryPlot.setSecondaryRangeAxis(...): plot not compatible with axis.");
                }
                axis.addChangeListener(this);
            }

            // plot is likely registered as a listener with the existing axis...
            if (existing != null) {
                existing.removeChangeListener(this);
            }

            notifyListeners(new PlotChangeEvent(this));

        }
        else {
            throw new AxisNotCompatibleException(
                "Plot.setSecondaryRangeAxis(...): axis not compatible with plot.");
        }

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
     * Checks the compatibility of a domain axis, returning true if the axis
     * is compatible with the plot, and false otherwise.
     *
     * @param axis  the proposed axis.
     *
     * @return <code>true</code> if the axis is compatible with the plot.
     */
    public abstract boolean isCompatibleDomainAxis(CategoryAxis axis);

    /**
     * Checks the compatibility of a range axis, returning true if the axis is
     * compatible with the plot, and false otherwise.
     *
     * @param axis  the proposed axis.
     *
     * @return <code>true</code> if the axis is compatible with the plot.
     */
    public abstract boolean isCompatibleRangeAxis(ValueAxis axis);

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
     * Returns a flag that indicates whether or not the value labels are showing.
     *
     * @return the flag.
     */
    public boolean getValueLabelsVisible() {
        return this.valueLabelsVisible;
    }

    /**
     * Sets the flag that indicates whether or not the value labels are showing.
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param flag  the flag.
     *
     * @deprecated use setValueLabelsVisible(boolean).
     */
    public void setLabelsVisible(boolean flag) {
        setValueLabelsVisible(flag);
    }

    /**
     * Sets the flag that indicates whether or not the value labels are showing.
     * <P>
     * Registered listeners are notified of a general change to the axis.
     * <P>
     * Not all renderers support this yet.
     *
     * @param flag  the flag.
     */
    public void setValueLabelsVisible(boolean flag) {
        if (this.valueLabelsVisible != flag) {
            this.valueLabelsVisible = flag;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the value label font.
     *
     * @return the value label font.
     */
    public Font getValueLabelFont() {
        return this.valueLabelFont;
    }

    /**
     * Sets the value label font.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     *
     * @param font  the new value label font.
     */
    public void setValueLabelFont(Font font) {

        // check arguments...
        if (font == null) {
            throw new IllegalArgumentException(
                "CategoryPlot.setValueLabelFont(...): null font not allowed.");
        }

        // make the change...
        if (!this.valueLabelFont.equals(font)) {
            this.valueLabelFont = font;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the value label paint.
     *
     * @return the value label paint
     */
    public Paint getValueLabelPaint() {
        return this.valueLabelPaint;
    }

    /**
     * Sets the value label paint.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     *
     * @param paint  the new value label paint.
     */
    public void setValueLabelPaint(Paint paint) {

        // check arguments...
        if (paint == null) {
            throw new IllegalArgumentException(
                "CategoryPlot.setValueLabelPaint(...): null paint not allowed.");
        }

        // make the change...
        if (!this.valueLabelPaint.equals(paint)) {
            this.valueLabelPaint = paint;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the value label formatter.
     *
     * @return the value label formatter
     */
    public NumberFormat getValueLabelFormatter() {
        return this.valueLabelFormatter;
    }

    /**
     * Sets the format string for the value labels.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     *
     * @param format  the new value label format pattern. Use <code>null</code> if labels are
     *                not to be shown.
     */
    public void setValueLabelFormatString(String format) {

        boolean changed = false;

        if (format == null) {
             if (this.valueLabelFormatter != null) {
                 this.valueLabelFormatPattern = null;
                 this.valueLabelFormatter = null;
                 changed = true;
             }
        }
        else if (this.valueLabelFormatter == null || !format.equals(this.valueLabelFormatPattern)) {
            this.valueLabelFormatPattern = format;
            this.valueLabelFormatter = new DecimalFormat(format);
            changed = true;
        }

        if (changed) {
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns true if the value labels should be rotated to vertical, and
     * false for standard horizontal labels.
     *
     * @return a flag indicating the orientation of the value labels.
     */
    public boolean getVerticalValueLabels() {
        return this.verticalValueLabels;
    }

    /**
     * Sets the flag that determines the orientation of the value labels.
     * Registered listeners are notified that the plot has been changed.
     *
     * @param flag  the flag.
     */
    public void setVerticalValueLabels(boolean flag) {
        if (this.verticalValueLabels != flag) {
            this.verticalValueLabels = flag;
            notifyListeners(new PlotChangeEvent(this));
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
        super.datasetChanged(event);

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
     * Returns the list of range markers.
     *
     * @return the list of range markers.
     */
    public List getRangeMarkers() {
        return this.rangeMarkers;
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
        SerialUtilities.writePaint(this.valueLabelPaint, stream);
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
        this.valueLabelPaint = SerialUtilities.readPaint(stream);
        
        if (this.domainAxis != null) {
            this.domainAxis.addChangeListener(this);
        }
        
        if (this.rangeAxis != null) {
            this.rangeAxis.addChangeListener(this);
        }
    }
    
}
