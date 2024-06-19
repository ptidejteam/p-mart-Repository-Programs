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

import com.sdi.pws.gui.DatabaseHolder;
import com.sdi.pws.gui.GuiUtil;
import com.sdi.pws.gui.ClipboardMonitor;
import com.sdi.pws.gui.compo.preferences.change.ChangeViewPreferences;
import com.sdi.pws.preferences.PrefStorage;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AppExit
extends AbstractAction
{
    private DatabaseHolder dbHolder;
    private JComponent application;
    private ChangeViewPreferences prefs;

    public AppExit(JComponent aAppFrame, DatabaseHolder aHolder, ChangeViewPreferences aPrefs)
    {
        super(GuiUtil.getText("action.exit"));
        dbHolder = aHolder;
        application = aAppFrame;
        prefs = aPrefs;

        // Install the icon.
        final ImageIcon lUIDIcon = new ImageIcon(AppExit.class.getClassLoader().getResource("assets/exit.png"));
        putValue(SMALL_ICON,lUIDIcon);
    }

    public void actionPerformed(ActionEvent e)
    {
        if(prefs.isChanged()) PrefStorage.savePreferences(prefs);
        if(GuiUtil.continueAndLooseChanges(application, dbHolder))
        {
            new ClearClipboard(new ClipboardMonitor()).actionPerformed(e);
            System.exit(0);
        }
    }
}