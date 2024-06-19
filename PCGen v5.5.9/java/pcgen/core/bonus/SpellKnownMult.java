/*
 * SpellKnownMult.java
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
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:27:55 $
 * 
 */

package pcgen.core.bonus;

import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.bonus.util.SpellCastInfo;

/**
 * <code>SpellKnownMult</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */
final class SpellKnownMult extends BonusObj
{
	SpellKnownMult()
	{
		super();
	}

	/*
	 * CLASS.<classname OR Any>;LEVEL.<level>
	 * TYPE.<type>;LEVEL.<level>
	 * @param token
	 * @return
	 */
	boolean parseToken(String token)
	{
		int idx = token.indexOf(";LEVEL=");
		if (idx < 0)
		{
			idx = token.indexOf(";LEVEL.");
		}
		if (idx < 0)
		{
			return false;
		}

		String level = token.substring(idx + 7);

		if (token.startsWith("TYPE=") || token.startsWith("TYPE."))
		{
			addBonusInfo(new SpellCastInfo(token.substring(5, idx), level));
			return true;
		}
		else if (token.startsWith("CLASS=") || token.startsWith("CLASS."))
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
		if (((SpellCastInfo) obj).getType() != null)
		{
			sb.append("TYPE.").append(((SpellCastInfo) obj).getType());
		}
		else if (((SpellCastInfo) obj).getPcClass() != null)
		{
			sb.append("CLASS.").append(((SpellCastInfo) obj).getPcClass().getName());
		}
		sb.append(";LEVEL.").append(((SpellCastInfo) obj).getLevel());
		return sb.toString();
	}

}

