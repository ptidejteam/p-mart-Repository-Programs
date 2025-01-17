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
 * AbstractDataset.java
 * --------------------
 * (C)opyright 2000-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Nicolas Brodu (for Astrium and EADS Corporate Research Center);
 *
 * $Id: AbstractDataset.java,v 1.1 2007/10/10 19:34:39 vauchers Exp $
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
 * 04-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 27-Mar-2003 : Implemented Serializable (DG);
 * 18-Aug-2003 : Implemented Cloneable (DG);
 * 08-Sep-2003 : Serialization fixes (NB);
 * 11-Sep-2003 : Cloning Fixes (NB);
 */

package org.jfree.data;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.event.EventListenerList;

/**
 * An abstract implementation of the {@link Dataset} interface, containing a mechanism
 * for registering change listeners.
 *
 */
public abstract class AbstractDataset implements Dataset, 
                                                 Cloneable, 
                                                 Serializable,
                                                 ObjectInputValidation {

    /** The group that the dataset belongs to. */
    private DatasetGroup group;

    /** Storage for registered change listeners. */
    private transient EventListenerList listenerList;

    /**
     * Constructs a dataset.
     * <P>
     * By default, the dataset is assigned to its own group.
     */
    protected AbstractDataset() {
        this.group = new DatasetGroup();
        this.listenerList = new EventListenerList();
    }

    /**
     * Returns the dataset group for the dataset.
     *
     * @return the dataset group.
     */
    public DatasetGroup getGroup() {
        return this.group;
    }

    /**
     * Sets the dataset group for the dataset.
     *
     * @param group  the dataset group.
     */
    public void setGroup(final DatasetGroup group) {
        this.group = group;
    }

    /**
     * Registers an object to receive notification of changes to the dataset.
     *
     * @param listener the object to register.
     */
    public void addChangeListener(final DatasetChangeListener listener) {
        this.listenerList.add(DatasetChangeListener.class, listener);
    }

    /**
     * Deregisters an object so that it no longer receives notification of changes to the dataset.
     *
     * @param listener the object to deregister.
     */
    public void removeChangeListener(final DatasetChangeListener listener) {
        this.listenerList.remove(DatasetChangeListener.class, listener);
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
     * @param event  contains information about the event that triggered the notification.
     */
    protected void notifyListeners(final DatasetChangeEvent event) {

        final Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == DatasetChangeListener.class) {
                ((DatasetChangeListener) listeners[i + 1]).datasetChanged(event);
            }
        }

    }

    /**
     * Returns a clone of the dataset. 
     * <p>
     * The cloned dataset will NOT include the {@link DatasetChangeListener} references that have
     * been registered with this dataset.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException  if the dataset does not support cloning.
     */
    public Object clone() throws CloneNotSupportedException {
        final AbstractDataset clone = (AbstractDataset) super.clone();
        clone.listenerList = new EventListenerList();
        return clone;    
    }
    
    /**
     * Handles serialization.
     *
     * @param stream  the output stream.
     *
     * @throws IOException if there is an I/O problem.
     */
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * Restores a serialized object.
     *
     * @param stream  the input stream.
     *
     * @throws IOException if there is an I/O problem.
     * @throws ClassNotFoundException if there is a problem loading a class.
     */
    private void readObject(final ObjectInputStream stream) 
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.listenerList = new EventListenerList();
        stream.registerValidation(this, 10);  // see comments about priority of 10 in 
                                              // validateObject() 
    }
 
    /**
     * Validates the object. We use this opportunity to call listeners who have registered during 
     * the deserialization process, as listeners are not serialized. This method is called by the 
     * serialization system after the entire graph is read.
     *  
     * This object has registered itself to the system with a priority of 10. Other callbacks may 
     * register with a higher priority number to be called before this object, or with a lower 
     * priority number to be called after the listeners were notified.
     * 
     * All listeners are supposed to have register by now, either in their readObject or 
     * validateObject methods. Notify them that this dataset has changed.  
     *
     * @exception InvalidObjectException If the object cannot validate itself.
     */
    public void validateObject() throws InvalidObjectException {
       fireDatasetChanged();
    }
   
}






