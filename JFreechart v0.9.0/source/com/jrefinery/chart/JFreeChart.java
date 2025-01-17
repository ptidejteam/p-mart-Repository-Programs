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
 * ---------------
 * JFreeChart.java
 * ---------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   David Li;
 *                   Wolfgang Irler;
 *
 * $Id: JFreeChart.java,v 1.1 2007/10/10 19:01:18 vauchers Exp $
 *
 * Changes (from 20-Jun-2001)
 * --------------------------
 * 20-Jun-2001 : Modifications submitted by Andrzej Porebski for legend placement;
 * 21-Jun-2001 : Removed JFreeChart parameter from Plot constructors (DG);
 * 22-Jun-2001 : Multiple titles added (original code by David Berry, with reworkings by DG);
 * 18-Sep-2001 : Updated header (DG);
 * 15-Oct-2001 : Moved data source classes into new package com.jrefinery.data.* (DG);
 * 18-Oct-2001 : New factory method for creating VerticalXYBarChart (DG);
 * 19-Oct-2001 : Moved series paint and stroke methods to the Plot class (DG);
 *               Moved static chart creation methods to new ChartFactory class (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 *               Fixed bug where chart isn't registered with the dataset (DG);
 * 07-Nov-2001 : Fixed bug where null title in constructor causes exception (DG);
 *               Tidied up event notification code (DG);
 * 17-Nov-2001 : Added getLegendItemCount() method (DG);
 * 21-Nov-2001 : Set clipping in draw method to ensure that nothing gets drawn outside the chart
 *               area (DG);
 * 11-Dec-2001 : Added the createBufferedImage(...) method, taken from the JFreeChartServletDemo
 *               class (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 16-Jan-2002 : Added handleClick(...) method (DG);
 * 22-Jan-2002 : Fixed bug correlating legend labels with pie data (DG);
 * 05-Feb-2002 : Removed redundant tooltips code (DG);
 * 19-Feb-2002 : Added accessor methods for the backgroundImage and backgroundImageAlpha
 *               attributes (DG);
 * 21-Feb-2002 : Added static fields for INFO, COPYRIGHT, LICENCE, CONTRIBUTORS and LIBRARIES.
 *               These can be used to display information about JFreeChart (DG);
 * 06-Mar-2002 : Moved constants to JFreeChartConstants interface (DG);
 * 18-Apr-2002 : PieDataset is no longer sorted (oldman);
 * 23-Apr-2002 : Moved dataset to the Plot class (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import com.jrefinery.ui.about.Contributor;
import com.jrefinery.ui.about.Library;
import com.jrefinery.chart.event.*;
import com.jrefinery.data.*;

/**
 * A chart class implemented using the Java 2D APIs.  The current version supports bar charts,
 * line charts, pie charts and xy plots (including time series data).
 * <P>
 * JFreeChart coordinates several objects to achieve its aim of being able to draw a chart
 * on a Java 2D graphics device: a Title, a Legend, a Plot and a Dataset (the Plot in turn
 * manages a horizontal axis and a vertical axis).
 * <P>
 * You should use JFreeChartPanel to display a chart in a GUI.
 * <P>
 * The ChartFactory class contains static methods for creating 'ready-made' charts.
 * @see JFreeChartPanel
 * @see ChartFactory
 * @see AbstractTitle
 * @see Legend
 * @see Plot
 */
public class JFreeChart implements JFreeChartInfo, JFreeChartConstants,
                                   TitleChangeListener,
                                   LegendChangeListener,
                                   PlotChangeListener {

    /** The chart title(s). */
    protected List titles;

    /** The chart legend. */
    protected Legend legend;

    /** Draws the visual representation of the data. */
    protected Plot plot;

    /** Flag that determines whether or not the chart is drawn with anti-aliasing. */
    protected boolean antialias;

    /** Paint used to draw the background of the chart. */
    protected Paint backgroundPaint;

    /** An optional background image for the chart. */
    protected Image backgroundImage;

    /** The alpha transparency for the background image. */
    protected float backgroundImageAlpha = 0.5f;

    /** Storage for registered change listeners. */
    protected List listeners;

    /**
     * Constructs a chart.
     * <P>
     * Note that the ChartFactory class contains static methods that will return a ready-made chart.
     *
     * @param plot Controller of the visual representation of the data.
     */
    public JFreeChart(Plot plot) {

        this(null, // title
             null, // font
             plot,
             false // create legend
             );

    }

    /**
     * Constructs a chart.
     * <P>
     * Note that the ChartFactory class contains static methods that will return a ready-made chart.
     *
     * @param title The main chart title.
     * @param titleFont The font for displaying the chart title.
     * @param plot Controller of the visual representation of the data.
     * @param createLegend Flag indicating whether or not a legend should be created for the chart.
     */
    public JFreeChart(String title, Font titleFont,
                      Plot plot,
                      boolean createLegend) {

        // create storage for listeners...
        listeners = new java.util.ArrayList();

        this.plot = plot;

        // the chart listens for changes in the plot...
        plot.addChangeListener(this);

        titles = new java.util.ArrayList();

        // create a legend, if requested...
        if (createLegend) {
            this.legend = Legend.createInstance(this);
            this.legend.addChangeListener(this);
        }

        this.antialias = true;
        this.backgroundPaint = DEFAULT_BACKGROUND_PAINT;

        // add the chart title, if one has been specified...
        if (title!=null) {
            if (titleFont==null) {
                titleFont = DEFAULT_TITLE_FONT;
            }
            TextTitle textTitle = new TextTitle(title, titleFont);
            textTitle.addChangeListener(this);
            titles.add(textTitle);
        }

    }

    /**
     * Returns a reference to the list of titles.
     *
     * @return A reference to the list of titles.
     */
    public List getTitles() {
        return this.titles;
    }

    /**
     * Sets the title list for the chart (completely replaces any existing titles).
     *
     * @param titles The new list of titles.
     */
    public void setTitles(List titles) {
        this.titles = titles;
        fireChartChanged();
    }

    /**
     * Returns the number of titles for the chart.
     *
     * @return The number of titles for the chart.
     */
    public int getTitleCount() {
        return this.titles.size();
    }

    /**
     * Returns a chart title.
     *
     * @param The index of the chart title (zero based).
     * @return A chart title.
     */
    public AbstractTitle getTitle(int index) {

        // check arguments...
        if ((index<0) || (index==this.getTitleCount())) {
            throw new IllegalArgumentException("JFreeChart.getTitle(...): index out of range.");
        }

        return (AbstractTitle)titles.get(index);

    }

    /**
     * Adds the chart title, and notifies registered listeners that the chart has been modified.
     *
     * @param title The chart title.
     */
    public void addTitle(AbstractTitle title) {

        if (title!=null) {
            this.titles.add(title);
            title.addChangeListener(this);
            fireChartChanged();
        }

    }

    /**
     * Returns the chart legend.
     *
     * @return The chart legend (possibly null).
     */
    public Legend getLegend() {
        return legend;
    }

    /**
     * Sets the chart legend.  Registered listeners are notified that the chart has been modified.
     *
     * @param legend The new chart legend (null permitted).
     */
    public void setLegend(Legend legend) {

        // if there is an existing legend, remove the chart from the list of change listeners...
        Legend existing = this.legend;
        if (existing!=null) {
            existing.removeChangeListener(this);
        }

        // set the new legend, and register the chart as a change listener...
        this.legend = legend;
        if (legend!=null) {
            legend.addChangeListener(this);
        }

        // notify chart change listeners...
        fireChartChanged();

    }

    /**
     * Returns the plot for the chart.  The plot is a class responsible for coordinating the visual
     * representation of the data, including the axes (if any).
     *
     * @return The plot.
     */
    public Plot getPlot() {
        return this.plot;
    }

    /**
     * Returns the plot cast as a CategoryPlot.  If the plot is not an instance of CategoryPlot,
     * then a ClassCastException is thrown.
     *
     * @return The plot.
     */
    public CategoryPlot getCategoryPlot() {
        return (CategoryPlot)this.plot;
    }

    /**
     * Returns the plot cast as an XYPlot.  If the plot is not an instance of XYPlot,
     * then a ClassCastException is thrown.
     *
     * @return The plot.
     */
    public XYPlot getXYPlot() {
        return (XYPlot)this.plot;
    }

    /**
     * Returns a flag that indicates whether or not anti-aliasing is used when the chart is drawn.
     *
     * @return The flag.
     */
    public boolean getAntiAlias() {
        return antialias;
    }

    /**
     * Sets a flag that indicates whether or not anti-aliasing is used when the chart is drawn.
     * <P>
     * Anti-aliasing can improve the appearance of charts.
     *
     * @param flag The new value of the flag.
     */
    public void setAntiAlias(boolean flag) {

        if (this.antialias!=flag) {
            this.antialias = flag;
            fireChartChanged();
        }

    }

    /**
     * Returns the color/shade used to fill the chart background.
     *
     * @return The color/shade used to fill the chart background.
     */
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    /**
     * Sets the color/shade used to fill the chart background.  All registered listeners are
     * notified that the chart has been changed.
     *
     * @param paint The new background color/shade.
     */
    public void setBackgroundPaint(Paint paint) {

        if (this.backgroundPaint!=null) {
            if (!this.backgroundPaint.equals(paint)) {
                this.backgroundPaint = paint;
                fireChartChanged();
            }
        }
        else {
            if (paint!=null) {
                this.backgroundPaint = paint;
                fireChartChanged();
            }
        }

    }

    /**
     * Returns the chart's background image (possibly null).
     *
     * @return The image.
     */
    public Image getBackgroundImage() {

        return this.backgroundImage;

    }

    /**
     * Sets the chart's background image (null permitted).  Registered listeners are notified
     * that the chart has been changed.
     *
     * @param image The image.
     */
    public void setBackgroundImage(Image image) {

        if (this.backgroundImage!=null) {
            if (!this.backgroundImage.equals(image)) {
                this.backgroundImage = image;
                fireChartChanged();
            }
        }
        else {
            if (image!=null) {
                this.backgroundImage = image;
                fireChartChanged();
            }
        }

    }

    /**
     * Returns the alpha-transparency for the chart's background image.
     *
     * @return The alpha-transparency.
     */
    public float getBackgroundImageAlpha() {

        return this.backgroundImageAlpha;

    }

    /**
     * Sets the alpha-transparency for the chart's background image.  Registered listeners are
     * notified that the chart has been changed.
     *
     * @param alpha The alpha value.
     */
    public void setBackgroundImageAlpha(float alpha) {

        if (this.backgroundImageAlpha!=alpha) {
            this.backgroundImageAlpha = alpha;
            fireChartChanged();
        }

    }

    /** A working rectangle for the title area (recycle the same object). */
    private Rectangle2D titleArea = new Rectangle2D.Double(0.0, 0.0, 0.0, 0.0);

    /** A working rectangle for the non-title area (recycle one object). */
    private Rectangle2D nonTitleArea = new Rectangle2D.Double(0.0, 0.0, 0.0, 0.0);

    /**
     * Draws the chart on a Java 2D graphics device (such as the screen or a printer).
     * <P>
     * This method is the focus of the entire JFreeChart library.
     * @param g2 The graphics device.
     * @param chartArea The area within which the chart should be drawn.
     * @param info Records info about the drawing (null means collect no info).
     */
    public void draw(Graphics2D g2, Rectangle2D chartArea, ChartRenderingInfo info) {

        // record the chart area, if info is requested...
        if (info!=null) {
            info.clear();
            info.setChartArea(chartArea);
        }

        // ensure no drawing occurs outside chart area...
        Shape savedClip = g2.getClip();
        g2.clip(chartArea);

        // set anti-alias...
        if (antialias) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
        }
        else {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        // draw the chart background...
        if (backgroundPaint!=null) {
            g2.setPaint(backgroundPaint);
            g2.fill(chartArea);
        }

        if (backgroundImage!=null) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       this.backgroundImageAlpha));
            g2.drawImage(this.backgroundImage,
                         (int)chartArea.getX(), (int)chartArea.getY(),
                         (int)chartArea.getWidth(), (int)chartArea.getHeight(), null);
            g2.setComposite(originalComposite);
        }

        // draw the titles...
        nonTitleArea.setRect(chartArea);

        Iterator iterator = titles.iterator();
        while (iterator.hasNext()) {
            AbstractTitle currentTitle = (AbstractTitle)iterator.next();
            switch (currentTitle.getPosition()) {

                case AbstractTitle.TOP : {
                    double availableHeight = Math.min(currentTitle.getPreferredHeight(g2),
                                                      nonTitleArea.getHeight());
                    double availableWidth = nonTitleArea.getWidth();
                    titleArea.setRect(nonTitleArea.getX(), nonTitleArea.getY(),
                                      availableWidth, availableHeight);
                    currentTitle.draw(g2, titleArea);
                    nonTitleArea.setRect(nonTitleArea.getX(),
                                         Math.min(nonTitleArea.getY()+availableHeight,
                                                  nonTitleArea.getMaxY()),
                                         availableWidth,
                                         Math.max(nonTitleArea.getHeight()-availableHeight, 0));
                    break;
                }

                case AbstractTitle.BOTTOM : {
                    double availableHeight = Math.min(currentTitle.getPreferredHeight(g2),
                                                      nonTitleArea.getHeight());
                    double availableWidth = nonTitleArea.getWidth();
                    titleArea.setRect(nonTitleArea.getX(), nonTitleArea.getMaxY()-availableHeight,
                                      availableWidth, availableHeight);
                    currentTitle.draw(g2, titleArea);
                    nonTitleArea.setRect(nonTitleArea.getX(), nonTitleArea.getY(),
                                         availableWidth, nonTitleArea.getHeight()-availableHeight);
                    break;
                }

                case AbstractTitle.RIGHT : {
                    double availableHeight = nonTitleArea.getHeight();
                    double availableWidth = Math.min(currentTitle.getPreferredWidth(g2),
                                                     nonTitleArea.getWidth());
                    titleArea.setRect(nonTitleArea.getMaxX()-availableWidth, nonTitleArea.getY(),
                                      availableWidth, availableHeight);
                    currentTitle.draw(g2, titleArea);
                    nonTitleArea.setRect(nonTitleArea.getX(), nonTitleArea.getY(),
                                         nonTitleArea.getWidth()-availableWidth, availableHeight);
                    break;
                }

                case AbstractTitle.LEFT : {
                    double availableHeight = nonTitleArea.getHeight();
                    double availableWidth = Math.min(currentTitle.getPreferredWidth(g2),
                                                     nonTitleArea.getWidth());
                    titleArea.setRect(nonTitleArea.getX(), nonTitleArea.getY(),
                                      availableWidth, availableHeight);
                    currentTitle.draw(g2, titleArea);
                    nonTitleArea.setRect(nonTitleArea.getX()+availableWidth, nonTitleArea.getY(),
                                         nonTitleArea.getWidth()-availableWidth, availableHeight);
                    break;
                }

                default :
                    throw new RuntimeException("JFreeChart.draw(...): unknown title position.");
            }
        }

        // draw the legend - the draw method will return the remaining area after the legend
        // steals a chunk of the non-title area for itself
        Rectangle2D plotArea = nonTitleArea;
        if (legend!=null) {
            plotArea.setRect(legend.draw(g2, nonTitleArea));
        }

        // draw the plot (axes and data visualisation)
        plot.draw(g2, plotArea, info);

        g2.setClip(savedClip);

    }

    /**
     * Creates and returns a buffered image into which the chart has been drawn.
     *
     * @param width The width.
     * @param height The height.
     */
    public BufferedImage createBufferedImage(int width, int height) {

        BufferedImage image = new BufferedImage(width , height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        this.draw(g2, new Rectangle2D.Double(0, 0, width, height), null);
        g2.dispose();
        return image;

    }

    /**
     * Handles a 'click' on the chart.
     * <P>
     * JFreeChart is not a UI component, so some other object (e.g. JFreeChartPanel) needs to
     * capture the click event and pass it onto the JFreeChart object.  If you are not using
     * JFreeChart in a client application, then this method is not required (and hopefully it
     * doesn't get in the way).
     */
    public void handleClick(int x, int y, ChartRenderingInfo info) {

        // pass the click on to the plot...
        // rely on the plot to post a plot change event and redraw the chart...
        this.plot.handleClick(x, y, info);

    }

    /**
     * Registers an object for notification of changes to the chart.
     *
     * @param listener The object being registered.
     */
    public void addChangeListener(ChartChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Deregisters an object for notification of changes to the chart.
     *
     * @param listener The object being deregistered.
     */
    public void removeChangeListener(ChartChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Sends a default ChartChangeEvent to all registered listeners.
     * <P>
     * This method is for convenience only.
     */
    protected void fireChartChanged() {
        ChartChangeEvent event = new ChartChangeEvent(this);
        notifyListeners(event);
    }

    /**
     * Sends a ChartChangeEvent to all registered listeners.
     *
     * @param event Information about the event that triggered the notification.
     */
    protected void notifyListeners(ChartChangeEvent event) {
        Iterator iterator = listeners.iterator();
        while (iterator.hasNext()) {
            ChartChangeListener listener = (ChartChangeListener)iterator.next();
            listener.chartChanged(event);
        }
    }

    /**
     * Receives notification that a chart title has changed, and passes this on to registered
     * listeners.
     *
     * @param event Information about the chart title change.
     */
    public void titleChanged(TitleChangeEvent event) {
        event.setChart(this);
        notifyListeners(event);
    }

    /**
     * Receives notification that the chart legend has changed, and passes this on to registered
     * listeners.
     *
     * @param event Information about the chart legend change.
     */
    public void legendChanged(LegendChangeEvent event) {
        event.setChart(this);
        notifyListeners(event);
    }

    /**
     * Receives notification that the plot has changed, and passes this on to registered listeners.
     *
     * @param event Information about the plot change.
     */
    public void plotChanged(PlotChangeEvent event) {

        event.setChart(this);
        notifyListeners(event);
    }

    /**
     * Prints information about JFreeChart to standard output.
     */
    public static void main(String[] args) {

        System.out.println(JFreeChart.NAME+" version "+JFreeChart.VERSION+".");
        System.out.println(JFreeChart.COPYRIGHT+".");
        System.out.println("For terms of use, see the licence below.");
        System.out.println();
        System.out.println("Further information:");
        System.out.println(JFreeChart.INFO);
        System.out.println();
        System.out.println("Contributors:");
        Iterator iterator = JFreeChart.CONTRIBUTORS.iterator();
        while (iterator.hasNext()) {
            Contributor contributor = (Contributor)(iterator.next());
            System.out.println(contributor.getName()+" ("+contributor.getEmail()+").");
        }
        System.out.println();
        System.out.println("Other libraries:");
        iterator = JFreeChart.LIBRARIES.iterator();
        while (iterator.hasNext()) {
            Library lib = (Library)(iterator.next());
            System.out.println(lib.getName()+" "+lib.getVersion()+" ("+lib.getInfo()+").");
        }

        System.out.println();
        System.out.println(JFreeChart.LICENCE);
    }

}
