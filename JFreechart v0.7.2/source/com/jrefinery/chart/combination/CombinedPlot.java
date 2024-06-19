/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * CombinedPlot.java
 * -----------------
 * (C) Copyright 2001, 2002, by Bill Kelemen.
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   -;
 *
 * $Id: CombinedPlot.java,v 1.1 2007/10/10 18:56:26 vauchers Exp $
 *
 * Changes:
 * --------
 * 06-Dec-2001 : Version 1 (BK);
 * 12-Dec-2001 : Removed unnecessary 'throws' clause from constructor (DG);
 * 18-Dec-2001 : Added plotArea attribute and get/set methods (BK);
 * 22-Dec-2001 : Fixed bug in chartChanged with multiple combinations of CombinedPlots (BK);
 * 08-Jan-2002 : Moved to new package com.jrefinery.chart.combination (DG);
 *
 */

package com.jrefinery.chart.combination;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import com.jrefinery.data.*;
import com.jrefinery.chart.*;
import com.jrefinery.chart.event.*;
import com.jrefinery.chart.tooltips.*;

/**
 * This Plot implements a combined plot that can contain one or more CombinedChart
 * objects. Each CombinedChart object is simply a JFreeChart object without title,
 * label, font, background and event handling.<p>
 *
 * CombinedPlots can display three types of plots: Vertically combined, Horizontally
 * combined or Overlaid. Also nesting of CombinedPlots via CombinedCharts is supported
 * (ex. nesting an Overlaid plot inside a Vertically combined plot). Depending on
 * the type of CombinedPlot, the horizontal, vertical or both axis will be shared
 * between the sub-plots.
 *
 * @com.jrefinery.chart.demo.JFreeChartDemo For example of usage
 *
 * @author Bill Kelemen (bill@kelemen-usa.com)
 */
public class CombinedPlot extends Plot implements HorizontalValuePlot, VerticalValuePlot  {

    /**
     * Constant used to define a horizontal CombinedPlot. All sub-plots are laid
     * out horizontally one after the other. Each sub-plot should have a different
     * horizontal axis, but the same vertical axis.
     */
    public static final int HORIZONTAL = 0;

    /**
     * Constant used to define a vertical CombinedPlot. All sub-plots are laid
     * out verticaly one on top of the other. Each sub-plot should have a different
     * vertical axis, but the same horizontal axis.
     */
    public static final int VERTICAL = 1;

    /**
     * Constant used to define an Overlaid CombinedPlot. All sub-plots are overlaid
     * one on top of the others. Both horizontal and vertixal axis should be the
     * same for all sub-plots.
     */
    public static final int OVERLAID = 3;

    /**
     * Stores the type of CombinedPlot.
     */
    private int type;

    /**
     * Stores the charts that will be combined together.
     */
    protected ArrayList charts = new ArrayList();

    /**
     * Total weights of all charts. Only valid for HORIZONTAL or VERTICAL types.
     */
    protected int weights = 0;

    /**
     * Keeps track that we need to call adjustPlots() before drawing charts.
     */
    protected boolean isAdjusted = false;

    /**
     * Keeps track that we adjusted the range of our axis. This array is indexed
     * by HORIZONTAL or VERTICAL constants.
     */
    protected boolean[] axisRangeSet = { false, false };

    /** Stores the last plotArea calculated in the draw() method. */
    private Rectangle2D plotArea;

    /**
     * Creates a HORIZONTAL or VERTICAL CombinedPlot. If type is HORIZONTAL, then
     * axis should contain the shared vertical axis. If type if VERTICAL, then
     * axis should contain the shared horizontal axis.
     *
     * @param axis Shared axis to use for all sub-plots.
     * @param type Type of CombinedPlot (HORIZONTAL or VERTICAL).
     */
    public CombinedPlot(Axis axis, int type) {

        super((type == VERTICAL ? axis : null), (type == HORIZONTAL ? axis : null));
        if (type != HORIZONTAL && type != VERTICAL) {
            throw new IllegalArgumentException("Invalid type (" + type + ")");
        }
        this.type = type;
        setInsets(new Insets(0, 0, 0, 0));

    }

    /**
     * Creates an OVERLAID CombinedPlot.
     *
     * @param horizontal Shared horizontal axis to use for all sub-plots.
     * @param vertical Shared vertical axis to use for all sub-plots.
     */
    public CombinedPlot(Axis horizontal, Axis vertical) {

        super(horizontal, vertical);
        type = OVERLAID;
        setInsets(new Insets(0, 0, 0, 0));

    }

    /**
     * Adds a CombinedChart to the CombinedPlot. Verifies that the shared axes are
     * the same and assigns a weight of 1 to this chart.
     *
     * @param chart The chart to add
     * @exception AxisNotCompatibleException If horizontal axis is not the same as
     *            previous charts, or if vertical axis is not compatible.
     * @exception IllegalArgumentException if weight is <= 0 for HORIZONTAL or
     *            VERTICAL plots, or weight != 0 for OVERLAID plots.
     */
    public void add(CombinedChart chart) throws AxisNotCompatibleException {
        add(chart, 1);
    }

    /**
     * Adds a CombinedChart to the CombinedPlot. Verifies that the shared axes are
     * the same.
     *
     * @param chart The chart to add
     * @param weight Weight of this chart relative to the rest. Must be greater than one.
     *            For an OVERLAID CombinedPlot, weight must be one.
     * @exception AxisNotCompatibleException If common axis is not the same as
     *            previous charts, or if the other axes is not compatible.
     * @exception IllegalArgumentException if weight is <= 0 for HORIZONTAL or
     *            VERTICAL plots, or weight != 0 for OVERLAID plots.
     */
    public void add(CombinedChart chart, int weight)
        throws AxisNotCompatibleException, IllegalArgumentException {

        // verify valid horizontal and vertical axis
        Plot p = chart.getPlot();
        if (getHorizontalAxis() != p.getHorizontalAxis() && type != HORIZONTAL) {
            throw new AxisNotCompatibleException("Can't combine with different horizontal axis");
        } else if (getVerticalAxis() != p.getVerticalAxis() && type != VERTICAL) {
            throw new AxisNotCompatibleException("Can't combine with different vertical axis");
        } else if (!isCompatibleHorizontalAxis((Axis)p.getHorizontalAxis())) {
            throw new AxisNotCompatibleException("Incompatible horizontal axis");
        } else if (!isCompatibleVerticalAxis((Axis)p.getVerticalAxis())) {
            throw new AxisNotCompatibleException("Incompatible vertical axis");
        }

        // verify valid weight
        if (weight <= 0 && type != OVERLAID) {
            throw new IllegalArgumentException("weight must be positive");
        } else if (weight != 1 && type == OVERLAID) {
            throw new IllegalArgumentException("weight must 1");
        }

        // only first chart of overlaid chart will draw background and outline
        if (charts.size() > 0 && type == OVERLAID) {
            p.setBackgroundPaint(null);
            p.setOutlineStroke(null);
            p.setOutlinePaint(null);
        }

        // store the chartot and its weight
        ChartInfo chartInfo = new ChartInfo(chart, weight);
        charts.add(chartInfo);

        // keep track of total weights
        weights += weight;
    }

    /**
     * Checks the compatibility of a horizontal axis, returning true if the axis is
     * compatible with the plot, and false otherwise.
     * @param axis The horizontal axis.
     */
    public boolean isCompatibleHorizontalAxis(Axis axis) {
        return ((axis instanceof HorizontalNumberAxis) ||
                (axis instanceof HorizontalDateAxis));
    }

    /**
     * Checks the compatibility of a vertical axis, returning true if the axis is compatible with
     * the plot, and false otherwise.  The vertical axis for this plot must be an instance of
     * VerticalNumberAxis.
     * @param axis The vertical axis.
     */
    public boolean isCompatibleVerticalAxis(Axis axis) {
        return (axis instanceof VerticalNumberAxis);
    }

    /**
     * Returns the plot type as a string. This implementation returns "Overlaid Plot",
     * "Horizontal Combined Plot", "Vertical Combined Plot" or "Unknown Combined Plot"
     * depending of the type of CombinedPlot.
     */
    public String getPlotType() {
        switch (type) {
          case OVERLAID:   return "Overlaid Plot";
          case HORIZONTAL: return "Horizontal Combined Plot";
          case VERTICAL:   return "Vertical Combined Plot";
          default:         return "Unknown Combined Plot";
        }
    }

    /**
     * Draws the CombinedPlot on a Java 2D graphics device (such as the screen or a printer).
     * Will perform all the placement calculations for each sub-plots and then tell these
     * to draw themselves.
     * <P>
     * @param g2 The graphics device;
     * @param drawArea The area within which the plot (including axis labels) should be drawn;
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, DrawInfo info) {

        int n = charts.size();
        int verticalGap = 0;
        int horizontalGap = 0;

        // adjust plot axis if needed
        adjustPlots();

        // adjust the drawing area for plot insets. insets are added as a border
        // and between sub-charts. For in-between space between sub-charts, the
        // max of insets.top and insets.bottom is used for VERTICAL plots and the
        // max of insets.right and insets.left for HORIZONTAL plots.
        drawArea = adjustForInsets(drawArea, insets);
        if (insets != null && type != OVERLAID) {
            verticalGap = Math.max(insets.top, insets.bottom);
            horizontalGap = Math.max(insets.right, insets.left);
        }

        // calculate shared axis height and width
        double hAxisAreaHeight = 0;
        double vAxisAreaWidth = 0;
        if (type != HORIZONTAL) {
            // all plots share the same horizontal axis, reserve the height
            hAxisAreaHeight = getHorizontalAxis().reserveHeight(g2, this, drawArea);
        }
        if (type != VERTICAL) {
            // all plots share the same vertical axis, reserve the width
            vAxisAreaWidth = getVerticalAxis().reserveWidth(g2, this, drawArea);
        }

        // make plotArea without shared axis areas
        Rectangle2D plotArea = new Rectangle2D.Double(drawArea.getX(),
                                                      drawArea.getY(),
                                                      drawArea.getWidth()-vAxisAreaWidth,
                                                      drawArea.getHeight()-hAxisAreaHeight);

        // calculate plotAreas of all sub-plots, maximum vertical/horizontal axis width/height
        Rectangle2D[] subPlotArea = new Rectangle2D[n];
        double x = plotArea.getX();
        double y = plotArea.getY();
        double usableWidth = plotArea.getWidth()-horizontalGap*(charts.size()-1);
        double usableHeight = plotArea.getHeight()-verticalGap*(charts.size()-1);
        double maxAxisWidth = Double.MIN_VALUE;
        double maxAxisHeight = Double.MIN_VALUE;
        for (int i=0; i<n; i++) {
            ChartInfo chartInfo = (ChartInfo)charts.get(i);
            Plot plot = chartInfo.plot;

            // calculate sub-plot height
            double subPlotAreaHeight = usableHeight;
            if (type == VERTICAL) {
                subPlotAreaHeight *= (double)chartInfo.weight/weights;
            }
            if (i == n-1) {
                // last plot has the visible horizontal axis
                subPlotAreaHeight += hAxisAreaHeight;
            }

            // calculate sub-plot width
            double subPlotAreaWidth = usableWidth;
            if (type == HORIZONTAL) {
                subPlotAreaWidth *= (double)chartInfo.weight/weights;
            }
            if (i == 0) {
                // first plot has the visible vertical axis
                subPlotAreaWidth += vAxisAreaWidth;
            }

            // calculate sub-plot area
            subPlotArea[i] = new Rectangle2D.Double(x, y, subPlotAreaWidth, subPlotAreaHeight);

            // calculate sub-plot max axis width and height if needed
            if (type != OVERLAID) {
                Rectangle2D tempArea = adjustForInsets(subPlotArea[i], plot.getInsets());
                if (type == VERTICAL) {
                    maxAxisWidth = Math.max(maxAxisWidth,
                                           plot.getVerticalAxis().reserveWidth(g2, plot, tempArea));
                } else if (type == HORIZONTAL) {
                    maxAxisHeight = Math.max(maxAxisHeight,
                                        plot.getHorizontalAxis().reserveHeight(g2, plot, tempArea));
                }
            }

            // calculat next (x, y)
            if (type == VERTICAL) {
                y += subPlotAreaHeight + verticalGap;
            } else if (type == HORIZONTAL) {
                x += subPlotAreaWidth + horizontalGap;
            } if (type == OVERLAID && i == 0) {
                x += vAxisAreaWidth;
            }
        }

        // set the width and height of non-shared axis of all sub-plots
        if (type == VERTICAL) {
            setVerticalAxisWidth(maxAxisWidth);
        } else if (type == HORIZONTAL) {
            setHorizontalAxisHeight(maxAxisHeight);
        }

        // draw all the charts
        for (int i=0; i<n; i++) {
            ChartInfo chartInfo = (ChartInfo)charts.get(i);
            chartInfo.chart.draw(g2, subPlotArea[i], null);
        }

    }

    /**
     * Utility method to adjust a Rectangle2D for Insets
     */
    protected Rectangle2D adjustForInsets(Rectangle2D drawArea, Insets insets) {

        if (insets != null) {
            return new Rectangle2D.Double(drawArea.getX()+insets.left,
                                          drawArea.getY()+insets.top,
                                          drawArea.getWidth()-insets.left-insets.right,
                                          drawArea.getHeight()-insets.top-insets.bottom);
        } else {
            return drawArea;
        }
    }

    /**
     * Sets the height of the non-shared horizontal axis of all combined sub-plots
     * @param height Height to
     */
    protected void setHorizontalAxisHeight(double height) {
        Iterator iter = charts.iterator();
        while (iter.hasNext()) {
            ChartInfo chartInfo = (ChartInfo)iter.next();
            Plot plot = chartInfo.plot;
            CombinableAxis axis = (CombinableAxis)plot.getHorizontalAxis();
            axis.setReserveDimension(height);
            if (plot instanceof CombinedPlot) {
                ((CombinedPlot)plot).setHorizontalAxisHeight(height);
            }
        }
    }

    /**
     * Sets the width of the vertical axis of all combined sub-charts.
     * @param width Width to set
     */
    protected void setVerticalAxisWidth(double width) {
        Iterator iter = charts.iterator();
        while (iter.hasNext()) {
            ChartInfo chartInfo = (ChartInfo)iter.next();
            Plot plot = chartInfo.plot;
            CombinableAxis axis = (CombinableAxis)plot.getVerticalAxis();
            axis.setReserveDimension(width);
            if (plot instanceof CombinedPlot) {
                ((CombinedPlot)plot).setVerticalAxisWidth(width);
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // From HorizontalValuePlot and VerticalValuePlot
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the minimum value in the domain of all the charts, since this is plotted
     * against the horizontal axis for a combined plot.
     * @return The minimum value to be plotted against the horizontal axis.
     */
    public Number getMinimumHorizontalDataValue() {
        if (charts.size() == 0) {
            return null;
        }
        Number min = new Double(Double.MAX_VALUE);
        Iterator iter = charts.iterator();
        while (iter.hasNext()) {
            ChartInfo chartInfo = (ChartInfo)iter.next();
            HorizontalValuePlot plot = (HorizontalValuePlot)chartInfo.plot;
            Number x = plot.getMinimumHorizontalDataValue();
            if (x.doubleValue() < min.doubleValue()) {
                min = x;
            }
        }
        return min;
    }

    /**
     * Returns the maximum value in the domain of all the charts, since this is plotted
     * against the horizontal axis for a combined plot.
     * @return The maximum value to be plotted against the horizontal axis.
     */
    public Number getMaximumHorizontalDataValue() {
        if (charts.size() == 0) {
            return null;
        }
        Number max = new Double(Double.MIN_VALUE);
        Iterator iter = charts.iterator();
        while (iter.hasNext()) {
            ChartInfo chartInfo = (ChartInfo)iter.next();
            HorizontalValuePlot plot = (HorizontalValuePlot)chartInfo.plot;
            Number x = plot.getMaximumHorizontalDataValue();
            if (x.doubleValue() > max.doubleValue()) {
                max = x;
            }
        }
        return max;
    }

    /**
     * Returns the minimum value displayed against the vertical axis.
     */
    public Number getMinimumVerticalDataValue() {
        if (charts.size() == 0) {
            return null;
        }
        Number min = new Double(Double.MAX_VALUE);
        Iterator iter = charts.iterator();
        while (iter.hasNext()) {
            ChartInfo chartInfo = (ChartInfo)iter.next();
            VerticalValuePlot plot = (VerticalValuePlot)chartInfo.plot;
            Number x = plot.getMinimumVerticalDataValue();
            if (x.doubleValue() < min.doubleValue()) {
                min = x;
            }
        }
        return min;
    }

    /**
     * Returns the maximum value displayed against the vertical axis.
     */
    public Number getMaximumVerticalDataValue() {
        if (charts.size() == 0) {
            return null;
        }
        Number max = new Double(Double.MIN_VALUE);
        Iterator iter = charts.iterator();
        while (iter.hasNext()) {
            ChartInfo chartInfo = (ChartInfo)iter.next();
            VerticalValuePlot plot = (VerticalValuePlot)chartInfo.plot;
            Number x = plot.getMaximumVerticalDataValue();
            if (x.doubleValue() > max.doubleValue()) {
                max = x;
            }
        }
        return max;
    }

    //////////////////////////////////////////////////////////////////////////////
    // New public methods
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Adjusts the charts to combine changing their axis and rescaling them to
     * take into account combined plots. Should be called after adding all
     * sub-charts to the CombinedPlot.
     */
    public void adjustPlots() {
        // adjust plot axes: wrap with CombinedXXXXXAxis/OverlaidXXXXAxis objects
        adjustPlotsAxis();

        // adjust plot axis min and max values to display all plots
        adjustPlotsMinMax();

        // adjust all paints and strokes for each plot
        adjustSeriesPaintAndStroke();
    }

    /**
     * Returns a List of all our Horizontal Axes. Does not traverse recursivelly the
     * sub-chart structure.
     */
    public java.util.List getHorizontalAxes() {
        return getHorizontalAxes(false);
    }

    /**
     * Returns a List of all our Horizontal Axes.
     * @param recursive true indicates to traverse all included sub-plots recursivelly.
     */
    public java.util.List getHorizontalAxes(boolean recursive) {
        ArrayList axes = new ArrayList();
        Iterator iter = charts.iterator();
        while (iter.hasNext()) {
            ChartInfo chartInfo = (ChartInfo)iter.next();
            axes.add(chartInfo.plot.getHorizontalAxis());
            if (recursive && chartInfo.plot instanceof CombinedPlot) {
                CombinedPlot subPlot = (CombinedPlot)chartInfo.plot;
                java.util.List moreAxes = subPlot.getHorizontalAxes(true);
                axes.addAll(moreAxes);
            }
        }
        return axes;
    }

    /**
     * Returns a List of all our Vertical Axes. Does not traverse recursivelly the
     * sub-chart structure.
     */
    public java.util.List getVerticalAxes() {
        return getVerticalAxes(false);
    }

    /**
     * Returns a List of all our Vertical Axes.
     * @param recursive true indicates to traverse all included sub-plots recursivelly.
     */
    public java.util.List getVerticalAxes(boolean recursive) {
        ArrayList axes = new ArrayList();
        Iterator iter = charts.iterator();
        while (iter.hasNext()) {
            ChartInfo chartInfo = (ChartInfo)iter.next();
            axes.add(chartInfo.plot.getVerticalAxis());
            if (recursive && chartInfo.plot instanceof CombinedPlot) {
                CombinedPlot subPlot = (CombinedPlot)chartInfo.plot;
                java.util.List moreAxes = subPlot.getVerticalAxes(true);
                axes.addAll(moreAxes);
            }
        }
        return axes;
    }

    /**
     * Returns the AxisRange (min/max) of the axes list.
     * @param axes List of axes to use in calculation
     */
    public AxisRange getRange(java.util.List axes) {
        AxisRange range = null;
        Iterator iter = axes.iterator();
        while (iter.hasNext()) {
            CombinableAxis axis = (CombinableAxis)iter.next();
            if (range == null) {
                range = axis.getRange();
            } else {
                range.combine(axis.getRange());
            }
        }
        return range;
    }

    /**
     * Sets the AxisRange (min/max) for all the axes in the List.
     * @param range Range to set.
     * @parem axes List of axes to set.
     */
    public void setRange(AxisRange range, java.util.List axes) {
        Iterator iter = axes.iterator();
        while (iter.hasNext()) {
            CombinableAxis axis = (CombinableAxis)iter.next();
            axis.setRange(range);
        }
    }

    /**
     * Returns the last plotArea calculated.
     * // TODO: is this useful?
     */
    public Rectangle2D getPlotArea() {
        return plotArea;
    }

    //////////////////////////////////////////////////////////////////////////////
    // Event handling
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Receives notification of a change to a (the) chart.
     * <P>
     * Reacts to dataset changes by reconfiguring the axes.
     * @param event Information about the chart change event.
     */
    public void chartChanged(ChartChangeEvent event) {
        readjustPlotsMinMax(axisRangeSet[HORIZONTAL], axisRangeSet[VERTICAL]);
    }

    /** Flag to prevent StackOverflow while we complete the readjustment. */
    private boolean inReadjustPlotsMinMax = false;

    /**
     * Readjust the plot axes min and max as needed.  After readjusting the plot axes,
     * it will readjust recursively contained combined plots that were missed.
     *
     * @param doHorizontalAxis Hint that we need to adjust the horizontal axis.
     *        In order to adjust the axis, in addition to the hint being true, the
     *        axisRangeSet[HORIZONTAL] flag for this plot must be true indicating that the
     *        plot does indeed adjust it's horizontal axis.
     * @param doVerticalAxis Hint that we need to adjust the vertical axis.
     *        In order to adjust the axis, in addition to the hint being true, the
     *        axisRangeSet[VERTICAL] flag for this plot must be true indicating that the
     *        plot does indeed adjust it's vertical axis.
     *
     */
     private void readjustPlotsMinMax(boolean doHorizontalAxis, boolean doVerticalAxis) {

        if (inReadjustPlotsMinMax) return;

        doHorizontalAxis &= axisRangeSet[HORIZONTAL];
        doVerticalAxis &= axisRangeSet[VERTICAL];


        // adjust plot axis min and max values to display all plots if needed
        if (doHorizontalAxis || doVerticalAxis) {
            if (doHorizontalAxis) {
                setAxisRangeSet(HORIZONTAL, false);
            }
            if (doVerticalAxis) {
                setAxisRangeSet(VERTICAL, false);
            }
            adjustPlotsMinMax(doHorizontalAxis, doVerticalAxis);
        }

        // recursively readjust any internal CombinedPlots we missed.
        inReadjustPlotsMinMax = true;  // prevents StackOverflow
        Iterator iter = charts.iterator();
        while(iter.hasNext()) {
            ChartInfo chartInfo = (ChartInfo)iter.next();
            Plot plot = chartInfo.plot;
            if (plot instanceof CombinedPlot) {
                ((CombinedPlot)plot).readjustPlotsMinMax(!doHorizontalAxis, !doVerticalAxis);
            }
        }
        inReadjustPlotsMinMax = false;

    }

    //////////////////////////////////////////////////////////////////////////////
    // private/protected methods
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Adjusts the internal charts to allow automatic rendering of their conponents.
     * This includes setting all HorizontalAxis to a CombinedHorizontalXXXXAxis or
     * OverlaidHorizontalXXXXAxis, and setting all VerticalAxis to a CombinedVerticalXXXXAxis or
     * OverlaidVerticalXXXXAxis.
     */
    protected void adjustPlotsAxis() {
        if (!isAdjusted) {
            adjustPlotsAxis(true, true);
        }
    }

    /**
     * Adjusts the internal charts to allow automatic rendering of their conponents.
     * This includes setting all HorizontalAxis to a CombinedHorizontalXXXXAxis or
     * OverlaidHorizontalXXXXAxis, and setting all VerticalAxis to a CombinedVerticalXXXXAxis or
     * OverlaidVerticalXXXXAxis.
     *
     * @param hVisible Indicates if the horizontal axis is visible for this plot
     * @parem vVisible Indicates if the vertical axis is visible for this plot
     */
    protected void adjustPlotsAxis(boolean hVisible, boolean vVisible) {

        Iterator iter = charts.iterator();
        boolean hAxisVisible, vAxisVisible;
        boolean first = true;
        while (iter.hasNext()) {
            ChartInfo chartInfo = (ChartInfo)iter.next();
            Plot plot = chartInfo.plot;

            if (plot instanceof CombinedPlot) {
                // adjust recursivelly any combined plots first
                hAxisVisible = (hVisible && horizontalAxisVisible(first, !iter.hasNext(), plot));
                vAxisVisible = (vVisible && verticalAxisVisible(first, !iter.hasNext(), plot));
                ((CombinedPlot)plot).adjustPlotsAxis(hAxisVisible, vAxisVisible);
            }

            // create combined horizontal axis
            hAxisVisible = (hVisible && horizontalAxisVisible(first, !iter.hasNext(), plot));
            Axis h = createCombinedAxis(plot, (Axis)plot.getHorizontalAxis(), hAxisVisible);

            // create combined vertical axis
            vAxisVisible = (vVisible && verticalAxisVisible(first, !iter.hasNext(), plot));
            Axis v = createCombinedAxis(plot, (Axis)plot.getVerticalAxis(), vAxisVisible);

            // adjust plot with new axes
            plot.setHorizontalAxis(h);
            plot.setVerticalAxis(v);

            first = false;
        }

        isAdjusted = true;
    }

    /**
     * Returns true if a horizontal axis is visible. Sub-classes can overwrite this
     * method to implement additional logic. This implementation returns true for
     * HORIZONTAL plots or if the plot under analysis is the first of the combination.
     *
     * @param firstPlot Are we the first plot of the CombinedPlot?
     * @param lastPlot Are we the last plot of the CombinedPlot?
     * @param subPlot Plot under analysis.
     */
    protected boolean horizontalAxisVisible(boolean firstPlot, boolean lastPlot, Plot subPlot) {
        if (type == HORIZONTAL) {
            return true;
        } else if (lastPlot) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true is a vertical axis is visible. Sub-classes can overwrite this
     * method to implement additional logic. This implementation returns true for
     * VERTICAL plots or if the plot under analysis is the first of the combination.
     *
     * @param firstPlot Are we the first plot of the CombinedPlot?
     * @param lastPlot Are we the last plot of the CombinedPlot?
     * @param subPlot Plot under analysis.
     */
    protected boolean verticalAxisVisible(boolean firstPlot, boolean lastPlot, Plot subPlot) {
        if (type == VERTICAL) {
            return true;
        } else if (firstPlot) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adjusts both of our axes ranges.
     */
    protected void adjustPlotsMinMax() {
        adjustPlotsMinMax((type != HORIZONTAL), (type != VERTICAL));
    }

    /**
     * Adjusts our axes ranges.
     *
     * @param adjustHorizontal Adjust the range of our horizontal axes?
     * @param adjustVertical Adjust the range of our vertical axes?
     */
    protected void adjustPlotsMinMax(boolean adjustHorizontal, boolean adjustVertical) {

        java.util.List axes;
        AxisRange range;

        if (adjustHorizontal && !axisRangeSet[HORIZONTAL]) {
            axes = getHorizontalAxes(true);
            range = getRange(axes);
            setRange(range, axes);
            setAxisRangeSet(HORIZONTAL, true);
        }

        if (adjustVertical && !axisRangeSet[VERTICAL]) {
            axes = getVerticalAxes(true);
            range = getRange(axes);
            setRange(range, axes);
            setAxisRangeSet(VERTICAL, true);
        }
    }

    /**
     * Recursively sets the axisRangeSet[type] flag to true for this and all its
     * CombinedPlot sub-plots.
     * @param type Indicates the flag to set (HORIZONTAL or VERTICAL).
     */
    private void setAxisRangeSet(int type, boolean flag) {
        axisRangeSet[type] = flag;
        Iterator iter = charts.iterator();
        while (iter.hasNext()) {
            ChartInfo chartInfo = (ChartInfo)iter.next();
            Plot plot = chartInfo.plot;
            if (plot instanceof CombinedPlot) {
                ((CombinedPlot)plot).setAxisRangeSet(type, flag);
            }
        }
    }

    /**
     * Adjusts the Stroke and Paint objects associated with each Plot's Series
     * that use the CombinedDataset so that each series is always drawn using the same
     * rendering objects, no matter on what plot it appears.
     */
    private void adjustSeriesPaintAndStroke() {
        if (chart != null) {
            SeriesDataset masterData = (SeriesDataset)chart.getDataset();
            adjustSeriesPaintAndStroke(this, masterData);
        }
    }

    /**
     * Adjusts the Stroke and Paint objects associated with each Plot's Series
     * that use the CombinedDataset so that each series is always drawn using the same
     * rendering objects, no matter on what plot it appears.
     * @param masterPlot Outermost plot that defines all the Stroke and Paint object
     *        to use
     * @param masterData Dataset
     */
    private void adjustSeriesPaintAndStroke(Plot masterPlot, Dataset masterData) {
        Iterator iter = charts.iterator();
        while (iter.hasNext()) {
            ChartInfo chartInfo = (ChartInfo)iter.next();
            Plot plot = chartInfo.plot;
            Dataset data = chartInfo.chart.getDataset();

            if (data instanceof CombinationDataset) {
                CombinationDataset childData = (CombinationDataset)data;
                if (childData.getParent() == masterData) {
                    int[] map = childData.getMap();
                    Stroke[] childStroke = new Stroke[map.length];
                    Paint[] childPaint = new Paint[map.length];
                    for (int j=0; j<map.length; j++) {
                        childStroke[j] = masterPlot.getSeriesStroke(map[j]);
                        childPaint[j] = masterPlot.getSeriesPaint(map[j]);
                    }
                    plot.setSeriesStroke(childStroke);
                    plot.setSeriesPaint(childPaint);
                }
            }

            // adjust recursivelly any internal CombinedPlot
            if (plot instanceof CombinedPlot) {
                ((CombinedPlot)plot).adjustSeriesPaintAndStroke(masterPlot, masterData);
            }
        }
    }

    /**
     * Factory method to create a combined/overlaid horizontal/vertical number/date axis
     * depending on the paramenters.
     *
     * @param plot Source plot containing the axis
     * @param axis Source axis (HorizontalNumberAxis, HorizontalDateAxis or
     *        VerticalNumberAxis).
     * @param visible True is axis is visible on chart
     */
    protected Axis createCombinedAxis(Plot plot, Axis axis, boolean visible)
        throws AxisNotCompatibleException {

        if (axis instanceof HorizontalNumberAxis) {
            if (plot instanceof CombinedPlot) {
                axis = new OverlaidHorizontalNumberAxis((CombinedPlot)plot);
            } else {
                axis = new CombinedHorizontalNumberAxis((HorizontalNumberAxis)axis, visible);
            }
        }

        else if (axis instanceof HorizontalDateAxis) {
            if (plot instanceof CombinedPlot) {
                axis = new OverlaidHorizontalDateAxis((CombinedPlot)plot);
            } else {
                axis = new CombinedHorizontalDateAxis((HorizontalDateAxis)axis, visible);
            }
        }

        else if (axis instanceof VerticalNumberAxis) {
            if (plot instanceof CombinedPlot) {
                axis = new OverlaidVerticalNumberAxis((CombinedPlot)plot);
            } else {
                axis = new CombinedVerticalNumberAxis((VerticalNumberAxis)axis, visible);
            }
        }

        else {
            throw new AxisNotCompatibleException("Invalid axis type: " + axis.getClass());
        }

        return axis;
    }

    //////////////////////////////////////////////////////////////////////////////
    // Internal classes
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Internal class to store a chart, plot and it's vertical weight (used only
     * for HORIZONTAL or VERTICAL combined plots). The vertical weight is used to
     * determine the amount of space to assign to this chart relative to the rest.
     * If all charts have the same weight, then they they will be presented the
     * same size.
     */
    class ChartInfo {
        public JFreeChart chart;
        public Plot plot;
        public int weight;

        ChartInfo(JFreeChart chart, int weight) {
            this.chart = chart;
            this.plot = chart.getPlot();
            this.weight = weight;
        }
    }

}