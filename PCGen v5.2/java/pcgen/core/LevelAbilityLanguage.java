/*
 * LevelAbilityLanguage.java
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
 * Created on Jul 31, 2001, 12:40:47 AM
 *
 * $Id: LevelAbilityLanguage.java,v 1.1 2006/02/21 01:13:12 vauchers Exp $
 */

package pcgen.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a bonus language a PC gets when gaining a level (am ADD:Language
 * line in the LST file).
 * @author Dmitry Jemerov <yole@spb.cityline.ru>
 * @version $Revision: 1.1 $
 */

final class LevelAbilityLanguage extends LevelAbility
{

	LevelAbilityLanguage(PObject aowner, int aLevel, String aList)
	{
		super(aowner, aLevel, aList);
	}

	/**
	 * Performs the initial setup of a chooser.
	 */

	public String prepareChooser(pcgen.gui.utils.ChooserInterface c)
	{
		super.prepareChooser(c);
		c.setTitle("Language Choice");
		return list;
	}

	/**
	 * Parses the comma-separated list of the ADD: field and returns the
	 * list of tokens to be shown in the chooser.
	 */

	public List getChoicesList(String bString)
	{
		final List aList = super.getChoicesList(bString.substring(9));
		Collections.sort(aList);
		return aList;
	}

	/**
	 * Processes a single token in the comma-separated list of the ADD:
	 * field and adds the choices to be shown in the list to aArrayList.
	 */

	void processToken(String aChoice, List aArrayList, String bString)
	{
		if (aChoice.startsWith("TYPE="))
		{
			aChoice = aChoice.substring(5);
			for (Iterator e = Globals.getLanguageList().iterator(); e.hasNext();)
			{
				final Language aLang = (Language) e.next();
				if (aLang.isType(aChoice))
				{
					aArrayList.add(aLang.getName());
				}
			}
		}
		else if (!Globals.getCurrentPC().getLanguagesList().contains(aChoice))
		{
			aArrayList.add(aChoice);
		}
	}

	/**
	 * Process the choice selected by the user.
	 */

	public void processChoice(List aArrayList, List selectedList, String eString)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		for (int index = 0; index < selectedList.size(); ++index)
		{
			aPC.addLanguage(selectedList.get(index).toString() + eString);
		}
	}
}
