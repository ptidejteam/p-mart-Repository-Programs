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
 * ----------------------
 * ItemLabelPosition.java
 * ----------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: ItemLabelPosition.java,v 1.1 2007/10/10 19:19:08 vauchers Exp $
 *
 * Changes
 * -------
 * 27-Oct-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.renderer;

import java.io.Serializable;

import org.jfree.ui.TextAnchor;

/**
 * The attributes that control the position of the label for each data item on a chart.
 * 
 * @author David Gilbert
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
     * Creates a new position record.  The item label anchor is a point relative to the
     * data item (dot, bar or other visual item) on a chart.  The item label is aligned
     * by aligning the text anchor with the item label anchor.
     * 
     * @param itemLabelAnchor  the item label anchor.
     * @param textAnchor  the text anchor.
     * @param rotationAnchor  the rotation anchor.
     * @param angle  the rotation angle.
     */
    public ItemLabelPosition(ItemLabelAnchor itemLabelAnchor, 
                             TextAnchor textAnchor,
                             TextAnchor rotationAnchor,
                             double angle) {
                                 
        this.itemLabelAnchor = itemLabelAnchor;
        this.textAnchor = textAnchor;
        this.rotationAnchor = rotationAnchor;
        this.angle = angle;
    
    }
    
    /**
     * Returns the item label anchor.
     * 
     * @return The item label anchor.
     */
    public ItemLabelAnchor getItemLabelAnchor() {
        return this.itemLabelAnchor;
    }
    
    /**
     * Returns the text anchor.
     * 
     * @return The text anchor.
     */
    public TextAnchor getTextAnchor() {
        return this.textAnchor;
    }
    
    /**
     * Returns the rotation anchor point.
     * 
     * @return The rotation anchor point.
     */
    public TextAnchor getRotationAnchor() {
        return this.rotationAnchor;
    }
    
    /**
     * Returns the angle of rotation for the label.
     * 
     * @return The angle.
     */
    public double getAngle() {
        return this.angle;
    }
    
    /**
     * Tests an object for equality with this instance.
     * 
     * @param object  the object.
     * 
     * @return A boolean.
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
