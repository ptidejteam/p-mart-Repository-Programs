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
 * ---------------------------
 * HorizontalCategoryPlot.java
 * ---------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: HorizontalCategoryPlot.java,v 1.1 2007/10/10 18:59:09 vauchers Exp $
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
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.Iterator;
import com.jrefinery.data.Dataset;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.DatasetUtilities;
import com.jrefinery.chart.event.*;
import com.jrefinery.chart.tooltips.*;

/**
 * A Plot that displays data in the form of a bar chart, using data from any class that
 * implements the CategoryDataset interface.
 * @see Plot
 */
public class HorizontalCategoryPlot extends CategoryPlot implements HorizontalValuePlot {

    /**
     * Constructs a horizontal bar plot.
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     */
    public HorizontalCategoryPlot(Axis horizontalAxis, Axis verticalAxis,
                                  CategoryItemRenderer renderer) {

        this(horizontalAxis,
             verticalAxis,
             renderer,
             Plot.DEFAULT_INSETS,
             Plot.DEFAULT_BACKGROUND_PAINT,
             null, // background image
             Plot.DEFAULT_BACKGROUND_ALPHA,
             Plot.DEFAULT_OUTLINE_STROKE,
             Plot.DEFAULT_OUTLINE_PAINT,
             Plot.DEFAULT_FOREGROUND_ALPHA,
             CategoryPlot.DEFAULT_INTRO_GAP_PERCENT,
             CategoryPlot.DEFAULT_TRAIL_GAP_PERCENT,
             CategoryPlot.DEFAULT_CATEGORY_GAPS_PERCENT,
             CategoryPlot.DEFAULT_ITEM_GAPS_PERCENT,
             null);

    }

    /**
     * Constructs a horizontal bar plot.
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     * @param insets The amount of space to leave blank around the edges of the plot.
     * @param backgroundPaint An optional color for the plot's background.
     * @param backgroundImage An optional image for the plot's background.
     * @param backgroundAlpha Alpha-transparency for the plot's background.
     * @param outlineStroke The Stroke used to draw an outline around the plot.
     * @param outlinePaint The color used to draw an outline around the plot.
     * @param alpha The alpha-transparency for the plot.
     * @param introGapPercent The gap before the first bar in the plot.
     * @param trailGapPercent The gap after the last bar in the plot.
     * @param categoryGapPercent The gap between the last bar in one category and the first bar in
     *                           the next category.
     * @param itemGapPercent The gap between bars within the same category.
     * @param toolTipGenerator The tooltip generator (null permitted).
     * @param renderer The renderer for the plot.
     */
    public HorizontalCategoryPlot(Axis horizontalAxis, Axis verticalAxis,
                                  CategoryItemRenderer renderer,
                                  Insets insets,
                                  Paint backgroundPaint,
                                  Image backgroundImage, float backgroundAlpha,
                                  Stroke outlineStroke, Paint outlinePaint,
                                  float alpha,
			          double introGapPercent, double trailGapPercent,
                                  double categoryGapPercent, double itemGapPercent,
                                  CategoryToolTipGenerator toolTipGenerator) {

	super(horizontalAxis, verticalAxis, renderer,
              insets,
              backgroundPaint, backgroundImage, backgroundAlpha,
              outlineStroke, outlinePaint, alpha,
              introGapPercent, trailGapPercent, categoryGapPercent, itemGapPercent,
              toolTipGenerator);

    }

    /**
     * A convenience method that returns the dataset for the plot, cast as a CategoryDataset.
     */
    public CategoryDataset getDataset() {

        CategoryDataset result = null;

        if (this.chart!=null) {
            result = (CategoryDataset)chart.getDataset();
        }

        return result;

    }

    /**
     * Sets the horizontal axis for the plot.  This method should throw an exception if the axis
     * doesn't implement the required interfaces.
     * @param axis The new horizontal axis.
     */
    public void setHorizontalAxis(Axis axis) throws AxisNotCompatibleException {
	// check that the axis implements the required interface (if not raise an exception);
	super.setHorizontalAxis(axis);
    }

    /**
     * Sets the vertical axis for the plot.  This method should throw an exception if the axis
     * doesn't implement the required interfaces.
     * @param axis The new vertical axis.
     */
    public void setVerticalAxis(Axis axis) throws AxisNotCompatibleException {
	// check that the axis implements the required interface (if not raise an exception);
	super.setVerticalAxis(axis);
    }

    /**
     * Returns the axis against which domain values are plotted.
     * <P>
     * For the HorizontalBarPlot class, the domain is a set of categories plotted against a
     * CategoryAxis.
     */
    public CategoryAxis getDomainAxis() {
	return (CategoryAxis)verticalAxis;
    }

    /**
     * Returns the axis against which range values are plotted.
     * <P>
     * For the HorizontalBarPlot class, the range is numerical data plotted against a ValueAxis.
     */
    public ValueAxis getRangeAxis() {
	return (ValueAxis)horizontalAxis;
    }

//    /**
//     * A convenience method that returns a list of the categories in the data source.
//     */
//    public List getCategories() {
//	return getDataset().getCategories();
//    }

    /**
     * Returns the x-coordinate (in Java 2D User Space) of the center of the specified category.
     * @param category The index of the category of interest (first category index = 0).
     * @param area The region within which the plot will be drawn.
     */
    public double getCategoryCoordinate(int category, Rectangle2D area) {

        // calculate first part of result...
        double result = area.getY()+(area.getHeight()*introGapPercent);


        // then add some depending on how many categories...
        int categoryCount = getDataset().getCategoryCount();
        if (categoryCount>1) {

	    double categorySpan = area.getHeight()
                                  *(1-introGapPercent-trailGapPercent-categoryGapsPercent);
            double categoryGapSpan = area.getHeight()*categoryGapsPercent;
            result = result
                     + (category+0.5)*(categorySpan/categoryCount)
                     + (category)*(categoryGapSpan/(categoryCount-1));

        }
        else {
            result = result
                     + (category+0.5)*area.getHeight()*(1-introGapPercent-trailGapPercent);
        }

        return result;

    }

    /**
     * Checks the compatibility of a horizontal axis, returning true if the axis is compatible with
     * the plot, and false otherwise.
     * @param axis The horizontal axis;
     */
    public boolean isCompatibleHorizontalAxis(Axis axis) {
	if (axis instanceof HorizontalNumberAxis) {
	    return true;
	}
	else return false;
    }

    /**
     * Checks the compatibility of a vertical axis, returning true if the axis is compatible with
     * the plot, and false otherwise.
     * @param axis The vertical axis;
     */
    public boolean isCompatibleVerticalAxis(Axis axis) {
	if (axis instanceof VerticalCategoryAxis) {
	    return true;
	}
	else return false;
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     * <P>
     * At your option, you may supply an instance of DrawInfo.  If you do, it will be populated
     * with information about the drawing, including various plot dimensions and tooltip info.
     * @param g2 The graphics device.
     * @param plotArea The area within which the plot should be drawn.
     * @param info A structure for passing back information about the chart drawing (ignored if
     *             null).
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, DrawInfo info) {

        // set up collection of drawing info...
        ToolTipsCollection tooltips = null;
        if (info!=null) {
            info.setPlotArea(plotArea);
            tooltips = info.getToolTipsCollection();
        }

        // adjust the drawing area for the plot insets (if any)...
	if (insets!=null) {
	    plotArea.setRect(plotArea.getX()+insets.left,
			     plotArea.getY()+insets.top,
                             plotArea.getWidth()-insets.left-insets.right,
			     plotArea.getHeight()-insets.top-insets.bottom);
	}

	// estimate the area required for drawing the axes...
	VerticalAxis vAxis = getVerticalAxis();
	HorizontalAxis hAxis = getHorizontalAxis();
	double vAxisAreaWidth = vAxis.reserveWidth(g2, this, plotArea);
	Rectangle2D hAxisArea = hAxis.reserveAxisArea(g2, this, plotArea, vAxisAreaWidth);

	// and this the area available for plotting data...
	Rectangle2D dataArea = new Rectangle2D.Double(plotArea.getX()+vAxisAreaWidth,
						      plotArea.getY(),
						      plotArea.getWidth()-vAxisAreaWidth,
						      plotArea.getHeight()-hAxisArea.getHeight());

        if (info!=null) {
            info.setDataArea(dataArea);
        }

        // draw the background and axes...
	drawOutlineAndBackground(g2, dataArea);
	getDomainAxis().draw(g2, plotArea, dataArea);
	getRangeAxis().draw(g2, plotArea, dataArea);

        // now get the data and plot the bars...
        CategoryDataset data = this.getDataset();
        if (data!=null) {

            Shape savedClip = g2.getClip();
            g2.clip(dataArea);

            // set up the alpha-transparency...
            Composite originalComposite = g2.getComposite();
            Composite newComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.foregroundAlpha);
            g2.setComposite(newComposite);

            // work out the span dimensions for the categories...
            int seriesCount = data.getSeriesCount();
            //int categoryCount = data.getCategoryCount();
            //int barCount = renderer.barWidthsPerCategory(data);

            this.renderer.initialise(g2, dataArea, this, data);

            // iterate through the categories...
            int categoryIndex = 0;
            Object previousCategory = null;
            Iterator iterator = data.getCategories().iterator();
            while (iterator.hasNext()) {

                Object category = iterator.next();

                // loop through the series...
                for (int series=0; series<seriesCount; series++) {

                    // draw the data item...
                    Shape region = renderer.drawCategoryItem(g2, dataArea,
                                                             this,
                                                             this.getRangeAxis(),
                                                             data, series,
                                                             category, categoryIndex,
                                                             previousCategory);

                    // collect optional tooltip information...
                    if (tooltips!=null) {
                        if (this.toolTipGenerator==null) {
                            toolTipGenerator = new StandardCategoryToolTipGenerator();
                        }
                        String tip = this.toolTipGenerator.generateToolTip(data, series, category);
                        if (region!=null) {
                            tooltips.addToolTip(tip, region);
                        }
                    }
                }

                categoryIndex++;
                previousCategory = category;

            }

            // draw a line at zero...
            double translatedZero = getRangeAxis().translateValueToJava2D(0.0, dataArea);
            Line2D baseline = new Line2D.Double(translatedZero, dataArea.getY(), translatedZero, dataArea.getMaxY());
            g2.setStroke(new BasicStroke());
            g2.draw(baseline);

            // draw vertical crosshair if required...
            ValueAxis hva = this.getRangeAxis();
            if (hva.isCrosshairVisible()) {
                this.drawVerticalLine(g2, dataArea, hva.getCrosshairValue(),
                                      hva.getCrosshairStroke(),
                                      hva.getCrosshairPaint());
            }

            g2.setClip(savedClip);
            g2.setComposite(originalComposite);
        }

    }

    /**
     * Returns the width of each bar in the chart.
     * @param plotArea The area within which the plot will be drawn.
     */
//    double calculateBarWidth(Rectangle2D plotArea) {

//	CategoryDataset data = getDataset();

	// series, category and bar counts
//	int categoryCount = data.getCategoryCount();
//	int seriesCount = data.getSeriesCount();
//	int barCount = renderer.barWidthsPerCategory(data)*categoryCount;

	// calculate the plot height (bars are horizontal) less whitespace
//	double usable = plotArea.getHeight() *
  //                                  (1.0 - introGapPercent - trailGapPercent - categoryGapsPercent);

    //    if (renderer.barWidthsPerCategory(data)>1) {
//	    usable = usable - ((seriesCount-1) * categoryCount * seriesGap);
   //     }

	// and thus the width of the bars
//	return usable/barCount;
 //   }

    /**
     * Returns a short string describing the type of plot.
     */
    public String getPlotType() {
	return "Horizontal Bar Plot";
    }

    /**
     * Returns the minimum value in the range (since the range values are plotted against the
     * horizontal axis by this plot).
     * <P>
     * This method will return null if the dataset is null.
     *
     * @return The minimum value.
     */
    public Number getMinimumHorizontalDataValue() {

        Number result = null;

	CategoryDataset data = this.getDataset();
	if (data!=null) {
            if (this.renderer.isStacked()) {
                result = DatasetUtilities.getMinimumStackedRangeValue(data);
            }
            else {
                result = DatasetUtilities.getMinimumRangeValue(data);
            }
        }

        return result;

    }

    /**
     * Returns the maximum value in the range (since the range values are plotted against the
     * horizontal axis by this plot).
     * <P>
     * This method will return null if the dataset is null.
     *
     * @return The maximum value.
     */
    public Number getMaximumHorizontalDataValue() {

        Number result = null;

	CategoryDataset data = this.getDataset();
	if (data!=null) {
            if (this.renderer.isStacked()) {
                result = DatasetUtilities.getMaximumStackedRangeValue(data);
            }
            else {
                result = DatasetUtilities.getMaximumRangeValue(data);
            }
	}

        return result;

    }

    /**
     * Handles a 'click' on the plot by updating the anchor values...
     */
    public void handleClick(int x, int y, DrawInfo info) {

        // set the anchor value for the horizontal axis...
        ValueAxis hva = this.getRangeAxis();
        double hvalue = hva.translateJava2DtoValue((float)x, info.getDataArea());
        hva.setAnchorValue(hvalue);
        hva.setCrosshairValue(hvalue);

    }

    /**
     * Utility method for drawing a crosshair on the chart (if required).
     */
    private void drawVerticalLine(Graphics2D g2, Rectangle2D dataArea, double value,
                                  Stroke stroke, Paint paint) {

        double xx = this.getRangeAxis().translateValueToJava2D(value, dataArea);
        Line2D line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);

    }

}
