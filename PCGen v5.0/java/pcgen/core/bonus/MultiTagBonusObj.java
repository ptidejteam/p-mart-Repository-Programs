package pcgen.core.bonus;

/*
 * MultiTagBonusObj.java
 * Copyright 2003 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on Mar 29, 2003, 00:00
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:07:59 $
 *
 */

/**
 * <code>MultiTagBonusObj</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @author  Jonas Karlsson <jujutsunerd@sf.net>
 */
public abstract class MultiTagBonusObj extends BonusObj
{
	private static String[] bonusTags = null;

	boolean parseToken(String token)
	{
		for (int i = 0; i < getBonusTagLength(); ++i)
		{
			if (getBonustag(i).equals(token))
			{
				addBonusInfo(new Integer(i));
				return true;
			}
		}
		return false;
	}

	String unparseToken(Object obj)
	{
		return getBonustag(((Integer) obj).intValue());
	}

	private static String getBonustag(int tagNumber)
	{
		return bonusTags[tagNumber];
	}

	private static int getBonusTagLength()
	{
		return bonusTags.length;
	}

	/**
	 * Give the array of tag strings.
	 * @param argBonusTags
	 */
	public static void setBonusTags(String[] argBonusTags)
	{
		bonusTags = argBonusTags;
	}
}
