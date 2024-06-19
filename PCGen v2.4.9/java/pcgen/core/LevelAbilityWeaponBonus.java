/*
 * LevelAbilityWeaponBonus.java
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
 * Created on Jul 26, 2001, 10:15:09 PM
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.gui.Chooser;

/**
 * Represents a weapon proficiency that a character gets when gaining a level
 * (an ADD:WEAPONBONUS entry in the LST file).
 *
 * @author Dmitry Jemerov <yole@spb.cityline.ru>
 * @version $Revision: 1.1 $
 */

public class LevelAbilityWeaponBonus extends LevelAbility
{
	private String bonusTag;
	private String bonusMod;

	LevelAbilityWeaponBonus(PCClass aOwnerClass, int aLevel, String aList)
	{
		super(aOwnerClass, aLevel, aList);
	}

	/**
	 * Performs the initial setup of a chooser.
	 */

	public String prepareChooser(Chooser c)
	{
		super.prepareChooser(c);
		c.setTitle("Select Weapon Prof");
		bonusTag = "";
		bonusMod = "";
		return list;
	}

	/**
	 * Parses the comma-separated list of the ADD: field and returns the
	 * list of tokens to be shown in the chooser.
	 */

	public ArrayList getChoicesList(String bString)
	{
		ArrayList aArrayList = new ArrayList();
		final PlayerCharacter aPC = Globals.getCurrentPC();
		final StringTokenizer bTok = new StringTokenizer(bString.substring(12), "|", false);
		bonusTag = bTok.nextToken();
		bonusMod = bTok.nextToken();
		while (bTok.hasMoreTokens())
		{
			String cString = bTok.nextToken();
			if (cString.startsWith("PCFEAT="))
			{
				final Feat aFeat = aPC.getFeatNamed(cString.substring(7));
				if (aFeat != null)
				{
					for (int ii = 0; ii < aFeat.getAssociatedList().size(); ii++)
					{
						if (!aArrayList.contains(aFeat.getAssociatedList().get(ii)))
						{
							aArrayList.add(aFeat.getAssociatedList().get(ii));
						}
					}
				}
			}
			else if (cString.startsWith("FEAT="))
			{
				final Feat aFeat = Globals.getFeatNamed(cString.substring(5));
				if (aFeat != null)
				{
					StringTokenizer aTok = new StringTokenizer(aFeat.getName(), " ", false);
					String aName = aTok.nextToken(); // first word of name should match type of weaponprof
					for (int ii = 0; ii < Globals.getWeaponProfList().size(); ii++)
					{
						final WeaponProf wp = (WeaponProf)Globals.getWeaponProfList().get(ii);
						if (wp.getType().equalsIgnoreCase(aName) &&
							!aArrayList.contains(wp.getName()))
							aArrayList.add(wp.getName());
					}
				}
			}
			else if (cString.startsWith("ALL"))
			{
				for (int ii = 0; ii < Globals.getWeaponProfList().size(); ii++)
					if (!aArrayList.contains(((String)Globals.getWeaponProfList().get(ii))))
						aArrayList.add(((String)Globals.getWeaponProfList().get(ii)));
			}
			else if (cString.startsWith("PCPROFLIST"))
			{
				for (Iterator ii = aPC.getWeaponProfList().iterator();
						 ii.hasNext();)
				{
					String prof = (String)ii.next();
					if (!aArrayList.contains(prof))
						aArrayList.add(prof);
				}
			}
			else
			{
				final WeaponProf wp = Globals.getWeaponProfNamed(cString);
				if (wp != null && !aArrayList.contains(cString))
					aArrayList.add(cString);
			}
		}
		return aArrayList;
	}

	/**
	 * Process the choice selected by the user.
	 */

	void processChoice(ArrayList aArrayList, List selectedList, String eString)
	{
		final String bonusString = "|" + bonusTag + "|" + bonusMod;
		for (int index = 0; index < selectedList.size(); index++)
		{
			final String cString = selectedList.get(index).toString() + eString;
			final StringBuffer weaponProfString = new StringBuffer("WEAPONPROF=").append(cString).append(bonusString);
			// aPC.getSpecialAbilityList().add(weaponProfString.toString());
			// The above causes a badd SA error.
			ownerClass.addBonusList(new StringBuffer("0|").append(weaponProfString).toString());
			ownerClass.addSaveList(new StringBuffer("BONUS|0|").append(weaponProfString).toString());
		}
	}
}
