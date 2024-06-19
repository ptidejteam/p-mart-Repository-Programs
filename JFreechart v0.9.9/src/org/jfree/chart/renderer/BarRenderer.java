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
 * ----------------
 * BarRenderer.java
 * ----------------
 * (C) Copyright 2002, 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: BarRenderer.java,v 1.1 2007/10/10 20:07:35 vauchers Exp $
 *
 * Changes
 * -------
 * 14-Mar-2002 : Version 1 (DG);
 * 23-May-2002 : Added tooltip generator to renderer (DG);
 * 29-May-2002 : Moved tooltip generator to abstract super-class (DG);
 * 25-Jun-2002 : Changed constructor to protected and removed redundant code (DG);
 * 26-Jun-2002 : Added axis to initialise method, and record upper and lower clip values (DG);
 * 24-Sep-2002 : Added getLegendItem(...) method (DG);
 * 09-Oct-2002 : Modified constructor to include URL generator (DG);
 * 05-Nov-2002 : Base dataset is now TableDataset not CategoryDataset (DG);
 * 10-Jan-2003 : Moved get/setItemMargin() method up from subclasses (DG);
 * 17-Jan-2003 : Moved plot classes into a separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Modified clipping to allow for dual axes and datasets (DG);
 * 12-May-2003 : Merged horizontal and vertical bar renderers (DG);
 * 12-Jun-2003 : Updates for item labels (DG);
 *
 */

package org.jfree.chart.renderer;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.data.CategoryDataset;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

/**
 * A {@link CategoryItemRenderer} that represents data using bars.
 *
 * @author David Gilbert
 */
public class BarRenderer extends AbstractCategoryItemRenderer implements Serializable {

    /** The default item margin percentage. */
    public static final double DEFAULT_ITEM_MARGIN = 0.20;

    /** Constant that controls the minimum width before a bar has an outline drawn. */
    public static final double BAR_OUTLINE_WIDTH_THRESHOLD = 3.0;

    /** The margin between items (bars) within a category. */
    private double itemMargin;

    /** The bar width. */
    private double barWidth;

    /** The upper clip (axis) value for the axis. */
    private double upperClip;

    /** The lower clip (axis) value for the axis. */
    private double lowerClip;

    /** The item label anchor offset (used to calculate the anchor point). */
    private double itemLabelAnchorOffset;

    /**
     * Default constructor.
     */
    public BarRenderer() {
        this(null, null);
    }

    /**
     * Constructs a bar renderer.
     *
     * @param labelGenerator  the label (including tooltips) generator (<code>null</code>
     *                        permitted).
     * @param urlGenerator  the URL generator (<code>null</code> permitted).
     */
    public BarRenderer(CategoryItemLabelGenerator labelGenerator,
                       CategoryURLGenerator urlGenerator) {

        super(labelGenerator, urlGenerator);
        this.itemMargin = DEFAULT_ITEM_MARGIN;
        this.itemLabelAnchorOffset = 2.0;

    }

    /**
     * Returns the item margin.
     *
     * @return the margin.
     */
    public double getItemMargin() {
        return this.itemMargin;
    }

    /**
     * Sets the item margin.  The value is expressed as a percentage of the available width for
     * plotting all the bars, with the resulting amount to be distributed between all the bars
     * evenly.
     *
     * @param percent  the new margin.
     */
    public void setItemMargin(double percent) {
        this.itemMargin = percent;
        this.firePropertyChanged("ItemMargin", null, null);
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
        this.firePropertyChanged("ItemLabelAnchorOffset", null, null);
    }

    /**
     * Returns the bar width.
     *
     * @return the bar width.
     */
    public double getBarWidth() {
        return this.barWidth;
    }

    /**
     * Updates the calculated bar width.
     *
     * @param width  the new width.
     */
    protected void setBarWidth(double width) {
        this.barWidth = width;
    }

    /**
     * Returns the lower clip value.
     * <P>
     * This value is recalculated in the initialise() method.
     *
     * @return the value.
     */
    public double getLowerClip() {
        return this.lowerClip;
    }

    /**
     * Returns the upper clip value.
     * <P>
     * This value is recalculated in the initialise() method.
     *
     * @return the value.
     */
    public double getUpperClip() {
        return this.upperClip;
    }

    /**
     * Initialises the renderer.
     * <p>
     * This method gets called once at the start of the process of drawing a chart.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the data is to be plotted.
     * @param plot  the plot.
     * @param info  collects chart rendering information for return to caller.
     *
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           CategoryPlot plot,
                           ChartRenderingInfo info) {

        super.initialise(g2, dataArea, plot, info);

        ValueAxis rangeAxis = plot.getRangeAxis();
        this.lowerClip = rangeAxis.getRange().getLowerBound();
        this.upperClip = rangeAxis.getRange().getUpperBound();

        // calculate the bar width
        CategoryAxis domainAxis = plot.getDomainAxis();
        CategoryDataset dataset = plot.getCategoryDataset();
        if (dataset != null) {
            int columns = dataset.getColumnCount();
            int rows = dataset.getRowCount();
            double space = 0.0;
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                space = dataArea.getHeight();
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                space = dataArea.getWidth();
            }
            double categoryMargin = 0.0;
            double currentItemMargin = 0.0;
            if (columns > 1) {
                categoryMargin = domainAxis.getCategoryMargin();
            }
            if (rows > 1) {
                currentItemMargin = getItemMargin();
            }
            double used = space * (1 - domainAxis.getLowerMargin() - domainAxis.getUpperMargin()
                                     - categoryMargin - currentItemMargin);
            if ((rows * columns) > 0) {
                setBarWidth(used / (dataset.getColumnCount() * dataset.getRowCount()));
            }
            else {
                setBarWidth(used);
            }
        }

    }

    /**
     * Draws the bar for a single (series, category) data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param data  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawItem(Graphics2D g2,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset data,
                         int row,
                         int column) {

        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            drawHorizontalItem(g2, dataArea, plot, domainAxis, rangeAxis, data, row, column);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            drawVerticalItem(g2, dataArea, plot, domainAxis, rangeAxis, data, row, column);
        }

    }

    /**
     * Draws the bar for a single (series, category) data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param data  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    protected void drawHorizontalItem(Graphics2D g2,
                                      Rectangle2D dataArea,
                                      CategoryPlot plot,
                                      CategoryAxis domainAxis,
                                      ValueAxis rangeAxis,
                                      CategoryDataset data,
                                      int row,
                                      int column) {

        // nothing is drawn for null values...
        Number dataValue = data.getValue(row, column);
        if (dataValue == null) {
            return;
        }

        // X
        double value = dataValue.doubleValue();
        double base = 0.0;
        double lclip = getLowerClip();
        double uclip = getUpperClip();
        if (uclip <= 0.0) {  // cases 1, 2, 3 and 4
            if (value >= uclip) {
                return; // bar is not visible
            }
            base = uclip;
            if (value <= lclip) {
                value = lclip;
            }
        }
        else if (lclip <= 0.0) { // cases 5, 6, 7 and 8
            if (value >= uclip) {
                value = uclip;
            }
            else {
                if (value <= lclip) {
                    value = lclip;
                }
            }
        }
        else { // cases 9, 10, 11 and 12
            if (value <= lclip) {
                return; // bar is not visible
            }
            base = lclip;
            if (value >= uclip) {
                value = uclip;
            }
        }

        AxisLocation location = plot.getRangeAxisLocation();
        double transX1 = rangeAxis.translateValueToJava2D(base, dataArea, location);
        double transX2 = rangeAxis.translateValueToJava2D(value, dataArea, location);
        double rectX = Math.min(transX1, transX2);
        double rectWidth = Math.abs(transX2 - transX1);

        // Y
        double rectY = domainAxis.getCategoryStart(column, getColumnCount(), dataArea,
                                                   plot.getDomainAxisLocation());

        int seriesCount = getRowCount();
        int categoryCount = getColumnCount();
        if (seriesCount > 1) {
            double seriesGap = dataArea.getHeight() * getItemMargin()
                               / (categoryCount * (seriesCount - 1));
            rectY = rectY + row * (getBarWidth() + seriesGap);
        }
        else {
            rectY = rectY + row * getBarWidth();
        }

        // draw the bar...
        double rectHeight = getBarWidth();
        Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
        g2.setPaint(getItemPaint(row, column));
        g2.fill(bar);

        // draw the outline...
        if (getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
            Stroke stroke = getItemOutlineStroke(row, column);
            Paint paint = getItemOutlinePaint(row, column);
            if (stroke != null && paint != null) {
                g2.setStroke(stroke);
                g2.setPaint(paint);
                g2.draw(bar);
            }
        }

        CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
        if (generator != null && isItemLabelVisible(row, column)) {
            drawItemLabel(g2, data, row, column, plot, generator, bar, (transX1 > transX2));
        }        
                
        // collect entity and tool tip information...
        if (getInfo() != null) {
            EntityCollection entities = getInfo().getEntityCollection();
            if (entities != null) {
                String tip = null;
                if (generator != null) {
                    tip = generator.generateToolTip(data, row, column);
                }
                String url = null;
                if (getURLGenerator() != null) {
                    url = getURLGenerator().generateURL(data, row, column);
                }
                CategoryItemEntity entity = new CategoryItemEntity(bar, tip, url, row,
                                              data.getColumnKey(column), column);
                entities.addEntity(entity);
            }

        }

    }

    /**
     * Draws an item label.
     * 
     * @param g2  the graphics device.
     * @param data  the dataset.
     * @param row  the row.
     * @param column  the column.
     * @param plot  the plot.
     * @param generator  the label generator.
     * @param bar  the bar.
     * @param negative  a flag indicating a negative value.
     */
    protected void drawItemLabel(Graphics2D g2,
                                 CategoryDataset data,
                                 int row,
                                 int column,
                                 CategoryPlot plot,
                                 CategoryItemLabelGenerator generator,
                                 Rectangle2D bar,
                                 boolean negative) {
                                     
        // draw the item labels if there are any...
        Font labelFont = getItemLabelFont(row, column);
        g2.setFont(labelFont);
        Paint paint = getItemLabelPaint(row, column);
        g2.setPaint(paint);
        String label = generator.generateItemLabel(data, row, column);

        // get the label anchor..
        ItemLabelAnchor labelAnchor = getItemLabelAnchor(row, column);
        TextAnchor textAnchor = getItemLabelTextAnchor(row, column);
        TextAnchor rotationAnchor = getItemLabelRotationAnchor(row, column);
        double angle = getItemLabelAngle(row, column).doubleValue();

        if (negative) {
            labelAnchor = ItemLabelAnchor.getHorizontalOpposite(labelAnchor);
            textAnchor = TextAnchor.getHorizontalOpposite(textAnchor);
            rotationAnchor = TextAnchor.getHorizontalOpposite(rotationAnchor);
        }

        // work out the label anchor point...
        Point2D anchorPoint = calculateLabelAnchorPoint(labelAnchor, bar, plot.getOrientation());
        RefineryUtilities.drawRotatedString(label, g2,
                                           (float) anchorPoint.getX(),
                                           (float) anchorPoint.getY(),
                                           textAnchor, rotationAnchor, angle);
                
    }
    
    /**
     * Draws the bar for a single (series, category) data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param data  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    protected void drawVerticalItem(Graphics2D g2,
                                    Rectangle2D dataArea,
                                    CategoryPlot plot,
                                    CategoryAxis domainAxis,
                                    ValueAxis rangeAxis,
                                    CategoryDataset data,
                                    int row,
                                    int column) {

        // nothing is drawn for null values...
        Number dataValue = data.getValue(row, column);
        if (dataValue == null) {
            return;
        }

        // BAR X
        double rectX = domainAxis.getCategoryStart(column, getColumnCount(), dataArea,
                                                   plot.getDomainAxisLocation());

        int seriesCount = getRowCount();
        int categoryCount = getColumnCount();
        if (seriesCount > 1) {
            double seriesGap = dataArea.getWidth() * getItemMargin()
                               / (categoryCount * (seriesCount - 1));
            rectX = rectX + row * (getBarWidth() + seriesGap);
        }
        else {
            rectX = rectX + row * getBarWidth();
        }

        // BAR Y
        double value = dataValue.doubleValue();
        double base = 0.0;
        double lclip = getLowerClip();
        double uclip = getUpperClip();

        if (uclip <= 0.0) {  // cases 1, 2, 3 and 4
            if (value >= uclip) {
                return; // bar is not visible
            }
            base = uclip;
            if (value <= lclip) {
                value = lclip;
            }
        }
        else if (lclip <= 0.0) { // cases 5, 6, 7 and 8
            if (value >= uclip) {
                value = uclip;
            }
            else {
                if (value <= lclip) {
                    value = lclip;
                }
            }
        }
        else { // cases 9, 10, 11 and 12
            if (value <= lclip) {
                return; // bar is not visible
            }
            base = getLowerClip();
            if (value >= uclip) {
               value = uclip;
            }
        }

        AxisLocation location = plot.getRangeAxisLocation();
        double transY1 = rangeAxis.translateValueToJava2D(base, dataArea, location);
        double transY2 = rangeAxis.translateValueToJava2D(value, dataArea, location);
        double rectY = Math.min(transY2, transY1);

        double rectWidth = getBarWidth();
        double rectHeight = Math.abs(transY2 - transY1);

        Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);

        g2.setPaint(getItemPaint(row, column));
        g2.fill(bar);

        // draw the outline...
        if (getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
            Stroke stroke = getItemOutlineStroke(row, column);
            Paint paint = getItemOutlinePaint(row, column);
            if (stroke != null && paint != null) {
                g2.setStroke(stroke);
                g2.setPaint(paint);
                g2.draw(bar);
            }
        }

        // draw the item labels if there are any...
        CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
        if (generator != null && isItemLabelVisible(row, column)) {
            Font labelFont = getItemLabelFont(row, column);
            g2.setFont(labelFont);
            Paint paint = getItemLabelPaint(row, column);
            g2.setPaint(paint);
            String label = generator.generateItemLabel(data, row, column);

            // get the label anchor..
            ItemLabelAnchor labelAnchor = getItemLabelAnchor(row, column);
            TextAnchor textAnchor = getItemLabelTextAnchor(row, column);
            TextAnchor rotationAnchor = getItemLabelRotationAnchor(row, column);

            if (transY2 > transY1) {
                labelAnchor = ItemLabelAnchor.getVerticalOpposite(labelAnchor);
                textAnchor = TextAnchor.getVerticalOpposite(textAnchor);
                rotationAnchor = TextAnchor.getVerticalOpposite(rotationAnchor);
            }
            double angle = getItemLabelAngle(row, column).doubleValue();

            // work out the label anchor point...
            Point2D anchorPoint = calculateLabelAnchorPoint(labelAnchor, bar,
                                                            PlotOrientation.VERTICAL);
            RefineryUtilities.drawRotatedString(label, g2,
                                               (float) anchorPoint.getX(),
                                               (float) anchorPoint.getY(),
                                               textAnchor, rotationAnchor, angle);
        }

        // collect entity and tool tip information...
        if (getInfo() != null) {
            EntityCollection entities = getInfo().getEntityCollection();
            if (entities != null) {
                String tip = null;
                if (generator != null) {
                    tip = generator.generateToolTip(data, row, column);
                }
                String url = null;
                if (getURLGenerator() != null) {
                    url = getURLGenerator().generateURL(data, row, column);
                }
                CategoryItemEntity entity = new CategoryItemEntity(bar, tip, url, row,
                                                    data.getColumnKey(column), column);
                entities.addEntity(entity);
            }
        }

    }

    /**
     * Calculates the item label anchor point.
     *
     * @param anchor  the anchor.
     * @param bar  the bar.
     * @param orientation  the plot orientation.
     *
     * @return The anchor point.
     */
    private Point2D calculateLabelAnchorPoint(ItemLabelAnchor anchor,
                                              Rectangle2D bar, PlotOrientation orientation) {

        Point2D result = null;

        if (orientation == PlotOrientation.HORIZONTAL) {
            result = getHorizontalLabelAnchorPoint(anchor, bar);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            result = getVerticalLabelAnchorPoint(anchor, bar);
        }

        return result;

    }

    /**
     * Returns the label anchor point for a horizontal bar.
     * 
     * @param anchor  the label anchor.
     * @param bar  the bar area.
     * 
     * @return The anchor point.
     */
    private Point2D getHorizontalLabelAnchorPoint(ItemLabelAnchor anchor, Rectangle2D bar) {

        Point2D result = null;

        double x0 = bar.getX() - this.itemLabelAnchorOffset;
        double x1 = bar.getX();
        double x2 = bar.getX() + this.itemLabelAnchorOffset;
        double x3 = bar.getCenterX();
        double x4 = bar.getMaxX() - this.itemLabelAnchorOffset;
        double x5 = bar.getMaxX();
        double x6 = bar.getMaxX() + this.itemLabelAnchorOffset;

        double y0 = bar.getMaxY() + this.itemLabelAnchorOffset;
        double y1 = bar.getMaxY();
        double y2 = bar.getMaxY() - this.itemLabelAnchorOffset;
        double y3 = bar.getCenterY();
        double y4 = bar.getMinY() + this.itemLabelAnchorOffset;
        double y5 = bar.getMinY();
        double y6 = bar.getMinY() - this.itemLabelAnchorOffset;

        if (anchor == ItemLabelAnchor.CENTER) {
            result = new Point2D.Double(x3, y3);
        }
        else if (anchor == ItemLabelAnchor.INSIDE1) {
            result = new Point2D.Double(x4, y2);
        }
        else if (anchor == ItemLabelAnchor.INSIDE2) {
            result = new Point2D.Double(x4, y2);
        }
        else if (anchor == ItemLabelAnchor.INSIDE3) {
            result = new Point2D.Double(x3, y2);
        }
        else if (anchor == ItemLabelAnchor.INSIDE4) {
            result = new Point2D.Double(x2, y2);
        }
        else if (anchor == ItemLabelAnchor.INSIDE5) {
            result = new Point2D.Double(x2, y2);
        }
        else if (anchor == ItemLabelAnchor.INSIDE6) {
            result = new Point2D.Double(x2, y3);
        }
        else if (anchor == ItemLabelAnchor.INSIDE7) {
            result = new Point2D.Double(x2, y4);
        }
        else if (anchor == ItemLabelAnchor.INSIDE8) {
            result = new Point2D.Double(x2, y4);
        }
        else if (anchor == ItemLabelAnchor.INSIDE9) {
            result = new Point2D.Double(x3, y4);
        }
        else if (anchor == ItemLabelAnchor.INSIDE10) {
            result = new Point2D.Double(x4, y4);
        }
        else if (anchor == ItemLabelAnchor.INSIDE11) {
            result = new Point2D.Double(x4, y4);
        }
        else if (anchor == ItemLabelAnchor.INSIDE12) {
            result = new Point2D.Double(x4, y3);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE1) {
            result = new Point2D.Double(x6, y1);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE2) {
            result = new Point2D.Double(x5, y0);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE3) {
            result = new Point2D.Double(x3, y0);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE4) {
            result = new Point2D.Double(x1, y0);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE5) {
            result = new Point2D.Double(x0, y1);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE6) {
            result = new Point2D.Double(x0, y3);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE7) {
            result = new Point2D.Double(x0, y5);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE8) {
            result = new Point2D.Double(x1, y6);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE9) {
            result = new Point2D.Double(x3, y6);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE10) {
            result = new Point2D.Double(x5, y6);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE11) {
            result = new Point2D.Double(x6, y5);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE12) {
            result = new Point2D.Double(x6, y3);
        }

        return result;

    }

    /**
     * Returns the label anchor point for a vertical bar.
     * 
     * @param anchor  the label anchor.
     * @param bar  the bar area.
     * 
     * @return The anchor point.
     */
    private Point2D getVerticalLabelAnchorPoint(ItemLabelAnchor anchor, Rectangle2D bar) {

        Point2D result = null;

        double x0 = bar.getX() - this.itemLabelAnchorOffset;
        double x1 = bar.getX();
        double x2 = bar.getX() + this.itemLabelAnchorOffset;
        double x3 = bar.getCenterX();
        double x4 = bar.getMaxX() - this.itemLabelAnchorOffset;
        double x5 = bar.getMaxX();
        double x6 = bar.getMaxX() + this.itemLabelAnchorOffset;

        double y0 = bar.getMaxY() + this.itemLabelAnchorOffset;
        double y1 = bar.getMaxY();
        double y2 = bar.getMaxY() - this.itemLabelAnchorOffset;
        double y3 = bar.getCenterY();
        double y4 = bar.getMinY() + this.itemLabelAnchorOffset;
        double y5 = bar.getMinY();
        double y6 = bar.getMinY() - this.itemLabelAnchorOffset;

        if (anchor == ItemLabelAnchor.CENTER) {
            result = new Point2D.Double(x3, y3);
        }
        else if (anchor == ItemLabelAnchor.INSIDE1) {
            result = new Point2D.Double(x4, y4);
        }
        else if (anchor == ItemLabelAnchor.INSIDE2) {
            result = new Point2D.Double(x4, y4);
        }
        else if (anchor == ItemLabelAnchor.INSIDE3) {
            result = new Point2D.Double(x4, y3);
        }
        else if (anchor == ItemLabelAnchor.INSIDE4) {
            result = new Point2D.Double(x4, y2);
        }
        else if (anchor == ItemLabelAnchor.INSIDE5) {
            result = new Point2D.Double(x4, y2);
        }
        else if (anchor == ItemLabelAnchor.INSIDE6) {
            result = new Point2D.Double(x3, y2);
        }
        else if (anchor == ItemLabelAnchor.INSIDE7) {
            result = new Point2D.Double(x2, y2);
        }
        else if (anchor == ItemLabelAnchor.INSIDE8) {
            result = new Point2D.Double(x2, y2);
        }
        else if (anchor == ItemLabelAnchor.INSIDE9) {
            result = new Point2D.Double(x2, y3);
        }
        else if (anchor == ItemLabelAnchor.INSIDE10) {
            result = new Point2D.Double(x2, y4);
        }
        else if (anchor == ItemLabelAnchor.INSIDE11) {
            result = new Point2D.Double(x2, y4);
        }
        else if (anchor == ItemLabelAnchor.INSIDE12) {
            result = new Point2D.Double(x3, y4);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE1) {
            result = new Point2D.Double(x5, y6);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE2) {
            result = new Point2D.Double(x6, y5);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE3) {
            result = new Point2D.Double(x6, y3);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE4) {
            result = new Point2D.Double(x6, y1);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE5) {
            result = new Point2D.Double(x5, y0);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE6) {
            result = new Point2D.Double(x3, y0);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE7) {
            result = new Point2D.Double(x1, y0);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE8) {
            result = new Point2D.Double(x0, y1);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE9) {
            result = new Point2D.Double(x0, y3);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE10) {
            result = new Point2D.Double(x0, y5);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE11) {
            result = new Point2D.Double(x1, y6);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE12) {
            result = new Point2D.Double(x3, y6);
        }

        return result;

    }

}
