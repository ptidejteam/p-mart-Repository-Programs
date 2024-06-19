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
 * Last Edited: $Date: 2006/02/21 01:33:33 $
 *
 *************************************************************************/

package pcgen.core.character;

import java.util.HashMap;
import java.util.Map;
import pcgen.core.PObject;

/**
 * <code>CompanionMod</code>.
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class CompanionMod extends PObject implements Comparable
{
	private final Map classMap = new HashMap();
	private final Map varMap = new HashMap();
	private final Map switchRaceMap = new HashMap();
	private String type = "";
	private int level = 0;
	private int nHD = 0;
	private String masterHPFormula = null;
	private String masterBABFormula = null;
	private String masterCheckFormula = null;
	private boolean useMasterSkill = false;

	/**
	 * Compares classMap, type and level.
	 * @param obj the CompanionMod to compare with
	 * @return a negative integer, zero, or a positive integer as this object
	 *         is less than, equal to, or greater than the specified object.
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(Object obj)
	{
		int result = 0;
		if (obj instanceof CompanionMod)
		{
			final CompanionMod aComp = (CompanionMod) obj;
			if (classMap.entrySet().equals(aComp.getClassMap().entrySet()) && (type.equals(aComp.getType())) && (level == aComp.getLevel()))
			{
				result = 1;
			}
		}
		return result;
	}

	/**
	 * Compares classMap, type and level
	 */
	public boolean equals(final Object obj)
	{
		boolean result = false;
		if (obj instanceof CompanionMod)
		{
			final CompanionMod aComp = (CompanionMod) obj;
			if (classMap.entrySet().equals(aComp.getClassMap().entrySet()) && (type.equals(aComp.getType())) && (level == aComp.getLevel()))
			{
				result = true;
			}
		}
		return result;
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

	public Map getVarMap()
	{
		return varMap;
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
		int result = -1;
		if (classMap.get(cName) != null)
		{
			result = Integer.parseInt(classMap.get(cName).toString());
		}
		else if (varMap.get(cName) != null)
		{
			result = Integer.parseInt(varMap.get(cName).toString());
		}
		return result;
	}

	public void setLevel(int x)
	{
		level = x;
	}

	public int getHitDie()
	{
		return nHD;
	}

	public void setHitDie(int x)
	{
		nHD = x;
	}

	public boolean getUseMasterSkill()
	{
		return useMasterSkill;
	}

	public void setUseMasterSkill(boolean x)
	{
		useMasterSkill = x;
	}

	public String getCopyMasterHP()
	{
		return masterHPFormula;
	}

	public void setCopyMasterHP(String x)
	{
		masterHPFormula = x;
	}

	public String getCopyMasterBAB()
	{
		return masterBABFormula;
	}

	public void setCopyMasterBAB(String x)
	{
		masterBABFormula = x;
	}

	public String getCopyMasterCheck()
	{
		return masterCheckFormula;
	}

	public void setCopyMasterCheck(String x)
	{
		masterCheckFormula = x;
	}
}

