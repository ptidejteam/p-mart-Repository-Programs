/*
 * PcgFilter.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package pcgen.gui;

import java.io.File;
import java.io.FilenameFilter;
import javax.swing.filechooser.FileFilter;
import pcgen.core.Globals;

/**
 *  This class filters out non-psheet files.
 *
 * @author Jonas Karlsson <jujutsunerd@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
class PsheetFilter extends FileFilter implements FilenameFilter
{
	/**
	 *  Returns a description of this class
	 *
	 * @return    The Description
	 * @since
	 */
	public String getDescription()
	{
		return "Party templates";
	}

	/**
	 *  Accept all directories and all psheet files
	 *
	 * @param  f  The file to be checked
	 * @return    Whether the file is accepted
	 * @since
	 */
	public boolean accept(File f)
	{
		if (f.isDirectory())
		{
			return true;
		}
		// author: Thomas Behr 08-02-02
		return accept(null, f.getName());
	}

	/**
	 *  Accept all psheet files
	 *
	 * @param dir    - the directory in which the file was found
	 * @param name - the name of the file.
	 *
	 * @return true if and only if the name should be included in the file list; false otherwise
	 * author Thomas Behr 08-02-02
	 */
	public boolean accept(File dir, String name)
	{
		String s = name.toLowerCase();
		if (s.startsWith("psheet"))
			if (!s.endsWith(".fo") || (s.endsWith(".fo") && Globals.isDebugMode()))
				return true;
		return false;
	}
}
