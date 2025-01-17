/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ----------------------
 * ItemLabelPosition.java
 * ----------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: ItemLabelPosition.java,v 1.1 2007/10/10 19:50:22 vauchers Exp $
 *
 * Changes
 * -------
 * 27-Oct-2003 : Version 1 (DG);
 * 19-Feb-2004 : Moved to org.jfree.chart.labels, updated Javadocs and argument checking (DG);
 * 26-Feb-2004 : Added new constructor (DG);
 *
 */

package org.jfree.chart.labels;

import java.io.Serializable;

import org.jfree.ui.TextAnchor;

/**
 * The attributes that control the position of the label for each data item on a chart.  Instances
 * of this class are immutable.
 */
public class ItemLabelPosition implements Serializable {

    /** The item label anchor point. */
    private ItemLabelAnchor itemLabelAnchor;
    
    /** The text anchor. */
    private TextAnchor textAnchor;
    
    /** The rotation anchor. */
    private TextAnchor rotationAnchor;

    /** The rotation angle. */    
    private double angle;
    
    /**
     * Creates a new position record with default settings.
     */
    public ItemLabelPosition() {
        this(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER, TextAnchor.CENTER, 0.0);
    }
    
    /**
     * Creates a new position record (with zero rotation).
     * 
     * @param itemLabelAnchor  the item label anchor (<code>null</code> not permitted).
     * @param textAnchor  the text anchor (<code>null</code> not permitted).
     */
    public ItemLabelPosition(ItemLabelAnchor itemLabelAnchor, TextAnchor textAnchor) {
        this(itemLabelAnchor, textAnchor, TextAnchor.CENTER, 0.0);    
    }
    
    /**
     * Creates a new position record.  The item label anchor is a point relative to the
     * data item (dot, bar or other visual item) on a chart.  The item label is aligned
     * by aligning the text anchor with the item label anchor.
     * 
     * @param itemLabelAnchor  the item label anchor (<code>null</code> not permitted).
     * @param textAnchor  the text anchor (<code>null</code> not permitted).
     * @param rotationAnchor  the rotation anchor (<code>null</code> not permitted).
     * @param angle  the rotation angle (in radians).
     */
    public ItemLabelPosition(ItemLabelAnchor itemLabelAnchor, 
                             TextAnchor textAnchor,
                             TextAnchor rotationAnchor,
                             double angle) {
              
        if (itemLabelAnchor == null) {
            throw new IllegalArgumentException("Null 'itemLabelAnchor' argument.");
        }
        if (textAnchor == null) {
            throw new IllegalArgumentException("Null 'textAnchor' argument.");
        }
        if (rotationAnchor == null) {
            throw new IllegalArgumentException("Null 'rotationAnchor' argument.");
        }
        
        this.itemLabelAnchor = itemLabelAnchor;
        this.textAnchor = textAnchor;
        this.rotationAnchor = rotationAnchor;
        this.angle = angle;
    
    }
    
    /**
     * Returns the item label anchor.
     * 
     * @return the item label anchor (never <code>null</code>).
     */
    public ItemLabelAnchor getItemLabelAnchor() {
        return this.itemLabelAnchor;
    }
    
    /**
     * Returns the text anchor.
     * 
     * @return the text anchor (never <code>null</code>).
     */
    public TextAnchor getTextAnchor() {
        return this.textAnchor;
    }
    
    /**
     * Returns the rotation anchor point.
     * 
     * @return the rotation anchor point (never <code>null</code>).
     */
    public TextAnchor getRotationAnchor() {
        return this.rotationAnchor;
    }
    
    /**
     * Returns the angle of rotation for the label.
     * 
     * @return the angle (in radians).
     */
    public double getAngle() {
        return this.angle;
    }
    
    /**
     * Tests this object for equality with an arbitrary object.
     * 
     * @param object  the object (<code>null</code> permitted).
     * 
     * @return a boolean.
     */
    public boolean equals(Object object) {
        
        if (object == null) {
            return false;
        }
        
        if (object == this) {
            return true;
        }
        
        if (object instanceof ItemLabelPosition) {
            ItemLabelPosition p = (ItemLabelPosition) object;
            boolean b0 = (this.itemLabelAnchor.equals(p.itemLabelAnchor));              
            boolean b1 = (this.textAnchor.equals(p.textAnchor));
            boolean b2 = (this.rotationAnchor.equals(p.rotationAnchor));
            boolean b3 = (this.angle == p.angle);
            return b0 && b1 && b2 && b3;
        }
        
        return false;
        
    }

}
