/*
 * LevelInfo.java
 * Copyright 2002 (C) James Dempsey
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
 * Created on August 16, 2002, 10:00 PM AEST (+10:00)
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:33:15 $
 *
 */
package pcgen.core;

import java.math.BigDecimal;
import pcgen.util.BigDecimalHelper;
import pcgen.util.StringReplaceAll;

/**
 * <code>LevelInfo</code> describes the data associated with a level
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class LevelInfo
{
	private String maxClassSkillString = "0";
	private String maxCrossClassSkillString= "0";
	private String levelString = "0";
	private String minXPString = "0";

	/**
	 * Default constructor for LevelInfo. Initialises everything to zero.
	 */
	public LevelInfo()
	{
	}

	/**
	 * Sets the levelString that this LevelInfo object describes.
	 * @param arg The level to be set (should be a number or variable)
	 */
	public void setLevelString(String arg)
	{
		this.levelString = arg;
	}
	
	/**
	 * Gets the levelString that this LevelInfo object describes.
	 */
	public String getLevelString()
	{
		return levelString;
	}

	/**
	 * Sets the maximum number of ranks a character can have in
	 * a class skill when they are at the level that this LevelInfo
	 * object describes.
	 * @param arg The maximum number of ranks allowed for a class skill
	 *				should be a number or a formula based on variable defined
	 *				in setLevelString.
	 */
	public void setMaxClassSkillString(String arg)
	{
		this.maxClassSkillString = arg;
	}

	/**
	 * Sets the maximum number of ranks a character can have in
	 * a cross-class skill when they are at the level that this LevelInfo
	 * object describes.
	 * @param arg The maximum number of ranks allowed for a cross-class skill
	 *				should be a number or a formula based on variable defined
	 *				in setLevelString.
	 */
	public void setMaxCrossClassSkillString(String arg)
	{
		this.maxCrossClassSkillString = arg;
	}

	/**
	 * Sets the min number of experience points required to
	 * qualify for the level that this LevelInfo object describes
	 * @param arg The amount of experience needed to acquire this level
	 *				should be a number or a formula based on variable defined
	 *				in setLevelString.
	 **/
	public void setMinXPString(String arg)
	{
		this.minXPString = arg;
	}

	/**
	 * Retrieves a human readable description of the details of this
	 * LevelInfo object.
	 * @return A string describing the LevelInfo object.
	 */
	public String toString()
	{
		return "Level: " + this.levelString + " MinXP: " + minXPString + " MaxClassSkill: " + maxClassSkillString
		+ " MaxCrossClassSkill: " + maxCrossClassSkillString + ".";
	}

	/**
	 * Retrieves the min number of experience points required to
	 * qualify for the level that this LevelInfo object describes
	 * @return    XP point value
	 **/
	public int getMinXP(int levelArg)
	{
		int level = 0;
		int xp = 0;
		final PlayerCharacter calcPC = Globals.getCurrentPC();
		if (calcPC == null)
			return 0;
		try
		{
			level = Integer.parseInt(levelString);
			xp = calcPC.getVariableValue(minXPString, "").intValue();
		}
		catch (Exception e)
		{
			level = levelArg;
			String minString = StringReplaceAll.replaceAll(minXPString, levelString, String.valueOf(levelArg));
			xp = calcPC.getVariableValue(minString, "").intValue();
		}
		
		return xp;
	}

	/**
	 * Retrieves the maximum number of ranks a character can have in
	 * a class skill when they are at the level that this LevelInfo
	 * object describes.
	 * @return The maximum number of ranks allowed for a class skill
	 */
	public BigDecimal getMaxClassSkillRank(int levelArg)
	{
		int level = 0;
		double ranks = 0;
		final PlayerCharacter calcPC = Globals.getCurrentPC();
		if (calcPC == null)
			return BigDecimalHelper.ZERO;
		try
		{
			level = Integer.parseInt(levelString);
			ranks = calcPC.getVariableValue(maxClassSkillString, "").doubleValue();
		}
		catch (Exception e)
		{
			level = levelArg;
			String rankString = StringReplaceAll.replaceAll(maxClassSkillString, levelString, String.valueOf(levelArg));
			ranks = calcPC.getVariableValue(rankString, "").doubleValue();
		}
		
		return new BigDecimal(ranks);
	}
	
	/**
	 * Retrieves the maximum number of ranks a character can have in
	 * a cross class skill when they are at the level that this LevelInfo
	 * object describes.
	 * @return The maximum number of ranks allowed for a cross-class skill
	 */
	public BigDecimal getMaxCrossClassSkillRank(int levelArg)
	{
		int level = 0;
		double ranks = 0;
		final PlayerCharacter calcPC = Globals.getCurrentPC();
		if (calcPC == null)
			return BigDecimalHelper.ZERO;
		try
		{
			level = Integer.parseInt(levelString);
			ranks = calcPC.getVariableValue(maxCrossClassSkillString, "").doubleValue();
		}
		catch (Exception e)
		{
			level = levelArg;
			String rankString = StringReplaceAll.replaceAll(maxCrossClassSkillString, levelString, String.valueOf(levelArg));
			ranks = calcPC.getVariableValue(rankString, "").doubleValue();
		}
		
		return new BigDecimal(ranks);
	}
}