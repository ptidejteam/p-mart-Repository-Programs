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
 * -----------
 * Series.java
 * -----------
 * (C) Copyright 2001-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: Series.java,v 1.1 2007/10/10 19:29:13 vauchers Exp $
 *
 * Changes
 * -------
 * 15-Nov-2001 : Version 1 (DG);
 * 29-Nov-2001 : Added cloning and property change support (DG);
 * 30-Jan-2002 : Added a description attribute and changed the constructors to protected (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 13-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Added equals(...) method (DG);
 * 26-Jun-2003 : Changed listener list to use EventListenerList - see bug 757027 (DG);
 * 15-Oct-2003 : Added a flag to control whether or not change events are sent to registered 
 *               listeners (DG);
 *
 */

package org.jfree.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import javax.swing.event.EventListenerList;

import org.jfree.util.ObjectUtils;

/**
 * Base class representing a data series.  Subclasses are left to implement the
 * actual data structures.
 * <P>
 * The series has two properties ("Name" and "Description") for which you can
 * register a {@link PropertyChangeListener}.
 * <P>
 * You can also register a {@link SeriesChangeListener} to receive notification of
 * changes to the series data.
 *
 * @author David Gilbert
 */
public class Series implements Cloneable, Serializable {

    /** The name of the series. */
    private String name;

    /** A description of the series. */
    private String description;

    /** Storage for registered change listeners. */
    private EventListenerList listeners;

    /** Object to support property change notification. */
    private PropertyChangeSupport propertyChangeSupport;

    /** A flag that controls whether or not changes are notified. */
    private boolean notify;

    /**
     * Creates a new series.
     *
     * @param name  the series name (<code>null</code> not permitted).
     */
    protected Series(String name) {
        this(name, null);
    }

    /**
     * Constructs a series.
     *
     * @param name  the series name (<code>null</code> NOT permitted).
     * @param description  the series description (<code>null</code> permitted).
     */
    protected Series(String name, String description) {
        if (name == null) {
            throw new IllegalArgumentException("Null 'name' argument.");
        }
        this.name = name;
        this.description = description;
        this.listeners = new EventListenerList();
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.notify = true;
        
    }

    /**
     * Returns the name of the series.
     *
     * @return the series name (never <code>null</code>).
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the series.
     *
     * @param name  the name (<code>null</code> not permitted).
     */
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null 'name' argument.");
        }
        String old = this.name;
        this.name = name;
        this.propertyChangeSupport.firePropertyChange("Name", old, name);

    }

    /**
     * Returns a description of the series.
     *
     * @return the series description (possibly <code>null</code>).
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of the series.
     *
     * @param description  the description (<code>null</code> permitted).
     */
    public void setDescription(String description) {
        String old = this.description;
        this.description = description;
        this.propertyChangeSupport.firePropertyChange("Description", old, description);
    }

    /**
     * Returns the flag that controls whether or not change events are sent to registered
     * listeners.
     * 
     * @return a boolean.
     */
    public boolean getNotify() {
        return this.notify;
    }
    
    /**
     * Sets the flag that controls whether or not change events are sent to registered 
     * listeners.
     * 
     * @param notify  the new value of the flag.
     */
    public void setNotify(boolean notify) {
        if (this.notify != notify) {
            this.notify = notify;
            fireSeriesChanged();
        }
    }
    
    /**
     * Returns a clone of the series.
     * <P>
     * Notes:
     * 1.  No need to clone the name or description, since String object is immutable.
     * 2.  We set the listener list to empty, since the listeners did not register with the clone.
     * 3.  Same applies to the PropertyChangeSupport instance.
     *
     * @return a clone of the series.
     * 
     * @throws CloneNotSupportedException  not thrown by this class, but subclasses may differ.
     */
    public Object clone() throws CloneNotSupportedException {

        Object obj = null;

        try {
            obj = super.clone();
        }
        catch (CloneNotSupportedException e) { // won't get here...
            System.err.println("Series.clone(): unexpected exception.");
        }

        Series clone = (Series) obj;
        clone.listeners = new EventListenerList();
        clone.propertyChangeSupport = new PropertyChangeSupport(clone);

        return clone;

    }

    /**
     * Tests the series for equality with another object.
     *
     * @param object  the object.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object object) {

        if (object == null) {
            return false;
        }

        if (object == this) {
            return true;
        }

        if ((object instanceof Series) == false) {
            return false;
        }
        Series s = (Series) object;
        if (getName().equals(s.getName()) == false) {
            return false;
        }

        if (ObjectUtils.equal(getDescription(), s.getDescription()) == false) {
            return false;
        }

        return true;
    }

    /**
     * Returns a hash code.
     * 
     * @return a hash code.
     */
    public int hashCode() {
        int result;
        result = this.name.hashCode();
        result = 29 * result + (this.description != null ? this.description.hashCode() : 0);
        return result;
    }

    /**
     * Registers an object with this series, to receive notification whenever the series changes.
     * <P>
     * Objects being registered must implement the {@link SeriesChangeListener} interface.
     *
     * @param listener  the listener to register.
     */
    public void addChangeListener(SeriesChangeListener listener) {
        this.listeners.add(SeriesChangeListener.class, listener);
    }

    /**
     * Deregisters an object, so that it not longer receives notification whenever the series 
     * changes.
     *
     * @param listener  the listener to deregister.
     */
    public void removeChangeListener(SeriesChangeListener listener) {
        this.listeners.remove(SeriesChangeListener.class, listener);
    }

    /**
     * General method for signalling to registered listeners that the series
     * has been changed.
     */
    public void fireSeriesChanged() {
        if (this.notify) {
            notifyListeners(new SeriesChangeEvent(this));
        }
    }

    /**
     * Sends a change event to all registered listeners.
     *
     * @param event Contains information about the event that triggered the notification.
     */
    protected void notifyListeners(SeriesChangeEvent event) {

        Object[] listenerList = this.listeners.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == SeriesChangeListener.class) {
                ((SeriesChangeListener) listenerList[i + 1]).seriesChanged(event);
            }
        }

    }

    /**
     * Adds a property change listener to the series.
     *
     * @param listener The listener.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes a property change listener from the series.
     *
     * @param listener The listener.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Fires a property change event.
     *
     * @param property  the property key.
     * @param oldValue  the old value.
     * @param newValue  the new value.
     */
    protected void firePropertyChange(String property, Object oldValue, Object newValue) {
        this.propertyChangeSupport.firePropertyChange(property, oldValue, newValue);
    }

}
