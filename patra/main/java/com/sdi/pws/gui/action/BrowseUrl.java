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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.net.URI;


public class BrowseUrl
extends AbstractAction
{
    private String url;

    public BrowseUrl(String aUrl)
    {
        super(aUrl);
        url = aUrl;

        // Install the icon.
        final ImageIcon lUIDIcon = new ImageIcon(AppAbout.class.getClassLoader().getResource("assets/www.png"));
        putValue(SMALL_ICON,lUIDIcon);
    }

    @Override
    public boolean isEnabled()
    {
        final Desktop lDesktop = Desktop.getDesktop();
        return lDesktop.isSupported(Desktop.Action.BROWSE);
    }

    public void actionPerformed(ActionEvent aActionEvent)
    {
        final Desktop lDesktop = Desktop.getDesktop();
        if(lDesktop.isSupported(Desktop.Action.BROWSE))
        {
            try { lDesktop.browse(new URI("http://" + url)); } catch(Exception e) { }
        }
    }
}
