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
 * -----------
 * XYPlot.java
 * -----------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Craig MacFarlane;
 *                   Mark Watson (www.markwatson.com);
 *                   Jonathan Nash;
 *
 * $Id: XYPlot.java,v 1.1 2007/10/10 19:02:36 vauchers Exp $
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
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Insets;
import java.awt.Image;
import java.awt.Color;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.ArrayList;
import com.jrefinery.data.Dataset;
import com.jrefinery.data.DatasetUtilities;
import com.jrefinery.data.XYDataset;
import com.jrefinery.chart.event.*;
import com.jrefinery.chart.tooltips.*;

/**
 * A general class for plotting data in the form of (x, y) pairs.  XYPlot can use data from any
 * class that implements the XYDataset interface (in the com.jrefinery.data package).
 * <P>
 * XYPlot makes use of a renderer to draw each point on the plot.  By using different renderers,
 * various chart types can be produced.  The ChartFactory class contains static methods for
 * creating pre-configured charts.
 * @see ChartFactory
 * @see Plot
 * @see XYDataset
 */
public class XYPlot extends Plot implements HorizontalValuePlot,
                                            VerticalValuePlot,
                                            PropertyChangeListener {

    /** Object responsible for drawing the visual representation of each point on the plot. */
    protected XYItemRenderer renderer;

    /** A list of (optional) vertical lines that will be overlaid on the plot. */
    protected List verticalLines = null;

    /** The colors for the vertical lines. */
    protected List verticalColors = null;

    /** A list of horizontal lines that will be overlaid on the plot. */
    protected List horizontalLines = null;

    /** The colors for the horizontal lines. */
    protected List horizontalColors = null;

    /** The tool tip generator. */
    protected XYToolTipGenerator toolTipGenerator;

    /**
     * Constructs an XYPlot with the specified axes (other attributes take default values).
     *
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     */
    public XYPlot(ValueAxis horizontalAxis, ValueAxis verticalAxis) {

	this(horizontalAxis, verticalAxis,
             Plot.DEFAULT_INSETS,
             Plot.DEFAULT_BACKGROUND_PAINT,
             null, // background image
             Plot.DEFAULT_BACKGROUND_ALPHA,
             Plot.DEFAULT_OUTLINE_STROKE,
             Plot.DEFAULT_OUTLINE_PAINT,
             Plot.DEFAULT_FOREGROUND_ALPHA,
             new StandardXYItemRenderer(),
             new StandardXYToolTipGenerator());

    }

    /**
     * Constructs an XYPlot with the specified axes and renderer (other attributes take default
     * values).
     *
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     * @param renderer The renderer
     */
    public XYPlot(ValueAxis horizontalAxis, ValueAxis verticalAxis, XYItemRenderer renderer) {

	this(horizontalAxis, verticalAxis,
             Plot.DEFAULT_INSETS,
             Plot.DEFAULT_BACKGROUND_PAINT,
             null, // background image
             Plot.DEFAULT_BACKGROUND_ALPHA,
             Plot.DEFAULT_OUTLINE_STROKE,
             Plot.DEFAULT_OUTLINE_PAINT,
             Plot.DEFAULT_FOREGROUND_ALPHA,
             renderer,
             new StandardXYToolTipGenerator());

    }

    /**
     * Constructs a new XY plot.
     *
     * @param horizontalAxis The horizontal axis.
     * @param verticalAxis The vertical axis.
     * @param insets Amount of blank space around the plot area.
     * @param backgroundPaint An optional color for the plot's background.
     * @param backgroundImage An optional image for the plot's background.
     * @param backgroundAlpha Alpha-transparency for the plot's background.
     * @param outlineStroke The Stroke used to draw an outline around the plot.
     * @param outlinePaint The color used to draw the plot outline.
     * @param alpha The alpha-transparency.
     * @param renderer The renderer.
     * @param toolTipGenerator The tooltip generator (null permitted).
     */
    public XYPlot(ValueAxis horizontalAxis, ValueAxis verticalAxis,
                  Insets insets,
                  Paint backgroundPaint, Image backgroundImage, float backgroundAlpha,
                  Stroke outlineStroke, Paint outlinePaint, float alpha,
                  XYItemRenderer renderer, XYToolTipGenerator toolTipGenerator) {

        super(horizontalAxis, verticalAxis,
              insets,
              backgroundPaint, backgroundImage, backgroundAlpha,
              outlineStroke, outlinePaint, alpha);

        this.renderer = renderer;
        this.renderer.addPropertyChangeListener(this);
        this.toolTipGenerator = toolTipGenerator;

    }

    /**
     * Returns a reference to the current item renderer.
     * @return A reference to the current item renderer.
     */
    public XYItemRenderer getItemRenderer() {
        return this.renderer;
    }

    /**
     * Sets the item renderer, and notifies all listeners of a change to the plot.
     * @param renderer The new renderer.
     */
    public void setXYItemRenderer(XYItemRenderer renderer) {

        boolean changed = false;

        if (this.renderer!=null) {
            if (!this.renderer.equals(renderer)) {
                this.renderer.removePropertyChangeListener(this);
                this.renderer = renderer;
                changed = true;
            }
        }
        else {
            if (renderer!=null) {
                this.renderer = renderer;
                changed = true;
            }
        }

        if (changed) this.notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Returns the tooltip generator for the plot.
     * @return The tooltip generator for the plot (possibly null).
     */
    public XYToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tooltip generator for the plot.
     * @param generator The new generator (null permitted).
     */
    public void setToolTipGenerator(XYToolTipGenerator generator) {
        this.toolTipGenerator = generator;
    }

    /**
     * A convenience method that returns the dataset for the plot, cast as an XYDataset.
     * @return The dataset for the plot, cast as an XYDataset.
     */
    public XYDataset getDataset() {
	return (XYDataset)chart.getDataset();
    }

    /**
     * Adds a vertical line at location with default color blue.
     * @return void
     */
    public void addVerticalLine(Number location) {
        addVerticalLine(location, Color.blue);
    }

    /**
     * Adds a vertical of the given color at location with the given color.
     * @return void
     */
    public void addVerticalLine(Number location, Paint color) {

        if (verticalLines == null) {
            verticalLines = new ArrayList();
            verticalColors = new ArrayList();
        }

        verticalColors.add(color);
        verticalLines.add(location);

    }

    /**
     * Adds a horizontal line at the specified data value, using the default color red.
     * @param value The data value.
     */
    public void addHorizontalLine(Number value) {

        addHorizontalLine(value, Color.red);
        this.notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Adds a horizontal line at the specified data value, using the specified color.
     * @param value The data value.
     * @param color The line color.
     */
    public void addHorizontalLine(Number location, Paint color) {

        if (horizontalLines == null) {
            horizontalLines = new ArrayList();
            horizontalColors = new ArrayList();
        }

        horizontalColors.add(color);
        horizontalLines.add(location);

    }

    /**
     * A convenience method that returns a reference to the horizontal axis cast as a
     * ValueAxis.
     * @return The horizontal axis cast as a ValueAxis.
     */
    public ValueAxis getDomainAxis() {
	return (ValueAxis)horizontalAxis;
    }

    /**
     * A convenience method that returns a reference to the vertical axis cast as a
     * ValueAxis.
     * @return The vertical axis cast as a ValueAxis.
     */
    public ValueAxis getRangeAxis() {
	return (ValueAxis)verticalAxis;
    }

    /**
     * Checks the compatibility of a horizontal axis, returning true if the axis is compatible with
     * the plot, and false otherwise.
     * @param axis The horizontal axis;
     * @return True if the axis is compatible with the plot, and false otherwise.
     */
    public boolean isCompatibleHorizontalAxis(Axis axis) {
	if (axis instanceof HorizontalNumberAxis) {
	    return true;
	}
	else if (axis instanceof HorizontalDateAxis) {
	    return true;
	}
	else return false;
    }

    /**
     * Checks the compatibility of a vertical axis, returning true if the axis is compatible with
     * the plot, and false otherwise.
     * @param axis The vertical axis;
     * @return True if the axis is compatible with the plot, and false otherwise.
     */
    public boolean isCompatibleVerticalAxis(Axis axis) {
	if (axis instanceof VerticalNumberAxis) {
	    return true;
	}
	else return false;
    }

    /**
     * Draws the XY plot on a Java 2D graphics device (such as the screen or a printer).
     * <P>
     * XYPlot relies on an XYItemRenderer to draw each item in the plot.  This allows the visual
     * representation of the data to be changed easily.
     * <P>
     * The optional info argument collects information about the rendering of the plot (dimensions,
     * tooltip information etc).  Just pass in null if you do not need this information.
     * @param g2 The graphics device.
     * @param plotArea The area within which the plot (including axis labels) should be drawn.
     * @param info Collects chart drawing information (null permitted).
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, DrawInfo info) {

        // set up info collection...
        ToolTipsCollection tooltips = null;
        if (info!=null) {
            info.setPlotArea(plotArea);
            tooltips = info.getToolTipsCollection();
        }

        // adjust the drawing area for plot insets (if any)...
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

        CrosshairInfo crosshairInfo = new CrosshairInfo();

        crosshairInfo.setCrosshairDistance(Double.POSITIVE_INFINITY);
        crosshairInfo.setAnchorX(this.getDomainAxis().getAnchorValue());
        crosshairInfo.setAnchorY(this.getRangeAxis().getAnchorValue());

        // draw the plot background and axes...
	drawOutlineAndBackground(g2, dataArea);
	this.horizontalAxis.draw(g2, plotArea, dataArea);
	this.verticalAxis.draw(g2, plotArea, dataArea);

        // now get the data and plot it (the visual representation will depend on the renderer
        // that has been set)...
        XYDataset data = this.getDataset();
        if (data!=null) {
	    Shape originalClip = g2.getClip();
            Composite originalComposite = g2.getComposite();

	    g2.clip(dataArea);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       this.foregroundAlpha));

            drawVerticalLines(g2, dataArea);
            drawHorizontalLines(g2, dataArea);

            double transRangeZero = this.getRangeAxis().translateValueToJava2D(0.0, dataArea);

            int seriesCount = data.getSeriesCount();
            for (int series=0; series<seriesCount; series++) {
                int itemCount = data.getItemCount(series);
                for (int item=0; item<itemCount; item++) {
                    Shape tooltipArea = renderer.drawItem(g2, dataArea, info, this,
                                                          (ValueAxis)horizontalAxis,
                                                          (ValueAxis)verticalAxis,
                                                          data, series, item,
                                                          transRangeZero, crosshairInfo);

                    // add a tooltip for the item...
                    if (tooltips!=null) {
                        if (this.toolTipGenerator==null) {
                            toolTipGenerator = new StandardXYToolTipGenerator();
                        }
                        String tip = this.toolTipGenerator.generateToolTip(data, series, item);
                        if (tooltipArea!=null) {
                            tooltips.addToolTip(tip, tooltipArea);
                        }
                    }

                }
            }


            // draw vertical crosshair if required...
            ValueAxis hva = (ValueAxis)this.horizontalAxis;
            hva.setCrosshairValue(crosshairInfo.getCrosshairX());
            if (hva.isCrosshairVisible()) {
                this.drawVerticalLine(g2, dataArea, hva.getCrosshairValue(),
                                      hva.getCrosshairStroke(),
                                      hva.getCrosshairPaint());
            }

            // draw horizontal crosshair if required...
            ValueAxis vva = (ValueAxis)this.verticalAxis;
            vva.setCrosshairValue(crosshairInfo.getCrosshairY());
            if (vva.isCrosshairVisible()) {
                this.drawHorizontalLine(g2, dataArea, vva.getCrosshairValue(),
                                        vva.getCrosshairStroke(),
                                        vva.getCrosshairPaint());
            }

            g2.setClip(originalClip);
            g2.setComposite(originalComposite);
        }
    }

    /**
     * Utility method for drawing a crosshair on the chart (if required).
     */
    private void drawVerticalLine(Graphics2D g2, Rectangle2D dataArea, double value,
                                  Stroke stroke, Paint paint) {

        double xx = this.getDomainAxis().translateValueToJava2D(value, dataArea);
        Line2D line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);

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

    /**
     * Support method for the draw(...) method.
     */
    private void drawVerticalLines(Graphics2D g2, Rectangle2D dataArea) {

        // Draw any vertical lines
        if (verticalLines != null) {
            for (int i=0; i<verticalLines.size(); i++) {
                g2.setPaint((Paint)verticalColors.get(i));
                g2.setStroke(new BasicStroke(1));
                Number x = (Number)verticalLines.get(i);
                int xint = (int)getDomainAxis().translateValueToJava2D(x.doubleValue(), dataArea);
                g2.drawLine(xint, 0, xint, (int)(dataArea.getHeight()));
            }
        }

    }

    /**
     * Support method for the draw(...) method.
     */
    private void drawHorizontalLines(Graphics2D g2, Rectangle2D dataArea) {

        // Draw any horizontal lines
        if (horizontalLines != null) {
            for (int i=0; i<horizontalLines.size(); i++) {
                g2.setPaint((Paint)horizontalColors.get(i));
                g2.setStroke(new BasicStroke(1));
                Number y = (Number)horizontalLines.get(i);
                int yint = (int)getRangeAxis().translateValueToJava2D(y.doubleValue(), dataArea);
                g2.drawLine(0, yint, (int)(dataArea.getWidth()), yint);
            }
        }

    }

    /**
     * Handles a 'click' on the plot by updating the anchor values...
     */
    public void handleClick(int x, int y, DrawInfo info) {

        // set the anchor value for the horizontal axis...
        ValueAxis hva = this.getDomainAxis();
        double hvalue = hva.translateJava2DtoValue((float)x, info.getDataArea());

        hva.setAnchorValue(hvalue);
        hva.setCrosshairValue(hvalue);

        // set the anchor value for the vertical axis...
        ValueAxis vva = this.getRangeAxis();
        double vvalue = vva.translateJava2DtoValue((float)y, info.getDataArea());
        vva.setAnchorValue(vvalue);
        vva.setCrosshairValue(vvalue);

    }

    public void zoom(double percent) {

        if (percent>0) {
            ValueAxis domainAxis = this.getDomainAxis();
            double range = domainAxis.getMaximumAxisValue()-domainAxis.getMinimumAxisValue();
            double scaledRange = range * percent;
            domainAxis.setAnchoredRange(scaledRange);

            ValueAxis rangeAxis = this.getRangeAxis();
            range = rangeAxis.getMaximumAxisValue()-rangeAxis.getMinimumAxisValue();
            scaledRange = range * percent;
            rangeAxis.setAnchoredRange(scaledRange);
        }
        else {
            this.getRangeAxis().setAutoRange(true);
            this.getDomainAxis().setAutoRange(true);
        }

    }

    /**
     * Returns the plot type as a string.
     * @return A short string describing the type of plot.
     */
    public String getPlotType() {
	return "XY Plot";
    }

    /**
     * Returns the minimum value in the domain, since this is plotted against the horizontal axis
     * for an XYPlot.
     * @return The minimum value to be plotted against the horizontal axis.
     */
    public Number getMinimumHorizontalDataValue() {

        Number result = null;

        if (this.chart!=null) {
	    Dataset data = this.getChart().getDataset();
	    if (data!=null) {
	        result = DatasetUtilities.getMinimumDomainValue(data);
	    }
        }

        return result;

    }

    /**
     * Returns the maximum value in the domain, since this is plotted against the horizontal axis
     * for an XYPlot.
     * @return The maximum value to be plotted against the horizontal axis.
     */
    public Number getMaximumHorizontalDataValue() {

        Number result = null;

        if (this.chart!=null) {
	    Dataset data = this.chart.getDataset();
	    if (data!=null) {
	        result = DatasetUtilities.getMaximumDomainValue(data);
	    }
        }

        return result;

    }

    /**
     * Returns the minimum value in the range, since this is plotted against the vertical axis for
     * an XYPlot.
     * @return The minimum value to be plotted against the vertical axis.
     */
    public Number getMinimumVerticalDataValue() {

        Number result = null;

        if (this.chart!=null) {
	    Dataset data = this.chart.getDataset();
	    if (data!=null) {
	        result = DatasetUtilities.getMinimumRangeValue(data);
	    }
        }

        return result;

    }

    /**
     * Returns the maximum value in the range, since this is plotted against the vertical axis for
     * an XYPlot.
     * @return The maximum value to be plotted against the vertical axis.
     */
    public Number getMaximumVerticalDataValue() {

        Number result = null;

        if (this.chart!=null) {
	    Dataset data = this.getChart().getDataset();
	    if (data!=null) {
	        result = DatasetUtilities.getMaximumRangeValue(data);
	    }
	}

        return result;

    }

    public void propertyChange(PropertyChangeEvent event) {

        this.notifyListeners(new PlotChangeEvent(this));

    }

}