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
import pcgen.gui.Chooser;

/**
 * Represents a special ability that a character gets when gaining a level
 * (an ADD:SPECIAL entry in the LST file).
 *
 * @author Dmitry Jemerov <yole@spb.cityline.ru>
 */

public class LevelAbilitySpecial extends LevelAbility
{
	LevelAbilitySpecial(PCClass aOwnerClass, int aLevel, String aList)
	{
		super(aOwnerClass, aLevel, aList);
	}

	/**
	 * Performs the initial setup of a chooser.
	 */

	public String prepareChooser(Chooser c)
	{
		super.prepareChooser(c);
		c.setTitle("Special Ability Choice");
		return list;
	}

	public ArrayList getChoicesList(String bString)
	{
		bString = bString.substring(bString.lastIndexOf('(') + 1);
		if (bString.endsWith(")"))
			bString = bString.substring(0, bString.length() - 1);

		ArrayList aList = super.getChoicesList(bString);
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
		if (aChoice.equals("FEATLIST"))
		{
			for (int index = 0; index < Globals.getFeatList().size(); index++)
			{
				final Feat aFeat = (Feat)Globals.getFeatList().get(index);
				if (aPC.qualifiesForFeat(aFeat.getKeyName()) &&
					(((!aPC.hasFeat(aChoice) && !aPC.hasFeatAutomatic(aChoice))) ||
					aFeat.isMultiples()))
				{
					aArrayList.add(Globals.getFeatList().get(index).toString());
				}
			}
		}
		else if (aChoice.equals("Free Feat"))
			aArrayList.add(aChoice);
		else if (aChoice.startsWith("FEATTYPE="))
		{
			String featType = aChoice.substring(9);
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
		else if (!aPC.hasSpecialAbility(aChoice))
			aArrayList.add(aChoice);
	}

	/**
	 * Process the choice selected by the user.
	 */

	void processChoice(ArrayList aArrayList, List selectedList)
	{
		if (selectedList.size() == 0)
			return;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (selectedList.get(0).toString().equals("Free Feat"))
		{
			aPC.setFeats(aPC.getFeats() + 1);
			return;
		}
		int index = 0;
		for (index = 0; index < Globals.getFeatList().size(); index++)
			if (Globals.getFeatList().get(index).toString().equals((String)selectedList.get(0).toString()))
			{
				aPC.modFeat(Globals.getFeatList().get(index).toString(), true, false);
				aPC.setFeats(aPC.getFeats() + 1);
				break;
			}
		if (index >= Globals.getFeatList().size())
		{
			aPC.getSpecialAbilityList().add((String)selectedList.get(0).toString());
			ownerClass.addSaveList((String)selectedList.get(0).toString());
		}
	}

}
