/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2006, by Object Refinery Limited and Contributors.
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
 * ---------------------
 * LookupPaintScale.java
 * ---------------------
 * (C) Copyright 2006, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: LookupPaintScale.java,v 1.1 2007/10/10 20:23:02 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Jul-2006 : Version 1 (DG);
 * 
 */

package org.jfree.experimental.chart.renderer;

import java.awt.Color;
import java.awt.Paint;
import java.io.Serializable;
import java.util.List;

import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

/**
 * A paint scale that uses a lookup table to associate paint instances
 * with data value ranges.
 * 
 * WARNING: THIS CLASS IS NOT PART OF THE STANDARD JFREECHART API AND IS 
 * SUBJECT TO ALTERATION OR REMOVAL.  DO NOT RELY ON THIS CLASS FOR 
 * PRODUCTION USE.  Please experiment with this code and provide feedback.
 */
public class LookupPaintScale 
        implements PaintScale, PublicCloneable, Serializable {

    /**
     * Stores the paint for a value.
     */
    class PaintItem {
        
        Number value;
        
        Paint paint;
        
        /**
         * Creates a new instance.
         * 
         * @param value  the value.
         * @param paint  the paint.
         */
        public PaintItem(Number value, Paint paint) {
            this.value = value;
            this.paint = paint;
        }
    }
    
    private Paint defaultPaint; 
    
    private List lookupTable;
    
    /**
     * Creates a new paint scale.
     */
    public LookupPaintScale() {
        this.defaultPaint = Color.lightGray;    
        this.lookupTable = new java.util.ArrayList();
    }
    
    /**
     * Adds an entry to the lookup table.
     * 
     * @param n  the data value.
     * @param p  the paint.
     */
    public void add(Number n, Paint p) {
        this.lookupTable.add(new PaintItem(n, p));
    }
    
    /**
     * Returns the paint associated with the specified value.
     * 
     * @param value  the value.
     * 
     * @return The paint.
     */
    public Paint getPaint(double value) {
        Paint result = defaultPaint;
        int index = this.lookupTable.size();
        boolean done = false;
        while (index > 0 && !done) {
            PaintItem item = (PaintItem) lookupTable.get(--index);
            if (value >= item.value.doubleValue()) {
                result = item.paint;
                done = true;
            }
        }
        return result;
    }
    
    /**
     * Tests this instance for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LookupPaintScale)) {
            return false;
        }
        LookupPaintScale that = (LookupPaintScale) obj;
        if (!PaintUtilities.equal(this.defaultPaint, that.defaultPaint)) {
            return false;
        }
        return true;
    }
    
    /**
     * Returns a clone of the instance.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if there is a problem cloning the
     *     instance.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
