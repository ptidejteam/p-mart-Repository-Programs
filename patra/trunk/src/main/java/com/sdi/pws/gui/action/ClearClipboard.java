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

import com.sdi.pws.gui.ClipboardMonitor;
import com.sdi.pws.gui.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ClearClipboard
extends AbstractAction
{
    private ClipboardMonitor monitor;

    public ClearClipboard(ClipboardMonitor aMonitor)
    {
        super(GuiUtil.getText("action.clearclip"));
        monitor = aMonitor;

        // Install the icon.
        final ImageIcon lUIDIcon = new ImageIcon(ClearClipboard.class.getClassLoader().getResource("assets/clearclip.png"));
        putValue(SMALL_ICON,lUIDIcon);

        // Decide if the action is enabled or not right now.
        setEnabled(monitor.isOwner());

        // Add dynamic change of being enabled.
        monitor.addPropertyChangeListener(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt)
            {
                ClearClipboard.this.setEnabled(monitor.isOwner());
            }
        });
    }

    public void actionPerformed(ActionEvent e)
    {
        if(monitor.isOwner())
        {
            monitor.lostOwnership(null, null);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(null), monitor);
        }
    }
}