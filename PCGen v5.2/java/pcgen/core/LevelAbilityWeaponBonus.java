/**
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
 *
 * $Id: LevelAbilityWeaponBonus.java,v 1.1 2006/02/21 01:13:12 vauchers Exp $
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Represents a weapon proficiency that a character gets when gaining a level
 * (an ADD:WEAPONBONUS entry in the LST file).
 *
 * @author Dmitry Jemerov <yole@spb.cityline.ru>
 * @version $Revision: 1.1 $
 */

final class LevelAbilityWeaponBonus extends LevelAbility
{
	private String bonusTag;
	private String bonusMod;

	LevelAbilityWeaponBonus(PObject aowner, int aLevel, String aList)
	{
		super(aowner, aLevel, aList);
	}

	/**
	 * Performs the initial setup of a chooser.
	 */

	public String prepareChooser(pcgen.gui.utils.ChooserInterface c)
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

	public List getChoicesList(String bString)
	{
		final List aArrayList = new ArrayList();
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
					for (int ii = 0; ii < aFeat.getAssociatedCount(); ++ii)
					{
						if (!aArrayList.contains(aFeat.getAssociated(ii)))
						{
							aArrayList.add(aFeat.getAssociated(ii));
						}
					}
				}
			}
			else if (cString.startsWith("FEAT="))
			{
				final Feat aFeat = Globals.getFeatNamed(cString.substring(5));
				if (aFeat != null)
				{
					final StringTokenizer aTok = new StringTokenizer(aFeat.getName(), " ", false);
					final String aName = aTok.nextToken(); // first word of name should match type of weaponprof
					Collection weaponProfsOfType = Globals.getAllWeaponProfsOfType(aName);
					for (Iterator ii = weaponProfsOfType.iterator(); ii.hasNext();)
					{
						WeaponProf wp = (WeaponProf) ii.next();
						if (!aArrayList.contains(wp.getName()))
						{
							aArrayList.add(wp.getName());
						}
					}
				}
			}
			else if (cString.startsWith("ALL"))
			{
				Globals.addUniqueWeaponProfsAsStringTo(aArrayList);
			}
			else if (cString.startsWith("PCPROFLIST"))
			{
				for (Iterator ii = aPC.getWeaponProfList().iterator(); ii.hasNext();)
				{
					final String prof = (String) ii.next();
					if (!aArrayList.contains(prof))
					{
						aArrayList.add(prof);
					}
				}
			}
			else
			{
				final WeaponProf wp = Globals.getWeaponProfNamed(cString);
				if (wp != null && !aArrayList.contains(cString))
				{
					aArrayList.add(cString);
				}
			}
		}
		return aArrayList;
	}

	/**
	 * Process the choice selected by the user.
	 */

	public void processChoice(List aArrayList, List selectedList, String eString)
	{
		final String bonusString = '|' + bonusTag + '|' + bonusMod;
		for (int index = 0; index < selectedList.size(); ++index)
		{
			final String cString = selectedList.get(index).toString() + eString;
			final String weaponProfString = "WEAPONPROF=" + cString + bonusString;
			// aPC.getSpecialAbilityList().add(weaponProfString.toString());			// The above causes a badd SA error.
			owner.addBonusList((new StringBuffer("0|").append(weaponProfString)).toString());
			owner.addSave((new StringBuffer("BONUS|0|").append(weaponProfString)).toString());
		}
	}
}
