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
 * CategoryDataset.java
 * --------------------
 * (C) Copyright 2000-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CategoryDataset.java,v 1.1 2007/10/10 19:29:13 vauchers Exp $
 *
 * Changes (from 21-Aug-2001)
 * --------------------------
 * 21-Aug-2001 : Added standard header. Fixed DOS encoding problem (DG);
 * 18-Sep-2001 : Updated e-mail address in header (DG);
 * 15-Oct-2001 : Moved to new package (com.jrefinery.data.*) (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 17-Nov-2001 : Updated Javadoc comments (DG);
 * 04-Mar-2002 : Updated import statement (DG);
 * 23-Oct-2002 : Reorganised code (DG);
 * 10-Jan-2003 : Updated Javadocs (DG);
 * 21-Jan-2003 : Merged with TableDataset (which only existed in CVS) (DG);
 * 13-Mar-2003 : Added KeyedValues2DDataset interface (DG);
 * 23-Apr-2003 : Switched CategoryDataset and KeyedValues2DDataset so that CategoryDataset is
 *               the super interface (DG);
 *
 */

package org.jfree.data;

/**
 * The interface for a dataset with one or more series, and values associated with categories.
 * <P>
 * The categories are represented by <code>Comparable</code> instance, with the category label
 * being provided by the <code>toString</code> method.
 *
 * @author David Gilbert
 */
public interface CategoryDataset extends KeyedValues2D, Dataset {

    // no additional methods required

}
