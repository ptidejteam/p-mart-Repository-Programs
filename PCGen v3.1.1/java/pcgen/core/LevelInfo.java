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
 * $Id: LevelInfo.java,v 1.1 2006/02/21 00:05:26 vauchers Exp $
 */

package pcgen.core;

import java.math.BigDecimal;

/**
 * <code>LevelInfo</code> describes the data associated with a level
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public class LevelInfo
{
	private int level;
	private int minXP;
	private BigDecimal maxClassSkillRanks;
	private BigDecimal maxCrossClassSkillRanks;

	/**
	 * Default constructor for LevelInfo. Initialises everything to zero.
	 */
	public LevelInfo()
	{
		this.level = 0;
		this.minXP = 0;
		this.maxClassSkillRanks = new BigDecimal(0);
		this.maxCrossClassSkillRanks = new BigDecimal(0);
	}

	/**
	 * Retrieves the level that this LevelInfo object describes.
	 * @return The level
	 */
	public int getLevel()
	{
		return level;

	}

	/**
	 * Sets the level that this LevelInfo object describes.
	 * @param level The level to be set
	 */
	public void setLevel(int level)
	{
		this.level = level;
	}

	/**
	 * Retrieves the minimum number of experience points required to
	 * qualify for the level that this LevelInfo object describes.
	 * @return The experience point value
	 */
	public int getMinXP()
	{
		return minXP;
	}

	/**
	 * Sets the minimum number of experience points required to
	 * qualify for the level that this LevelInfo object describes.
	 * @param minXP The experience point value
	 */
	public void setMinXP(int minXP)
	{
		this.minXP = minXP;
	}

	/**
	 * Retrieves the maximum number of ranks a character can have in
	 * a class skill when they are at the level that this LevelInfo
	 * object describes.
	 * @return The maximum number of ranks allowed for a class skill
	 */
	public BigDecimal getMaxClassSkillRanks()
	{
		return maxClassSkillRanks;
	}

	/**
	 * Sets the maximum number of ranks a character can have in
	 * a class skill when they are at the level that this LevelInfo
	 * object describes.
	 * @param maxClassSkillRanks The maximum number of ranks allowed for
	 *              a class skill
	 */
	public void setMaxClassSkillRanks(BigDecimal maxClassSkillRanks)
	{
		this.maxClassSkillRanks = maxClassSkillRanks;
	}

	/**
	 * Retrieves the maximum number of ranks a character can have in
	 * a cross class skill when they are at the level that this LevelInfo
	 * object describes.
	 * @return The maximum number of ranks allowed for a cross-class skill
	 */
	public BigDecimal getMaxCrossClassSkillRanks()
	{
		return maxCrossClassSkillRanks;
	}

	/**
	 * Sets the maximum number of ranks a character can have in
	 * a cross-class skill when they are at the level that this LevelInfo
	 * object describes.
	 * @param maxCrossClassSkillRanks The maximum number of ranks allowed
	 *              for a cross-class skill
	 */
	public void setMaxCrossClassSkillRanks(BigDecimal maxCrossClassSkillRanks)
	{
		this.maxCrossClassSkillRanks = maxCrossClassSkillRanks;
	}

	/**
	 * Retrieves a human readable description of the details of this
	 * LevelInfo object.
	 * @return A string describing the LevelInfo object.
	 */
	public String toString()
	{
		return "Level: " + this.level + " MinXP: " + minXP + " MaxClassSkill: " + maxClassSkillRanks +
		  " MaxCrossClassSkill: " + maxCrossClassSkillRanks + ".";
	}

}
