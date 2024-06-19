/*
 * BonusObj.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on December 13, 2002, 9:19 AM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:27:55 $
 *
 */

package pcgen.core.bonus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.utils.Utility;

/**
 * <code>BonusObj</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 **/
public abstract class BonusObj implements Serializable
{
	private List prereqList = null;
	private int typeOfBonus = Bonus.BONUS_UNDEFINED;
	private String bonusName = "";
	private String bonusType = "";
	private Object bonusValue = null;
	private List bonusInfo = new LinkedList();
	private String choiceString = "";
	private Object creatorObj = null;
	private Object targetObj = null;
	private boolean isApplied = false;
	private int pcLevel = -1;
	private String varPart = "";
	private boolean valueIsStatic = true;
	private Map dependMap = new HashMap();

	public String toString()
	{
		final StringBuffer sb = new StringBuffer(50);
		if (pcLevel >= 0)
		{
			sb.append(pcLevel).append('|');
		}
		sb.append(getTypeOfBonus());
		if (bonusInfo.size() > 0)
		{
			for (int i = 0; i < bonusInfo.size(); ++i)
			{
				sb.append(i == 0 ? '|' : ',').append(unparseToken(bonusInfo.get(i)));
			}
		}
		else
		{
			sb.append("|ERROR");
		}

		if (bonusValue != null)
		{
			sb.append('|').append(bonusValue.toString());
		}

		if (prereqList != null)
		{
			for (int i = 0; i < prereqList.size(); ++i)
			{
				sb.append('|').append(prereqList.get(i));
			}
		}
		if (bonusType.length() != 0)
		{
			sb.append("|TYPE=").append(bonusType);
		}
		return sb.toString();
	}

	boolean parseToken(final String token)
	{
		return false;
	}

	String unparseToken(final Object obj)
	{
		return "";
	}

	void setBonusName(final String aName)
	{
		bonusName = aName;
	}

	void setTypeOfBonus(final int type)
	{
		typeOfBonus = type;
	}

	boolean addType(final String typeString)
	{
		if (bonusType.length() == 0)
		{
			bonusType = typeString.toUpperCase();
			return true;
		}
		return false;
	}

	void addPreReq(final String prereqString)
	{
		if (prereqList == null)
		{
			prereqList = new LinkedList();
		}
		if (!prereqList.contains(prereqString.toUpperCase()))
		{
			prereqList.add(prereqString.toUpperCase());
		}
	}

	void addBonusInfo(final Object obj)
	{
		bonusInfo.add(obj);
	}

	private void buildDependMap(String aString)
	{
		// First wack out all the () pairs to find variable names
		while (aString.lastIndexOf('(') >= 0)
		{
			int x = Utility.innerMostStringStart(aString);
			int y = Utility.innerMostStringEnd(aString);
			if (y < x)
			{
				return;
			}
			String bString = aString.substring(x + 1, y);
			buildDependMap(bString);
			aString = aString.substring(0, x) + aString.substring(y + 1);
		}
		if ((aString.indexOf("(") >= 0) ||
			(aString.indexOf(")") >= 0) ||
			(aString.indexOf("%") >= 0))
		{
			return;
		}
		// We now have the substring we want to work on
		StringTokenizer cTok = new StringTokenizer(aString, ".");
		while (cTok.hasMoreTokens())
		{
			String controlString = cTok.nextToken();
			// skip flow control tags
			if (controlString.equals("IF") ||
				controlString.equals("THEN") ||
				controlString.equals("ELSE") ||
				controlString.equals("GT") ||
				controlString.equals("GTEQ") ||
				controlString.equals("EQ") ||
				controlString.equals("LTEQ") ||
				controlString.equals("LT"))
			{
				continue;
			}
			// Now remove math strings: + - / *
			// remember, a StringTokenizer will tokenize
			// on any of the found delimiters
			StringTokenizer mTok = new StringTokenizer(controlString, "+-/*");
			while (mTok.hasMoreTokens())
			{
				String newString = mTok.nextToken();
				String testString = newString;
				boolean found = false;
				// now Check for MIN or MAX
				while (!found)
				{
					if (newString.indexOf("MAX") >= 0)
					{
						testString = newString.substring(0, newString.indexOf("MAX"));
						newString = newString.substring(newString.indexOf("MAX") + 3);
					}
					else if (newString.indexOf("MIN") >= 0)
					{
						testString = newString.substring(0, newString.indexOf("MIN"));
						newString = newString.substring(newString.indexOf("MIN") + 3);
					}
					else
					{
						found = true;
					}
					// check to see if it's a number
					try
					{
						Float.parseFloat(testString);
					}
					catch (NumberFormatException e)
					{
						// It's a Variable!
						if (testString.length() > 0)
						{
							dependMap.put(testString, "1");
						}
					}
				}
			}
		}
	}


	////////////////////////////////////////////////
	//        Public Accessors and Mutators       //
	////////////////////////////////////////////////

	public String getTypeOfBonus()
	{
		return Bonus.bonusTags[typeOfBonus];
	}

	public String getBonusName()
	{
		return bonusName;
	}

	public String getBonusInfo()
	{
		final StringBuffer sb = new StringBuffer(50);
		if (bonusInfo.size() > 0)
		{
			for (int i = 0; i < bonusInfo.size(); ++i)
			{
				sb.append(i == 0 ? "" : ",").append(unparseToken(bonusInfo.get(i)));
			}
		}
		else
		{
			sb.append("|ERROR");
		}
		return sb.toString().toUpperCase();
	}

	public List getBonusInfoList()
	{
		return bonusInfo;
	}

	public String getValue()
	{
		return bonusValue.toString();
	}

	public double getValueAsdouble()
	{
		return Double.parseDouble(bonusValue.toString());
	}

	public void setValue(final String bValue)
	{
		try
		{
			bonusValue = new Integer(bValue);
		}
		catch (NumberFormatException e1)
		{
			try
			{
				bonusValue = new Float(bValue);
			}
			catch (Exception e2)
			{
				bonusValue = bValue.toUpperCase();
				valueIsStatic = false;
				buildDependMap(bValue.toUpperCase());
			}
		}
	}

	public boolean isValueStatic()
	{
		return valueIsStatic;
	}

	public List getPrereqList()
	{
		return prereqList;
	}

	public String getPrereqString()
	{
		final StringBuffer sb = new StringBuffer(50);
		if (prereqList != null)
		{
			for (int i = 0; i < prereqList.size(); ++i)
			{
				sb.append(i == 0 ? "" : "|").append(prereqList.get(i));
			}
		}
		return sb.toString().toUpperCase();
	}

	public boolean hasPreReqs()
	{
		return (prereqList != null);
	}

	public String getTypeString()
	{
		return bonusType;
	}

	public boolean hasTypeString()
	{
		return (bonusType.length() > 0);
	}

	public void setVariable(final String aString)
	{
		varPart = aString.toUpperCase();
	}

	public String getVariable()
	{
		return varPart;
	}

	public boolean hasVariable()
	{
		return (varPart.length() > 0);
	}

	public void setPCLevel(int anInt)
	{
		pcLevel = anInt;
	}

	public int getPCLevel()
	{
		return pcLevel;
	}

	public void setChoiceString(final String aString)
	{
		choiceString = aString.toUpperCase();
	}

	/**
	 * Once PObject is converted from using bonusTo() to using
	 * BonusObj, then this function will be required. -- JSC 8/20/03
	 **/
	public String getChoiceString()
	{
		return choiceString;
	}

	public void setApplied(final boolean aBool)
	{
		isApplied = aBool;
	}

	public boolean isApplied()
	{
		return isApplied;
	}

	public void setCreatorObject(Object anObj)
	{
		creatorObj = anObj;
	}

	public Object getCreatorObject()
	{
		return creatorObj;
	}

	public void setTargetObject(Object anObj)
	{
		targetObj = anObj;
	}

	public Object getTargetObject()
	{
		return targetObj;
	}

	public boolean getDependsOn(String aString)
	{
		return dependMap.containsKey(aString);
	}

	/**
	 * Returns a String which can be used to display in the GUI
	 **/
	public String getName()
	{
		StringBuffer b = new StringBuffer();
		if (creatorObj instanceof PlayerCharacter)
		{
			b.append(((PlayerCharacter) creatorObj).getName());
		}
		else if (creatorObj instanceof PObject)
		{
			b.append(creatorObj.toString());
		}
		else
		{
			b.append("NONE");
		}
		b.append(" [");
		if (targetObj instanceof PlayerCharacter)
		{
			b.append("PC");
		}
		else if (targetObj instanceof Equipment)
		{
			b.append(((Equipment) targetObj).getName());
		}
		else
		{
			b.append("NONE");
		}
		b.append("]");
		return b.toString();
	}

}
