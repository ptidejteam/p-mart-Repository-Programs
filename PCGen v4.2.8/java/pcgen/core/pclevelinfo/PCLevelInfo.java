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
 * $Id: PCLevelInfo.java,v 1.1 2006/02/21 01:00:32 vauchers Exp $
 */

package pcgen.core.pclevelinfo;

import java.util.ArrayList;

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
	//private ArrayList skillsLearned = null;
	//private ArrayList featsTaken = null;
	private ArrayList statsPostModified = null;
	private ArrayList statsPreModified = null;

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
		ArrayList statList;
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

	public ArrayList getModifiedStats(final boolean preMod)
	{
		if (preMod)
		{
			return statsPreModified;
		}
		return statsPostModified;
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
}

