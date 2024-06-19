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
import com.sdi.pws.gui.GuiUtil;
import com.sdi.pws.gui.dialog.edit.Editor1;
import com.sdi.pws.gui.dialog.edit.Editor2;
import com.sdi.pws.preferences.Preferences;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;

public class EntryNew
extends AbstractAction
{
    private PwsDatabase db;
    private JComponent application;
    private Preferences prefs;

    public EntryNew(JComponent aAppFrame, PwsDatabase aDb, Preferences aGlobalPrefs)
    {
        super(GuiUtil.getText("action.entrynew"));
        db = aDb;
        application = aAppFrame;
        prefs = aGlobalPrefs;

        // Install the icon.
        final ImageIcon lUIDIcon = new ImageIcon(EntryNew.class.getClassLoader().getResource("assets/add.png"));
        putValue(SMALL_ICON,lUIDIcon);
    }

    public void actionPerformed(ActionEvent e)
    {
        if (Codec1.VERSION.equals(db.getVersion()))
            Editor1.createNew(application, db, prefs);
        else if (Codec2.VERSION.equals(db.getVersion()))
            Editor2.createNew(application, db, prefs);
        else
        {
            JOptionPane.showMessageDialog(application, MessageFormat.format(GuiUtil.getText("general.unsupported"), new Object[]{db.getVersion()}), GuiUtil.getText("general.error"), JOptionPane.ERROR_MESSAGE);
        }
    }
}