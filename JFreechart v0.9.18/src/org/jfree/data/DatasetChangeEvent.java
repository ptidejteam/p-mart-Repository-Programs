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
 * -----------------------
 * DatasetChangeEvent.java
 * -----------------------
 * (C) Copyright 2000-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DatasetChangeEvent.java,v 1.1 2007/10/10 19:39:25 vauchers Exp $
 *
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header. Fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Move to new package (com.jrefinery.data.*) (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 11-Jun-2002 : Separated the event source from the dataset to cover the case where the dataset
 *               is changed to null in the Plot class.  Updated Javadocs (DG);
 * 04-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.data;

/**
 * A change event that encapsulates information about a change to a dataset.
 *
 * @author David Gilbert
 */
public class DatasetChangeEvent extends java.util.EventObject {

    /**
     * The dataset that generated the change event.
     */
    private Dataset data;

    /**
     * Constructs a new event.
     * <P>
     * The source is either the dataset or the Plot class.  The dataset can be
     * null (in this case the source will be the Plot class).
     *
     * @param source  the source of the event.
     * @param data  the dataset that generated the event.
     */
    public DatasetChangeEvent(Object source, Dataset data) {
        super(source);
        this.data = data;
    }

    /**
     * Returns the dataset that generated the event.
     *
     * @return the dataset.
     */
    public Dataset getDataset() {
        return this.data;
    }

}
