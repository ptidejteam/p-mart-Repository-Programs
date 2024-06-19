/*
 * Bonus.java
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
 * Last Edited: $Date: 2006/02/21 01:33:50 $
 *
 */

package pcgen.core.bonus.util;

import pcgen.core.PCClass;

/**
 * Extracted from SpellCastMult, SpellKnown and SpellCast.
 */
public class SpellCastInfo
{
	private String level = "0";
	private String type = null;
	private PCClass pcClass = null;

	public SpellCastInfo(final String argType, final String argLevel)
	{
		super();
		setType(argType);
		setLevel(argLevel);
	}

	public SpellCastInfo(final PCClass argClass, final String argLevel)
	{
		super();
		setPcClass(argClass);
		setLevel(argLevel);
	}

	public String getLevel()
	{
		return level;
	}

	private void setLevel(String argLevel)
	{
		level = argLevel;
	}

	public String getType()
	{
		return type;
	}

	private void setType(String argType)
	{
		type = argType;
	}

	public PCClass getPcClass()
	{
		return pcClass;
	}

	private void setPcClass(PCClass argClass)
	{
		pcClass = argClass;
	}

}
