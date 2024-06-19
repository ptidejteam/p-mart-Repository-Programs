/*
 * SizeAdjustment.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on December 13, 2001, 4:24 PM
 */

package pcgen.core;

import java.util.Iterator;
import java.util.List;

/**
 * <code>SizeAdjustment</code>.
 *
 * @author Greg Bingleman <byngl@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class SizeAdjustment extends PObject
{
	private String abbreviation = ""; // should be 1-character long
	private boolean isDefaultSize = false;

	public void setAbb(String ab)
	{
		abbreviation = ab;
	}

	public String getAbbreviation()
	{
		return abbreviation;
	}

	public void setIsDefaultSize(boolean arg)
	{
		isDefaultSize = arg;
	}

	boolean isDefaultSize()
	{
		return isDefaultSize;
	}

	double getBonusTo(String bonusType, List typeList, double defaultValue)
	{
		for (Iterator i = typeList.iterator(); i.hasNext();)
		{
			double a = bonusTo(bonusType, "TYPE=" + i.next().toString());
			if (a != 0.0)
			{
				defaultValue = a;
				break;
			}
		}
		return defaultValue;
	}
}
