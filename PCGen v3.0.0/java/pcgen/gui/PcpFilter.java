/*
 * PcpFilter.java
 * Copyright 2001 (C) Jason Buchanan <lonejedi@users.sourceforge.net>
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
 * Created on August 3, 2001 16:29
 */

package pcgen.gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *  This class filters out non-pcp files.
 *
 * @author     Jason Buchanan <lonejedi@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class PcpFilter extends FileFilter
{

	/**
	 *  Returns a description of this class
	 *
	 * @return    The Description
	 * @since
	 */
	public String getDescription()
	{
		return "Pcp files only";
	}


	/**
	 *  Accept all directories and all pcp files
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
			if (fileName.endsWith("PCP"))
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
