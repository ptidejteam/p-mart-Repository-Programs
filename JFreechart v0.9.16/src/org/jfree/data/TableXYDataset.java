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
 * -------------------
 * TableXYDataset.java
 * -------------------
 * (C) Copyright 2000-2004, by Richard Atkinson and contributors.
 *
 * Original Author:  Richard Atkinson;
 * Contributor(s):   -;
 *
 * $Id: TableXYDataset.java,v 1.1 2007/10/10 19:25:30 vauchers Exp $
 *
 * Changes
 * -------
 * 22-Sep-2003 : Changed to be an interface.  Previous functionality moved to DefaultTableXYDataset;
 *
 */
package org.jfree.data;

/**
 * An Interface to represent XY data in a table format so that each series is
 * guaranteed to have the same number of items.  This is used primarily by
 * the StackedXYAreaRenderer.  The functionality that used to be implemented
 * here is now in {@link DefaultTableXYDataset}.
 */
public interface TableXYDataset extends XYDataset {

    /**
     * Returns the number of items every series.
     *
     * @return the number of items within the series.
     */
    public int getItemCount();

}
