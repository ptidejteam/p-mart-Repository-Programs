/*
 * KitFeat.java
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
 * Created on September 23, 2002, 8:55 PM
 *
 * $Id: KitFeat.java,v 1.1 2006/02/21 01:33:36 vauchers Exp $
 */

package pcgen.core.kit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.utils.Utility;

/**
 * <code>KitFeat</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

public final class KitFeat extends BaseKit implements Serializable
{
	private boolean free = false;
	private final List featList = new ArrayList();

	//Only change the UID when the serialized form of the class has also changed
	private static final long serialVersionUID = 1;

	/**
	 * Constructor that takes a | separated feat list as a string.
	 * @param argFeatList the string containing a list of feats
	 */
	public KitFeat(String argFeatList)
	{
		final StringTokenizer aTok = new StringTokenizer(argFeatList, "|");
		while (aTok.hasMoreTokens())
		{
			featList.add(aTok.nextToken());
		}
	}

	/**
	 * Returns a string representation of the object.
	 * @return the string representation of the object
	 * @see Object#toString()
	 */
	public String toString()
	{
		final int maxSize = featList.size();
		//this could be optimized further if we know the average size of each string in the array
		final StringBuffer info = new StringBuffer(maxSize * 10);
		if ((choiceCount != 1) || (featList.size() != 1))
		{
			info.append(choiceCount).append(" of ");
		}
		info.append(Utility.joinToStringBuffer(featList, ", "));
		if (free)
		{
			info.append(" (free)");
		}
		return info.toString();
	}

	/**
	 * Returns the feat list.
	 * @return the feat list
	 */
	public List getFeatList()
	{
		return featList;
	}

	/**
	 * Set whether the kit is free.
	 * @param argFree true if the kit is free
	 */
	public void setFree(boolean argFree)
	{
		free = argFree;
	}

}
