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
import com.sdi.pws.codec.CsvCodec;
import com.sdi.pws.gui.DatabaseHolder;
import com.sdi.pws.gui.GuiUtil;
import com.sdi.pws.preferences.Preferences;

import javax.swing.*;
import java.io.File;

public class FileCsvImport
extends FileImport
{
    private static final Codec codec = new CsvCodec();

    protected Codec getCodec()
    {
        return codec;
    }

    protected File askFile()
    {
        return GuiUtil.browseForCsv(application, prefs);
    }

    protected String askPassphrase()
    {
        return "Bruno Ranschaert (c) 2005-2006";
    }

    public FileCsvImport(JComponent aAppFrame, DatabaseHolder aHolder, Preferences aPrefs)
    {
        super(GuiUtil.getText("action.filecsvImport"));
        wireAction(aAppFrame, aHolder, aPrefs);
    }
}