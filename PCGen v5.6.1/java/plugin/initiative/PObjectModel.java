/*
 *  pcgen
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
 *  PObjectModel.java
 *
 *  Created on Nov 5, 2003, 2:58:55 PM
 */
package plugin.initiative;

/**
 * <p>
 * An abstract class used for the "model" classes for the plugins.  Basically
 * provides utility methods for the AttackModel, SkillModel, etc. classes.
 * </p>
 *
 * @author Ross M. Lodge
 */
public abstract class PObjectModel
{
	/** Constant for decoding incoming object strings */
	protected static final int SEGMENT_POSITION_NAME = 0;

	/** Constant for decoding incoming string types */
	private static final String TYPE_PREFIX_SKILL = "skill:";
	/** Constant for decoding incoming string types */
	private static final String TYPE_PREFIX_ATTACK = "attack:";
	/** Constant for decoding incoming string types */
	private static final String TYPE_PREFIX_SPELL = "spell:";

	protected String m_name = null;
	protected String[] outputTokens = null;

	/**
	 * <p>
	 * Constructs a new skill model based on a string. The string should
	 * generally contain output tokens separated by backslashes.
	 * </p>
	 * <p>
	 * The default implementation of this constructor is to split the incoming
	 * string and save the results to outputTokens.  It also assumes the
	 * name of the incoming object is the first token, and sets that value.
	 * </p>
	 *
	 * @param objectString
	 *            The string description of the object.
	 */
	public PObjectModel(String objectString)
	{
		outputTokens = objectString.split("\\\\");
		m_name = getStringValue(outputTokens,SEGMENT_POSITION_NAME);
	}

	/**
	 * <p>Provides an index-safe method of retrieving data
	 * from the array of strings generated by parsing the
	 * input weaponString.</p>
	 *
	 * @param values Array of strings
	 * @param index Index to get from array
	 * @return The requested string entry, or ""
	 */
	protected String getStringValue(String[] values, int index)
	{
		String returnValue = "";
		if (values.length > index)
		{
			returnValue = values[index];
		}
		return returnValue;
	}

	/**
	 * <p>
	 * A save conversion from string to int.  This avoids NumberFormatExceptions,
	 * and also removes pluses from the incoming values.
	 * </p>
	 *
	 * @param value
	 * 			String value to interpret.
	 * @return
	 * 			The integer conversion of the incoming string
	 */
	protected static int getInt(String value)
	{
		int returnValue = 0;
		try
		{
			if (value.startsWith("+"))
			{
				returnValue = Integer.parseInt(value.substring(1));
			}
			else
			{
				returnValue = Integer.parseInt(value);
			}
		}
		catch (NumberFormatException e)
		{
			//Do Nothing
		}
		return returnValue;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return getName();
	}

	/**
	 * <p>Gets the value of name</p>
	 * @return Returns the name.
	 */
	public String getName()
	{
		return m_name;
	}

	/**
	 * <p>Sets the value of name</p>
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		m_name = name;
	}

	/**
	 * <p>
	 * A factory method that tries to determine what kind of object the string represents
	 * and generate the appropriate subclass.
	 * </p>
	 *
	 * @param objectString
	 * 			An appropriate object string, including the type prefix.
	 * @return
	 * 			A new instance of a PObjectModel subclass.
	 */
	public static PObjectModel Factory(String objectString)
	{
		PObjectModel returnValue = null;
		if (objectString != null)
		{
			if (objectString.startsWith(TYPE_PREFIX_SKILL))
			{
				returnValue = new SkillModel(objectString.substring(TYPE_PREFIX_SKILL.length()));
			}
			else if (objectString.startsWith(TYPE_PREFIX_ATTACK))
			{
				returnValue = new AttackModel(objectString.substring(TYPE_PREFIX_ATTACK.length()));
			}
			else if (objectString.startsWith(TYPE_PREFIX_SPELL))
			{
				returnValue = new SpellModel(objectString.substring(TYPE_PREFIX_SPELL.length()));
			}
		}
		return returnValue;
	}

}
