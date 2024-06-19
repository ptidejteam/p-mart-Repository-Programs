/*
 * PCLevelInfo.java
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
 * Created on November 29, 2002, 10:38 PM
 *
 * $Id: PCLevelInfo.java,v 1.1 2006/02/21 01:18:57 vauchers Exp $
 */

package pcgen.core.pclevelinfo;

import java.util.ArrayList;
import java.util.List;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.util.Logging;

/**
 * ???
 *
 * @author byngl
 * @version $Revision: 1.1 $
 */

public final class PCLevelInfo
{
	private String classKeyName = "";
	//private int skillPoints = 0;
	//private int feats = 0;
	//private List skillsLearned = null;
	//private List featsTaken = null;
	private List statsPostModified = null;
	private List statsPreModified = null;
	private int skillPointsGained = 0;
	private int skillPointsRemaining = 0;
	private int level = 0;

	public PCLevelInfo(final String argClassKeyName)
	{
		super();
		classKeyName = argClassKeyName;
	}

	public String getClassKeyName()
	{
		return classKeyName;
	}

	public void setClassKeyName(final String argClassKeyName)
	{
		classKeyName = argClassKeyName;
	}

	public void addModifiedStat(final String statAbb, final int mod, final boolean isPreMod)
	{
		List statList;
		if (isPreMod)
		{
			if (statsPreModified == null)
			{
				statsPreModified = new ArrayList();
			}
			statList = statsPreModified;
		}
		else
		{
			if (statsPostModified == null)
			{
				statsPostModified = new ArrayList();
			}
			statList = statsPostModified;
		}

		PCLevelInfoStat aStat;
		for (int i = 0; i < statList.size(); ++i)
		{
			aStat = (PCLevelInfoStat) statList.get(i);
			if (statAbb.equals(aStat.getStatAbb()))
			{
				aStat.modifyStat(mod);
				if (aStat.getStatMod() == 0)
				{
					statList.remove(aStat);
				}
				return;
			}
		}
		statList.add(new PCLevelInfoStat(statAbb, mod));
	}

	public List getModifiedStats(final boolean preMod)
	{
		List result = statsPostModified;
		if (preMod)
		{
			result = statsPreModified;
		}
		return result;
	}

	public int getTotalStatMod(final String statAbb, final boolean includePost)
	{
		int mod = 0;
		if (statsPreModified != null)
		{
			for (int i = 0; i < statsPreModified.size(); ++i)
			{
				if (((PCLevelInfoStat) statsPreModified.get(i)).getStatAbb().equals(statAbb))
				{
					mod += ((PCLevelInfoStat) statsPreModified.get(i)).getStatMod();
				}
			}
		}
		if (includePost && (statsPostModified != null))
		{
			for (int i = 0; i < statsPostModified.size(); ++i)
			{
				if (((PCLevelInfoStat) statsPostModified.get(i)).getStatAbb().equals(statAbb))
				{
					mod += ((PCLevelInfoStat) statsPostModified.get(i)).getStatMod();
				}
			}
		}
		return mod;
	}

	public int getSkillPointsGained()
	{
		return getSkillPointsGained(true);
	}

	public int getSkillPointsGained(boolean addBonus)
	{
		// If this information in not saved on PCG, then try to recalc it
		if (skillPointsGained == 0 && classKeyName.length() > 0)
		{
			PCClass aClass = Globals.getClassKeyed(classKeyName);
			skillPointsGained = aClass.recalcSkillPointMod(Globals.getCurrentPC(), level);
		}
		if (addBonus)
		{
			return skillPointsGained + getBonusSkillPool();
		}
		else
		{
			return skillPointsGained;
		}
	}

	public void setSkillPointsGained(int arg)
	{
		skillPointsGained = arg;
	}

	private int getBonusSkillPool()
	{
		int returnValue = 0;
		PCClass aClass = Globals.getCurrentPC().getClassKeyed(classKeyName);
		if (aClass != null)
		{
			// These bonuses apply to the level or higher.
			// We have to add and then remove the previous to get the effective level bonus
			returnValue += (int) aClass.getBonusTo("SKILLPOOL", "NUMBER", level);
			returnValue -= (int) aClass.getBonusTo("SKILLPOOL", "NUMBER", level - 1);
		}
		if (level == 1)
		{
			returnValue += (int) Globals.getCurrentPC().getTotalBonusTo("SKILLPOOL", "CLASS." + classKeyName);
		}
		returnValue += (int) Globals.getCurrentPC().getTotalBonusTo("SKILLPOOL", "CLASS." + classKeyName + ";LEVEL." + Integer.toString(level));
		return returnValue;
	}
	
	public int getSkillPointsRemaining()
	{
		return getSkillPointsRemaining(true);
	}

	public int getSkillPointsRemaining(boolean addBonus)
	{
		if (addBonus)
		{
			return skillPointsRemaining + getBonusSkillPool();
		}
		else
		{
			return skillPointsRemaining;
		}
	}

	public void setSkillPointsRemaining(int arg)
	{
		setSkillPointsRemaining(arg, true);
	}

	public void setSkillPointsRemaining(int arg, boolean delBonus)
	{
		skillPointsRemaining = arg;
		if (delBonus)
		{
			skillPointsRemaining -= getBonusSkillPool();
		}
	}

	public void setLevel(int arg)
	{
		level = arg;
	}

	public int getLevel()
	{
		return level;
	}

}
