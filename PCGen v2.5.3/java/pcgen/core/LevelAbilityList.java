/*
 * LevelAbilityList.java
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
 * Created on July 24, 2001, 12:36 PM
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.gui.Chooser;

/**
 * Represents an option list that a character gets when gaining a level
 * (an ADD:LIST entry in the LST file).
 *
 * @author Dmitry Jemerov <yole@spb.cityline.ru>
 * @version $Revision: 1.1 $
 */

public class LevelAbilityList extends LevelAbility
{
	private int cnt = 0;
	private ArrayList aChoiceList;
	private ArrayList aBonusList;

	LevelAbilityList(PCClass aOwnerClass, int aLevel, String aList)
	{
		super(aOwnerClass, aLevel, aList);
	}

	/**
	 * Performs the initial setup of a chooser.
	 */

	public String prepareChooser(Chooser c)
	{
		super.prepareChooser(c);
		c.setTitle("Option List");
		return list;
	}


	public ArrayList getChoicesList(String bString)
	{
		aChoiceList = new ArrayList();
		aBonusList = new ArrayList();
		return super.getChoicesList(bString.substring(5));
	}

	/**
	 * Processes a single token in the comma-separated list of the ADD:
	 * field and adds the choices to be shown in the list to aArrayList.
	 */

	void processToken(String aChoice, ArrayList aArrayList, String bString)
	{
		final StringTokenizer cTok = new StringTokenizer(aChoice, "[]", false);
		aArrayList.add(cTok.nextToken());
		while (cTok.hasMoreTokens())
		{
			final String bTokString = cTok.nextToken();
			final String aString = new StringBuffer(cnt).append(String.valueOf(cnt)).append("|").append(bTokString).toString();
			if (bTokString.startsWith("CHOOSE:"))
				aChoiceList.add(aString);
			if (bTokString.startsWith("BONUS:"))
				aBonusList.add(aString);
		}
		cnt++;
	}

	/**
	 * Process the choice selected by the user.
	 */

	void processChoice(ArrayList aArrayList, List selectedList, String eString)
	{
		for (int index = 0; index < selectedList.size(); index++)
		{
			cnt = aArrayList.indexOf(selectedList.get(index).toString() + eString);
			String theChoice = null;
			String theBonus = null;
			ArrayList selectedBonusList = new ArrayList();
			final String prefix = cnt + "|";
			for (Iterator e = aBonusList.iterator(); e.hasNext();)
			{
				theBonus = (String)e.next();
				if (theBonus.startsWith(prefix))
				{
					theBonus = theBonus.substring((cnt / 10) + 2);
					selectedBonusList.add(theBonus);
				}
			}
			for (Iterator e = aChoiceList.iterator(); e.hasNext();)
			{
				theChoice = (String)e.next();
				//final StringTokenizer bTok = new StringTokenizer(theChoice, "|", false);
				if (theChoice.startsWith(prefix))
				{
					theChoice = theChoice.substring((cnt / 10) + 9);
					break;
				}
				theChoice = "";
			}
			if (theChoice != null && theChoice.length() > 0)
				ownerClass.getChoices(theChoice, selectedBonusList);
			else if (selectedBonusList.size() > 0)
			{
				for (Iterator e1 = selectedBonusList.iterator(); e1.hasNext();)
					ownerClass.applyBonus((String)e1.next(), "");
			}
		}
	}
}
