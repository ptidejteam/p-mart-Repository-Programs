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
 * -----------------------
 * StackedBarRenderer.java
 * -----------------------
 * (C) Copyright 2000-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Richard Atkinson;
 *                   Thierry Saura;
 *                   Christian W. Zuckschwerdt;
 *
 * $Id: StackedBarRenderer.java,v 1.1 2007/10/10 19:09:11 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Oct-2001 : Version 1 (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 22-Nov-2001 : Modified to allow for negative data values (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 16-Jan-2002 : Fixed bug for single category datasets (DG);
 * 15-Feb-2002 : Added isStacked() method (DG);
 * 14-Mar-2002 : Modified to implement the CategoryItemRenderer interface (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 * 11-Jun-2002 : Added check for (permitted) null info object, bug and fix reported by David
 *               Basten.  Also updated Javadocs. (DG);
 * 25-Jun-2002 : Removed redundant import (DG);
 * 26-Jun-2002 : Small change to entity (DG);
 * 05-Aug-2002 : Small modification to drawCategoryItem method to support URLs for HTML image
 *               maps (RA);
 * 08-Aug-2002 : Added optional linking lines, contributed by Thierry Saura (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and CategoryToolTipGenerator
 *               interface (DG);
 * 05-Nov-2002 : Replaced references to CategoryDataset with TableDataset (DG);
 * 26-Nov-2002 : Replaced isStacked() method with getRangeType() method (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 12-May-2003 : Merged horizontal and vertical stacked bar renderers (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 
 */

package org.jfree.chart.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.CategoryDataset;
import org.jfree.ui.RectangleEdge;

/**
 * A stacked bar renderer for use with the {@link org.jfree.chart.plot.CategoryPlot} class.
 *
 * @author David Gilbert
 */
public class StackedBarRenderer extends BarRenderer implements Serializable {

    /** Linking lines flag. */
    private boolean linkingLines = false;

    /** Points set register. */
    private transient double[] pointsRegister = null;

    /**
     * Creates a new renderer with no tool tip generator and no URL generator.
     * <P>
     * The defaults (no tool tip or URL generators) have been chosen to minimise the processing
     * required to generate a default chart.  If you require tool tips or URLs, then you can
     * easily add the required generators.
     */
    public StackedBarRenderer() {
        super();
    }

    /**
     * Returns the range type.
     *
     * @return the range type.
     */
    public RangeType getRangeType() {
        return RangeType.STACKED;
    }

    /**
     * Returns a flag to indicate whether or not there are lines between the items.
     *
     * @return boolean
     */
    public boolean hasLinkingLines() {
        return this.linkingLines;
    }

    /**
     * Sets or unsets the linking lines between items.
     *
     * @param status boolean linking lines if true.
     */
    public void setLinkingLines(boolean status) {
        this.linkingLines = status;
    }

    /**
     * Initialises the renderer.
     * <p>
     * This method gets called once at the start of the process of drawing a chart.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param info  optional information collection.
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           CategoryPlot plot,
                           ChartRenderingInfo info) {

        super.initialise(g2, dataArea, plot, info);

        // calculate the bar width
        CategoryAxis domainAxis = plot.getDomainAxis();
        CategoryDataset data = plot.getDataset();
        if (data != null) {
            PlotOrientation orientation = plot.getOrientation();
            double space = 0.0;
            if (orientation == PlotOrientation.HORIZONTAL) {
                space = dataArea.getHeight();
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                space = dataArea.getWidth();
            }
            int columns = data.getColumnCount();
            double categoryMargin = 0.0;
            if (columns > 1) {
                categoryMargin = domainAxis.getCategoryMargin();
            }

            double used = space * (1 - domainAxis.getLowerMargin() - domainAxis.getUpperMargin()
                                     - categoryMargin);
            if (columns > 0) {
                setBarWidth(used / columns);
            }
            else {
                setBarWidth(used);
            }
        }

    }

    /**
     * Draws a stacked bar for a specific item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain (category) axis.
     * @param rangeAxis  the range (value) axis.
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
            drawHorizontalItem(g2, dataArea,
                               plot, domainAxis, rangeAxis, data, row, column);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            drawVerticalItem(g2, dataArea,
                             plot, domainAxis, rangeAxis, data, row, column);
        }

    }

    /**
     * Draws a stacked bar for a specific item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawHorizontalItem(Graphics2D g2,
                                   Rectangle2D dataArea,
                                   CategoryPlot plot,
                                   CategoryAxis domainAxis,
                                   ValueAxis rangeAxis,
                                   CategoryDataset dataset,
                                   int row,
                                   int column) {

        // RECT X
        double positiveBase = 0.0;
        double negativeBase = 0.0;
        for (int i = 0; i < row; i++) {
            Number v = dataset.getValue(i, column);
            if (v != null) {
                double d = v.doubleValue();
                if (d > 0) {
                    positiveBase = positiveBase + d;
                }
                else {
                    negativeBase = negativeBase + d;
                }
            }
        }


        Number value = dataset.getValue(row, column);
        if (value != null) {
            double xx = value.doubleValue();
            double translatedBase;
            double translatedValue;
            double rectX;

            RectangleEdge location = plot.getRangeAxisEdge();
            if (xx > 0) {
                translatedBase = rangeAxis.translateValueToJava2D(positiveBase, dataArea, location);
                translatedValue = rangeAxis.translateValueToJava2D(positiveBase + xx, dataArea, 
                                                                   location);
                rectX = Math.min(translatedBase, translatedValue);
            }
            else {
                translatedBase = rangeAxis.translateValueToJava2D(negativeBase, dataArea, location);
                translatedValue = rangeAxis.translateValueToJava2D(negativeBase + xx, dataArea, 
                                                                   location);
                rectX = Math.min(translatedBase, translatedValue);
            }

            // Y
            double rectY = domainAxis.getCategoryStart(column, getColumnCount(), dataArea,
                                                       plot.getDomainAxisEdge());

            // RECT WIDTH
            double rectWidth = Math.abs(translatedValue - translatedBase);
            // Supplied as a parameter as it is constant

            // rect HEIGHT
            double rectHeight = getBarWidth();

            Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
            Paint seriesPaint = getItemPaint(row, column);
            g2.setPaint(seriesPaint);
            g2.fill(bar);
            if (getBarWidth() > 3) {
                g2.setStroke(getItemStroke(row, column));
                g2.setPaint(getItemOutlinePaint(row, column));
                g2.draw(bar);
            }

            // collect entity and tool tip information...
            if (getInfo() != null) {
                EntityCollection entities = getInfo().getEntityCollection();
                if (entities != null) {
                    String tip = null;
                    CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
                    if (generator != null) {
                        tip = generator.generateToolTip(dataset, row, column);
                    }
                    String url = null;
                    if (getItemURLGenerator(row, column) != null) {
                        url = getItemURLGenerator(row, column).generateURL(dataset, row, column);
                    }
                    CategoryItemEntity entity = new CategoryItemEntity(
                        bar, tip, url, dataset, row, dataset.getColumnKey(column), column
                    );
                    entities.addEntity(entity);
                }
            }
        }

    }

    /**
     * Draws a stacked bar for a specific item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain (category) axis.
     * @param rangeAxis  the range (value) axis.
     * @param data  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawVerticalItem(Graphics2D g2,
                                 Rectangle2D dataArea,
                                 CategoryPlot plot,
                                 CategoryAxis domainAxis,
                                 ValueAxis rangeAxis,
                                 CategoryDataset dataset,
                                 int row,
                                 int column) {

        Paint seriesPaint = getItemPaint(row, column);

        if (hasLinkingLines() && (pointsRegister == null)) {
            // need to init the points set...
            pointsRegister = new double[dataset.getColumnCount() * dataset.getRowCount() * 2];
        }

        // BAR X
        double rectX = domainAxis.getCategoryStart(column, getColumnCount(), dataArea,
                                                   plot.getDomainAxisEdge());

        // BAR Y
        double positiveBase = 0.0;
        double negativeBase = 0.0;

        for (int i = 0; i < row; i++) {
            Number v = dataset.getValue(i, column);
            if (v != null) {
                double d = v.doubleValue();
                if (d > 0) {
                    positiveBase = positiveBase + d;
                }
                else {
                    negativeBase = negativeBase + d;
                }
            }
        }

        boolean positiveValue = false;
        Number value = dataset.getValue(row, column);
        if (value != null) {
            double xx = value.doubleValue();
            double translatedBase;
            double translatedValue;
            double barY;

            RectangleEdge location = plot.getRangeAxisEdge();
            if (xx > 0) {
                translatedBase = rangeAxis.translateValueToJava2D(positiveBase, dataArea, location);
                translatedValue = rangeAxis.translateValueToJava2D(positiveBase + xx, dataArea, 
                                                                   location);
                barY = Math.min(translatedBase, translatedValue);
                positiveValue = true;
            }
            else {
                translatedBase = rangeAxis.translateValueToJava2D(negativeBase, dataArea, location);
                translatedValue = rangeAxis.translateValueToJava2D(negativeBase + xx, dataArea, 
                                                                   location);
                barY = Math.min(translatedBase, translatedValue);
            }

            double rectWidth = getBarWidth();
            double barHeight = Math.abs(translatedValue - translatedBase);

            Rectangle2D bar = new Rectangle2D.Double(rectX, barY, rectWidth, barHeight);
            g2.setPaint(seriesPaint);
            g2.fill(bar);
            if (rectWidth > 3) {
                g2.setStroke(getItemStroke(row, column));
                g2.setPaint(getItemOutlinePaint(row, column));
                g2.draw(bar);
            }

            if (hasLinkingLines()) {
                // the same series of two categories are linked together
                if (column == 0) {
                    // first category, no line drawn
                    pointsRegister[2 * row] = rectX + rectWidth;
                    if (positiveValue) {
                        pointsRegister[(2 * row) + 1] = barY;
                    }
                    else {
                        pointsRegister[(2 * row) + 1] = barY + barHeight;
                    }
                }
                else {
                    // other categories
                    int position = (column * dataset.getRowCount() * 2) + (2 * row);
                    pointsRegister[position] = rectX + rectWidth;
                    if (positiveValue) {
                        // draw a line between two stacked bars
                        double lastX = pointsRegister[position - 2 * dataset.getRowCount()];
                        if (lastX > 0.0) {
                            Line2D line = new Line2D.Double(rectX, barY, lastX,
                                pointsRegister[position - 2 * dataset.getRowCount() + 1]);
                            g2.setPaint(Color.black);
                            g2.draw(line);
                        }
                        // register the base's extremity of the drawing bar
                        pointsRegister[position + 1] = barY;
                    }
                    else {
                        double lastX = pointsRegister[position - 2 * dataset.getRowCount()];
                        if (lastX > 0.0) {
                            Line2D line = new Line2D.Double(rectX, barY + barHeight, lastX,
                                pointsRegister[position - 2 * dataset.getRowCount() + 1]);
                            g2.setPaint(Color.black);
                            g2.draw(line);
                        }
                        pointsRegister[position + 1] = barY + barHeight;
                    }
                }
            }


            // collect entity and tool tip information...
            if (getInfo() != null) {
                EntityCollection entities = getInfo().getEntityCollection();
                if (entities != null) {
                    String tip = null;
                    CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
                    if (generator != null) {
                        tip = generator.generateToolTip(dataset, row, column);
                    }
                    String url = null;
                    if (getItemURLGenerator(row, column) != null) {
                        url = getItemURLGenerator(row, column).generateURL(dataset, row, column);
                    }
                    CategoryItemEntity entity = new CategoryItemEntity(
                        bar, tip, url, dataset, row, dataset.getColumnKey(column), column
                    );
                    entities.addEntity(entity);
                }
            }
        }

    }

}
