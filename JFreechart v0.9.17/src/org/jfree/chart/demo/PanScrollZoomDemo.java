/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ----------------------
 * PanScrollZoomDemo.java
 * ----------------------
 * (C) Copyright 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  Matthias Rose (Ablay & Fodi GmbH, Germany);
 * Contributor(s):   Eduardo Ramalho;
 *                   David Gilbert (for Object Refinery Limited);
 *
 * $Id: PanScrollZoomDemo.java,v 1.1 2007/10/10 19:29:07 vauchers Exp $
 *
 * Changes
 * -------
 * 18-Feb-2004 : Version 1 added to JFreeChart distribution, contributed by Matthias Rose (DG);
 *
 */

package org.jfree.chart.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.AbstractButton;
import javax.swing.BoundedRangeModel;
import javax.swing.ButtonGroup;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.chart.renderer.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.Range;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

/**
 * A demo for panning, scrolling and zooming.
 */
public class PanScrollZoomDemo extends JFrame
                               implements ActionListener, 
                                          ChangeListener, 
                                          ChartChangeListener, 
                                          MouseListener, 
                                          MouseMotionListener {

    /** The panel that displays the chart. */
    private ChartPanel chartPanel;
    
    /** The scroll factor. */
    private double scrollFactor = 1000;
    
    /** The scroll bar. */
    private JScrollBar scrollBar;
    
    /** The starting point for panning. */
    private Point2D panStartPoint;
    
    /** The min/max values for the primary axis. */
    private double[] primYMinMax = new double[2];
    
    /** The min/max values for the secondary axis. */
    private double[] secondYMinMax = new double[2];

    /** Action command for the 'Pan' button. */
    private static final String ACTION_CMD_PAN = "pan";
    
    /** Action command for the zoom box button. */
    private static final String ACTION_CMD_ZOOM_BOX = "zoomBox";
    
    /** Action command for the zoom fit button. */
    private static final String ACTION_CMD_ZOOM_TO_FIT = "zoomFit";
    
    /** Action command for the '+' button. */
    private static final String ACTION_CMD_ZOOM_IN = "zoomIn";
    
    /** Action command for the '-' button. */
    private static final String ACTION_CMD_ZOOM_OUT = "zoomOut";

    /** The zoom factor. */
    private static final double ZOOM_FACTOR = 0.8;

    /** The toolbar. */
    private JToolBar toolBar;
    
    /** The zoom button. */
    private AbstractButton zoomButton;
    
    /** The pan button. */
    private AbstractButton panButton;
    
    /** The zoom in button. */
    private AbstractButton zoomInButton;
    
    /** The zoom out button. */
    private AbstractButton zoomOutButton;
    
    /** The fit button. */
    private AbstractButton fitButton;

    /**
     * Creates a new demo instance.
     * 
     * @param frameTitle  the frame title.
     */
    public PanScrollZoomDemo(String frameTitle) {

        super(frameTitle);

        this.toolBar = createToolbar();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(this.toolBar, BorderLayout.SOUTH);

        JFreeChart chart = createChart();

        this.scrollBar.setModel(new DefaultBoundedRangeModel());
        recalcScrollBar(chart.getPlot());

        this.chartPanel = new ChartPanel(chart) {
            public void autoRangeBoth() {
                System.out.println("Use 'Fit all' button");
            }
        };

        chart.addChangeListener(this);

        // enable zoom
        actionPerformed(new ActionEvent(this, 0, ACTION_CMD_ZOOM_BOX));

        // MouseListeners for pan function
        this.chartPanel.addMouseListener(this);
        this.chartPanel.addMouseMotionListener(this);

        // remove popup menu to allow panning
        // with right mouse pressed
        this.chartPanel.setPopupMenu(null);

        getContentPane().add(this.chartPanel);
    }

    /**
     * Creates a sample chart.
     * 
     * @return a sample chart.
     */
    private JFreeChart createChart() {

        XYSeriesCollection primaryJFreeColl = new XYSeriesCollection();
        XYSeries left1 = new XYSeries("Left 1");
        left1.add(1, 2);
        left1.add(2.8, 5.9);
        left1.add(3, null);
        left1.add(3.4, 2);
        left1.add(5, -1);
        left1.add(7, 1);
        primaryJFreeColl.addSeries(left1);

        XYSeriesCollection secondaryJFreeColl = new XYSeriesCollection();
        XYSeries right1 = new XYSeries("Right 1");
        right1.add(3.5, 2.2);
        right1.add(1.2, 1.3);
        right1.add(5.7, 4.1);
        right1.add(7.5, 7.4);
        secondaryJFreeColl.addSeries(right1);

        NumberAxis xAxis = new NumberAxis("X");
        xAxis.setAutoRangeIncludesZero(false);
        xAxis.setAutoRangeStickyZero(false);

        NumberAxis primaryYAxis = new NumberAxis("Y1");
        primaryYAxis.setAutoRangeIncludesZero(false);
        primaryYAxis.setAutoRangeStickyZero(false);

        // create plot
        XYItemLabelGenerator labelGenerator = new StandardXYItemLabelGenerator();
        XYItemRenderer y1Renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES);
        y1Renderer.setSeriesPaint(0, Color.blue);
        y1Renderer.setToolTipGenerator(labelGenerator);
        XYPlot xyPlot = new XYPlot(primaryJFreeColl, xAxis, primaryYAxis, y1Renderer);

        // 2nd y-axis

        NumberAxis secondaryYAxis = new NumberAxis("Y2");
        secondaryYAxis.setAutoRangeIncludesZero(false);
        secondaryYAxis.setAutoRangeStickyZero(false);

        xyPlot.setSecondaryRangeAxis(0, secondaryYAxis);
        xyPlot.setSecondaryDataset(0, secondaryJFreeColl);

        xyPlot.mapSecondaryDatasetToRangeAxis(0, new Integer(0));
        xyPlot.mapSecondaryDatasetToDomainAxis(0, new Integer(0));

        XYItemRenderer y2Renderer = new StandardXYItemRenderer(
            StandardXYItemRenderer.SHAPES_AND_LINES
        );
        y2Renderer.setToolTipGenerator(labelGenerator);
        xyPlot.setSecondaryRenderer(0, y2Renderer);

        // set some fixed y-dataranges and remember them
        // because default chartPanel.autoRangeBoth()
        // would destroy them

        ValueAxis axis = xyPlot.getRangeAxis();
        this.primYMinMax[0] = -5;
        this.primYMinMax[1] = 15;
        axis.setLowerBound(this.primYMinMax[0]);
        axis.setUpperBound(this.primYMinMax[1]);

        axis = xyPlot.getSecondaryRangeAxis(0);
        this.secondYMinMax[0] = -1;
        this.secondYMinMax[1] = 10;
        axis.setLowerBound(this.secondYMinMax[0]);
        axis.setUpperBound(this.secondYMinMax[1]);

        // Title + legend

        String title = "To pan in zoom mode hold right mouse pressed";
        JFreeChart ret = new JFreeChart(title, null, xyPlot, true);
        TextTitle textTitle = new TextTitle(
            "(but you can only pan if the chart was zoomed before)"
        );
        ret.addSubtitle(textTitle);
        return ret;
    }

    /**
     * Creates the toolbar.
     * 
     * @return the toolbar.
     */
    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();

        ButtonGroup groupedButtons = new ButtonGroup();

        // ACTION_CMD_PAN
        this.panButton = new JToggleButton();
        prepareButton(this.panButton, ACTION_CMD_PAN, " Pan ", "Pan mode");
        groupedButtons.add(this.panButton);
        toolbar.add(this.panButton);

        // ACTION_CMD_ZOOM_BOX
        this.zoomButton = new JToggleButton();
        prepareButton(this.zoomButton, ACTION_CMD_ZOOM_BOX, " Zoom ", "Zoom mode");
        groupedButtons.add(this.zoomButton);
        this.zoomButton.setSelected(true); // no other makes sense after startup
        toolbar.add(this.zoomButton);

        // end of toggle-button group for select/pan/zoom-box
        toolbar.addSeparator();

        // ACTION_CMD_ZOOM_IN
        this.zoomInButton = new JButton();
        prepareButton(this.zoomInButton, ACTION_CMD_ZOOM_IN, " + ", "Zoom in");
        toolbar.add(this.zoomInButton);

        // ACTION_CMD_ZOOM_OUT
        this.zoomOutButton = new JButton();
        prepareButton(this.zoomOutButton, ACTION_CMD_ZOOM_OUT, " - ", "Zoom out");
        toolbar.add(this.zoomOutButton);

        // ACTION_CMD_ZOOM_TO_FIT
        this.fitButton = new JButton();
        prepareButton(this.fitButton, ACTION_CMD_ZOOM_TO_FIT, " Fit ", "Fit all");
        toolbar.add(this.fitButton);

        toolbar.addSeparator();

        this.scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        //   int ht = (int) zoomButton.getPreferredSize().getHeight();
        //   scrollBar.setPreferredSize(new Dimension(0, ht));
        this.scrollBar.setModel(new DefaultBoundedRangeModel());

        toolbar.add(this.scrollBar);

        this.zoomOutButton.setEnabled(false);
        this.fitButton.setEnabled(false);
        this.scrollBar.setEnabled(false);

        toolbar.setFloatable(false);
        return toolbar;
    }

    /**
     * Prepares a button.
     * 
     * @param button  the button.
     * @param actionKey  the action key.
     * @param buttonLabelText  the button label.
     * @param toolTipText  the tooltip text.
     */
    private void prepareButton(AbstractButton button, 
                               String actionKey, 
                               String buttonLabelText,
                               String toolTipText) {
        // todo
        // as this action is empty and the button text is
        // redefined later, it can be safely removed ...
//        Action action = new AbstractAction(actionKey) {
//            public void actionPerformed(ActionEvent evt) {
//                // ignored
//            }
//        };
//        button.addActionListener(action);
        button.setActionCommand(actionKey);
        button.setText(buttonLabelText);
        button.setToolTipText(toolTipText);
        button.addActionListener(this);
    }

    /**
     * Sets the pan mode.
     * 
     * @param val  a boolean.
     */
    private void setPanMode(boolean val) {

        this.chartPanel.setHorizontalZoom(!val);
        // chartPanel.setHorizontalAxisTrace(! val);
        this.chartPanel.setVerticalZoom(!val);
        // chartPanel.setVerticalAxisTrace(! val);

        if (val) {
            this.chartPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        else {
            this.chartPanel.setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Handles an action event.
     * 
     * @param evt
     *            the event.
     */
    public void actionPerformed(ActionEvent evt) {
        try {
            String acmd = evt.getActionCommand();

            if (acmd.equals(ACTION_CMD_ZOOM_BOX)) {
                setPanMode(false);
            } 
            else if (acmd.equals(ACTION_CMD_PAN)) {
                setPanMode(true);
            } 
            else if (acmd.equals(ACTION_CMD_ZOOM_IN)) {
                ChartRenderingInfo info = this.chartPanel.getChartRenderingInfo();
                Rectangle2D rect = info.getPlotInfo().getDataArea();
                zoomBoth(rect.getCenterX(), rect.getCenterY(), ZOOM_FACTOR);
            } 
            else if (acmd.equals(ACTION_CMD_ZOOM_OUT)) {
                ChartRenderingInfo info = this.chartPanel.getChartRenderingInfo();
                Rectangle2D rect = info.getPlotInfo().getDataArea();
                zoomBoth(rect.getCenterX(), rect.getCenterY(), 1 / ZOOM_FACTOR);
            } 
            else if (acmd.equals(ACTION_CMD_ZOOM_TO_FIT)) {

                // X-axis (has no fixed borders)
                this.chartPanel.autoRangeHorizontal();

                // Y-Axes) (autoRangeVertical
                // not useful because of fixed borders
                Plot plot = this.chartPanel.getChart().getPlot();
                if (plot instanceof ValueAxisPlot) {

                    XYPlot vvPlot = (XYPlot) plot;
                    ValueAxis axis = vvPlot.getRangeAxis();
                    if (axis != null) {
                        axis.setLowerBound(this.primYMinMax[0]);
                        axis.setUpperBound(this.primYMinMax[1]);
                    }
                    if (plot instanceof XYPlot) {
                        XYPlot xyPlot = (XYPlot) plot;
                        axis = xyPlot.getSecondaryRangeAxis(0);
                        if (axis != null) {
                            axis.setLowerBound(this.secondYMinMax[0]);
                            axis.setUpperBound(this.secondYMinMax[1]);
                        }
                    }
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles a {@link ChangeEvent} (in this case, coming from the scrollbar).
     * 
     * @param event  the event.
     */
    public void stateChanged(ChangeEvent event) {
        try {
            Object src = event.getSource();
            BoundedRangeModel scrollBarModel = this.scrollBar.getModel();
            if (src == scrollBarModel) {
                int val = scrollBarModel.getValue();
                int ext = scrollBarModel.getExtent();

                Plot plot = this.chartPanel.getChart().getPlot();
                if (plot instanceof XYPlot) {
                    XYPlot hvp = (XYPlot) plot;
                    ValueAxis axis = hvp.getDomainAxis();

                    // avoid problems
                    this.chartPanel.getChart().removeChangeListener(this);

                    axis.setRange(val / this.scrollFactor, (val + ext) / this.scrollFactor);

                    // restore chart listener
                    this.chartPanel.getChart().addChangeListener(this);
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles a {@link ChartChangeEvent}.
     * 
     * @param event  the event.
     */
    public void chartChanged(ChartChangeEvent event) {
        try {
            if (event.getChart() == null) {
                return;
            }  

            BoundedRangeModel scrollBarModel = this.scrollBar.getModel();
            if (scrollBarModel == null) {
                return;
            }

            boolean chartIsZoomed = false;

            Plot plot = event.getChart().getPlot();
            if (plot instanceof XYPlot) {
                XYPlot hvp = (XYPlot) plot;
                ValueAxis xAxis = hvp.getDomainAxis();
                Range xAxisRange = xAxis.getRange();

                // avoid recursion
                scrollBarModel.removeChangeListener(this);

                int low = (int) (xAxisRange.getLowerBound() * this.scrollFactor);
                scrollBarModel.setValue(low);
                int ext = (int) (xAxisRange.getUpperBound() * this.scrollFactor - low);
                scrollBarModel.setExtent(ext);

                // restore
                scrollBarModel.addChangeListener(this);

                // check if zoomed horizontally
                //Range hdr = hvp.getHorizontalDataRange(xAxis);
                Range hdr = hvp.getDataRange(xAxis);

                double len = hdr == null ? 0 : hdr.getLength();
                chartIsZoomed |= xAxisRange.getLength() < len;
            }

            if (!chartIsZoomed && plot instanceof XYPlot) {
                // check if zoomed vertically
                XYPlot vvp = (XYPlot) plot;
                ValueAxis yAxis = vvp.getRangeAxis();
                if (yAxis != null) {
                    chartIsZoomed = yAxis.getLowerBound() > this.primYMinMax[0]
                        || yAxis.getUpperBound() < this.primYMinMax[1];

                    // right y-axis
                    if (!chartIsZoomed && plot instanceof XYPlot) {
                        XYPlot xyPlot = (XYPlot) plot;
                        yAxis = xyPlot.getSecondaryRangeAxis(0);
                        if (yAxis != null) {
                            chartIsZoomed = yAxis.getLowerBound() > this.secondYMinMax[0]
                                || yAxis.getUpperBound() < this.secondYMinMax[1];
                        }
                    }
                }
            }

            // enable "zoom-out-buttons" if chart is zoomed
            // otherwise disable them
            this.panButton.setEnabled(chartIsZoomed);
            this.zoomOutButton.setEnabled(chartIsZoomed);
            this.fitButton.setEnabled(chartIsZoomed);
            this.scrollBar.setEnabled(chartIsZoomed);
            if (!chartIsZoomed) {
                setPanMode(false);
                this.zoomButton.setSelected(true);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Mouse[Motion]Listeners for pan

    /**
     * Handles a mouse pressed event (to start panning).
     * 
     * @param event  the event.
     */
    public void mousePressed(MouseEvent event) {
        try {
            if (this.panButton.isSelected()
                || this.panButton.isEnabled()
                && SwingUtilities.isRightMouseButton(event)) {
                Rectangle2D dataArea = this.chartPanel.getScaledDataArea();
                Point2D point = event.getPoint();
                if (dataArea.contains(point)) {
                    setPanMode(true);
                    this.panStartPoint = point;
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles a mouse released event (stops panning).
     * 
     * @param event  the event.
     */
    public void mouseReleased(MouseEvent event) {
        try {
            this.panStartPoint = null; // stop panning
            if (!this.panButton.isSelected()) {
                setPanMode(false);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles a mouse dragged event to perform panning.
     * 
     * @param event  the event.
     */
    public void mouseDragged(MouseEvent event) {
        try {
            if (this.panStartPoint != null) {
                Rectangle2D scaledDataArea = this.chartPanel.getScaledDataArea();

                this.panStartPoint = RefineryUtilities.getPointInRectangle(
                    this.panStartPoint.getX(),
                    this.panStartPoint.getY(),
                    scaledDataArea
                );
                Point2D panEndPoint = RefineryUtilities.getPointInRectangle(
                    event.getX(), event.getY(), scaledDataArea
                );

                // horizontal pan

                Plot plot = this.chartPanel.getChart().getPlot();
                if (plot instanceof XYPlot) {
                    XYPlot hvp = (XYPlot) plot;
                    ValueAxis xAxis = hvp.getDomainAxis();

                    if (xAxis != null) {
                        double translatedStartPoint = xAxis.java2DToValue(
                            (float) this.panStartPoint.getX(),
                            scaledDataArea,
                            hvp.getDomainAxisEdge()
                        );
                        double translatedEndPoint = xAxis.java2DToValue(
                            (float) panEndPoint.getX(),
                            scaledDataArea,
                            hvp.getDomainAxisEdge()
                        );
                        double dX = translatedStartPoint - translatedEndPoint;

                        double oldMin = xAxis.getLowerBound();
                        double newMin = oldMin + dX;

                        double oldMax = xAxis.getUpperBound();
                        double newMax = oldMax + dX;

                        // do not pan out of range
                        if (newMin >= hvp.getDataRange(xAxis).getLowerBound()
                            && newMax <= hvp.getDataRange(xAxis).getUpperBound()) {
                            xAxis.setLowerBound(newMin);
                            xAxis.setUpperBound(newMax);
                        }
                    }
                }

                // vertical pan (1. Y-Axis)

                if (plot instanceof XYPlot) {
                    XYPlot vvp = (XYPlot) plot;
                    ValueAxis yAxis = vvp.getRangeAxis();

                    if (yAxis != null) {
                        double translatedStartPoint = yAxis.java2DToValue(
                            (float) this.panStartPoint.getY(),
                            scaledDataArea,
                            vvp.getRangeAxisEdge()
                        );
                        double translatedEndPoint = yAxis.java2DToValue(
                            (float) panEndPoint.getY(),
                            scaledDataArea,
                            vvp.getRangeAxisEdge()
                        );
                        double dY = translatedStartPoint - translatedEndPoint;

                        double oldMin = yAxis.getLowerBound();
                        double newMin = oldMin + dY;

                        double oldMax = yAxis.getUpperBound();
                        double newMax = oldMax + dY;

                        // do not pan out of range
                        if (newMin >= this.primYMinMax[0] && newMax <= this.primYMinMax[1]) {
                            yAxis.setLowerBound(newMin);
                            yAxis.setUpperBound(newMax);
                        }
                    }
                }

                // vertical pan (2. Y-Axis)

                if (plot instanceof XYPlot) {
                    XYPlot xyPlot = (XYPlot) plot;
                    ValueAxis yAxis = xyPlot.getSecondaryRangeAxis(0);

                    if (yAxis != null) {
                        double translatedStartPoint = yAxis.java2DToValue(
                            (float) this.panStartPoint.getY(),
                            scaledDataArea,
                            xyPlot.getSecondaryRangeAxisEdge(0)
                        );
                        double translatedEndPoint = yAxis.java2DToValue(
                            (float) panEndPoint.getY(),
                            scaledDataArea,
                            xyPlot.getSecondaryRangeAxisEdge(0)
                        );
                        double dY = translatedStartPoint - translatedEndPoint;

                        double oldMin = yAxis.getLowerBound();
                        double newMin = oldMin + dY;

                        double oldMax = yAxis.getUpperBound();
                        double newMax = oldMax + dY;

                        if (newMin >= this.secondYMinMax[0] && newMax <= this.secondYMinMax[1]) {
                            yAxis.setLowerBound(newMin);
                            yAxis.setUpperBound(newMax);
                        }
                    }
                }

                // for the next time
                this.panStartPoint = panEndPoint;
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 
     * Handles a mouse clicked event, in this case by ignoring it.
     * 
     * @param event  the event.
     */
    public void mouseClicked(MouseEvent event) {
        // ignored
    }

    /** 
     * Handles a mouse moved event, in this case by ignoring it.
     * 
     * @param event  the event.
     */
    public void mouseMoved(MouseEvent event) {
        // ignored
    }

    /** 
     * Handles a mouse entered event, in this case by ignoring it.
     * 
     * @param event  the event.
     */
    public void mouseEntered(MouseEvent event) {
        // ignored
    }

    /** 
     * Handles a mouse exited event, in this case by ignoring it.
     * 
     * @param event  the event.
     */
    public void mouseExited(MouseEvent event) {
        // ignored
    }

    /**
     * Starting point for the demo.
     * 
     * @param args  the command line arguments (ignored).
     */
    public static void main(String[] args) {
        
        try {
            final String lookAndFeelClassName = WindowsLookAndFeel.class.getName();
            UIManager.setLookAndFeel(lookAndFeelClassName);
        } 
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        PanScrollZoomDemo demo = new PanScrollZoomDemo("Pan & Scroll & Zoom - Demo");
        demo.pack();
        demo.setVisible(true);
    
    }

    // PRIVATE 
    
    /**
     * Recalculates the scrollbar settings.
     * 
     * @param plot  the plot.
     */
    private void recalcScrollBar(Plot plot) {
        if (plot instanceof XYPlot) {
            XYPlot hvp = (XYPlot) plot;
            ValueAxis axis = hvp.getDomainAxis();

            axis.setLowerMargin(0);
            axis.setUpperMargin(0);

            Range rng = axis.getRange();

            BoundedRangeModel scrollBarModel = this.scrollBar.getModel();
            int len = scrollBarModel.getMaximum() - scrollBarModel.getMinimum();
            if (rng.getLength() > 0) {
                scrollFactor = len / rng.getLength();
            }
            
            double dblow = rng.getLowerBound();
            int ilow = (int) (dblow * scrollFactor);
            scrollBarModel.setMinimum(ilow);
            int val = ilow;
            scrollBarModel.setValue(val);

            double dbup = rng.getUpperBound();
            int iup = (int) (dbup * scrollFactor);
            scrollBarModel.setMaximum(iup);
            int ext = iup - ilow;
            scrollBarModel.setExtent(ext);

            scrollBarModel.addChangeListener(this);
        }
    }

    /**
     * Zooms in on an anchor point (measured in Java2D coordinates).
     * 
     * @param x  the x value.
     * @param y  the y value.
     * @param zoomFactor  the zoomFactor < 1 == zoom in; else out.
     */
    private void zoomBoth(double x, double y, double zoomFactor) {
        zoomHorizontal(x, zoomFactor);
        zoomVertical(y, zoomFactor);
    }

    /**
     * Decreases the range on the horizontal axis, centered about a Java2D x coordinate.
     * <P>
     * The range on the x axis is multiplied by zoomFactor
     * 
     * @param x  the x coordinate in Java2D space.
     * @param zoomFactor  the zoomFactor < 1 == zoom in; else out.
     */
    private void zoomHorizontal(double x, double zoomFactor) {

        JFreeChart chart = this.chartPanel.getChart();
        ChartRenderingInfo info = this.chartPanel.getChartRenderingInfo();
        if (chart.getPlot() instanceof XYPlot) {
            XYPlot hvp = (XYPlot) chart.getPlot();
            ValueAxis axis = hvp.getDomainAxis();
            if (axis != null) {
                double anchorValue = axis.java2DToValue(
                    (float) x, info.getPlotInfo().getDataArea(), hvp.getDomainAxisEdge()
                );
                if (zoomFactor < 1.0) {
                    axis.resizeRange(zoomFactor, anchorValue);
                } 
                else if (zoomFactor > 1.0) {
                    Range range = hvp.getDataRange(axis);
                    adjustRange(axis, range, zoomFactor, anchorValue);
                }
            }
        }
    }

    /**
     * Decreases the range on the vertical axis, centered about a Java2D y coordinate.
     * <P>
     * The range on the y axis is multiplied by zoomFactor
     * 
     * @param y  the y coordinate in Java2D space.
     * @param zoomFactor  the zoomFactor < 1 == zoom in; else out.
     */
    private void zoomVertical(double y, double zoomFactor) {

        JFreeChart chart = this.chartPanel.getChart();
        ChartRenderingInfo info = this.chartPanel.getChartRenderingInfo();

        // 1. (left) Y-Axis

        if (chart.getPlot() instanceof XYPlot) {
            XYPlot vvp = (XYPlot) chart.getPlot();
            ValueAxis primYAxis = vvp.getRangeAxis();
            if (primYAxis != null) {
                double anchorValue =
                    primYAxis.java2DToValue(
                        (float) y, info.getPlotInfo().getDataArea(), vvp.getRangeAxisEdge()
                    );
                if (zoomFactor < 1.0) {
                    // zoom in
                    primYAxis.resizeRange(zoomFactor, anchorValue);

                } 
                else if (zoomFactor > 1.0) {
                    // zoom out
                    Range range = new Range(this.primYMinMax[0], this.primYMinMax[1]);
                    adjustRange(primYAxis, range, zoomFactor, anchorValue);
                }
            }

            // 2. (right) Y-Axis

            if (chart.getPlot() instanceof XYPlot) {
                XYPlot xyp = (XYPlot) chart.getPlot();
                ValueAxis secYAxis = xyp.getSecondaryRangeAxis(0);
                if (secYAxis != null) {
                    double anchorValue =
                        secYAxis.java2DToValue(
                            (float) y,
                            info.getPlotInfo().getDataArea(),
                            xyp.getSecondaryRangeAxisEdge(0));
                    if (zoomFactor < 1.0) {
                        // zoom in
                        secYAxis.resizeRange(zoomFactor, anchorValue);

                    } 
                    else if (zoomFactor > 1.0) {
                        // zoom out
                        Range range = new Range(this.secondYMinMax[0], this.secondYMinMax[1]);
                        adjustRange(secYAxis, range, zoomFactor, anchorValue);
                    }
                }
            }
        }
    }

    /**
     * used for zooming
     * 
     * @param axis  the axis.
     * @param range  the range.
     * @param zoomFactor  the zoom factor.
     * @param anchorValue  the anchor value.
     */
    private void adjustRange(ValueAxis axis, Range range, double zoomFactor, double anchorValue) {

        if (axis == null || range == null) {
            return;
        }

        double rangeMinVal = range.getLowerBound() - range.getLength() * axis.getLowerMargin();
        double rangeMaxVal = range.getUpperBound() + range.getLength() * axis.getUpperMargin();
        double halfLength = axis.getRange().getLength() * zoomFactor / 2;
        double zoomedMinVal = anchorValue - halfLength;
        double zoomedMaxVal = anchorValue + halfLength;
        double adjMinVal = zoomedMinVal;
        if (zoomedMinVal < rangeMinVal) {
            adjMinVal = rangeMinVal;
            zoomedMaxVal += rangeMinVal - zoomedMinVal;
        }
        double adjMaxVal = zoomedMaxVal;
        if (zoomedMaxVal > rangeMaxVal) {
            adjMaxVal = rangeMaxVal;
            zoomedMinVal -= zoomedMaxVal - rangeMaxVal;
            adjMinVal = Math.max(zoomedMinVal, rangeMinVal);
        }

        Range adjusted = new Range(adjMinVal, adjMaxVal);
        axis.setRange(adjusted);
    }
    
}
