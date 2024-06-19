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
 * --------------------
 * DrawingSupplier.java
 * --------------------
 * (C) Copyright 2003, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DrawingSupplier.java,v 1.1 2007/10/10 19:57:50 vauchers Exp $
 *
 * Changes
 * -------
 * 16-Jan-2003 : Version 1 (DG);
 * 17-Jan-2003 : Renamed PaintSupplier --> DrawingSupplier (DG);
 * 
 */

package com.jrefinery.chart.renderer;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

/**
 * A supplier of <code>Paint</code> and <code>Stroke</code> objects.
 * 
 * @author David Gilbert
 */
public interface DrawingSupplier {

    /**
     * Returns the next paint in a sequence maintained by the supplier. 
     * 
     * @return The paint.
     */
    public Paint getNextPaint();

    /**
     * Returns the next outline paint in a sequence maintained by the supplier.
     * 
     * @return The paint.
     */    
    public Paint getNextOutlinePaint();

    /**
     * Returns the next <code>Stroke</code> object in a sequence maintained by the supplier.
     * 
     * @return The stroke.
     */
    public Stroke getNextStroke();
        
    /**
     * Returns the next <code>Stroke</code> object in a sequence maintained by the supplier.
     * 
     * @return The stroke.
     */
    public Stroke getNextOutlineStroke();

    /**
     * Returns the next <code>Shape</code> object in a sequence maintained by the supplier.
     * 
     * @return The shape.
     */
    public Shape getNextShape();
        
}
