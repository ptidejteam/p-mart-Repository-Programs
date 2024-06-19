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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.gui.Chooser;

/**
 * Represents class skills the character gains when going up a level
 * (an ADD:CLASSSKILLS line in the LST file).
 *
 * @author Dmitry Jemerov <yole@spb.cityline.ru>
 * @version $Revision: 1.1 $
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
		super.prepareChooser(c);
		final StringTokenizer aTok = new StringTokenizer(list, "|", false);
		c.setTitle(aTok.nextToken());
		if (aTok.hasMoreTokens())
			c.setPool(Integer.parseInt(aTok.nextToken()));

		return list;
	}

	/**
	 * Generates the list of tokens to be shown in the chooser from the list of
	 * skills of given type.
	 */

	public ArrayList getChoicesList(String bString)
	{
		final ArrayList aArrayList = new ArrayList();
		rootArrayList = new ArrayList();
		final String aText = list.substring(list.lastIndexOf('(') + 1, list.lastIndexOf(')'));
		for (int index = 0; index < Globals.getSkillList().size(); index++)
		{
			final Skill aSkill = (Skill)Globals.getSkillList().get(index);
			if ((aText.equals("UNTRAINED") && aSkill.untrained().equals("Y")) ||
				(aText.equals("TRAINED") && aSkill.untrained().equals("N")) ||
				(aText.equals("EXCLUSIVE") && aSkill.isExclusive().equals("Y")) ||
				(aText.equals("NONEXCLUSIVE") && aSkill.isExclusive().equals("N")) ||
				aText.equals("ANY"))
//                if (skillList().contains(aSkill.keyName()))
//                {
				if (aSkill.getRootName().length() == 0)
					aArrayList.add(aSkill.getKeyName());
			if (aSkill.getRootName().length() > 0 && !rootArrayList.contains(aSkill.getKeyName()))
				aArrayList.add(aSkill.getKeyName());
			if (aSkill.getRootName().length() > 0 && !rootArrayList.contains(aSkill.getKeyName()))
				rootArrayList.add(aSkill.getKeyName());
//                }
		}
		return aArrayList;
	}

	/**
	 * Process the choice selected by the user.
	 */

	void processChoice(ArrayList aArrayList, List selectedList, String eString)
	{
		for (int index = 0; index < selectedList.size(); index++)
		{
			final String nString = selectedList.get(index).toString() + eString;
			if (!ownerClass.skillList().contains(nString))
			{
				if (rootArrayList.contains(nString))
				{
					Skill aSkill = null;
					for (Iterator e2 = Globals.getSkillList().iterator(); e2.hasNext();)
					{
/*            Skill aSkill = (Skill)e2.next();
            if (aSkill.getRootName().equals(nString))
              ownerClass.skillList().add(aSkill.getKeyName());
							*/
						aSkill = (Skill)e2.next();
						if (nString.equals(aSkill.getName()))
						{
							ownerClass.skillList().add(aSkill.getKeyName());
						}
					}
				}
				else
					ownerClass.skillList().add(nString);
			}
		}
	}

}
