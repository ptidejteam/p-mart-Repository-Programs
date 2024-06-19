/*
 * LevelAbilityClassSkills.java
 * Copyright 2001 (C) Dmitry Jemerov
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
 * Created on Jul 27, 2001, 12:13:37 AM
 *
 * $Id: LevelAbilityClassSkills.java,v 1.1 2006/02/21 01:07:42 vauchers Exp $
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import pcgen.gui.ChooserInterface;

/**
 * Represents class skills the character gains when going up a level
 * (an ADD:CLASSSKILLS line in the LST file).
 *
 * @author Dmitry Jemerov <yole@spb.cityline.ru>
 * @version $Revision: 1.1 $
 */

final class LevelAbilityClassSkills extends LevelAbility
{
	private static final int CHOICETYPE_ANY = -1;
	private static final int CHOICETYPE_NONE = 0;
	private static final int CHOICETYPE_UNTRAINED = 1;
	private static final int CHOICETYPE_TRAINED = 2;
	private static final int CHOICETYPE_EXCLUSIVE = 3;
	private static final int CHOICETYPE_NONEXCLUSIVE = 4;
	private static final int CHOICETYPE_CROSSCLASS = 5;
	private static final int CHOICETYPE_BYTYPE = 6;

	private int autoRank = 0;

	LevelAbilityClassSkills(PObject aOwner, int aLevel, String aList)
	{
		super(aOwner, aLevel, aList);
	}

	/**
	 * Performs the initial setup of a chooser.
	 */

	public String prepareChooser(ChooserInterface c)
	{
		super.prepareChooser(c);
		final StringTokenizer aTok = new StringTokenizer(list, "|", false);
		try
		{
			c.setTitle(aTok.nextToken());
		}
		catch (NoSuchElementException e)
		{
			// do nothing since the hasMoreTokens will return false
		}
		if (aTok.hasMoreTokens())
		{
			final String s = aTok.nextToken();
			if (s.equalsIgnoreCase("ALL"))
			{
				c.setPool(Integer.MIN_VALUE);
			}
			else
			{
				c.setPool(Integer.parseInt(s));
			}
		}

		return list;
	}

	/**
	 * Generates the list of tokens to be shown in the chooser from the list of
	 * skills of given type.
	 */

	public ArrayList getChoicesList(String bString)
	{
		final ArrayList aArrayList = new ArrayList();
		//final String aText = list.substring(list.lastIndexOf('(') + 1, list.lastIndexOf(')'));
		final StringTokenizer aTok = new StringTokenizer(list.substring(list.lastIndexOf('(') + 1, list.lastIndexOf(')')), ",", false);
		int choiceType;
		String skillType = "";
		Skill aSkill;
		autoRank = 0;
		PCClass theClass;
		if (owner instanceof PCClass)
		{
			theClass = (PCClass) owner;
		}
		else
		{
			theClass = new PCClass();

		}
		while (aTok.hasMoreTokens())
		{
			choiceType = CHOICETYPE_NONE;
			final String aText = aTok.nextToken();
			if ("UNTRAINED".equals(aText))
			{
				choiceType = CHOICETYPE_UNTRAINED;
			}
			else if ("TRAINED".equals(aText))
			{
				choiceType = CHOICETYPE_TRAINED;
			}
			else if ("EXCLUSIVE".equals(aText))
			{
				choiceType = CHOICETYPE_EXCLUSIVE;
			}
			else if ("NONEXCLUSIVE".equals(aText))
			{
				choiceType = CHOICETYPE_NONEXCLUSIVE;
			}
			else if ("CROSSCLASSSKILLS".equals(aText))
			{
				choiceType = CHOICETYPE_CROSSCLASS;
			}
			else if ("ANY".equals(aText))
			{
				choiceType = CHOICETYPE_ANY;
			}
			else if (aText.startsWith("TYPE="))
			{
				skillType = aText.substring(5);
				choiceType = CHOICETYPE_BYTYPE;
			}
			else if (aText.startsWith("AUTORANK="))
			{
				try
				{
					autoRank = Integer.parseInt(aText.substring(9));
				}
				catch (Exception exc)
				{
				}
			}
			else
			{
				aSkill = Globals.getSkillNamed(aText);
				if ((aSkill != null) && !aSkill.isClassSkill(theClass))
				{
					aArrayList.add(aSkill.getKeyName());
				}
			}
			if (choiceType == CHOICETYPE_NONE)
			{
				continue;
			}

			for (int index = 0, x = Globals.getSkillList().size(); index < x; ++index)
			{
				aSkill = (Skill) Globals.getSkillList().get(index);
				//
				// Already a class skill--no point in making it one again
				//
				if (aSkill.isClassSkill(theClass))
				{
					continue;
				}

				switch (choiceType)
				{
					case CHOICETYPE_UNTRAINED:
						if ("Y".equals(aSkill.getUntrained()))
						{
							break;
						}
						continue;

					case CHOICETYPE_TRAINED:
						if ("N".equals(aSkill.getUntrained()))
						{
							break;
						}
						continue;

					case CHOICETYPE_EXCLUSIVE:
						if (aSkill.isExclusive())
						{
							break;
						}
						continue;

					case CHOICETYPE_NONEXCLUSIVE:
						if (!aSkill.isExclusive())
						{
							break;
						}
						continue;

					case CHOICETYPE_CROSSCLASS:
						if (!aSkill.isExclusive())
						{
							break;
						}
						continue;

					case CHOICETYPE_ANY:
						break;

					case CHOICETYPE_BYTYPE:
						if (aSkill.isType(skillType))
						{
							break;
						}
						continue;
					default:
						//TODO What to do here?
				}
				aArrayList.add(aSkill.getKeyName());
			}
		}
		return aArrayList;
	}

	/**
	 * Process the choice selected by the user.
	 */

	public void processChoice(ArrayList aArrayList, List selectedList, String eString)
	{
		for (int index = 0; index < selectedList.size(); ++index)
		{
			final String nString = selectedList.get(index).toString() + eString;
			if ((owner instanceof PCClass) && !((PCClass) owner).skillList().contains(nString))
			{
				((PCClass) owner).skillList().add(nString);
				if (autoRank != 0)
				{
					Skill aSkill = Globals.getSkillKeyed(nString);
					if (aSkill != null)
					{
						final PlayerCharacter aPC = Globals.getCurrentPC();
						if (aPC != null)
						{
							aSkill = aPC.addSkill(aSkill);
							aSkill.modRanks(1.0, (PCClass) owner, true);
						}
					}
				}
			}
		}
		addAllToAssociated(selectedList);
	}

}
