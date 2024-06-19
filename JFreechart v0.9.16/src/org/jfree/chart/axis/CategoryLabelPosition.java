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
 * --------------------------
 * CategoryLabelPosition.java
 * --------------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CategoryLabelPosition.java,v 1.1 2007/10/10 19:25:36 vauchers Exp $
 *
 * Changes
 * -------
 * 31-Oct-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.axis;

import java.io.Serializable;

import org.jfree.text.TextBlockAnchor;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

/**
 * The attributes that control the position of the labels for the categories on a 
 * {@link CategoryAxis}.
 * <P>
 * Instances of this class are immutable and other JFreeChart classes rely upon this.
 * 
 * @author David Gilbert
 */
public class CategoryLabelPosition implements Serializable {

    /** The category anchor point. */
    private RectangleAnchor categoryAnchor;
    
    /** The text block anchor. */
    private TextBlockAnchor labelAnchor;
    
    /** The rotation anchor. */
    private TextAnchor rotationAnchor;

    /** The rotation angle. */    
    private double angle;
    
    /**
     * Creates a new position record with default settings.
     */
    public CategoryLabelPosition() {
        this(RectangleAnchor.CENTER, TextBlockAnchor.BOTTOM_CENTER, TextAnchor.CENTER, 0.0);
    }
    
    /**
     * Creates a new position record.  The item label anchor is a point relative to the
     * data item (dot, bar or other visual item) on a chart.  The item label is aligned
     * by aligning the text anchor with the item label anchor.
     * 
     * @param categoryAnchor  the category anchor.
     * @param labelAnchor  the label anchor.
     * @param rotationAnchor  the rotation anchor.
     * @param angle  the rotation angle.
     */
    public CategoryLabelPosition(RectangleAnchor categoryAnchor, 
                                 TextBlockAnchor labelAnchor,
                                 TextAnchor rotationAnchor,
                                 double angle) {
                                 
        this.categoryAnchor = categoryAnchor;
        this.labelAnchor = labelAnchor;
        this.rotationAnchor = rotationAnchor;
        this.angle = angle;
    
    }
    
    /**
     * Returns the item label anchor.
     * 
     * @return The item label anchor.
     */
    public RectangleAnchor getCategoryAnchor() {
        return this.categoryAnchor;
    }
    
    /**
     * Returns the text block anchor.
     * 
     * @return The text block anchor.
     */
    public TextBlockAnchor getLabelAnchor() {
        return this.labelAnchor;
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
        
        if (object instanceof CategoryLabelPosition) {

            CategoryLabelPosition p = (CategoryLabelPosition) object;
            boolean b0 = (this.categoryAnchor.equals(p.categoryAnchor));              
            boolean b1 = (this.labelAnchor.equals(p.labelAnchor));
            boolean b2 = (this.rotationAnchor.equals(p.rotationAnchor));
            boolean b3 = (this.angle == p.angle);
            return b0 && b1 && b2 && b3;
        }
        
        return false;
        
    }

}
