/*
 * CompanionMod.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 *************************************************************************
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @Created on July 10th, 2002, 3:55 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:13:32 $
 *
 *************************************************************************/

package pcgen.core.character;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import pcgen.core.PObject;

/**
 * <code>CompanionMod</code>.
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision: 1.1 $

 **/
public final class CompanionMod extends PObject implements Comparable
{
	private final Map classMap = new HashMap();
	private final Map switchRaceMap = new HashMap();
	private String type = "";
	private int level = 0;
	private int nHD = 0;
	private ArrayList vFeatList = null;

	/**
	 * Compares classMap, type and level
	 **/
	public int compareTo(Object obj)
	{
		if (obj instanceof CompanionMod)
		{
			final CompanionMod aComp = (CompanionMod) obj;
			if (classMap.entrySet().equals(aComp.getClassMap().entrySet()) && (type.equals(aComp.getType())) && (level == aComp.getLevel()))
			{
				return 1;
			}
		}
		return 0;
	}

	/**
	 * Compares classMap, type and level
	 **/
	public boolean equals(final Object obj)
	{
		if (obj instanceof CompanionMod)
		{
			final CompanionMod aComp = (CompanionMod) obj;
			if (classMap.entrySet().equals(aComp.getClassMap().entrySet()) && (type.equals(aComp.getType())) && (level == aComp.getLevel()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Hashcode of the keyname
	 */
	public int hashCode()
	{
		return classMap.hashCode();
	}

	public String toString()
	{
		return type;
	}

	public Map getClassMap()
	{
		return classMap;
	}

	public Map getSwitchRaceMap()
	{
		return switchRaceMap;
	}

	public String getCompanionSwitch(final String aName)
	{
		return (String) switchRaceMap.get(aName.toUpperCase());
	}

	private int getLevel()
	{
		return level;
	}

	public int getLevel(String cName)
	{
		if (classMap.get(cName) != null)
		{
			return Integer.parseInt(classMap.get(cName).toString());
		}
		return -1;
	}

	public void setLevel(int x)
	{
		level = x;
	}

	//public String getType()
	//{
	//	return type;
	//}

	//public void setType(String x)
	//{
	//	type = x;
	//}

	public int getHitDie()
	{
		return nHD;
	}

	public void setHitDie(int x)
	{
		nHD = x;
	}

	/**
	 * Returns the list of virtual feats this
	 * companion bestows upon its master
	 * @return	ArrayList of virtual feats
	 **/
	public ArrayList getVFeatList()
	{
		return vFeatList;
	}

	public boolean hasVFeats()
	{
		final ArrayList vFeats = getVFeatList();
		return (vFeats != null) && (vFeats.size() > 0);
	}

	/**
	 * Adds to the virtual feat list this
	 * companion bestows upon its master.
	 * @param vList a | delimited list of feats to add to the list

	 */
	public void addVFeatList(String vList)
	{
		final StringTokenizer aTok = new StringTokenizer(vList, "|", false);
		while (aTok.hasMoreTokens())
		{
			if (vFeatList == null)
			{
				vFeatList = new ArrayList();
			}
			vFeatList.add(aTok.nextToken());
		}
	}

}

