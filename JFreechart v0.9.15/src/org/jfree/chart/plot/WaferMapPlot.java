/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Object Refinery Limited and Contributors.
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
 * -----------------
 * WaferMapPlot.java
 * -----------------
 *
 * (C) Copyright 2003, by Robert Redburn and Contributors.
 *
 * Original Author:  Robert Redburn;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * Changes
 * -------
 * 25-Nov-2003 : Version 1 contributed by Robert Redburn (DG);
 *
 */
package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ResourceBundle;

import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.renderer.WaferMapRenderer;
import org.jfree.data.WaferMapDataset;

/**
 * A wafer map plot.
 */
public class WaferMapPlot extends Plot implements PropertyChangeListener,
                                                  Cloneable,
                                                  Serializable {

    /** The default grid line stroke. */
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5f,
        BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_BEVEL,
        0.0f,
        new float[] {2.0f, 2.0f},
        0.0f);

    /** The default grid line paint. */
    public static final Paint DEFAULT_GRIDLINE_PAINT = Color.lightGray;

    /** The default crosshair visibility. */
    public static final boolean DEFAULT_CROSSHAIR_VISIBLE = false;

    /** The default crosshair stroke. */
    public static final Stroke DEFAULT_CROSSHAIR_STROKE = DEFAULT_GRIDLINE_STROKE;

    /** The default crosshair paint. */
    public static final Paint DEFAULT_CROSSHAIR_PAINT = Color.blue;

    /** The resourceBundle for the localization. */
    static protected ResourceBundle localizationResources = 
                            ResourceBundle.getBundle("org.jfree.chart.plot.LocalizationBundle");

    /** The plot orientation. 
     *  vertical = notch down
     *  horizontal = notch right
     */
    private PlotOrientation orientation;

    /** The dataset. */
    private WaferMapDataset dataset;

    /** Object responsible for drawing the visual representation of each point on the plot. */
    private WaferMapRenderer renderer;

    /**
     * constructor with a dataset only
     * 
     * @param dataset
     */
    public WaferMapPlot(WaferMapDataset dataset) {
        this(dataset, null);
    }

    /**
     * Creates a new plot.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     * @param renderer  the renderer (<code>null</code> permitted).
     */
    public WaferMapPlot(WaferMapDataset dataset,
                        WaferMapRenderer renderer) {

        super();

        this.orientation = PlotOrientation.VERTICAL;
        
        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }

        this.renderer = renderer;
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addPropertyChangeListener(this);
        }

    }

    /**
     * Returns the plot type as a string.
     *
     * @return a short string describing the type of plot.
     */
    public String getPlotType() {
        return("WMAP_Plot");
    }

    /**
     * Sets the item renderer, and notifies all listeners of a change to the plot.
     * <P>
     * If the renderer is set to <code>null</code>, no chart will be drawn.
     *
     * @param renderer  the new renderer (<code>null</code> permitted).
     */
    public void setRenderer(WaferMapRenderer renderer) {

        if (this.renderer != null) {
            this.renderer.removePropertyChangeListener(this);
        }

        this.renderer = renderer;
        if (renderer != null) {
            renderer.setPlot(this);
        }

        notifyListeners(new PlotChangeEvent(this));

    }
    
    /**
     * Draws the wafermap view.
     * 
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param state  the plot state.
     * @param info  the plot rendering info.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, PlotState state, PlotRenderingInfo info) {

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

        drawChipGrid(g2, plotArea);       
        drawWaferEdge(g2, plotArea);
        
    }

    /**
     * Calculates and draws the chip locations on the wafer.
     * 
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     */
    private void drawChipGrid(Graphics2D g2, Rectangle2D plotArea) {
        
        Shape savedClip = g2.getClip();
        g2.setClip(getWaferEdge(plotArea));
        Rectangle2D chip = new Rectangle2D.Double();
        int xchips = 35;
        int ychips = 20;
        double space = 1d;
        if (dataset != null) {
            xchips = dataset.getMaxChipX() + 2;
            ychips = dataset.getMaxChipY() + 2;
            space = dataset.getChipSpace();
        }
        double startX = plotArea.getX();
        double startY = plotArea.getY();
        double chipWidth=1d;
        double chipHeight=1d;
        if (plotArea.getWidth() != plotArea.getHeight()) {
            double major = 0d;
            double minor = 0d;
            if (plotArea.getWidth() > plotArea.getHeight()) {
                major = plotArea.getWidth();
                minor = plotArea.getHeight();
            } 
            else {
                major = plotArea.getHeight();
                minor = plotArea.getWidth();
            } 
            //set upperLeft point
            if (plotArea.getWidth() == minor) { // x is minor
                startY += (major - minor)/2;
                chipWidth = (plotArea.getWidth() - (space * xchips - 1)) / xchips;
                chipHeight = (plotArea.getWidth() - (space * ychips - 1)) / ychips;
            }
            else { // y is minor
                startX += (major - minor) / 2;
                chipWidth = (plotArea.getHeight() - (space * xchips - 1)) / xchips;
                chipHeight = (plotArea.getHeight() - (space * ychips - 1)) / ychips;
            }
        }
        
        for (int x = 1; x <= xchips; x++) {
            double upperLeftX = (startX - chipWidth) + (chipWidth * x) + (space * (x - 1));
            for (int y = 1; y <= ychips; y++) {
                double upperLeftY = (startY - chipHeight) + (chipHeight * y) + (space * (y - 1));
                chip.setFrame(upperLeftX, upperLeftY, chipWidth, chipHeight);
                g2.setColor(Color.white);
                if (dataset.getChipValue(x - 1, ychips - y - 1) != null) {
                    g2.setPaint(renderer.getChipColor(dataset.getChipValue(x - 1, ychips - y - 1)));
                } 
                g2.fill(chip);
                g2.setColor(Color.lightGray);
                g2.draw(chip);
            }
        }
        g2.setClip(savedClip);
    }

    /**
     * Calculates the location of the waferedge.
     * 
     * @param plotArea
     * @return
     */
    private Ellipse2D getWaferEdge(Rectangle2D plotArea) {
        Ellipse2D edge = new Ellipse2D.Double();
        double diameter = plotArea.getWidth();
        double upperLeftX = plotArea.getX();
        double upperLeftY = plotArea.getY();
        //get major dimension
        if (plotArea.getWidth() != plotArea.getHeight()) {
            double major = 0d;
            double minor = 0d;
            if (plotArea.getWidth() > plotArea.getHeight()) {
                major = plotArea.getWidth();
                minor = plotArea.getHeight();
            } 
            else {
                major = plotArea.getHeight();
                minor = plotArea.getWidth();
            } 
            //ellipse diameter is the minor dimension
            diameter = minor;
            //set upperLeft point
            if (plotArea.getWidth() == minor) { // x is minor
                upperLeftY = plotArea.getY() + (major - minor) / 2;
            }
            else { // y is minor
                upperLeftX = plotArea.getX() + (major - minor) / 2;
            }
        }
        edge.setFrame(upperLeftX, upperLeftY, diameter, diameter); 
        return edge;        
    }

    /**
     * Draws the waferedge, including the notch.
     * 
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     */
    private void drawWaferEdge(Graphics2D g2, Rectangle2D plotArea) {
        // draw the wafer
        Ellipse2D waferEdge = getWaferEdge(plotArea);
        g2.setColor(Color.black);
        g2.draw(waferEdge);
        // calculate and draw the notch
        // horizontal orientation is considered notch right
        // vertical orientation is considered notch down
        Arc2D notch = null;
        Rectangle2D waferFrame = waferEdge.getFrame();
        double notchDiameter = waferFrame.getWidth() * 0.04;
        if (orientation == PlotOrientation.HORIZONTAL) {
            Rectangle2D notchFrame = 
                new Rectangle2D.Double(waferFrame.getX() 
                                       + waferFrame.getWidth() - (notchDiameter / 2), 
                                       waferFrame.getY()
                                       + (waferFrame.getHeight() / 2) - (notchDiameter / 2),
                                       notchDiameter, notchDiameter);
            notch = new Arc2D.Double(notchFrame, 90d, 180d, Arc2D.OPEN);
        }
        else {
            Rectangle2D notchFrame = 
                new Rectangle2D.Double(waferFrame.getX()
                                       + (waferFrame.getWidth() / 2) - (notchDiameter / 2), 
                                       waferFrame.getY() 
                                       + waferFrame.getHeight() - (notchDiameter / 2),
                                       notchDiameter, notchDiameter);
            notch = new Arc2D.Double(notchFrame, 0d, 180d, Arc2D.OPEN);        
        }
        g2.setColor(Color.white);
        g2.fill(notch);
        g2.setColor(Color.black);
        g2.draw(notch);
        
    }

    /**
     * Returns the dataset
     * 
     * @return wafermapdataset
     */
    public WaferMapDataset getDataset() {
        return this.dataset;
    }

    /**
     * Return the legend items from the renderer.
     * 
     * @return legenditemcollection
     */
    public LegendItemCollection getLegendItems() {
        return renderer.getLegendCollection();
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
        
    }
    
    /**
     * Notifies all registered listeners of a property change.
     * <P>
     * One source of property change events is the plot's renderer.
     *
     * @param event  information about the property change.
     */
    public void propertyChange(PropertyChangeEvent event) {
        notifyListeners(new PlotChangeEvent(this));
    }

}
