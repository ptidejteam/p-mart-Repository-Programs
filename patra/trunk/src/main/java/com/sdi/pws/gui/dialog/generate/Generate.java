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

package com.sdi.pws.gui.dialog.generate;

import com.sdi.pws.generator.Generator;
import com.sdi.pws.generator.GeneratorImpl;
import com.sdi.pws.gui.compo.generator.change.ChangeViewGenerator;
import com.sdi.pws.gui.compo.generator.view.JGeneratorProps;
import com.sdi.pws.gui.GuiUtil;
import com.sdi.pws.preferences.Preferences;
import com.sdi.pws.util.PreferencesUtil;

import javax.swing.*;

public class Generate
{
    public static String generateNewPassword(JComponent aApp, Preferences aGlobalPrefs)
    {
        // First we clone the generator parameter.
        // We dont want to modify the global generator, we just want to start with the
        // default parameters set by the user in the preferences.

        final Generator lCustomGen = PreferencesUtil.getDefaultGenerator(aGlobalPrefs);                

        // Create the db and the component.
        final ChangeViewGenerator lGenModel = new ChangeViewGenerator(lCustomGen);
        final JGeneratorProps lGenProps = new JGeneratorProps(lGenModel);

        // Let the user make a choice.
        while(true)
        {
            // Show the generation dialog.
            final int lUserResponse = JOptionPane.showOptionDialog(aApp, lGenProps, GuiUtil.getText("generate.title"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, null, null);

            if(lUserResponse == JOptionPane.OK_OPTION)
            {
                // Start generating some password candidates.
                String[] lCandidates = new String[10];
                for(int i = 0; i < 10; i++)
                {
                    lCandidates[i] = lCustomGen.generate();
                }

                // Let the user choose a candidate.
                String lSelectedPassword = (String) JOptionPane.showInputDialog(aApp, GuiUtil.getText("generate.proposal"), GuiUtil.getText("generate.proposal.title"), JOptionPane.QUESTION_MESSAGE, null, lCandidates, null);
                if(lSelectedPassword != null) return lSelectedPassword;
            }
            else return null;
        }
    }
}
