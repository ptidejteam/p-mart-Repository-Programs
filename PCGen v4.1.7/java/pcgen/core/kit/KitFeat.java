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
 * $Id: KitFeat.java,v 1.1 2006/02/21 00:57:51 vauchers Exp $
 */

package pcgen.core.kit;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * <code>KitFeat</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

public final class KitFeat implements Serializable
{
	private boolean free = false;
	private int choiceCount = 1;
	private ArrayList featList = new ArrayList();
	private ArrayList prereqs = null;

	//Only change the UID when the serialized form of the class has also changed
	private static final long serialVersionUID = 1;

	public KitFeat(String argFeatList)
	{
		final StringTokenizer aTok = new StringTokenizer(argFeatList, "|");
		while (aTok.hasMoreTokens())
		{
			featList.add(aTok.nextToken());
		}
	}

	private static void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
	}

	public String toString()
	{
		final int maxSize = featList.size();
		//this could be optimized further if we know the average size of each string in the array
		final StringBuffer info = new StringBuffer(maxSize * 20);
		if ((choiceCount != 1) || (featList.size() != 1))
		{
			info.append(choiceCount).append(" of ");
		}
		for (int i = 0; i < maxSize; ++i)
		{
			if (i != 0)
			{
				info.append(", ");
			}
			info.append((String) featList.get(i));
		}
		if (free)
		{
			info.append(" (free)");
		}
		return info.toString();
	}

	public ArrayList getFeatList()
	{
		return featList;
	}

	public void setFree(boolean argFree)
	{
		free = argFree;
	}

	public int getChoiceCount()
	{
		return choiceCount;
	}

	public void setChoiceCount(String argChoiceCount)
	{
		try
		{
			choiceCount = Integer.parseInt(argChoiceCount);
		}
		catch (Exception exc)
		{
			System.err.println("Invalid choice count \"" + argChoiceCount + "\" in KitFeat.setChoiceCount");
		}
	}

	public void addPrereq(String argPrereq)
	{
		if (prereqs == null)
		{
			prereqs = new ArrayList();
		}
		prereqs.add(argPrereq);
	}

	public ArrayList getPrereqs()
	{
		return prereqs;
	}
}
