/*
 *  pcgen - DESCRIPTION OF PACKAGE
 *  Copyright (C) 2004 Ross M. Lodge
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
 *  SaveModel.java
 *
 *  Created on Jan 16, 2004, 2:29:36 PM
 */
package plugin.initiative;

/**
 * <p>
 * This class models a saving throw; basically it models a save
 * as the name of the saving throw (fort/ref/will) and a bonus.
 * </p>
 * 
 * @author Ross M. Lodge
 *
 */
public class SaveModel extends PObjectModel
{
	
	/** Bonus segment position */
	private static final int SEGMENT_POSITION_BONUS = 1;
	
	/** Applicable save types */
	public static final String SAVE_TYPE_NONE = "NONE";
	public static final String SAVE_TYPE_FORTITUDE = "FORTITUDE";
	public static final String SAVE_TYPE_REFLEX = "REFLEX";
	public static final String SAVE_TYPE_WILL = "WILL";
	
	/** Bonus value */
	int m_bonus = 0;
	
	/** DC (not set by initialization */
	int m_dc = 0;
	
	/** Save type */
	String m_saveType = null;
	
	/**
	 * <p>
	 * Constructs a SaveModel with type NONE and +0 bonus.
	 * </p>
	 */
	public SaveModel()
	{
		this(SAVE_TYPE_NONE + "\\+0");
	}
	
	/**
	 * <p>
	 * Constructs a new save model based on a string.  The string should
	 * have the following tokens, in the following order, separated by
	 * backslashes:
	 * </p>
	 * 
	 * <ol>
	 * <li>FORTITUDE, REFLEX, or WILL</li>
	 * <li>|CHECK.XYZ.TOTAL|</li>
	 * </ol>
	 *
	 * @param objectString String description of skill
	 */
	public SaveModel(String objectString)
	{
		super(objectString);
		setSaveType(getName());
		setBonus(getInt(getStringValue(outputTokens, SEGMENT_POSITION_BONUS)));
	}

	/**
	 * <p>Gets the bonus value</p>
	 * 
	 * @return Returns the bonus.
	 */
	public int getBonus()
	{
		return m_bonus;
	}

	/**
	 * <p>Sets the bonus value</p>
	 * @param bonus The bonus to set.
	 */
	public void setBonus(int bonus)
	{
		m_bonus = bonus;
	}

	/**
	 * <p>Gets the dc</p>
	 * 
	 * @return Returns the dc.
	 */
	public int getDc()
	{
		return m_dc;
	}

	/**
	 * <p>Sets the dc</p>
	 * @param dc The dc to set.
	 */
	public void setDc(int dc)
	{
		m_dc = dc;
	}

	/**
	 * <p>Gets the save type</p>
	 * 
	 * @return Returns the saveType.
	 */
	public String getSaveType()
	{
		return m_saveType;
	}

	/**
	 * <p>Sets the save type</p>
	 * 
	 * @param saveType The saveType to set.
	 */
	public void setSaveType(String saveType)
	{
		if (SAVE_TYPE_FORTITUDE.startsWith(saveType.toUpperCase()))
		{
			m_saveType = SAVE_TYPE_FORTITUDE;
		}
		else if (SAVE_TYPE_REFLEX.startsWith(saveType.toUpperCase()))
		{
			m_saveType = SAVE_TYPE_REFLEX;
		}
		else if (SAVE_TYPE_WILL.startsWith(saveType.toUpperCase()))
		{
			m_saveType = SAVE_TYPE_WILL;
		}
		else
		{
			m_saveType = SAVE_TYPE_NONE;
		}
		if (!m_saveType.equals(getName()))
		{
			setName(m_saveType);
		}
	}

}
