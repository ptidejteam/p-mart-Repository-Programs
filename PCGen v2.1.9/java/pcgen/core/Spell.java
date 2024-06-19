/*
 * Spell.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
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
 * Created on April 21, 2001, 2:15 PM
 */

package pcgen.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/**
 * <code>Spell</code> creates a new tabbed panel.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
//public class Spell extends PObject implements Map

public class Spell extends PObject
{
/*  public static final String[] ATTRIBUTE_NAMES =
  {
    "School",
    "Subschool",
    "Class Levels",
    "Components Lists",
    "Casting Time",
    "Range",
    "Effect",
    "Effect Type",
    "Duration",
    "Save Info",
    "SR",
    "Spell Books",
    "Times",
    "Descriptor List"
  };

  private Map map;
  }
*/
	private String school = new String();
	private String subschool = new String();
	private String classLevels = new String();
	private String componentList = new String();
	private String castingTime = new String();
	private String range = new String();
	private String effect = new String();
	private String effectType = new String();
	private String duration = new String();
	private String saveInfo = new String();
	private String SR = new String();
	private ArrayList spellBooks = new ArrayList();
	private ArrayList times = new ArrayList();
	private ArrayList descriptorList = new ArrayList();
	private int level = 0;
	private String stat = "";

	public Object clone()
	{
		Spell aSpell = (Spell)super.clone();
		aSpell.setSchool(school);
		aSpell.setSubschool(subschool);
		aSpell.isSpecified = isSpecified;
		aSpell.descriptorList = (ArrayList)descriptorList.clone();
		aSpell.spellBooks = (ArrayList)spellBooks.clone();
		aSpell.stat = stat;
		return aSpell;
	}

	public String toString()
	{
		if (getTimes().size() > 0)
		{
			final int anInt = ((Integer)getTimes().get(0)).intValue();
			if (anInt > 1)
				return name + " " + String.valueOf(anInt) + "x";
		}
		return name;
	}

	public List getDescriptorList()
	{
		return descriptorList;
	}

	public String info()
	{
		return "SCHOOL:" + getSchool() + " SUB:" + getSubschool() +
			" COMP:" + getComponentList() + " CAST:" + getCastingTime() +
			" RANGE:" + getRange() + " EFFECT:" + getEffect() +
			" TYPE:" + getEffectType() + " SAVE:" + getSaveInfo() +
			" SR:" + getSR();
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int anInt)
	{
		level = anInt;
	}

	public String getSchool()
	{
		return school;
	}

	private void setSchool(String aString)
	{
		school = aString;
	}

	public String getSubschool()
	{
		return subschool;
	}

	private void setSubschool(String aString)
	{
		subschool = aString;
	}

	public String getClassLevels()
	{
		return classLevels;
	}

	public void setClassLevels(String aString)
	{
		if (!aString.equals("NONE"))
			classLevels = aString;
	}

	public String getComponentList()
	{
		return componentList;
	}

	private void setComponentList(String aString)
	{
		componentList = aString;
	}

	public String getCastingTime()
	{
		return castingTime;
	}

	private void setCastingTime(String aString)
	{
		castingTime = aString;
	}

	public String getRange()
	{
		return range;
	}

	private void setRange(String aString)
	{
		range = aString;
	}

	public String getEffect()
	{
		return effect;
	}

	private void setEffect(String aString)
	{
		effect = aString;
	}

	public String getEffectType()
	{
		return effectType;
	}

	private void setEffectType(String aString)
	{
		effectType = aString;
	}

	public String getDuration()
	{
		return duration;
	}

	private void setDuration(String aString)
	{
		duration = aString;
	}

	public String getSaveInfo()
	{
		return saveInfo;
	}

	private void setSaveInfo(String aString)
	{
		saveInfo = aString;
	}

	public String getSR()
	{
		return SR;
	}

	private void setSR(String aString)
	{
		SR = aString;
	}

	public boolean isInSpecialty(Collection specialtyList)
	{
		// specialty may be at school or descriptor level
		if (specialtyList.contains(school))
			return true;
		for (int j = 0; j < descriptorList.size(); j++)
			if (specialtyList.contains((String)descriptorList.get(j)))
				return true;
		return false;
	}

	public boolean isProhibited(String prohibitedString)
	{
		final StringTokenizer aTok = new StringTokenizer(prohibitedString, ",", false);
		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			if (aString.equals(school) || descriptorList.contains(aString))
			{
				return true;
			}
		}
		return false;
	}
	// returning -1 means it isn't in the list of className
	public int levelForClass(String className)
	{
		return levelForClass(className, className);
	}

	public int levelForClass(String className, String realName)
	{
		if (!passesPreReqTests())
			return -1;
		PCClass aClass = null;
		if (Globals.getCurrentPC() != null)
		{
			aClass = Globals.getCurrentPC().getClassNamed(realName);
			if (aClass == null)
				aClass = Globals.getCurrentPC().getClassNamed(className);
			if (aClass != null)
			{
				// if the spell falls into a specialty , accept it (some spells may fall into both
				//   the specialty and prohibited list, so check specialty first)
				if (!isInSpecialty(aClass.getSpecialtyList()) && isProhibited(aClass.getProhibitedString()))
					return -1;
			}
		}
		StringTokenizer aTok = new StringTokenizer(classLevels, ",", false);
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			if (!aString.equals("NONE"))
			{
				if (aTok.hasMoreTokens())
				{
					try
					{
						final int spellLevel = Integer.parseInt(aTok.nextToken());
						if (aString.equals(className) || (aClass != null && aString.equals(aClass.getSubClassName())))
						{
							if (aClass != null && stat.length() > 0 && aClass.getSpellBaseStat().equals("SPELL"))
							{
								final int index = s_STATNAMES.lastIndexOf(stat);
								if (index >= 0 && Globals.getCurrentPC().adjStats(index / 3) < 10 + spellLevel)
									return -1;
							}
							return spellLevel;
						}
						else if (className.startsWith("Domain") && className.length() > 6)
						{
							final StringTokenizer bTok =
								new StringTokenizer(className.substring(6), "(),", false);
							while (bTok.hasMoreTokens())
								if (bTok.nextToken().trim().equals(aString))
									return spellLevel;
						}
					}
					catch (NumberFormatException nfe)
					{
						System.out.println("Spell named \"" + name +
							"\" has bad spell level info: " +
							classLevels);
					}
				}
				else
				{
					System.out.println("Spell named \"" + name +
						"\" has bad spell level info: " +
						classLevels);
				}
			}
		}
		return -1;
	}

	public List getSpellBooks()
	{
		return spellBooks;
	}

	public List getTimes()
	{
		return times;
	}

	public void selectSpellBook(String aString)
	{
		final int i = getSpellBooks().indexOf(aString);
		if (i > 0)
		{
			spellBooks.remove(i);
			getSpellBooks().add(0, aString);
			Integer aTime = (Integer)getTimes().get(i);
			getTimes().remove(i);
			times.add(0, aTime);
		}
	}

	public void addToSpellBook(String bookName, boolean allowMults)
	{
		final int i = getSpellBooks().indexOf(bookName);
		if (i >= 0)
		{
			final int j = ((Integer)times.get(i)).intValue() + 1;
			if (j > 1 && allowMults && !bookName.equals("Known Spells"))
			{
				getTimes().set(i, new Integer(j));
			}
		}
		else
		{
			getSpellBooks().add(bookName);
			times.add(new Integer(1));
		}

	}

	public void removeFromSpellBook(String bookName)
	{
		final int i = getSpellBooks().indexOf(bookName);
		if (i >= 0)
		{
			final Integer anInt = (Integer)times.get(i);
			final int j = anInt.intValue() - 1;
			getTimes().remove(i);
			if (j > 0)
				getTimes().add(i, new Integer(j));
			else
				spellBooks.remove(i);
		}
		spellBooks.trimToSize();
	}


	public String descriptor()
	{
		StringBuffer retVal = new StringBuffer();
		for (Iterator i = descriptorList.iterator(); i.hasNext();)
		{
			final String aString = (String)i.next();
			if (retVal.length() > 0)
				retVal.append(", ");
			retVal.append(aString);
		}
		return retVal.toString();
	}

	public Integer timesForSpellBook(String bookName)
	{
		final int i = getSpellBooks().indexOf(bookName);
		if (i >= 0)
			return (Integer)times.get(i);
		return new Integer(0);
	}

	public void addDescriptors(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
			descriptorList.add(aTok.nextToken());
	}

	public void parseLine(String inputLine, File sourceFile, int lineNum)
	{
		final String tabdelim = "\t";
		int i = 0;
		final StringTokenizer colToken = new StringTokenizer(inputLine, tabdelim, false);
		while (colToken.hasMoreElements())
		{
			final String aString = colToken.nextToken();
			if (super.parseTag(aString))
				continue;
			final int aLen = aString.length();
			if ((aLen > 11) && aString.startsWith("DESCRIPTOR"))
			{
				addDescriptors(aString.substring(11));
				continue;
			}
			else if ((aLen > 4) && aString.startsWith("KEY:"))
			{
				setKeyName(aString.substring(4));
				continue;
			}
			else if (aString.startsWith("PRE"))
			{
				preReqArrayList.add(aString);
				continue;
			}
			else if (aString.startsWith("STAT:") && aString.length() > 5)
			{
				stat = aString.substring(5);
				continue;
			}
			i++;
			switch (i)
			{
				case 1:
					setName(aString);
					break;
				case 2:
					setSchool(aString);
					break;
				case 3:
					setSubschool(aString);
					break;
				case 4:
					setClassLevels(aString);
					break;
				case 5:
					setComponentList(aString);
					break;
				case 6:
					setCastingTime(aString);
					break;
				case 7:
					setRange(aString);
					break;
				case 8:
					setEffect(aString);
					break;
				case 9:
					setEffectType(aString);
					break;
				case 10:
					setDuration(aString);
					break;
				case 11:
					setSaveInfo(aString);
					break;
				case 12:
					setSR(aString);
					break;
				default:
					JOptionPane.showMessageDialog
						(null, "Illegal spell info " +
						sourceFile.getName() + ":" + Integer.toString(lineNum) +
						" \"" + aString + "\"", "PCGen", JOptionPane.ERROR_MESSAGE);
			}
		}
		if (i < 12)
		{
			JOptionPane.showMessageDialog(null, "Expected more fields in " + /*PCGen.currentFile+":"+PCGen.lineNum.toString()+*/ " (" + inputLine + ")", "PCGen", JOptionPane.ERROR_MESSAGE);
		}
	}

	public Spell()
	{
		super();
/*    int i=0;
    map = new HashMap(ATTRIBUTE_NAMES.length);
    for(i=0;i<11;i++)
        map.put(ATTRIBUTE_NAMES[i], new String());
    while(i<ATTRIBUTE_NAMES.length)
        map.put(ATTRIBUTE_NAMES[i++], new ArrayList());
*/
	}
}
