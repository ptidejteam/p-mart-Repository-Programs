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

package com.sdi.pws.gui.compo.preferences.change;

import com.sdi.pws.preferences.Preferences;
import com.sdi.pws.preferences.PreferencesException;

import java.util.Iterator;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

public class ChangeViewPreferences
implements Preferences
{
    private Preferences prefs;
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    private boolean changed = false;

    public ChangeViewPreferences(Preferences aInternal)
    {
        prefs = aInternal;
    }

    public boolean isChanged()
    {
        return changed;
    }

    public void clearChanged()
    {
        changed = false;
    }

    public boolean hasPreference(String aName)
    {
        return prefs.hasPreference(aName);
    }

    public Iterator iterator()
    {
        return prefs.iterator();
    }

    public String getPref(String aName)
    {
        return prefs.getPref(aName);
    }

    public boolean getBoolPref(String aName)
    throws PreferencesException
    {
        return prefs.getBoolPref(aName);
    }

    public int getIntPref(String aName)
    throws PreferencesException
    {
        return prefs.getIntPref(aName);
    }

    public void setPreference(String aName, String aValue)
    {
        if(prefs.hasPreference(aName))
        {
            final String lOldVal = prefs.getPref(aName);
            if(lOldVal != null)
            {
                if(!lOldVal.equals(aValue))
                {
                    // Change.
                    prefs.setPreference(aName, aValue);
                    support.firePropertyChange(aName, lOldVal, aValue);
                    changed = true;
                }
            }
            else
            {
                if(aValue != null)
                {
                    // Change.
                    prefs.setPreference(aName, aValue);
                    support.firePropertyChange(aName, lOldVal, aValue);
                    changed = true;
                }
            }
        }
        else
        {
            // New preference.
            prefs.setPreference(aName, aValue);
            support.firePropertyChange(aName, null, aValue);
            changed = true;
        }
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        support.removePropertyChangeListener(propertyName, listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        support.addPropertyChangeListener(propertyName, listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        support.removePropertyChangeListener(listener);
    }
}