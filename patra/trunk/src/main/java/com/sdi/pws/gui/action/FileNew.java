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

import com.sdi.pws.codec.Codec;
import com.sdi.pws.codec.Codec2;
import com.sdi.pws.db.PwsDatabase;
import com.sdi.pws.db.PwsDatabaseImpl;
import com.sdi.pws.gui.DatabaseHolder;
import com.sdi.pws.gui.GuiUtil;
import com.sdi.pws.gui.compo.db.change.ChangeViewDatabase;
import com.sdi.pws.gui.dialog.password.Password;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class FileNew
extends AbstractAction
{
    private DatabaseHolder dbHolder;
    private JComponent application;
    private File file;
    private String passphrase;

    public FileNew(JComponent aAppFrame, DatabaseHolder aHolder)
    {
        super(GuiUtil.getText("action.filenew"));
        dbHolder = aHolder;
        application = aAppFrame;

        // Install the icon.
        final ImageIcon lUIDIcon = new ImageIcon(CopyUid.class.getClassLoader().getResource("assets/new.png"));
        putValue(SMALL_ICON,lUIDIcon);
    }

    public FileNew(JComponent aAppFrame, DatabaseHolder aHolder, File aFile, String aPassphrase)
    {
        this(aAppFrame, aHolder);
        file = aFile;
        passphrase = aPassphrase;

        // Install the icon.
        final ImageIcon lUIDIcon = new ImageIcon(CopyUid.class.getClassLoader().getResource("assets/new.png"));
        putValue(SMALL_ICON,lUIDIcon);
    }

    public void actionPerformed(ActionEvent e)
    {
        if(GuiUtil.continueAndLooseChanges(application, dbHolder))
        {
             // Try to read the database.
            String lPassphrase;
            if(passphrase == null) lPassphrase = Password.askPassword(application);
            else lPassphrase = passphrase;

            if (lPassphrase != null)
            {
                // Create a new empty database and write it.
                final Codec lCodec = new Codec2();
                final PwsDatabase lDb = new PwsDatabaseImpl();
                lDb.setCodec(lCodec);
                lDb.setPassphrase(lPassphrase);
                lDb.setFile(file);
                lDb.setChanged(true);

                // Install the new database in the GUI.
                if (lDb != null)
                {
                    dbHolder.setCurrentDatabase(new ChangeViewDatabase(lDb));
                }
            }
        }
    }
}