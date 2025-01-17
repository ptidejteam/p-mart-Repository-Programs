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
 * $Id: AbstractCategoryItemRenderer.java,v 1.1 2007/10/10 20:07:35 vauchers Exp $
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
 *
 */

package org.jfree.chart.renderer;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.LegendItem;
import org.jfree.chart.Marker;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.data.CategoryDataset;
import org.jfree.data.Range;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

/**
 * An abstract base class that you can use to implement a new {@link CategoryItemRenderer}.
 * <p>
 * When you create a new {@link CategoryItemRenderer} you are not required to extend this class,
 * but it makes the job easier.
 *
 * @author David Gilbert
 */
public abstract class AbstractCategoryItemRenderer extends AbstractRenderer
                                                   implements CategoryItemRenderer, Serializable {

    private CategoryPlot plot;
    
    /** The label generator table. */
    private CategoryItemLabelGeneratorTable labelGeneratorTable;

    /** The default label generator. */
    private CategoryItemLabelGenerator defaultLabelGenerator;

    /** The override label generator. */
    private CategoryItemLabelGenerator overrideLabelGenerator;

    /** The URL generator. */
    private CategoryURLGenerator urlGenerator;

//    /** A table of booleans that controls the visibility of item labels for each series. */
//    private BooleanTable itemLabelsVisible;
//
//    /** The default item label visibility. */
//    private Boolean defaultItemLabelsVisible;
//
//    /** A flag indicating whether or not value labels are shown. */
//    private Boolean overrideItemLabelsVisible;

    /** The number of rows in the dataset (temporary record). */
    private transient int rowCount;

    /** The number of columns in the dataset (temporary record). */
    private transient int columnCount;

    private double itemLabelAnchorOffset = 2.0;

    /**
     * Creates a new renderer with no tool tip generator and no URL generator.
     * <P>
     * The defaults (no tool tip or URL generators) have been chosen to minimise the processing
     * required to generate a default chart.  If you require tool tips or URLs, then you can
     * easily add the required generators.
     */
    protected AbstractCategoryItemRenderer() {
        this(null, null);
    }

    /**
     * Creates a new renderer with the specified label generator but no URL generator.
     *
     * @param labelGenerator  the label generator.
     */
    protected AbstractCategoryItemRenderer(CategoryItemLabelGenerator labelGenerator) {
        this(labelGenerator, null);
    }

    /**
     * Creates a new renderer with the specified URL generator but no tooltip generator.
     *
     * @param urlGenerator  the URL generator.
     */
    protected AbstractCategoryItemRenderer(CategoryURLGenerator urlGenerator) {
        this(null, urlGenerator);
    }

    /**
     * Creates a new renderer with the specified tooltip generator and URL generator.
     *
     * @param labelGenerator  the tool tip generator.
     * @param urlGenerator  the URL generator.
     */
    protected AbstractCategoryItemRenderer(CategoryItemLabelGenerator labelGenerator,
                                           CategoryURLGenerator urlGenerator) {

        this.overrideLabelGenerator = labelGenerator;
        this.urlGenerator = urlGenerator;

//        this.overrideItemLabelsVisible = Boolean.FALSE;
        this.labelGeneratorTable = new CategoryItemLabelGeneratorTable();

    }

    public CategoryPlot getPlot() {
        return this.plot;
    }
    
    public void setPlot(CategoryPlot plot) {
        this.plot = plot;    
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
     * Returns the URL generator for HTML image maps.
     *
     * @return the URL generator.
     */
    public CategoryURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    /**
     * Sets the URL generator for HTML image maps.
     *
     * @param urlGenerator  the URL generator.
     */
    public void setURLGenerator(CategoryURLGenerator urlGenerator) {

        Object oldValue = this.urlGenerator;
        this.urlGenerator = urlGenerator;
        firePropertyChanged("renderer.URLGenerator", oldValue, urlGenerator);

    }

    /**
     * Initialises the renderer.
     * <P>
     * Stores a reference to the {@link ChartRenderingInfo} object (which might be
     * <code>null</code>), and then sets the useCategoriesPaint flag according to the special case
     * conditions a) there is only one series and b) the categoriesPaint array is not null.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param info  an object for returning information about the structure of the chart
     *              (<code>null</code> permitted).
     *
     */
    public void initialise(Graphics2D g2, Rectangle2D dataArea,
                           CategoryPlot plot, ChartRenderingInfo info) {

        setPlot(plot);
        CategoryDataset data = plot.getCategoryDataset();
        if (data != null) {
            this.rowCount = data.getRowCount();
            this.columnCount = data.getColumnCount();
        }
        else {
            this.rowCount = 0;
            this.columnCount = 0;
        }
        setInfo(info);

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
        double v = axis.translateValueToJava2D(value, dataArea, plot.getRangeAxisLocation());
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
        double v = axis.translateValueToJava2D(value, dataArea, plot.getRangeAxisLocation());
        Line2D line = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
        }
        
        Paint paint = marker.getOutlinePaint();
        if (paint == null) {
            paint = Plot.DEFAULT_OUTLINE_PAINT;
        }
        g2.setPaint(paint);

        Stroke stroke = marker.getOutlineStroke();
        if (stroke == null) {
            stroke = Plot.DEFAULT_OUTLINE_STROKE;
        }
        g2.setStroke(stroke);
        
        g2.draw(line);

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

        CategoryPlot plot = (CategoryPlot) getPlot();
        if (plot == null) {
            return null;
        }

        CategoryDataset dataset;
        if (datasetIndex == 0) {
            dataset = plot.getCategoryDataset();
        }
        else {
            dataset = plot.getSecondaryCategoryDataset();
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
            if (this.defaultLabelGenerator != null) {
                result = result && this.defaultLabelGenerator.equals(r.getDefaultLabelGenerator());
            }
            else {
                result = result && (r.getDefaultLabelGenerator() == null);
            }
            if (this.urlGenerator != null) {
                result = result && this.urlGenerator.equals(r.getURLGenerator());
            }
            else {
                result = result && (r.getURLGenerator() == null);
            }
        }

        return result;

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

        // return the override, if there is one...
        if (this.overrideLabelGenerator != null) {
            return this.overrideLabelGenerator;
        }

        // otherwise look up the generator table
        CategoryItemLabelGenerator generator 
            = this.labelGeneratorTable.getLabelGenerator(0, series);
        if (generator == null) {
            generator = this.defaultLabelGenerator;
        }
        return generator;

    }

    /**
     * Sets the label generator for a series.
     *
     * @param series  the series index (zero based).
     * @param generator  the generator.
     */
    public void setSeriesItemLabelGenerator(int series, CategoryItemLabelGenerator generator) {
        this.labelGeneratorTable.setLabelGenerator(0, series, generator);
    }

    /**
     * Returns the default label generator.
     *
     * @return The default label generator.
     */
    public CategoryItemLabelGenerator getDefaultLabelGenerator() {
        return this.defaultLabelGenerator;
    }

    /**
     * Sets the default label generator.
     *
     * @param generator  the default label generator.
     */
    public void setDefaultLabelGenerator(CategoryItemLabelGenerator generator) {
        this.defaultLabelGenerator = generator;
    }

    /**
     * Sets the override label generator.
     *
     * @param generator  the generator.
     */
    public void setOverrideLabelGenerator(CategoryItemLabelGenerator generator) {
        this.overrideLabelGenerator = generator;
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

    protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation,
                                 CategoryDataset dataset, int row, int column,
                                 double x, double y, boolean flipAnchors) {
        CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
        if (generator != null) {
            Font labelFont = getItemLabelFont(row, column);
            g2.setFont(labelFont);
            Paint paint = getItemLabelPaint(row, column);
            g2.setPaint(paint);
            String label = generator.generateItemLabel(dataset, row, column);

            // get the label anchor..
            ItemLabelAnchor labelAnchor = getItemLabelAnchor(row, column);
            TextAnchor textAnchor = getItemLabelTextAnchor(row, column);
            TextAnchor rotationAnchor = getItemLabelRotationAnchor(row, column);

            if (flipAnchors) {
                labelAnchor = ItemLabelAnchor.getVerticalOpposite(labelAnchor);
                textAnchor = TextAnchor.getVerticalOpposite(textAnchor);
                rotationAnchor = TextAnchor.getVerticalOpposite(rotationAnchor);
            }
            double angle = getItemLabelAngle(row, column).doubleValue();

            // work out the label anchor point...
            Point2D anchorPoint = calculateLabelAnchorPoint(labelAnchor, x, y, orientation);
            RefineryUtilities.drawRotatedString(label, g2,
                                               (float) anchorPoint.getX(),
                                               (float) anchorPoint.getY(),
                                               textAnchor, rotationAnchor, angle);
        }

    }

    /**
     * Calculates the item label anchor point.
     *
     * @param anchor  the anchor.
     * @param point  the data point.
     * @param orientation  the plot orientation.
     *
     * @return The anchor point.
     */
    private Point2D calculateLabelAnchorPoint(ItemLabelAnchor anchor,
                                              double x, double y, PlotOrientation orientation) {

        Point2D result = null;

        if (orientation == PlotOrientation.HORIZONTAL) {
            result = getHorizontalLabelAnchorPoint(anchor, y, x);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            result = getVerticalLabelAnchorPoint(anchor, x, y);
        }

        return result;

    }

    private Point2D getHorizontalLabelAnchorPoint(ItemLabelAnchor anchor, double x, double y) {

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
            result = new Point2D.Double(x, y + this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE4) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE5) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE6) {
            result = new Point2D.Double(x - this.itemLabelAnchorOffset, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE7) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE8) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE9) {
            result = new Point2D.Double(x, y - this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE10) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE11) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE12) {
            result = new Point2D.Double(x + this.itemLabelAnchorOffset, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE1) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE2) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE3) {
            result = new Point2D.Double(x, y + 2.0 * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE4) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE5) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE6) {
            result = new Point2D.Double(x- 2.0 * this.itemLabelAnchorOffset, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE7) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE8) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE9) {
            result = new Point2D.Double(x, y - 2.0 * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE10) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE11) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE12) {
            result = new Point2D.Double(x + 2.0 * this.itemLabelAnchorOffset, y);
        }

        return result;

    }

    private Point2D getVerticalLabelAnchorPoint(ItemLabelAnchor anchor, double x, double y) {

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

}
