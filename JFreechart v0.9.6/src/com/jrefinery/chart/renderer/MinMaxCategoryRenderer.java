/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
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
 * ---------------------------
 * MinMaxCategoryRenderer.java
 * ---------------------------
 * (C) Copyright 2002, 2003, by Simba Management Limited.
 *
 * Original Author:  Tomer Peretz;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: MinMaxCategoryRenderer.java,v 1.1 2007/10/10 19:57:50 vauchers Exp $
 *
 * Changes:
 * --------
 * 29-May-2002 : Version 1 (TP);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and CategoryToolTipGenerator
 *               interface (DG);
 * 05-Nov-2002 : Base dataset is now TableDataset not CategoryDataset (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 *
 */

package com.jrefinery.chart.renderer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.Component;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Arc2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.text.NumberFormat;
import javax.swing.Icon;
import com.jrefinery.chart.Marker;
import com.jrefinery.chart.axis.CategoryAxis;
import com.jrefinery.chart.axis.ValueAxis;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.CategoryItemEntity;
import com.jrefinery.chart.plot.CategoryPlot;
import com.jrefinery.ui.RefineryUtilities;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.Range;

/**
 * Renderer for drawing min max plot. This renderer draws all the series under the same category 
 * in the same x position using <code>objectIcon</code> and a line from the maximum value to the 
 * minimum value.
 * <p>
 * For use with the {@link com.jrefinery.chart.plot.VerticalCategoryPlot} class.
 *
 * @author Tomer Peretz
 */
public class MinMaxCategoryRenderer extends AbstractCategoryItemRenderer {

    /** Scale factor for standard shapes. */
    private double shapeScale = 6;

    /** A flag indicating whether or not lines are drawn between XY points. */
    private boolean plotLines = false;

    /** The paint of the line between the minimum value and the maximum value.*/
    private Paint groupPaint = Color.black;

    /** The stroke of the line between the minimum value and the maximum value.*/
    private Stroke groupStroke = new BasicStroke(1.0f);

    /** The icon used to indicate the minimum value.*/
    private Icon minIcon = getIcon(new Arc2D.Double(-4, -4, 8, 8, 0, 360, Arc2D.OPEN),
                                   null, Color.black);

    /** The icon used to indicate the maximum value.*/
    private Icon maxIcon = getIcon(new Arc2D.Double(-4, -4, 8, 8, 0, 360, Arc2D.OPEN),
                                   null, Color.black);

    /** The icon used to indicate the values.*/
    private Icon objectIcon = getIcon(new Line2D.Double(-4, 0, 4, 0), false, true);

    /** The last category. */
    private int lastCategory = -1;

    /** The minimum. */
    private double min;

    /** The maximum. */
    private double max;

    /** The minimum number. */
    private Number minValue;

    /** The maximum number. */
    private Number maxValue;

    /**
     * Default constructor.
     */
    public MinMaxCategoryRenderer () {
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
     * @param dataset  the dataset index (zero-based).
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawItem (Graphics2D g2,
                          Rectangle2D dataArea,
                          CategoryPlot plot,
                          CategoryAxis domainAxis,
                          ValueAxis rangeAxis,
                          CategoryDataset data,
                          int dataset,
                          int row,
                          int column) {

        // first check the number we are plotting...
        Number value = data.getValue(row, column);
        if (value != null) {
            // current data point...
            double x1 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea);
            double y1 = rangeAxis.translateValueToJava2D(value.doubleValue(), dataArea);
            g2.setPaint(getItemPaint(dataset, row, column));
            g2.setStroke(getItemStroke(dataset, row, column));
            Shape shape = null;
            shape = new Rectangle2D.Double(x1 - 4, y1 - 4, 8.0, 8.0);
            objectIcon.paintIcon(null, g2, (int) x1, (int) y1);
            if (lastCategory == column) {
                if (minValue.doubleValue() > value.doubleValue()) {
                    min = y1;
                    minValue = value;
                }
                if (maxValue.doubleValue() < value.doubleValue()) {
                    max = y1;
                    maxValue = value;
                }
                if (data.getRowCount() - 1 == row) {
                    g2.setPaint(groupPaint);
                    g2.setStroke(groupStroke);
                    g2.draw(new Line2D.Double(x1, min, x1, max));
                    minIcon.paintIcon(null, g2, (int) x1, (int) min);
                    maxIcon.paintIcon(null, g2, (int) x1, (int) max);
                    if (plot.getValueLabelsVisible()) {
                        NumberFormat formatter = plot.getValueLabelFormatter();
                        Font labelFont = plot.getValueLabelFont();
                        g2.setFont(labelFont);
                        Paint paint = plot.getValueLabelPaint();
                        g2.setPaint(paint);
                        boolean rotate = plot.getVerticalValueLabels();
                        drawLabel(g2, formatter.format(minValue), x1, min,
                                labelFont, rotate, LineAndShapeRenderer.BOTTOM);
                        drawLabel(g2, formatter.format(maxValue), x1, max,
                                labelFont, rotate, LineAndShapeRenderer.TOP);
                    }
                }
            }
            else {
                lastCategory = column;
                min = y1;
                max = y1;
                minValue = value;
                maxValue = value;
            }
            // connect to the previous point
            if (this.plotLines) {
                if (column != 0) {
                    Number previousValue = data.getValue(row, column - 1);
                    if (previousValue != null) {
                        // previous data point...
                        double previous = previousValue.doubleValue();
                        double x0 = domainAxis.getCategoryStart(column - 1, getColumnCount(), 
                                                                dataArea);
                        double y0 = rangeAxis.translateValueToJava2D(previous, dataArea);
                        g2.setPaint(getItemPaint(dataset, row, column));
                        g2.setStroke(getItemStroke(dataset, row, column));
                        Line2D line = new Line2D.Double(x0, y0, x1, y1);
                        g2.draw(line);
                    }
                }
            }

            // collect entity and tool tip information...
            if (getInfo() != null) {
                EntityCollection entities = getInfo().getEntityCollection();
                if (entities != null && shape != null) {
                    String tip = null;
                    if (getToolTipGenerator() != null) {
                        tip = getToolTipGenerator().generateToolTip(data, row, column);
                    }
                    CategoryItemEntity entity = new CategoryItemEntity(shape, tip, row, 
                                                        data.getColumnKey(column), column);
                    entities.addEntity(entity);
                }
            }
        }
    }

    /**
     * Draws a value label on the plot.
     *
     * @param g2  the graphics device.
     * @param label  the label text.
     * @param x  the x position of the data point.
     * @param y  the y position of the data point.
     * @param labelFont  the font to draw the label with.
     * @param rotate  true if the label is to be rotated 90 degrees, false otherwise
     * @param labelPosition  the position of the label
     */
    private void drawLabel (Graphics2D g2, String label, double x, double y,
                            Font labelFont, boolean rotate, int labelPosition) {

        FontRenderContext frc = g2.getFontRenderContext();
        Rectangle2D labelBounds = labelFont.getStringBounds(label, frc);
        LineMetrics lm = labelFont.getLineMetrics(label, frc);
        float lead = lm.getLeading();
        double width = labelBounds.getWidth();
        double height = labelBounds.getHeight();
        float labelx;
        float labely;
        int position = labelPosition;
        if (rotate) {
            if (position == LineAndShapeRenderer.TOP) {
                labelx = (float) (x + height / 2 - lm.getDescent());
                labely = (float) (y - shapeScale);
            }
            else if (position == LineAndShapeRenderer.BOTTOM) {
                labelx = (float) (x + height / 2 - lm.getDescent());
                labely = (float) (y + shapeScale + width);
            }
            else if (position == LineAndShapeRenderer.LEFT) {
                labelx = (float) (x - shapeScale / 2 - lead - lm.getDescent());
                labely = (float) (y + width / 2);
            }
            else {
                labelx = (float) (x + shapeScale / 2 + lead + lm.getAscent());
                labely = (float) (y + width / 2);
            }
            RefineryUtilities.drawRotatedString(label, g2, labelx, labely, -Math.PI / 2);
        }
        else {
            if (position == LineAndShapeRenderer.TOP) {
                labelx = (float) (x - width / 2);
                labely = (float) (y - shapeScale / 2 - lm.getDescent() - lead);
            }
            else if (position == LineAndShapeRenderer.BOTTOM) {
                labelx = (float) (x - width / 2);
                labely = (float) (y + shapeScale / 2 + lm.getAscent() + lead);
            }
            else if (position == LineAndShapeRenderer.LEFT) {
                labelx = (float) (x - shapeScale - width);
                labely = (float) (y + height / 2 - lm.getDescent());
            }
            else {
                labelx = (float) (x + shapeScale);
                labely = (float) (y + height / 2 - lm.getDescent());
            }
            g2.drawString(label, labelx, labely);
        }
    }

    /**
     * Draws a horizontal line across the chart to represent the marker.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param marker  the marker line.
     * @param axisDataArea  the axis data area.
     * @param dataClipRegion  the data clip region.
     */
    public void drawRangeMarker (Graphics2D g2, CategoryPlot plot, ValueAxis axis,
                                 Marker marker, Rectangle2D axisDataArea, Shape dataClipRegion) {
        double value = marker.getValue();
        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }
        double y = axis.translateValueToJava2D(marker.getValue(), axisDataArea);
        Line2D line = new Line2D.Double(axisDataArea.getMinX(), y, axisDataArea.getMaxX(), y);
        g2.setPaint(marker.getOutlinePaint());
        g2.draw(line);
    }

    /**
     * Sets whether or not lines are drawn between category points.
     *
     * @param drawLines  if true, then line will be drawn between sequenced categories.
     */
    public void setDrawLines (boolean drawLines) {
        this.plotLines = drawLines;
    }

    /**
     * Gets whether or not lines are drawn between category points.
     *
     * @return boolean true if line will be drawn between sequenced categories, otherwise false.
     */
    public boolean isDrawLines () {
        return plotLines;
    }

    /**
     * Sets the paint of the line between the minimum value and the maximum value.
     *
     * @param groupPaint  the new paint.
     */
    public void setGroupPaint (Paint groupPaint) {
        this.groupPaint = groupPaint;
    }

    /**
     * Gets the paint of the line between the minimum value and the maximum value.
     *
     * @return the paint.
     */
    public Paint getGroupPaint () {
        return groupPaint;
    }

    /**
     * Sets the stroke of the line between the minimum value and the maximum value.
     *
     * @param groupStroke The new stroke
     */
    public void setGroupStroke (Stroke groupStroke) {
        this.groupStroke = groupStroke;
    }

    /**
     * Gets the stroke of the line between the minimum value and the maximum value.
     *
     * @return Stroke The current stroke.
     */
    public Stroke getGroupStroke () {
        return  groupStroke;
    }

    /**
     * Sets the icon used to indicate the values.
     *
     * @param objectIcon  the icon.
     */
    public void setObjectIcon (Icon objectIcon) {
        this.objectIcon = objectIcon;
    }

    /**
     * Gets the icon used to indicate the values.
     *
     * @return the icon.
     */
    public Icon getObjectIcon () {
        return  objectIcon;
    }

    /**
     * Sets the icon used to indicate the maximum value.
     *
     * @param maxIcon  the max icon.
     */
    public void setMaxIcon (Icon maxIcon) {
        this.maxIcon = maxIcon;
    }

    /**
     * Gets the icon used to indicate the maximum value.
     *
     * @return the icon
     */
    public Icon getMaxIcone () {
        return maxIcon;
    }

    /**
     * Sets the icon used to indicate the minimum value.
     *
     * @param minIcon  the min icon.
     */
    public void setMinIcon (Icon minIcon) {
        this.minIcon = minIcon;
    }

    /**
     * Gets the icon used to indicate the minimum value.
     *
     * @return Icon
     */
    public Icon getMinIcon () {
        return  minIcon;
    }

    /**
     * Returns an icon.
     *
     * @param shape  the shape.
     * @param fillPaint  the fill paint.
     * @param outlinePaint  the outline paint.
     *
     * @return the icon.
     */
    private Icon getIcon(Shape shape, final Paint fillPaint, final Paint outlinePaint) {

      final int width = shape.getBounds().width;
      final int height = shape.getBounds().height;
      final GeneralPath path = new GeneralPath(shape);
      return new Icon() {
          public void paintIcon(Component c, Graphics g, int x, int y) {
              Graphics2D g2 = (Graphics2D) g;
              path.transform(AffineTransform.getTranslateInstance(x, y));
              if (fillPaint != null) {
                  g2.setPaint(fillPaint);
                  g2.fill(path);
              }
              if (outlinePaint != null) {
                  g2.setPaint(outlinePaint);
                  g2.draw(path);
              }
              path.transform(AffineTransform.getTranslateInstance(-x, -y));
        }

        public int getIconWidth() {
            return width;
        }

        public int getIconHeight() {
            return height;
        }

      };
    }

    /**
     * Returns an icon.
     *
     * @param shape  the shape.
     * @param fill  the fill flag.
     * @param outline  the outline flag.
     *
     * @return the icon.
     */
    private Icon getIcon(Shape shape, final boolean fill, final boolean outline) {
        final int width = shape.getBounds().width;
        final int height = shape.getBounds().height;
        final GeneralPath path = new GeneralPath(shape);
        return new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                path.transform(AffineTransform.getTranslateInstance(x, y));
                if (fill) {
                    g2.fill(path);
                }
                if (outline) {
                    g2.draw(path);
                }
                path.transform(AffineTransform.getTranslateInstance(-x, -y));
            }

            public int getIconWidth() {
                return width;
            }

            public int getIconHeight() {
                return height;
            }
        };
    }

}
