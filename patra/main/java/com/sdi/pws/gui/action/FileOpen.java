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

import com.sdi.pws.codec.*;
import com.sdi.pws.gui.DatabaseHolder;
import com.sdi.pws.gui.GuiUtil;
import com.sdi.pws.gui.compo.db.change.ChangeViewDatabase;
import com.sdi.pws.gui.dialog.password.Password;
import com.sdi.pws.db.PwsDatabase;
import com.sdi.pws.preferences.Preferences;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class FileOpen
extends AbstractAction
{
    private DatabaseHolder dbHolder;
    private JComponent application;
    private String passphrase;
    private File file;
    private Preferences prefs;

    public FileOpen(JComponent aAppFrame, String aPassphrase, DatabaseHolder aHolder, File aFile, Preferences aPrefs)
    {
        super(GuiUtil.getText("action.fileopen"));
        passphrase = aPassphrase;
        dbHolder = aHolder;
        file = aFile;
        application = aAppFrame;
        prefs = aPrefs;

        // Install the icon.
        final ImageIcon lUIDIcon = new ImageIcon(FileOpen.class.getClassLoader().getResource("assets/open.png"));
        putValue(SMALL_ICON,lUIDIcon);
    }

    public FileOpen(JComponent aAppFrame, DatabaseHolder aHolder, Preferences aPrefs)
    {
        super(GuiUtil.getText("action.fileopen"));
        dbHolder = aHolder;
        application = aAppFrame;
        prefs = aPrefs;

        // Install the icon.
        final ImageIcon lUIDIcon = new ImageIcon(CopyUid.class.getClassLoader().getResource("assets/open.png"));
        putValue(SMALL_ICON,lUIDIcon);
    }

    public void actionPerformed(ActionEvent e)
    {
        if(GuiUtil.continueAndLooseChanges(application, dbHolder))
        {
            if ((passphrase == null) && (file == null))
            {
                final File lFile = GuiUtil.browseForSafe(application, prefs);
                if(lFile != null)
                {
                    String lPassphrase = "";
                    boolean lSuccess = false;
                    while(lPassphrase != null && !lSuccess)
                    {
                        lPassphrase = Password.askPassword(application);
                        lSuccess = this.openDb(lFile, lPassphrase);

                    }
                }
            }
            else if((passphrase != null ) && (file != null))
            {
                openDb(file, passphrase);
            }
        }
    }

    private boolean openDb(File aFile, String aPassphrase)
    {
        try
        {
            if (aPassphrase != null)
            {
                final AutoCodec lAutoCodec = new AutoCodec();
                final PwsDatabase lDb = lAutoCodec.read(aFile, aPassphrase);

                if (lDb != null)
                {
                    // Open the application window.
                    // This should be done before we show dialogs, because otherwise we run
                    // the risk that the dialogs are under the frame.
                    application.setVisible(true);

                    // Install the new database in the GUI.
                    final ChangeViewDatabase lDbModel = new ChangeViewDatabase(lDb);
                    dbHolder.setCurrentDatabase(lDbModel);
                    if(prefs.hasPreference(Preferences.PREF_DEFAULT_DB))
                    {
                        final String lDefaultDb = prefs.getPref(Preferences.PREF_DEFAULT_DB);
                        if(!lDefaultDb.equals(aFile.getAbsolutePath()))
                        {
                            if(prefs.hasPreference(Preferences.PREF_SUGGEST_DEFAULT_DB))
                            {
                                boolean lSuggest = true;
                                try{lSuggest = prefs.getBoolPref(Preferences.PREF_SUGGEST_DEFAULT_DB);}catch(Exception e){;}
                                if(lSuggest && JOptionPane.showConfirmDialog(application, GuiUtil.getText("action.fileopen.default"), GuiUtil.getText("general.confirm"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
                                {
                                    prefs.setPreference(Preferences.PREF_DEFAULT_DB, aFile.getAbsolutePath());
                                }
                            }
                        }
                    }

                    // Check the 1->2 conversion feature.
                    /////////////////////////////////////
                    final Action lUpgrade = new FileVersionUpgrade(application, dbHolder, prefs);
                    final Codec lSelected = lAutoCodec.getSelected();
                    if (lSelected instanceof Codec1)
                    {
                        final String lConvertMode = prefs.getPref(Preferences.PREF_CONVERT_MODE);
                        if (lConvertMode.equals(Preferences.CONVERT_AUTO))
                        {
                            lUpgrade.actionPerformed(new ActionEvent(this, 0, null));
                        }
                        else if (lConvertMode.equals(Preferences.CONVERT_ASK))
                        {
                            int lUserResponse = JOptionPane.showConfirmDialog(application, GuiUtil.getText("action.fileopen.convert"));
                            switch (lUserResponse)
                            {
                                case JOptionPane.OK_OPTION:
                                    lUpgrade.actionPerformed(new ActionEvent(this, 0, null));
                                    break;
                                case JOptionPane.CANCEL_OPTION:
                                case JOptionPane.CLOSED_OPTION:
                                default:
                                    break;
                            }
                        }
                    }
                    // End conversion.
                }
                return true;
            }
        }
        catch (CodecException eCodec)
        {
            JOptionPane.showMessageDialog(application, eCodec.getFormattedMessage(), GuiUtil.getText("general.error"), JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
}