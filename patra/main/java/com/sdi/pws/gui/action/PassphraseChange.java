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
import com.sdi.pws.gui.compo.db.change.ChangeViewDatabase;
import com.sdi.pws.gui.dialog.changePassword.ChangePassword;
import com.sdi.pws.preferences.Preferences;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PassphraseChange
extends AbstractAction
{
    protected DatabaseHolder dbHolder;
    protected JFrame application;
    protected Preferences prefs;

    public PassphraseChange(JFrame aAppFrame, DatabaseHolder aHolder, Preferences aPrefs)
    {
        super(GuiUtil.getText("action.passphrasechange"));
        this.initializeAction(aAppFrame, aHolder, aPrefs);

        // Install the icon.
        final ImageIcon lUIDIcon = new ImageIcon(CopyUid.class.getClassLoader().getResource("assets/change.png"));
        putValue(SMALL_ICON,lUIDIcon);
    }

    protected void initializeAction(JFrame aAppFrame, DatabaseHolder aHolder, Preferences aPrefs)
    {
        dbHolder = aHolder;
        application = aAppFrame;
        prefs = aPrefs;

        // Decide if the action is enabled or not right now.
        // Initialize current state using the current database.
        if(dbHolder.getCurrentDatabase() != null)
        {
            setEnabled(true);
            dbHolder.getCurrentDatabase().addPropertyChangeListener(new PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent evt)
                {
                    // Decide if the action is enabled or not right now.
                    setEnabled(dbHolder.getCurrentDatabase() != null);
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
                    setEnabled(true);
                    lNewDb.addPropertyChangeListener(new PropertyChangeListener()
                    {
                        public void propertyChange(PropertyChangeEvent aDbEvent)
                        {
                            // Decide if the action is enabled or not right now.
                            setEnabled(dbHolder.getCurrentDatabase() != null);
                        }
                    });
                }
            }
        });
    }

    public void actionPerformed(ActionEvent e)
    {
       ChangePassword.changePassword(application, dbHolder.getCurrentDatabase());
    }
}