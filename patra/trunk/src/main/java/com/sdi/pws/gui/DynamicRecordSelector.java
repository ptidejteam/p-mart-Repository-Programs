/*
Password Tracker (PATRA). An application to safely store your passwords.
Copyright (C) 2006-2009  Bruno Ranschaert, S.D.I.-Consulting BVBA.

For more information contact: nospam@sdi-consulting.com
Visit our website: http://www.sdi-consulting.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

package com.sdi.pws.gui;

import com.sdi.pws.db.PwsRecord;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Object holder for selectors. The internal selector can change. The GUI elements can keep the
 * dynamic shell, they don't have to be aware that the internal selector changed.
 * They can keep track of the external shell.
 * Goal: prevent putting everything in a shared context.  It is independent of the selector implementation,
 * it could be a table view selector or a tree view selector, we want to make abstraction at this level.
 * The concrete internal selector should be changed when another view becomes active.
 */
public class DynamicRecordSelector
implements RecordSelector, PropertyChangeListener
{
    private RecordSelector selector;
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    public DynamicRecordSelector(RecordSelector aSelector)
    {
        selector = aSelector;
        aSelector.addPropertyChangeListener(this);
    }

    // Property change support.

    public void addPropertyChangeListener(PropertyChangeListener aListener)
    {
        support.addPropertyChangeListener(aListener);
    }

    public void removePropertyChangeListener(PropertyChangeListener aListener)
    {
        support.removePropertyChangeListener(aListener);
    }

    // Listen to internal events and pass them through to
    // our own listeners.
    public void propertyChange(PropertyChangeEvent evt)
    {
        support.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }

    // Selector stuff.

    public void setSelector(RecordSelector aSelector)
    {
        final Object lOldValue = selector;
        final Object lNewValue = aSelector;

        if(selector != null) selector.removePropertyChangeListener(this);
        selector = aSelector;
        
        if(aSelector != null) aSelector.addPropertyChangeListener(this);
        support.firePropertyChange("selector", lOldValue, lNewValue);
    }

    public boolean isInfoAvailable()
    {
        if(selector == null) return false;
        else return selector.isInfoAvailable();
    }

    public PwsRecord getSelectedRecord()
    {
        if(selector == null) return null;
        else return selector.getSelectedRecord();
    }
}