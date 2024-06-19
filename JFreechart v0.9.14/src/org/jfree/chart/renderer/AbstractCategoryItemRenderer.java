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
 * ---------------------------------
 * AbstractCategoryItemRenderer.java
 * ---------------------------------
 * (C) Copyright 2002, 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: AbstractCategoryItemRenderer.java,v 1.1 2007/10/10 19:19:08 vauchers Exp $
 *
 * Changes:
 * --------
 * 29-May-2002 : Version 1 (DG);
 * 06-Jun-2002 : Added accessor methods for the tool tip generator (DG);
 * 11-Jun-2002 : Made constructors protected (DG);
 * 26-Jun-2002 : Added axis to initialise method (DG);
 * 05-Aug-2002 : Added urlGenerator member variable plus accessors (RA);
 * 22-Aug-2002 : Added categoriesPaint attribute, based on code submitted by Janet Banks.
 *               This can be used when there is only one series, and you want each category
 *               item to have a different color (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 29-Oct-2002 : Fixed bug where background image for plot was not being drawn (DG);
 * 05-Nov-2002 : Replaced references to CategoryDataset with TableDataset (DG);
 * 26-Nov 2002 : Replaced the isStacked() method with getRangeType() (DG);
 * 09-Jan-2003 : Renamed grid-line methods (DG);
 * 17-Jan-2003 : Moved plot classes into separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 12-May-2003 : Modified to take into account the plot orientation (DG);
 * 12-Aug-2003 : Very minor javadoc corrections (DB)
 * 13-Aug-2003 : Implemented Cloneable (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 05-Nov-2003 : Fixed marker rendering bug (833623) (DG);
 *
 */

package org.jfree.chart.renderer;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.LegendItem;
import org.jfree.chart.Marker;
import org.jfree.chart.MarkerLabelPosition;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.data.CategoryDataset;
import org.jfree.data.Range;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.NumberUtils;
import org.jfree.util.ObjectList;
import org.jfree.util.ObjectUtils;

/**
 * An abstract base class that you can use to implement a new {@link CategoryItemRenderer}.
 * <p>
 * When you create a new {@link CategoryItemRenderer} you are not required to extend this class,
 * but it makes the job easier.
 *
 * @author David Gilbert
 */
public abstract class AbstractCategoryItemRenderer extends AbstractRenderer
                                                   implements CategoryItemRenderer, 
                                                              Cloneable,
                                                              Serializable {

    /** The plot that the renderer is assigned to. */
    private CategoryPlot plot;

    /** The item label generator for ALL series. */
    private CategoryItemLabelGenerator itemLabelGenerator;

    /** A list of item label generators (one per series). */
    private ObjectList itemLabelGeneratorList;

    /** The base item label generator. */
    private CategoryItemLabelGenerator baseItemLabelGenerator;

    /** The URL generator. */
    private CategoryURLGenerator itemURLGenerator;

    /** A list of item label generators (one per series). */
    private ObjectList itemURLGeneratorList;

    /** The base item label generator. */
    private CategoryURLGenerator baseItemURLGenerator;

    /** The number of rows in the dataset (temporary record). */
    private transient int rowCount;

    /** The number of columns in the dataset (temporary record). */
    private transient int columnCount;

    /** The item label anchor offset. */
    private double itemLabelAnchorOffset = 2.0;

    /**
     * Creates a new renderer with no tool tip generator and no URL generator.
     * <P>
     * The defaults (no tool tip or URL generators) have been chosen to minimise the processing
     * required to generate a default chart.  If you require tool tips or URLs, then you can
     * easily add the required generators.
     */
    protected AbstractCategoryItemRenderer() {
        this.itemLabelGenerator = null;
        this.itemLabelGeneratorList = new ObjectList();
        this.itemURLGenerator = null;
        this.itemURLGeneratorList = new ObjectList();
    }

    /**
     * Returns the plot that the renderer is currently assigned to.
     *
     * @return The plot.
     */
    public CategoryPlot getPlot() {
        return this.plot;
    }

    /**
     * Sets the plot that the renderer is currently assigned to.
     *
     * @param plot  the plot.
     */
    public void setPlot(CategoryPlot plot) {
        this.plot = plot;
    }

    /**
     * Returns the label generator for a data item.  This method just calls the
     * getSeriesItemLabelGenerator method, but you can override this behaviour if you want to.
     *
     * @param row  the row index (zero based).
     * @param column  the column index (zero based).
     *
     * @return The label generator.
     */
    public CategoryItemLabelGenerator getItemLabelGenerator(int row, int column) {
        return getSeriesItemLabelGenerator(row);
    }

    /**
     * Returns the label generator for a series.
     *
     * @param series  the series index (zero based).
     *
     * @return The label generator for the series.
     */
    public CategoryItemLabelGenerator getSeriesItemLabelGenerator(int series) {

        // return the generator for ALL series, if there is one...
        if (this.itemLabelGenerator != null) {
            return this.itemLabelGenerator;
        }

        // otherwise look up the generator table
        CategoryItemLabelGenerator generator
            = (CategoryItemLabelGenerator) this.itemLabelGeneratorList.get(series);
        if (generator == null) {
            generator = this.baseItemLabelGenerator;
        }
        return generator;

    }

    /**
     * Sets the item label generator for ALL series.
     *
     * @param generator  the generator.
     */
    public void setItemLabelGenerator(CategoryItemLabelGenerator generator) {
        this.itemLabelGenerator = generator;
    }

    /**
     * Sets the label generator for a series.
     *
     * @param series  the series index (zero based).
     * @param generator  the generator.
     */
    public void setSeriesItemLabelGenerator(int series, CategoryItemLabelGenerator generator) {
        this.itemLabelGeneratorList.set(series, generator);
    }

    /**
     * Returns the base item label generator.
     *
     * @return The base item label generator.
     */
    public CategoryItemLabelGenerator getBaseItemLabelGenerator() {
        return this.baseItemLabelGenerator;
    }

    /**
     * Sets the base item label generator.
     *
     * @param generator  the base item label generator.
     */
    public void setBaseItemLabelGenerator(CategoryItemLabelGenerator generator) {
        this.baseItemLabelGenerator = generator;
    }

    /**
     * Returns the URL generator for a data item.  This method just calls the
     * getSeriesItemURLGenerator method, but you can override this behaviour if you want to.
     *
     * @param row  the row index (zero based).
     * @param column  the column index (zero based).
     *
     * @return The URL generator.
     */
    public CategoryURLGenerator getItemURLGenerator(int row, int column) {
        return getSeriesItemURLGenerator(row);
    }

    /**
     * Returns the URL generator for a series.
     *
     * @param series  the series index (zero based).
     *
     * @return The URL generator for the series.
     */
    public CategoryURLGenerator getSeriesItemURLGenerator(int series) {

        // return the generator for ALL series, if there is one...
        if (this.itemURLGenerator != null) {
            return this.itemURLGenerator;
        }

        // otherwise look up the generator table
        CategoryURLGenerator generator
            = (CategoryURLGenerator) this.itemURLGeneratorList.get(series);
        if (generator == null) {
            generator = this.baseItemURLGenerator;
        }
        return generator;

    }

    /**
     * Sets the item URL generator for ALL series.
     *
     * @param generator  the generator.
     */
    public void setItemURLGenerator(CategoryURLGenerator generator) {
        this.itemURLGenerator = generator;
    }

    /**
     * Sets the URL generator for a series.
     *
     * @param series  the series index (zero based).
     * @param generator  the generator.
     */
    public void setSeriesItemURLGenerator(int series, CategoryURLGenerator generator) {
        this.itemURLGeneratorList.set(series, generator);
    }

    /**
     * Returns the base item URL generator.
     *
     * @return The item URL generator.
     */
    public CategoryURLGenerator getBaseItemURLGenerator() {
        return this.baseItemURLGenerator;
    }

    /**
     * Sets the base item URL generator.
     *
     * @param generator  the item URL generator.
     */
    public void setBaseItemURLGenerator(CategoryURLGenerator generator) {
        this.baseItemURLGenerator = generator;
    }

    /**
     * Returns the item label anchor offset.
     *
     * @return The offset.
     */
    public double getItemLabelAnchorOffset() {
        return this.itemLabelAnchorOffset;
    }

    /**
     * Sets the item label anchor offset.
     *
     * @param offset  the offset.
     */
    public void setItemLabelAnchorOffset(double offset) {
        this.itemLabelAnchorOffset = offset;
        firePropertyChanged("ItemLabelAnchorOffset", null, null);
    }

    /**
     * Returns the number of rows in the dataset.  This value is updated in the
     * {@link AbstractCategoryItemRenderer#initialise} method.
     *
     * @return the row count.
     */
    public int getRowCount() {
        return this.rowCount;
    }

    /**
     * Returns the number of columns in the dataset.  This value is updated in the
     * {@link AbstractCategoryItemRenderer#initialise} method.
     *
     * @return the column count.
     */
    public int getColumnCount() {
        return this.columnCount;
    }

    /**
     * Initialises the renderer and returns a state object that will be used for the
     * remainder of the drawing process for a single chart.  The state object allows 
     * for the fact that the renderer may be used simultaneously by multiple threads (each
     * thread will work with a separate state object).
     * <P>
     * Stores a reference to the {@link PlotRenderingInfo} object (which might be
     * <code>null</code>), and then sets the useCategoriesPaint flag according to the special case
     * conditions a) there is only one series and b) the categoriesPaint array is not null.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param index  the secondary index (<code>null</code> for primary renderer).
     * @param info  an object for returning information about the structure of the plot
     *              (<code>null</code> permitted).
     * 
     * @return The renderer state.
     *
     */
    public CategoryItemRendererState initialise(Graphics2D g2, 
                                                Rectangle2D dataArea,
                                                CategoryPlot plot, 
                                                Integer index,
                                                PlotRenderingInfo info) {

        setPlot(plot);
        CategoryDataset data = getDataset(plot, index);
        if (data != null) {
            this.rowCount = data.getRowCount();
            this.columnCount = data.getColumnCount();
        }
        else {
            this.rowCount = 0;
            this.columnCount = 0;
        }
        return new CategoryItemRendererState(info);

    }

    /**
     * Returns the range type for the renderer.
     * <p>
     * The default implementation returns <code>STANDARD</code>, subclasses may override this
     * behaviour.
     * <p>
     * The {@link CategoryPlot} uses this information when auto-calculating the range for the axis.
     *
     * @return the range type.
     */
    public RangeType getRangeType() {
        return RangeType.STANDARD;
    }

    /**
     * Draws a background for the data area.  The default implementation just gets the plot to
     * draw the outline, but some renderers will override this behaviour.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     */
    public void drawBackground(Graphics2D g2,
                               CategoryPlot plot,
                               Rectangle2D dataArea) {

        plot.drawBackground(g2, dataArea);

    }

    /**
     * Draws an outline for the data area.  The default implementation just gets the plot to
     * draw the outline, but some renderers will override this behaviour.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     */
    public void drawOutline(Graphics2D g2,
                            CategoryPlot plot,
                            Rectangle2D dataArea) {

        plot.drawOutline(g2, dataArea);

    }

    /**
     * Draws a grid line against the domain axis.
     * <P>
     * Note that this default implementation assumes that the horizontal axis is the domain axis.
     * If this is not the case, you will need to override this method.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the area for plotting data (not yet adjusted for any 3D effect).
     * @param value  the Java2D value at which the grid line should be drawn.
     */
    public void drawDomainGridline(Graphics2D g2,
                                   CategoryPlot plot,
                                   Rectangle2D dataArea,
                                   double value) {

        Line2D line = null;
        PlotOrientation orientation = plot.getOrientation();

        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(dataArea.getMinX(), value, dataArea.getMaxX(), value);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(value, dataArea.getMinY(), value, dataArea.getMaxY());
        }

        Paint paint = plot.getDomainGridlinePaint();
        if (paint == null) {
            paint = CategoryPlot.DEFAULT_GRIDLINE_PAINT;
        }
        g2.setPaint(paint);

        Stroke stroke = plot.getDomainGridlineStroke();
        if (stroke == null) {
            stroke = CategoryPlot.DEFAULT_GRIDLINE_STROKE;
        }
        g2.setStroke(stroke);

        g2.draw(line);

    }

    /**
     * Draws a grid line against the range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param dataArea  the area for plotting data (not yet adjusted for any 3D effect).
     * @param value  the value at which the grid line should be drawn.
     *
     */
    public void drawRangeGridline(Graphics2D g2,
                                  CategoryPlot plot,
                                  ValueAxis axis,
                                  Rectangle2D dataArea,
                                  double value) {

        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }

        PlotOrientation orientation = plot.getOrientation();
        double v = axis.translateValueToJava2D(value, dataArea, plot.getRangeAxisEdge());
        Line2D line = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
        }

        Paint paint = plot.getRangeGridlinePaint();
        if (paint == null) {
            paint = CategoryPlot.DEFAULT_GRIDLINE_PAINT;
        }
        g2.setPaint(paint);

        Stroke stroke = plot.getRangeGridlineStroke();
        if (stroke == null) {
            stroke = CategoryPlot.DEFAULT_GRIDLINE_STROKE;
        }
        g2.setStroke(stroke);

        g2.draw(line);

    }

    /**
     * Draws a marker for the range axis.
     * <P>
     * A marker is a constant value, usually represented by a line.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the range axis.
     * @param marker  the marker to be drawn.
     * @param dataArea  the area inside the axes.
     */
    public void drawRangeMarker(Graphics2D g2,
                                CategoryPlot plot,
                                ValueAxis axis,
                                Marker marker,
                                Rectangle2D dataArea) {

        double value = marker.getValue();
        Range range = axis.getRange();

        if (!range.contains(value)) {
            return;
        }

        PlotOrientation orientation = plot.getOrientation();
        double v = axis.translateValueToJava2D(value, dataArea, plot.getRangeAxisEdge());
        Line2D line = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
        }

        g2.setPaint(marker.getOutlinePaint());
        g2.setStroke(marker.getOutlineStroke());
        g2.draw(line);
        
        String label = marker.getLabel();
        MarkerLabelPosition position = marker.getLabelPosition();
        if (label != null) {
            Font labelFont = marker.getLabelFont();
            g2.setFont(labelFont);
            g2.setPaint(marker.getLabelPaint());
            double[] coordinates = calculateRangeMarkerTextPosition(g2, orientation, dataArea, 
                                                                    v, label, labelFont,
                                                               position);
            g2.drawString(label, (int) coordinates[0], (int) coordinates[1]);
        }

    }

    /**
     * Calculates the (x, y) coordinates for drawing a marker label.
     * 
     * @param g2  the graphics device.
     * @param orientation  the plot orientation.
     * @param dataArea  the data area.
     * @param coordinate  the range value (converted to Java 2D space).
     * @param label  the label.
     * @param font  the font.
     * @param position  the label position.
     * 
     * @return the coordinates for drawing the marker label.
     */
    private double[] calculateRangeMarkerTextPosition(Graphics2D g2, 
                                                      PlotOrientation orientation,
                                                      Rectangle2D dataArea,
                                                      double coordinate,
                                                      String label,
                                                      Font font,
                                                      MarkerLabelPosition position) {
                                                     
        double[] result = new double[2];
        FontRenderContext frc = g2.getFontRenderContext();
        FontMetrics fm = g2.getFontMetrics();
        LineMetrics metrics = font.getLineMetrics(label, frc);
        Rectangle2D bounds = fm.getStringBounds(label, g2);
        if (orientation == PlotOrientation.HORIZONTAL) {
            if (position == MarkerLabelPosition.TOP_LEFT) {
                result[0] = coordinate - bounds.getWidth() - 2.0;
                result[1] = dataArea.getMinY() + bounds.getHeight();
            }
            else if (position == MarkerLabelPosition.TOP_RIGHT) {
                result[0] = coordinate + 2.0;
                result[1] = dataArea.getMinY() + bounds.getHeight();
            }
            else if (position == MarkerLabelPosition.BOTTOM_LEFT) {
                result[0] = coordinate - bounds.getWidth() - 2.0;
                result[1] = dataArea.getMaxY() - metrics.getDescent() - metrics.getLeading();
            }
            else if (position == MarkerLabelPosition.BOTTOM_RIGHT) {
                result[0] = coordinate + 2.0;
                result[1] = dataArea.getMaxY() - metrics.getDescent() - metrics.getLeading();
            }           
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            if (position == MarkerLabelPosition.TOP_LEFT) {
                result[0] = dataArea.getMinX() + 2.0;
                result[1] = coordinate - metrics.getDescent() - metrics.getLeading();
            }
            else if (position == MarkerLabelPosition.TOP_RIGHT) {
                result[0] = dataArea.getMaxX() - bounds.getWidth() - 2.0;
                result[1] = coordinate - metrics.getDescent() - metrics.getLeading();
            }
            else if (position == MarkerLabelPosition.BOTTOM_LEFT) {
                result[0] = dataArea.getMinX() + 2.0;
                result[1] = coordinate + bounds.getHeight();
            }
            else if (position == MarkerLabelPosition.BOTTOM_RIGHT) {
                result[0] = dataArea.getMaxX() - bounds.getWidth() - 2.0;
                result[1] = coordinate + bounds.getHeight();
            }                       
        }
        return result;    
        
    }
    
    /**
     * Returns a legend item for a series.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return the legend item.
     */
    public LegendItem getLegendItem(int datasetIndex, int series) {

        CategoryPlot plot = getPlot();
        if (plot == null) {
            return null;
        }

        CategoryDataset dataset;
        if (datasetIndex == 0) {
            dataset = plot.getDataset();
        }
        else {
            dataset = plot.getSecondaryDataset(datasetIndex - 1);
        }
        String label = dataset.getRowKey(series).toString();
        String description = label;
        Shape shape = getSeriesShape(series);
        Paint paint = getSeriesPaint(series);
        Paint outlinePaint = getSeriesOutlinePaint(series);
        Stroke stroke = getSeriesStroke(series);

        return new LegendItem(label, description,
                              shape, paint, outlinePaint, stroke);

    }

    /**
     * Tests this renderer for equality with another object.
     *
     * @param obj  the object.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {

        boolean result = super.equals(obj);

        if (obj instanceof AbstractCategoryItemRenderer) {
            AbstractCategoryItemRenderer r = (AbstractCategoryItemRenderer) obj;

            boolean b0 = ObjectUtils.equal(this.itemLabelGenerator, r.itemLabelGenerator);
            boolean b1 = ObjectUtils.equal(this.itemLabelGeneratorList, r.itemLabelGeneratorList);
            boolean b2 = ObjectUtils.equal(this.baseItemLabelGenerator, r.baseItemLabelGenerator);
            boolean b3 = ObjectUtils.equal(this.itemURLGenerator, r.itemURLGenerator);
            boolean b4 = ObjectUtils.equal(this.itemURLGeneratorList, r.itemURLGeneratorList);
            boolean b5 = ObjectUtils.equal(this.baseItemURLGenerator, r.baseItemURLGenerator);
            boolean b6 = NumberUtils.equal(this.itemLabelAnchorOffset, r.itemLabelAnchorOffset);
            
            result = b0 && b1 && b2 && b3 && b4 && b5 && b6; 
        }

        return result;

    }
    
    /**
     * Returns a hash code for the renderer.
     * 
     * @return The hash code.
     */
    public int hashCode() {
        int result = super.hashCode();
        return result;
    }

    /**
     * Returns the drawing supplier from the plot.
     *
     * @return The drawing supplier (possibly <code>null</code>).
     */
    public DrawingSupplier getDrawingSupplier() {
        DrawingSupplier result = null;
        CategoryPlot cp = getPlot();
        if (cp != null) {
            result = cp.getDrawingSupplier();
        }
        return result;
    }

    /**
     * Draws an item label.
     *
     * @param g2  the graphics device.
     * @param orientation  the orientation.
     * @param dataset  the dataset.
     * @param row  the row.
     * @param column  the column.
     * @param x  the x coordinate.
     * @param y  the y coordinate.
     * @param negative  indicates a negative value (which affects the item label position).
     */
    protected void drawItemLabel(Graphics2D g2, 
                                 PlotOrientation orientation,
                                 CategoryDataset dataset, 
                                 int row, int column,
                                 double x, double y, 
                                 boolean negative) {
                                     
        CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
        if (generator != null) {
            Font labelFont = getItemLabelFont(row, column);
            g2.setFont(labelFont);
            Paint paint = getItemLabelPaint(row, column);
            g2.setPaint(paint);
            String label = generator.generateItemLabel(dataset, row, column);

            // get the label anchor..
            ItemLabelPosition position = null;
            if (!negative) {
                position = getPositiveItemLabelPosition(row, column);
            }
            else {
                position = getNegativeItemLabelPosition(row, column);
            }

            // work out the label anchor point...
            Point2D anchorPoint = calculateLabelAnchorPoint(position.getItemLabelAnchor(), 
                                                            x, 
                                                            y, 
                                                            orientation);
            RefineryUtilities.drawRotatedString(label, g2,
                                               (float) anchorPoint.getX(),
                                               (float) anchorPoint.getY(),
                                               position.getTextAnchor(), 
                                               position.getRotationAnchor(), 
                                               position.getAngle());
        }

    }

    /**
     * Calculates the item label anchor point.
     *
     * @param anchor  the anchor.
     * @param x  the x coordinate.
     * @param y  the y coordinate.
     * @param orientation  the plot orientation.
     *
     * @return The anchor point.
     */
    private Point2D calculateLabelAnchorPoint(ItemLabelAnchor anchor,
                                              double x, double y, PlotOrientation orientation) {

        Point2D result = null;

        if (anchor == ItemLabelAnchor.CENTER) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE1) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE2) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE3) {
            result = new Point2D.Double(x + this.itemLabelAnchorOffset, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE4) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE5) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE6) {
            result = new Point2D.Double(x, y + this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE7) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE8) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE9) {
            result = new Point2D.Double(x - this.itemLabelAnchorOffset, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE10) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE11) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE12) {
            result = new Point2D.Double(x, y - this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE1) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE2) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE3) {
            result = new Point2D.Double(x + 2.0 * this.itemLabelAnchorOffset, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE4) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE5) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE6) {
            result = new Point2D.Double(x, y + 2.0 * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE7) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE8) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE9) {
            result = new Point2D.Double(x - 2.0 * this.itemLabelAnchorOffset, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE10) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE11) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE12) {
            result = new Point2D.Double(x, y - 2.0 * this.itemLabelAnchorOffset);
       }

        return result;

    }
    
    /**
     * Returns an independent copy of the renderer.
     * <p>
     * The <code>plot</code> reference is shallow copied.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException  should not happen.
     */
    public Object clone() throws CloneNotSupportedException {
        AbstractCategoryItemRenderer clone = (AbstractCategoryItemRenderer) super.clone();

        if (this.itemLabelGenerator != null) {
            clone.itemLabelGenerator = (CategoryItemLabelGenerator) this.itemLabelGenerator.clone();
        }

        if (this.itemLabelGeneratorList != null) {
            clone.itemLabelGeneratorList = (ObjectList) this.itemLabelGeneratorList.clone();
        }
        
        if (this.baseItemLabelGenerator != null) {
            clone.baseItemLabelGenerator 
                = (CategoryItemLabelGenerator) this.baseItemLabelGenerator.clone();
        }
        
        if (this.itemURLGenerator != null) {
            clone.itemURLGenerator = (CategoryURLGenerator) this.itemURLGenerator.clone();
        }

        if (this.itemURLGeneratorList != null) {
            clone.itemURLGeneratorList = (ObjectList) this.itemURLGeneratorList.clone();
        }

        if (this.baseItemURLGenerator != null) {
            clone.baseItemURLGenerator 
                = (CategoryURLGenerator) this.baseItemURLGenerator.clone();
        }
        

        return clone;
    }

    /**
     * Returns a domain axis for a plot.
     * 
     * @param plot  the plot.
     * @param index  the axis index (<code>null</code> for the primary axis).
     * 
     * @return A domain axis.
     */
    protected CategoryAxis getDomainAxis(CategoryPlot plot, Integer index) {
        CategoryAxis result = null;
        if (index == null) {
            result = plot.getDomainAxis();
        }
        else {
            result = plot.getSecondaryDomainAxis(index.intValue());
            if (result == null) {
                result = plot.getDomainAxis();
            }
        }
        return result;
    }

    /**
     * Returns a range axis for a plot.
     * 
     * @param plot  the plot.
     * @param index  the axis index (<code>null</code> for the primary axis).
     * 
     * @return A range axis.
     */
    protected ValueAxis getRangeAxis(CategoryPlot plot, Integer index) {
        ValueAxis result = null;
        if (index == null) {
            result = plot.getRangeAxis();
        }
        else {
            result = plot.getSecondaryRangeAxis(index.intValue());
            if (result == null) {
                result = plot.getRangeAxis();
            }
        }
        return result;
    }

    /**
     * Returns a dataset for a plot.
     * 
     * @param plot  the plot.
     * @param index  the dataset index (<code>null</code> for the primary dataset).
     * 
     * @return A dataset.
     */
    protected CategoryDataset getDataset(CategoryPlot plot, Integer index) {
        CategoryDataset result = null;
        if (index == null) {
            result = plot.getDataset();
        }
        else {
            result = plot.getSecondaryDataset(index.intValue());
        }
        return result;    
    }
    
}
