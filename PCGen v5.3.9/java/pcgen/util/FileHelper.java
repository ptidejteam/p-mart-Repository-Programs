/**
 * FileHelper.java
 * Copyright 2003 (C) John Watson <john@sleazyweasel.com>
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
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:15:59 $
 */
package pcgen.util;

import java.io.File;

/**
 * Assorted path and filename methods.
 *
 * @author     John Watson <john@sleazyweasel.com>
 * @version    $Revision: 1.1 $
 **/

public class FileHelper
{
	public static String findRelativePath(File base, File relative)
	{
		File testFile = base.getParentFile();

		String relativePath = stripOffRoot(relative);

		StringBuffer dots = new StringBuffer();
		do
		{
			if (testFile.getParentFile() == null)
			{
				//we're at the root...
				return dots.append(relativePath).toString();
			}
			String testPath = stripOffRoot(testFile);
			if (relativePath.indexOf(testPath) == 0)
			{
				String pieceToKeep = relativePath.substring(testPath.length() + 1, relativePath.length());
				return dots.append(pieceToKeep).toString();
			}
			else
			{
				dots.append("../");
			}

			testFile = testFile.getParentFile();
		}
		while (testFile != null);
		return dots + relativePath;
	}

	private static String stripOffRoot(File relative)
	{
		String root = findRoot(relative);
		return relative.getAbsolutePath().substring(root.length(), relative.getAbsolutePath().length());
	}

	private static String findRoot(File file)
	{
		File test = file.getParentFile();
		while (test.getParentFile() != null)
		{
			test = test.getParentFile();
		}
		return test.getAbsolutePath();
	}

}
