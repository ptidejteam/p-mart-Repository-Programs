/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
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
 * -------------------
 * CombinedXYPlot.java
 * -------------------
 * (C) Copyright 2001, 2002, by Bill Kelemen and Contributors.
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *                   Anthony Boulestreau;

 * $Id: CombinedXYPlot.java,v 1.1 2007/10/10 19:02:26 vauchers Exp $
 *
 * Changes:
 * --------
 * 06-Dec-2001 : Version 1 (BK);
 * 12-Dec-2001 : Removed unnecessary 'throws' clause from constructor (DG);
 * 18-Dec-2001 : Added plotArea attribute and get/set methods (BK);
 * 22-Dec-2001 : Fixed bug in chartChanged with multiple combinations of CombinedPlots (BK);
 * 08-Jan-2002 : Moved to new package com.jrefinery.chart.combination (DG);
 * 25-Feb-2002 : Updated import statements (DG);
 * 28-Feb-2002 : Readded "this.plotArea = plotArea" that was deleted from draw() method (BK);
 * 26-Mar-2002 : Added an empty zoom method (this method needs to be written so that combined
 *               plots will support zooming (DG);
 * 29-Mar-2002 : Changed the method createCombinedAxis adding the creation of OverlaidSymbolicAxis
 *               and CombinedSymbolicAxis(AB);
 * 23-Apr-2002 : Renamed CombinedPlot-->MultiXYPlot, and simplified the structure (DG);
 * 23-May-2002 : Renamed (again) MultiXYPlot-->CombinedXYPlot (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Iterator;
import com.jrefinery.data.Range;
import com.jrefinery.data.Dataset;
import com.jrefinery.data.SeriesDataset;
import com.jrefinery.data.CombinationDataset;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.Plot;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.HorizontalAxis;
import com.jrefinery.chart.VerticalAxis;
import com.jrefinery.chart.HorizontalValuePlot;
import com.jrefinery.chart.VerticalValuePlot;
import com.jrefinery.chart.Axis;
import com.jrefinery.chart.ValueAxis;
import com.jrefinery.chart.HorizontalDateAxis;
import com.jrefinery.chart.HorizontalNumberAxis;
import com.jrefinery.chart.VerticalNumberAxis;
import com.jrefinery.chart.VerticalSymbolicAxis;
import com.jrefinery.chart.HorizontalSymbolicAxis;
import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.AxisNotCompatibleException;
import com.jrefinery.chart.event.ChartChangeEvent;

/**
 * An extension of XYPlot that can contain multiple subplots, laid out horizontally or vertically.
 * <P>
 * This class was originally written by Bill Kelemen, and has since been modified extensively by
 * David Gilbert.
 *
 * @author Bill Kelemen (bill@kelemen-usa.com).
 *
 */
public class CombinedXYPlot extends XYPlot {

    /** Constant used to indicate horizontal layout. */
    public static final int HORIZONTAL = 0;

    /** Constant used to indicate vertical layout. */
    public static final int VERTICAL = 1;

    /** The layout type (HORIZONTAL or VERTICAL). */
    private int type;

    /** Storage for the subplot references. */
    protected List subplots;

    /** The total number of series in all subplots. */
    protected int seriesCount = 0;

    /** Total weight of all charts. */
    protected int totalWeight = 0;

    /** The gap between subplots. */
    protected double gap = 5.0;

    /**
     * Creates a new MultiXYPlot.
     * <P>
     * If the layout type is HORIZONTAL, you need to supply a vertical axis to be shared by the
     * subplots.  If the layout type is VERTICAL, you need to supply a horizontal axis to be shared
     * by the subplots.
     *
     * @param axis The shared axis.
     * @param type The layout type (HORIZONTAL or VERTICAL).
     */
    public CombinedXYPlot(ValueAxis axis, int type) {

        super(null, // no data in the parent plot
             (type == VERTICAL ? axis : null),
             (type == HORIZONTAL ? axis : null));

        if (type!=HORIZONTAL && type!=VERTICAL) {
            throw new IllegalArgumentException("Invalid type (" + type + ")");
        }

        this.type = type;
        this.subplots = new java.util.ArrayList();

    }

    /**
     * Adds a subplot with a particular weight (greater than or equal to one).  The weight
     * determines how much space is allocated to the subplot relative to all the other subplots.
     * <P>
     * The subplot should have a null horizontal axis (for VERTICAL layout) or a
     * null vertical axis (for HORIZONTAL layout).
     *
     * @param subplot The subplot.
     * @param weight The weight.
     */
    public void add(XYPlot subplot, int weight) throws AxisNotCompatibleException,
                                                       IllegalArgumentException {

        if (!isValidSubHorizontalAxis((Axis)subplot.getHorizontalAxis())) {
            throw new AxisNotCompatibleException("MultiXYPlot.add(...): invalid horizontal axis.");
        }
        else if (!isValidSubVerticalAxis((Axis)subplot.getVerticalAxis())) {
            throw new AxisNotCompatibleException("MultiXYPlot.add(...): invalid vertical axis.");
        }

        // verify valid weight
        if (weight<=0) {
            throw new IllegalArgumentException("MultiXYPlot.add(...): weight must be positive.");
        }

        // store the plot and its weight
        subplot.setParent(this);
        subplot.setWeight(weight);
        subplot.setInsets(new Insets(0, 0, 0, 0));
        subplot.setFirstSeriesIndex(seriesCount);
        seriesCount = seriesCount + subplot.getSeriesCount();
        if (type==VERTICAL) subplot.setDomainAxis(null);
        if (type==HORIZONTAL) subplot.setRangeAxis(null);
        subplots.add(subplot);

        // keep track of total weights
        totalWeight += weight;

        if (type==HORIZONTAL) getRangeAxis().configure();
        if (type==VERTICAL) getDomainAxis().configure();

    }

    /**
     * Checks that the horizontal axis for the subplot is valid.
     * <P>
     * Note that for a VERTICAL layout, the horizontal axis must be null (since each subplot
     * shares the horizontal axis maintained by this class).
     *
     * @param axis The horizontal axis.
     */
    public boolean isValidSubHorizontalAxis(Axis axis) {

        boolean result = true;

        if (type==VERTICAL) {
            result = (axis==null);
        }

        return result;

    }

    /**
     * Checks that the vertical axis for the subplot is valid.
     * <P>
     * Note that for a HORIZONTAL layout, the vertical axis must be null (since each subplot
     * shares the vertical axis maintained by this class).
     *
     * @param axis The vertical axis.
     */
    public boolean isValidSubVerticalAxis(Axis axis) {

        boolean result = true;

        if (type==HORIZONTAL) {
            result = (axis==null);
        }

        return result;

    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     * Will perform all the placement calculations for each sub-plots and then tell these to draw
     * themselves.
     * <P>
     * @param g2 The graphics device.
     * @param plotArea The area within which the plot (including axis labels) should be drawn.
     * @param info Information about the drawing.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {

        // set up info collection...
        if (info!=null) {
            info.setPlotArea(plotArea);

        }

        // adjust the drawing area for plot insets (if any)...
        if (insets!=null) {
            plotArea.setRect(plotArea.getX()+insets.left,
                             plotArea.getY()+insets.top,
                             plotArea.getWidth()-insets.left-insets.right,
                             plotArea.getHeight()-insets.top-insets.bottom);
        }

        // reserve the height or width of the shared axis...
        double sharedAxisDimension = 0;
        if (type==CombinedXYPlot.VERTICAL) {
            HorizontalAxis hAxis = getHorizontalAxis();
            if (hAxis!=null) {
                sharedAxisDimension = hAxis.reserveHeight(g2, this, plotArea);
            }
        }
        else { // VERTICAL
            VerticalAxis vAxis = getVerticalAxis();
            if (vAxis!=null) {
                sharedAxisDimension = vAxis.reserveWidth(g2, this, plotArea);
            }
        }

        Rectangle2D dataArea;
        if (type==HORIZONTAL) {
            dataArea = new Rectangle2D.Double(plotArea.getX()+sharedAxisDimension, plotArea.getY(),
                                              plotArea.getWidth()-sharedAxisDimension,
                                              plotArea.getHeight());
        }
        else {
            dataArea = new Rectangle2D.Double(plotArea.getX(), plotArea.getY(),
                                              plotArea.getWidth(),
                                              plotArea.getHeight()-sharedAxisDimension);
        }

        // work out the maximum height or width of the non-shared axes...
        int n = subplots.size();

        // calculate plotAreas of all sub-plots, maximum vertical/horizontal axis width/height
        Rectangle2D[] subPlotArea = new Rectangle2D[n];
        double x = dataArea.getX();
        double y = dataArea.getY();
        double usableWidth = dataArea.getWidth();
        if (type==HORIZONTAL) {
            usableWidth = usableWidth-gap*(n-1);
        }
        double usableHeight = dataArea.getHeight();
        if (type==VERTICAL) {
            usableHeight=usableHeight-gap*(n-1);
        }
        double maxAxisWidth = Double.MIN_VALUE;
        double maxAxisHeight = Double.MIN_VALUE;
        for (int i=0; i<n; i++) {
            XYPlot plot = (XYPlot)subplots.get(i);

            // calculate sub-plot height
            double subPlotAreaHeight = usableHeight;
            if (type == VERTICAL) {
                subPlotAreaHeight *= (double)plot.getWeight()/totalWeight;
            }

            // calculate sub-plot width
            double subPlotAreaWidth = usableWidth;
            if (type == HORIZONTAL) {
                subPlotAreaWidth *= (double)plot.getWeight()/totalWeight;
            }

            // calculate sub-plot area
            subPlotArea[i] = new Rectangle2D.Double(x, y, subPlotAreaWidth, subPlotAreaHeight);

            // calculate sub-plot max axis width and height if needed
            //Rectangle2D tempArea = adjustForInsets(subPlotArea[i], plot.getInsets());
            if (type == VERTICAL) {
                maxAxisWidth = Math.max(maxAxisWidth,
                                        plot.getVerticalAxis().reserveWidth(g2, plot, subPlotArea[i]));
            }
            else if (type == HORIZONTAL) {
                maxAxisHeight = Math.max(maxAxisHeight,
                                         plot.getHorizontalAxis().reserveHeight(g2, plot, subPlotArea[i]));
            }


            // calculat next (x, y)
            if (type == VERTICAL) {
                y += subPlotAreaHeight + gap;
            }
            else if (type == HORIZONTAL) {
                x += subPlotAreaWidth + gap;
            }
        }

        // set the width and height of non-shared axis of all sub-plots
        if (type == VERTICAL) {
            setVerticalAxisWidth(maxAxisWidth);
            dataArea.setRect(dataArea.getX()+maxAxisWidth, dataArea.getY(),
                             dataArea.getWidth()-maxAxisWidth, dataArea.getHeight());
            domainAxis.draw(g2, plotArea, dataArea);
        }
        else if (type == HORIZONTAL) {
            setHorizontalAxisHeight(maxAxisHeight);
            dataArea.setRect(dataArea.getX(), dataArea.getY(),
                             dataArea.getWidth(), dataArea.getHeight()-maxAxisHeight);
            rangeAxis.draw(g2, plotArea, dataArea);
        }

        if (info!=null) {
            info.setDataArea(dataArea);
        }

        // draw all the charts
        for (int i=0; i<n; i++) {
            XYPlot plot = (XYPlot)subplots.get(i);
            plot.draw(g2, subPlotArea[i], info);
        }

    }

    /**
     * Sets the height for the horizontal axis of each subplot.
     *
     * @param height The height.
     */
    protected void setHorizontalAxisHeight(double height) {

        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot plot = (XYPlot)iterator.next();
            Axis axis = plot.getDomainAxis();
            axis.setFixedDimension(height);
        }

    }

    /**
     * Sets the width for the vertical axis of each subplot.
     *
     * @param width The width.
     */
    protected void setVerticalAxisWidth(double width) {

        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot plot = (XYPlot)iterator.next();
            Axis axis = plot.getRangeAxis();
            axis.setFixedDimension(width);
        }

    }


    /**
     * A zoom method that does nothing.  TO BE DONE.
     *
     * @param percent The zoom percentage.
     */
    public void zoom(double percent) {
    }

    /**
     * Returns an array of labels to be displayed by the legend.
     *
     * @return An array of legend item labels (or null).
     */
    public List getLegendItemLabels() {

        List result = new java.util.ArrayList();

        if (subplots!=null) {
            Iterator iterator = subplots.iterator();
            while (iterator.hasNext()) {
                XYPlot plot = (XYPlot)iterator.next();
                List more = plot.getLegendItemLabels();
                result.addAll(more);
            }
        }

        return result;

    }

    /**
     * Returns a string describing the type of plot.
     *
     * @return The type of plot.
     */
    public String getPlotType() {

        switch (type) {
          case HORIZONTAL: return "Horizontal MultiXYPlot";
          case VERTICAL:   return "Vertical MultiXYPlot";
          default:         return "Unknown";
        }

    }

    //////////////////////////////////////////////////////////////////////////////
    // From HorizontalValuePlot and VerticalValuePlot
    //////////////////////////////////////////////////////////////////////////////

    public Range getHorizontalDataRange() {

        Range result = null;

        if (type==VERTICAL) {
            if (subplots!=null) {
                Iterator iterator = subplots.iterator();
                while (iterator.hasNext()) {
                    XYPlot subplot = (XYPlot)iterator.next();
                    result = Range.combine(result, subplot.getHorizontalDataRange());
                }
            }
        }

        return result;

    }

    public Range getVerticalDataRange() {

        Range result = null;

        if (type==HORIZONTAL) {
            if (subplots!=null) {
                Iterator iterator = subplots.iterator();
                while (iterator.hasNext()) {
                    XYPlot subplot = (XYPlot)iterator.next();
                    result = Range.combine(result, subplot.getVerticalDataRange());
                }
            }
        }

        return result;

    }


}
