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
 * --------------------
 * TickLabelEntity.java
 * --------------------
 * (C) Copyright 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TickLabelEntity.java,v 1.1 2007/10/10 19:34:54 vauchers Exp $
 *
 * Changes:
 * --------
 * 16-Mar-2004 : Version 1 (DG);
 *
 */

package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.Serializable;

/**
 * A chart entity representing a tick label.
 */
public class TickLabelEntity extends ChartEntity implements Cloneable, Serializable {
    
    /**
     * Creates a new entity.
     *
     * @param area  the area (<code>null</code> not permitted).
     * @param toolTipText  the tool tip text (<code>null</code> permitted).
     * @param urlText  the URL text for HTML image maps (<code>null</code> permitted).
     */
    public TickLabelEntity(Shape area, String toolTipText, String urlText) {
        super(area, toolTipText, urlText);    
    }
    
}
