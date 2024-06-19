/*
 * FOPResourceChecker.java
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
 * Created on April 21, 2001, 2:15 PM
 */

package pcgen.util;

/**
 * Title:        FOPResourcesChecker.java
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

public class FOPResourceChecker
{

	private static int missingResourceCount;
	private static StringBuffer resourceBuffer;

	/**
	 *
	 */
	static
	{
		missingResourceCount = 0;
		resourceBuffer = new StringBuffer();
		checkResource();
	}

	/**
	 *
	 */
	private static void checkResource()
	{
		try
		{
			Class.forName("org.apache.fop.apps.Fop");
		}
		catch (ClassNotFoundException cnfex)
		{
			resourceBuffer.append("Missing resource: fop.jar not found.\n");
			missingResourceCount++;
		}
		try
		{
			Class.forName("org.apache.xalan.xslt.Process");
		}
		catch (ClassNotFoundException cnfex)
		{
			resourceBuffer.append("Missing resource: xalan.jar not found.\n");
			missingResourceCount++;
		}
		try
		{
			Class.forName("org.apache.xerces.framework.XMLParser");
		}
		catch (ClassNotFoundException cnfex)
		{
			resourceBuffer.append("Missing resource: xerces.jar not found.\n");
			missingResourceCount++;
		}
		try
		{
			Class.forName("org.apache.batik.apps.svgpp.Main");
		}
		catch (ClassNotFoundException cnfex)
		{
			resourceBuffer.append("Missing resource: batik.jar not found.\n");
			missingResourceCount++;
		}
		if (missingResourceCount > 0)
		{
			resourceBuffer.append("Look for the missing files on http://pcgen.sourceforge.net");
		}
	}

	public static int getMissingResourceCount()
	{
		return missingResourceCount;
	}

	public static String getMissingResourceMessage()
	{
		return resourceBuffer.toString();
	}

}
