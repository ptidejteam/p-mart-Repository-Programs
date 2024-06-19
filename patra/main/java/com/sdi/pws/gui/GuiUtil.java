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
import com.sdi.pws.gui.action.FileOpen;
import com.sdi.pws.preferences.Preferences;
import com.sdi.pws.preferences.PreferencesException;

import javax.swing.*;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Locale;
import java.net.URL;
import java.text.MessageFormat;

public class GuiUtil
{
    public static void centerComponent(Component aTarget)
    {
        centerComponent(aTarget, null);
    }

    public static void centerComponent(Component aTarget, Component aFrame)
    {
        if (aTarget == null)
        {
            return;
        }

        final Dimension d = (aFrame != null ? aFrame.getSize() : Toolkit.getDefaultToolkit().getScreenSize());
        final Point l = (aFrame != null ? aFrame.getLocation() : new Point(0,0));

        aTarget.setLocation(l.x + Math.max(0, (d.getSize().width / 2) - (aTarget.getSize().width / 2)), l.y + Math.max(0, (d.getSize().height / 2) - (aTarget.getSize().height / 2)));
    }

    public static boolean continueAndLooseChanges(JComponent application, DatabaseHolder dbHolder)
    {
         if (dbHolder != null)
        {
            final ChangeViewDatabase lDb = dbHolder.getCurrentDatabase();
            if (lDb != null && lDb.isChanged())
            {
                // Issue a warning. We want to close the application but
                // there is a dirty database in memory.
                if (JOptionPane.showConfirmDialog(application, GuiUtil.getText("util.loosechanges"), GuiUtil.getText("general.confirm"), JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
                {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean continueAndLooseInfo(JComponent application)
    {
        return JOptionPane.showConfirmDialog(application, GuiUtil.getText("util.downgradewarning"), GuiUtil.getText("general.confirm"), JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static File browseForSafe(JComponent aComponent, Preferences aPrefs)
    {
        final String lExt = GuiUtil.getText("util.datext");
        final String lDescr = MessageFormat.format(GuiUtil.getText("util.datdescr"), new Object[]{lExt});
        return browseForFile(aComponent, aPrefs, lExt, lDescr);
    }

    public static File browseForCsv(JComponent aComponent, Preferences aPrefs)
    {
        final String lExt = GuiUtil.getText("util.csvext");
        final String lDescr = MessageFormat.format(GuiUtil.getText("util.csvdescr"), new Object[]{lExt});
        return browseForFile(aComponent, aPrefs, lExt, lDescr);
    }

    public static void manageBackups(File aFile, Preferences aPrefs)
    {
        // First find out how many backups we have to make.
        int lNrBacksups = 0;
        if(aPrefs.hasPreference(Preferences.PREF_NR_BACKUPS))
            try{lNrBacksups = aPrefs.getIntPref(Preferences.PREF_NR_BACKUPS);}catch(Exception e){;}

        // We will only consider backups if the number is larger than 0.
        if((lNrBacksups > 0) && aFile.exists() && aFile.isFile())
        {
            final String lName = aFile.getName();
            File lDir = aFile.getParentFile();

            // Construct a backup file name and copy the exising file to the backup.
            // The backup name ends with the creation timestamp, we need this so we
            // can order the backups accordin to the creation date.
            // We want to clean up the oldest backups first.
            final String lBackupExt = "-bak-" + new Date().getTime();
            final File lBackupFile = new File(aFile.getPath() + lBackupExt);
            aFile.renameTo(lBackupFile);

            // List the exising backups of the file.
            // We order them by name and then by creation timestamp.
            File[] lBackups = lDir.listFiles(new FilenameFilter()
            {
                public boolean accept(File dir, String name)
                {
                    if(name.equals(lName)) return false;
                    else return name.startsWith(lName) && (name.indexOf("-bak-") > 0);
                }
            });
            Arrays.sort(lBackups);

            // Now we can delete the spurious backups.
            for(int i = 0; i < (lBackups.length - lNrBacksups); i++)
                lBackups[i].delete();
        }
    }

    public static File browseForXml(JComponent aComponent, Preferences aPrefs)
    {
        final String lExt = GuiUtil.getText("util.xmlext");
        final String lDescr = MessageFormat.format(GuiUtil.getText("util.xmldescr"), new Object[]{lExt});
        return browseForFile(aComponent, aPrefs, lExt, lDescr);
    }

    public static File browseForFile(JComponent aComponent, Preferences aPrefs, String aExt, String aDesc)
    {
        final JFileChooser lChooser = new JFileChooser();
        lChooser.addChoosableFileFilter(new SimpleFileFilter(new String[] {aExt}, aDesc));
        final int returnVal = lChooser.showOpenDialog(aComponent);

        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File lChoosenFile = lChooser.getSelectedFile();
            if (aPrefs.hasPreference(Preferences.PREF_ADD_EXTENSION))
            {
                boolean lAddExtension = false;
                try
                {
                    lAddExtension = aPrefs.getBoolPref(Preferences.PREF_ADD_EXTENSION);
                }
                catch (PreferencesException e) { ; }
                if (lAddExtension)
                {
                    String lName = lChoosenFile.getName();
                    if (lName.indexOf('.') < 0) lChoosenFile = new File(lChoosenFile.getAbsolutePath() + "." + aExt);
                }
            }
            return lChoosenFile;
        }
        else return null;
    }

    public static boolean continueAndExport(JComponent aComponent, Preferences aPrefs)
    {
        if(aPrefs.hasPreference(Preferences.PREF_WARN_ON_EXPORT))
        {
            try
            {
                if(aPrefs.getBoolPref(Preferences.PREF_WARN_ON_EXPORT))
                {
                    if (JOptionPane.showConfirmDialog(aComponent, GuiUtil.getText("util.exportwarning"), GuiUtil.getText("general.confirm"), JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
                    {
                        return false;
                    }
                }
            }
            catch(PreferencesException e){;}
        }
        return true;
    }

    private static HelpSet hs = null;

    public static HelpBroker getHelpBroker()
    {
        if (hs == null)
        {
            final String helpHS = "help/pwt.hs";
            try
            {
                final URL hsURL = HelpSet.findHelpSet(GuiUtil.class.getClassLoader(), helpHS);
                hs = new HelpSet(null, hsURL);
            }
            catch (Exception ee) { ; }

        }
        return hs.createHelpBroker();
    }

    public static String getText(String aCode)
    {
        final ResourceBundle lBundle = ResourceBundle.getBundle("guiBundle", Locale.ENGLISH, FileOpen.class.getClassLoader());
        final String lMsg = lBundle.getString(aCode);
        return lMsg;
    }
}
