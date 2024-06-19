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

package com.sdi.pws.gui;

import com.sdi.pws.gui.compo.db.change.ChangeViewDatabase;
import com.sdi.pws.db.PwsDatabase;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.text.MessageFormat;

public class TitleRenderer
{
    private DatabaseHolder dbHolder;
    private JFrame frame;

    public TitleRenderer(JFrame aFrame, DatabaseHolder aHolder)
    {
        dbHolder = aHolder;
        frame = aFrame;

        // Decide if the action is enabled or not right now.
        // Initialize current state using the current database.
        if (dbHolder.getCurrentDatabase() != null)
        {
            setTitle(dbHolder.getCurrentDatabase());
            dbHolder.getCurrentDatabase().addPropertyChangeListener(new PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent evt)
                {
                    // Decide if the action is enabled or not right now.
                    if (dbHolder.getCurrentDatabase() != null)
                        setTitle(dbHolder.getCurrentDatabase());
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
                if (lOldDb != null)
                {
                    lOldDb.removePropertyChangeListener(this);
                }

                // We can now investigate the state of the new database and
                // add a listener to the new database.
                if (lNewDb != null)
                {
                    setTitle(lNewDb);
                    lNewDb.addPropertyChangeListener(new PropertyChangeListener()
                    {
                        public void propertyChange(PropertyChangeEvent aDbEvent)
                        {
                            // Decide if the action is enabled or not right now.
                            if (dbHolder.getCurrentDatabase() != null)
                                setTitle(dbHolder.getCurrentDatabase());
                        }
                    });
                }
            }
        });
    }

    private void setTitle(PwsDatabase aDb)
    {
        final File lFile = aDb.getFile();
        String lFileRepr;
        if(lFile == null)
        {
            lFileRepr = MessageFormat.format(GuiUtil.getText("general.title.changed"), new Object[]{"New"});
        }
        else
        {
            final String lFileName =  lFile.getAbsolutePath();
            if(aDb.isChanged()) lFileRepr = MessageFormat.format(GuiUtil.getText("general.title.changed"), new Object[]{lFileName});
            else lFileRepr = MessageFormat.format(GuiUtil.getText("general.title.normal"), new Object[]{lFileName});
        }
        frame.setTitle(lFileRepr);
    }
}