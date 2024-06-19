/*
 * ItemCapacity.java
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
 * @(#) $Id: ItemCapacity.java,v 1.1 2006/02/21 01:10:54 vauchers Exp $
 */

/**
 * <code>ItemCapacity</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */

package pcgen.core.bonus;

final class ItemCapacity extends BonusObj
{
	ItemCapacity()
	{
		super();
	}

	boolean parseToken(String token)
	{
		if (token.startsWith("TYPE="))
		{
			addBonusInfo(token.substring(5));
			return true;
		}
		return false;
	}

	String unparseToken(Object obj)
	{
		return "TYPE=" + obj.toString();
	}
}

