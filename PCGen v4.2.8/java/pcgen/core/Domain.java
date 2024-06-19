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
import javax.swing.JOptionPane;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;
import pcgen.util.GuiFacade;

/**
 * <code>Domain</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class Domain extends PObject
{
	//private String grantedPower = "";
	private String featList = "";
	private boolean isLocked = false;

	public Object clone()
	{
		Domain aObj = null;
		try
		{
			aObj = (Domain) super.clone();
			//aObj.grantedPower = grantedPower;
			aObj.featList = featList;
			aObj.isLocked = isLocked;
		}
		catch (CloneNotSupportedException exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			return aObj;
		}
	}

	//public void setGrantedPower(String aString)
	//{
	//	grantedPower = aString;
	//}

	//public String getGrantedPower()
	//{
	//	return grantedPower;
	//}

	void addSpellsToClassForLevels(PCClass aClass, int minLevel, int maxLevel)
	{
		if (aClass == null)
		{
			return;
		}
		for (int aLevel = minLevel; aLevel <= maxLevel; aLevel++)
		{
			final ArrayList domainSpells = Globals.getSpellsIn(aLevel, "", name);
			if (!domainSpells.isEmpty())
			{
				for (Iterator di = domainSpells.iterator(); di.hasNext();)
				{
					final Spell aSpell = (Spell) di.next();
					ArrayList slist = aClass.getCharacterSpell(aSpell, Globals.getDefaultSpellBook(), aLevel);
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
							final ArrayList aList = Globals.getSpellsIn(-1, "", name);
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
			if (aClass != null && spellList != null && !spellList.isEmpty())
			{
				for (Iterator ri = spellList.iterator(); ri.hasNext();)
				{
					// spellname|times|book|PRExxx|PRExxx|etc
					String spellLine = ri.next().toString();
					final StringTokenizer aTok = new StringTokenizer(spellLine, "|", false);
					String spellName = aTok.nextToken();
					Spell aSpell = Globals.getSpellNamed(spellName);
					if (aSpell == null)
					{
						return;
					}
					final int times = Integer.parseInt(aTok.nextToken());
					String book = aTok.nextToken();
					if (aTok.hasMoreTokens())
					{
						ArrayList qList = new ArrayList();
						while (aTok.hasMoreTokens())
						{
							qList.add(aTok.nextToken());
						}
						if (!passesPreReqTestsForList(qList))
						{
							continue;
						}
					}
					ArrayList aList = aClass.getCharacterSpell(aSpell, book, -1);
					if (aList.isEmpty())
					{
						CharacterSpell cs = new CharacterSpell(this, aSpell);
						cs.addInfo(1, times, book);
						aClass.addCharacterSpell(cs);
					}
				}
			}
			if (aPC != null && choiceString.length() > 0 && !aPC.isImporting() && !choiceString.startsWith("FEAT|"))
			{
				Utility.modChoices(this, new ArrayList(), new ArrayList(), true);
			}
			if (aPC != null && !aPC.isImporting())
			{
				globalChecks();
			}
		}
	}

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
		return passesPreReqTests();
	}

	public String getSpellKey()
	{
		return "DOMAIN|" + name;
	}
}
