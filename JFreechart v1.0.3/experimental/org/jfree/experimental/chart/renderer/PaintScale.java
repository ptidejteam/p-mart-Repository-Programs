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
 * ---------------
 * PaintScale.java
 * ---------------
 * (C) Copyright 2006, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: PaintScale.java,v 1.1 2007/10/10 20:30:37 vauchers Exp $
 *
 * Changes
 * -------
 * 05-Jul-2006 : Version 1 (DG);
 * 
 */

package org.jfree.experimental.chart.renderer;

import java.awt.Paint;

import org.jfree.experimental.chart.renderer.xy.XYBlockRenderer;

/**
 * A source for <code>Paint</code> instances, used by the 
 * {@link XYBlockRenderer}.  
 * <br><br>
 * NOTE: Classes that implement this interface should also implement 
 * <code>PublicCloneable</code> and <code>Serializable</code>, so 
 * that any renderer that references an instance of this interface can still be
 * cloned or serialized.
 * 
 * WARNING: THIS CLASS IS NOT PART OF THE STANDARD JFREECHART API AND IS 
 * SUBJECT TO ALTERATION OR REMOVAL.  DO NOT RELY ON THIS CLASS FOR 
 * PRODUCTION USE.  Please experiment with this code and provide feedback.
 */
public interface PaintScale {
    
    /**
     * Returns a <code>Paint</code> instance for the specified value.
     * 
     * @param value  the value.
     * 
     * @return A <code>Paint</code> instance (never <code>null</code>).
     */
    public Paint getPaint(double value);
    
}
