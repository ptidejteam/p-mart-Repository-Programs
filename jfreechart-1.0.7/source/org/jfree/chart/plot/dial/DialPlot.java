/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -------------
 * DialPlot.java
 * -------------
 * (C) Copyright 2006, 2007, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 03-Nov-2006 : Version 1 (DG);
 * 08-Mar-2007 : Fix in hashCode() (DG);
 * 17-Oct-2007 : Fixed listener registration/deregistration bugs (DG);
 * 24-Oct-2007 : Maintain pointers in their own list, so they can be
 *               drawn after other layers (DG);
 * 
 */

package org.jfree.chart.plot.dial;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.ValueDataset;
import org.jfree.util.ObjectList;
import org.jfree.util.ObjectUtilities;

/**
 * A dial plot.
 */
public class DialPlot extends Plot implements DialLayerChangeListener {

    /**
     * The background layer (optional).
     */
    private DialLayer background;
    
    /**
     * The needle cap (optional).
     */
    private DialLayer cap;
    
    /**
     * The dial frame.
     */
    private DialFrame dialFrame;
    
    /**
     * The dataset(s) for the dial plot.
     */
    private ObjectList datasets;
    
    /**
     * The scale(s) for the dial plot. 
     */
    private ObjectList scales;
    
    /** Storage for keys that map datasets to scales. */
    private ObjectList datasetToScaleMap;

    /**
     * The drawing layers for the dial plot.
     */
    private List layers;
    
    /** 
     * The pointer(s) for the dial.
     */
    private List pointers;
    
    /**
     * The x-coordinate for the view window.
     */
    private double viewX;
    
    /**
     * The y-coordinate for the view window.
     */
    private double viewY;
    
    /**
     * The width of the view window, expressed as a percentage.
     */
    private double viewW;
    
    /**
     * The height of the view window, expressed as a percentage.
     */
    private double viewH;
    
    /** 
     * Creates a new instance of <code>DialPlot</code>.
     */
    public DialPlot() {
        this(null);    
    }
    
    /** 
     * Creates a new instance of <code>DialPlot</code>.
     * 
     * @param dataset  the dataset (<code>null</code> permitted).
     */
    public DialPlot(ValueDataset dataset) {
        this.background = null;
        this.cap = null;
        this.dialFrame = new ArcDialFrame();
        this.datasets = new ObjectList();
        if (dataset != null) {
            this.setDataset(dataset);  
        }
        this.scales = new ObjectList();
        this.datasetToScaleMap = new ObjectList();
        this.layers = new java.util.ArrayList();
        this.pointers = new java.util.ArrayList();
        this.viewX = 0.0;
        this.viewY = 0.0;
        this.viewW = 1.0;
        this.viewH = 1.0;
    }

    /**
     * Returns the background.
     *
     * @return The background (possibly <code>null</code>).
     *
     * @see #setBackground(DialLayer)
     */
    public DialLayer getBackground() {
        return this.background;
    }
    
    /**
     * Sets the background layer and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param background  the background layer (<code>null</code> permitted).
     *
     * @see #getBackground()
     */
    public void setBackground(DialLayer background) {
        if (this.background != null) {
            this.background.removeChangeListener(this);
        }
        this.background = background;
        if (background != null) {
            background.addChangeListener(this);
        }
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the cap.
     *
     * @return The cap (possibly <code>null</code>).
     *
     * @see #setCap(DialLayer)
     */
    public DialLayer getCap() {
        return this.cap;
    }
    
    /**
     * Sets the cap and sends a {@link PlotChangeEvent} to all registered 
     * listeners.
     *
     * @param cap  the cap (<code>null</code> permitted).
     *
     * @see #getCap()
     */
    public void setCap(DialLayer cap) {
        if (this.cap != null) {
            this.cap.removeChangeListener(this);
        }
        this.cap = cap;
        if (cap != null) {
            cap.addChangeListener(this);
        }
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the dial's frame.
     *
     * @return The dial's frame (never <code>null</code>).
     *
     * @see #setDialFrame(DialFrame)
     */
    public DialFrame getDialFrame() {
        return this.dialFrame;
    }
    
    /**
     * Sets the dial's frame and sends a {@link PlotChangeEvent} to all 
     * registered listeners.
     *
     * @param frame  the frame (<code>null</code> not permitted).
     *
     * @see #getDialFrame()
     */
    public void setDialFrame(DialFrame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("Null 'frame' argument.");
        }
        this.dialFrame.removeChangeListener(this);
        this.dialFrame = frame;
        frame.addChangeListener(this);
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the x-coordinate of the viewing rectangle.  This is specified
     * in the range 0.0 to 1.0, relative to the dial's framing rectangle.
     * 
     * @return The x-coordinate of the viewing rectangle.
     * 
     * @see #setView(double, double, double, double)
     */
    public double getViewX() {
        return this.viewX;
    }
    
    /**
     * Returns the y-coordinate of the viewing rectangle.  This is specified
     * in the range 0.0 to 1.0, relative to the dial's framing rectangle.
     * 
     * @return The y-coordinate of the viewing rectangle.
     * 
     * @see #setView(double, double, double, double)
     */
    public double getViewY() {
        return this.viewY;
    }
    
    /**
     * Returns the width of the viewing rectangle.  This is specified
     * in the range 0.0 to 1.0, relative to the dial's framing rectangle.
     * 
     * @return The width of the viewing rectangle.
     * 
     * @see #setView(double, double, double, double)
     */
    public double getViewWidth() {
        return this.viewW;
    }
    
    /**
     * Returns the height of the viewing rectangle.  This is specified
     * in the range 0.0 to 1.0, relative to the dial's framing rectangle.
     * 
     * @return The height of the viewing rectangle.
     * 
     * @see #setView(double, double, double, double)
     */
    public double getViewHeight() {
        return this.viewH;
    }
    
    /**
     * Sets the viewing rectangle, relative to the dial's framing rectangle,
     * and sends a {@link PlotChangeEvent} to all registered listeners.
     * 
     * @param x  the x-coordinate (in the range 0.0 to 1.0).
     * @param y  the y-coordinate (in the range 0.0 to 1.0).
     * @param w  the width (in the range 0.0 to 1.0).
     * @param h  the height (in the range 0.0 to 1.0).
     * 
     * @see #getViewX()
     * @see #getViewY()
     * @see #getViewWidth()
     * @see #getViewHeight()
     */
    public void setView(double x, double y, double w, double h) {
        this.viewX = x;
        this.viewY = y;
        this.viewW = w;
        this.viewH = h;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Adds a layer to the plot and sends a {@link PlotChangeEvent} to all 
     * registered listeners.
     * 
     * @param layer  the layer (<code>null</code> not permitted).
     */
    public void addLayer(DialLayer layer) {
        if (layer == null) {
            throw new IllegalArgumentException("Null 'layer' argument.");
        }
        this.layers.add(layer);
        layer.addChangeListener(this);
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the index for the specified layer.
     * 
     * @param layer  the layer (<code>null</code> not permitted).
     * 
     * @return The layer index.
     */
    public int getLayerIndex(DialLayer layer) {
        if (layer == null) {
            throw new IllegalArgumentException("Null 'layer' argument.");
        }
        return this.layers.indexOf(layer);
    }
    
    /**
     * Removes the layer at the specified index and sends a 
     * {@link PlotChangeEvent} to all registered listeners.
     * 
     * @param index  the index.
     */
    public void removeLayer(int index) {
        DialLayer layer = (DialLayer) this.layers.get(index);
        if (layer != null) {
            layer.removeChangeListener(this);
        }
        this.layers.remove(index);
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Removes the specified layer and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     * 
     * @param layer  the layer (<code>null</code> not permitted).
     */
    public void removeLayer(DialLayer layer) {
        // defer argument checking
        removeLayer(getLayerIndex(layer));
    }
    
    /**
     * Adds a pointer to the plot and sends a {@link PlotChangeEvent} to all 
     * registered listeners.
     * 
     * @param pointer  the pointer (<code>null</code> not permitted).
     */
    public void addPointer(DialPointer pointer) {
        if (pointer == null) {
            throw new IllegalArgumentException("Null 'pointer' argument.");
        }
        this.pointers.add(pointer);
        pointer.addChangeListener(this);
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the index for the specified pointer.
     * 
     * @param pointer  the pointer (<code>null</code> not permitted).
     * 
     * @return The pointer index.
     */
    public int getPointerIndex(DialPointer pointer) {
        if (pointer == null) {
            throw new IllegalArgumentException("Null 'pointer' argument.");
        }
        return this.pointers.indexOf(pointer);
    }
    
    /**
     * Removes the pointer at the specified index and sends a 
     * {@link PlotChangeEvent} to all registered listeners.
     * 
     * @param index  the index.
     */
    public void removePointer(int index) {
        DialPointer pointer = (DialPointer) this.pointers.get(index);
        if (pointer != null) {
            pointer.removeChangeListener(this);
        }
        this.pointers.remove(index);
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Removes the specified pointer and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     * 
     * @param pointer  the pointer (<code>null</code> not permitted).
     */
    public void removePointer(DialPointer pointer) {
        // defer argument checking
        removeLayer(getPointerIndex(pointer));
    }

    /**
     * Returns the dial pointer that is associated with the specified
     * dataset, or <code>null</code>.
     * 
     * @param datasetIndex  the dataset index.
     * 
     * @return The pointer.
     */
    public DialPointer getPointerForDataset(int datasetIndex) {
        DialPointer result = null;
        Iterator iterator = this.pointers.iterator();
        while (iterator.hasNext()) {
            DialPointer p = (DialPointer) iterator.next();
            if (p.getDatasetIndex() == datasetIndex) {
                return p;
            }
        }
        return result;
    }
    
    /**
     * Returns the primary dataset for the plot.
     *
     * @return The primary dataset (possibly <code>null</code>).
     */
    public ValueDataset getDataset() {
        return getDataset(0);
    }

    /**
     * Returns the dataset at the given index.
     *
     * @param index  the dataset index.
     *
     * @return The dataset (possibly <code>null</code>).
     */
    public ValueDataset getDataset(int index) {
        ValueDataset result = null;
        if (this.datasets.size() > index) {
            result = (ValueDataset) this.datasets.get(index);
        }
        return result;
    }

    /**
     * Sets the dataset for the plot, replacing the existing dataset, if there 
     * is one, and sends a {@link PlotChangeEvent} to all registered 
     * listeners.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     */
    public void setDataset(ValueDataset dataset) {
        setDataset(0, dataset);
    }

    /**
     * Sets a dataset for the plot.
     *
     * @param index  the dataset index.
     * @param dataset  the dataset (<code>null</code> permitted).
     */
    public void setDataset(int index, ValueDataset dataset) {
        
        ValueDataset existing = (ValueDataset) this.datasets.get(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.datasets.set(index, dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        
        // send a dataset change event to self...
        DatasetChangeEvent event = new DatasetChangeEvent(this, dataset);
        datasetChanged(event);
        
    }

    /**
     * Returns the number of datasets.
     *
     * @return The number of datasets.
     */
    public int getDatasetCount() {
        return this.datasets.size();
    }    
    
    /**
     * Draws the plot.  This method is usually called by the {@link JFreeChart}
     * instance that manages the plot.
     * 
     * @param g2  the graphics target.
     * @param area  the area in which the plot should be drawn.
     * @param anchor  the anchor point (typically the last point that the 
     *     mouse clicked on, <code>null</code> is permitted).
     * @param parentState  the state for the parent plot (if any).
     * @param info  used to collect plot rendering info (<code>null</code> 
     *     permitted).
     */
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, 
            PlotState parentState, PlotRenderingInfo info) {
        
        // first, expand the viewing area into a drawing frame
        Rectangle2D frame = viewToFrame(area);
        
        // draw the background if there is one...
        if (this.background != null && this.background.isVisible()) {
            if (this.background.isClippedToWindow()) {
                Shape savedClip = g2.getClip();
                g2.setClip(this.dialFrame.getWindow(frame));
                this.background.draw(g2, this, frame, area);
                g2.setClip(savedClip);
            }
            else {
                this.background.draw(g2, this, frame, area);
            }
        }
        
        Iterator iterator = this.layers.iterator();
        while (iterator.hasNext()) {
            DialLayer current = (DialLayer) iterator.next();
            if (current.isVisible()) {
                if (current.isClippedToWindow()) {
                    Shape savedClip = g2.getClip();
                    g2.setClip(this.dialFrame.getWindow(frame));
                    current.draw(g2, this, frame, area);
                    g2.setClip(savedClip);
                }
                else {
                    current.draw(g2, this, frame, area);
                }
            }
        }
        
        // draw the pointers
        iterator = this.pointers.iterator();
        while (iterator.hasNext()) {
            DialPointer current = (DialPointer) iterator.next();
            if (current.isVisible()) {
                if (current.isClippedToWindow()) {
                    Shape savedClip = g2.getClip();
                    g2.setClip(this.dialFrame.getWindow(frame));
                    current.draw(g2, this, frame, area);
                    g2.setClip(savedClip);
                }
                else {
                    current.draw(g2, this, frame, area);
                }
            }
        }

        // draw the cap if there is one...
        if (this.cap != null && this.cap.isVisible()) {
            if (this.cap.isClippedToWindow()) {
                Shape savedClip = g2.getClip();
                g2.setClip(this.dialFrame.getWindow(frame));
                this.cap.draw(g2, this, frame, area);
                g2.setClip(savedClip);
            }
            else {
                this.cap.draw(g2, this, frame, area);
            }
        }
        
        if (this.dialFrame.isVisible()) {
            this.dialFrame.draw(g2, this, frame, area);
        }
        
    }
    
    /**
     * Returns the frame surrounding the specified view rectangle.
     * 
     * @param view  the view rectangle (<code>null</code> not permitted).
     * 
     * @return The frame rectangle.
     */
    private Rectangle2D viewToFrame(Rectangle2D view) {
        double width = view.getWidth() / this.viewW;
        double height = view.getHeight() / this.viewH;
        double x = view.getX() - (width * this.viewX);
        double y = view.getY() - (height * this.viewY);
        return new Rectangle2D.Double(x, y, width, height);
    }
    
    /**
     * Returns the value from the specified dataset.
     * 
     * @param datasetIndex  the dataset index.
     * 
     * @return The data value.
     */
    public double getValue(int datasetIndex) {
        double result = Double.NaN;
        ValueDataset dataset = getDataset(datasetIndex);
        if (dataset != null) {
            Number n = dataset.getValue();
            if (n != null) {
                result = n.doubleValue();
            }
        }
        return result;
    }
    
    /**
     * Adds a dial scale to the plot and sends a {@link PlotChangeEvent} to 
     * all registered listeners.
     * 
     * @param index  the scale index.
     * @param scale  the scale (<code>null</code> not permitted).
     */
    public void addScale(int index, DialScale scale) {
        if (scale == null) {
            throw new IllegalArgumentException("Null 'scale' argument.");
        }
        DialScale existing = (DialScale) this.scales.get(index);
        if (existing != null) {
            removeLayer(existing);
        }
        this.layers.add(scale);
        this.scales.set(index, scale);
        scale.addChangeListener(this);
        notifyListeners(new PlotChangeEvent(this));         
    }
    
    /**
     * Returns the scale at the given index.
     *
     * @param index  the scale index.
     *
     * @return The scale (possibly <code>null</code>).
     */
    public DialScale getScale(int index) {
        DialScale result = null;
        if (this.scales.size() > index) {
            result = (DialScale) this.scales.get(index);
        }
        return result;
    }

    /**
     * Maps a dataset to a particular scale.
     * 
     * @param index  the dataset index (zero-based).
     * @param scaleIndex  the scale index (zero-based).
     */
    public void mapDatasetToScale(int index, int scaleIndex) {
        this.datasetToScaleMap.set(index, new Integer(scaleIndex));  
        notifyListeners(new PlotChangeEvent(this)); 
    }
    
    /**
     * Returns the dial scale for a specific dataset.
     * 
     * @param datasetIndex  the dataset index.
     * 
     * @return The dial scale.
     */
    public DialScale getScaleForDataset(int datasetIndex) {
        DialScale result = (DialScale) this.scales.get(0);    
        Integer scaleIndex = (Integer) this.datasetToScaleMap.get(datasetIndex);
        if (scaleIndex != null) {
            result = getScale(scaleIndex.intValue());
        }
        return result;    
    }
    
    /**
     * A utility method that computes a rectangle using relative radius values.
     * 
     * @param rect  the reference rectangle (<code>null</code> not permitted).
     * @param radiusW  the width radius (must be > 0.0)
     * @param radiusH  the height radius.
     * 
     * @return A new rectangle.
     */
    public static Rectangle2D rectangleByRadius(Rectangle2D rect, 
            double radiusW, double radiusH) {
        if (rect == null) {
            throw new IllegalArgumentException("Null 'rect' argument.");
        }
        double x = rect.getCenterX();
        double y = rect.getCenterY();
        double w = rect.getWidth() * radiusW;
        double h = rect.getHeight() * radiusH;
        return new Rectangle2D.Double(x - w / 2.0, y - h / 2.0, w, h);
    }
    
    /**
     * Receives notification when a layer has changed, and responds by 
     * forwarding a {@link PlotChangeEvent} to all registered listeners.
     * 
     * @param event  the event.
     */
    public void dialLayerChanged(DialLayerChangeEvent event) {
        this.notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Tests this <code>DialPlot</code> instance for equality with an 
     * arbitrary object.  The plot's dataset(s) is (are) not included in 
     * the test.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DialPlot)) {
            return false;
        }
        DialPlot that = (DialPlot) obj;
        if (!ObjectUtilities.equal(this.background, that.background)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.cap, that.cap)) {
            return false;
        }
        if (!this.dialFrame.equals(that.dialFrame)) {
            return false;
        }
        if (this.viewX != that.viewX) {
            return false;
        }
        if (this.viewY != that.viewY) {
            return false;
        }
        if (this.viewW != that.viewW) {
            return false;
        }
        if (this.viewH != that.viewH) {
            return false;
        }
        if (!this.layers.equals(that.layers)) {
            return false;
        }
        if (!this.pointers.equals(that.pointers)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns a hash code for this instance.
     * 
     * @return The hash code.
     */
    public int hashCode() {
        int result = 193;
        result = 37 * result + ObjectUtilities.hashCode(this.background);
        result = 37 * result + ObjectUtilities.hashCode(this.cap);
        result = 37 * result + this.dialFrame.hashCode();
        long temp = Double.doubleToLongBits(this.viewX);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.viewY);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.viewW);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.viewH);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
    
    /**
     * Returns the plot type.
     * 
     * @return <code>"DialPlot"</code>
     */
    public String getPlotType() {
        return "DialPlot";
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
    private void readObject(ObjectInputStream stream) 
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }

    
}
