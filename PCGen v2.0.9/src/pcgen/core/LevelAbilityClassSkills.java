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
 */
package pcgen.core;

import pcgen.gui.Chooser;

import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Represents class skills the character gains when going up a level
 * (an ADD:CLASSSKILLS line in the LST file).
 *
 * @author Dmitry Jemerov <yole@spb.cityline.ru>
 */

public class LevelAbilityClassSkills extends LevelAbility
{
	private ArrayList rootArrayList;

	LevelAbilityClassSkills(PCClass aOwnerClass, int aLevel, String aList)
	{
		super(aOwnerClass, aLevel, aList);
	}

	/**
	 * Performs the initial setup of a chooser.
	 */

	public String prepareChooser(Chooser c)
	{
		super.prepareChooser (c);
		StringTokenizer aTok = new StringTokenizer(list, "|", false);
		c.setTitle (aTok.nextToken());
		if (aTok.hasMoreTokens())
			c.setPool (Integer.parseInt(aTok.nextToken()));

		return list;
	}

	/**
	 * Generates the list of tokens to be shown in the chooser from the list of
	 * skills of given type.
	 */

	public ArrayList getChoicesList(String bString)
	{
		ArrayList aArrayList = new ArrayList();
		rootArrayList = new ArrayList();
		String aText = list.substring(list.lastIndexOf('(') + 1, list.lastIndexOf(')'));
		for (int index = 0; index < Globals.skillList.size(); index++)
		{
			Skill aSkill = (Skill)Globals.skillList.get(index);
			if ((aText.equals("UNTRAINED") && aSkill.untrained().equals("Y")) ||
				(aText.equals("TRAINED") && aSkill.untrained().equals("N")) ||
				(aText.equals("EXCLUSIVE") && aSkill.isExclusive().equals("Y")) ||
				(aText.equals("NONEXCLUSIVE") && aSkill.isExclusive().equals("N")) ||
				aText.equals("ANY"))
//                if (skillList().contains(aSkill.keyName()))
//                {
				if (aSkill.rootName.length() == 0)
					aArrayList.add(aSkill.keyName());
			if (aSkill.rootName.length() > 0 && !rootArrayList.contains(aSkill.rootName))
				aArrayList.add(aSkill.rootName);
			if (aSkill.rootName.length() > 0 && !rootArrayList.contains(aSkill.rootName))
				rootArrayList.add(aSkill.rootName);
//                }
		}
		return aArrayList;
	}

	/**
	 * Process the choice selected by the user.
	 */

	void processChoice(ArrayList aArrayList, List selectedList)
	{
		for (int index = 0; index < selectedList.size(); index++)
			if (!ownerClass.skillList().contains(selectedList.get(index).toString()))
			{
				String nString = selectedList.get(index).toString();
				if (rootArrayList.contains(nString))
				{
					for (Iterator e2 = Globals.skillList.iterator(); e2.hasNext();)
					{
						Skill aSkill = (Skill)e2.next();
						if (aSkill.rootName.equals(nString))
							ownerClass.skillList().add(aSkill.keyName());
					}
				}
				else
					ownerClass.skillList().add(nString);
			}
	}

}
