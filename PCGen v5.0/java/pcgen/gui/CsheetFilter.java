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
import java.io.FilenameFilter;
import java.util.ArrayList;
import javax.swing.filechooser.FileFilter;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.util.PropertyFactory;

/**
 *  This class filters out non-csheet files.
 *
 * @author     Jonas Karlsson <jujutsunerd@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

final class CsheetFilter extends FileFilter implements FilenameFilter
{
	private static final String chaTemplates = PropertyFactory.getString("in_chaTemplates");
	private String dirFilter = null;
	private ArrayList acceptedList = null;
	private int sheetType = 0; // 0 = character sheet, 1 = party sheet
	private String ignoreExtension = ".fo";

	/**
	 *  Returns a description of this class
	 *
	 * @return    The Description
	 * @since
	 */
	public String getDescription()
	{
		return chaTemplates;
	}

	/**
	 *  sets a directory filter for this instance
	 *
	 * @param arg   the directory name in which valid sheets will be found
	 * @since
	 */
	public void setDirFilter(final String arg)
	{
		dirFilter = arg;
	}

	/**
	 *  sets the sheet type filter for this instance
	 *
	 * @param arg   the type of sheets to be accepted (0==csheets, 1==psheet)
	 * @since
	 */
	public void setSheetType(final int arg)
	{
		sheetType = arg;
	}

	/**
	 *  sets the extenion of files which are not acceptable
	 *
	 * @param arg   the extenion of files not to be accepted
	 * @since
	 */
	public void setIgnoreExtension(final String arg)
	{
		ignoreExtension = arg;
	}

	/**
	 *  Accept all directories and all csheet files
	 *
	 * @param  f  The file to be checked
	 * @return    Whether the file is accepted
	 */

	public boolean accept(final File f)
	{
		if (f.isDirectory())
		{
			final File fileList[] = f.listFiles();
			for (int i = 0; i < fileList.length; ++i)
			{
				accept(fileList[i]);
			}
			return true;
		}
		return accept(f.getParentFile(), f.getName());
	}

	public boolean accept(final File dir, final String name)
	{
		final String s = name.toLowerCase();
		final File aFile = new File(dir + File.separator + name);
		if (aFile.isDirectory())
		{
			return accept(aFile);
		}
		if (!aFile.getParent().endsWith(File.separator + dirFilter))
		{
			return false;
		}

		if (!s.endsWith(ignoreExtension) || (s.endsWith(ignoreExtension) && Globals.isDebugMode()))
		{
			if ((sheetType == 0 && s.startsWith("csheet")) || (sheetType == 1 && s.startsWith("psheet")))
			{
				if (acceptedList != null)
				{
					try
					{
						final String filename = dir.getAbsolutePath().substring(SettingsHandler.getTemplatePath().getAbsolutePath().length() + 1) + File.separator + name;
						acceptedList.add(filename);
					}
					catch (Exception exc)
					{
					}
				}
				return true;
			}
		}

		return false;
	}

	public ArrayList getAccepted()
	{
		acceptedList = new ArrayList();
		accept(SettingsHandler.getTemplatePath());
		return acceptedList;
	}

	public CsheetFilter()
	{
	}

	public CsheetFilter(int sheetTypeInt)
	{
		sheetType = sheetTypeInt;
	}

}


