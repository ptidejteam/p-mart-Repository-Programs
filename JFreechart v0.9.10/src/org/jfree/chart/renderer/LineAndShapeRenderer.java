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
 * -------------------------
 * LineAndShapeRenderer.java
 * -------------------------
 * (C) Copyright 2001-2003, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *                   Jeremy Bowman;
 *                   Richard Atkinson;
 *
 * $Id: LineAndShapeRenderer.java,v 1.1 2007/10/10 19:05:08 vauchers Exp $
 *
 * Changes
 * -------
 * 23-Oct-2001 : Version 1 (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 16-Jan-2002 : Renamed HorizontalCategoryItemRenderer.java --> CategoryItemRenderer.java (DG);
 * 05-Feb-2002 : Changed return type of the drawCategoryItem method from void to Shape, as part
 *               of the tooltips implementation (DG);
 * 11-May-2002 : Support for value label drawing (JB);
 * 29-May-2002 : Now extends AbstractCategoryItemRenderer (DG);
 * 25-Jun-2002 : Removed redundant import (DG);
 * 05-Aug-2002 : Small modification to drawCategoryItem method to support URLs for
 *               HTML image maps (RA);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 11-Oct-2002 : Added new constructor to incorporate tool tip and URL generators (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and CategoryToolTipGenerator
 *               interface (DG);
 * 05-Nov-2002 : Base dataset is now TableDataset not CategoryDataset (DG);
 * 06-Nov-2002 : Renamed drawCategoryItem(...) --> drawItem(...) and now using axis for
 *               category spacing (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 * 10-Apr-2003 : Changed CategoryDataset to KeyedValues2DDataset in drawItem(...) method (DG);
 * 12-May-2003 : Modified to take into account the plot orientation (DG);
 *
 */

package org.jfree.chart.renderer;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.CategoryDataset;
import org.jfree.util.BooleanList;

/**
 * A renderer that draws shapes for each data item, and lines between data items.
 * <p>
 * For use with the {@link CategoryPlot} class.
 *
 * @author David Gilbert
 */
public class LineAndShapeRenderer extends AbstractCategoryItemRenderer implements Serializable {

    /** Useful constant for specifying the type of rendering (shapes only). */
    public static final int SHAPES = 1;

    /** Useful constant for specifying the type of rendering (lines only). */
    public static final int LINES = 2;

    /** Useful constant for specifying the type of rendering (shapes and lines). */
    public static final int SHAPES_AND_LINES = 3;

    /** Constant indicating that labels are to be shown above data points */
    public static final int TOP = 1;

    /** Constant indicating that labels are to be shown below data points */
    public static final int BOTTOM = 2;

    /** Constant indicating that labels are to be shown left of data points */
    public static final int LEFT = 3;

    /** Constant indicating that labels are to be shown right of data points */
    public static final int RIGHT = 4;

    /** A flag indicating whether or not shapes are drawn at each XY point. */
    private boolean drawShapes;

    /** A flag indicating whether or not lines are drawn between XY points. */
    private boolean drawLines;

    /** A flag that controls whether or not shapes are filled for ALL series. */
    private Boolean shapesFilled;
    
    /** A table of flags that control (per series) whether or not shapes are filled. */
    private BooleanList seriesShapesFilled;
    
    /** The default value returned by the getShapeFilled(...) method. */
    private Boolean defaultShapesFilled;

    /**
     * Constructs a default renderer (draws shapes and lines).
     */
    public LineAndShapeRenderer() {
        this(SHAPES_AND_LINES);
    }

    /**
     * Constructs a renderer of the specified type.
     * <P>
     * Use one of the constants SHAPES, LINES or SHAPES_AND_LINES.
     *
     * @param type  the type of renderer.
     * 
     */
    public LineAndShapeRenderer(int type) {
        super();
        if (type == SHAPES) {
            this.drawShapes = true;
        }
        if (type == LINES) {
            this.drawLines = true;
        }
        if (type == SHAPES_AND_LINES) {
            this.drawShapes = true;
            this.drawLines = true;
        }

        this.shapesFilled = null;
        this.seriesShapesFilled = new BooleanList();
        this.defaultShapesFilled = Boolean.TRUE;
    }

    /**
     * Returns <code>true</code> if a shape should be drawn to represent each data point, and
     * <code>false</code> otherwise.
     *
     * @return A boolean flag.
     */
    public boolean isDrawShapes() {
        return this.drawShapes;
    }

    /**
     * Sets the flag that controls whether or not a shape should be drawn to represent each data
     * point.
     *
     * @param draw  the new value of the flag.
     */
    public void setDrawShapes(boolean draw) {
        if (draw != this.drawShapes) {
            this.drawShapes = draw;
            this.firePropertyChanged("Shapes", new Boolean (!draw), new Boolean (draw));
        }
    }

    /**
     * Returns <code>true</code> if a line should be drawn from the previous to the current data
     * point, and <code>false</code> otherwise.
     *
     * @return A boolean flag.
     */
    public boolean isDrawLines() {
        return this.drawLines;
    }

    /**
     * Sets the flag that controls whether or not lines are drawn between consecutive data points.
     *
     * @param draw  the new value of the flag.
     */
    public void setDrawLines(boolean draw) {
        if (draw != this.drawLines) {
            this.drawLines = draw;
            this.firePropertyChanged("Lines", new Boolean (!draw), new Boolean (draw));
        }
    }

    // SHAPES FILLED
    
    /**
     * Returns the flag used to control whether or not the shape for an item is filled. 
     * <p>
     * The default implementation passes control to the <code>getSeriesShapesFilled</code> method.
     * You can override this method if you require different behaviour.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return  A boolean.
     */
    public boolean getItemShapeFilled(int series, int item) {
        return getSeriesShapesFilled(series);
    }

    /**
     * Returns the flag used to control whether or not the shapes for a series are filled. 
     *
     * @param series  the series index (zero-based).
     *
     * @return  A boolean.
     */
    public boolean getSeriesShapesFilled(int series) {

        // return the overall setting, if there is one...
        if (this.shapesFilled != null) {
            return this.shapesFilled.booleanValue();
        }

        // otherwise look up the paint table
        Boolean flag = this.seriesShapesFilled.getBoolean(series);
        if (flag != null) {
            return flag.booleanValue();
        }
        else {
            return this.defaultShapesFilled.booleanValue();
        } 

    }

    /**
     * Sets the 'shapes filled' for ALL series.
     * 
     * @param filled  the flag.
     */
    public void setShapesFilled(boolean filled) {
        setShapesFilled(Boolean.valueOf(filled));
    }
    
    /**
     * Sets the 'shapes filled' for ALL series.
     * 
     * @param filled  the flag (<code>null</code> permitted).
     */
    public void setShapesFilled(Boolean filled) {
        this.shapesFilled = filled;
    }
    
    /**
     * Sets the 'shapes filled' flag for a series.
     *
     * @param series  the series index (zero-based).
     * @param filled  the flag.
     */
    public void setSeriesShapesFilled(int series, Boolean filled) {
        this.seriesShapesFilled.setBoolean(series, filled);
    }

    /**
     * Returns the default 'shape filled' attribute.
     *
     * @return The default flag.
     */
    public Boolean getDefaultShapesFilled() {
        return this.defaultShapesFilled;
    }

    /**
     * Sets the default 'shapes filled' flag.
     *
     * @param flag  the flag.
     */
    public void setDefaultShapesFilled(Boolean flag) {
        this.defaultShapesFilled = flag;
    }

    /**
     * Draw a single data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the data is drawn.
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

        // nothing is drawn for null...
        Number value = data.getValue(row, column);
        if (value == null) {
            return;
        }

        PlotOrientation orientation = plot.getOrientation();

        // current data point...
        double x1 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea,
                                                 plot.getDomainAxisEdge());
        double y1 = rangeAxis.translateValueToJava2D(value.doubleValue(), dataArea,
                                                     plot.getRangeAxisEdge());

        g2.setPaint(getItemPaint(row, column));
        g2.setStroke(getItemStroke(row, column));

        Shape shape = getItemShape(row, column);
        if (orientation == PlotOrientation.HORIZONTAL) {
            shape = createTransformedShape(shape, y1, x1);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            shape = createTransformedShape(shape, x1, y1);
        }
        if (this.drawShapes) {
            
            if (getItemShapeFilled(row, column)) {
                g2.fill(shape);
            }
            else {
                g2.draw(shape);
            }
        }

        if (this.drawLines) {
            if (column != 0) {

                Number previousValue = data.getValue(row, column - 1);
                if (previousValue != null) {

                    // previous data point...
                    double previous = previousValue.doubleValue();
                    double x0 = domainAxis.getCategoryMiddle(column - 1,
                                                             getColumnCount(), dataArea,
                                                             plot.getDomainAxisEdge());
                    double y0 = rangeAxis.translateValueToJava2D(previous, dataArea,
                                                                 plot.getRangeAxisEdge());

                    g2.setPaint(getItemPaint(row, column));
                    g2.setStroke(getItemStroke(row, column));
                    Line2D line = null;
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        line = new Line2D.Double(y0, x0, y1, x1);
                    }
                    else if (orientation == PlotOrientation.VERTICAL) {
                        line = new Line2D.Double(x0, y0, x1, y1);
                    }
                    g2.draw(line);
                }
            }
        }

        // draw the item labels if there are any...
        if (isItemLabelVisible(row, column)) {
            drawItemLabel(g2, orientation, data, row, column, x1, y1, (value.doubleValue() < 0.0));
        }

        // collect entity and tool tip information...
        if (getInfo() != null) {
            EntityCollection entities = getInfo().getEntityCollection();
            if (entities != null && shape != null) {
                String tip = null;
                CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
                if (generator != null) {
                    tip = generator.generateToolTip(data, row, column);
                }
                String url = null;
                if (getItemURLGenerator(row, column) != null) {
                    url = getItemURLGenerator(row, column).generateURL(data, row, column);
                }
                CategoryItemEntity entity = new CategoryItemEntity(shape, tip, url, row,
                                                                 data.getColumnKey(column), column);
                entities.addEntity(entity);

            }

        }

    }


}
