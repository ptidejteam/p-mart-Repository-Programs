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
import com.sdi.pws.preferences.Preferences;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.MessageFormat;

public class FileOpenOrNew
extends AbstractAction
{
    private JPasswordField pwdField;
    private DatabaseHolder holder;
    private JTextComponent path;
    private JComponent application;
    private Preferences prefs;

    public FileOpenOrNew(JComponent aApplication, JPasswordField aPwdfield, DatabaseHolder aHolder, JTextComponent aPath, Preferences aPrefs)
    {
        super(GuiUtil.getText("action.fileopenornew"));
        pwdField = aPwdfield;
        holder = aHolder;
        path = aPath;
        application = aApplication;
        prefs = aPrefs;

        // Install the icon.
        final ImageIcon lUIDIcon = new ImageIcon(FileOpen.class.getClassLoader().getResource("assets/go.png"));
        putValue(SMALL_ICON,lUIDIcon);
    }

    public void actionPerformed(ActionEvent e)
    {
        final File lFile = new File(path.getText());
        if(lFile.exists())
        {
            // File exists, we open an existing database.
            // Delegate to existing open file action.
            final Action lOpen = new FileOpen(application, new String(pwdField.getPassword()), holder, new File(path.getText()), prefs);
            lOpen.actionPerformed(e);
        }
        else
        {
            // File does not exist.
            // Ask the user if he wants to create a new database.
            if (JOptionPane.showConfirmDialog(application, MessageFormat.format(GuiUtil.getText("action.fileopenornew.confirm"), new Object[]{lFile.getAbsolutePath()}), GuiUtil.getText("general.confirm"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
            {
                // Delegate to an existing  new file action.
                final Action lNew = new FileNew(application, holder, lFile, new String(pwdField.getPassword()));
                lNew.actionPerformed(e);
            }
        }
    }
}