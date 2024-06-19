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
 * ---------------------------
 * HorizontalCategoryPlot.java
 * ---------------------------
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Jeremy Bowman;
 *
 * $Id: HorizontalCategoryPlot.java,v 1.1 2007/10/10 19:57:54 vauchers Exp $
 *
 * Changes (from 21-Jun-2001)
 * --------------------------
 * 21-Jun-2001 : Removed redundant JFreeChart parameter from constructors (DG);
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 19-Oct-2001 : Moved series paint and stroke attributes from JFreeChart.java to Plot.java (DG);
 * 22-Oct-2001 : Changed draw(...) method with introduction of HorizontalBarRenderer class (DG);
 *               Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 * 31-Oct-2001 : Debugging for gap settings (DG);
 * 20-Nov-2001 : Fixed clipping bug that shows up when chart is displayed inside JScrollPane (DG);
 * 12-Dec-2001 : Removed unnecessary 'throws' clause in constructors (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 16-Jan-2002 : Renamed the tooltips class (DG);
 * 22-Jan-2002 : Added DrawInfo class, incorporating tooltips and crosshairs (DG);
 * 06-Feb-2002 : Added optional background image and alpha-transparency to Plot and subclasses (DG);
 * 13-Feb-2002 : Renamed getCategoryAxis() --> getDomainAxis() (DG);
 * 15-Feb-2002 : Modified getMaximumVerticalDataValue() and getMinimumVerticalDataValue() to handle
 *               stacked plots (DG);
 * 28-Feb-2002 : Renamed Datasets.java --> DatasetUtilities.java (DG);
 * 13-Mar-2002 : Renamed HorizontalBarPlot.java --> HorizontalCategoryPlot.java (DG);
 * 03-Apr-2002 : Added g2.setPaint(...) in draw(...) method (DG);
 * 23-Apr-2002 : Moved dataset from JFreeChart to Plot (DG);
 * 29-Apr-2002 : Added getHorizontalAxis() method (DG);
 * 13-May-2002 : Added methods (by Jeremy Bowman) to check axis compatibility (DG);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 26-Jun-2002 : Added axis to initialise(...) method call (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 06-Nov-2002 : Changed renderer interface and replaced CategoryDataset with TableDataset (DG);
 * 19-Nov-2002 : Added axis location parameters to constructor (DG);
 * 21-Jan-2003 : Removed monolithic constructor (DG);
 *
 */

package com.jrefinery.chart.plot;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.util.Iterator;

import com.jrefinery.chart.ChartRenderingInfo;
import com.jrefinery.chart.Marker;
import com.jrefinery.chart.axis.AxisConstants;
import com.jrefinery.chart.axis.Axis;
import com.jrefinery.chart.axis.HorizontalAxis;
import com.jrefinery.chart.axis.VerticalAxis;
import com.jrefinery.chart.axis.CategoryAxis;
import com.jrefinery.chart.axis.VerticalCategoryAxis;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.axis.Tick;
import com.jrefinery.chart.renderer.CategoryItemRenderer;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.DatasetUtilities;
import com.jrefinery.data.Range;

/**
 * A general plotting class that uses data from a {@link CategoryDataset} and renders each data item
 * using a {@link CategoryItemRenderer}.  In this plot, the values are plotted along the horizontal 
 * axis and the categories are plotted along the vertical axis.  The {@link VerticalCategoryPlot} 
 * provides the reverse orientation.
 *
 * @author David Gilbert
 * 
 */
public class HorizontalCategoryPlot extends CategoryPlot implements HorizontalValuePlot {

    /**
     * Constructs a horizontal category plot.
     *
     * @param data  the dataset.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param renderer  the renderer for the data.
     */
    public HorizontalCategoryPlot(CategoryDataset data,
                                  CategoryAxis domainAxis,
                                  ValueAxis rangeAxis,
                                  CategoryItemRenderer renderer) {

        super(data, domainAxis, rangeAxis, renderer);
        setDomainAxisLocation(AxisConstants.LEFT);
        setRangeAxisLocation(AxisConstants.BOTTOM);
   
    }

    /**
     * Checks the compatibility of a horizontal axis, returning true if the
     * axis is compatible with the plot, and false otherwise.
     *
     * @param axis The proposed horizontal axis.
     *
     * @return <code>true</code> if the axis is compatible with the plot.
     */
    public boolean isCompatibleHorizontalAxis(Axis axis) {

        if ((axis instanceof HorizontalAxis) && (axis instanceof ValueAxis)) {
            return true;
        }
        else {
            return false;
        }

    }

    /**
     * Checks the compatibility of a vertical axis, returning true if the axis
     * is compatible with the plot, and false otherwise.
     *
     * @param axis  the vertical axis.
     *
     * @return <code>true</code> if the axis is compatible with the plot.
     */
    public boolean isCompatibleVerticalAxis(Axis axis) {

        if (axis instanceof VerticalCategoryAxis) {
            return true;
        }
        else {
            return false;
        }

    }

    /**
     * Checks the compatibility of a domain axis, returning true if the axis
     * is compatible with the plot, and false otherwise.
     *
     * @param axis  the proposed axis.
     *
     * @return <code>true</code> if the axis is compatible with the plot.
     */
    public boolean isCompatibleDomainAxis(CategoryAxis axis) {

        if (axis == null) {
            return true;
        }
        else {
            return isCompatibleVerticalAxis(axis);
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
        else {
            return isCompatibleHorizontalAxis(axis);
        }

    }


    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     * <P>
     * At your option, you may supply an instance of ChartRenderingInfo.
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

        // estimate the width of the vertical axis...
        double vAxisWidth = 0.0;
        VerticalAxis vAxis = (VerticalAxis) getDomainAxis();
        if (vAxis != null) {
            vAxisWidth = vAxis.reserveWidth(g2, this, plotArea, getDomainAxisLocation());
        }

        // estimate the height of the horizontal axis...
        double hAxisHeight = 0.0;
        HorizontalAxis hAxis = (HorizontalAxis) getRangeAxis();
        if (hAxis != null) {
            hAxisHeight = hAxis.reserveHeight(g2, this, plotArea,
                                              getRangeAxisLocation(),
                                              vAxisWidth, getDomainAxisLocation());
        }

        // estimate the height of the secondary range axis (if any)...
        double hAxis2Height = 0.0;
        int secondaryAxisLocation = getOppositeAxisLocation(getRangeAxisLocation());
        HorizontalAxis hAxis2 = (HorizontalAxis) getSecondaryRangeAxis();
        if (hAxis2 != null) {
            hAxis2Height = hAxis2.reserveHeight(g2, this, plotArea, secondaryAxisLocation,
                                               vAxisWidth, getDomainAxisLocation());
        }

        // and thus the area available for plotting...
        double x1 = getRectX(plotArea.getX(), vAxisWidth, 0.0, getDomainAxisLocation());
        double y1 = getRectY(plotArea.getY(), hAxisHeight, hAxis2Height, getRangeAxisLocation());
        Rectangle2D dataArea = new Rectangle2D.Double(x1, y1,
                                                plotArea.getWidth() - vAxisWidth,
                                                plotArea.getHeight() - hAxisHeight - hAxis2Height);

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

        getDomainAxis().draw(g2, plotArea, dataArea, getDomainAxisLocation());
        getRangeAxis().draw(g2, plotArea, dataArea, getRangeAxisLocation());

        Axis rangeAxis2 = getSecondaryRangeAxis();
        if (rangeAxis2 != null) {
            int l = getOppositeAxisLocation(getRangeAxisLocation());
            rangeAxis2.draw(g2, plotArea, dataArea, l);
        }

        if (renderer != null) {

            // draw the domain grid lines, if any...
            if (isDomainGridlinesVisible()) {
                Stroke gridStroke = getDomainGridlineStroke();
                Paint gridPaint = getDomainGridlinePaint();
                if ((gridStroke != null) && (gridPaint != null)) {
                    // iterate over the categories
                    CategoryDataset data = getCategoryDataset();
                    if (data != null) {
                        CategoryAxis axis = getDomainAxis();
                        int columnCount = data.getColumnCount();
                        for (int c = 0; c < columnCount; c++) {
                            double xx = axis.getCategoryMiddle(c, columnCount, dataArea);
                            renderer.drawDomainGridline(g2, this, dataArea, xx);
                        }
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
                        renderer.drawRangeGridline(g2, this, getRangeAxis(), dataArea,
                                                   tick.getNumericalValue());
                    }
                }
            }
        }

        // draw the range markers...
        if ((getRangeMarkers() != null) && (getRenderer() != null)) {
            Iterator iterator = getRangeMarkers().iterator();
            while (iterator.hasNext()) {
                Marker marker = (Marker) iterator.next();
                getRenderer().drawRangeMarker(g2, this, getRangeAxis(), marker, dataArea);
            }
        }

        // now get the data and plot the data items...
        render(g2, dataArea, info);
        render2(g2, dataArea, info);

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
        if (data != null) {

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
                                      data, 0, row, column);
                }
            }

            // draw vertical crosshair if required...
            ValueAxis hva = getRangeAxis();
            if (isRangeCrosshairVisible()) {
                drawVerticalLine(g2, dataArea, getRangeCrosshairValue(),
                                 getRangeCrosshairStroke(),
                                 getRangeCrosshairPaint());
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
        if (dataset != null) {

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
            if (renderer == null) {
                renderer = getRenderer();
            }
            renderer.initialise(g2, dataArea, this, info);

            int columnCount = dataset.getColumnCount();
            int rowCount = dataset.getRowCount();
            for (int column = 0; column < columnCount; column++) {
                for (int row = 0; row < rowCount; row++) {
                    renderer.drawItem(g2, dataArea,
                                      this,
                                      getDomainAxis(),
                                      rangeAxis,
                                      dataset, 1, row, column);
                }
            }

            g2.setClip(savedClip);
            g2.setComposite(originalComposite);

        }

    }

    /**
     * Returns a short string describing the type of plot.
     *
     * @return plot type description.
     */
    public String getPlotType() {
        return "Horizontal Category Plot";
    }

    /**
     * Returns the range of data values that will be plotted against the range axis.
     * <P>
     * If the dataset is <code>null</code>, this method returns <code>null</code>.
     *
     * @param axis  the axis.
     * 
     * @return the range.
     */
    public Range getHorizontalDataRange(ValueAxis axis) {

        Range result = null;

        CategoryDataset dataset = getCategoryDataset();
        CategoryItemRenderer renderer = getRenderer();

        if (axis.equals(getSecondaryRangeAxis())) {
            dataset = getSecondaryCategoryDataset();
            if (getSecondaryRenderer() != null) {
                renderer = getSecondaryRenderer();
            }
        }

        if ((dataset != null) && (renderer != null)) {

            switch (renderer.getRangeType()) {

                // stacked data items...
                case CategoryItemRenderer.STACKED:
                    result = DatasetUtilities.getStackedRangeExtent(dataset);
                    break;

                // regular data items...
                case CategoryItemRenderer.STANDARD:
                default:
                    result = DatasetUtilities.getRangeExtent(dataset);

            }

        }

        return result;

    }

    /**
     * Returns the horizontal axis.
     * <P>
     * This method supports the HorizontalValuePlot interface.
     *
     * @return the horizontal axis.
     */
    public ValueAxis getHorizontalValueAxis() {
        return getRangeAxis();
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

        // set the anchor value for the horizontal axis...
        ValueAxis hva = getRangeAxis();
        double hvalue = hva.translateJava2DtoValue((float) x, info.getDataArea());
        hva.setAnchorValue(hvalue);
        setRangeCrosshairValue(hvalue);

    }

    /**
     * Utility method for drawing a crosshair on the chart (if required).
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param value  the x-coordinate of the vertical crosshair line.
     * @param stroke  the stroke.
     * @param paint  the paint.
     */
    private void drawVerticalLine(Graphics2D g2,
                                  Rectangle2D dataArea, double value,
                                  Stroke stroke, Paint paint) {

        double xx = getRangeAxis().translateValueToJava2D(value, dataArea);
        Line2D line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);

    }

}
