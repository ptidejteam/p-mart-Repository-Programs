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
import java.util.Iterator;
import java.util.StringTokenizer;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;

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
							ArrayList aList = Globals.getCharacterSpell(null, Globals.getDefaultSpellBook(), -1, this, null);
							for (Iterator i = aList.iterator(); i.hasNext();)
							{
								CharacterSpell gcs = (CharacterSpell)i.next();
								if (gcs.getLevel() < maxLevel)
								{
									CharacterSpell cs = aClass.getFirstCharacterSpell(gcs.getSpell(), Globals.getDefaultSpellBook(), gcs.getLevel(), this, aClass);
									if (cs == null)
									{
										cs = new CharacterSpell(this, aClass, gcs.getSpell());
										cs.setLevel(gcs.getLevel());
										aClass.addCharacterSpell(cs);
//System.out.println("Domain:setIsLocked:("+this.getName()+") "+cs.getSpell().getName());
										if (aClass.getNumSpellsFromSpecialty() == 0)
											aClass.setNumSpellsFromSpecialty(1);
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
				final PCClass aClass = aPC.getClassNamed("Cleric");
				while (aClass != null && aTok.hasMoreTokens())
				{
					final String spellName = aTok.nextToken();
					final Spell aSpell = (Spell)Globals.getSpellMap().get(spellName);
					if (aSpell != null)
					{
						CharacterSpell cs = new CharacterSpell(this, aClass, aSpell);
						aClass.addCharacterSpell(cs);
//System.out.println("Domain:setIsLocked:>0:("+this.getName()+") "+cs.getSpell().getName());
					}
				}
			}
			if (aPC != null && choiceString.length() > 0 && !aPC.getIsImporting())
			{
				//getChoices(choiceString, new ArrayList());
				Utility.modChoices(this, true, new ArrayList(), new ArrayList(), true);
			}
			if ((aPC != null) && getFeatList().length() > 0)
			{
				// give a bonus feat to use in choice
				aPC.setFeats(aPC.getFeats() + 1);
				aPC.modFeatsFromList(featList, aBool, false);
			}
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
