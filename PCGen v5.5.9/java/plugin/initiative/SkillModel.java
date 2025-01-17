/*
 *  gmgen.plugin - GMGEN Project
 *  Copyright (C) 2003 Ross M. Lodge
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 *  SkillModel.java
 *
 *  Created on Nov 5, 2003, 2:48:50 PM
 */
package plugin.initiative;

/**
 * <p>This class models a skill.  It basically represents a skill as a name and
 * total bonus.</p>
 *
 * @author Ross M. Lodge
 */
public class SkillModel extends PObjectModel
{
	String m_name = null;
	int m_bonus = 0;

	/** Constant for decoding incoming skill strings */
	private static final int SEGMENT_POSITION_NAME = 0;
	/** Constant for decoding incoming skill strings */
	private static final int SEGMENT_POSITION_BONUS = 1;

	/**
	 * <p>Constructs a new skill model based on a string.  The string should
	 * have the following tokens, in the following order, separated by
	 * backslashes:</p>
	 * <ol>
	 * <li>|SKILL.%skill|</li>
	 * <li>|SKILL.%skill.TOTAL|</li>
	 * </ol>
	 *
	 * @param objectString String description of skill
	 */
	public SkillModel(String objectString)
	{
		super(objectString);
		setBonus(getInt(getStringValue(outputTokens,SEGMENT_POSITION_BONUS)));
	}

	/**
	 * <p>Gets the value of bonus</p>
	 * @return Returns the bonus.
	 */
	public int getBonus()
	{
		return m_bonus;
	}

	/**
	 * <p>Sets the value of bonus</p>
	 * @param bonus The bonus to set.
	 */
	public void setBonus(int bonus)
	{
		m_bonus = bonus;
	}

}
