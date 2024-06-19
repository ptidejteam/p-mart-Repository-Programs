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

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class SimpleFileFilter
extends FileFilter
{
    String[] extensions;
    String description;

    public SimpleFileFilter(String ext)
    {
        this(new String[]{ext}, null);
    }

    public SimpleFileFilter(String[] exts, String descr)
    {
        extensions = new String[exts.length];
        for (int i = exts.length - 1; i >= 0; i--)
        {
            extensions[i] = exts[i].toLowerCase();
        }
        description = (descr == null ? exts[0] + " files" : descr);
    }

    public boolean accept(File f)
    {
        if (f.isDirectory())
        {
            return true;
        }

        String name = f.getName().toLowerCase();
        for (int i = extensions.length - 1; i >= 0; i--)
        {
            if (name.endsWith(extensions[i]))
            {
                return true;
            }
        }
        return false;
    }

    public String getDescription()
    {
        return description;
    }
}