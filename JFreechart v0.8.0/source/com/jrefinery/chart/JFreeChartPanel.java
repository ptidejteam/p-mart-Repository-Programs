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
 * --------------------
 * JFreeChartPanel.java
 * --------------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   Søren Caspersen;
 *                   Jonathan Nash;
 *                   Hans-Jurgen Greiner;
 *
 * $Id: JFreeChartPanel.java,v 1.1 2007/10/10 18:59:09 vauchers Exp $
 *
 * Changes (from 28-Jun-2001)
 * --------------------------
 * 28-Jun-2001 : Integrated buffering code contributed by Søren Caspersen (DG);
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 22-Nov-2001 : Added scaling to improve display of charts in small sizes (DG);
 * 26-Nov-2001 : Added property editing, saving and printing (DG);
 * 11-Dec-2001 : Transferred saveChartAsPNG method to new ChartUtilities class (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 16-Jan-2002 : Added an optional crosshair, based on the implementation by Jonathan Nash.
 *               Renamed the tooltips class (DG);
 * 23-Jan-2002 : Implemented zooming based on code by Hans-Jurgen Greiner (DG);
 * 05-Feb-2002 : Improved tooltips setup.  Renamed method attemptSaveAs()-->doSaveAs() and made
 *               it public rather than private (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import com.jrefinery.ui.*;
import com.jrefinery.chart.event.*;
import com.jrefinery.chart.tooltips.*;
import com.jrefinery.chart.ui.*;

/**
 * A Swing GUI component for displaying a JFreeChart.
 * <P>
 * The panel is registered to receive notification of changes to the chart, so that the chart can
 * be redrawn automatically as required.
 */
public class JFreeChartPanel extends JPanel implements ActionListener,
                                                       MouseListener,
                                                       Printable,
                                                       ChartChangeListener {

    /** Default setting for buffer usage. */
    public static final boolean DEFAULT_BUFFER_USED = true;

    /** The default panel width. */
    public static final int DEFAULT_WIDTH = 680;

    /** The default panel height. */
    public static final int DEFAULT_HEIGHT = 420;

    /** The default limit below which chart scaling kicks in. */
    public static final double WIDTH_SCALING_THRESHOLD = 300;

    /** The default limit below which chart scaling kicks in. */
    public static final double HEIGHT_SCALING_THRESHOLD = 200;

    /** Properties action command. */
    public static final String PROPERTIES_ACTION_COMMAND = "PROPERTIES";

    /** Save action command. */
    public static final String SAVE_ACTION_COMMAND = "SAVE";

    /** Print action command. */
    public static final String PRINT_ACTION_COMMAND = "PRINT";

    /** Zoom in action command. */
    public static final String ZOOM_IN_ACTION_COMMAND = "ZOOM_IN";

    /** Zoom out action command. */
    public static final String ZOOM_OUT_ACTION_COMMAND = "ZOOM_OUT";

    /** Zoom reset action command. */
    public static final String ZOOM_AUTO_ACTION_COMMAND = "ZOOM_RESET";

    /** The chart that is displayed in the panel. */
    protected JFreeChart chart;

    /** A flag that controls whether or not the off-screen buffer is used. */
    protected boolean useBuffer;

    /** A flag that indicates that the buffer should be refreshed. */
    protected boolean refreshBuffer;

    /** A buffer for the rendered chart. */
    protected Image chartBuffer;

    /** The height of the chart buffer. */
    protected int chartBufferHeight;

    /** The width of the chart buffer. */
    protected int chartBufferWidth;

    /** The minimum area for drawing charts (null allowed). */
    protected Rectangle2D minimumDrawArea;

    /** The popup menu for the frame. */
    protected JPopupMenu popup;

    /** The drawing info collected the last time the chart was drawn. */
    protected DrawInfo info;

    /** The scale factor used to draw the chart. */
    protected double scaleX;

    /** The scale factor used to draw the chart. */
    protected double scaleY;

    /** Menu item for zooming in on a chart. */
    protected JMenuItem zoomInMenuItem;

    /** Menu item for zooming out on a chart. */
    protected JMenuItem zoomOutMenuItem;

    /** The current zoom level. */
    protected int zoomLevel = 0;

    /** The maximum zoom level. */
    protected static final int MAX_ZOOM_LEVEL = 4;

    /**
     * Constructs a JFreeChart panel.
     * @param chart The chart.
     */
    public JFreeChartPanel(JFreeChart chart) {

	this(chart,
             DEFAULT_WIDTH,
             DEFAULT_HEIGHT,
             WIDTH_SCALING_THRESHOLD,
             HEIGHT_SCALING_THRESHOLD,
             DEFAULT_BUFFER_USED,
             true,  // properties
             true,  // save
             true,  // print
             true,  // zoom
             true   // tooltips
             );

    }

    /**
     * Constructs a JFreeChart panel.
     * @param chart The chart.
     * @param width The preferred width of the panel.
     * @param height The preferred height of the panel.
     * @param useBuffer A flag that indicates whether to use the off-screen buffer to improve
     *                  performance (at the expense of memory).
     * @param properties A flag indicating whether or not the chart property editor should be
     *                   available via the popup menu.
     * @param save A flag indicating whether or not save options should be available via the popup
     *             menu.
     * @param print A flag indicating whether or not the print option should be available via the
     *              popup menu.
     * @param zoom A flag indicating whether or not zoom options should be added to the popup menu.
     * @param tooltips A flag indicating whether or not tooltips should be enabled for the chart.
     *
     */
    public JFreeChartPanel(JFreeChart chart, int width, int height,
                           double minimumDrawWidth, double minimumDrawHeight,
                           boolean useBuffer,
                           boolean properties, boolean save, boolean print, boolean zoom,
                           boolean tooltips) {

	this.chart = chart;
        this.info = new DrawInfo();
	this.setPreferredSize(new Dimension(width, height));
        this.useBuffer = useBuffer;
        this.refreshBuffer = false;
        this.chart.addChangeListener(this);
        this.minimumDrawArea = new Rectangle2D.Double(0, 0, minimumDrawWidth, minimumDrawHeight);

        // set up popup menu...
        this.popup = null;
        if (properties || save || print || zoom) {
            popup = this.createPopupMenu(properties, save, print, zoom);
        }

        this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);

        if (tooltips) {
            this.info.setToolTipsCollection(new StandardToolTipsCollection());
            this.enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
            setToolTipText("ON");
        }
        this.addMouseListener(this);
        this.setOpaque(true);

    }

    /**
     * Returns the chart contained in the panel.
     * @return The chart contained in the panel.
     */
    public JFreeChart getChart() {
	return chart;
    }

    /**
     * Sets the chart that is displayed in the panel.
     * @param chart The chart.
     */
    public void setChart(JFreeChart chart) {

        // stop listening for changes to the existing chart...
        if (this.chart!=null) {
            this.chart.removeChangeListener(this);
        }

        // add the new chart...
        this.chart = chart;
        this.chart.addChangeListener(this);
        if (this.useBuffer) this.refreshBuffer = true;
        repaint();

    }

    /**
     * Switches chart tooltip generation on or off.
     */
    public void setToolTipGeneration(boolean flag) {

        if (flag) {
            this.info.setToolTipsCollection(new StandardToolTipsCollection());
        }
        else {
            this.info.setToolTipsCollection(null);
        }

    }

    /**
     * Returns a string for the tooltip.
     */
    public String getToolTipText(MouseEvent e) {

        String result = null;
        ToolTipsCollection tooltips = this.info.getToolTipsCollection();
        if (tooltips!=null) {
            Insets insets = this.getInsets();
            result = tooltips.getToolTipText((int)((e.getX()-insets.left)/scaleX),
                                             (int)((e.getY()-insets.top)/scaleY));
        }
        return result;

    }

    /**
     * Returns the minimum drawing area for the chart.
     * @return The minimum drawing area for the chart.
     */
    public Rectangle2D getMinimumDrawArea() {
        return this.minimumDrawArea;
    }

    /**
     * Sets the minimum drawing area for the chart.
     * <P>
     * If the panel is too small to permit the chart to be drawn at this size, then the chart
     * is scaled to fit the smaller space.  Using scaling at small sizes results in better looking
     * charts than the underlying JFreeChart layout mechanism.
     * @param area The area.
     */
    public void setMinimumDrawArea(Rectangle2D area) {

        this.minimumDrawArea = area;
        if (this.useBuffer) this.refreshBuffer = true;
        repaint();

    }

    /**
     * Sets the refresh buffer flag.
     */
    public void setRefreshBuffer(boolean flag) {
        this.refreshBuffer = flag;
    }

    /**
     * A working structure representing the area available on the panel for drawing the chart,
     * taking into account the insets.
     */
    private Rectangle2D available = new Rectangle2D.Double();

    private Rectangle2D chartArea = new Rectangle2D.Double();

    /**
     * Paints the component by drawing the chart to fill the entire component, but
     * allowing for the insets (which will be non-zero if a border has been set for this
     * component).  To increase performance, an off-screen buffer image can be used.
     *
     * @param g The graphics device for drawing on.
     */
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        // first determine the size of the chart rendering area...
        Dimension size = getSize();
        Insets insets = getInsets();
        available.setRect(insets.left, insets.top,
                          size.getWidth()-insets.left-insets.right,
			  size.getHeight()-insets.top-insets.bottom);

        // work out if scaling is required...
        boolean scale = false;
        double drawWidth = available.getWidth();
        double drawHeight = available.getHeight();
        scaleX = 1.0;
        scaleY = 1.0;

        if (minimumDrawArea!=null) {

            double minimumDrawWidth = minimumDrawArea.getWidth();
            if (drawWidth<minimumDrawWidth) {
                scaleX = drawWidth/minimumDrawWidth;
                drawWidth = minimumDrawWidth;
                scale = true;
            }

            double minimumDrawHeight = minimumDrawArea.getHeight();
            if (drawHeight<minimumDrawHeight) {
                scaleY = drawHeight/minimumDrawHeight;
                drawHeight = minimumDrawHeight;
                scale = true;
            }
        }

        chartArea.setRect(0.0, 0.0, drawWidth, drawHeight);

        // are we using the chart buffer?
        if (useBuffer) {

            // do we need to resize the buffer?
            if ((chartBuffer==null) || (chartBufferWidth!=available.getWidth())
                                    || (chartBufferHeight!=available.getHeight())) {

                chartBufferWidth = (int)available.getWidth();
                chartBufferHeight = (int)available.getHeight();
                chartBuffer = createImage(chartBufferWidth, chartBufferHeight);
                refreshBuffer = true;

            }

            // do we need to redraw the buffer?
            if (refreshBuffer) {

                Rectangle2D bufferArea = new Rectangle2D.Double(0, 0,
                                                                chartBufferWidth,
                                                                chartBufferHeight);

                Graphics2D bufferG2 = (Graphics2D)chartBuffer.getGraphics();
                if (scale) {
                    AffineTransform saved = bufferG2.getTransform();
                    bufferG2.transform(AffineTransform.getScaleInstance(scaleX, scaleY));
                    chart.draw(bufferG2, chartArea, this.info);
                    bufferG2.setTransform(saved);
                }
                else chart.draw(bufferG2, bufferArea, this.info);

                refreshBuffer = false;

            }

            // zap the buffer onto the panel...
            g.drawImage(chartBuffer, insets.left, insets.right, this);

        }

        // or redrawing the chart every time...
        else {

            AffineTransform saved = g2.getTransform();
            g2.translate(insets.left, insets.right);
            if (scale) {
                g2.transform(AffineTransform.getScaleInstance(scaleX, scaleY));
            }
            chart.draw(g2, chartArea, this.info);
            g2.setTransform(saved);

        }

    }

    /**
     * Receives notification of changes to the chart, and redraws the chart.
     * @param event Details of the chart change event.
     */
    public void chartChanged(ChartChangeEvent event) {

        this.refreshBuffer = true;
        this.repaint();

    }

    /**
     * Handles action events generated by the popup menu.
     * @param event The event.
     */
    public void actionPerformed(ActionEvent event) {

        String command = event.getActionCommand();

        if (command.equals(PROPERTIES_ACTION_COMMAND)) {
            this.attemptEditChartProperties();
        }
        else if (command.equals(SAVE_ACTION_COMMAND)) {
            try {
                this.doSaveAs();
            }
            catch (IOException e) {
                System.err.println("JFreeChartPanel.doSaveAs: i/o exception = "+e.getMessage());
            }
        }
        else if (command.equals(PRINT_ACTION_COMMAND)) {
            this.createChartPrintJob();
        }
        else if (command.equals(ZOOM_IN_ACTION_COMMAND)) {
            this.zoomIn();
        }
        else if (command.equals(ZOOM_OUT_ACTION_COMMAND)) {
            this.zoomOut();
        }
        else if (command.equals(ZOOM_AUTO_ACTION_COMMAND)) {
            this.zoomAuto();
        }

    }

    /**
     * Checks to see if the popup menu should be displayed, otherwise hands on to the superclass.
     * @param e The mouse event.
     */
    public void processMouseEvent(MouseEvent e) {

        if (e.isPopupTrigger()) {

            if (popup!=null) {
                popup.show(this, e.getX(), e.getY());
            }

        }

        else {
            super.processMouseEvent(e);
        }

    }

    /**
     * Receives notification of mouse clicks, and passes these on to the chart.
     * @param e Information about the mouse event.
     */
    public void mouseClicked(MouseEvent e) {

        // take into account (a) the insets on the panel, (b) the scale factors used when the
        // chart was last drawn and (c) the draw info...
        Insets insets = getInsets();
        chart.handleClick((int)((e.getX()-insets.left)/scaleX),
                          (int)((e.getY()-insets.top)/scaleY),
                          this.info);

    }

    /**
     * Does nothing.  Required for implementation of the MouseListener interface.
     */
    public void mouseEntered(MouseEvent e) {
        // do nothing
    }

    /**
     * Does nothing.  Required for implementation of the MouseListener interface.
     */
    public void mouseExited(MouseEvent e) {
        // do nothing
    }

    /**
     * Does nothing.  Required for implementation of the MouseListener interface.
     */
    public void mousePressed(MouseEvent e) {
        // do nothing
    }

    /**
     * Does nothing.  Required for implementation of the MouseListener interface.
     */
    public void mouseReleased(MouseEvent e) {
        // do nothing
    }

    /**
     * Displays a dialog that allows the user to edit the properties for the current chart.
     */
    private void attemptEditChartProperties() {

        ChartPropertyEditPanel panel = new ChartPropertyEditPanel(chart);
        int result = JOptionPane.showConfirmDialog(this, panel, "Chart Properties",
                                                   JOptionPane.OK_CANCEL_OPTION,
                                                   JOptionPane.PLAIN_MESSAGE);
        if (result==JOptionPane.OK_OPTION) {
            panel.updateChartProperties(chart);
        }

    }

    /**
     * Opens a file chooser and gives the user an opportunity to save the chart in PNG format.
     */
    public void doSaveAs() throws IOException {

        JFileChooser fileChooser = new JFileChooser();
        ExtensionFileFilter filter = new ExtensionFileFilter("PNG Image Files", ".png");
        fileChooser.addChoosableFileFilter(filter);

        int option = fileChooser.showSaveDialog(this);
        if (option==JFileChooser.APPROVE_OPTION) {
            ChartUtilities.saveChartAsPNG(fileChooser.getSelectedFile(),
                                          this.chart, this.getWidth(), this.getHeight());
        }

    }

    /**
     * Creates a popup menu for the panel.
     * @param properties Include a menu item for the chart property editor.
     * @param save Include a menu item for saving the chart.
     * @param print Include a menu item for printing the chart.
     * @param zoom Include menu items for zooming.
     */
    protected JPopupMenu createPopupMenu(boolean properties, boolean save, boolean print,
                                         boolean zoom) {

        JPopupMenu result = new JPopupMenu("Chart:");
        boolean separator = false;

        if (properties) {
            JMenuItem propertiesItem = new JMenuItem("Properties...");
            propertiesItem.setActionCommand(PROPERTIES_ACTION_COMMAND);
            propertiesItem.addActionListener(this);
            result.add(propertiesItem);
            separator = true;
        }

        if (save) {
            if (separator) {
                result.addSeparator();
                separator = false;
            }
            JMenuItem saveItem = new JMenuItem("Save as...");
            saveItem.setActionCommand(SAVE_ACTION_COMMAND);
            saveItem.addActionListener(this);
            result.add(saveItem);
            separator = true;
        }

        if (print) {
            if (separator) {
                result.addSeparator();
                separator = false;
            }
            JMenuItem printItem = new JMenuItem("Print...");
            printItem.setActionCommand(PRINT_ACTION_COMMAND);
            printItem.addActionListener(this);
            result.add(printItem);
            separator = true;
        }

        if (zoom) {
            if (separator) {
                result.addSeparator();
                separator = false;
            }

            JMenu zoomMenu = new JMenu("Zoom");

            zoomInMenuItem = new JMenuItem("In");
            zoomInMenuItem.setActionCommand(ZOOM_IN_ACTION_COMMAND);
            zoomInMenuItem.addActionListener(this);
            zoomMenu.add(zoomInMenuItem);

            zoomOutMenuItem = new JMenuItem("Out");
            zoomOutMenuItem.setActionCommand(ZOOM_OUT_ACTION_COMMAND);
            zoomOutMenuItem.addActionListener(this);
            zoomMenu.add(zoomOutMenuItem);

            zoomMenu.addSeparator();

            JMenuItem zoomAutoMenuItem = new JMenuItem("Reset");
            zoomAutoMenuItem.setActionCommand(ZOOM_AUTO_ACTION_COMMAND);
            zoomAutoMenuItem.addActionListener(this);
            zoomMenu.add(zoomAutoMenuItem);

            result.add(zoomMenu);
        }

        return result;

    }

    /**
     * Creates a print job for the chart.
     */
    public void createChartPrintJob() {

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        if (job.printDialog()) {
            try {
                job.print();
            }
            catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, e);
            }
        }

    }

    /**
     * Prints the chart on a single page.
     */
    public int print(Graphics g, PageFormat pf, int pageIndex) {

        if (pageIndex!=0) return NO_SUCH_PAGE;
        Graphics2D g2 = (Graphics2D)g;
        double x = pf.getImageableX();
        double y = pf.getImageableY();
        double w = pf.getImageableWidth();
        double h = pf.getImageableHeight();
        chart.draw(g2, new Rectangle2D.Double(x, y, w, h), null);
        return PAGE_EXISTS;

    }

    /**
     * Zooms in on the anchor point.
     */
    public void zoomIn() {

        if (zoomLevel < MAX_ZOOM_LEVEL) {
            zoomLevel++;
            chart.getPlot().zoom(0.5);
            if (!zoomOutMenuItem.isEnabled()) {
                zoomOutMenuItem.setEnabled(true);
            }
        }

        if ((zoomLevel >= MAX_ZOOM_LEVEL) && zoomInMenuItem.isEnabled()) {
            zoomInMenuItem.setEnabled(false);
        }
    }

    /**
     * Zooms out with the given point in the center.
     */
    public void zoomOut() {
        if (zoomLevel > -MAX_ZOOM_LEVEL) {
            zoomLevel--;
            chart.getPlot().zoom(2.0);
            if (!zoomInMenuItem.isEnabled()) {
                zoomInMenuItem.setEnabled(true);
            }
        }

        if ((zoomLevel <= -MAX_ZOOM_LEVEL) && zoomOutMenuItem.isEnabled()) {
            zoomOutMenuItem.setEnabled(false);
        }
    }

    /**
     * Resets zoom.
     */
    public void zoomAuto() {
            zoomLevel=0;
            chart.getPlot().zoom(0.0);
            if (!zoomInMenuItem.isEnabled()) {
                zoomInMenuItem.setEnabled(true);
            }
            if (!zoomOutMenuItem.isEnabled()) {
                zoomOutMenuItem.setEnabled(true);
            }
     }

}
