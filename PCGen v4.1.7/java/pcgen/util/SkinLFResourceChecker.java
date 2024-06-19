/*
 * SkinLFResourceChecker.java
 * Copyright 2001 (C) Jason Buchanan
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
 * Created on January 3, 2002
 */

package pcgen.util;

import pcgen.core.Constants;

/**
 * Title:        SkinLFResourceChecker.java
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Jason Buchanan
 * @version $Revision: 1.1 $
 */

public final class SkinLFResourceChecker
{
	private static int missingResourceCount;
	private static StringBuffer resourceBuffer;

	static
	{
		missingResourceCount = 0;
		resourceBuffer = new StringBuffer();
		checkResource();
	}

	private static void checkResource()
	{
		try
		{
			Class.forName("com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
		}
		catch (ClassNotFoundException cnfex)
		{
			resourceBuffer.append("Missing resource: skinlf.jar not found.").append(Constants.s_LINE_SEP);
			missingResourceCount++;
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