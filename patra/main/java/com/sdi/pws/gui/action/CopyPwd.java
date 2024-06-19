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

import com.sdi.pws.db.PwsField;
import com.sdi.pws.db.PwsRecord;
import com.sdi.pws.gui.ClipboardMonitor;
import com.sdi.pws.gui.RecordSelector;
import com.sdi.pws.gui.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class CopyPwd
extends AbstractAction
{
    private RecordSelector selector;
    private ClipboardMonitor monitor;

    public CopyPwd(RecordSelector aSelector, ClipboardMonitor aMonitor)
    {
        super(GuiUtil.getText("action.copypwd"));
        selector = aSelector;
        monitor = aMonitor;

        // Install the icon.
        final ImageIcon lPWDIcon = new ImageIcon(CopyUid.class.getClassLoader().getResource("assets/pwd.png"));
        putValue(SMALL_ICON,lPWDIcon);

        // Decide if the action is enabled or not right now.
        setEnabled(selector.isInfoAvailable());

        // Add dynamic change of being enabled.
        selector.addPropertyChangeListener(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt)
            {
                CopyPwd.this.setEnabled(selector.isInfoAvailable());
            }
        });
    }

    public void actionPerformed(ActionEvent e)
    {
        if(selector.isInfoAvailable())
        {
            final PwsRecord lRecord = selector.getSelectedRecord();
            String lPwd = null;
            if(lRecord.hasType(PwsField.FIELD_PWD))
                try{lPwd = lRecord.get(PwsField.FIELD_PWD).getAsString();}catch(Exception eIgnore){ }
            monitor.grabOwnership();
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(lPwd), monitor);
        }
    }
}