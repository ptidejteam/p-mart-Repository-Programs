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
 * -------------
 * LinePlot.java
 * -------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Sylvain Vieujot;
 *
 * $Id: LinePlot.java,v 1.1 2007/10/10 18:56:25 vauchers Exp $
 *
 * Changes (from 21-Jun-2001)
 * --------------------------
 * 21-Jun-2001 : Removed redundant JFreeChart parameter from constructor (DG);
 * 18-Sep-2001 : Updated e-mail address and fixed DOS encoding (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 19-Oct-2001 : Moved series paint and stroke methods from JFreeChart.java to Plot.java (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Added HorizontalCategoryItemRenderer interface for drawing the data (DG);
 * 20-Nov-2001 : Fixed clipping bug that shows up when chart is displayed inside JScrollPane (DG);
 *               Added properties to control gaps at each end of the plot (DG);
 * 12-Dec-2001 : Removed unnecessary 'throws' clause in constructor (DG);
 * 13-Dec-2001 : Added skeleton code for tooltips (DG);
 * 16-Jan-2002 : Renamed tooltips class.  Renamed the category item renderer interface (DG);
 * 05-Feb-2002 : Added accessor methods for the renderer, plus a new constructor.  Added more code
 *               for tooltips.  Added alpha-transparency setting based on code by Sylvain
 *               Vieujot (DG);
 * 06-Feb-2002 : Added optional background image and alpha-transparency to Plot and subclasses (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import com.jrefinery.chart.event.PlotChangeEvent;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardCategoryToolTipGenerator;
import com.jrefinery.chart.tooltips.ToolTipsCollection;
import com.jrefinery.data.Dataset;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.Datasets;

/**
 * A Plot that displays data in the form of a line chart, using data from any class that
 * implements the CategoryDataset interface.
 * @see Plot
 */
public class LinePlot extends Plot implements CategoryPlot, VerticalValuePlot {

    /** The default gap before the first category (10%). */
    public static final double DEFAULT_INTRO_GAP = 0.10;

    /** The maximum gap before the first category (25%). */
    public static final double MAX_INTRO_GAP = 0.25;

    /** The default gap after the last category (10%). */
    public static final double DEFAULT_TRAIL_GAP = 0.10;

    /** The maximum gap after the last category (25%). */
    public static final double MAX_TRAIL_GAP = 0.25;

    /** The gap before the first category, as a percentage of the total space. */
    protected double introGapPercent=0.10;

    /** The gap after the last category, as a percentage of the total space. */
    protected double trailGapPercent=0.10;

    /** The renderer that draws the lines. */
    protected CategoryItemRenderer renderer;

    /** The tool tip generator. */
    protected CategoryToolTipGenerator toolTipGenerator;

    /**
     * Constructs a line plot (by default, the line plot draws shapes at each data point, as well
     * as the lines between data points).
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     */
    public LinePlot(CategoryAxis horizontalAxis, ValueAxis verticalAxis) {

	this(horizontalAxis, verticalAxis,
             Plot.DEFAULT_INSETS,
             Plot.DEFAULT_BACKGROUND_PAINT,
             null, // background image
             Plot.DEFAULT_BACKGROUND_ALPHA,
             Plot.DEFAULT_OUTLINE_STROKE,
             Plot.DEFAULT_OUTLINE_PAINT,
             Plot.DEFAULT_FOREGROUND_ALPHA,
             DEFAULT_INTRO_GAP,
             DEFAULT_TRAIL_GAP,
             new LineAndShapeRenderer(LineAndShapeRenderer.SHAPES_AND_LINES));

    }

    /**
     * Constructs a new line plot with the specified attributes.
     * @param horizontalAxis The horizontal (category) axis.
     * @param verticalAxis The vertical (value) axis.
     * @param insets The space around the outside of the plot.
     * @param backgroundPaint An optional color for the plot's background.
     * @param backgroundImage An optional image for the plot's background.
     * @param backgroundAlpha Alpha-transparency for the plot's background.
     * @param outlineStroke The pen/brush used to draw an outline around the data area.
     * @param outlineColor The color used to draw the outline around the data area.
     * @param foregroundAlpha The alpha-transparency for the plot's foreground.
     * @param introGapPercent The space before the first data item (expressed as a percentage).
     * @param trailGapPercent The space after the last data item (expressed as a percentage).
     * @param renderer The class responsible for rendering the data on the plot.
     *
     */
    public LinePlot(CategoryAxis horizontalAxis, ValueAxis verticalAxis,
                    Insets insets,
                    Paint backgroundPaint, Image backgroundImage, float backgroundAlpha,
                    Stroke outlineStroke, Paint outlineColor, float foregroundAlpha,
                    double introGapPercent, double trailGapPercent,
                    CategoryItemRenderer renderer) {

        super(horizontalAxis, verticalAxis,
              insets,
              backgroundPaint, backgroundImage, backgroundAlpha,
              outlineStroke, outlineColor, foregroundAlpha);

        this.introGapPercent = introGapPercent;
        this.trailGapPercent = trailGapPercent;
        this.renderer = renderer;

    }

    /**
     * Returns the intro gap.
     * @return The intro gap as a percentage of the available width.
     */
    public double getIntroGapPercent() {
        return this.introGapPercent;
    }

    /**
     * Sets the intro gap.
     * @param The gap as a percentage of the total width.
     */
    public void setIntroGapPercent(double percent) {

        // check arguments...
        if ((percent<=0.0) || (percent>MAX_INTRO_GAP)) {
            throw new IllegalArgumentException("LinePlot.setIntroGapPercent(double): "
                                               +"gap percent outside valid range.");
        }

        // make the change...
        if (introGapPercent!=percent) {
            introGapPercent = percent;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the trail gap.
     * @return The trail gap as a percentage of the available width.
     */
    public double getTrailGapPercent() {
        return this.introGapPercent;
    }

    /**
     * Sets the trail gap.
     * @param The gap as a percentage of the total width.
     */
    public void setTrailGapPercent(double percent) {

        // check arguments...
        if ((percent<=0.0) || (percent>MAX_TRAIL_GAP)) {
            throw new IllegalArgumentException("LinePlot.setTrailGapPercent(double): "
                                               +"gap percent outside valid range.");
        }

        // make the change...
        if (trailGapPercent!=percent) {
            trailGapPercent = percent;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the renderer for the plot.
     * @return The renderer for the plot.
     */
    public CategoryItemRenderer getRenderer() {
        return this.renderer;
    }

    /**
     * Sets the renderer for this plot.
     * <P>
     * For now, the LineAndShapeRenderer class is the only available renderer for the LinePlot
     * class.
     * @param renderer The new renderer.
     */
    public void setRenderer(CategoryItemRenderer renderer) {

        // check arguments...
        if (renderer==null) {
            throw new IllegalArgumentException("LinePlot.setRenderer(...): "
                                               +"null not permitted.");
        }

        // make the change...
        this.renderer = renderer;
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Returns the tooltip generator for the plot.
     * @return The tooltip generator for the plot.
     */
    public CategoryToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tooltip generator for the plot.
     * @param generator The new generator.
     */
    public void setToolTipGenerator(CategoryToolTipGenerator generator) {
        this.toolTipGenerator = generator;
    }

    /**
     * A convenience method that returns the dataset for the plot, cast as a CategoryDataset.
     */
    public CategoryDataset getDataset() {
	return (CategoryDataset)chart.getDataset();
    }

    /**
     * A convenience method that returns a reference to the horizontal axis cast as a
     * CategoryAxis.
     */
    public CategoryAxis getCategoryAxis() {
	return (CategoryAxis)horizontalAxis;
    }

    /**
     * A convenience method that returns a reference to the vertical axis cast as a
     * VerticalNumberAxis.
     */
    public VerticalNumberAxis getValueAxis() {
	return (VerticalNumberAxis)verticalAxis;
    }

    /**
     * A convenience method that returns a list of the categories in the data source.
     */
    public java.util.List getCategories() {
	return getDataset().getCategories();
    }

    /**
     * Returns the x-coordinate (in Java 2D User Space) of the center of the specified category.
     * @param category The category (zero-based index).
     * @param area The region within which the plot will be drawn.
     */
    public double getCategoryCoordinate(int category, Rectangle2D area) {

        // check arguments...
	int count = getDataset().getCategoryCount();
        if ((category<0) || (category>=count)) {
            throw new IllegalArgumentException("LinePlot.getCategoryCoordinate(...): "
                                               +"category outside valid range.");
        }
        if (area==null) {
            throw new IllegalArgumentException("LinePlot.getCategoryCoordinate(...): "
                                               +"null area not permitted.");
        }

        // calculate result...
        double result = area.getX() + area.getWidth()/2;
        if (count>1) {
            double available = area.getWidth() * (1-introGapPercent-trailGapPercent);
	    result = area.getX()+(introGapPercent*area.getWidth())
                                +(category*1.0/(count-1.0))*available;
        }

        return result;

    }

    /**
     * Checks the compatibility of a horizontal axis, returning true if the axis is compatible with
     * the plot, and false otherwise.
     * @param axis The horizontal axis.
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
     * @param info Collects info about the drawing.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, DrawInfo info) {

        // set up info collection...
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
	HorizontalAxis hAxis = getHorizontalAxis();
	VerticalAxis vAxis = getVerticalAxis();
	double hAxisAreaHeight = hAxis.reserveHeight(g2, this, plotArea);
	Rectangle2D vAxisArea = vAxis.reserveAxisArea(g2, this, plotArea, hAxisAreaHeight);

	// ...and therefore what is left for the plot itself...
	Rectangle2D dataArea = new Rectangle2D.Double(plotArea.getX()+vAxisArea.getWidth(),
						      plotArea.getY(),
						      plotArea.getWidth()-vAxisArea.getWidth(),
						      plotArea.getHeight()-hAxisAreaHeight);

        if (info!=null) {
            info.setDataArea(dataArea);
        }

        // draw the background and axes...
	drawOutlineAndBackground(g2, dataArea);
	this.horizontalAxis.draw(g2, plotArea, dataArea);
	this.verticalAxis.draw(g2, plotArea, dataArea);

        // now get the data and plot the lines (or shapes, or lines and shapes)...
        CategoryDataset data = this.getDataset();
        if (data!=null) {
            Shape originalClip=g2.getClip();
	    g2.clip(dataArea);
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       this.foregroundAlpha));

	    int seriesCount = data.getSeriesCount();
            int categoryCount = data.getCategoryCount();
            int categoryIndex = 0;
            Object previousCategory = null;
            Iterator iterator = data.getCategories().iterator();
            while (iterator.hasNext()) {

                Object category = iterator.next();
                for (int series=0; series<seriesCount; series++) {
                    Shape tooltipArea = renderer.drawCategoryItem(g2, dataArea, this,
                                              getValueAxis(), data,
                                              series, category, categoryIndex, previousCategory);

                    // add a tooltip for the item...
                    if (tooltips!=null) {
                        if (this.toolTipGenerator==null) {
                            toolTipGenerator = new StandardCategoryToolTipGenerator();
                        }
                        String tip = this.toolTipGenerator.generateToolTip(data, series, category);
                        if (tooltipArea!=null) {
                            tooltips.addToolTip(tip, tooltipArea);
                        }
                    }
                }
                previousCategory = category;
                categoryIndex++;

            }

	    g2.setClip(originalClip);
            g2.setComposite(originalComposite);
        }

    }

    /**
     * Returns a short string describing the plot type;
     */
    public String getPlotType() {
	return "Line Plot";
    }

    /**
     * Returns the minimum value in the range, since this is plotted against the vertical axis for
     * LinePlot.
     */
    public Number getMinimumVerticalDataValue() {

	Dataset data = this.getChart().getDataset();
	if (data!=null) {
	    return Datasets.getMinimumRangeValue(data);
	}
	else return null;

    }

    /**
     * Returns the maximum value in either the domain or the range, whichever is displayed against
     * the vertical axis for the particular type of plot implementing this interface.
     */
    public Number getMaximumVerticalDataValue() {

	Dataset data = this.getChart().getDataset();
	if (data!=null) {
	    return Datasets.getMaximumRangeValue(data);
	}
	else return null;
    }

}