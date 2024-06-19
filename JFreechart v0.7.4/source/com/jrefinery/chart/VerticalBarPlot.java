/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart
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
 * --------------------
 * VerticalBarPlot.java
 * --------------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Serge V. Grachov;
 *
 * $Id: VerticalBarPlot.java,v 1.1 2007/10/10 18:57:57 vauchers Exp $
 *
 * Changes (from 21-Jun-2001):
 * ---------------------------
 * 21-Jun-2001 : Removed redundant JFreeChart parameter from constructors (DG);
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 19-Oct-2001 : Moved series paint and stroke attributes from JFreeChart.java to Plot.java (DG);
 *               Added new VerticalBarRenderer class (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 * 31-Oct-2001 : Debugging for gap settings (DG);
 *               Amendments by Serge V. Grachov to support 3D-effect bar plots (DG);
 * 20-Nov-2001 : Fixed clipping bug that shows up when chart is displayed inside JScrollPane (DG);
 * 12-Dec-2001 : Removed redundant 'throws' clause from constructor (DG);
 * 13-Dec-2001 : Added tooltips, tidied up default values in constructor (DG);
 * 16-Jan-2002 : Renamed tooltips class (DG);
 * 06-Feb-2002 : Added optional background image and alpha-transparency to Plot and subclasses (DG);
 * 13-Feb-2002 : Renamed getCategoryAxis() --> getDomainAxis() (DG);
 * 15-Feb-2002 : Modified getMaximumVerticalDataValue() and getMinimumVerticalDataValue() to handle
 *               stacked plots (DG);
 * 28-Feb-2002 : Renamed Datasets.java --> DatasetUtilities.java (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.Dataset;
import com.jrefinery.data.DatasetUtilities;
import com.jrefinery.chart.event.*;
import com.jrefinery.chart.tooltips.*;

/**
 * A general class for plotting vertical bars, using data from any class that implements the
 * CategoryDataset interface.
 * <P>
 * This class now relies on a renderer to draw the individual bars, giving some flexibility to
 * change the visual representation of the data.
 * @see Plot
 * @see VerticalBarRenderer
 */
public class VerticalBarPlot extends BarPlot implements VerticalValuePlot {

    /** The renderer responsible for drawing individual bars. */
    protected VerticalBarRenderer renderer;

    /**
     * Constructs a new vertical bar plot.
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     */
    public VerticalBarPlot(CategoryAxis horizontalAxis, ValueAxis verticalAxis) {

	this(horizontalAxis, verticalAxis,
             Plot.DEFAULT_INSETS,
             Plot.DEFAULT_BACKGROUND_PAINT,
             null, // background image
             Plot.DEFAULT_BACKGROUND_ALPHA,
             Plot.DEFAULT_OUTLINE_STROKE,
             Plot.DEFAULT_OUTLINE_PAINT,
             Plot.DEFAULT_FOREGROUND_ALPHA,
             BarPlot.DEFAULT_INTRO_GAP_PERCENT,
             BarPlot.DEFAULT_TRAIL_GAP_PERCENT,
             BarPlot.DEFAULT_CATEGORY_GAPS_PERCENT,
             BarPlot.DEFAULT_ITEM_GAPS_PERCENT,
             null,
             new VerticalBarRenderer()
             );

    }

    /**
     * Constructs a new vertical bar plot.
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     * @param insets The amount of space to leave blank around the edges of the plot.
     * @param backgroundPaint An optional color for the plot's background.
     * @param backgroundImage An optional image for the plot's background.
     * @param backgroundAlpha Alpha-transparency for the plot's background.
     * @param outlineStroke The Stroke used to draw an outline around the plot.
     * @param outlinePaint The color used to draw an outline around the plot.
     * @param foregroundAlpha The alpha-transparency for the plot.
     * @param introGapPercent The gap before the first bar in the plot, as a percentage of the
     *                        available drawing space.
     * @param trailGapPercent The gap after the last bar in the plot, as a percentage of the
     *                        available drawing space.
     * @param categoryGapPercent The percentage of drawing space allocated to the gap between the
     *                           last bar in one category and the first bar in the next category.
     * @param itemGapPercent The gap between bars within the same category.
     */
    public VerticalBarPlot(CategoryAxis horizontalAxis, ValueAxis verticalAxis,
                           Insets insets,
                           Paint backgroundPaint, Image backgroundImage, float backgroundAlpha,
                           Stroke outlineStroke, Paint outlinePaint,
                           float foregroundAlpha,
			   double introGapPercent, double trailGapPercent,
                           double categoryGapPercent, double itemGapPercent,
                           CategoryToolTipGenerator toolTipGenerator,
                           VerticalBarRenderer renderer) {

	super(horizontalAxis, verticalAxis,
              insets,
              backgroundPaint, backgroundImage, backgroundAlpha,
              outlineStroke, outlinePaint,
              foregroundAlpha,
	      introGapPercent, trailGapPercent, categoryGapPercent, itemGapPercent,
              toolTipGenerator);

        this.renderer = renderer;

    }

    /**
     * Sets the renderer for the bar plot.
     * @param renderer The renderer.
     */
    public void setRenderer(VerticalBarRenderer renderer) {
        this.renderer = renderer;
        this.notifyListeners(new PlotChangeEvent(this));
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
     * Sets the vertical axis for the plot.  This method should throw an exception if the axis
     * doesn't implement the required interfaces.
     * @param vAxis The new vertical axis.
     */
    public void setVerticalAxis(Axis vAxis) throws AxisNotCompatibleException {
	// check that the axis implements the required interface (if not raise an exception);
	super.setVerticalAxis(vAxis);
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
     * Returns the axis against which domain values are plotted.
     * <P>
     * For the VerticalBarPlot class, the domain is a set of categories plotted against a
     * CategoryAxis.
     */
    public CategoryAxis getDomainAxis() {

	return (CategoryAxis)horizontalAxis;

    }

    /**
     * Returns the axis against which range values are plotted.
     * <P>
     * For the VerticalBarPlot class, the range is numerical data plotted against a ValueAxis.
     */
    public ValueAxis getRangeAxis() {

	return (VerticalNumberAxis)verticalAxis;

    }

    /**
     * A convenience method that returns a list of the categories in the dataset.
     */
    public List getCategories() {
	return getDataset().getCategories();
    }

    /**
     * Returns the x-coordinate (in Java 2D User Space) of the center of the specified category.
     * @param category The index of the category of interest (first category index = 0);
     * @param area The region within which the plot will be drawn.
     */
    public double getCategoryCoordinate(int category, Rectangle2D area) {

        // calculate first part of result...
        double result = area.getX() + (area.getWidth()*introGapPercent);

        // then add some depending on how many categories...
	int categoryCount = getDataset().getCategoryCount();
        if (categoryCount>1) {

	    double categorySpan = area.getWidth()
                                  * (1-introGapPercent-trailGapPercent-categoryGapsPercent);
            double categoryGapSpan = area.getWidth()*categoryGapsPercent;
            result = result
                     + (category+0.5)*(categorySpan/categoryCount)
                     + (category)*(categoryGapSpan/(categoryCount-1));
        }
        else {
            result = result
                     + (category+0.5)*area.getWidth()*(1-introGapPercent-trailGapPercent);
        }

        return result;

    }

    /**
     * Checks the compatibility of a horizontal axis, returning true if the axis is compatible with
     * the plot, and false otherwise.
     * @param axis The horizontal axis;
     */
    public boolean isCompatibleHorizontalAxis(Axis axis) {
	if (axis instanceof CategoryAxis) {
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
	if (axis instanceof VerticalNumberAxis) {
	    return true;
	}
	else return false;
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     * @param g2 The graphics device.
     * @param plotArea The area within which the plot should be drawn.
     * @param info Collects info as the chart is drawn.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, DrawInfo info) {

        // set up the drawing info...
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

        if ((plotArea.getWidth()>=MINIMUM_WIDTH_TO_DRAW)
         && (plotArea.getHeight()>=MINIMUM_HEIGHT_TO_DRAW)) {

            // estimate the area required for drawing the axes...
            HorizontalAxis hAxis = getHorizontalAxis();
            VerticalAxis vAxis = getVerticalAxis();
            double hAxisAreaHeight = hAxis.reserveHeight(g2, this, plotArea);
            Rectangle2D vAxisArea = vAxis.reserveAxisArea(g2, this, plotArea, hAxisAreaHeight);

            // and thus the area available for plotting...
            Rectangle2D dataArea = new Rectangle2D.Double(plotArea.getX()+vAxisArea.getWidth(),
                                                          plotArea.getY(),
                                                          plotArea.getWidth()-vAxisArea.getWidth(),
                                                          plotArea.getHeight()-hAxisAreaHeight);

            if (info!=null) {
                info.setDataArea(dataArea);
            }
            Shape backgroundPlotArea = calculateBackgroundPlotArea(dataArea);

            // draw the background and axes...
            drawOutlineAndBackground(g2, backgroundPlotArea);
            getDomainAxis().draw(g2, plotArea, dataArea);
            getRangeAxis().draw(g2, plotArea, dataArea);

            drawBars(g2, backgroundPlotArea, dataArea, info);

            // draw horizontal crosshair if required...
            ValueAxis vva = this.getRangeAxis();
            if (vva.isCrosshairVisible()) {
                this.drawHorizontalLine(g2, dataArea, vva.getCrosshairValue(),
                                        vva.getCrosshairStroke(),
                                        vva.getCrosshairPaint());
            }
        }


    }

    /**
     * Returns chart's background area.
     */
    protected Shape calculateBackgroundPlotArea(Rectangle2D plotArea) {
      return plotArea;
    }

    /**
     * Draws charts bars
     * @param g2 The graphics device;
     * @param backgroundPlotArea The area within which will be clipped
     * @param plotArea The area within which the plot should be drawn.
     */
    protected void drawBars(Graphics2D g2,
                            Shape backgroundPlotArea, Rectangle2D dataArea,
                            DrawInfo info) {

        // now get the data and plot the bars...
        CategoryDataset data = this.getDataset();
        if (data!=null) {
            Shape savedClip = g2.getClip();
            g2.clip(backgroundPlotArea);
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       this.foregroundAlpha));

            int seriesCount = data.getSeriesCount();
            int categoryCount = data.getCategoryCount();
            int barCount = renderer.barWidthsPerCategory(data);
            double translatedZero = getRangeAxis().translateValueToJava2D(0.0, dataArea);

            // work out the span dimensions for the categories...
            double categorySpan = 0.0;
            double categoryGapSpan = 0.0;
            if (categoryCount>1) {
                categorySpan = dataArea.getWidth()
                               * (1-introGapPercent-trailGapPercent-categoryGapsPercent);
                categoryGapSpan = dataArea.getWidth()*categoryGapsPercent;
            }
            else {
                categorySpan = dataArea.getWidth()*(1-introGapPercent-trailGapPercent);
            }

            // work out the item span...
            double itemSpan = categorySpan;
            double itemGapSpan = 0.0;
            if (seriesCount>1) {
                if (renderer.hasItemGaps()) {
                    itemGapSpan = dataArea.getWidth()*itemGapsPercent;
                    itemSpan = itemSpan - itemGapSpan;
                }
            }
            double itemWidth = itemSpan/(categoryCount*renderer.barWidthsPerCategory(data));

            int categoryIndex = 0;
	    Iterator iterator = data.getCategories().iterator();
	    while (iterator.hasNext()) {

		Object category = iterator.next();
		for (int series=0; series<seriesCount; series++) {
                    Shape tooltipArea = renderer.drawBar(g2,
                                                         dataArea, this,
                                                         getRangeAxis(),
                                                         data, series,
                                                         category, categoryIndex,
                                                         translatedZero, itemWidth,
                                                         categorySpan, categoryGapSpan,
                                                         itemSpan, itemGapSpan);

                    // add a tooltip for the bar...
                    if (info!=null) {
                        ToolTipsCollection tooltips = info.getToolTipsCollection();
                        if (tooltips!=null) {
                            if (this.toolTipGenerator==null) {
                                toolTipGenerator = new StandardCategoryToolTipGenerator();
                            }
                            String tip = this.toolTipGenerator.generateToolTip(data, series,
                                                                               category);
                            if (tooltipArea!=null) {
                                tooltips.addToolTip(tip, tooltipArea);
                            }
                        }
                    }

                }
                categoryIndex++;

            }

            // draw a line at zero...
            Line2D baseline = new Line2D.Double(dataArea.getX(), translatedZero,
                                                dataArea.getMaxX(), translatedZero);
            g2.setStroke(new BasicStroke());
            g2.draw(baseline);

            g2.setClip(savedClip);
            g2.setComposite(originalComposite);
        }
    }

    /**
     * Draws the plot outline and background.
     * @param g2 The graphics device.
     * @param area The area for the plot.
     */
    public void drawOutlineAndBackground(Graphics2D g2, Shape area) {

	if (backgroundPaint!=null) {
	    g2.setPaint(backgroundPaint);
	    g2.fill(area);
	}

	if ((outlineStroke!=null) && (outlinePaint!=null)) {
	    g2.setStroke(outlineStroke);
	    g2.setPaint(outlinePaint);
	    g2.draw(area);
	}

    }

    /**
     * Returns the width of each bar in the chart.
     * @param area The area within which the plot will be drawn.
     */
    double calculateBarWidth(Rectangle2D plotArea) {

	CategoryDataset data = getDataset();

	// series, category and bar counts
	int categoryCount = data.getCategoryCount();
	int seriesCount = data.getSeriesCount();
	int barCount = renderer.barWidthsPerCategory(data)*categoryCount;

	// calculate the plot width (bars are vertical) less whitespace
        double usable = plotArea.getWidth() *
                                    (1.0 - introGapPercent - trailGapPercent - categoryGapsPercent);

        if (renderer.barWidthsPerCategory(data)>1) {
//	    usable = usable - ((seriesCount-1) * categoryCount * seriesGap);
        }

	// and thus the width of the bars
	return usable/barCount;
    }

    /**
     * Returns a short string describing the type of plot.
     */
    public String getPlotType() {
	return "Bar Plot";
    }

    /**
     * Returns the minimum value in the range (since this is plotted against the vertical axis by
     * VerticalBarPlot).
     * <P>
     * This method will return null if the dataset is null.
     *
     * @return The minimum value.
     */
    public Number getMinimumVerticalDataValue() {

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
     * vertical axis by this plot).
     * <P>
     * This method will return null if the dataset is null.
     *
     * @return The maximum value.
     */
    public Number getMaximumVerticalDataValue() {

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
        ValueAxis vva = this.getRangeAxis();
        double vvalue = vva.translateJava2DtoValue((float)y, info.getDataArea());
        vva.setAnchorValue(vvalue);
        vva.setCrosshairValue(vvalue);

    }

    /**
     * Utility method for drawing a crosshair on the chart (if required).
     */
    private void drawHorizontalLine(Graphics2D g2, Rectangle2D dataArea, double value,
                                    Stroke stroke, Paint paint) {

        double yy = this.getRangeAxis().translateValueToJava2D(value, dataArea);
        Line2D line = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);

    }

}
