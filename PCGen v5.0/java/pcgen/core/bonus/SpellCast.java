/*
 * SpellCast.java
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
 * @(#) $Id: SpellCast.java,v 1.1 2006/02/21 01:07:59 vauchers Exp $
 */

/**
 * <code>SpellCast</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */

package pcgen.core.bonus;

import pcgen.core.Globals;
import pcgen.core.PCClass;

final class SpellCast extends BonusObj
{
	SpellCast()
	{
		super();
	}

	private static class SpellCastInfo
	{
		Object level = null;
		String type = null;
		PCClass pcClass = null;

		SpellCastInfo(final String argType, final Object argLevel)
		{
			super();
			type = argType;
			level = argLevel;
		}

		SpellCastInfo(final PCClass argClass, final Object argLevel)
		{
			super();
			pcClass = argClass;
			level = argLevel;
		}
	}

	//
	// CLASS=<classname OR Any>;LEVEL=<level>
	// TYPE=<type>;LEVEL=<level>
	//
	boolean parseToken(String token)
	{
		final int idx = token.indexOf(";LEVEL=");
		if (idx < 0)
		{
			return false;
		}
		Object level;
		try
		{
			level = new Integer(token.substring(idx + 7));
		}
		catch (Exception exc)
		{
			level = token.substring(idx + 7);
		}

		if (token.startsWith("TYPE="))
		{
			addBonusInfo(new SpellCastInfo(token.substring(5, idx), level));
			return true;
		}
		else if (token.startsWith("CLASS="))
		{
			if (token.substring(6, idx).equals("Any"))
			{
				addBonusInfo(new SpellCastInfo((PCClass) null, level));
				return true;
			}
			else
			{
				final PCClass aClass = Globals.getClassNamed(token.substring(6, idx));
				if (aClass != null)
				{
					addBonusInfo(new SpellCastInfo(aClass, level));
					return true;
				}
			}
		}
		return false;
	}

	String unparseToken(Object obj)
	{
		StringBuffer sb = new StringBuffer(30);
		if (((SpellCastInfo) obj).type != null)
		{
			sb.append("TYPE=").append(((SpellCastInfo) obj).type);
		}
		else
		{
			sb.append("CLASS=");
			if (((SpellCastInfo) obj).pcClass != null)
			{
				sb.append(((SpellCastInfo) obj).pcClass.getName());
			}
			else
			{
				sb.append("Any");
			}
		}
		sb.append(";LEVEL=").append(((SpellCastInfo) obj).level.toString());
		return sb.toString();
	}

}

