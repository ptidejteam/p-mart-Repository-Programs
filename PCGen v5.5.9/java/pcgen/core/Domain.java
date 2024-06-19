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
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:27:59 $
 *
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;
import pcgen.gui.utils.GuiFacade;

/**
 * <code>Domain</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class Domain extends PObject
{
	private String featList = "";
	private boolean isLocked = false;

	public Object clone()
	{
		Domain aObj = null;
		try
		{
			aObj = (Domain) super.clone();
			aObj.featList = featList;
			aObj.isLocked = false;
			//aObj.isLocked = isLocked;
		}
		catch (CloneNotSupportedException exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
		}
		return aObj;
	}

	void addSpellsToClassForLevels(PCClass aClass, int minLevel, int maxLevel)
	{
		if (aClass == null)
		{
			return;
		}
		for (int aLevel = minLevel; aLevel <= maxLevel; aLevel++)
		{
			final List domainSpells = Globals.getSpellsIn(aLevel, "", name);
			if (!domainSpells.isEmpty())
			{
				for (Iterator di = domainSpells.iterator(); di.hasNext();)
				{
					final Spell aSpell = (Spell) di.next();
					List slist = aClass.getCharacterSpell(aSpell, Globals.getDefaultSpellBook(), aLevel);
					boolean flag = true;
					for (Iterator si = slist.iterator(); si.hasNext();)
					{
						CharacterSpell cs1 = (CharacterSpell) si.next();
						flag = !(cs1.getOwner().equals(this));
						if (!flag)
						{
							break;
						}
					}
					if (flag)
					{
						CharacterSpell cs = new CharacterSpell(this, aSpell);
						cs.addInfo(aLevel, 1, Globals.getDefaultSpellBook());
						aClass.addCharacterSpell(cs);
					}
				}
			}
		}
	}

	public void setIsLocked(boolean aBool)
	{
		if (isLocked == aBool)
		{
			return;
		}
		isLocked = aBool;
		if (aBool)
		{
			final PlayerCharacter aPC = Globals.getCurrentPC();
			final CharacterDomain aCD = aPC.getCharacterDomainForDomain(name);
			PCClass aClass = null;
			if (aCD != null)
			{
				if (aCD.isFromPCClass())
				{
					aClass = aPC.getClassNamed(aCD.getObjectName());
					if (aClass != null)
					{
						int maxLevel;
						for (maxLevel = 0; maxLevel < 10; maxLevel++)
						{
							if (aClass.getCastForLevel(aClass.getLevel(), maxLevel) == 0)
							{
								break;
							}
						}
						if (maxLevel > 0)
						{
							addSpellsToClassForLevels(aClass, 0, maxLevel - 1);
						}

						if (maxLevel > 1 && aClass.getNumSpellsFromSpecialty() == 0)
						{
							final List aList = Globals.getSpellsIn(-1, "", name);
							for (Iterator i = aList.iterator(); i.hasNext();)
							{
								final Spell gcs = (Spell) i.next();
								if (gcs.levelForKey("DOMAIN", name) < maxLevel)
								{
									if (aClass.getNumSpellsFromSpecialty() == 0)
									{
										aClass.setNumSpellsFromSpecialty(1);
									}
								}
							}
						}
					}
				}
			}

			List spellList = getSpellList();

			if (aClass != null && spellList != null && !spellList.isEmpty())
			{
				for (Iterator ri = spellList.iterator(); ri.hasNext();)
				{
					PCSpell pcSpell = (PCSpell)ri.next();

					// spellname|times|book|PRExxx|PRExxx|etc
//					String spellLine = ri.next().toString();
//					final StringTokenizer aTok = new StringTokenizer(spellLine, "|", false);
//					String spellName = aTok.nextToken();
					Spell aSpell = Globals.getSpellNamed(pcSpell.getKeyName());
					if (aSpell == null)
					{
						return;
					}
//					final int times = Integer.parseInt(aTok.nextToken());
					final int times = Integer.parseInt(pcSpell.getTimesPerDay());
//					String book = aTok.nextToken();
					String book = pcSpell.getSpellbook();
//					if (aTok.hasMoreTokens())
//					{
//						List qList = new ArrayList();
//						while (aTok.hasMoreTokens())
//						{
//							qList.add(aTok.nextToken());
//						}
//						if (!passesPreReqToGainForList(qList))
//						{
//							continue;
//						}
//					}
					if (passesPreReqToGainForList(pcSpell.getPreReqList()))
					{
						List aList = aClass.getCharacterSpell(aSpell, book, -1);
						if (aList.isEmpty())
						{
							CharacterSpell cs = new CharacterSpell(this, aSpell);
							cs.addInfo(1, times, book);
							aClass.addCharacterSpell(cs);
						}
					}
				}
			}
			// sage_sam stop here

			if ((choiceString.length() > 0) && !aPC.isImporting() && !choiceString.startsWith("FEAT|"))
			{
				modChoices(this, new ArrayList(), new ArrayList(), true);
			}
			if (!aPC.isImporting())
			{
				globalChecks();
				activateBonuses();
			}
		}
	}

	/**
	 * Only compares the name.
	 * @param o
	 * @return
	 * @see java.lang.Object#equals
	 */
	public boolean equals(Object o)
	{
		if (o != null)
		{
			if (o.getClass() == this.getClass())
			{
				return ((Domain) o).getName().equals(this.getName());
			}
		}
		return false;
	}

	/**
	 * Only uses the name for hashCode.
	 * @return
	 */
	public int hashCode()
	{
		int result;
		result = (getName() != null ? getName().hashCode() : 0);
		return result;
	}

	String getFeatList()
	{
		return featList;
	}

	public void setFeatList(String aString)
	{
		featList = aString;
	}

	public boolean qualifiesForDomain()
	{
		return passesPreReqToGain();
	}

	public String getSpellKey()
	{
		return "DOMAIN|" + name;
	}
}
