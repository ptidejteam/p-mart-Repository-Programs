/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
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
 * AbstractDataset.java
 * --------------------
 * (C)opyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: AbstractDataset.java,v 1.1 2007/10/10 19:52:14 vauchers Exp $
 *
 * Changes (from 21-Aug-2001)
 * --------------------------
 * 21-Aug-2001 : Added standard header. Fixed DOS encoding problem (DG);
 * 18-Sep-2001 : Updated e-mail address in header (DG);
 * 15-Oct-2001 : Moved to new package (com.jrefinery.data.*) (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 17-Nov-2001 : Changed constructor from public to protected, created new AbstractSeriesDataset
 *               class and transferred series-related methods, updated Javadoc comments (DG);
 * 04-Mar-2002 : Updated import statements (DG);
 * 11-Jun-2002 : Updated for change in the event constructor (DG);
 * 07-Aug-2002 : Changed listener list to use javax.swing.event.EventListenerList (DG);
 *
 */

package com.jrefinery.data;

import javax.swing.event.EventListenerList;

/**
 * An abstract implementation of the Dataset interface, containing a mechanism
 * for registering change listeners.
 */
public abstract class AbstractDataset implements Dataset {

    /** Storage for registered change listeners. */
    protected EventListenerList listenerList;

    /**
     * Constructs a dataset.
     */
    protected AbstractDataset() {
        this.listenerList = new EventListenerList();
    }

    /**
     * Registers an object to receive notification of changes to the dataset.
     *
     * @param listener The object to register.
     */
    public void addChangeListener(DatasetChangeListener listener) {
        listenerList.add(DatasetChangeListener.class, listener);
    }

    /**
     * Deregisters an object so that it no longer receives notification of changes to the dataset.
     *
     * @param listener The object to deregister.
     */
    public void removeChangeListener(DatasetChangeListener listener) {
        listenerList.remove(DatasetChangeListener.class, listener);
    }

    /**
     * Notifies all registered listeners that the dataset has changed.
     */
    protected void fireDatasetChanged() {
        notifyListeners(new DatasetChangeEvent(this, // source
                                               this  // dataset
                                               ));
    }

    /**
     * Notifies all registered listeners that the dataset has changed.
     *
     * @param event Contains information about the event that triggered the notification.
     */
    protected void notifyListeners(DatasetChangeEvent event) {

        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==DatasetChangeListener.class) {
                ((DatasetChangeListener)listeners[i+1]).datasetChanged(event);
            }
        }

    }

}






