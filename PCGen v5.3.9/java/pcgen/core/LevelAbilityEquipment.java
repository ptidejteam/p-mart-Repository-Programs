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
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:16:13 $
 *
 */

package pcgen.core;

import java.util.Collections;
import java.util.List;

import pcgen.util.Logging;

/**
 * Represents a equipment that a character gets when gaining a level
 * (an ADD:EQUIP entry in the LST file).
 *
 * @author Felipe Diniz <fdiniz@imagelink.com.br>
 * @version $Revision: 1.1 $
 */

final class LevelAbilityEquipment extends LevelAbility
{

	LevelAbilityEquipment(PObject aowner, int aLevel, String aString)
	{
		super(aowner, aLevel, aString);
	}

	/**
	 * Performs the initial setup of a chooser.
	 **/
	public String prepareChooser(pcgen.gui.utils.ChooserInterface c)
	{
		super.prepareChooser(c);
		c.setTitle("Equipment Choice");
		return list;
	}

	public List getChoicesList(String bString)
	{
		final List aList = super.getChoicesList(bString.substring(6));
		Collections.sort(aList);
		return aList;
	}

	/**
	 * Processes a single token in the comma-separated list of the ADD:
	 * field and adds the choices to be shown in the list to aArrayList.
	 **/
	void processToken(String aChoice, List aArrayList, String bString)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aChoice.startsWith("TYPE=") || aChoice.startsWith("TYPE."))
		{
			String eqType = aChoice.substring(5);
			aArrayList.addAll(Globals.getEquipmentOfType(Globals.getEquipmentList(),eqType,""));
		}
		else
		{
			String equipmentName = aChoice;
			Equipment aEquipment = Globals.getEquipmentNamed(equipmentName);

			if (aEquipment == null)
			{
				Logging.errorPrint("LevelAbilityEquipment: Equipment not found: " + equipmentName);
				return;
			}

			if (!aPC.getEquipmentMasterList().contains(aEquipment))
			{
				if (aEquipment.passesPreReqToGain())
				{
					aArrayList.add(aChoice);
				}
			}
		}
	}

	/**
	 * Process the choice selected by the user.
	 */

	public void processChoice(List aArrayList, List selectedList, String extraString)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();

		for (int n = 0; n < selectedList.size(); n++)
		{
			String equipmentName = selectedList.get(n).toString();
			Equipment aEquipment = Globals.getEquipmentNamed(equipmentName);

			if (aEquipment == null)
			{
				Logging.errorPrint("LevelAbilityEquipment: Equipment not found: " + equipmentName);
				return;
			}
			Equipment bEquipment = (Equipment) aEquipment.clone();
			bEquipment.setQty(1);
			aPC.addEquipment(bEquipment);
		}
	}

}
