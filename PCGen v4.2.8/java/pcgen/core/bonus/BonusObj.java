/*
 * BonusObj.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on December 13, 2002, 9:19 AM
 *
 * @(#) $Id: BonusObj.java,v 1.1 2006/02/21 01:00:21 vauchers Exp $
 */

/**
 * <code>BonusObj</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */
package pcgen.core.bonus;

import java.io.Serializable;
import java.util.ArrayList;

public class BonusObj implements Serializable
{
	private ArrayList prereqList = null;
	private int typeOfBonus = Bonus.BONUS_UNDEFINED;
	private String bonusType = "";
	private Object bonusValue = null;
	private ArrayList bonusInfo = null;

	public String toString()
	{
		final StringBuffer sb = new StringBuffer(50);
		sb.append(getTypeOfBonus());
		if (bonusInfo != null)
		{
			for (int i = 0; i < bonusInfo.size(); ++i)
			{
				sb.append(i == 0 ? '|' : ',').append(unparseToken(bonusInfo.get(i)));
			}
		}
		else
		{
			sb.append("|ERROR");
		}

		if (bonusValue != null)
		{
			sb.append('|').append(bonusValue.toString());
		}

		if (prereqList != null)
		{
			for (int i = 0; i < prereqList.size(); ++i)
			{
				sb.append('|').append(prereqList.get(i));
			}
		}
		if (bonusType.length() != 0)
		{
			sb.append("|TYPE=").append(bonusType);
		}
		return sb.toString();
	}

	boolean parseToken(final String token)
	{
		return false;
	}

	String unparseToken(final Object obj)
	{
		return "";
	}

	void setTypeOfBonus(final int type)
	{
		typeOfBonus = type;
	}

	void setValue(final String bValue)
	{
		try
		{
			bonusValue = new Integer(bValue);
		}
		catch (Exception e1)
		{
			try
			{
				bonusValue = new Float(bValue);
			}
			catch (Exception e2)
			{
				bonusValue = bValue;
			}
		}
	}

	boolean addType(final String typeString)
	{
		if (bonusType.length() == 0)
		{
			bonusType = typeString;
			return true;
		}
		return false;
	}

	void addPreReq(final String prereqString)
	{
		if (prereqList == null)
		{
			prereqList = new ArrayList();
		}
		if (!prereqList.contains(prereqString))
		{
			prereqList.add(prereqString);
		}
	}

	void addBonusInfo(final Object obj)
	{
		if (bonusInfo == null)
		{
			bonusInfo = new ArrayList();
		}
		bonusInfo.add(obj);
	}

	String getTypeOfBonus()
	{
		return Bonus.bonusTags[typeOfBonus];
	}

	void setVariable(final String aString)
	{
	}
}
