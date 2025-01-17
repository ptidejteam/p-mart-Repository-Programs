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
 * ---------------
 * NumberTick.java
 * ---------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: NumberTick.java,v 1.1 2007/10/10 19:46:24 vauchers Exp $
 *
 * Changes
 * -------
 * 07-Nov-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.axis;

import org.jfree.ui.TextAnchor;

/**
 * A numerical tick.
 */
public class NumberTick extends ValueTick {
    
    /** The number. */
    private Number number;
    
    /**
     * Creates a new tick.
     * 
     * @param number  the number.
     * @param label  the label.
     * @param textAnchor  the part of the label that is aligned with the anchor point.
     * @param rotationAnchor  defines the rotation point relative to the text.
     * @param angle  the rotation angle (in radians).
     */
    public NumberTick(Number number, String label,
                      TextAnchor textAnchor, TextAnchor rotationAnchor, double angle) {
                        
        super(number.doubleValue(), label, textAnchor, rotationAnchor, angle);
        this.number = number;
            
    }

    /**
     * Returns the number.
     * 
     * @return the number.
     */
    public Number getNumber() {
        return this.number;    
    }
    
}
