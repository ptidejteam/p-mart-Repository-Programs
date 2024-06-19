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

import com.sdi.pws.gui.GuiUtil;
import com.sdi.pws.gui.dialog.about.About;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AppAbout
extends AbstractAction
{
    private JComponent application;

    public AppAbout(JComponent aAppFrame)
    {
        super(GuiUtil.getText("action.about"));
        application = aAppFrame;

        // Install the icon.
        final ImageIcon lUIDIcon = new ImageIcon(AppAbout.class.getClassLoader().getResource("assets/about.png"));
        putValue(SMALL_ICON,lUIDIcon);
    }

    public void actionPerformed(ActionEvent e)
    {
        About.about(application);
    }
}