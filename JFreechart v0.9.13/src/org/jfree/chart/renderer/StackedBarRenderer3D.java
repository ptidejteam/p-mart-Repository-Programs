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
 * StackedBarRenderer3D.java
 * -------------------------
 * (C) Copyright 2000-2003, by Serge V. Grachov and Contributors.
 *
 * Original Author:  Serge V. Grachov;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Richard Atkinson;
 *                   Christian W. Zuckschwerdt;
 *
 * $Id: StackedBarRenderer3D.java,v 1.1 2007/10/10 19:15:26 vauchers Exp $
 *
 * Changes
 * -------
 * 31-Oct-2001 : Version 1, contributed by Serge V. Grachov (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 15-Feb-2002 : Added isStacked() method (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 * 19-Jun-2002 : Added check for null info in drawCategoryItem method (DG);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 26-Jun-2002 : Small change to entity (DG);
 * 05-Aug-2002 : Small modification to drawCategoryItem method to support URLs for HTML image
 *               maps (RA);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and CategoryToolTipGenerator
 *               interface (DG);
 * 05-Nov-2002 : Replaced references to CategoryDataset with TableDataset (DG);
 * 26-Nov-2002 : Replaced isStacked() method with getRangeType() method (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Added default constructor (bug 726235) and fixed bug 726260) (DG);
 * 13-May-2003 : Renamed StackedVerticalBarRenderer3D --> StackedBarRenderer3d (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 
 */

package org.jfree.chart.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.data.CategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * Renders stacked bars with 3D-effect, for use with the {@link org.jfree.chart.plot.CategoryPlot}
 * class.
 *
 * @author Serge V. Grachov
 */
public class StackedBarRenderer3D extends BarRenderer3D 
                                  implements Cloneable, PublicCloneable, Serializable {

    /**
     * Creates a new renderer with no tool tip generator and no URL generator.
     * <P>
     * The defaults (no tool tip or URL generators) have been chosen to minimise the processing
     * required to generate a default chart.  If you require tool tips or URLs, then you can
     * easily add the required generators.
     */
    public StackedBarRenderer3D() {
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
     * Initialises the renderer.
     * <p>
     * This method gets called once at the start of the process of drawing a chart.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param index  the renderer index (<code>null</code> for the primary renderer).
     * @param info  optional information collection.
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           CategoryPlot plot,
                           Integer index,
                           PlotRenderingInfo info) {

        super.initialise(g2, dataArea, plot, index, info);

        // calculate the bar width
        CategoryAxis domainAxis = getDomainAxis(plot, index);
        CategoryDataset data = getDataset(plot, index);
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
     * Draws a stacked bar (with 3D-effect) for a specific item.
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
     * Draws a stacked bar (with 3D-effect) for a specific item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain (category) axis.
     * @param rangeAxis  the range (value) axis.
     * @param dataset  the dataset.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    protected void drawHorizontalItem(Graphics2D g2,
                                      Rectangle2D dataArea,
                                      CategoryPlot plot,
                                      CategoryAxis domainAxis,
                                      ValueAxis rangeAxis,
                                      CategoryDataset dataset,
                                      int row,
                                      int column) {

        Paint paint = getItemPaint(row, column);

        Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(),
                                                      dataArea.getY() + getYOffset(),
                                                      dataArea.getWidth() - getXOffset(),
                                                      dataArea.getHeight() - getYOffset());
        // BAR X
        double x2 = domainAxis.getCategoryStart(column, getColumnCount(), adjusted,
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

        Number value = dataset.getValue(row, column);
        if (value != null) {

            double xx = value.doubleValue();
            double translatedBase;
            double translatedValue;

            RectangleEdge location = plot.getRangeAxisEdge();
            if (xx > 0) {
                translatedBase = rangeAxis.translateValueToJava2D(positiveBase, adjusted, location);
                translatedValue = rangeAxis.translateValueToJava2D(positiveBase + xx, adjusted,
                                                                   location);
            }
            else {
                translatedBase = rangeAxis.translateValueToJava2D(negativeBase, adjusted, location);
                translatedValue = rangeAxis.translateValueToJava2D(negativeBase + xx, adjusted,
                                                                   location);
            }

            double y0 = Math.min(translatedBase, translatedValue);
            double y2 = Math.max(translatedBase, translatedValue);

            double x0 = x2 + getBarWidth();
            double x1 = x0 - getYOffset();
            double x3 = x2 - getYOffset();

            double y1 = y0 + getXOffset();
            double y3 = y2 + getXOffset();

            Rectangle2D bar = new Rectangle2D.Double(y0, x2, y2 - y0, x0 - x2);
            g2.setPaint(paint);
            g2.fill(bar);

            GeneralPath barR3d = null;
            GeneralPath barT3d = null;
            if ((x2 - y0) != 0) {
                if (xx > 0.0) {
                    barR3d = new GeneralPath();
                    barR3d.moveTo((float) y2, (float) x0);
                    barR3d.lineTo((float) y2, (float) x2);
                    barR3d.lineTo((float) y3, (float) x3);
                    barR3d.lineTo((float) y3, (float) x1);
                    barR3d.closePath();
                    if (paint instanceof Color) {
                        g2.setPaint(((Color) paint).darker());
                    }
                    g2.fill(barR3d);
                }
                
                barT3d = new GeneralPath();
                barT3d.moveTo((float) y0, (float) x2);
                barT3d.lineTo((float) y1, (float) x3);
                barT3d.lineTo((float) y3, (float) x3);
                barT3d.lineTo((float) y2, (float) x2);
                barT3d.closePath();
                if (paint instanceof Color) {
                    g2.setPaint(((Color) paint)); //.brighter());
                }
                g2.fill(barT3d);
            }

            if ((x0 - x2) > 3) {
                g2.setStroke(getItemStroke(row, column));
                g2.setPaint(getItemOutlinePaint(row, column));
                g2.draw(bar);
                if (barR3d != null) {
                    g2.draw(barR3d);
                }
                if (barT3d != null) {
                    g2.draw(barT3d);
                }
            }

            // collect entity and tool tip information...
            if (getInfo() != null) {
                EntityCollection entities = getInfo().getOwner().getEntityCollection();
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
     * Draws a stacked bar (with 3D-effect) for a specific item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain (category) axis.
     * @param rangeAxis  the range (value) axis.
     * @param dataset  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    protected void drawVerticalItem(Graphics2D g2,
                                    Rectangle2D dataArea,
                                    CategoryPlot plot,
                                    CategoryAxis domainAxis,
                                    ValueAxis rangeAxis,
                                    CategoryDataset dataset,
                                    int row,
                                    int column) {

        Paint seriesPaint = getItemPaint(row, column);

        Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(),
                                                      dataArea.getY() + getYOffset(),
                                                      dataArea.getWidth() - getXOffset(),
                                                      dataArea.getHeight() - getYOffset());
        // BAR X
        double x0 = domainAxis.getCategoryStart(column, getColumnCount(), adjusted,
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

        Number value = dataset.getValue(row, column);
        if (value != null) {

            double xx = value.doubleValue();
            double translatedBase;
            double translatedValue;

            RectangleEdge location = plot.getRangeAxisEdge();
            if (xx > 0) {
                translatedBase = rangeAxis.translateValueToJava2D(positiveBase, adjusted, location);
                translatedValue = rangeAxis.translateValueToJava2D(positiveBase + xx, adjusted,
                                                                   location);
            }
            else {
                translatedBase = rangeAxis.translateValueToJava2D(negativeBase, adjusted, location);
                translatedValue = rangeAxis.translateValueToJava2D(negativeBase + xx, adjusted,
                                                                   location);
            }

            double y0 = Math.max(translatedBase, translatedValue);
            double y2 = Math.min(translatedBase, translatedValue);

            double x1 = x0 + getBarWidth();
            double x2 = x0 + getXOffset();
            double x3 = x1 + getXOffset();

            double y1 = y0 - getYOffset();
            double y3 = y2 - getYOffset();

            Rectangle2D bar = new Rectangle2D.Double(x0, y2, x1 - x0, y0 - y2);
            g2.setPaint(seriesPaint);
            g2.fill(bar);

            GeneralPath barR3d = null;
            GeneralPath barT3d = null;
            if ((y0 - y2) != 0) {
                barR3d = new GeneralPath();
                barR3d.moveTo((float) x1, (float) y0);
                barR3d.lineTo((float) x1, (float) y2);
                barR3d.lineTo((float) x3, (float) y3);
                barR3d.lineTo((float) x3, (float) y1);
                barR3d.closePath();
                if (seriesPaint instanceof Color) {
                    g2.setPaint(((Color) seriesPaint).darker());
                }
                g2.fill(barR3d);

                if (xx > 0) {
                    barT3d = new GeneralPath();
                    barT3d.moveTo((float) x0, (float) y2);
                    barT3d.lineTo((float) x1, (float) y2);
                    barT3d.lineTo((float) x3, (float) y3);
                    barT3d.lineTo((float) x2, (float) y3);
                    barT3d.closePath();
                    if (seriesPaint instanceof Color) {
                        g2.setPaint(((Color) seriesPaint)); //.brighter());
                    }
                    g2.fill(barT3d);
                }
            }

            if ((x1 - x0) > 3) {
                g2.setStroke(getItemStroke(row, column));
                g2.setPaint(getItemOutlinePaint(row, column));
                g2.draw(bar);
                if (barR3d != null) {
                    g2.draw(barR3d);
                }
                if (barT3d != null) {
                    g2.draw(barT3d);
                }
            }

            // collect entity and tool tip information...
            if (getInfo() != null) {
                EntityCollection entities = getInfo().getOwner().getEntityCollection();
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
