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
 * Created on May 10, 2001 09:01
 */

package pcgen.gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *  This class filters out non-pcg files.
 *
 * @author     Jonas Karlsson <jujutsunerd@users.sourceforge.net>
 * @version    $Revision: 1.1 $
 */
public class PcgFilter extends FileFilter
{

	/**
	 *  Returns a description of this class
	 *
	 * @return    The Description
	 * @since
	 */
	public String getDescription()
	{
		return "Pcg files only";
	}


	/**
	 *  Accept all directories and all pcg files
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
			if (fileName.endsWith("PCG"))
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

