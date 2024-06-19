/*
 * LevelAbilitySpecial.java
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
 * Created on July 24, 2001, 11:24 PM
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pcgen.gui.ChooserInterface;

/**
 * Represents a special ability that a character gets when gaining a level
 * (an ADD:SPECIAL entry in the LST file).
 *
 * @author Dmitry Jemerov <yole@spb.cityline.ru>
 * @version $Revision: 1.1 $
 */

final class LevelAbilitySpecial extends LevelAbility
{
	LevelAbilitySpecial(PCClass aOwnerClass, int aLevel, String aList)
	{
		super(aOwnerClass, aLevel, aList);
	}

	/**
	 * Performs the initial setup of a chooser.
	 */

	public String prepareChooser(ChooserInterface c)
	{
		super.prepareChooser(c);
		c.setTitle("Special Ability Choice");
		return list;
	}

	public ArrayList getChoicesList(String bString)
	{
		bString = bString.substring(bString.lastIndexOf('(') + 1);
		if (bString.lastIndexOf(")") >= bString.length() - 2)
			bString = bString.substring(0, bString.lastIndexOf(")"));

		final ArrayList aList = super.getChoicesList(bString);
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
		if ("FEATLIST".equals(aChoice))
		{
			for (int index = 0; index < Globals.getFeatList().size(); index++)
			{
				final Feat aFeat = Globals.getFeatListFeat(index);
				if (aPC.qualifiesForFeat(aFeat.getKeyName()) &&
					(((!aPC.hasFeat(aChoice) && !aPC.hasFeatAutomatic(aChoice))) ||
					aFeat.isMultiples()))
				{
					aArrayList.add(Globals.getFeatListFeatString(index));
				}
			}
		}
		else if ("Free Feat".equals(aChoice))
			aArrayList.add(aChoice);
		else if (aChoice.startsWith("FEATTYPE="))
		{
			final String featType = aChoice.substring(9);
			aArrayList.addAll(aPC.getAvailableFeatNames(featType));
		}
		else if (aChoice.startsWith("FEAT="))
		{
			final Feat aFeat = Globals.getFeatNamed(aChoice.substring(5));
			if (aPC.qualifiesForFeat(aFeat.getKeyName()) &&
				(((!aPC.hasFeat(aChoice) && !aPC.hasFeatAutomatic(aChoice))) ||
				aFeat.isMultiples()))
			{
				aArrayList.add(aFeat.getKeyName());
			}
		}
		else
		{
			final ArrayList aList = aPC.getSpecialAbilityListStrings();
			if (!aList.contains(aChoice))
				aArrayList.add(aChoice);
		}
	}

	/**
	 * Process the choice selected by the user.
	 */

	void processChoice(ArrayList aArrayList, List selectedList, String eString)
	{
		if (selectedList.size() == 0)
		{
			return;
		}
		final PlayerCharacter aPC = Globals.getCurrentPC();
		for (int i = 0; i < selectedList.size(); i++)
		{
			String aString = selectedList.get(i).toString();
			if ("Free Feat".equals(aString))
			{
				aPC.setFeats(aPC.getFeats() + 1);
				continue;
			}
			int index;
			aString += eString;
			for (index = 0; index < Globals.getFeatList().size(); index++)
				if (Globals.getFeatListFeatString(index).equals(aString))
				{
					aPC.modFeat(Globals.getFeatListFeatString(index), true, false);
					aPC.setFeats(aPC.getFeats() + 1);
					break;
				}
			if (index >= Globals.getFeatList().size())
			{
				SpecialAbility sa = new SpecialAbility(aString, "PCCLASS|" + ownerClass.getName() + "|" + level());
				ownerClass.addSpecialAbilityToList(sa);
				ownerClass.addSave(aString);
			}
		}
	}
}
