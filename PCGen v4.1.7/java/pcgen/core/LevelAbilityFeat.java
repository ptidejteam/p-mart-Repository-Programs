/*
 * LevelAbilityFeat.java
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
 * Created on July 24, 2001, 10:11 PM
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.gui.ChooserInterface;

/**
 * Represents a feat that a character gets when gaining a level
 * (an ADD:FEAT entry in the LST file).
 *
 * @author Dmitry Jemerov <yole@spb.cityline.ru>
 * @version $Revision: 1.1 $
 */

final class LevelAbilityFeat extends LevelAbility
{

	LevelAbilityFeat(PCClass aOwnerClass, int aLevel, String aList)
	{
		super(aOwnerClass, aLevel, aList);
	}

	/**
	 * Performs the initial setup of a chooser.
	 */

	public String prepareChooser(ChooserInterface c)
	{
		super.prepareChooser(c);
		c.setTitle("Feat Choice");
		c.setPoolFlag(false);
		return list;
	}

	public ArrayList getChoicesList(String bString)
	{
		final ArrayList aList = super.getChoicesList(bString.substring(5));
		Collections.sort(aList);
		return aList;
	}

	/**
	 * Processes a single token in the comma-separated list of the ADD:
	 * field and adds the choices to be shown in the list to aArrayList.
	 */

	void processToken(String aChoice, ArrayList aArrayList, String bString)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aChoice.startsWith("TYPE="))
		{
			String featType = aChoice.substring(5);

			if ("REGION".equals(featType))
			{
				String regionType;
				final Iterator iterator = aPC.getTemplateList().iterator();

				for (Iterator e = iterator; e.hasNext();)
				{
					final PCTemplate t = (PCTemplate) e.next();
					regionType = t.getRegion();
					if (!regionType.equals(Constants.s_NONE))
					{
						featType = regionType;
					}
				}
			}

			if ("SUBREGION".equals(featType))
			{
				String subregionType;
				final Iterator iterator = aPC.getTemplateList().iterator();

				for (Iterator e = iterator; e.hasNext();)
				{
					final PCTemplate t = (PCTemplate) e.next();
					subregionType = t.getSubRegion();
					if (!subregionType.equals(Constants.s_NONE))
					{
						featType = subregionType;
					}
				}
			}

			if ("ALLREGION".equals(featType))
			{
				String regionType;
				final Iterator iterator = aPC.getTemplateList().iterator();

				for (Iterator e = iterator; e.hasNext();)
				{
					final PCTemplate t = (PCTemplate) e.next();
					regionType = t.getRegion();
					if (!regionType.equals(Constants.s_NONE))
					{
						featType = regionType;
					}
				}
				aArrayList.addAll(aPC.getAvailableFeatNames(featType));//add regions then reset for subregions

				String subregionType;
				final Iterator iterator2 = aPC.getTemplateList().iterator();

				for (Iterator e = iterator2; e.hasNext();)
				{
					final PCTemplate t = (PCTemplate) e.next();
					subregionType = t.getSubRegion();
					if (!subregionType.equals(Constants.s_NONE))
					{
						featType = subregionType;
					}
				}
			}
			aArrayList.addAll(aPC.getAvailableFeatNames(featType));
		}
		else
		{
			final StringTokenizer aTok = new StringTokenizer(aChoice, ",", false);
			String featName = aTok.nextToken().trim();
			String subName = "";
			Feat aFeat = Globals.getFeatNamed(featName);

			if (aFeat == null)
			{
				Globals.debugPrint("LevelAbilityFeat: Feat not found: ", featName);
				return;
			}

			if (!featName.equalsIgnoreCase(aFeat.getName()))
			{
				subName = featName.substring(aFeat.getName().length());
				featName = aFeat.getName();
				final int i = subName.indexOf('(');
				if (i > -1)
				{
					subName = subName.substring(i + 1);
				}
			}

			if (aFeat.passesPreReqTests())
			{
				if (aFeat.isMultiples())
				{
					//
					// If already have taken the feat, use it so we can remove
					// any choices already selected
					//
					final Feat pcFeat = aPC.getFeatNamed(featName);
					if (pcFeat != null)
					{
						aFeat = pcFeat;
					}

					final int percIdx = subName.indexOf('%');
					if (percIdx > -1)
					{
						subName = subName.substring(0, percIdx);
					}
					else if (subName.length() != 0)
					{
						final int idx = subName.lastIndexOf(')');
						if (idx > -1)
						{
							subName = subName.substring(0, idx);
						}
					}

					final ArrayList availableList = new ArrayList();	// available list of choices
					final ArrayList selectedList = new ArrayList();		// selected list of choices
					aFeat.modChoices(true, availableList, selectedList, false);

					//
					// Remove any that don't match
					//
					if (subName.length() != 0)
					{
						for (int n = availableList.size() - 1; n >= 0; --n)
						{
							String aString = (String) availableList.get(n);
							if (!aString.startsWith(subName))
							{
								availableList.remove(n);
							}
						}
						//
						// Example: ADD:FEAT(Skill Focus(Craft (Basketweaving)))
						// If you have no ranks in Craft (Basketweaving), the available list will be empty
						//
						// Make sure that the specified feat is available, even though it does not meet the prerequisite
						//
						if ((percIdx == -1) && (availableList.size() == 0))
						{
							availableList.add(subName);
						}
					}
					//
					// Remove any already selected
					//
					if (!aFeat.isStacks())
					{
						for (Iterator e = selectedList.iterator(); e.hasNext();)
						{
							int idx = availableList.indexOf((String) e.next());
							if (idx > -1)
							{
								availableList.remove(idx);
							}
						}
					}
					if (!aFeat.getChoiceString().startsWith("SPELLLIST|"))
					{
						for (Iterator e = availableList.iterator(); e.hasNext();)
						{
							aArrayList.add(featName + "(" + (String) e.next() + ")");
						}
					}
					else
					{
						aArrayList.add(featName);
					}
					return;
				}
				else if (!aPC.hasFeat(featName) && !aPC.hasFeatAutomatic(featName))
				{
					aArrayList.add(aChoice);
				}
			}
		}
	}

	/**
	 * Process the choice selected by the user.
	 */

	void processChoice(ArrayList aArrayList, List selectedList, String eString)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		aPC.setFeats(aPC.getFeats() + 1);
		for (int n = 0; n < selectedList.size(); n++)
		{
			final String featString = selectedList.get(n).toString() + eString;
			aPC.modFeat(featString, true, false);
		}
	}

}
