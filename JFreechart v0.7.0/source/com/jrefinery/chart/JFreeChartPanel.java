/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.jrefinery.com/jfreechart;
 * Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
 *
 * (C) Copyright 2000, 2001 by Simba Management Limited and Contributors.
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
 * (C) Copyright 2000, 2001 by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   Søren Caspersen;
 *
 * $Id: JFreeChartPanel.java,v 1.1 2007/10/10 18:54:39 vauchers Exp $
 *
 * Changes (from 28-Jun-2001)
 * --------------------------
 * 28-Jun-2001 : Integrated buffering code contributed by Søren Caspersen (DG);
 * 18-Sep-2001 : Updated e-mail address and fixed DOS encoding problem (DG);
 * 22-Nov-2001 : Added scaling to improve display of charts in small sizes (DG);
 * 26-Nov-2001 : Added property editing, saving and printing (DG);
 * 11-Dec-2001 : Transferred saveChartAsPNG method to new ChartUtilities class (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.print.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import com.jrefinery.ui.*;
import com.jrefinery.chart.event.*;
import com.jrefinery.chart.ui.*;
import com.keypoint.*;

/**
 * A Swing GUI component for displaying a JFreeChart.
 * <P>
 * The panel is registered to receive notification of changes to the chart, so that the chart can
 * be redrawn automatically as required.
 */
public class JFreeChartPanel extends JComponent implements ActionListener,
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

    /**
     * Constructs a JFreeChart panel.
     * @param chart The chart.
     * @param width The preferred width of the panel.
     * @param height The preferred height of the panel.
     * @param useBuffer A flag that indicates whether to use the off-screen buffer to improve
     *                  performance (at the expense of memory).
     */
    public JFreeChartPanel(JFreeChart chart, int width, int height,
                           boolean useBuffer,
                           double minimumDrawWidth, double minimumDrawHeight) {

	this.chart = chart;
	setPreferredSize(new Dimension(width, height));
        this.useBuffer = useBuffer;
        this.refreshBuffer = false;
        this.chart.addChangeListener(this);
        this.minimumDrawArea = new Rectangle2D.Double(0, 0, minimumDrawWidth, minimumDrawHeight);
        popup = this.createPopupMenu();
        this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);

    }

    /**
     * Constructs a JFreeChart panel.
     * @param chart The chart.
     */
    public JFreeChartPanel(JFreeChart chart) {

	this(chart, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_BUFFER_USED,
             WIDTH_SCALING_THRESHOLD, HEIGHT_SCALING_THRESHOLD);

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

        this.chart = chart;
        if (this.useBuffer) this.refreshBuffer = true;
        repaint();

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
     * Paints the component - this means drawing the chart to fill the entire component, but
     * allowing for the insets (which will be non-zero if a border has been set for this
     * component).  To increase performance, an off-screen buffer image can be used.
     * @param g The graphics device for drawing on;
     */
    public void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D)g;

        // first determine the size of the chart rendering area...
        Dimension size = getSize();
        Insets insets = getInsets();
        Rectangle2D screenArea = new Rectangle2D.Double(insets.left, insets.top,
			                                size.getWidth()-insets.left-insets.right,
						        size.getHeight()-insets.top-insets.bottom);

        // work out if scaling is required...
        boolean scale = false;
        double scaleX = 1.0;
        double scaleY = 1.0;

        if (screenArea.getWidth()<minimumDrawArea.getWidth()) {
            scaleX = screenArea.getWidth()/minimumDrawArea.getWidth();
            scale = true;
        }

        if (screenArea.getHeight()<minimumDrawArea.getHeight()) {
            scaleY = screenArea.getHeight()/minimumDrawArea.getHeight();
            scale = true;
        }

        Rectangle2D chartArea = new Rectangle2D.Double(0, 0,
                                                       Math.max(screenArea.getWidth(),
                                                                minimumDrawArea.getWidth()),
                                                       Math.max(screenArea.getHeight(),
                                                                minimumDrawArea.getHeight()));

        // are we using the chart buffer...
        if (useBuffer) {

            // do we need to resize the buffer?
            if ((chartBuffer==null) || (chartBufferWidth!=screenArea.getWidth())
                                    || (chartBufferHeight!=screenArea.getHeight())) {

                chartBufferWidth = (int)screenArea.getWidth();
                chartBufferHeight = (int)screenArea.getHeight();
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
                    chart.draw(bufferG2, chartArea);
                    bufferG2.setTransform(saved);
                }
                else chart.draw(bufferG2, bufferArea);

                refreshBuffer = false;
            }

            // zap the buffer onto the panel...
            g.drawImage(chartBuffer, insets.left, insets.right, this);

        }

        // or redrawing the chart every time...
        else {

            if (scale) {
                AffineTransform saved = g2.getTransform();
                g2.translate(insets.left, insets.right);
                g2.transform(AffineTransform.getScaleInstance(scaleX, scaleY));
                chart.draw(g2, chartArea);
                g2.setTransform(saved);
            }
            else chart.draw(g2, screenArea);
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
            this.attemptSaveAs();
        }
        else if (command.equals(PRINT_ACTION_COMMAND)) {
            this.createChartPrintJob();
        }

    }

    /**
     * Checks to see if the popup menu should be displayed, otherwise hands on to the superclass.
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
     * Handles a user request to save the chart as an image file (currently just PNG format).
     */
    private void attemptSaveAs() {

        JFileChooser fileChooser = new JFileChooser();
        ExtensionFileFilter filter = new ExtensionFileFilter("PNG Image Files", ".png");
        fileChooser.addChoosableFileFilter(filter);

        int option = fileChooser.showSaveDialog(this);
        if (option==JFileChooser.APPROVE_OPTION) {
            ChartUtilities.saveChartAsPNG(fileChooser.getSelectedFile(),
                                          this.chart, this.getWidth(), this.getHeight());
        }

    }

//    /**
//     * Saves the chart as a PNG format image file.
//     * @param file The file.
//     * @param width The image width.
//     * @param height The image height.
//     */
//    private void saveChartAsPNG(File file, int width, int height) {
//
//        BufferedImage chartImage = chart.createBufferedImage(width, height);
//
//        PngEncoder encoder = new PngEncoder(chartImage, false, 0, 9);
//        byte[] pngData = encoder.pngEncode();
//        try {
//            DataOutputStream out = new DataOutputStream(
//                                       new BufferedOutputStream(
//                                           new FileOutputStream(file)));
//            out.write(pngData);
//        }
//        catch (IOException e) {
//            System.out.println("Problem in SaveChartAsPNG");
//        }
//
//    }

    /**
     * Creates a popup menu for the frame.
     */
    protected JPopupMenu createPopupMenu() {

        JPopupMenu result = new JPopupMenu("Chart:");

        JMenuItem properties = new JMenuItem("Properties...");
        properties.setActionCommand(PROPERTIES_ACTION_COMMAND);
        properties.addActionListener(this);
        result.add(properties);

        result.addSeparator();

        JMenuItem save = new JMenuItem("Save as...");
        save.setActionCommand(SAVE_ACTION_COMMAND);
        save.addActionListener(this);
        result.add(save);

        result.addSeparator();

        JMenuItem print = new JMenuItem("Print...");
        print.setActionCommand(PRINT_ACTION_COMMAND);
        print.addActionListener(this);
        result.add(print);

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
        chart.draw(g2, new Rectangle2D.Double(x, y, w, h));
        return PAGE_EXISTS;

    }



}
