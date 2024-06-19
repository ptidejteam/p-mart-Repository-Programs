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

import com.sdi.pws.gui.dialog.prefs.Prefs;
import com.sdi.pws.gui.GuiUtil;
import com.sdi.pws.preferences.Preferences;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AppPrefsEdit
extends AbstractAction
{
    private JComponent application;
    private Preferences prefs;

    public AppPrefsEdit(JComponent aAppFrame, Preferences aGlobalPrefs)
    {
        super(GuiUtil.getText("action.prefs"));
        application = aAppFrame;
        prefs = aGlobalPrefs;

        // Install the icon.
        final ImageIcon lUIDIcon = new ImageIcon(CopyUid.class.getClassLoader().getResource("assets/preferences.png"));
        putValue(SMALL_ICON,lUIDIcon);
    }

    public void actionPerformed(ActionEvent e)
    {
        Prefs.editPreferences(application, prefs);
    }
}
