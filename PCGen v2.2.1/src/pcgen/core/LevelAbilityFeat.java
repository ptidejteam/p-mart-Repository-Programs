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
import java.util.List;
import java.util.StringTokenizer;
import pcgen.gui.Chooser;

/**
 * Represents a feat that a character gets when gaining a level
 * (an ADD:FEAT entry in the LST file).
 *
 * @author Dmitry Jemerov <yole@spb.cityline.ru>
 */

public class LevelAbilityFeat extends LevelAbility
{

	LevelAbilityFeat(PCClass aOwnerClass, int aLevel, String aList)
	{
		super(aOwnerClass, aLevel, aList);
	}

	/**
	 * Performs the initial setup of a chooser.
	 */

	public String prepareChooser(Chooser c)
	{
		super.prepareChooser(c);
		c.setTitle("Feat Choice");
		c.setPool(1);  // tbannist: Changed to 1 to fix exotic weapon problem
		c.setPoolFlag(false);
		return list;
	}


	public ArrayList getChoicesList(String bString)
	{
		ArrayList aList = super.getChoicesList(bString.substring(5));
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
			final String featType = aChoice.substring(5);
			aArrayList.addAll(aPC.getAvailableFeatNames(featType));
		}
		else
		{
			StringTokenizer aTok = new StringTokenizer(aChoice,"(",false);
			String featName = aTok.nextToken().trim();
			Feat aFeat = null;
			if (aPC.qualifiesForFeat(featName))
			{
				for (int index = 0; index < Globals.getFeatList().size(); index++)
				{
					aFeat = (Feat)Globals.getFeatList().get(index);
					if (aFeat.getKeyName().equals(featName))
					{
						if ((!aPC.hasFeat(featName) && !aPC.hasFeatAutomatic(featName)) || aFeat.isMultiples())
							aArrayList.add(aChoice);
						break;
					}
				}
			}
		}
	}

	/**
	 * Process the choice selected by the user.
	 */

	void processChoice(ArrayList aArrayList, List selectedList)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		aPC.setFeats(aPC.getFeats() + 1);
		for (int n = 0; n < selectedList.size(); n++)
		{
			final String featString = selectedList.get(n).toString();
			aPC.modFeat(featString, true, false);
		}
	}

}
