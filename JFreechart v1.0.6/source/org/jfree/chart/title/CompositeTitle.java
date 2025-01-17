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
 * -------------------
 * CompositeTitle.java
 * -------------------
 * (C) Copyright 2005, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CompositeTitle.java,v 1.1 2007/10/10 20:53:48 vauchers Exp $
 *
 * Changes
 * -------
 * 19-Nov-2004 : Version 1 (DG);
 * 11-Jan-2005 : Removed deprecated code in preparation for 1.0.0 release (DG);
 * 04-Feb-2005 : Implemented MAXIMUM_WIDTH in calculateSize (DG);
 * 20-Apr-2005 : Added new draw() method (DG);
 * 03-May-2005 : Implemented equals() method (DG);
 *
 */

package org.jfree.chart.title;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.ui.Size2D;

/**
 * A title that contains multiple titles within a {@link BlockContainer}.
 */
public class CompositeTitle extends Title implements Cloneable, Serializable {
    
    /** For serialization. */
    private static final long serialVersionUID = -6770854036232562290L;
    
    /** A container for the individual titles. */
    private BlockContainer container;
    
    /**
     * Creates a new composite title with a default border arrangement.
     */
    public CompositeTitle() {
        this(new BlockContainer(new BorderArrangement()));   
    }
    
    /**
     * Creates a new title using the specified container. 
     * 
     * @param container  the container (<code>null</code> not permitted).
     */
    public CompositeTitle(BlockContainer container) {
        if (container == null) {
            throw new IllegalArgumentException("Null 'container' argument.");
        }
        this.container = container;
    }
    
    /**
     * Returns the container holding the titles.
     * 
     * @return The title container (never <code>null</code>).
     */
    public BlockContainer getContainer() {
        return this.container;
    }
    
    /**
     * Sets the title container.
     * 
     * @param container  the container (<code>null</code> not permitted).
     */
    public void setTitleContainer(BlockContainer container) {
        if (container == null) {
            throw new IllegalArgumentException("Null 'container' argument.");
        }
        this.container = container;    
    }
    
    /**
     * Arranges the contents of the block, within the given constraints, and 
     * returns the block size.
     * 
     * @param g2  the graphics device.
     * @param constraint  the constraint (<code>null</code> not permitted).
     * 
     * @return The block size (in Java2D units, never <code>null</code>).
     */
    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        RectangleConstraint contentConstraint = toContentConstraint(constraint);
        Size2D contentSize = this.container.arrange(g2, contentConstraint);
        return new Size2D(
            calculateTotalWidth(contentSize.getWidth()), 
            calculateTotalHeight(contentSize.getHeight())
        );
    }
    
    /**
     * Draws the title on a Java 2D graphics device (such as the screen or a 
     * printer).
     *
     * @param g2  the graphics device.
     * @param area  the area allocated for the title.
     */
    public void draw(Graphics2D g2, Rectangle2D area) {
        area = trimMargin(area);
        drawBorder(g2, area);
        area = trimBorder(area);
        area = trimPadding(area);
        this.container.draw(g2, area);
    }
    
    /**
     * Draws the block within the specified area.
     * 
     * @param g2  the graphics device.
     * @param area  the area.
     * @param params  ignored (<code>null</code> permitted).
     * 
     * @return Always <code>null</code>.
     */
    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        draw(g2, area);
        return null;
    }
    
    /**
     * Tests this title for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;   
        }
        if (!(obj instanceof CompositeTitle)) {
            return false;   
        }
        if (!super.equals(obj)) {
            return false;   
        }
        CompositeTitle that = (CompositeTitle) obj;
        if (!this.container.equals(that.container)) {
            return false;   
        }
        return true;
    }

}
