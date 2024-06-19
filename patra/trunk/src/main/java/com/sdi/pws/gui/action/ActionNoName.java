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

package com.sdi.pws.gui.action;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionEvent;

/**
 * Decorator class that blanks out the name. It can be used to assign the same action to different
 * containers, but to show it differently in different containers.
 */
public class ActionNoName
implements Action
{
    private Action action;

    public ActionNoName(Action aAction)
    {
        action = aAction;
    }

    public boolean isEnabled()
    {
        return action.isEnabled();
    }

    public void setEnabled(boolean b)
    {
        action.setEnabled(b);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        action.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        action.removePropertyChangeListener(listener);
    }

    public Object getValue(String key)
    {
        if(key.equals(Action.NAME)) return null;
        return action.getValue(key);
    }

    public void putValue(String key, Object value)
    {
        action.putValue(key, value);
    }

    public void actionPerformed(ActionEvent e)
    {
        action.actionPerformed(e);
    }
}
