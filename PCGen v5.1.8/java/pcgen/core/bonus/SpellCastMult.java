/*
 * SpellCastMult.java
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
 * @(#) $Id: SpellCastMult.java,v 1.1 2006/02/21 01:10:54 vauchers Exp $
 */

/**
 * <code>SpellCastMult</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */

package pcgen.core.bonus;

import pcgen.core.Globals;
import pcgen.core.PCClass;

final class SpellCastMult extends BonusObj
{
	SpellCastMult()
	{
		super();
	}

	private static class SpellCastInfo
	{
		int level = 0;
		String type = null;
		PCClass pcClass = null;

		SpellCastInfo(final String argType, final int argLevel)
		{
			super();
			type = argType;
			level = argLevel;
		}

		SpellCastInfo(final PCClass argClass, final int argLevel)
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
		final int level;
		try
		{
			level = Integer.parseInt(token.substring(idx + 7));
		}
		catch (Exception exc)
		{
			return false;
		}

		if (token.startsWith("TYPE="))
		{
			addBonusInfo(new SpellCastInfo(token.substring(5, idx), level));
			return true;
		}
		else if (token.startsWith("CLASS="))
		{
			final PCClass aClass = Globals.getClassNamed(token.substring(6, idx));
			if (aClass != null)
			{
				addBonusInfo(new SpellCastInfo(aClass, level));
				return true;
			}
		}

		return false;
	}

	String unparseToken(Object obj)
	{
		final StringBuffer sb = new StringBuffer(30);
		if (((SpellCastInfo) obj).type != null)
		{
			sb.append("TYPE=").append(((SpellCastInfo) obj).type);
		}
		else if (((SpellCastInfo) obj).pcClass != null)
		{
			sb.append("CLASS=").append(((SpellCastInfo) obj).pcClass.getName());
		}
		sb.append(";LEVEL=").append(((SpellCastInfo) obj).level);
		return sb.toString();
	}

}

