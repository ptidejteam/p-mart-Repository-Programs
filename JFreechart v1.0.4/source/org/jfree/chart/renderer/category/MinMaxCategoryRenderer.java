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
 * ---------------------------
 * MinMaxCategoryRenderer.java
 * ---------------------------
 * (C) Copyright 2002-2007, by Object Refinery Limited.
 *
 * Original Author:  Tomer Peretz;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Christian W. Zuckschwerdt;
 *                   Nicolas Brodu (for Astrium and EADS Corporate Research 
 *                   Center);
 *
 * $Id: MinMaxCategoryRenderer.java,v 1.1 2007/10/10 20:46:51 vauchers Exp $
 *
 * Changes:
 * --------
 * 29-May-2002 : Version 1 (TP);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and 
 *               CategoryToolTipGenerator interface (DG);
 * 05-Nov-2002 : Base dataset is now TableDataset not CategoryDataset (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 * 10-Apr-2003 : Changed CategoryDataset to KeyedValues2DDataset in drawItem() 
 *               method (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 08-Sep-2003 : Implemented Serializable (NB);
 * 29-Oct-2003 : Added workaround for font alignment in PDF output (DG);
 * 05-Nov-2004 : Modified drawItem() signature (DG);
 * 17-Nov-2005 : Added change events and argument checks (DG);
 * 02-Feb-2007 : Removed author tags all over JFreeChart sources (DG);
 * 
 */

package org.jfree.chart.renderer.category;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.Icon;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.io.SerialUtilities;

/**
 * Renderer for drawing min max plot. This renderer draws all the series under 
 * the same category in the same x position using <code>objectIcon</code> and 
 * a line from the maximum value to the minimum value.
 * <p>
 * For use with the {@link org.jfree.chart.plot.CategoryPlot} class.
 */
public class MinMaxCategoryRenderer extends AbstractCategoryItemRenderer {

    /** For serialization. */
    private static final long serialVersionUID = 2935615937671064911L;
    
    /** A flag indicating whether or not lines are drawn between XY points. */
    private boolean plotLines = false;

    /** 
     * The paint of the line between the minimum value and the maximum value.
     */
    private transient Paint groupPaint = Color.black;

    /** 
     * The stroke of the line between the minimum value and the maximum value.
     */
    private transient Stroke groupStroke = new BasicStroke(1.0f);

    /** The icon used to indicate the minimum value.*/
    private transient Icon minIcon = getIcon(new Arc2D.Double(-4, -4, 8, 8, 0,
            360, Arc2D.OPEN), null, Color.black);

    /** The icon used to indicate the maximum value.*/
    private transient Icon maxIcon = getIcon(new Arc2D.Double(-4, -4, 8, 8, 0,
            360, Arc2D.OPEN), null, Color.black);

    /** The icon used to indicate the values.*/
    private transient Icon objectIcon = getIcon(new Line2D.Double(-4, 0, 4, 0),
            false, true);

    /** The last category. */
    private int lastCategory = -1;

    /** The minimum. */
    private double min;

    /** The maximum. */
    private double max;

    /**
     * Default constructor.
     */
    public MinMaxCategoryRenderer() {
        super();
    }

    /**
     * Gets whether or not lines are drawn between category points.
     *
     * @return boolean true if line will be drawn between sequenced categories,
     *         otherwise false.
     *         
     * @see #setDrawLines(boolean)
     */
    public boolean isDrawLines() {
        return this.plotLines;
    }

    /**
     * Sets the flag that controls whether or not lines are drawn to connect
     * the items within a series and sends a {@link RendererChangeEvent} to 
     * all registered listeners.
     *
     * @param draw  the new value of the flag.
     * 
     * @see #isDrawLines()
     */
    public void setDrawLines(boolean draw) {
        if (this.plotLines != draw) {
            this.plotLines = draw;
            this.notifyListeners(new RendererChangeEvent(this));
        }
        
    }

    /**
     * Returns the paint used to draw the line between the minimum and maximum
     * value items in each category.
     *
     * @return The paint (never <code>null</code>).
     * 
     * @see #setGroupPaint(Paint)
     */
    public Paint getGroupPaint() {
        return this.groupPaint;
    }

    /**
     * Sets the paint used to draw the line between the minimum and maximum
     * value items in each category and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     * 
     * @see #getGroupPaint()
     */
    public void setGroupPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.groupPaint = paint;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the stroke used to draw the line between the minimum and maximum
     * value items in each category.
     *
     * @return The stroke (never <code>null</code>).
     * 
     * @see #setGroupStroke(Stroke)
     */
    public Stroke getGroupStroke() {
        return this.groupStroke;
    }

    /**
     * Sets the stroke of the line between the minimum value and the maximum 
     * value.
     *
     * @param groupStroke The new stroke
     */
    public void setGroupStroke(Stroke groupStroke) {
        this.groupStroke = groupStroke;
    }

    /**
     * Returns the icon drawn for each data item.
     *
     * @return The icon (never <code>null</code>).
     * 
     * @see #setObjectIcon(Icon)
     */
    public Icon getObjectIcon() {
        return this.objectIcon;
    }

    /**
     * Sets the icon drawn for each data item.
     *
     * @param icon  the icon.
     * 
     * @see #getObjectIcon()
     */
    public void setObjectIcon(Icon icon) {
        if (icon == null) {
            throw new IllegalArgumentException("Null 'icon' argument.");
        }
        this.objectIcon = icon;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the icon displayed for the maximum value data item within each
     * category.
     *
     * @return The icon (never <code>null</code>).
     * 
     * @see #setMaxIcon(Icon)
     */
    public Icon getMaxIcon() {
        return this.maxIcon;
    }

    /**
     * Sets the icon displayed for the maximum value data item within each
     * category and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param icon  the icon (<code>null</code> not permitted).
     * 
     * @see #getMaxIcon()
     */
    public void setMaxIcon(Icon icon) {
        if (icon == null) {
            throw new IllegalArgumentException("Null 'icon' argument.");
        }
        this.maxIcon = icon;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the icon displayed for the minimum value data item within each
     * category.
     *
     * @return The icon (never <code>null</code>).
     * 
     * @see #setMinIcon(Icon)
     */
    public Icon getMinIcon() {
        return this.minIcon;
    }

    /**
     * Sets the icon displayed for the minimum value data item within each
     * category and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param icon  the icon (<code>null</code> not permitted).
     * 
     * @see #getMinIcon()
     */
    public void setMinIcon(Icon icon) {
        if (icon == null) {
            throw new IllegalArgumentException("Null 'icon' argument.");
        }
        this.minIcon = icon;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Draw a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area in which the data is drawn.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2, CategoryItemRendererState state,
            Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis,
            ValueAxis rangeAxis, CategoryDataset dataset, int row, int column,
            int pass) {

        // first check the number we are plotting...
        Number value = dataset.getValue(row, column);
        if (value != null) {
            // current data point...
            double x1 = domainAxis.getCategoryMiddle(
                column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
            double y1 = rangeAxis.valueToJava2D(
                value.doubleValue(), dataArea, plot.getRangeAxisEdge());
            g2.setPaint(getItemPaint(row, column));
            g2.setStroke(getItemStroke(row, column));
            Shape shape = null;
            shape = new Rectangle2D.Double(x1 - 4, y1 - 4, 8.0, 8.0);
            this.objectIcon.paintIcon(null, g2, (int) x1, (int) y1);
            if (this.lastCategory == column) {
                if (this.min > value.doubleValue()) {
                    this.min = value.doubleValue();
                }
                if (this.max < value.doubleValue()) {
                    this.max = value.doubleValue();
                }
                if (dataset.getRowCount() - 1 == row) {
                    g2.setPaint(this.groupPaint);
                    g2.setStroke(this.groupStroke);
                    double minY = rangeAxis.valueToJava2D(this.min, dataArea, 
                            plot.getRangeAxisEdge());
                    double maxY = rangeAxis.valueToJava2D(this.max, dataArea, 
                            plot.getRangeAxisEdge());
                    g2.draw(new Line2D.Double(x1, minY, x1, maxY));
                    this.minIcon.paintIcon(null, g2, (int) x1, (int) minY);
                    this.maxIcon.paintIcon(null, g2, (int) x1, (int) maxY);
                }
            }
            else {  // reset the min and max
                this.lastCategory = column;
                this.min = value.doubleValue();
                this.max = value.doubleValue();
            }
            // connect to the previous point
            if (this.plotLines) {
                if (column != 0) {
                    Number previousValue = dataset.getValue(row, column - 1);
                    if (previousValue != null) {
                        // previous data point...
                        double previous = previousValue.doubleValue();
                        double x0 = domainAxis.getCategoryMiddle(
                            column - 1, getColumnCount(), dataArea,
                            plot.getDomainAxisEdge());
                        double y0 = rangeAxis.valueToJava2D(
                            previous, dataArea, plot.getRangeAxisEdge());
                        g2.setPaint(getItemPaint(row, column));
                        g2.setStroke(getItemStroke(row, column));
                        Line2D line = new Line2D.Double(x0, y0, x1, y1);
                        g2.draw(line);
                    }
                }
            }

            // collect entity and tool tip information...
            if (state.getInfo() != null) {
                EntityCollection entities = state.getEntityCollection();
                if (entities != null && shape != null) {
                    String tip = null;
                    CategoryToolTipGenerator tipster 
                        = getToolTipGenerator(row, column);
                    if (tipster != null) {
                        tip = tipster.generateToolTip(dataset, row, column);
                    }
                    CategoryItemEntity entity = new CategoryItemEntity(
                        shape, tip, null, dataset, row, 
                        dataset.getColumnKey(column), column);
                    entities.add(entity);
                }
            }
        }
    }

    /**
     * Returns an icon.
     *
     * @param shape  the shape.
     * @param fillPaint  the fill paint.
     * @param outlinePaint  the outline paint.
     *
     * @return The icon.
     */
    private Icon getIcon(Shape shape, final Paint fillPaint, 
                        final Paint outlinePaint) {

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
     * @return The icon.
     */
    private Icon getIcon(Shape shape, final boolean fill, 
                         final boolean outline) {
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
    
    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.groupStroke, stream);
        SerialUtilities.writePaint(this.groupPaint, stream);
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
        this.groupStroke = SerialUtilities.readStroke(stream);
        this.groupPaint = SerialUtilities.readPaint(stream);
          
        this.minIcon = getIcon(
            new Arc2D.Double(-4, -4, 8, 8, 0, 360, Arc2D.OPEN), null, 
            Color.black);
        this.maxIcon = getIcon(
            new Arc2D.Double(-4, -4, 8, 8, 0, 360, Arc2D.OPEN), null, 
            Color.black);
        this.objectIcon = getIcon(new Line2D.Double(-4, 0, 4, 0), false, true);
    }
    
}
