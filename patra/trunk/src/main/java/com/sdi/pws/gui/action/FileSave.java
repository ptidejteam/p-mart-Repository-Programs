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

import com.sdi.pws.codec.CodecException;
import com.sdi.pws.db.PwsDatabase;
import com.sdi.pws.gui.DatabaseHolder;
import com.sdi.pws.gui.GuiUtil;
import com.sdi.pws.gui.compo.db.change.ChangeViewDatabase;
import com.sdi.pws.preferences.Preferences;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class FileSave
extends AbstractAction
{
    private DatabaseHolder dbHolder;
    private JComponent application;
    private Preferences prefs;

    public FileSave(JComponent aAppFrame, DatabaseHolder aHolder, Preferences aPrefs)
    {
        super(GuiUtil.getText("action.filesave"));
        dbHolder = aHolder;
        application = aAppFrame;
        prefs = aPrefs;

        // Install the icon.
        final ImageIcon lUIDIcon = new ImageIcon(FileSave.class.getClassLoader().getResource("assets/save.png"));
        putValue(SMALL_ICON,lUIDIcon);

        // Decide if the action is enabled or not right now.
        // Initialize current state using the current database.
        if(dbHolder.getCurrentDatabase() != null)
        {
            setEnabled(dbHolder.getCurrentDatabase().isChanged());
            dbHolder.getCurrentDatabase().addPropertyChangeListener(new PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent evt)
                {
                    // Decide if the action is enabled or not right now.
                    if (dbHolder.getCurrentDatabase() != null)
                        setEnabled(dbHolder.getCurrentDatabase().isChanged());
                }
            });
        }

        // Add a listener chain so that the state is maintained if the database changes.
        dbHolder.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent aHolderEvt)
            {
                final ChangeViewDatabase lOldDb = (ChangeViewDatabase) aHolderEvt.getOldValue();
                final ChangeViewDatabase lNewDb = (ChangeViewDatabase) aHolderEvt.getNewValue();

                // We must not forget to stop listening to the old database.
                if(lOldDb != null)
                {
                    lOldDb.removePropertyChangeListener(this);
                }

                // We can now investigate the state of the new database and
                // add a listener to the new database.
                if (lNewDb != null)
                {
                    setEnabled(lNewDb.isChanged());
                    lNewDb.addPropertyChangeListener(new PropertyChangeListener()
                    {
                        public void propertyChange(PropertyChangeEvent aDbEvent)
                        {
                            // Decide if the action is enabled or not right now.
                            if (dbHolder.getCurrentDatabase() != null)
                                setEnabled(dbHolder.getCurrentDatabase().isChanged());
                        }
                    });
                }
            }
        });
    }

    public void actionPerformed(ActionEvent e)
    {
        try
        {
            // Try to write the database.
            final PwsDatabase lDb = dbHolder.getCurrentDatabase();
            if(lDb.getFile() == null)
            {
                // File is not known, we have to ask it first.
                File lNewFile = GuiUtil.browseForSafe(application, prefs);
                if(lNewFile != null)
                {
                    lDb.setFile(lNewFile);
                    GuiUtil.manageBackups(lDb.getFile(), prefs);
                    lDb.getCodec().write(lDb);
                    lDb.setChanged(false);
                }
            }
            else
            {
                // File is known, we can write immediately.
                GuiUtil.manageBackups(lDb.getFile(), prefs);
                lDb.getCodec().write(lDb);
                lDb.setChanged(false);
            }
        }
        catch (CodecException eCodec)
        {
            JOptionPane.showMessageDialog(application, eCodec.getFormattedMessage(), GuiUtil.getText("general.error"), JOptionPane.ERROR_MESSAGE);
        }
    }
}
