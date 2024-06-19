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
import javax.swing.filechooser.FileFilter;

/**
 *  This class filters out non-psheet files.
 *
 * @author     Jonas Karlsson <jujutsunerd@users.sourceforge.net>
 */
public class PsheetFilter extends FileFilter
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
		String fileName = f.getName().toUpperCase();
		if (fileName != null)
		{
			if (fileName.startsWith("PSHEET"))
			{
				return true;
			}
			else
			{
				return false;
			}
		}

		return false;
	}
}

