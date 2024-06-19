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

import com.sdi.pws.codec.Codec1;
import com.sdi.pws.codec.Codec2;
import com.sdi.pws.db.PwsDatabase;
import com.sdi.pws.db.PwsRecord;
import com.sdi.pws.gui.DatabaseHolder;
import com.sdi.pws.gui.GuiUtil;
import com.sdi.pws.gui.RecordSelector;
import com.sdi.pws.gui.dialog.edit.Editor1;
import com.sdi.pws.gui.dialog.edit.Editor2;
import com.sdi.pws.preferences.Preferences;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;

public class EntryEdit
extends AbstractAction
{
    private RecordSelector selector;
    private JComponent application;
    private DatabaseHolder holder;
    private Preferences prefs;

    public EntryEdit(JComponent aAppFrame, RecordSelector aSelector, DatabaseHolder aHolder, Preferences aGlobalPrefs)
    {
        super(GuiUtil.getText("action.entryedit"));
        selector = aSelector;
        application = aAppFrame;
        holder = aHolder;
        prefs = aGlobalPrefs;


        // Install the icon.
        final ImageIcon lUIDIcon = new ImageIcon(EntryEdit.class.getClassLoader().getResource("assets/edit.png"));
        putValue(SMALL_ICON,lUIDIcon);

        // Decide if the action is enabled or not right now.
        setEnabled(selector.isInfoAvailable());

        // Add dynamic change of being enabled.
        selector.addPropertyChangeListener(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt)
            {
                EntryEdit.this.setEnabled(selector.isInfoAvailable());
            }
        });
    }

    public void actionPerformed(ActionEvent e)
    {
        if(selector.isInfoAvailable())
        {
            final PwsRecord lRecord = selector.getSelectedRecord();
            final PwsDatabase lDb = holder.getCurrentDatabase();

            if(Codec1.VERSION.equals(lDb.getVersion()))
                Editor1.edit(application, lRecord, prefs);
            else if(Codec2.VERSION.equals(lDb.getVersion()))
                Editor2.edit(application, lDb, lRecord, prefs);
            else
            {
                JOptionPane.showMessageDialog(application, MessageFormat.format(GuiUtil.getText("general.unsupported"), new Object[]{lDb.getVersion()}), GuiUtil.getText("general.error"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}