/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2006, by Object Refinery Limited and Contributors.
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
 * --------------------------
 * StackedXYAreaRenderer.java
 * --------------------------
 * (C) Copyright 2003-2006, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson;
 * Contributor(s):   Christian W. Zuckschwerdt;
 *                   David Gilbert (for Object Refinery Limited);
 *
 * $Id: StackedXYAreaRenderer.java,v 1.1 2007/10/10 20:29:43 vauchers Exp $
 *
 * Changes:
 * --------
 * 27-Jul-2003 : Initial version (RA);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 18-Aug-2003 : Now handles null values (RA);
 * 20-Aug-2003 : Implemented Cloneable, PublicCloneable and Serializable (DG);
 * 22-Sep-2003 : Changed to be a two pass renderer with optional shape Paint 
 *               and Stroke (RA);
 * 07-Oct-2003 : Added renderer state (DG);
 * 10-Feb-2004 : Updated state object and changed drawItem() method to make 
 *               overriding easier (DG);
 * 25-Feb-2004 : Replaced CrosshairInfo with CrosshairState.  Renamed 
 *               XYToolTipGenerator --> XYItemLabelGenerator (DG);
 * 15-Jul-2004 : Switched getX() with getXValue() and getY() with 
 *               getYValue() (DG);
 * 10-Sep-2004 : Removed getRangeType() method (DG);
 * 11-Nov-2004 : Now uses ShapeUtilities to translate shapes (DG);
 * 06-Jan-2005 : Override equals() (DG);
 * 07-Jan-2005 : Update for method name changes in DatasetUtilities (DG);
 * 28-Mar-2005 : Use getXValue() and getYValue() from dataset (DG);
 * 06-Jun-2005 : Fixed null pointer exception, plus problems with equals() and
 *               serialization (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 10-Nov-2006 : Fixed bug 1593156, NullPointerException with line 
 *               plotting (DG);
 * 
 */

package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Stack;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

/**
 * A stacked area renderer for the {@link XYPlot} class.
 * <br><br>
 * SPECIAL NOTE:  This renderer does not currently handle negative data values
 * correctly.  This should get fixed at some point, but the current workaround
 * is to use the {@link StackedXYAreaRenderer2} class instead.
 */
public class StackedXYAreaRenderer extends XYAreaRenderer 
                                   implements Cloneable, 
                                              PublicCloneable,
                                              Serializable {
    
    /** For serialization. */
    private static final long serialVersionUID = 5217394318178570889L;
     
     /**
     * A state object for use by this renderer.
     */
    static class StackedXYAreaRendererState extends XYItemRendererState {
        
        /** The area for the current series. */
        private Polygon seriesArea;
        
        /** The line. */
        private Line2D line;
        
        /** The points from the last series. */
        private Stack lastSeriesPoints;
        
        /** The points for the current series. */
        private Stack currentSeriesPoints;
        
        /**
         * Creates a new state for the renderer.
         * 
         * @param info  the plot rendering info.
         */
        public StackedXYAreaRendererState(PlotRenderingInfo info) {
            super(info);
            this.seriesArea = null;
            this.line = new Line2D.Double();
            this.lastSeriesPoints = new Stack();
            this.currentSeriesPoints = new Stack();
        }
        
        /**
         * Returns the series area.
         * 
         * @return The series area.
         */
        public Polygon getSeriesArea() {
            return this.seriesArea;
        }
        
        /**
         * Sets the series area.
         * 
         * @param area  the area.
         */
        public void setSeriesArea(Polygon area) {
            this.seriesArea = area;
        }
        
        /**
         * Returns the working line.
         * 
         * @return The working line.
         */
        public Line2D getLine() {
            return this.line;
        }
        
        /**
         * Returns the current series points.
         * 
         * @return The current series points.
         */
        public Stack getCurrentSeriesPoints() {
            return this.currentSeriesPoints;
        }
        
        /**
         * Sets the current series points.
         * 
         * @param points  the points.
         */
        public void setCurrentSeriesPoints(Stack points) {
            this.currentSeriesPoints = points;
        }
    
        /**
         * Returns the last series points.
         * 
         * @return The last series points.
         */
        public Stack getLastSeriesPoints() {
            return this.lastSeriesPoints;
        }
        
        /**
         * Sets the last series points.
         * 
         * @param points  the points.
         */
        public void setLastSeriesPoints(Stack points) {
            this.lastSeriesPoints = points;
        }
    
    }

    /** 
     * Custom Paint for drawing all shapes, if null defaults to series shapes 
     */
    private transient Paint shapePaint = null;

    /** 
     * Custom Stroke for drawing all shapes, if null defaults to series 
     * strokes.
     */
    private transient Stroke shapeStroke = null;

    /**
     * Creates a new renderer.
     */
    public StackedXYAreaRenderer() {
        this(AREA);
    }
    /**
     * Constructs a new renderer.
     *
     * @param type  the type of the renderer.
     */
    public StackedXYAreaRenderer(int type) {
        this(type, null, null);
    }

    /**
     * Constructs a new renderer.  To specify the type of renderer, use one of 
     * the constants: <code>SHAPES</code>, <code>LINES</code>, 
     * <code>SHAPES_AND_LINES</code>, <code>AREA</code> or 
     * <code>AREA_AND_SHAPES</code>.
     *
     * @param type  the type of renderer.
     * @param labelGenerator  the tool tip generator to use (<code>null</code> 
     *                        is none).
     * @param urlGenerator  the URL generator (<code>null</code> permitted).
     */
    public StackedXYAreaRenderer(int type,
                                 XYToolTipGenerator labelGenerator, 
                                 XYURLGenerator urlGenerator) {

        super(type, labelGenerator, urlGenerator);
    }

    /**
     * Returns the paint used for rendering shapes, or <code>null</code> if 
     * using series paints.
     *
     * @return The Paint.
     */
    public Paint getShapePaint() {
        return this.shapePaint;
    }

    /**
     * Returns the stroke used for rendering shapes, or <code>null</code> if 
     * using series strokes.
     *
     * @return The stroke.
     */
    public Stroke getShapeStroke() {
        return this.shapeStroke;
    }

    /**
     * Sets the paint for rendering shapes.
     *
     * @param shapePaint  the paint (<code>null</code> permitted).
     */
    public void setShapePaint(Paint shapePaint) {
        this.shapePaint = shapePaint;
    }

    /**
     * Sets the stroke for rendering shapes.
     *
     * @param shapeStroke  the stroke (<code>null</code> permitted).
     */
    public void setShapeStroke(Stroke shapeStroke) {
        this.shapeStroke = shapeStroke;
    }

    /**
     * Initialises the renderer. This method will be called before the first
     * item is rendered, giving the renderer an opportunity to initialise any 
     * state information it wants to maintain.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param data  the data.
     * @param info  an optional info collection object to return data back to 
     *              the caller.
     *
     * @return A state object that should be passed to subsequent calls to the 
     *         drawItem() method.
     */
    public XYItemRendererState initialise(Graphics2D g2,
                                          Rectangle2D dataArea,
                                          XYPlot plot,
                                          XYDataset data,
                                          PlotRenderingInfo info) {

        return new StackedXYAreaRendererState(info);

    }

    /**
     * Returns the number of passes required by the renderer.
     * 
     * @return 2.
     */
    public int getPassCount() {
        return 2;
    }

    /**
     * Returns the range of values the renderer requires to display all the 
     * items from the specified dataset.
     * 
     * @param dataset  the dataset (<code>null</code> permitted).
     * 
     * @return The range ([0.0, 0.0] if the dataset contains no values, and 
     *         <code>null</code> if the dataset is <code>null</code>).
     *         
     * @throws ClassCastException if <code>dataset</code> is not an instance
     *         of {@link TableXYDataset}.
     */
    public Range findRangeBounds(XYDataset dataset) {
        if (dataset != null) {
            return DatasetUtilities.findStackedRangeBounds(
                (TableXYDataset) dataset);
        }
        else {
            return null;
        }
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information 
     *              etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  information about crosshairs on a plot.
     * @param pass  the pass index.
     * 
     * @throws ClassCastException if <code>state</code> is not an instance of
     *         <code>StackedXYAreaRendererState</code> or <code>dataset</code>
     *         is not an instance of {@link TableXYDataset}.
     */
    public void drawItem(Graphics2D g2,
                         XYItemRendererState state,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairState crosshairState,
                         int pass) {

        PlotOrientation orientation = plot.getOrientation();
        StackedXYAreaRendererState areaState 
            = (StackedXYAreaRendererState) state;
        // Get the item count for the series, so that we can know which is the
        // end of the series.
        TableXYDataset tdataset = (TableXYDataset) dataset;
        int itemCount = tdataset.getItemCount();

        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        boolean nullPoint = false;
        if (Double.isNaN(y1)) {
            y1 = 0.0;
            nullPoint = true;
        }

        //  Get height adjustment based on stack and translate to Java2D values
        double ph1 = getPreviousHeight(tdataset, series, item);
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, 
                plot.getDomainAxisEdge());
        double transY1 = rangeAxis.valueToJava2D(y1 + ph1, dataArea, 
                plot.getRangeAxisEdge());

        //  Get series Paint and Stroke
        Paint seriesPaint = getItemPaint(series, item);
        Stroke seriesStroke = getItemStroke(series, item);

        if (pass == 0) {
            //  On first pass render the areas, line and outlines

            if (item == 0) {
                // Create a new Area for the series
                areaState.setSeriesArea(new Polygon());
                areaState.setLastSeriesPoints(
                        areaState.getCurrentSeriesPoints());
                areaState.setCurrentSeriesPoints(new Stack());

                // start from previous height (ph1)
                double transY2 = rangeAxis.valueToJava2D(ph1, dataArea, 
                        plot.getRangeAxisEdge());

                // The first point is (x, 0)
                if (orientation == PlotOrientation.VERTICAL) {
                    areaState.getSeriesArea().addPoint((int) transX1, 
                            (int) transY2);
                } 
                else if (orientation == PlotOrientation.HORIZONTAL) {
                    areaState.getSeriesArea().addPoint((int) transY2, 
                            (int) transX1);
                }
            }

            // Add each point to Area (x, y)
            if (orientation == PlotOrientation.VERTICAL) {
                Point point = new Point((int) transX1, (int) transY1);
                areaState.getSeriesArea().addPoint((int) point.getX(), 
                        (int) point.getY());
                areaState.getCurrentSeriesPoints().push(point);
            }
            else if (orientation == PlotOrientation.HORIZONTAL) {
                areaState.getSeriesArea().addPoint((int) transY1, 
                        (int) transX1);
            }

            if (getPlotLines()) {
                if (item > 0) {
                    // get the previous data point...
                    double x0 = dataset.getXValue(series, item - 1);
                    double y0 = dataset.getYValue(series, item - 1);
                    double ph0 = getPreviousHeight(tdataset, series, item - 1);
                    double transX0 = domainAxis.valueToJava2D(x0, dataArea, 
                            plot.getDomainAxisEdge());
                    double transY0 = rangeAxis.valueToJava2D(y0 + ph0, 
                            dataArea, plot.getRangeAxisEdge());

                    if (orientation == PlotOrientation.VERTICAL) {
                        areaState.getLine().setLine(transX0, transY0, transX1, 
                                transY1);
                    }
                    else if (orientation == PlotOrientation.HORIZONTAL) {
                        areaState.getLine().setLine(transY0, transX0, transY1, 
                                transX1);
                    }
                    g2.draw(areaState.getLine());
                }
            }

            // Check if the item is the last item for the series and number of 
            // items > 0.  We can't draw an area for a single point.
            if (getPlotArea() && item > 0 && item == (itemCount - 1)) {

                double transY2 = rangeAxis.valueToJava2D(ph1, dataArea, 
                        plot.getRangeAxisEdge());

                if (orientation == PlotOrientation.VERTICAL) {
                    // Add the last point (x,0)
                    areaState.getSeriesArea().addPoint((int) transX1, 
                            (int) transY2);
                }
                else if (orientation == PlotOrientation.HORIZONTAL) {
                    // Add the last point (x,0)
                    areaState.getSeriesArea().addPoint((int) transY2, 
                            (int) transX1);
                }

                // Add points from last series to complete the base of the 
                // polygon
                if (series != 0) {
                    Stack points = areaState.getLastSeriesPoints();
                    while (!points.empty()) {
                        Point point = (Point) points.pop();
                        areaState.getSeriesArea().addPoint((int) point.getX(), 
                                (int) point.getY());
                    }
                }

                //  Fill the polygon
                g2.setPaint(seriesPaint);
                g2.setStroke(seriesStroke);
                g2.fill(areaState.getSeriesArea());

                //  Draw an outline around the Area.
                if (isOutline()) {
                    g2.setStroke(getSeriesOutlineStroke(series));
                    g2.setPaint(getSeriesOutlinePaint(series));
                    g2.draw(areaState.getSeriesArea());
                }
            }

            updateCrosshairValues(crosshairState, x1, y1, transX1, transY1, 
                    orientation);

        } 
        else if (pass == 1) {
            // On second pass render shapes and collect entity and tooltip 
            // information

            Shape shape = null;
            if (getPlotShapes()) {
                shape = getItemShape(series, item);
                if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    shape = ShapeUtilities.createTranslatedShape(shape, 
                            transX1, transY1);
                } 
                else if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                    shape = ShapeUtilities.createTranslatedShape(shape, 
                            transY1, transX1);
                }
                if (!nullPoint) {
                    if (getShapePaint() != null) {
                        g2.setPaint(getShapePaint());
                    } 
                    else {
                        g2.setPaint(seriesPaint);
                    }
                    if (getShapeStroke() != null) {
                        g2.setStroke(getShapeStroke());
                    } 
                    else {
                        g2.setStroke(seriesStroke);
                    }
                    g2.draw(shape);
                }
            } 
            else {
                if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    shape = new Rectangle2D.Double(transX1 - 3, transY1 - 3, 
                            6.0, 6.0);
                } 
                else if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                    shape = new Rectangle2D.Double(transY1 - 3, transX1 - 3, 
                            6.0, 6.0);
                }
            }

            // collect entity and tool tip information...
            if (state.getInfo() != null) {
                EntityCollection entities = state.getEntityCollection();
                if (entities != null && shape != null && !nullPoint) {
                    String tip = null;
                    XYToolTipGenerator generator 
                        = getToolTipGenerator(series, item);
                    if (generator != null) {
                        tip = generator.generateToolTip(dataset, series, item);
                    }
                    String url = null;
                    if (getURLGenerator() != null) {
                        url = getURLGenerator().generateURL(dataset, series, 
                                item);
                    }
                    XYItemEntity entity = new XYItemEntity(shape, dataset, 
                            series, item, tip, url);
                    entities.add(entity);
                }
            }

        }
    }

    /**
     * Calculates the stacked value of the all series up to, but not including 
     * <code>series</code> for the specified item. It returns 0.0 if 
     * <code>series</code> is the first series, i.e. 0.
     *
     * @param dataset  the dataset.
     * @param series  the series.
     * @param index  the index.
     *
     * @return The cumulative value for all series' values up to but excluding 
     *         <code>series</code> for <code>index</code>.
     */
    protected double getPreviousHeight(TableXYDataset dataset, 
                                       int series, int index) {
        double result = 0.0;
        for (int i = 0; i < series; i++) {
            double value = dataset.getYValue(i, index);
            if (!Double.isNaN(value)) {
                result += value;
            }
        }
        return result;
    }
    
    /**
     * Tests the renderer for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StackedXYAreaRenderer) || !super.equals(obj)) {
            return false;
        }
        StackedXYAreaRenderer that = (StackedXYAreaRenderer) obj;
        if (!PaintUtilities.equal(this.shapePaint, that.shapePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.shapeStroke, that.shapeStroke)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a clone of the renderer.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if the renderer cannot be cloned.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
        this.shapePaint = SerialUtilities.readPaint(stream);
        this.shapeStroke = SerialUtilities.readStroke(stream);
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
        SerialUtilities.writePaint(this.shapePaint, stream);
        SerialUtilities.writeStroke(this.shapeStroke, stream);
    }

}
