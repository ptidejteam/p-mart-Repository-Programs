/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
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
 * RingPlot.java
 * -------------
 * (C) Copyright 2004, 2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limtied);
 * Contributor(s):   -
 *
 * $Id: RingPlot.java,v 1.1 2007/10/10 20:11:19 vauchers Exp $
 *
 * Changes
 * -------
 * 08-Nov-2004 : Version 1 (DG);
 * 22-Feb-2005 : Renamed DonutPlot --> RingPlot (DG);
 * 06-Jun-2005 : Added default constructor and fixed equals() method to handle
 *               GradientPaint (DG);
 * 
 */

package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.data.general.PieDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.Rotation;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.UnitType;

/**
 * A customised pie plot that leaves a hole in the middle.
 */
public class RingPlot extends PiePlot implements Cloneable, Serializable {
    
    /** For serialization. */
    private static final long serialVersionUID = 1556064784129676620L;
    
    /** 
     * A flag that controls whether or not separators are drawn between the
     * sections of the chart.
     */
    private boolean separatorsVisible;
    
    /** The stroke used to draw separators. */
    private transient Stroke separatorStroke;
    
    /** The paint used to draw separators. */
    private transient Paint separatorPaint;
    
    /** 
     * The length of the inner separator extension (as a percentage of the
     * depth of the sections). 
     */
    private double innerSeparatorExtension;
    
    /** 
     * The length of the outer separator extension (as a percentage of the
     * depth of the sections). 
     */
    private double outerSeparatorExtension;
    
    /**
     * Creates a new plot with a <code>null</code> dataset.
     */
    public RingPlot() {
        this(null);   
    }
    
    /**
     * Creates a new plot for the specified dataset.
     * 
     * @param dataset  the dataset (<code>null</code> permitted).
     */
    public RingPlot(PieDataset dataset) {
        super(dataset);
        this.separatorsVisible = true;
        this.separatorStroke = new BasicStroke(0.5f);
        this.separatorPaint = Color.gray;
        this.innerSeparatorExtension = 0.20;  // twenty percent
        this.outerSeparatorExtension = 0.20;  // twenty percent
    }
    
    /**
     * Returns a flag that indicates whether or not separators are drawn between
     * the sections in the chart.
     * 
     * @return A boolean.
     */
    public boolean getSeparatorsVisible() {
        return this.separatorsVisible;
    }
    
    /**
     * Sets the flag that controls whether or not separators are drawn between 
     * the sections in the chart, and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     * 
     * @param visible  the flag.
     */
    public void setSeparatorsVisible(boolean visible) {
        this.separatorsVisible = visible;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the separator stroke.
     * 
     * @return The stroke (never <code>null</code>).
     */
    public Stroke getSeparatorStroke() {
        return this.separatorStroke;
    }
    
    /**
     * Sets the stroke used to draw the separator between sections.
     * 
     * @param stroke  the stroke (<code>null</code> not permitted).
     */
    public void setSeparatorStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.separatorStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the separator paint.
     * 
     * @return The paint (never <code>null</code>).
     */
    public Paint getSeparatorPaint() {
        return this.separatorPaint;
    }
    
    /**
     * Sets the paint used to draw the separator between sections.
     * 
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setSeparatorPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.separatorPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the length of the inner extension of the separator line that
     * is drawn between sections, expressed as a percentage of the depth of
     * the section.
     * 
     * @return The inner separator extension (as a percentage).
     */
    public double getInnerSeparatorExtension() {
        return this.innerSeparatorExtension;
    }
    
    /**
     * Sets the length of the inner extension of the separator line that is
     * drawn between sections, as a percentage of the depth of the 
     * sections, and sends a {@link PlotChangeEvent} to all registered 
     * listeners.
     * 
     * @param percent  the percentage.
     */
    public void setInnerSeparatorExtension(double percent) {
        this.innerSeparatorExtension = percent;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the length of the outer extension of the separator line that
     * is drawn between sections, expressed as a percentage of the depth of
     * the section.
     * 
     * @return The outer separator extension (as a percentage).
     */
    public double getOuterSeparatorExtension() {
        return this.outerSeparatorExtension;
    }
    
    /**
     * Sets the length of the outer extension of the separator line that is
     * drawn between sections, as a percentage of the depth of the 
     * sections, and sends a {@link PlotChangeEvent} to all registered 
     * listeners.
     * 
     * @param percent  the percentage.
     */
    public void setOuterSeparatorExtension(double percent) {
        this.outerSeparatorExtension = percent;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Draws a single data item.
     *
     * @param g2  the graphics device (<code>null</code> not permitted).
     * @param section  the section index.
     * @param dataArea  the data plot area.
     * @param state  state information for one chart.
     * @param currentPass  the current pass index.
     */
    protected void drawItem(Graphics2D g2,
                            int section,
                            Rectangle2D dataArea,
                            PiePlotState state,
                            int currentPass) {
    
        PieDataset dataset = getDataset();
        Number n = dataset.getValue(section);
        if (n == null) {
            return;   
        }
        double value = n.doubleValue();
        double angle1 = 0.0;
        double angle2 = 0.0;
        
        Rotation direction = getDirection();
        if (direction == Rotation.CLOCKWISE) {
            angle1 = state.getLatestAngle();
            angle2 = angle1 - value / state.getTotal() * 360.0;
        }
        else if (direction == Rotation.ANTICLOCKWISE) {
            angle1 = state.getLatestAngle();
            angle2 = angle1 + value / state.getTotal() * 360.0;         
        }
        else {
            throw new IllegalStateException("Rotation type not recognised.");   
        }
        
        double angle = (angle2 - angle1);
        if (Math.abs(angle) > getMinimumArcAngleToDraw()) {
            double ep = 0.0;
            double mep = getMaximumExplodePercent();
            if (mep > 0.0) {
                ep = getExplodePercent(section) / mep;                
            }
            Rectangle2D arcBounds = getArcBounds(
                state.getPieArea(), state.getExplodedPieArea(),
                angle1, angle, ep
            );            
            Arc2D.Double arc = new Arc2D.Double(
                arcBounds, angle1, angle, Arc2D.OPEN
            );

            // create the bounds for the inner arc
            RectangleInsets s = new RectangleInsets(
                UnitType.RELATIVE, 0.10, 0.10, 0.10, 0.10
            );
            Rectangle2D innerArcBounds = new Rectangle2D.Double();
            innerArcBounds.setRect(arcBounds);
            s.trim(innerArcBounds);
            // calculate inner arc in reverse direction, for later 
            // GeneralPath construction
            Arc2D.Double arc2 = new Arc2D.Double(
                innerArcBounds, angle1 + angle, -angle, Arc2D.OPEN
            );
            GeneralPath path = new GeneralPath();
            path.moveTo(
                (float) arc.getStartPoint().getX(), 
                (float) arc.getStartPoint().getY()
            );
            path.append(arc.getPathIterator(null), false);
            path.append(arc2.getPathIterator(null), true);
            path.closePath();
            
            Line2D separator = new Line2D.Double(
                arc2.getEndPoint(), arc.getStartPoint()
            );
            
            if (currentPass == 0) {
                Paint shadowPaint = getShadowPaint();
                double shadowXOffset = getShadowXOffset();
                double shadowYOffset = getShadowYOffset();
                if (shadowPaint != null) {
                    Shape shadowArc = ShapeUtilities.createTranslatedShape(
                        path, (float) shadowXOffset, (float) shadowYOffset
                    );
                    g2.setPaint(shadowPaint);
                    g2.fill(shadowArc);
                }
            }
            else if (currentPass == 1) {

                Paint paint = getSectionPaint(section);
                g2.setPaint(paint);
                g2.fill(path);
                Paint outlinePaint = getSectionOutlinePaint(section);
                Stroke outlineStroke = getSectionOutlineStroke(section);
                if (outlinePaint != null && outlineStroke != null) {
                    g2.setPaint(outlinePaint);
                    g2.setStroke(outlineStroke);
                    g2.draw(path);
                }
                
                if (this.separatorsVisible) {
                    Line2D extendedSeparator = extendLine(
                        separator, this.innerSeparatorExtension, 
                        this.innerSeparatorExtension
                    );  
                    g2.setStroke(this.separatorStroke);
                    g2.setPaint(this.separatorPaint);
                    g2.draw(extendedSeparator);
                }
                
                // add an entity for the pie section
                if (state.getInfo() != null) {
                    EntityCollection entities = state.getEntityCollection();
                    if (entities != null) {
                        Comparable key = dataset.getKey(section);
                        String tip = null;
                        PieToolTipGenerator toolTipGenerator 
                            = getToolTipGenerator();
                        if (toolTipGenerator != null) {
                            tip = toolTipGenerator.generateToolTip(
                                dataset, key
                            );
                        }
                        String url = null;
                        PieURLGenerator urlGenerator = getURLGenerator();
                        if (urlGenerator != null) {
                            url = urlGenerator.generateURL(
                                dataset, key, getPieIndex()
                            );
                        }
                        PieSectionEntity entity = new PieSectionEntity(
                            arc, dataset, getPieIndex(), section, key, tip, url
                        );
                        entities.add(entity);
                    }
                }
            }
        }    
        state.setLatestAngle(angle2);
    }

    /**
     * Tests this plot for equality with an arbitrary object.
     * 
     * @param obj  the object to test against (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RingPlot)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        RingPlot that = (RingPlot) obj;
        if (this.separatorsVisible != that.separatorsVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.separatorStroke, that.separatorStroke
        )) {
            return false;
        }
        if (!PaintUtilities.equal(this.separatorPaint, that.separatorPaint)) {
            return false;
        }
        if (this.innerSeparatorExtension != that.innerSeparatorExtension) {
            return false;
        }
        if (this.outerSeparatorExtension != that.outerSeparatorExtension) {
            return false;
        }        
        return true;
    }
    
    /**
     * Creates a new line by extending an existing line.
     * 
     * @param line  the line (<code>null</code> not permitted).
     * @param startPercent  the amount to extend the line at the start point 
     *                      end.
     * @param endPercent  the amount to extend the line at the end point end.
     * 
     * @return A new line.
     */
    private Line2D extendLine(Line2D line, double startPercent, 
                              double endPercent) {
        if (line == null) {
            throw new IllegalArgumentException("Null 'line' argument.");
        }
        double x1 = line.getX1();
        double x2 = line.getX2();
        double deltaX = x2 - x1;
        double y1 = line.getY1();
        double y2 = line.getY2();
        double deltaY = y2 - y1;
        x1 = x1 - (startPercent * deltaX);
        y1 = y1 - (startPercent * deltaY);
        x2 = x2 + (endPercent * deltaX);
        y2 = y2 + (endPercent * deltaY);
        return new Line2D.Double(x1, y1, x2, y2);
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
        SerialUtilities.writeStroke(this.separatorStroke, stream);
        SerialUtilities.writePaint(this.separatorPaint, stream);
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
        this.separatorStroke = SerialUtilities.readStroke(stream);
        this.separatorPaint = SerialUtilities.readPaint(stream);
    }
    
}
