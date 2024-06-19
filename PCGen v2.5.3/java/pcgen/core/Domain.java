/*
 * Domain.java
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * <code>Domain</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class Domain extends PObject
{
	private String grantedPower = "";
	private String skillList = "";
	private String featList = "";
	private String spellList = "";
	private boolean isLocked = false;
	private String specialAbility = "";

	/**
	 * @return the name of this domain
	 */
	public String toString()
	{
		return name;
	}

	public void setGrantedPower(String aString)
	{
		grantedPower = aString;
	}

	public String getGrantedPower()
	{
		return grantedPower;
	}

	/**
         * Selector
         *
         * <br>author: Thomas Behr 20-03-02
         *
	 * @return a String detailing the skill list of this domain
	 */
	public String getSkillList()
	{
		return skillList;
	}

	public String skillList()
	{
		return skillList;
	}

	public void setSkillList(String aString)
	{
		skillList = aString;
	}

	public boolean isLocked()
	{
		return isLocked;
	}

	public void setIsLocked(boolean aBool)
	{
		if (isLocked == aBool)
			return;
		isLocked = aBool;
		if (aBool)
		{
			final PlayerCharacter aPC = Globals.getCurrentPC();
			CharacterDomain aCD = aPC.getCharacterDomainForDomain(name);
			if (aCD != null)
			{
				if (aCD.getSourceType().equalsIgnoreCase("PCClass"))
				{
					PCClass aClass = (PCClass)aPC.getClassNamed(aCD.getSourceName());
					if (aClass != null)
					{
						int maxLevel = 0;
						for (maxLevel = 0; maxLevel < 10; maxLevel++)
						{
							if (aClass.getCastForLevel(aClass.getLevel().intValue(), maxLevel) == 0)
								break;
						}
						if (maxLevel > 1)
						{
							for (Iterator i = Globals.getSpellMap().values().iterator(); i.hasNext();)
							{
								Spell aSpell = (Spell)i.next();
								String classLevels = aSpell.levelForClass(name);
								StringTokenizer aTok = new StringTokenizer(classLevels, ",", false);
								while (aTok.hasMoreTokens())
								{
									String aName = aTok.nextToken();
									int level = -1;
									if (aTok.hasMoreTokens())
										level = Integer.parseInt(aTok.nextToken());
									if (level >= 0 && level < maxLevel && aName.equalsIgnoreCase(name))
									{
										Spell bSpell = aClass.getSpellNamed(aSpell.getName());
										if (bSpell == null) // new spell for this class
										{
											aSpell = (Spell)aSpell.clone();
											aSpell.setClassLevels(name + "," + String.valueOf(level));
											aClass.spellList().add(aSpell);
											aSpell.addToSpellBook(Globals.getDefaultSpellBook(), false);
											aPC.addSpellBook(Globals.getDefaultSpellBook());
										}
										else
										{
											String cString = bSpell.getClassLevels();
											bSpell.setClassLevels(cString + "," + name + "," + String.valueOf(level));
										}
										if (aClass.getKnownSpellsFromSpecialty() == 0)
											aClass.setKnownSpellsFromSpecialty(1);
									}
								}
							}
						}
					}
				}
			}
			if (getSpellList().length() > 0)
			{
				final StringTokenizer aTok = new StringTokenizer(getSpellList(), ",", false);
				final Collection clericSpells = aPC.getClassNamed("Cleric").spellList();
				while (aTok.hasMoreTokens())
				{
					final String spellName = aTok.nextToken();
					final Spell aSpell = (Spell)Globals.getSpellMap().get(spellName);
					if (aSpell != null)
					{
						final Spell bSpell = (Spell)aSpell.clone();
						bSpell.setClassLevels("Cleric,1");
						clericSpells.add(bSpell);
					}
				}
			}
			if (aPC != null && getFeatList().length() > 0)
				aPC.modFeatsFromList(featList, aBool, false);
			if (aPC != null && choiceString.length() > 0 && !aPC.getIsImporting())
				getChoices(choiceString, new ArrayList());
		}
	}

	public String getFeatList()
	{
		return featList;
	}

	public void setFeatList(String aString)
	{
		featList = aString;
	}

	public String getSpellList()
	{
		return spellList;
	}

	public void setSpellList(String aString)
	{
		spellList = aString;
	}

	public boolean hasSkill(String skillName)
	{
		final StringTokenizer aTok = new StringTokenizer(skillList, ",", false);
		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			if (aString.equals(skillName) || skillName.startsWith(aString))
				return true;
		}
		return false;
	}

	public boolean hasFeat(String featName)
	{
		final StringTokenizer aTok = new StringTokenizer(featList, ",", false);
		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			if (aString.equals(featName))
				return true;
		}
		return false;
	}

	public boolean qualifiesForDomain()
	{
		return passesPreReqTests();
	}

	/**
	 * @return the special ability granted by this domain
	 */
	public final String getSpecialAbility()
	{
		return specialAbility;
	}

	public final void setSpecialAbility(String specialAbility)
	{
		this.specialAbility = specialAbility;
	}

	public final void addSpecialAbility(String specialAbility)
	{
		this.specialAbility += specialAbility;
	}
}
