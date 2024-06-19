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
 * ---------------
 * JFreeChart.java
 * ---------------
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   David Li;
 *                   Wolfgang Irler;
 *                   Christian W. Zuckschwerdt;
 *                   Klaus Rheinwald;
 *
 * $Id: JFreeChart.java,v 1.1 2007/10/10 20:00:07 vauchers Exp $
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
 * 13-Jun-2002 : Added an extra draw(...) method (DG);
 * 25-Jun-2002 : Implemented the Drawable interface and removed redundant imports (DG);
 * 26-Jun-2002 : Added another createBufferedImage(...) method (DG);
 * 18-Sep-2002 : Fixed issues reported by Checkstyle (DG);
 * 23-Sep-2002 : Added new contributor (DG);
 * 28-Oct-2002 : Created main title and subtitle list to replace existing title list (DG);
 * 08-Jan-2003 : Added contributor (DG);
 * 17-Jan-2003 : Added new constructor (DG);
 * 22-Jan-2003 : Added ChartColor class by Cameron Riley, and background image alignment code by
 *               Christian W. Zuckschwerdt (DG);
 * 11-Feb-2003 : Added flag to allow suppression of chart change events, based on a suggestion by
 *               Klaus Rheinwald (DG);
 * 04-Mar-2003 : Added small fix for suppressed chart change events (see bug id 690865) (DG);
 * 10-Mar-2003 : Added Benoit Xhenseval to contributors (DG);
 * 26-Mar-2003 : Implemented Serializable (DG); 
 */

package com.jrefinery.chart;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.event.EventListenerList;

import com.jrefinery.JCommon;
import com.jrefinery.chart.event.ChartChangeEvent;
import com.jrefinery.chart.event.ChartChangeListener;
import com.jrefinery.chart.event.ChartProgressEvent;
import com.jrefinery.chart.event.ChartProgressListener;
import com.jrefinery.chart.event.LegendChangeEvent;
import com.jrefinery.chart.event.LegendChangeListener;
import com.jrefinery.chart.event.PlotChangeEvent;
import com.jrefinery.chart.event.PlotChangeListener;
import com.jrefinery.chart.event.TitleChangeEvent;
import com.jrefinery.chart.event.TitleChangeListener;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.chart.plot.Plot;
import com.jrefinery.chart.plot.XYPlot;
import com.jrefinery.io.SerialUtilities;
import com.jrefinery.ui.Align;
import com.jrefinery.ui.Drawable;
import com.jrefinery.ui.about.Contributor;
import com.jrefinery.ui.about.Library;
import com.jrefinery.ui.about.Licences;
import com.jrefinery.ui.about.ProjectInfo;
import com.jrefinery.util.ObjectUtils;

/**
 * A chart class implemented using the Java 2D APIs.  The current version
 * supports bar charts, line charts, pie charts and xy plots (including time
 * series data).
 * <P>
 * JFreeChart coordinates several objects to achieve its aim of being able to
 * draw a chart on a Java 2D graphics device: a list of {@link AbstractTitle} objects, a 
 * {@link Legend}, a {@link Plot} and a {@link com.jrefinery.data.Dataset} (the plot in 
 * turn manages a horizontal axis and a vertical axis).
 * <P>
 * You should use a {@link ChartPanel} to display a chart in a GUI.
 * <P>
 * The {@link ChartFactory} class contains static methods for creating 'ready-made' charts.
 *
 * @see ChartPanel
 * @see ChartFactory
 * @see AbstractTitle
 * @see Legend
 * @see Plot
 *
 * @author David Gilbert
 *
 */
public class JFreeChart implements JFreeChartConstants,
                                   Drawable,
                                   TitleChangeListener,
                                   LegendChangeListener,
                                   PlotChangeListener, 
                                   Serializable {

    /** Information about the project. */
    public static final ProjectInfo INFO = new JFreeChartInfo();

    /** The chart title (optional). */
    private TextTitle title;

    /** The chart subtitles (zero, one or many). */
    private List subtitles;

    /** The chart legend. */
    private Legend legend;

    /** Draws the visual representation of the data. */
    private Plot plot;

    /** Flag that determines whether or not the chart is drawn with anti-aliasing. */
    private boolean antialias;

    /** Paint used to draw the background of the chart. */
    private transient Paint backgroundPaint;

    /** An optional background image for the chart. */
    private transient Image backgroundImage;  // todo: not serialized yet

    /** The alignment for the background image. */
    private int backgroundImageAlignment = Align.FIT;
    
    /** The alpha transparency for the background image. */
    private float backgroundImageAlpha = 0.5f;

    /** Storage for registered change listeners. */
    private transient EventListenerList changeListeners;

    /** Storage for registered progress listeners. */
    private transient EventListenerList progressListeners;

    /** A flag that can be used to enable/disable notification of chart change events. */
    private boolean notify;
    
    /**
     * Constructs a chart.
     * <P>
     * Note that the ChartFactory class contains static methods that will
     * return a ready-made chart.
     *
     * @param plot  controller of the visual representation of the data.
     */
    public JFreeChart(Plot plot) {

        this(null, // title
             null, // font
             plot,
             false // create legend
             );

    }
    
    /**
     * Creates a new chart.
     * 
     * @param title  the chart title.
     * @param plot  the plot.
     */
    public JFreeChart(String title, Plot plot) {
        this(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
    }

    /**
     * Constructs a chart.
     * <P>
     * Note that the ChartFactory class contains static methods that will
     * return a ready-made chart.
     *
     * @param title  the main chart title.
     * @param titleFont  the font for displaying the chart title.
     * @param plot  controller of the visual representation of the data.
     * @param createLegend  a flag indicating whether or not a legend should
     *                      be created for the chart.
     */
    public JFreeChart(String title, Font titleFont, Plot plot, boolean createLegend) {

        // create storage for listeners...
        this.progressListeners = new EventListenerList();
        this.changeListeners = new EventListenerList();
        this.notify = true;  // default is to notify listeners when the chart changes
        
        this.plot = plot;
        plot.addChangeListener(this);
        
        this.subtitles = new java.util.ArrayList();

        // create a legend, if requested...
        if (createLegend) {
            this.legend = Legend.createInstance(this);
            this.legend.addChangeListener(this);
        }

        // add the chart title, if one has been specified...
        if (title != null) {
            if (titleFont == null) {
                titleFont = DEFAULT_TITLE_FONT;
            }
            this.title = new TextTitle(title, titleFont);
            this.title.addChangeListener(this);
        }

        this.antialias = true;
        this.backgroundPaint = DEFAULT_BACKGROUND_PAINT;

        this.backgroundImage = DEFAULT_BACKGROUND_IMAGE;    
        this.backgroundImageAlignment = DEFAULT_BACKGROUND_IMAGE_ALIGNMENT;
        this.backgroundImageAlpha = DEFAULT_BACKGROUND_IMAGE_ALPHA;
                                     
    }

    /**
     * Returns the chart title.
     *
     * @return the chart title.
     */
    public TextTitle getTitle() {
        return this.title;
    }

    /**
     * Sets the title for the chart.
     *
     * @param title  the new title.
     */
    public void setTitle(TextTitle title) {
        this.title = title;
        fireChartChanged();
    }

    /**
     * Sets the chart title.
     *
     * @param title  the new title.
     */
    public void setTitle(String title) {

        if (title != null) {
            if (this.title == null) {
                setTitle(new TextTitle(title, JFreeChart.DEFAULT_TITLE_FONT));
            }
            else {
                this.title.setText(title);
            }
        }
        else {
            setTitle((TextTitle) null);
        }

    }

    /**
     * Returns the list of subtitles.
     *
     * @return the subtitle list.
     */
    public List getSubtitles() {
        return this.subtitles;
    }

    /**
     * Sets the title list for the chart (completely replaces any existing titles).
     *
     * @param subtitles  the new list of subtitles.
     */
    public void setSubtitles(List subtitles) {
        this.subtitles = subtitles;
        fireChartChanged();
    }

    /**
     * Returns the number of titles for the chart.
     *
     * @return  the number of titles for the chart.
     */
    public int getSubtitleCount() {
        return this.subtitles.size();
    }

    /**
     * Returns a chart subtitle.
     *
     * @param index  the index of the chart subtitle (zero based).
     *
     * @return a chart subtitle.
     */
    public AbstractTitle getSubtitle(int index) {

        // check arguments...
        if ((index < 0) || (index == getSubtitleCount())) {
            throw new IllegalArgumentException("JFreeChart.getSubtitle(...): index out of range.");
        }

        return (AbstractTitle) this.subtitles.get(index);

    }

    /**
     * Adds a chart subtitle, and notifies registered listeners that the chart has been modified.
     *
     * @param subtitle  the subtitle.
     */
    public void addSubtitle(AbstractTitle subtitle) {

        if (subtitle != null) {
            this.subtitles.add(subtitle);
            subtitle.addChangeListener(this);
            fireChartChanged();
        }

    }

    /**
     * Returns the chart legend.
     *
     * @return the chart legend (possibly null).
     */
    public Legend getLegend() {
        return legend;
    }

    /**
     * Sets the chart legend.  Registered listeners are notified that the chart
     * has been modified.
     *
     * @param legend  the new chart legend (null permitted).
     */
    public void setLegend(Legend legend) {

        // if there is an existing legend, remove the chart from the list of
        // change listeners...
        Legend existing = this.legend;
        if (existing != null) {
            existing.removeChangeListener(this);
        }

        // set the new legend, and register the chart as a change listener...
        this.legend = legend;
        if (legend != null) {
            legend.addChangeListener(this);
        }

        // notify chart change listeners...
        fireChartChanged();

    }

    /**
     * Returns the plot for the chart.  The plot is a class responsible for
     * coordinating the visual representation of the data, including the axes
     * (if any).
     *
     * @return the plot.
     */
    public Plot getPlot() {
        return this.plot;
    }

    /**
     * Returns the plot cast as a CategoryPlot.  If the plot is not an instance
     * of CategoryPlot, then a ClassCastException is thrown.
     *
     * @return the plot.
     */
    public CategoryPlot getCategoryPlot() {
        return (CategoryPlot) this.plot;
    }

    /**
     * Returns the plot cast as an XYPlot.  If the plot is not an instance of
     * XYPlot, then a ClassCastException is thrown.
     *
     * @return the plot.
     */
    public XYPlot getXYPlot() {
        return (XYPlot) this.plot;
    }

    /**
     * Returns a flag that indicates whether or not anti-aliasing is used when
     * the chart is drawn.
     *
     * @return the flag.
     */
    public boolean getAntiAlias() {
        return antialias;
    }

    /**
     * Sets a flag that indicates whether or not anti-aliasing is used when the
     * chart is drawn.
     * <P>
     * Anti-aliasing usually improves the appearance of charts.
     *
     * @param flag  the new value of the flag.
     */
    public void setAntiAlias(boolean flag) {

        if (this.antialias != flag) {
            this.antialias = flag;
            fireChartChanged();
        }

    }

    /**
     * Returns the color/shade used to fill the chart background.
     *
     * @return the color/shade used to fill the chart background.
     */
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    /**
     * Sets the color/shade used to fill the chart background.  All registered
     * listeners are notified that the chart has been changed.
     *
     * @param paint  the new background color/shade.
     */
    public void setBackgroundPaint(Paint paint) {

        if (this.backgroundPaint != null) {
            if (!this.backgroundPaint.equals(paint)) {
                this.backgroundPaint = paint;
                fireChartChanged();
            }
        }
        else {
            if (paint != null) {
                this.backgroundPaint = paint;
                fireChartChanged();
            }
        }

    }

    /**
     * Returns the chart's background image (possibly null).
     *
     * @return the image.
     */
    public Image getBackgroundImage() {

        return this.backgroundImage;

    }

    /**
     * Sets the chart's background image (null permitted). Registered listeners
     * are notified that the chart has been changed.
     *
     * @param image  the image.
     */
    public void setBackgroundImage(Image image) {

        if (this.backgroundImage != null) {
            if (!this.backgroundImage.equals(image)) {
                this.backgroundImage = image;
                fireChartChanged();
            }
        }
        else {
            if (image != null) {
                this.backgroundImage = image;
                fireChartChanged();
            }
        }

    }

    /**
     * Returns the background image alignment. Alignment constants are defined in the 
     * <code>com.jrefinery.ui.Align</code> class in the JCommon class library.
     * 
     * @return The alignment.
     */
    public int getBackgroundImageAlignment() {
        return this.backgroundImageAlignment;
    }
    
    /**
     * Sets the background alignment.
     * <p>
     * Alignment options are defined by the {@link com.jrefinery.ui.Align} class.
     * 
     * @param alignment  the alignment.
     */
    public void setBackgroundImageAlignment(int alignment) {
        if (this.backgroundImageAlignment != alignment) {
            this.backgroundImageAlignment = alignment;
            fireChartChanged();
        }
    }
    
    /**
     * Returns the alpha-transparency for the chart's background image.
     *
     * @return the alpha-transparency.
     */
    public float getBackgroundImageAlpha() {

        return this.backgroundImageAlpha;

    }

    /**
     * Sets the alpha-transparency for the chart's background image.
     * Registered listeners are notified that the chart has been changed.
     *
     * @param alpha  the alpha value.
     */
    public void setBackgroundImageAlpha(float alpha) {

        if (this.backgroundImageAlpha != alpha) {
            this.backgroundImageAlpha = alpha;
            fireChartChanged();
        }

    }

    /** 
     * Returns a flag that controls whether or not change events are sent to registered listeners.
     * 
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean isNotify() {
        return this.notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
        // if the flag is being set to true, there may be queued up changes...
        if (notify) {
            notifyListeners(new ChartChangeEvent(this));
        }
    }
        
    /**
     * Draws the chart on a Java 2D graphics device (such as the screen or a
     * printer).
     * <P>
     * This method is the focus of the entire JFreeChart library.
     *
     * @param g2  the graphics device.
     * @param area  the area within which the chart should be drawn.
     */
    public void draw(Graphics2D g2, Rectangle2D area) {
        draw(g2, area, null);
    }

    /**
     * Draws the chart on a Java 2D graphics device (such as the screen or a
     * printer).
     * <P>
     * This method is the focus of the entire JFreeChart library.
     *
     * @param g2  the graphics device.
     * @param chartArea  the area within which the chart should be drawn.
     * @param info  records info about the drawing (null means collect no info).
     */
    public void draw(Graphics2D g2, Rectangle2D chartArea, ChartRenderingInfo info) {

        notifyListeners(new ChartProgressEvent(this, this, ChartProgressEvent.DRAWING_STARTED, 0));
        
        // record the chart area, if info is requested...
        if (info != null) {
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
        if (backgroundPaint != null) {
            g2.setPaint(backgroundPaint);
            g2.fill(chartArea);
        }

        if (backgroundImage != null) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       this.backgroundImageAlpha));
            Rectangle2D dest = new Rectangle2D.Double(0.0, 0.0, 
                                                      backgroundImage.getWidth(null),
                                                      backgroundImage.getHeight(null));
            Align.align(dest, chartArea, this.backgroundImageAlignment);
            g2.drawImage(this.backgroundImage,
                         (int) dest.getX(), (int) dest.getY(),
                         (int) dest.getWidth(), (int) dest.getHeight(),
                         null);
            g2.setComposite(originalComposite);
        }

        // draw the title and subtitles...
        Rectangle2D nonTitleArea = new Rectangle2D.Double();
        nonTitleArea.setRect(chartArea);

        if (this.title != null) {
            drawTitle(this.title, g2, nonTitleArea);
        }

        Iterator iterator = this.subtitles.iterator();
        while (iterator.hasNext()) {
            AbstractTitle currentTitle = (AbstractTitle) iterator.next();
            drawTitle(currentTitle, g2, nonTitleArea);
        }

        // draw the legend - the draw method will return the remaining area
        // after the legend steals a chunk of the non-title area for itself
        Rectangle2D plotArea = nonTitleArea;
        if (legend != null) {
            plotArea.setRect(legend.draw(g2, nonTitleArea));
        }

        // draw the plot (axes and data visualisation)
        plot.draw(g2, plotArea, info);

        g2.setClip(savedClip);

        notifyListeners(
            new ChartProgressEvent(this, this, ChartProgressEvent.DRAWING_FINISHED, 100)
        );

    }

    /**
     * Draws a title.
     * <P>
     * The title should be drawn at the top, bottom, left or right of the nonTitleArea, and
     * the area should be updated to reflect the amount of space used by the title.
     *
     * @param title  the title.
     * @param g2  the graphics device.
     * @param nonTitleArea  the area.
     */
    public void drawTitle(AbstractTitle title, Graphics2D g2, Rectangle2D nonTitleArea) {

        Rectangle2D titleArea = new Rectangle2D.Double();
        switch (title.getPosition()) {

            case AbstractTitle.TOP : {
                double availableHeight = Math.min(title.getPreferredHeight(g2),
                                                  nonTitleArea.getHeight());
                double availableWidth = nonTitleArea.getWidth();
                titleArea.setRect(nonTitleArea.getX(), nonTitleArea.getY(),
                                  availableWidth, availableHeight);
                title.draw(g2, titleArea);
                nonTitleArea.setRect(nonTitleArea.getX(),
                    Math.min(nonTitleArea.getY() + availableHeight,
                             nonTitleArea.getMaxY()),
                             availableWidth,
                             Math.max(nonTitleArea.getHeight() - availableHeight, 0));
                break;
            }

            case AbstractTitle.BOTTOM : {
                double availableHeight =
                    Math.min(title.getPreferredHeight(g2),
                        nonTitleArea.getHeight());
                double availableWidth = nonTitleArea.getWidth();
                titleArea.setRect(nonTitleArea.getX(),
                                  nonTitleArea.getMaxY() - availableHeight,
                                  availableWidth, availableHeight);
                title.draw(g2, titleArea);
                nonTitleArea.setRect(nonTitleArea.getX(), nonTitleArea.getY(),
                                     availableWidth,
                                     nonTitleArea.getHeight() - availableHeight);
                break;
            }

            case AbstractTitle.RIGHT : {
                double availableHeight = nonTitleArea.getHeight();
                double availableWidth =
                    Math.min(title.getPreferredWidth(g2),
                        nonTitleArea.getWidth());
                titleArea.setRect(nonTitleArea.getMaxX() - availableWidth,
                                  nonTitleArea.getY(), availableWidth, availableHeight);
                title.draw(g2, titleArea);
                nonTitleArea.setRect(nonTitleArea.getX(), nonTitleArea.getY(),
                                     nonTitleArea.getWidth() - availableWidth,
                                     availableHeight);
                break;
            }

            case AbstractTitle.LEFT : {
                double availableHeight = nonTitleArea.getHeight();
                double availableWidth = Math.min(title.getPreferredWidth(g2),
                                                 nonTitleArea.getWidth());
                titleArea.setRect(nonTitleArea.getX(), nonTitleArea.getY(),
                                  availableWidth, availableHeight);
                title.draw(g2, titleArea);
                nonTitleArea.setRect(nonTitleArea.getX() + availableWidth,
                                     nonTitleArea.getY(),
                                     nonTitleArea.getWidth() - availableWidth,
                                     availableHeight);
                break;
            }

            default :
                throw new RuntimeException("JFreeChart.draw(...): unknown title position.");
        }
    }

    /**
     * Creates and returns a buffered image into which the chart has been drawn.
     *
     * @param width  the width.
     * @param height  the height.
     *
     * @return a buffered image.
     */
    public BufferedImage createBufferedImage(int width, int height) {

        return createBufferedImage(width, height, null);

    }

    /**
     * Creates and returns a buffered image into which the chart has been drawn.
     *
     * @param width  the width.
     * @param height  the height.
     * @param info  optional object for collection chart dimension and entity information.
     *
     * @return a buffered image.
     */
    public BufferedImage createBufferedImage(int width, int height, ChartRenderingInfo info) {

        BufferedImage image = new BufferedImage(width , height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        draw(g2, new Rectangle2D.Double(0, 0, width, height), info);
        g2.dispose();
        return image;

    }

    /**
     * Handles a 'click' on the chart.
     * <P>
     * JFreeChart is not a UI component, so some other object (e.g. ChartPanel)
     * needs to capture the click event and pass it onto the JFreeChart object.
     * If you are not using JFreeChart in a client application, then this
     * method is not required (and hopefully it doesn't get in the way).
     *
     * @param x  x-coordinate of the click.
     * @param y  y-coordinate of the click.
     * @param info  optional object for collection chart dimension and entity information.
     */
    public void handleClick(int x, int y, ChartRenderingInfo info) {

        // pass the click on to the plot...
        // rely on the plot to post a plot change event and redraw the chart...
        this.plot.handleClick(x, y, info);

    }

    /**
     * Registers an object for notification of changes to the chart.
     *
     * @param listener  the object being registered.
     */
    public void addChangeListener(ChartChangeListener listener) {
        this.changeListeners.add(ChartChangeListener.class, listener);
    }

    /**
     * Deregisters an object for notification of changes to the chart.
     *
     * @param listener  the object being deregistered.
     */
    public void removeChangeListener(ChartChangeListener listener) {
        this.changeListeners.remove(ChartChangeListener.class, listener);
    }

    /**
     * Sends a default {@link ChartChangeEvent} to all registered listeners.
     * <P>
     * This method is for convenience only.
     */
    protected void fireChartChanged() {
        ChartChangeEvent event = new ChartChangeEvent(this);
        notifyListeners(event);
    }

    /**
     * Sends a {@link ChartChangeEvent} to all registered listeners.
     *
     * @param event  information about the event that triggered the notification.
     */
    protected void notifyListeners(ChartChangeEvent event) {

        if (this.notify) {
            Object[] listeners = this.changeListeners.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == ChartChangeListener.class) {
                    ((ChartChangeListener) listeners[i + 1]).chartChanged(event);
                }
            }
        }

    }

    /**
     * Registers an object for notification of progress events relating to the chart.
     *
     * @param listener  the object being registered.
     */
    public void addProgressListener(ChartProgressListener listener) {
        this.progressListeners.add(ChartProgressListener.class, listener);
    }

    /**
     * Deregisters an object for notification of changes to the chart.
     *
     * @param listener  the object being deregistered.
     */
    public void removeProgressListener(ChartProgressListener listener) {
        this.progressListeners.remove(ChartProgressListener.class, listener);
    }

    /**
     * Sends a {@link ChartProgressEvent} to all registered listeners.
     *
     * @param event  information about the event that triggered the notification.
     */
    protected void notifyListeners(ChartProgressEvent event) {

        Object[] listeners = this.progressListeners.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChartProgressListener.class) {
                ((ChartProgressListener) listeners[i + 1]).chartProgress(event);
            }
        }

    }

    /**
     * Receives notification that a chart title has changed, and passes this
     * on to registered listeners.
     *
     * @param event  information about the chart title change.
     */
    public void titleChanged(TitleChangeEvent event) {
        event.setChart(this);
        notifyListeners(event);
    }

    /**
     * Receives notification that the chart legend has changed, and passes this
     * on to registered listeners.
     *
     * @param event  information about the chart legend change.
     */
    public void legendChanged(LegendChangeEvent event) {
        event.setChart(this);
        notifyListeners(event);
    }

    /**
     * Receives notification that the plot has changed, and passes this on to
     * registered listeners.
     *
     * @param event  information about the plot change.
     */
    public void plotChanged(PlotChangeEvent event) {

        event.setChart(this);
        notifyListeners(event);
    }

    /**
     * Tests this chart for equality with another object.
     * 
     * @param obj  the object.
     * 
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {
        
        if (obj == null) {
            return false;
        }
        
        if (obj == this) {
            return true;
        }
        
        if (obj instanceof JFreeChart) {
            
            JFreeChart c = (JFreeChart) obj;
            boolean b0 = ObjectUtils.equalOrBothNull(this.title, c.title);
            boolean b1 = ObjectUtils.equalOrBothNull(this.subtitles, c.subtitles);
            boolean b2 = ObjectUtils.equalOrBothNull(this.legend, c.legend);
            boolean b3 = ObjectUtils.equalOrBothNull(this.plot, c.plot);
            boolean b4 = (this.antialias == c.antialias);
            boolean b5 = ObjectUtils.equalOrBothNull(this.backgroundPaint, c.backgroundPaint);
            boolean b6 = ObjectUtils.equalOrBothNull(this.backgroundImage, c.backgroundImage);
            boolean b7 = (this.backgroundImageAlignment == c.backgroundImageAlignment);
            boolean b8 = (this.backgroundImageAlpha == c.backgroundImageAlpha);
            boolean b9 = (this.notify == c.notify);

            return b0 && b1 && b2 && b3 && b4 && b5 && b6 && b7 && b8 && b9;    
            
        }
        
        return false;
        
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
        SerialUtilities.writePaint(this.backgroundPaint, stream);
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
        this.backgroundPaint = SerialUtilities.readPaint(stream);
        this.progressListeners = new EventListenerList();
        this.changeListeners = new EventListenerList();
        
        // register as a listener with sub-components...        
        if (this.title != null) {
            this.title.addChangeListener(this);
        }

        for (int i = 0; i < getSubtitleCount(); i++) {
            getSubtitle(i).addChangeListener(this);
        }
        
        if (this.legend != null) {
            this.legend.addChangeListener(this);
        }
        
        if (this.plot != null) {
            this.plot.addChangeListener(this);
        }
        
    }
    
    /**
     * Prints information about JFreeChart to standard output.
     *
     * @param args  no arguments are honored.
     */
    public static void main(String[] args) {

        System.out.println(JFreeChart.INFO.toString());

    }

    /**
     * Returns the flag that controls whether notification of chart change events is
     * suppressed.
     * 
     * @return The flag.
     * 
     * @deprecated Use isNotify() instead.
     */
    public boolean getSuppressChartChangeEvents() {
        return isNotify();
    }
    
    /**
     * Sets a flag that is used to suppress notification of chart change events.
     * 
     * @param flag  the flag.
     * 
     * @deprecated Use setNotify(boolean) instead.
     */
    public void setSuppressChartChangeEvents(boolean flag) {
        setNotify(!flag);
    }
    
}

/**
 * Information about the JCommon project.  One instance of this class is assigned to
 * <code>JFreeChart.INFO<code>.
 *
 * @author David Gilbert
 */
class JFreeChartInfo extends ProjectInfo {

    /** Default constructor. */
    public JFreeChartInfo() {

        // get a locale-specific resource bundle...
        String baseResourceClass = "com.jrefinery.chart.resources.JFreeChartResources";
        ResourceBundle resources = ResourceBundle.getBundle(baseResourceClass);

        setName(resources.getString("project.name"));
        setVersion(resources.getString("project.version"));
        setInfo(resources.getString("project.info"));
        setCopyright(resources.getString("project.copyright"));
        setLogo(null);  // load only when required
        setLicenceName("LGPL");
        setLicenceText(Licences.LGPL);

        setContributors(Arrays.asList(
            new Contributor[] {
                new Contributor("Richard Atkinson", "richard_c_atkinson@ntlworld.com"),
                new Contributor("David Berry", "-"),
                new Contributor("Anthony Boulestreau", "-"),
                new Contributor("Jeremy Bowman", "-"),
                new Contributor("Søren Caspersen", "-"),
                new Contributor("Chuanhao Chiu", "-"),
                new Contributor("Pascal Collet", "-"),
                new Contributor("Martin Cordova", "-"),
                new Contributor("Paolo Cova", "-"),
                new Contributor("Mike Duffy", "-"),
                new Contributor("Jonathan Gabbai", "-"),
                new Contributor("David Gilbert", "david.gilbert@object-refinery.com"),
                new Contributor("Serge V. Grachov", "-"),
                new Contributor("Joao Guilherme Del Valle", "-"),
                new Contributor("Hans-Jurgen Greiner", "-"),
                new Contributor("Aiman Han", "-"),
                new Contributor("Jon Iles", "-"),
                new Contributor("Wolfgang Irler", "-"),
                new Contributor("Xun Kang", "-"),
                new Contributor("Bill Kelemen", "-"),
                new Contributor("Norbert Kiesel", "-"),
                new Contributor("Gideon Krause", "-"),
                new Contributor("David Li", "-"),
                new Contributor("Tin Luu", "-"),
                new Contributor("Craig MacFarlane", "-"),
                new Contributor("Achilleus Mantzios", "-"),
                new Contributor("Thomas Meier", "-"),
                new Contributor("Jim Moore", "-"),
                new Contributor("Jonathan Nash", "-"),
                new Contributor("David M. O'Donnell", "-"),
                new Contributor("Krzysztof Paz", "-"),
                new Contributor("Tomer Peretz", "-"),
                new Contributor("Andrzej Porebski", "-"),
                new Contributor("Xavier Poinsard", "-"),
                new Contributor("Viktor Rajewski", "-"),
                new Contributor("Michael Rauch", "-"),
                new Contributor("Cameron Riley", "-"),
                new Contributor("Dan Rivett", "d.rivett@ukonline.co.uk"),
                new Contributor("Thierry Saura", "-"),
                new Contributor("Andreas Schneider", "-"),
                new Contributor("Jean-Luc SCHWAB", "-"),
                new Contributor("Bryan Scott", "-"),
                new Contributor("Roger Studner", "-"),
                new Contributor("Irv Thomae", "-"),
                new Contributor("Eric Thomas", "-"),
                new Contributor("Rich Unger", "-"),
                new Contributor("Daniel van Enckevort", "-"),
                new Contributor("Laurence Vanhelsuwe", "-"),
                new Contributor("Sylvain Vieujot", "-"),
                new Contributor("Mark Watson", "www.markwatson.com"),
                new Contributor("Alex Weber", "-"),
                new Contributor("Matthew Wright", "-"),
                new Contributor("Benoit Xhenseval", "-"), 
                new Contributor("Christian W. Zuckschwerdt", 
                                "Christian.Zuckschwerdt@Informatik.Uni-Oldenburg.de"),
                new Contributor("Hari", "-"),
                new Contributor("Sam (oldman)", "-"),
            }
        ));

        setLibraries(Arrays.asList(
            new Library[] {
                new Library(JCommon.INFO)
            }
        ));

    }

    /**
     * Returns the JFreeChart logo (a picture of a gorilla).
     *
     * @return  the JFreeChart logo.
     */
    public Image getLogo() {

        Image logo = super.getLogo();
        if (logo == null) {
            URL imageURL = ClassLoader.getSystemResource("com/jrefinery/chart/gorilla.jpg");
            if (imageURL != null) {
                ImageIcon temp = new ImageIcon(imageURL);  // use ImageIcon because it waits for
                                                           // the image to load...
                logo = temp.getImage();
                setLogo(logo);
            }
        }
        return logo;

    }

}
