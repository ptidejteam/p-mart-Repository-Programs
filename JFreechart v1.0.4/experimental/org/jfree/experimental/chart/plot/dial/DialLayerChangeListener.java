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
 * ----------------------------
 * DialLayerChangeListener.java
 * ----------------------------
 * (C) Copyright 2006, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DialLayerChangeListener.java,v 1.1 2007/10/10 20:46:45 vauchers Exp $
 *
 * Changes
 * -------
 * 06-Nov-2006 : Version 1 (DG);
 * 
 */

package org.jfree.experimental.chart.plot.dial;

import java.util.EventListener;

/**
 * The interface via which an object is notified of changes to a 
 * {@link DialLayer}.  The {@link DialPlot} class listens for changes to its
 * layers in this way.
 */
public interface DialLayerChangeListener extends EventListener {
    
    /**
     * A call-back method for receiving notification of a change to a 
     * {@link DialLayer}.
     * 
     * @param event  the event.
     */
    public void dialLayerChanged(DialLayerChangeEvent event);

}
